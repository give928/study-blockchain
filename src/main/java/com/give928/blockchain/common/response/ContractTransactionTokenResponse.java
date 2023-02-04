package com.give928.blockchain.common.response;

import com.give928.blockchain.common.domain.TokenType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
public class ContractTransactionTokenResponse {
    private final String from;
    private final String to;
    private final BigInteger tokenId;
    private final TokenType tokenType;

    @Builder
    private ContractTransactionTokenResponse(String from, String to, BigInteger tokenId, TokenType tokenType) {
        this.from = from;
        this.to = to;
        this.tokenId = tokenId;
        this.tokenType = tokenType;
    }
}
