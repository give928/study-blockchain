package com.give928.blockchain.common.service;

import com.give928.blockchain.common.request.TransactionRequest;
import com.give928.blockchain.common.response.TransactionResponse;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<TransactionResponse> sendTransaction(TransactionRequest transactionRequest);

    Mono<TransactionResponse> getTransaction(String transactionHash);
}
