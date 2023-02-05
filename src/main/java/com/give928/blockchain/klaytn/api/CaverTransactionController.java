package com.give928.blockchain.klaytn.api;

import com.give928.blockchain.common.request.TransactionRequest;
import com.give928.blockchain.common.response.TransactionResponse;
import com.give928.blockchain.klaytn.service.CaverTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/klaytn/caver/transactions")
@RestController
public class CaverTransactionController {
    private final CaverTransactionService caverTransactionService;

    public CaverTransactionController(CaverTransactionService caverTransactionService) {
        this.caverTransactionService = caverTransactionService;
    }

    @PostMapping("")
    public Mono<ResponseEntity<TransactionResponse>> sendTransaction(@RequestBody TransactionRequest transactionRequest) {
        return caverTransactionService.sendTransaction(transactionRequest)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{transactionHash}")
    public Mono<ResponseEntity<TransactionResponse>> sendTransaction(@PathVariable String transactionHash) {
        return caverTransactionService.getTransaction(transactionHash)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }
}
