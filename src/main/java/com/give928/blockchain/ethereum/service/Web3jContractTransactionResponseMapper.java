package com.give928.blockchain.ethereum.service;

import com.give928.blockchain.common.domain.TokenType;
import com.give928.blockchain.common.domain.TransactionStatus;
import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.response.ContractTransactionResponse;
import com.give928.blockchain.common.response.ContractTransactionTokenResponse;
import com.give928.blockchain.common.util.FeeUtil;
import com.give928.blockchain.common.util.HexadecimalUtil;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

@Service
public class Web3jContractTransactionResponseMapper implements Web3jResponseMapper<ContractTransactionResponse> {
    public ContractTransactionResponse of(TransactionType transactionType, String transactionHash) {
        return ContractTransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionHash)
                .build();
    }

    public ContractTransactionResponse of(TransactionType transactionType, Transaction transaction) {
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

    public ContractTransactionResponse of(TransactionType transactionType, Transaction transaction,
                                          TransactionReceipt transactionReceipt, EthBlock.Block block,
                                          String errorMessage) {
        LocalDateTime timestamp = null;
        if (block != null && block.getTimestamp() != null) {
            timestamp = LocalDateTime.ofInstant(Instant.ofEpochMilli(block.getTimestamp().longValueExact() * 1_000),
                                                TimeZone.getDefault().toZoneId());
        }
        return ContractTransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionReceipt.getTransactionHash())
                .status(TransactionStatus.find(transactionReceipt.isStatusOK()))
                .blockNumber(transactionReceipt.getBlockNumber())
                .timestamp(timestamp)
                .from(transactionReceipt.getFrom())
                .to(transactionReceipt.getTo())
                .tokens(transactionReceipt.getLogs()
                                .stream()
                                .map(log -> ContractTransactionTokenResponse.builder()
                                        .from(HexadecimalUtil.removeHexadecimalPadding(log.getTopics().get(1)))
                                        .to(HexadecimalUtil.removeHexadecimalPadding(log.getTopics().get(2)))
                                        .tokenId(HexadecimalUtil.toBigInteger(log.getTopics().get(3)))
                                        .tokenType(TokenType.ERC721)
                                        .build())
                                .toList())
                .value(transaction.getValue())
                .transactionFee(FeeUtil.calculate(transactionReceipt.getGasUsed(), transactionReceipt.getEffectiveGasPrice()))
                .gasPrice(transaction.getGasPrice())
                .nonce(transaction.getNonce())
                .errorMessage(errorMessage)
                .build();
    }
}
