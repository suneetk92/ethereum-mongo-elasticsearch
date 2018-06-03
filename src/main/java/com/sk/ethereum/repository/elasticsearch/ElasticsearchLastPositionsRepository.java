package com.sk.ethereum.repository.elasticsearch;

import com.sk.ethereum.model.LastPositionsModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchLastPositionsRepository extends ElasticsearchRepository<LastPositionsModel, String> {
}
