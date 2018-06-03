package com.sk.ethereum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@org.springframework.data.mongodb.core.mapping.Document(collection = "lastPositions")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ethereum", type = "lastPositions")
public class LastPositionsModel {
    @Id
    private String type;
    private String hash;
    private Long number;
}
