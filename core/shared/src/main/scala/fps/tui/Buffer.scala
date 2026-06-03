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

package fps.tui

import terminus.effect.AnsiCodes

/** A 2D grid of terminal cells that components render into.
  *
  * Coordinates are 0-based. Out-of-bounds writes are silently ignored,
  * providing isolation between components—a buggy component cannot corrupt
  * cells outside its allocated region.
  */
trait Buffer:
  /** Write a single char at (x, y). Out-of-bounds writes are ignored. */
  def put(x: Int, y: Int, char: Char): Unit

  /** Write a string starting at (x, y), ignoring newline characters. */
  def putString(x: Int, y: Int, str: String): Unit

  /** Creates a Buffer that writes to the same location as Buffer. Remap
    * coordinates to that (0, 0) in the returned Buffer is (x, y) in bounds, and
    * writes outside bounds are ignored.
    */
  def view(bounds: Rect): Buffer

/** A Buffer that writes to an underlying array of characters. */
final class ArrayBuffer(width: Int, height: Int) extends Buffer:
  // Row-major flat array: index = y * width + x
  private val cells: Array[Char] = Array.fill(width * height)(' ')

  def put(x: Int, y: Int, char: Char): Unit =
    if x >= 0 && x < width && y >= 0 && y < height then
      cells(y * width + x) = char

  def putString(x: Int, y: Int, str: String): Unit =
    var col = x
    str.foreach(c =>
      if c == '\n' then ()
      else put(col, y, c)
      col = col + 1
    )

  def view(bounds: Rect): Buffer =
    BufferView(bounds, this)

  /** Flush the entire buffer to the terminal.
    *
    * Iterates cells row by row, using absolute cursor positioning at the start
    * of each row. Continuation cells (right half of wide characters) are
    * skipped. Emits a full SGR reset before and after rendering.
    */
  def render(using t: Terminal): Unit =
    t.write(AnsiCodes.sgr("0"))
    var y = 0
    while y < height do
      t.cursor.to(1, y + 1) // 1-based terminal coordinates
      var x = 0
      while x < width do
        val char = cells(y * width + x)
        t.write(char)
        x += 1
      y += 1
    t.write(AnsiCodes.sgr("0"))
    t.flush()

/** A Buffer that writes to the given source Buffer, where writes are offset by
  * the x and y coordinates of the given bounds, and clipped to bounds.
  *
  * This allows components to write to the buffer as if they were starting at
  * (0, 0), and ensures components do not overflow their given Rect.
  */
final class BufferView(bounds: Rect, source: Buffer) extends Buffer:
  def put(x: Int, y: Int, char: Char): Unit =
    if x >= 0 && x < bounds.width && y >= 0 && y < bounds.height then
      source.put(x + bounds.x, y + bounds.y, char)

  def putString(x: Int, y: Int, str: String): Unit =
    var col = x
    str.foreach(c =>
      if c == '\n' then ()
      else put(col, y, c)
      col = col + 1
    )

  def view(bounds: Rect): Buffer =
    BufferView(bounds, this)
