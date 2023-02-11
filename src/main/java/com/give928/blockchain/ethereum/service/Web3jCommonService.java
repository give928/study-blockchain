package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.ethereum.domain.Web3jTransactionHelper;
import com.give928.blockchain.ethereum.exception.Web3jErrorException;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.response.Callback;
import org.web3j.tx.response.NoOpProcessor;
import org.web3j.tx.response.PollingTransactionReceiptProcessor;
import org.web3j.tx.response.QueuingTransactionReceiptProcessor;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@Service
public class Web3jCommonService {
    private final DefaultGasProvider defaultGasProvider = new DefaultGasProvider();
    public static final int SLEEP_DURATION = 3_000;
    public static final int ATTEMPTS = 30;

    private final Web3j web3j;

    public Web3jCommonService(Web3j web3j) {
        this.web3j = web3j;
    }

    public Mono<BigInteger> getChainId() {
        return Mono.from(web3j.ethChainId()
                                 .flowable())
                .map(ethChainId -> {
                    if (ethChainId.hasError()) {
                        throw new Web3jErrorException(ethChainId.getError());
                    }

                    return ethChainId.getChainId();
                });
    }

    public Mono<BigInteger> getNonce(String address) {
        return Mono.from(web3j.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST)
                                 .flowable())
                .map(ethGetTransactionCount -> {
                    if (ethGetTransactionCount.hasError()) {
                        throw new Web3jErrorException(ethGetTransactionCount.getError());
                    }

                    return ethGetTransactionCount.getTransactionCount();
                });
    }

    public Mono<BigInteger> getGasPrice() {
        return getGasPrice(null);
    }

    public Mono<BigInteger> getGasPrice(BigInteger gasPrice) {
        return Mono.justOrEmpty(gasPrice)
                .switchIfEmpty(Mono.from(web3j.ethGasPrice()
                                                 .flowable())
                                       .map(ethBlock -> {
                                           if (ethBlock.hasError()) {
                                               return DefaultGasProvider.GAS_PRICE;
                                           }
                                           return ethBlock.getGasPrice();
                                       }));
    }

    public Mono<BigInteger> getGasLimit() {
        return getGasLimit(null);
    }

    public Mono<BigInteger> getGasLimit(BigInteger gasLimit) {
        return Mono.justOrEmpty(gasLimit)
                .switchIfEmpty(Mono.from(web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, false)
                                                 .flowable())
                                       .map(ethBlock -> {
                                           if (ethBlock.hasError()) {
                                               return DefaultGasProvider.GAS_LIMIT;
                                           }
                                           return ethBlock.getBlock()
                                                   .getGasLimit();
                                       }));
    }

    public Mono<Web3jTransactionHelper> getTransactionHelper(String privateKey) {
        return Mono.zip(getChainId(), getNonce(privateKey), getGasLimit(), getGasPrice())
                .map(tuple -> Web3jTransactionHelper.builder()
                        .chainId(tuple.getT1())
                        .nonce(tuple.getT2())
                        .gasPrice(tuple.getT3())
                        .gasLimit(tuple.getT4())
                        .build());
    }

    public Mono<Web3jTransactionHelper> newDefaultTransactionHelper(String privateKey) {
        return getChainId()
                .map(chainId -> Web3jTransactionHelper.builder()
                        .fastRawTransactionManager(new FastRawTransactionManager(web3j, Credentials.create(privateKey),
                                                                                 chainId.longValue(),
                                                                                 new NoOpProcessor(web3j)))
                        .staticGasProvider(defaultGasProvider)
                        .build());
    }

    public Mono<Web3jTransactionHelper> newNoOpTransactionHelper(String privateKey) {
        return Mono.zip(getChainId(), getGasPrice(), getGasLimit())
                .map(tuple -> Web3jTransactionHelper.builder()
                        .fastRawTransactionManager(new FastRawTransactionManager(web3j, Credentials.create(privateKey),
                                                                                 tuple.getT1().longValue(),
                                                                                 new NoOpProcessor(web3j)))
                        .gasPrice(tuple.getT2())
                        .gasLimit(tuple.getT3())
                        .build());
    }

    public Mono<Web3jTransactionHelper> newPollingTransactionHelper(String privateKey) {
        return Mono.zip(getChainId(), getGasPrice(), getGasLimit())
                .map(tuple -> Web3jTransactionHelper.builder()
                        .fastRawTransactionManager(new FastRawTransactionManager(web3j, Credentials.create(privateKey),
                                                                                 tuple.getT1().longValue(),
                                                                                 new PollingTransactionReceiptProcessor(
                                                                                         web3j, SLEEP_DURATION,
                                                                                         ATTEMPTS)))
                        .gasPrice(tuple.getT2())
                        .gasLimit(tuple.getT3())
                        .build());
    }

    public Mono<Web3jTransactionHelper> newQueuingTransactionHelper(String privateKey, Callback callback) {
        return Mono.zip(getChainId(), getGasPrice(), getGasLimit())
                .map(tuple -> Web3jTransactionHelper.builder()
                        .fastRawTransactionManager(
                                new FastRawTransactionManager(web3j, Credentials.create(privateKey),
                                                              tuple.getT1().longValue(),
                                                              new QueuingTransactionReceiptProcessor(web3j, callback,
                                                                      TransactionManager.DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH,
                                                                      TransactionManager.DEFAULT_POLLING_FREQUENCY)))
                        .gasPrice(tuple.getT2())
                        .gasLimit(tuple.getT3())
                        .build());
    }
}
