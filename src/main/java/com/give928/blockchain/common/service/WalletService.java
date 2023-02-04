package com.give928.blockchain.common.service;

import com.give928.blockchain.common.response.WalletResponse;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface WalletService {
    Mono<WalletResponse> createWallet(String password);

    Mono<BigInteger> getBalance(String address);

    Mono<BigDecimal> getCoin(String address);
}
