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
package org.teavm.flavour.templates.expr.type;

import java.util.Map;

/**
 *
 * @author Alexey Andreev
 */
public abstract class GenericTypeBuilder {
    GenericType build(GenericTypeEnvironment env) {
        return build(env.cache);
    }

    GenericType build(Map<GenericTypeBuilder, GenericType> cache) {
        GenericType result = cache.get(this);
        if (result == null) {
            result = buildCacheMiss(cache);
        }
        return result;
    }

    abstract GenericType buildCacheMiss(Map<GenericTypeBuilder, GenericType> cache);
}
