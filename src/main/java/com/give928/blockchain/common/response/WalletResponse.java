package com.give928.blockchain.common.response;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class WalletResponse {
    private final String address;
    private final String privateKey;
}
