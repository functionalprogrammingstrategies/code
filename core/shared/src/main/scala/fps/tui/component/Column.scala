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
import fps.tui.Rect
import fps.tui.Size
import fps.tui.context.DefaultLayoutContext
import fps.tui.context.LayoutContext

import scala.collection.mutable

final class Column private[tui] (layoutContext: DefaultLayoutContext)
    extends Component:

  private val sizes: mutable.ArrayBuffer[Size] =
    new mutable.ArrayBuffer(layoutContext.components.size)

  private def updateSizes(): Unit =
    sizes.clear()
    layoutContext.components.foreach { c => sizes += c.size }

  def size: Size =
    updateSizes()
    sizes.foldLeft(Size.zero)(_.column(_))

  def render(size: Size, buf: Buffer): Unit =
    var y = 0
    layoutContext.components.zip(sizes).foreach { (child, childSize) =>
      child.render(
        childSize,
        buf.view(Rect(0, y, childSize.width, childSize.height))
      )
      y += childSize.height
    }

object Column:
  def apply(body: LayoutContext ?=> Unit)(using ctx: LayoutContext): Unit =
    ctx.addComponent { runtime =>
      // Evaluate body here so we do not retain a reference to it and it can be garbage collected.
      val layoutContext = DefaultLayoutContext(runtime)
      body(using layoutContext)

      new Column(layoutContext)
    }
