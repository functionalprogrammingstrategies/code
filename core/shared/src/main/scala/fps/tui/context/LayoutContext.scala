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

package fps.tui.context

import fps.tui.Component
import fps.tui.Runtime

import scala.collection.mutable

/** The capability of adding components to the layout tree. */
trait LayoutContext:
  def addComponent(build: Runtime => Component): Unit

/** The default implementation of LayoutContext */
trait DefaultLayoutContext(runtime: Runtime) extends LayoutContext:
  private[tui] val components: mutable.ArrayBuffer[Component] =
    mutable.ArrayBuffer.empty

  def addComponent(build: Runtime => Component): Unit =
    components += build(runtime)
object DefaultLayoutContext:
  def apply(runtime: Runtime): DefaultLayoutContext =
    new DefaultLayoutContext(runtime) {}
