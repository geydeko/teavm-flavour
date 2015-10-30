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
package org.teavm.flavour.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.teavm.flavour.templates.BindAttribute;
import org.teavm.flavour.templates.BindDirective;
import org.teavm.flavour.templates.BindTemplate;
import org.teavm.flavour.templates.Computation;
import org.teavm.flavour.templates.Slot;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.events.MouseEvent;

/**
 *
 * @author Alexey Andreev
 */
@BindDirective(name = "paginator")
@BindTemplate("templates/flavour/widgets/paginator.html")
public class Paginator extends AbstractWidget {
    private Computation<Integer> maxPages = () -> 11;
    private Computation<Pageable> data;
    private BiConsumer<Integer, Consumer<String>> linkGenerator;
    private List<Item> items = new ArrayList<>();
    private Pageable cachedData;
    private int cachedMaxPages;
    private int cachedPage;
    private int cachedPageCount;

    public Paginator(Slot slot) {
        super(slot);
    }

    @BindAttribute(name = "data")
    public void setData(Computation<Pageable> data) {
        this.data = data;
    }

    @BindAttribute(name = "max-pages", optional = true)
    public void setMaxPages(Computation<Integer> maxPages) {
        this.maxPages = maxPages;
    }

    @BindAttribute(name = "page-link", optional = true)
    public void setLinkGenerator(BiConsumer<Integer, Consumer<String>> linkGenerator) {
        this.linkGenerator = linkGenerator;
    }

    @Override
    public void render() {
        Pageable data = this.data.perform();
        int maxPages = this.maxPages.perform();
        int page = data.getCurrentPage();
        int pageCount = data.getPageCount();
        if (maxPages != cachedMaxPages || data != cachedData || cachedPage != page || cachedPageCount != pageCount) {
            cachedData = data;
            cachedPage = page;
            cachedPageCount = pageCount;
            cachedMaxPages = maxPages;
            rebuildItems();
            super.render();
        }
    }

    public int getPageCount() {
        return cachedPageCount;
    }

    public boolean isPreviousAvailable() {
        return cachedPage > 0;
    }

    public boolean isNextAvailable() {
        return cachedPage < cachedPageCount - 1;
    }

    public int getCurrentPage() {
        return cachedPage;
    }

    public void selectPage(MouseEvent event, int pageNum) {
        cachedData.setCurrentPage(pageNum);
        replaceHash();
        event.preventDefault();
    }

    public void nextPage(MouseEvent event) {
        if (cachedData.getCurrentPage() < cachedData.getPageCount() - 1) {
            cachedData.setCurrentPage(cachedData.getCurrentPage() + 1);
        }
        replaceHash();
        event.preventDefault();
    }

    public void previousPage(MouseEvent event) {
        if (cachedData.getCurrentPage() > 0) {
            cachedData.setCurrentPage(cachedData.getCurrentPage() - 1);
        }
        replaceHash();
        event.preventDefault();
    }

    public void getPageLink(int pageNumber, Consumer<String> consumer) {
        if (linkGenerator != null) {
            linkGenerator.accept(pageNumber, consumer);
        } else {
            consumer.accept(Window.current().getLocation().getHash());
        }
    }

    private void replaceHash() {
        linkGenerator.accept(cachedData.getCurrentPage(), hash -> {
            if (hash != null) {
                Window.current().getHistory().replaceState(null, "", "#" + Window.encodeURI(hash));
            }
        });
    }

    private void rebuildItems() {
        items.clear();
        items.add(new Item(ItemType.PREVIOUS, cachedPage));

        if (cachedMaxPages >= cachedPageCount) {
            for (int i = 0; i < cachedPageCount; ++i) {
                items.add(new Item(ItemType.PAGE, i));
            }
        } else {
            int buttons = cachedMaxPages - 2;
            int central = buttons / 3;
            int left = central - 1;
            int right = central - 1;
            central += 2 + buttons % 3;
            int centralLeft = central / 2;
            int centralRight = central - centralLeft;

            if (left >= cachedPage - centralLeft) {
                left += central;
                right++;
                for (int i = 0; i < left; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
                items.add(new Item(ItemType.SKIP, -1));
                for (int i = cachedPageCount - right; i < cachedPageCount; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
            } else if (cachedPageCount - right <= cachedPage + centralRight) {
                buttons++;
                right += central;
                left++;
                for (int i = 0; i < left; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
                items.add(new Item(ItemType.SKIP, -1));
                for (int i = cachedPageCount - right; i < cachedPageCount; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
            } else {
                for (int i = 0; i < left; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
                items.add(new Item(ItemType.SKIP, -1));
                for (int i = cachedPage - centralLeft; i < cachedPage + centralRight; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
                items.add(new Item(ItemType.SKIP, -1));
                for (int i = cachedPageCount - right; i < cachedPageCount; ++i) {
                    items.add(new Item(ItemType.PAGE, i));
                }
            }
        }

        items.add(new Item(ItemType.NEXT, cachedPage));
    }

    public void refresh() {
        cachedData.refresh();
    }

    public List<Item> getItems() {
        return items;
    }

    public static class Item {
        private ItemType type;
        private int pageNumber;

        public Item(ItemType type, int pageNumber) {
            this.type = type;
            this.pageNumber = pageNumber;
        }

        public ItemType getType() {
            return type;
        }

        public int getPageNumber() {
            return pageNumber;
        }

        public boolean isPrevious() {
            return type == ItemType.PREVIOUS;
        }

        public boolean isNext() {
            return type == ItemType.NEXT;
        }

        public boolean isPage() {
            return type == ItemType.PAGE;
        }

        public boolean isSkip() {
            return type == ItemType.SKIP;
        }
    }

    public static enum ItemType {
        PREVIOUS,
        NEXT,
        PAGE,
        SKIP
    }
}