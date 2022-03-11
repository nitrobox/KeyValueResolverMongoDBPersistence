package com.nitrobox.keyvalueresolver.mongodbpersistence;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KeyValueResolverKeyRepository extends MongoRepository<KeyEntity, String> {
}
