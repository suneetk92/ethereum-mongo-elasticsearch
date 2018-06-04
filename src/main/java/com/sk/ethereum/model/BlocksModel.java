package com.sk.ethereum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.web3j.protocol.core.methods.response.EthBlock;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@org.springframework.data.mongodb.core.mapping.Document(collection = "blocks")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ethereum", type = "blocks")
public class BlocksModel {
    @Id
    private String id;
    private Long number;
    private String numberRaw;
    private String hash;
    private String parentHash;
    private Double nonce;
    private String nonceRaw;
    private String sha3Uncles;
    private String logsBloom;
    private String transactionsRoot;
    private String stateRoot;
    private String receiptsRoot;
    private String author;
    private String miner;
    private String mixHash;
    private Long difficulty;
    private String difficultyRaw;
    private Double totalDifficulty;
    private String totalDifficultyRaw;
    private String extraData;
    private Long size;
    private String sizeRaw;
    private Long gasLimit;
    private String gasLimitRaw;
    private Long gasUsed;
    private String gasUsedRaw;
    @Field(type = FieldType.Date)
    private Date timestamp;
    private String timestampRaw;
    private List<TransactionsModel> transactions;
    private List<String> uncles;
    private List<String> sealFields;

    public BlocksModel(EthBlock.Block result) {
        this.id = result.getNumberRaw();
        this.number = result.getNumber().longValue();
        this.numberRaw = result.getNumberRaw();
        this.hash = result.getHash();
        this.parentHash = result.getParentHash();
        this.nonce = result.getNonce().doubleValue();
        this.nonceRaw = result.getNonceRaw();
        this.sha3Uncles = result.getSha3Uncles();
        this.logsBloom = result.getLogsBloom();
        this.transactionsRoot = result.getTransactionsRoot();
        this.stateRoot = result.getStateRoot();
        this.receiptsRoot = result.getReceiptsRoot();
        this.author = result.getAuthor();
        this.miner = result.getMiner();
        this.mixHash = result.getMixHash();
        this.difficulty = result.getDifficulty().longValue();
        this.difficultyRaw = result.getDifficultyRaw();
        this.totalDifficulty = result.getTotalDifficulty().doubleValue();
        this.totalDifficultyRaw = result.getTotalDifficultyRaw();
        this.extraData = result.getExtraData();
        this.size = result.getSize().longValue();
        this.sizeRaw = result.getSizeRaw();
        this.gasLimit = result.getGasLimit().longValue();
        this.gasLimitRaw = result.getGasLimitRaw();
        this.gasUsed = result.getGasUsed().longValue();
        this.gasUsedRaw = result.getGasUsedRaw();
        this.timestamp = new Date(result.getTimestamp().longValue()*1000);
        this.timestampRaw = result.getTimestampRaw();
        this.transactions = result.getTransactions().stream().map(TransactionsModel::new).collect(Collectors.toList());
        this.uncles = result.getUncles();
        this.sealFields = result.getSealFields();
    }
}
