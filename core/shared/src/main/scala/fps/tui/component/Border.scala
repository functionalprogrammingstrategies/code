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
import fps.tui.Size

final case class Border(
    topLeft: Char,
    topRight: Char,
    horizontal: Char,
    vertical: Char,
    bottomLeft: Char,
    bottomRight: Char
):
  def render(size: Size, buf: Buffer): Unit =
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
object Border:
  /** A border that reserves space but does not render any characters. */
  val empty = Border(
    topLeft = ' ',
    topRight = ' ',
    horizontal = ' ',
    vertical = ' ',
    bottomLeft = ' ',
    bottomRight = ' '
  )

  val single = Border(
    topLeft = '┌',
    topRight = '┐',
    horizontal = '─',
    vertical = '│',
    bottomLeft = '└',
    bottomRight = '┘'
  )

  val double = Border(
    topLeft = '╔',
    topRight = '╗',
    horizontal = '═',
    vertical = '║',
    bottomLeft = '╚',
    bottomRight = '╝'
  )

  val thick = Border(
    topLeft = '┏',
    topRight = '┓',
    horizontal = '━',
    vertical = '┃',
    bottomLeft = '┗',
    bottomRight = '┛'
  )

  val rounded = Border(
    topLeft = '╭',
    topRight = '╮',
    horizontal = '─',
    vertical = '│',
    bottomLeft = '╰',
    bottomRight = '╯'
  )

  val ascii = Border(
    topLeft = '+',
    topRight = '+',
    horizontal = '-',
    vertical = '|',
    bottomLeft = '+',
    bottomRight = '+'
  )
