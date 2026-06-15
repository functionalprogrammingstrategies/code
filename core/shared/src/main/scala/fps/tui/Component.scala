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

/** A component is something that can be rendered to the terminal and
  * participates in layout.
  *
  * The protocol for calling the methods on Component is:
  *
  *   - The constructor is in the setup stage. It should do any required setup,
  *     such as adding itself to a LayoutContext, but not run reactives.
  *   - size should do any recalculation required, including running reactives.
  *   - render should not do any recalculation.
  *   - size is called once before render.
  *   - both size and render are only called in the reactive stage.
  */
trait Component:
  /** Get the size without recalculating any reactive dependencies. */
  def size: Size

  /** Render this component, with the given size, to the Buffer. The component
    * should always start at coordinate (0, 0).
    */
  def render(size: Size, buf: Buffer): Unit
