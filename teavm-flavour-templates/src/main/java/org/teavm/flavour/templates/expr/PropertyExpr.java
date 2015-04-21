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
package org.teavm.flavour.templates.expr;

/**
 *
 * @author Alexey Andreev
 */
public class PropertyExpr<T> extends Expr<T> {
    private Expr<T> instance;
    private String propertyName;

    public Expr<T> getInstance() {
        return instance;
    }

    public void setInstance(Expr<T> instance) {
        this.instance = instance;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public void acceptVisitor(ExprVisitor<? super T> visitor) {
        visitor.visit(this);
    }
}
