package com.give928.blockchain.ethereum.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Wallet {
    private final String address;
    private final String privateKey;

    @Builder
    private Wallet(String address, String privateKey) {
        this.address = address;
        this.privateKey = privateKey;
    }
}
