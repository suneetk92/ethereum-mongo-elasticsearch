package com.sk.ethereum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.web3j.protocol.core.methods.response.EthBlock;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionsHashModel {
    private String value;

    public TransactionsHashModel(EthBlock.TransactionResult value) {
        this.value = value.get().toString();
    }
}
