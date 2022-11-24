/*
 * KeyValueResolver MongoDB persistence
 * - A MongoDB persistence layer for the KeyValueResolver
 * Copyright (C) 2022 Nitrobox GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nitrobox.keyvalueresolver.mongodbpersistence;

import com.nitrobox.keyvalueresolver.DomainSpecificValue;
import com.nitrobox.keyvalueresolver.DomainSpecificValueFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DomainSpecificValue")
public class DomainSpecificValueEntity {

    @Id
    private Key id;
    private String value;

    private DomainSpecificValueEntity() {}

    public DomainSpecificValueEntity(String key, String value, String changeSet, String domainValuesPattern) {
        this.id = new Key(key, changeSet, domainValuesPattern);
        this.value = value;
    }

    public static List<DomainSpecificValue> toDomainSpecificValues(DomainSpecificValueFactory factory,
            List<DomainSpecificValueEntity> entities) {
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
                .map(entity -> entity.toDomainSpecificValue(factory))
                .collect(Collectors.toList());
    }

    public DomainSpecificValue toDomainSpecificValue(DomainSpecificValueFactory domainSpecificValueFactory) {
        return domainSpecificValueFactory.createFromPattern(value, id.changeSet, id.domainValuesPattern);
    }

    public String getKey() {
        return id.key;
    }

    public String getValue() {
        return value;
    }

    public String getChangeSet() {
        return id.changeSet;
    }

    public String getDomainValuesPattern() {
        return id.domainValuesPattern;
    }

    private class Key {
        private final String key;
        private final String changeSet;
        private final String domainValuesPattern;

        public Key(String key, String changeSet, String domainValuesPattern) {
            this.key = key;
            this.changeSet = changeSet;
            this.domainValuesPattern = domainValuesPattern;
        }
    }
}
