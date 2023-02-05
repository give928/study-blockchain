package com.give928.blockchain.klaytn.response;

import com.give928.blockchain.common.response.WalletResponse;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class KlaytnWalletResponse extends WalletResponse {
    private final String publicKey;
    private final String klaytnWalletKey;
}
