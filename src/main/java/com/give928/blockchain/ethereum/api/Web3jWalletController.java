package com.give928.blockchain.ethereum.api;

import com.give928.blockchain.common.response.WalletResponse;
import com.give928.blockchain.ethereum.response.EthResponse;
import com.give928.blockchain.ethereum.response.WeiResponse;
import com.give928.blockchain.ethereum.service.Web3jWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/ethereum/web3j/wallets")
@RestController
public class Web3jWalletController {
    private final Web3jWalletService web3jWalletService;

    public Web3jWalletController(Web3jWalletService web3jWalletService) {
        this.web3jWalletService = web3jWalletService;
    }

    @PostMapping("")
    public Mono<ResponseEntity<WalletResponse>> createWallet(@RequestBody String password) {
        return web3jWalletService.createWallet(password)
                .map(walletResponse -> ResponseEntity.ok()
                        .body(walletResponse));
    }

    @GetMapping("/{address}/wei")
    public Mono<ResponseEntity<WeiResponse>> getBalance(@PathVariable String address) {
        return web3jWalletService.getBalance(address)
                .map(wei -> ResponseEntity.ok()
                        .body(new WeiResponse(wei)));
    }

    @GetMapping("/{address}/eth")
    public Mono<ResponseEntity<EthResponse>> getCoin(@PathVariable String address) {
        return web3jWalletService.getCoin(address)
                .map(eth -> ResponseEntity.ok()
                        .body(new EthResponse(eth)));
    }
}
