package com.give928.blockchain.common.service;

import com.give928.blockchain.common.domain.TokenType;
import com.give928.blockchain.common.domain.TransactionStatus;
import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.response.ContractTransactionResponse;
import com.give928.blockchain.common.response.ContractTransactionTokenResponse;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
public class ContractTransactionResponseMapper implements TransactionMapper<ContractTransactionResponse> {
    public ContractTransactionResponse map(TransactionType transactionType, String transactionHash) {
        return ContractTransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionHash)
                .build();
    }

    public ContractTransactionResponse map(TransactionType transactionType, Transaction transaction) {
        return ContractTransactionResponse.builder()
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

    public ContractTransactionResponse map(TransactionType transactionType, Transaction transaction,
                                           TransactionReceipt transactionReceipt, EthBlock.Block block,
                                           String errorMessage) {
        BigInteger transactionFee = null;
        if (transaction.getGasPrice() != null && transactionReceipt.getGasUsed() != null) {
            transactionFee = transaction.getGasPrice().multiply(transactionReceipt.getGasUsed());
        }
        LocalDateTime timestamp = null;
        if (block != null && block.getTimestamp() != null) {
            timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(block.getTimestamp().longValueExact() * 1_000),
                                                TimeZone.getDefault().toZoneId());
        }
        TransactionStatus status = TransactionStatus.FAIL;
        if (transactionReceipt.isStatusOK()) {
            status = TransactionStatus.SUCCESS;
        }

        return ContractTransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transaction.getHash())
                .status(status)
                .blockNumber(transaction.getBlockNumber())
                .timestamp(timestamp)
                .from(transaction.getFrom())
                .to(transaction.getTo())
                .tokens(transactionReceipt.getLogs()
                                .stream()
                                .map(log -> ContractTransactionTokenResponse.builder()
                                        .from(TransactionMapper.removeHexadecimalPadding(log.getTopics().get(1)))
                                        .to(TransactionMapper.removeHexadecimalPadding(log.getTopics().get(2)))
                                        .tokenId(TransactionMapper.toBigInteger(log.getTopics().get(3)))
                                        .tokenType(TokenType.ERC721)
                                        .build())
                                .toList())
                .value(transaction.getValue())
                .transactionFee(transactionFee)
                .gasPrice(transaction.getGasPrice())
                .nonce(transaction.getNonce())
                .errorMessage(errorMessage)
                .build();
    }
}
