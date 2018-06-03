package com.sk.ethereum.repository.elasticsearch;

import com.sk.ethereum.model.TransactionsModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchTransactionsRepository extends ElasticsearchRepository<TransactionsModel, String> {
}
