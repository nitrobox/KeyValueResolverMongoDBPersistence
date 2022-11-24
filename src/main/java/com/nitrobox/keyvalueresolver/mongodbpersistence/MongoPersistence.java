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

import static com.nitrobox.keyvalueresolver.mongodbpersistence.DomainSpecificValueEntity.toDomainSpecificValues;

import com.nitrobox.keyvalueresolver.DomainSpecificValue;
import com.nitrobox.keyvalueresolver.DomainSpecificValueFactory;
import com.nitrobox.keyvalueresolver.KeyValues;
import com.nitrobox.keyvalueresolver.Persistence;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MongoPersistence implements Persistence {

    private final KeyValueResolverKeyRepository keyRepository;
    private final KeyValueResolverValuesRepository valuesRepository;

    public MongoPersistence(KeyValueResolverKeyRepository keyRepository, KeyValueResolverValuesRepository valuesRepository) {
        this.keyRepository = keyRepository;
        this.valuesRepository = valuesRepository;
    }

    @Override
    public KeyValues load(String key, DomainSpecificValueFactory factory) {
        return keyRepository.findById(key)
                .map(keyEntity -> createKeyValues(factory, keyEntity, valuesRepository.findByIdKey(key)))
                .orElse(null);
    }

    @Override
    public Collection<KeyValues> loadAll(DomainSpecificValueFactory factory) {
        final Map<String, List<DomainSpecificValueEntity>> valueEntityMap = new HashMap<>();
        valuesRepository.findAll()
                .forEach(value -> valueEntityMap.computeIfAbsent(value.getKey(), key -> new ArrayList<>()).add(value));
        return keyRepository.findAll().stream()
                .map(keyEntity -> createKeyValues(factory, keyEntity, valueEntityMap.get(keyEntity.getKey())))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private KeyValues createKeyValues(DomainSpecificValueFactory factory, KeyEntity keyEntity, List<DomainSpecificValueEntity> values) {
        return keyEntity.toKeyValues(factory, toDomainSpecificValues(factory, values));
    }

    @Override
    public Collection<KeyValues> reload(Collection<KeyValues> keyValues, DomainSpecificValueFactory domainSpecificValueFactory) {
        return loadAll(domainSpecificValueFactory);
    }

    @Override
    public void store(final String key, final KeyValues keyValues, DomainSpecificValue domainSpecificValue) {
        keyRepository.save(new KeyEntity(keyValues.getKey(), keyValues.getDescription()));
        valuesRepository.save(
                new DomainSpecificValueEntity(key, (String) domainSpecificValue.getValue(), domainSpecificValue.getChangeSet(),
                        domainSpecificValue.getPattern()));
    }

    @Override
    public void remove(String key) {
        keyRepository.deleteById(key);
        valuesRepository.deleteByIdKey(key);
    }

    @Override
    public void remove(final String key, final DomainSpecificValue domainSpecificValue) {
        valuesRepository.deleteByIdKeyAndIdChangeSetAndIdDomainValuesPattern(key, domainSpecificValue.getChangeSet(),
                domainSpecificValue.getPattern());
    }
}
