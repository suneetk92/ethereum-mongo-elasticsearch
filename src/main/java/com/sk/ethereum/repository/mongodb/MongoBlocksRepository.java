package com.sk.ethereum.repository.mongodb;

import com.sk.ethereum.model.BlocksModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoBlocksRepository extends MongoRepository<BlocksModel, String> {
}
