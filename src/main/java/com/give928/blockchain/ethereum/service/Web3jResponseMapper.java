package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.common.domain.TransactionType;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

public interface Web3jResponseMapper<T> {
    T of(TransactionType transactionType, String transactionHash);

    T of(TransactionType transactionType, Transaction transaction);

    T of(TransactionType transactionType, Transaction transaction, TransactionReceipt transactionReceipt,
         EthBlock.Block block, String errorMessage);
}
