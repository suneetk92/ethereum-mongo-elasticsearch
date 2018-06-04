package com.sk.ethereum.service;

import com.sk.ethereum.model.BlocksModel;
import com.sk.ethereum.model.LastPositionsModel;
import com.sk.ethereum.model.TransactionsModel;
import com.sk.ethereum.repository.mongodb.MongoBlocksRepository;
import com.sk.ethereum.repository.mongodb.MongoLastPositionsRepository;
import com.sk.ethereum.repository.mongodb.MongoTransactionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.http.HttpService;
import rx.Observable;
import rx.Subscription;

import java.math.BigInteger;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

@Service
public class MongoService implements EthereumService {

    private static final Logger LOGGER = Logger.getLogger(MongoService.class.getName());

    private final MongoBlocksRepository blocksRepository;
    private final MongoLastPositionsRepository lastPositionsRepository;
    private final MongoTransactionsRepository transactionsRepository;

    @Value("${web3j.host}")
    private String httpHost;

    @Value("${is.catchUpToLatestAndSubscribeToNewBlocksObservable}")
    private Boolean isCatchUpToLatestAndSubscribeToNewBlocksObservable;

    @Value("${is.catchUpToLatestAndSubscribeToNewTransactionsObservable}")
    private Boolean isCatchUpToLatestAndSubscribeToNewTransactionsObservable;

    private Subscription blockSubscription;
    private Subscription transactionSubscription;

    private LastPositionsModel lastBlockResult;
    private LastPositionsModel lastTransactionResult;

    private Web3j web3;

    @Autowired
    public MongoService(MongoBlocksRepository blocksRepository, MongoLastPositionsRepository lastPositionsRepository, MongoTransactionsRepository transactionsRepository) {
        this.blocksRepository = blocksRepository;
        this.lastPositionsRepository = lastPositionsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public void storeInDB() {
        web3 = Web3j.build(new HttpService(httpHost));

        if (isCatchUpToLatestAndSubscribeToNewBlocksObservable) {
            Optional<LastPositionsModel> lastBlock = lastPositionsRepository.findById("lastBlock");
            lastBlockResult = lastBlock.orElseGet(() -> new LastPositionsModel("lastBlock", "0x0eef117513ba151b9a9e823edc7ee11aeb69b666296e79769dd656de17c4352e", 5720000L));
            BigInteger blockNumber = BigInteger.valueOf(lastBlockResult.getNumber());

            LOGGER.info("Last Block synced in mongo till: " + blockNumber);

            blockSubscription = web3.catchUpToLatestAndSubscribeToNewBlocksObservable(DefaultBlockParameter.valueOf(blockNumber), true)
                    .retryWhen(errors -> errors.flatMap(error -> {
                                LOGGER.info("Error occurred: " + error);

                                lastPositionsRepository.save(lastBlockResult);
                                return Observable.just(new Object()).delay(1L, TimeUnit.MINUTES);
                            })
                    )
                    .subscribe(block -> {
                        BlocksModel result = new BlocksModel(block.getBlock());
                        lastBlockResult = new LastPositionsModel("lastBlock", result.getHash(), result.getNumber());

                        blocksRepository.save(result);
                        lastPositionsRepository.save(lastBlockResult);

                        LOGGER.info("Block written in mongo database: " + result.getNumber());
                    }, error -> {
                        lastPositionsRepository.save(lastBlockResult);
                        LOGGER.info(error.getMessage());
                    });
        }

        if (isCatchUpToLatestAndSubscribeToNewTransactionsObservable) {
            Optional<LastPositionsModel> lastTransaction = lastPositionsRepository.findById("lastTransaction");
            lastTransactionResult = lastTransaction.orElseGet(() -> new LastPositionsModel("lastTransaction", "0x0eef117513ba151b9a9e823edc7ee11aeb69b666296e79769dd656de17c4352e", 5720000L));
            BigInteger transactionNumber = BigInteger.valueOf(lastTransactionResult.getNumber());

            LOGGER.info("Last Transaction synced in mongo till: " + transactionNumber);

            transactionSubscription = web3.catchUpToLatestAndSubscribeToNewTransactionsObservable(DefaultBlockParameter.valueOf(transactionNumber))
                    .retryWhen(errors -> errors.flatMap(error -> {
                                LOGGER.info("Error occurred: " + error);
                                lastPositionsRepository.save(lastTransactionResult);
                                return Observable.just(new Object()).delay(1L, TimeUnit.MINUTES);
                            })
                    )
                    .subscribe(transaction -> {
                        TransactionsModel result = new TransactionsModel(transaction);
                        lastTransactionResult = new LastPositionsModel("lastTransaction", result.getHash(), result.getBlockNumber());

                        transactionsRepository.save(result);
                        lastPositionsRepository.save(lastTransactionResult);

                        LOGGER.info("Transaction written in mongo database: " + result.getBlockNumber());
                    }, error -> {
                        lastPositionsRepository.save(lastTransactionResult);
                        LOGGER.info(error.getMessage());
                    });
        }
    }

    @Override
    public void destroyConnections() {
        lastPositionsRepository.save(lastBlockResult);
        lastPositionsRepository.save(lastTransactionResult);

        blockSubscription.unsubscribe();
        transactionSubscription.unsubscribe();

        web3.shutdown();
    }
}
