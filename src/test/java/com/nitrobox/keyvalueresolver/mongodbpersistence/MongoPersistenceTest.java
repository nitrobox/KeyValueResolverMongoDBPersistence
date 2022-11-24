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

import static org.assertj.core.api.Assertions.assertThat;

import com.mongodb.client.MongoClients;
import com.nitrobox.keyvalueresolver.KeyValueResolver;
import com.nitrobox.keyvalueresolver.KeyValueResolverImpl;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.ImmutableMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@DataMongoTest
@ExtendWith(SpringExtension.class)
class MongoPersistenceTest {

    protected static final String HOST = "localhost";
    protected static final int PORT = 27066;
    protected static final String CONNECTION_STRING = "mongodb://" + HOST + ":" + PORT;
    protected static final String MONGO_SCHEMA_NAME = "KeyValuesResolver";

    protected MongodExecutable mongodExecutable;
    protected MongoTemplate mongoTemplate;

    @Autowired
    KeyValueResolverKeyRepository keyRepository;
    @Autowired
    KeyValueResolverValuesRepository valuesRepository;

    @BeforeEach
    protected void setUpInMemoryMongoDB() throws Exception {

        ImmutableMongodConfig mongodConfig = MongodConfig
                .builder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(HOST, PORT, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
        mongoTemplate = new MongoTemplate(MongoClients.create(CONNECTION_STRING), MONGO_SCHEMA_NAME);
    }

    @AfterEach
    void clean() {
        mongodExecutable.stop();
    }

    @Test
    void persistAndLoad() {
        final KeyValueResolver roperty = new KeyValueResolverImpl(new MongoPersistence(keyRepository, valuesRepository), "domain1",
                "domain2");
        roperty.set("key1", "value1", "descr1");
        roperty.set("key1", "value1_dom1&2", "descr1", "domVal1", "domVal2");
        roperty.set("key1", "value1_dom1", "descr1", "domVal1");
        roperty.set("key2", "value2", "descr2", "domVal1");
        roperty.set("key3", "value3", "descr3", "domVal1", "domVal2");
        roperty.set("key4", "value4", "descr4", "domVal1", "domVal2");
        roperty.remove("key1", "domVal1", "domVal2");
        roperty.removeKey("key3");
        roperty.remove("key4", "domVal1", "domVal2");
        assertThat((String) roperty.get("key1", "domVal1", "domVal2")).isEqualTo("value1_dom1");
        assertThat((String) roperty.get("key2", "domVal1", "domVal2")).isEqualTo("value2");
        assertThat((String) roperty.get("key3", "domVal1", "domVal2")).isNull();
        assertThat((String) roperty.get("key4")).isNull();
        assertThat(roperty.getKeyValues("key4")).isNotNull();

        final KeyValueResolver roperty2 = new KeyValueResolverImpl(new MongoPersistence(keyRepository, valuesRepository), "domain1",
                "domain2");
        assertThat((String) roperty2.get("key1", "domVal1", "domVal2")).isEqualTo("value1_dom1");
        assertThat((String) roperty2.get("key2", "domVal1", "domVal2")).isEqualTo("value2");
        assertThat((String) roperty2.get("key3", "domVal1", "domVal2")).isNull();
        assertThat((String) roperty2.get("key4")).isNull();
        assertThat(roperty2.getKeyValues("key4")).isNotNull();
    }
}