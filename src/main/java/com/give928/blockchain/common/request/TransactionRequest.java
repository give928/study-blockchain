package com.give928.blockchain.common.request;

import lombok.Getter;

import java.math.BigInteger;

@Getter
public class TransactionRequest {
    private String privateKey;
    private String to;
    private BigInteger value;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
}
