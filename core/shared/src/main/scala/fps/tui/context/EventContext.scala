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

import fps.tui.FocusId
import fps.tui.Runtime
import terminus.Key

import scala.collection.mutable

/** The capability of responding to input events. */
trait EventContext:
  /** Register a handler that fires only for the given key.
    *
    * Registering a handler automatically makes a component focusable.
    */
  def onKey(key: Key)(handler: => Unit): Unit

  /** Register a handler that fires for every key press.
    *
    * Unlike [[onKey]], which matches a specific [[Key]], this handler receives
    * the pressed [[Key]] and can inspect it freely. Handlers registered here
    * fire in addition to any matching [[onKey]] handlers for the same key.
    *
    * Registering a handler automatically makes a component focusable.
    */
  def onAnyKey(handler: Key => Unit): Unit

trait DefaultEventContext(focusId: FocusId, runtime: Runtime)
    extends EventContext:
  private val keyHandlers: mutable.Map[Key, mutable.ArrayBuffer[() => Unit]] =
    mutable.Map.empty
  private val anyKeyHandlers: mutable.ArrayBuffer[Key => Unit] =
    mutable.ArrayBuffer.empty

  def onKey(key: Key)(handler: => Unit): Unit =
    keyHandlers.getOrElseUpdate(key, mutable.ArrayBuffer.empty) += (() =>
      handler
    )
    runtime.addFocusable(focusId, keyHandlers, anyKeyHandlers)

  def onAnyKey(handler: Key => Unit): Unit =
    anyKeyHandlers += handler
    runtime.addFocusable(focusId, keyHandlers, anyKeyHandlers)

  def nextFocus(): Unit = runtime.nextFocus()

  def prevFocus(): Unit = runtime.prevFocus()

  def hasFocus: Boolean = runtime.currentFocusId == focusId
