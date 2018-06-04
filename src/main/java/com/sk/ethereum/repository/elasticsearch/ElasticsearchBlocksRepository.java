package com.sk.ethereum.repository.elasticsearch;

import com.sk.ethereum.model.BlocksModel;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticsearchBlocksRepository extends ElasticsearchRepository<BlocksModel, String> {
}
