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
