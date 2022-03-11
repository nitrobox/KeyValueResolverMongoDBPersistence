# KeyValueResolverMongoDBPersistence
A persistence for KeyValueResolver into a MongoDB using Spring Data

The project that wants to use this persistence needs to add a dependency to 
```org.springframework.boot:spring-boot-starter-data-mongodb``` and activate
the Mongo Repository by using the annotation:
```@EnableMongoRepositories("com.nitrobox.keyvalueresolver.mongodbpersistence")```
