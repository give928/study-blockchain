package com.give928.blockchain.klaytn.service;

import com.give928.blockchain.common.service.WalletService;
import com.give928.blockchain.klaytn.response.KlaytnWalletResponse;
import com.klaytn.caver.Caver;
import com.klaytn.caver.methods.response.Quantity;
import com.klaytn.caver.utils.Utils;
import com.klaytn.caver.wallet.keyring.PrivateKey;
import com.klaytn.caver.wallet.keyring.SingleKeyring;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.BigInteger;

@Service
@Slf4j
public class CaverWalletService implements WalletService<KlaytnWalletResponse> {
    private final Caver caver;

    public CaverWalletService(Caver caver) {
        this.caver = caver;
    }

    // @formatter:off
    @Override
    public Mono<KlaytnWalletResponse> createWallet(String password) {
        return Mono.<KlaytnWalletResponse>fromCallable(() -> {
                    SingleKeyring singleKeyring = caver.wallet.keyring.generate(password);
                    PrivateKey privateKey = singleKeyring.getKey();
                    return KlaytnWalletResponse.builder()
                            .address(singleKeyring.getAddress())
                            .privateKey(privateKey.getPrivateKey())
                            .publicKey(privateKey.getPublicKey(true))
                            .klaytnWalletKey(singleKeyring.getKlaytnWalletKey())
                            .build();
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(walletResponse -> log.info(
                        "create wallet. address: {}, privateKey: {}, publicKey: {}, klaytnWalletKey: {}",
                        walletResponse.getAddress(), walletResponse.getPrivateKey(), walletResponse.getPublicKey(),
                        walletResponse.getKlaytnWalletKey()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    @Override
    public Mono<BigInteger> getBalance(String address) {
        return Mono.from(caver.rpc.klay.getBalance(address)
                                 .flowable())
                .subscribeOn(Schedulers.boundedElastic())
                .map(Quantity::getValue)
                .doOnNext(balance -> log.info("address: {}, balance: {}", address, balance));
    }

    @Override
    public Mono<BigDecimal> getCoin(String address) {
        return getBalance(address)
                .map(balance -> new BigDecimal(Utils.convertFromPeb(String.valueOf(balance), Utils.KlayUnit.KLAY)));
    }
}
