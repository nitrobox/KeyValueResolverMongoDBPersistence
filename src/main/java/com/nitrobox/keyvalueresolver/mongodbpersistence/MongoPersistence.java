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
        if (values == null || values.isEmpty()) {
            return null;
        }
        final KeyValues keyValues = keyEntity.toKeyValues(factory);
        keyValues.setDomainSpecificValues(toDomainSpecificValues(factory, values));
        return keyValues;
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
