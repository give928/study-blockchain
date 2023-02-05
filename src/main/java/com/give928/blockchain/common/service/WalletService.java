package com.give928.blockchain.common.service;

import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.BigInteger;

public interface WalletService<T> {
    Mono<T> createWallet(String password);

    Mono<BigInteger> getBalance(String address);

    Mono<BigDecimal> getCoin(String address);
}
