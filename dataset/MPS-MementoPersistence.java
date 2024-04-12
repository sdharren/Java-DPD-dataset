/*
 * Copyright 2003-2011 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.nodeEditor;

import jetbrains.mps.openapi.editor.EditorComponentState;
import org.jdom.Element;

/**
 * Cyril.Konopko, 09.10.2010
 */
public class MementoPersistence {
  public static void saveMemento(EditorComponentState o, Element e) {
    if (o instanceof Memento) {
      ((Memento) o).save(e);
    }
  }

  public static EditorComponentState loadMemento(Element e) {
    return Memento.load(e);
  }
}
