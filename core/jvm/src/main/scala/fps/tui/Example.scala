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

import fps.tui.component.Border
import fps.tui.component.Row
import fps.tui.component.Text
import fps.tui.context.LayoutContext
import fps.tui.reactive.Reactive
import fps.tui.reactive.Signal
import terminus.Key

@main def example(): Unit =
  def prompt(question: String)(using LayoutContext) =
    Row(Some(Border.rounded)) { evt ?=>
      val answer = Signal("")

      evt.onAnyKey { key =>
        // Eat keys that can mess up rendering
        if key == Key.tab || key == Key.newLine then ()
        if key == Key.backspace then answer.set(answer.peek.dropRight(1))
        else
          key.code match
            case terminus.KeyCode.Character(char) =>
              answer.set(answer.peek :+ char)
            case _ => ()
      }

      Text()(Reactive(question ++ ": "))
      Text()(Reactive(answer.value))
    }

  val ui =
    FullScreen {
      prompt("What's your name?")
      prompt("Are you having fun?")
      prompt("What's for dinner?")
    }

  val terminal = terminus.JLineTerminal.apply
  ui.run(terminal)
  terminal.close()
