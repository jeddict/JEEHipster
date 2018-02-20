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
package io.github.jeddict.client.angular.domain;

import io.github.jeddict.client.web.main.domain.BaseRelationship;
import io.github.jeddict.client.web.main.domain.BaseEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static io.github.jeddict.jcode.util.StringHelper.camelCase;
import static io.github.jeddict.jcode.util.StringHelper.firstLower;
import static io.github.jeddict.jcode.util.StringHelper.firstUpper;
import static io.github.jeddict.jcode.util.StringHelper.kebabCase;
import static io.github.jeddict.jcode.util.StringHelper.pluralize;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;

/**
 *
 * @author jGauravGupta
 */
public class NGEntity extends BaseEntity {

    private String entityAngularName;
    private final Map<String, List<BaseRelationship>> differentRelationships = new HashMap<>();

    public NGEntity(String name, String entityAngularSuffix) {
        super(name, entityAngularSuffix);
        String entityNameSpinalCased = kebabCase(firstLower(name));
        String entityNamePluralizedAndSpinalCased = kebabCase(firstLower(pluralize(name)));

        this.entityFileName = kebabCase(this.entityNameCapitalized + firstUpper(entityAngularSuffix));
        this.entityPluralFileName = entityNamePluralizedAndSpinalCased + entityAngularSuffix;
        this.entityServiceFileName = this.entityFileName;
        this.entityAngularName = this.entityClass + firstUpper(camelCase(entityAngularSuffix));
        this.entityStateName = kebabCase(entityAngularName);
        this.entityUrl = this.entityStateName;
        this.entityTranslationKey = this.entityInstance;
        this.entityTranslationKeyMenu = camelCase(this.entityStateName);
    }

    @Override
    public void addRelationship(BaseRelationship relationship) {
        super.addRelationship(relationship);
        String entityType = relationship.getOtherEntityNameCapitalized();
        if (!getDifferentTypes().contains(entityType)) {
            getDifferentTypes().add(entityType);
        }

        if (!differentRelationships.containsKey(entityType)) {
            differentRelationships.put(entityType, new ArrayList<>());
        }
        differentRelationships.get(entityType).add(relationship);
    }

    @Override
    public String getEntityName() {
        return getEntityAngularName();
    }
    
    public String getEntityAngularName() {
        return entityAngularName;
    }

    /**
     * @return the differentRelationships
     */
    public String getDifferentRelationshipsJSON() {
        try {
            //map issue in nashorn
            return new ObjectMapper().writeValueAsString(differentRelationships);
        } catch (JsonProcessingException ex) {
            Exceptions.printStackTrace(ex);
        }
        return "{}";
    }

}
