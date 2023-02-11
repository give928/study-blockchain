package com.give928.blockchain.klaytn.service;

import com.give928.blockchain.common.domain.TransactionStatus;
import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.common.util.FeeUtil;
import com.give928.blockchain.common.util.HexadecimalUtil;
import com.klaytn.caver.methods.response.Block;
import com.klaytn.caver.methods.response.TransactionReceipt;
import org.springframework.stereotype.Service;

@Service
public class CaverTransactionResponseMapper {
    public TransactionResponse of(TransactionType transactionType, String transactionHash) {
        return TransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionHash)
                .build();
    }

    public TransactionResponse of(TransactionType transactionType,
                                  TransactionReceipt.TransactionReceiptData transactionReceipt,
                                  Block.BlockData<?> block, String errorMessage) {
        return TransactionResponse.builder()
                .transactionType(transactionType)
                .transactionHash(transactionReceipt.getTransactionHash())
                .status(TransactionStatus.find(transactionReceipt.getStatus()))
                .blockNumber(HexadecimalUtil.toBigInteger(transactionReceipt.getBlockNumber()))
                .timestamp(HexadecimalUtil.toLocalDateTime(block.getTimestamp()))
                .from(transactionReceipt.getFrom())
                .to(transactionReceipt.getTo())
                .value(HexadecimalUtil.toBigInteger(transactionReceipt.getValue()))
                .transactionFee(FeeUtil.calculate(transactionReceipt.getGasUsed(), transactionReceipt.getEffectiveGasPrice()))
                .gasPrice(HexadecimalUtil.toBigInteger(transactionReceipt.getGasPrice()))
                .nonce(HexadecimalUtil.toBigInteger(transactionReceipt.getNonce()))
                .errorMessage(errorMessage)
                .build();
    }
}
