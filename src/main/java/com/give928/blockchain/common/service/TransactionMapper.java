package com.give928.blockchain.common.service;

import com.give928.blockchain.common.domain.TransactionType;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;

public interface TransactionMapper<T> {
    String HEX_PREFIX = "0x";
    String HEX_PADDING = "0x000000000000000000000000";
    String EMPTY = "";
    int RADIX = 16;

    T map(TransactionType transactionType, String transactionHash);

    T map(TransactionType transactionType, Transaction transaction);

    T map(TransactionType transactionType, Transaction transaction, TransactionReceipt transactionReceipt,
          EthBlock.Block block, String errorMessage);

    static String removeHexadecimalPadding(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return hexadecimal.replace(HEX_PADDING, HEX_PREFIX);
    }

    static BigInteger toBigInteger(String hexadecimal) {
        if (hexadecimal == null) {
            return null;
        }
        return new BigInteger(hexadecimal.replace(HEX_PREFIX, EMPTY), RADIX);
    }
}
