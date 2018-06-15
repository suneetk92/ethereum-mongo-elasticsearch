package com.sk.ethereum.service;

import com.sk.ethereum.model.BlocksModel;
import com.sk.ethereum.model.LastPositionsModel;
import com.sk.ethereum.model.TransactionsModel;
import com.sk.ethereum.repository.elasticsearch.ElasticsearchBlocksRepository;
import com.sk.ethereum.repository.elasticsearch.ElasticsearchLastPositionsRepository;
import com.sk.ethereum.repository.elasticsearch.ElasticsearchTransactionsRepository;
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
public class ElasticsearchService implements EthereumService {

    private static final Logger LOGGER = Logger.getLogger(ElasticsearchService.class.getName());

    private final ElasticsearchBlocksRepository blocksRepository;
    private final ElasticsearchLastPositionsRepository lastPositionsRepository;
    private final ElasticsearchTransactionsRepository transactionsRepository;

    @Value("${web3j.host}")
    private String httpHost;

    @Value("${lastBlock.number}")
    private Long number;

    @Value("${is.catchUpToLatestAndSubscribeToNewBlocksObservable}")
    private Boolean isCatchUpToLatestAndSubscribeToNewBlocksObservable;

    @Value("${is.blocksObservable}")
    private Boolean isBlocksObservable;

    @Value("${is.catchUpToLatestAndSubscribeToNewTransactionsObservable}")
    private Boolean isCatchUpToLatestAndSubscribeToNewTransactionsObservable;

    private Subscription blockSubscription;
    private Subscription transactionSubscription;

    private LastPositionsModel lastBlockResult;
    private LastPositionsModel lastTransactionResult;
    private Web3j web3;

    @Autowired
    public ElasticsearchService(ElasticsearchBlocksRepository blocksRepository, ElasticsearchLastPositionsRepository lastPositionsRepository, ElasticsearchTransactionsRepository transactionsRepository) {
        this.blocksRepository = blocksRepository;
        this.lastPositionsRepository = lastPositionsRepository;
        this.transactionsRepository = transactionsRepository;
    }

    @Override
    public void storeInDB() {
        web3 = Web3j.build(new HttpService(httpHost));

        if (isBlocksObservable) {
            blockSubscription = web3.blockObservable(true)
                    .retryWhen(errors -> errors.flatMap(error -> {
                                LOGGER.info("Error occurred: " + error);

                                lastPositionsRepository.save(lastBlockResult);
                                return Observable.just(new Object()).delay(1L, TimeUnit.MINUTES);
                            })
                    )
                    .subscribe(block -> {
                        BlocksModel result = new BlocksModel(block.getBlock());
                        lastBlockResult = new LastPositionsModel("lastBlock", result.getNumber());

                        blocksRepository.save(result);
                        lastPositionsRepository.save(lastBlockResult);

                        LOGGER.info("Block written in elasticsearch database: " + result.getNumber());
                    }, error -> {
                        lastPositionsRepository.save(lastBlockResult);
                        LOGGER.info("Exception occurred: " + error.getMessage());
                    });
        }

        if (isCatchUpToLatestAndSubscribeToNewBlocksObservable) {
            Optional<LastPositionsModel> lastBlock = lastPositionsRepository.findById("lastBlock");
            lastBlockResult = lastBlock.orElseGet(() -> new LastPositionsModel("lastBlock", number));
            BigInteger blockNumber = BigInteger.valueOf(lastBlockResult.getNumber());

            LOGGER.info("Last Block synced in elasticsearch till: " + blockNumber);

            blockSubscription = web3.catchUpToLatestAndSubscribeToNewBlocksObservable(DefaultBlockParameter.valueOf(blockNumber), true)
                    .retryWhen(errors -> errors.flatMap(error -> {
                                LOGGER.info("Error occurred: " + error);

                                lastPositionsRepository.save(lastBlockResult);
                                return Observable.just(new Object()).delay(1L, TimeUnit.MINUTES);
                            })
                    )
                    .subscribe(block -> {
                        BlocksModel result = new BlocksModel(block.getBlock());
                        lastBlockResult = new LastPositionsModel("lastBlock", result.getNumber());

                        blocksRepository.save(result);
                        lastPositionsRepository.save(lastBlockResult);

                        LOGGER.info("Block written in elasticsearch database: " + result.getNumber());
                    }, error -> {
                        lastPositionsRepository.save(lastBlockResult);
                        LOGGER.info("Exception occurred: " + error.getMessage());
                    });
        }

        if (isCatchUpToLatestAndSubscribeToNewTransactionsObservable) {
            Optional<LastPositionsModel> lastTransaction = lastPositionsRepository.findById("lastTransaction");
            lastTransactionResult = lastTransaction.orElseGet(() -> new LastPositionsModel("lastTransaction", number));
            BigInteger transactionNumber = BigInteger.valueOf(lastTransactionResult.getNumber());

            LOGGER.info("Last Transaction synced in elasticsearch till: " + transactionNumber);

            transactionSubscription = web3.catchUpToLatestAndSubscribeToNewTransactionsObservable(DefaultBlockParameter.valueOf(transactionNumber))
                    .retryWhen(errors -> errors.flatMap(error -> {
                                LOGGER.info("Error occurred: " + error);
                                lastPositionsRepository.save(lastTransactionResult);
                                return Observable.just(new Object()).delay(1L, TimeUnit.MINUTES);
                            })
                    )
                    .subscribe(transaction -> {
                        TransactionsModel result = new TransactionsModel(transaction);
                        lastTransactionResult = new LastPositionsModel("lastTransaction", result.getBlockNumber());

                        transactionsRepository.save(result);
                        lastPositionsRepository.save(lastTransactionResult);

                        LOGGER.info("Transaction written in elasticsearch database: " + result.getBlockNumber());
                    }, error -> {
                        lastPositionsRepository.save(lastTransactionResult);
                        LOGGER.info("Exception occurred: " + error.getMessage());
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
