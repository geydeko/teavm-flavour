/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.flavour.templates.directives;

import org.teavm.dom.events.EventListener;
import org.teavm.dom.events.MouseEvent;
import org.teavm.dom.html.HTMLElement;
import org.teavm.flavour.templates.*;

/**
 *
 * @author Alexey Andreev
 */
@BindAttributeDirective(name = "click")
public class ClickBinder implements Renderable {
    private HTMLElement element;
    private Action action;
    private boolean bound;

    public ClickBinder(HTMLElement element) {
        this.element = element;
    }

    @BindContent
    public void setAction(Action action) {
        this.action = action;
    }

    @Override
    public void render() {
        if (!bound) {
            bound = true;
            element.addEventListener("click", clickListener);
        }
    }

    @Override
    public void destroy() {
        if (bound) {
            bound = false;
            element.removeEventListener("click", clickListener);
        }
    }

    private EventListener<MouseEvent> clickListener = new EventListener<MouseEvent>() {
        @Override
        public void handleEvent(MouseEvent evt) {
            action.perform();
        }
    };
}
