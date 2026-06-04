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
import fps.tui.Rect
import fps.tui.Size
import fps.tui.context.DefaultEventContext
import fps.tui.context.DefaultLayoutContext
import fps.tui.context.EventContext
import fps.tui.context.LayoutContext

import scala.collection.mutable

final class Row private[tui] (
    border: Option[Border],
    context: DefaultLayoutContext & DefaultEventContext
) extends Component:
  private val sizes: mutable.ArrayBuffer[Size] =
    new mutable.ArrayBuffer(context.components.size)

  private def updateSizes(): Unit =
    sizes.clear()
    context.components.foreach { c => sizes += c.size }

  def size: Size =
    updateSizes()
    val childSize = sizes.foldLeft(Size.zero)(_.row(_))
    if border.isDefined then Size(childSize.width + 4, childSize.height + 4)
    else childSize

  def render(size: Size, buf: Buffer): Unit =
    if context.hasFocus then border.foreach(_.render(size, buf))
    val inset = if border.isDefined then 2 else 0
    var x = inset
    context.components.zip(sizes).foreach { (child, childSize) =>
      child.render(
        childSize,
        buf.view(Rect(x, inset, childSize.width, childSize.height))
      )
      x += childSize.width
    }

object Row:
  def apply(
      border: Option[Border] = None
  )(
      body: EventContext & LayoutContext ?=> Unit
  )(using ctx: LayoutContext): Unit =
    ctx.addComponent { runtime =>
      val focusId = FocusId.next
      // Evaluate body here so we do not retain a reference to it and it can be garbage collected.
      val context = new DefaultEventContext(focusId, runtime)
        with DefaultLayoutContext(runtime) {}
      body(using context)

      new Row(border, context)
    }
