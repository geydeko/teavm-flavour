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
package org.teavm.flavour.templates;

import java.util.List;

/**
 *
 * @author Alexey Andreev
 */
public abstract class DomFragment implements Fragment {
    @Override
    public Component create() {
        return new AbstractComponent(Slot.create()) {
            private List<Renderable> renderables;
            @Override
            public void render() {
                if (renderables == null) {
                    DomBuilder builder = new DomBuilder(getSlot());
                    buildDom(builder);
                    renderables = builder.getRenderables();
                }
                for (Renderable renderable : renderables) {
                    renderable.render();
                }
            }
            @Override
            public void destroy() {
                if (renderables != null) {
                    for (Renderable renderable : renderables) {
                        renderable.destroy();
                    }
                    renderables = null;
                }
                super.destroy();
            }
        };
    }

    protected abstract void buildDom(DomBuilder builder);
}
