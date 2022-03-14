package com.nitrobox.keyvalueresolver.mongodbpersistence;

import com.nitrobox.keyvalueresolver.DomainSpecificValue;
import com.nitrobox.keyvalueresolver.DomainSpecificValueFactory;
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

    private DomainSpecificValueEntity(Key id, String value) {
        this.id = id;
        this.value = value;
    }

    public DomainSpecificValueEntity(String key, String value, String changeSet, String domainValuesPattern) {
        this.id = new Key(key, changeSet, domainValuesPattern);
        this.value = value;
    }

    public static List<DomainSpecificValue> toDomainSpecificValues(DomainSpecificValueFactory factory,
            List<DomainSpecificValueEntity> entities) {
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
