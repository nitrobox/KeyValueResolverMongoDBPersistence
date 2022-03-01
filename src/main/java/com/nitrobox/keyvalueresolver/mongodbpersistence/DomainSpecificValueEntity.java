package com.nitrobox.keyvalueresolver.mongodbpersistence;

import com.nitrobox.keyvalueresolver.DomainSpecificValue;
import com.nitrobox.keyvalueresolver.DomainSpecificValueFactory;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("DomainSpecificValue")
public class DomainSpecificValueEntity {

    private final String key;
    private final String value;
    private final String changeSet;
    private final String domainValuesPattern;

    public DomainSpecificValueEntity(String key, String value, String changeSet, String domainValuesPattern) {
        this.key = key;
        this.value = value;
        this.changeSet = changeSet;
        this.domainValuesPattern = domainValuesPattern;
    }

    public static List<DomainSpecificValue> toDomainSpecificValues(DomainSpecificValueFactory factory,
            List<DomainSpecificValueEntity> entities) {
        return entities.stream()
                .map(entity -> entity.toDomainSpecificValue(factory))
                .collect(Collectors.toList());
    }

    public DomainSpecificValue toDomainSpecificValue(DomainSpecificValueFactory domainSpecificValueFactory) {
        return domainSpecificValueFactory.createFromPattern(value, changeSet, domainValuesPattern);
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getChangeSet() {
        return changeSet;
    }

    public String getDomainValuesPattern() {
        return domainValuesPattern;
    }
}
