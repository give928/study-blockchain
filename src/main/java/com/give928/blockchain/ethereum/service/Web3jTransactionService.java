package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.request.TransactionRequest;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.common.service.TransactionService;
import com.give928.blockchain.ethereum.domain.Web3jTransactionHelper;
import com.give928.blockchain.ethereum.exception.Web3jErrorException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
public class Web3jTransactionService implements TransactionService {
    private final Web3j web3j;
    private final Web3jCommonService web3JCommonService;
    private final Web3jTransactionResponseMapper transactionResponseMapper;
    private final WebClient tenderlyApiWebClient;

    public Web3jTransactionService(Web3j web3j, Web3jCommonService web3JCommonService,
                                   Web3jTransactionResponseMapper transactionResponseMapper,
                                   WebClient tenderlyApiWebClient) {
        this.web3j = web3j;
        this.web3JCommonService = web3JCommonService;
        this.transactionResponseMapper = transactionResponseMapper;
        this.tenderlyApiWebClient = tenderlyApiWebClient;
    }

    // @formatter:off
    @Override
    public Mono<TransactionResponse> sendTransaction(TransactionRequest transactionRequest) {
        return web3JCommonService.newPollingTransactionHelper(transactionRequest.getPrivateKey())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(web3jTransactionHelper -> sendTransaction(transactionRequest, web3jTransactionHelper))
                .doOnNext(transactionReceipt -> log.info("send transaction receipt: {}", transactionReceipt.toString()))
                .map(transactionReceipt -> transactionResponseMapper.of(TransactionType.TRANSACTION, transactionReceipt.getTransactionHash()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    // @formatter:off
    private Mono<TransactionReceipt> sendTransaction(TransactionRequest transactionRequest,
                                                     Web3jTransactionHelper web3jTransactionHelper) {
        String to = transactionRequest.getTo();
        BigDecimal value = BigDecimal.valueOf(transactionRequest.getValue()
                                                      .longValue());
        BigInteger gasPrice = web3jTransactionHelper.getGasPrice();
        BigInteger gasLimit = web3jTransactionHelper.getGasLimit();

        FastRawTransactionManager fastRawTransactionManager = web3jTransactionHelper.getFastRawTransactionManager();
        log.info("send transaction from: {}, to: {}, nonce: {}, wei: {}, gasPrice: {}, gasLimit: {}",
                 fastRawTransactionManager.getFromAddress(), to, fastRawTransactionManager.getCurrentNonce(), value, gasPrice, gasLimit);

        return Mono.from(new Transfer(web3j, fastRawTransactionManager)
                                       .sendFunds(to, value, Convert.Unit.WEI, gasPrice, gasLimit)
                                       .flowable());
    }
    // @formatter:on

    // @formatter:off
    public Mono<TransactionResponse> sendSignedTransaction(TransactionRequest transactionRequest) {
        return web3JCommonService.getTransactionHelper(transactionRequest.getPrivateKey())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(web3jTransactionHelper -> sendSignedTransaction(transactionRequest, web3jTransactionHelper))
                .doOnNext(ethSendTransaction -> log.info(
                        "transaction hash: {}", ethSendTransaction.getTransactionHash()))
                .map(ethSendTransaction -> transactionResponseMapper.of(TransactionType.TRANSACTION, ethSendTransaction.getTransactionHash()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    // @formatter:off
    private Mono<EthSendTransaction> sendSignedTransaction(TransactionRequest transactionRequest,
                                                           Web3jTransactionHelper web3jTransactionHelper) {
        return Mono.fromCallable(() -> {
                    Credentials credentials = Credentials.create(transactionRequest.getPrivateKey());
                    String to = transactionRequest.getTo();
                    BigInteger value = transactionRequest.getValue();
                    long chainId = web3jTransactionHelper.getChainId()
                            .longValue();
                    BigInteger nonce = web3jTransactionHelper.getNonce();
                    BigInteger gasPrice = web3jTransactionHelper.getGasPrice();
                    BigInteger gasLimit = web3jTransactionHelper.getGasLimit();

                    log.info("send transaction from: {}, to: {}, wei: {}, chainId: {}, nonce: {}, gasLimit: {}, gasPrice: {}",
                             credentials.getAddress(), to, value, chainId, nonce, gasLimit, gasPrice);

                    RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, gasPrice, gasLimit, to,
                                                                                          value);
                    return TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
                })
                .flatMap(signedMessage -> Mono.from(web3j.ethSendRawTransaction(Numeric.toHexString(signedMessage))
                                                            .flowable()));
    }
    // @formatter:on

    @Override
    public Mono<TransactionResponse> getTransaction(String transactionHash) {
        return getEthTransactionAndReceiptAndBlock(transactionHash)
                .map(tuple -> toTransactionResponse(transactionHash, tuple.getT1(), tuple.getT2(), tuple.getT3(), tuple.getT4().orElse(null)));
    }

    // @formatter:off
    public Mono<Tuple4<EthTransaction, EthGetTransactionReceipt, EthBlock, Optional<String>>> getEthTransactionAndReceiptAndBlock(String transactionHash) {
        return Mono.zip(getEthTransaction(transactionHash), getEthGetTransactionReceipt(transactionHash))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(tuple -> log.info("transaction receipt: {}", tuple.getT2().getResult() != null ? tuple.getT2().getResult().toString() : null))
                .flatMap(tuple -> {
                    EthTransaction ethTransaction = tuple.getT1();
                    return getEthBlock(ethTransaction
                                               .getResult()
                                               .getBlockHash())
                            .map(ethBlock -> Tuples.of(ethTransaction, tuple.getT2(), ethBlock));
                })
                .flatMap(tuple -> {
                    EthTransaction ethTransaction = tuple.getT1();
                    EthGetTransactionReceipt ethGetTransactionReceipt = tuple.getT2();
                    if (ethTransaction.getTransaction().isPresent() &&
                            ethGetTransactionReceipt.getTransactionReceipt().isPresent() &&
                            !ethGetTransactionReceipt.getResult().isStatusOK()) {
                        Transaction transaction = ethTransaction.getResult();
                        return getErrorMessage(transaction.getChainId(), transaction.getHash())
                                .map(errorMessage -> Tuples.of(tuple.getT1(), tuple.getT2(), tuple.getT3(), Optional.of(errorMessage)));
                    }
                    return Mono.just(Tuples.of(tuple.getT1(), tuple.getT2(), tuple.getT3(), Optional.<String>empty()));
                })
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    // @formatter:off
    private Mono<String> getErrorMessage(long chainId, String transactionHash) {
        return tenderlyApiWebClient.get()
                .uri(uriBuilder -> uriBuilder.path("/{chainId}/tx/{transactionHash}")
                        .build(chainId, transactionHash))
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Map.class))
                .map(map -> (String) map.get("error_message"));
    }
    // @formatter:on

    // @formatter:off
    private Mono<EthTransaction> getEthTransaction(String transactionHash) {
        return Mono.from(web3j.ethGetTransactionByHash(transactionHash)
                                 .flowable())
                .map(ethTransaction -> {
                    if (ethTransaction.hasError()) {
                        throw new Web3jErrorException(ethTransaction.getError());
                    }

                    return ethTransaction;
                });
    }
    // @formatter:on

    // @formatter:off
    private Mono<EthGetTransactionReceipt> getEthGetTransactionReceipt(String transactionHash) {
        return Mono.from(web3j.ethGetTransactionReceipt(transactionHash)
                                 .flowable())
                .map(ethGetTransactionReceipt -> {
                    if (ethGetTransactionReceipt.hasError()) {
                        throw new Web3jErrorException(ethGetTransactionReceipt.getError());
                    }

                    return ethGetTransactionReceipt;
                });
    }
    // @formatter:on

    // @formatter:off
    private Mono<EthBlock> getEthBlock(String blockHash) {
        return Mono.from(web3j.ethGetBlockByHash(blockHash, false)
                                 .flowable())
                .map(ethBlock -> {
                    if (ethBlock.hasError()) {
                        throw new Web3jErrorException(ethBlock.getError());
                    }

                    return ethBlock;
                });
    }
    // @formatter:on

    private TransactionResponse toTransactionResponse(String transactionHash, EthTransaction ethTransaction,
                                                      EthGetTransactionReceipt ethGetTransactionReceipt,
                                                      EthBlock ethBlock, String errorMessage) {
        // 거래가 조회되지 않음(Dropped and Replaced)
        Optional<Transaction> transactionOptional = ethTransaction.getTransaction();
        if (ethTransaction.hasError() || transactionOptional.isEmpty()) {
            return transactionResponseMapper.of(TransactionType.TRANSACTION, transactionHash);
        }
        Transaction transaction = transactionOptional.get();

        // 거래 영수증이 조회되지 않음(Pending or Indexing, 아직 거래가 처리되지 않음)
        Optional<TransactionReceipt> transactionReceiptOptional = ethGetTransactionReceipt.getTransactionReceipt();
        if (ethGetTransactionReceipt.hasError() || transactionReceiptOptional.isEmpty()) {
            return transactionResponseMapper.of(TransactionType.TRANSACTION, transaction);
        }

        // Success or Failed, 블록 정보를 조회해서 블록의 timestamp 를 거래 시각으로 설정
        return transactionResponseMapper.of(TransactionType.TRANSACTION, transaction,
                                            transactionReceiptOptional.get(),
                                            ethBlock.getBlock(), errorMessage);
    }
}
