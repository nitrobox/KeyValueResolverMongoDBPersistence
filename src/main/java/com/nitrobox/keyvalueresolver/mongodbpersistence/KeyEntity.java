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
import com.nitrobox.keyvalueresolver.KeyValues;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Key")
public class KeyEntity {

    @Id
    private final String key;
    private final String description;

    public KeyEntity(String key, String description) {
        this.key = key;
        this.description = description;
    }

    public KeyValues toKeyValues(DomainSpecificValueFactory domainSpecificValueFactory, List<DomainSpecificValue> domainSpecificValues) {
        return new KeyValues(key, domainSpecificValueFactory, description, domainSpecificValues);
    }

    public String getKey() {
        return key;
    }

    public String getDescription() {
        return description;
    }
}
