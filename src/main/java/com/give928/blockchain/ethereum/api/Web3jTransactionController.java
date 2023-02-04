package com.give928.blockchain.ethereum.api;

import com.give928.blockchain.common.request.TransactionRequest;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.ethereum.service.Web3jTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/ethereum/web3j/transactions")
@RestController
public class Web3jTransactionController {
    private final Web3jTransactionService web3jTransactionService;

    public Web3jTransactionController(Web3jTransactionService web3jTransactionService) {
        this.web3jTransactionService = web3jTransactionService;
    }

    @PostMapping("")
    public Mono<ResponseEntity<TransactionResponse>> sendTransaction(@RequestBody TransactionRequest transactionRequest) {
        return web3jTransactionService.sendTransaction(transactionRequest)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{transactionHash}")
    public Mono<ResponseEntity<TransactionResponse>> sendTransaction(@PathVariable String transactionHash) {
        return web3jTransactionService.getTransaction(transactionHash)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }
}
