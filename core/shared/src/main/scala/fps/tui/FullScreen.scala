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

import fps.tui.component.Column
import fps.tui.context.DefaultEventContext
import fps.tui.context.DefaultLayoutContext
import fps.tui.context.LayoutContext
import terminus.AlternateScreenMode
import terminus.Cursor
import terminus.Erase
import terminus.Key
import terminus.KeyCode
import terminus.KeyReader
import terminus.RawMode
import terminus.Writer
import terminus.effect

/** The root of a component tree. Acts as a column and renders into the
  * alternate screen of the terminal.
  */
class FullScreen(runtime: Runtime, column: Column):

  private[tui] def toBuffer(): ArrayBuffer =
    val currentSize = column.size
    val buf = ArrayBuffer(currentSize.width, currentSize.height)
    column.render(currentSize, buf)
    buf

  def run(terminal: FullScreen.InteractiveTerminal): Unit =
    import FullScreen.InteractiveTerminal

    val program: InteractiveTerminal ?=> Unit =
      InteractiveTerminal.cursor.hidden {
        InteractiveTerminal.raw {
          InteractiveTerminal.alternateScreen {
            InteractiveTerminal.erase.screen()

            var quit = false

            // Setup default handlers
            runtime.addRootFocusable(
              Map(
                Key.tab -> Seq(() => runtime.nextFocus()),
                Key.shift(KeyCode.Tab) -> Seq(() => runtime.prevFocus()),
                Key.controlQ -> Seq(() => quit = true)
              ),
              Seq.empty
            )

            def renderFrame(): Unit =
              val buf = toBuffer()
              InteractiveTerminal.erase.screen()
              buf.render

            while !quit do
              renderFrame()
              InteractiveTerminal.readKey() match
                case terminus.Eof => quit = true
                case key: Key     => runtime.dispatch(key)
          }
        }
      }

    program(using terminal)

object FullScreen:
  type InteractiveTerminal = effect.AlternateScreenMode & effect.Erase &
    effect.Cursor & effect.Writer & effect.AlternateScreenMode &
    effect.KeyReader & effect.RawMode
  object InteractiveTerminal
      extends AlternateScreenMode,
        Cursor,
        Erase,
        KeyReader,
        RawMode,
        Writer

  def apply(body: LayoutContext ?=> Unit): FullScreen =
    val focusId = FocusId.next
    val runtime = Runtime.empty
    val context = new DefaultEventContext(focusId, runtime)
      with DefaultLayoutContext(runtime) {}
    // Evaluate body here so we do not retain a reference to it and it can be garbage collected.
    body(using context)
    val column = new Column(None, context)
    val fullScreen = new FullScreen(runtime, column)

    fullScreen
