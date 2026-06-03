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

import terminus.Key

import scala.collection
import scala.collection.mutable

/** The runtime provides capabilties that are internal to the system, and not
  * used by the application programmer.
  */
final class Runtime private ():
  private var currentFocus: FocusId = FocusId.zero
  private var focusListIdx: Int = 0

  // The root focusable gets to handle events before the focused element
  private var rootFocusable: Runtime.Focusable = Runtime.Focusable.empty

  // The order in which we visit focusables. Follows the order in which they are
  // added.
  private val focusablesOrder: mutable.ArrayBuffer[FocusId] =
    mutable.ArrayBuffer.empty

  private val focusables: mutable.Map[FocusId, Runtime.Focusable] =
    mutable.Map.empty

  def currentFocusId: FocusId = currentFocus

  def addRootFocusable(
      keyHandlers: collection.Map[Key, collection.Seq[() => Unit]],
      anyKeyHandlers: collection.Seq[Key => Unit]
  ): Unit =
    val focusable = Runtime.Focusable(keyHandlers, anyKeyHandlers)
    rootFocusable = focusable

  def addFocusable(
      focusId: FocusId,
      keyHandlers: collection.Map[Key, collection.Seq[() => Unit]],
      anyKeyHandlers: collection.Seq[Key => Unit]
  ): Unit =
    val focusable = Runtime.Focusable(keyHandlers, anyKeyHandlers)
    focusables.updateWith(focusId) {
      case Some(old) => Some(focusable)
      case None      =>
        // If this is the first focusable make it focused
        if focusablesOrder.isEmpty then currentFocus = focusId
        // We haven't seen focusId before, so make sure it is focusablesOrder
        focusablesOrder += focusId
        Some(focusable)
    }
    ()

  def nextFocus(): Unit =
    if focusablesOrder.size == 0 then ()
    else
      focusListIdx = (focusListIdx + 1) % focusablesOrder.size
      currentFocus = focusablesOrder(focusListIdx)

  def prevFocus(): Unit =
    if focusablesOrder.size == 0 then ()
    else
      focusListIdx =
        if focusListIdx == 0 then focusablesOrder.size - 1 else focusListIdx - 1
      currentFocus = focusablesOrder(focusListIdx)

  def dispatch(key: Key): Unit =
    rootFocusable.handle(key)
    focusables.get(currentFocus) match
      case None            => ()
      case Some(focusable) => focusable.handle(key)

object Runtime:
  case class Focusable(
      keyHandlers: collection.Map[Key, collection.Seq[() => Unit]],
      anyKeyHandlers: collection.Seq[Key => Unit]
  ):
    def handle(key: Key): Unit =
      keyHandlers.get(key) match
        case None           => ()
        case Some(handlers) => handlers.foreach(f => f())
      anyKeyHandlers.foreach(f => f(key))

  object Focusable:
    val empty: Focusable = Focusable(Map.empty, Seq.empty)

  def empty: Runtime = new Runtime()
