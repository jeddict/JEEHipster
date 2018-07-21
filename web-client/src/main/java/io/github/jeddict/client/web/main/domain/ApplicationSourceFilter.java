/**
 * Copyright 2013-2018 the original author or authors from the Jeddict project (https://jeddict.github.io/).
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package io.github.jeddict.client.web.main.domain;

import static io.github.jeddict.jcode.util.FileUtil.getSimpleFileNameWithExt;
import java.util.Map;
import java.util.function.Supplier;

public abstract class ApplicationSourceFilter {

    protected final BaseApplicationConfig config;
        
    public ApplicationSourceFilter(BaseApplicationConfig config) {
        this.config = config;
    }
    
    public boolean isEnable(String file) {
        String simpleFileName = getSimpleFileNameWithExt(file);
        Supplier<Boolean> filter = getGeneratorFilter().get(simpleFileName);
        if (filter == null) {
            filter = getGeneratorFilter().get(file);
        }
        if (filter != null) {
            return filter.get();
        }
        return true;
    }

    protected abstract Map<String, Supplier<Boolean>> getGeneratorFilter();

}
