package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.common.domain.TransactionStatus;
import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.common.util.FeeUtil;
import com.give928.blockchain.common.util.HexadecimalUtil;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.time.LocalDateTime;

@Service
public class Web3jTransactionResponseMapper implements Web3jResponseMapper<TransactionResponse> {
    public TransactionResponse mapTransaction(TransactionType transactionType, String transactionHash) {
        return TransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionHash)
                .build();
    }

    public TransactionResponse mapTransaction(TransactionType transactionType, Transaction transaction) {
        return TransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transaction.getHash())
                .status(TransactionStatus.PENDING)
                .from(transaction.getFrom())
                .to(transaction.getTo())
                .value(transaction.getValue())
                .gasPrice(transaction.getGasPrice())
                .nonce(transaction.getNonce())
                .build();
    }

    public TransactionResponse mapTransaction(TransactionType transactionType, Transaction transaction,
                                              TransactionReceipt transactionReceipt, EthBlock.Block block,
                                              String errorMessage) {
        LocalDateTime timestamp = null;
        if (block != null && block.getTimestamp() != null) {
            timestamp = HexadecimalUtil.toLocalDateTime(block.getTimestamp());
        }
        return TransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transaction.getHash())
                .status(TransactionStatus.find(transactionReceipt.isStatusOK()))
                .blockNumber(transaction.getBlockNumber())
                .timestamp(timestamp)
                .from(transaction.getFrom())
                .to(transaction.getTo())
                .value(transaction.getValue())
                .transactionFee(FeeUtil.calculate(transactionReceipt.getGasUsed(), transactionReceipt.getEffectiveGasPrice()))
                .gasPrice(transaction.getGasPrice())
                .nonce(transaction.getNonce())
                .errorMessage(errorMessage)
                .build();
    }
}
