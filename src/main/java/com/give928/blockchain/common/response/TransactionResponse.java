package com.give928.blockchain.common.response;

import com.give928.blockchain.common.domain.TransactionStatus;
import com.give928.blockchain.common.domain.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
public class TransactionResponse {
    private final TransactionType transactionType;
    private final String transactionHash;
    private final TransactionStatus status;
    private final BigInteger blockNumber;
    private final LocalDateTime timestamp;
    private final String from;
    private final String to;
    private final BigInteger value;
    private final BigInteger transactionFee;
    private final BigInteger gasPrice;
    private final BigInteger nonce;
    private final String errorMessage;

    @Builder
    private TransactionResponse(TransactionType transactionType, String transactionHash, TransactionStatus status,
                                BigInteger blockNumber, LocalDateTime timestamp, String from, String to,
                                BigInteger value, BigInteger transactionFee, BigInteger gasPrice, BigInteger nonce,
                                String errorMessage) {
        this.transactionType = transactionType;
        this.transactionHash = transactionHash;
        this.status = status;
        this.blockNumber = blockNumber;
        this.timestamp = timestamp;
        this.from = from;
        this.to = to;
        this.value = value;
        this.transactionFee = transactionFee;
        this.gasPrice = gasPrice;
        this.nonce = nonce;
        this.errorMessage = errorMessage;
    }
}
