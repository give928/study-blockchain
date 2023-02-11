package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.common.response.WalletResponse;
import com.give928.blockchain.common.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.web3j.crypto.*;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.utils.Convert;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Service
@Slf4j
public class Web3jWalletService implements WalletService<WalletResponse> {
    public static final String HEXADECIMAL_FORMAT = "0x%s";
    private final Web3j web3j;

    public Web3jWalletService(Web3j web3j) {
        this.web3j = web3j;
    }

    // @formatter:off
    @Override
    public Mono<WalletResponse> createWallet(String password) {
        return Mono.<WalletResponse>fromCallable(() -> {
                    try {
                        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
                        WalletFile walletFile = Wallet.createStandard(password, ecKeyPair);
                        String address = walletFile.getAddress();
                        String privateKey = ecKeyPair.getPrivateKey().toString(16);
                        return WalletResponse.builder()
                                .address(String.format(HEXADECIMAL_FORMAT, address))
                                .privateKey(String.format(HEXADECIMAL_FORMAT, privateKey))
                                .build();
                    } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException |
                             CipherException e) {
                        log.error("create wallet exception", e);
                        throw new IllegalStateException(e);
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .doOnNext(walletResponse -> log.info("create wallet. address: {}, privateKey: {}", walletResponse.getAddress(), walletResponse.getPrivateKey()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    // @formatter:off
    @Override
    public Mono<BigInteger> getBalance(String address) {
        return Mono.from(web3j.ethGetBalance(address, DefaultBlockParameterName.LATEST)
                                       .flowable())
                .subscribeOn(Schedulers.boundedElastic())
                .map(EthGetBalance::getBalance)
                .doOnNext(balance -> log.info("address: {}, balance: {}", address, balance));
    }
    // @formatter:on

    @Override
    public Mono<BigDecimal> getCoin(String address) {
        return getBalance(address)
                .map(balance -> Convert.fromWei(balance.toString(), Convert.Unit.ETHER));
    }
}
