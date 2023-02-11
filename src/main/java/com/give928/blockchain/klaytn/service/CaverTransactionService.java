package com.give928.blockchain.klaytn.service;

import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.request.TransactionRequest;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.common.service.TransactionService;
import com.give928.blockchain.ethereum.exception.Web3jErrorException;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Block;
import com.klaytn.caver.methods.response.Bytes32;
import com.klaytn.caver.methods.response.TransactionReceipt;
import com.klaytn.caver.transaction.TxPropertyBuilder;
import com.klaytn.caver.transaction.type.ValueTransfer;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

import java.io.IOException;
import java.math.BigInteger;

@Service
@Slf4j
public class CaverTransactionService implements TransactionService {
    private static final BigInteger DEFAULT_GAS = BigInteger.valueOf(25_000);

    private final Caver caver;
    private final CaverTransactionResponseMapper caverTransactionResponseMapper;

    public CaverTransactionService(Caver caver, CaverTransactionResponseMapper caverTransactionResponseMapper) {
        this.caver = caver;
        this.caverTransactionResponseMapper = caverTransactionResponseMapper;
    }

    // @formatter:off
    @Override
    public Mono<TransactionResponse> sendTransaction(TransactionRequest transactionRequest) {
        return sendRawTransaction(transactionRequest)
                .subscribeOn(Schedulers.boundedElastic())
                .map(bytes32 -> {
                    if (bytes32.hasError()) {
                        throw new Web3jErrorException(bytes32.getError());
                    }
                    return caverTransactionResponseMapper.map(TransactionType.TRANSACTION, bytes32.getResult());
                })
                .doOnNext(transactionResponse -> log.info("send transaction hash: {}", transactionResponse.getTransactionHash()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    private Mono<Bytes32> sendRawTransaction(TransactionRequest transactionRequest) {
        SingleKeyring keyring = caver.wallet.keyring.createFromPrivateKey(transactionRequest.getPrivateKey());
        // caver wallet 에 추가
        caver.wallet.add(keyring);

        // 자산 이전 트랜잭션 생성
        ValueTransfer valueTransfer = caver.transaction.valueTransfer.create(
                TxPropertyBuilder.valueTransfer()
                        .setFrom(keyring.getAddress())
                        .setTo(transactionRequest.getTo())
                        .setValue(transactionRequest.getValue())
                        .setGas(getGas(transactionRequest))
        );
        log.info("send transaction from: {}, to: {}, nonce: {}, wei: {}, gasPrice: {}, gas: {}",
                 valueTransfer.getFrom(), valueTransfer.getTo(), valueTransfer.getNonce(), valueTransfer.getValue(),
                 valueTransfer.getGasPrice(), valueTransfer.getGas());

        try {
            // 트랜잭션 서명
            valueTransfer.sign(keyring);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Klaytn으로 트랜잭션 전송
        return Mono.from(caver.rpc.klay.sendRawTransaction(valueTransfer.getRawTransaction())
                                 .flowable());
    }

    private static BigInteger getGas(TransactionRequest transactionRequest) {
        if (transactionRequest.getGasLimit() != null) {
            return transactionRequest.getGasLimit();
        }
        return DEFAULT_GAS;
    }

    @Override
    public Mono<TransactionResponse> getTransaction(String transactionHash) {
        return getTransactionReceipt(transactionHash)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(transactionReceiptData -> log.info("transaction receipt: {}",
                                                             transactionReceiptData != null ? transactionReceiptData.toString() : null))
                .flatMap(transactionReceiptData -> getBlock(transactionReceiptData.getBlockHash())
                        .map(ethBlock -> Tuples.of(transactionReceiptData, ethBlock)))
                .map(tuple -> caverTransactionResponseMapper.map(TransactionType.TRANSACTION, tuple.getT1(),
                                                                 tuple.getT2(), null));
    }

    private Mono<TransactionReceipt.TransactionReceiptData> getTransactionReceipt(String transactionHash) {
        return Mono.from(caver.rpc.klay.getTransactionReceipt(transactionHash)
                                 .flowable())
                .map(transactionReceipt -> {
                    if (transactionReceipt.hasError()) {
                        throw new Web3jErrorException(transactionReceipt.getError());
                    }

                    return transactionReceipt.getResult();
                });
    }

    private Mono<Block.BlockData> getBlock(String blockHash) {
        return Mono.from(caver.rpc.klay.getBlockByHash(blockHash)
                                 .flowable())
                .map(block -> {
                    if (block.hasError()) {
                        throw new Web3jErrorException(block.getError());
                    }

                    return block.getResult();
                });
    }
}
