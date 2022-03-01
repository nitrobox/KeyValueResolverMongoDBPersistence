package com.nitrobox.keyvalueresolver.mongodbpersistence;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValuesResolverValuesRepository extends MongoRepository<DomainSpecificValueEntity, String> {

    List<DomainSpecificValueEntity> findByKey(String key);
    void deleteByKey(String key);
    void deleteByKeyAndChangeSetAndDomainValuesPattern(String key, String changeSet, String pattern);
}
