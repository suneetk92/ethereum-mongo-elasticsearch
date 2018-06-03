package com.sk.ethereum.repository.mongodb;

import com.sk.ethereum.model.LastPositionsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoLastPositionsRepository extends MongoRepository<LastPositionsModel, String> {
}
