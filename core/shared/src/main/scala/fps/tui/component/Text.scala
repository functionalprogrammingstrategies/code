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
import fps.tui.Runtime
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
    runtime: Runtime,
    focusId: FocusId,
    text: Reactive[String]
) extends Component:
  private def hasFocus: Boolean = runtime.currentFocusId == focusId

  def size =
    val str = text.value(using ReactiveRuntime.empty)
    Size(str.size + 4, 5)

  def render(size: Size, buf: Buffer): Unit =
    // Border style
    val topLeft = '╭'
    val topRight = '╮'
    val horizontal = '─'
    val vertical = '│'
    val bottomLeft = '╰'
    val bottomRight = '╯'

    // Draw border if focused
    if hasFocus then
      val right = size.width - 1
      val bottom = size.height - 1

      // Top row
      buf.put(0, 0, topLeft)
      var x = 1
      while x < size.width do
        buf.put(x, 0, horizontal)
        x += 1
      buf.put(right, 0, topRight)

      // Sides
      var y = 1
      while y < bottom do
        buf.put(0, y, vertical)
        buf.put(right, y, vertical)
        y += 1

      // Bottom row
      buf.put(0, bottom, bottomLeft)
      x = 1
      while x < right do
        buf.put(x, bottom, horizontal)
        x += 1
      buf.put(right, bottom, bottomRight)

    buf.putString(2, 2, text.peek)

object Text:
  def apply(expr: EventContext ?=> Reactive[String])(using
      ctx: LayoutContext
  ): Unit =
    ctx.addComponent { runtime =>
      val focusId = FocusId.next
      val eventContext = DefaultEventContext(focusId, runtime)
      val text = expr(using eventContext)
      new Text(runtime, focusId, text)
    }
