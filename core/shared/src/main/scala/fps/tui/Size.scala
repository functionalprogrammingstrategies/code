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

/** The layout size of a component in cells.
  */
final case class Size(width: Int, height: Int):
  def row(that: Size): Size =
    Size(this.width + that.width, this.height.max(that.height))

  def column(that: Size): Size =
    Size(this.width.max(that.width), this.height + that.height)
object Size:
  def fixed(width: Int, height: Int): Size =
    Size(width, height)

  val zero: Size = fixed(0, 0)
