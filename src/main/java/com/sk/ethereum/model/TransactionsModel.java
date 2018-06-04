package com.sk.ethereum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@org.springframework.data.mongodb.core.mapping.Document(collection = "transactions")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "ethereum", type = "transactions")
public class TransactionsModel {

    private String hash;
    private Long nonce;
    private String nonceRaw;
    private String blockHash;
    private Long blockNumber;
    private String blockNumberRaw;
    private Long resultIndex;
    private String resultIndexRaw;
    private String from;
    private String to;
    private Long value;
    private String valueRaw;
    private Long gasPrice;
    private String gasPriceRaw;
    private Long gas;
    private String gasRaw;
    private String input;
    private String creates;
    private String publicKey;
    private String raw;
    private String r;
    private String s;
    private int v;

    public TransactionsModel(Transaction result) {
        this.hash = result.getHash();
        this.nonce = result.getNonce().longValue();
        this.nonceRaw = result.getNonceRaw();
        this.blockHash = result.getBlockHash();
        this.blockNumber = result.getBlockNumber().longValue();
        this.blockNumberRaw = result.getBlockNumberRaw();
        this.resultIndex = result.getTransactionIndex().longValue();
        this.resultIndexRaw = result.getTransactionIndexRaw();
        this.from = result.getFrom();
        this.to = result.getTo();
        this.value = result.getValue().longValue();
        this.valueRaw = result.getValueRaw();
        this.gasPrice = result.getGasPrice().longValue();
        this.gasPriceRaw = result.getGasPriceRaw();
        this.gas = result.getGas().longValue();
        this.gasRaw = result.getGasRaw();
        this.input = result.getInput();
        this.creates = result.getCreates();
        this.publicKey = result.getPublicKey();
        this.raw = result.getRaw();
        this.r = result.getR();
        this.s = result.getS();
        this.v = result.getV();
    }

    public TransactionsModel(EthBlock.TransactionResult result) {
        Transaction transaction = (Transaction) result.get();
        this.hash = transaction.getHash();
        this.nonce = transaction.getNonce().longValue();
        this.nonceRaw = transaction.getNonceRaw();
        this.blockHash = transaction.getBlockHash();
        this.blockNumber = transaction.getBlockNumber().longValue();
        this.blockNumberRaw = transaction.getBlockNumberRaw();
        this.resultIndex = transaction.getTransactionIndex().longValue();
        this.resultIndexRaw = transaction.getTransactionIndexRaw();
        this.from = transaction.getFrom();
        this.to = transaction.getTo();
        this.value = transaction.getValue().longValue();
        this.valueRaw = transaction.getValueRaw();
        this.gasPrice = transaction.getGasPrice().longValue();
        this.gasPriceRaw = transaction.getGasPriceRaw();
        this.gas = transaction.getGas().longValue();
        this.gasRaw = transaction.getGasRaw();
        this.input = transaction.getInput();
        this.creates = transaction.getCreates();
        this.publicKey = transaction.getPublicKey();
        this.raw = transaction.getRaw();
        this.r = transaction.getR();
        this.s = transaction.getS();
        this.v = transaction.getV();
    }
}
