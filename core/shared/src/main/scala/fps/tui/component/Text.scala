/*
 * Copyright 2026 Creative Scala
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fps.tui.component

import fps.tui.Buffer
import fps.tui.Component
import fps.tui.FocusId
import fps.tui.Size
import fps.tui.context.DefaultEventContext
import fps.tui.context.EventContext
import fps.tui.context.LayoutContext
import fps.tui.reactive.Reactive
import fps.tui.reactive.ReactiveRuntime

/** A component that displays Text. The most basic leaf component, from which
  * many other components can be built.
  */
final class Text private (
    border: Option[Border],
    text: Reactive[String],
    context: DefaultEventContext
) extends Component:
  def size =
    val str = text.value(using ReactiveRuntime.empty)
    val borderSize = if border.isDefined then 4 else 0
    Size(str.size + borderSize, 1 + borderSize)

  def render(size: Size, buf: Buffer): Unit =
    if context.hasFocus then border.foreach(_.render(size, buf))
    val inset = if border.isDefined then 2 else 0
    buf.putString(inset, inset, text.peek)

object Text:
  def apply(
      border: Option[Border] = None
  )(expr: EventContext ?=> Reactive[String])(using
      ctx: LayoutContext
  ): Unit =
    ctx.addComponent { runtime =>
      val focusId = FocusId.next
      val eventContext = new DefaultEventContext(focusId, runtime) {}
      val text = expr(using eventContext)
      new Text(border, text, eventContext)
    }
