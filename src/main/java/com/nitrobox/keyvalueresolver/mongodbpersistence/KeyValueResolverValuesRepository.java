package com.nitrobox.keyvalueresolver.mongodbpersistence;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueResolverValuesRepository extends MongoRepository<DomainSpecificValueEntity, String> {

    List<DomainSpecificValueEntity> findByIdKey(String key);
    void deleteByIdKey(String key);
    void deleteByIdKeyAndIdChangeSetAndIdDomainValuesPattern(String key, String changeSet, String pattern);
}
