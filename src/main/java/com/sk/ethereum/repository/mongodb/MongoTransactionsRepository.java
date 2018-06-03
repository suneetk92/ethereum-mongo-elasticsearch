package com.sk.ethereum.repository.mongodb;

import com.sk.ethereum.model.TransactionsModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MongoTransactionsRepository extends MongoRepository<TransactionsModel, String> {
}
