package com.give928.blockchain.ethereum.domain;

import lombok.Builder;
import lombok.Getter;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Getter
public class Web3jTransactionHelper {
    private final FastRawTransactionManager fastRawTransactionManager;
    private final BigInteger chainId;
    private final BigInteger nonce;
    private final BigInteger gasPrice;
    private final BigInteger gasLimit;
    private final StaticGasProvider staticGasProvider;

    @Builder
    private Web3jTransactionHelper(FastRawTransactionManager fastRawTransactionManager, BigInteger chainId,
                                   BigInteger nonce, BigInteger gasPrice, BigInteger gasLimit,
                                   StaticGasProvider staticGasProvider) {
        this.fastRawTransactionManager = fastRawTransactionManager;
        this.chainId = chainId;
        this.nonce = nonce;
        this.gasPrice = gasPrice;
        this.gasLimit = gasLimit;
        StaticGasProvider sgp = staticGasProvider;
        if (gasPrice != null && gasLimit != null) {
            sgp = new StaticGasProvider(gasPrice, gasLimit);
        }
        if (sgp == null) {
            sgp = new DefaultGasProvider();
        }
        this.staticGasProvider = sgp;
    }

    public FastRawTransactionManager getFastRawTransactionManager() {
        return fastRawTransactionManager;
    }

    public BigInteger getGasPrice() {
        if (staticGasProvider.getGasPrice() != null) {
            return staticGasProvider.getGasPrice();
        }
        return gasPrice;
    }

    public BigInteger getGasLimit() {
        if (staticGasProvider.getGasLimit() != null) {
            return staticGasProvider.getGasLimit();
        }
        return gasLimit;
    }
}
