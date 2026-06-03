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

import fps.tui.component.Text
import fps.tui.context.LayoutContext
import fps.tui.reactive.Reactive
import fps.tui.reactive.Signal
import terminus.Key

@main def example(): Unit =
  def prompt(question: String)(using LayoutContext) =
    Text { evt ?=>
      val name = Signal("")

      evt.onAnyKey(key =>
        // Eat keys that can mess up rendering
        if key == Key.tab || key == Key.newLine then ()
        else
          key.code match
            case terminus.KeyCode.Character(char) =>
              name.set(name.peek :+ char)
            case _ => ()
      )

      Reactive {
        val n = name.value
        if n.isEmpty then s"${question}?"
        else s"${question}, ${n}?"
      }
    }

  val ui =
    FullScreen {
      prompt("Hello")
      prompt("Whatcha")
      prompt("What up")
    }

  val terminal = terminus.JLineTerminal.apply
  ui.run(terminal)
  terminal.close()
