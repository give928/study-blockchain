package com.give928.blockchain.klaytn.api;

import com.give928.blockchain.klaytn.response.KlayResponse;
import com.give928.blockchain.klaytn.response.KlaytnWalletResponse;
import com.give928.blockchain.klaytn.response.PebResponse;
import com.give928.blockchain.klaytn.service.CaverWalletService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequestMapping("/v1/klaytn/caver/wallets")
@RestController
public class CaverWalletController {
    private final CaverWalletService caverWalletService;

    public CaverWalletController(CaverWalletService caverWalletService) {
        this.caverWalletService = caverWalletService;
    }

    @PostMapping("")
    public Mono<ResponseEntity<KlaytnWalletResponse>> createWallet(@RequestBody String password) {
        return caverWalletService.createWallet(password)
                .map(walletResponse -> ResponseEntity.ok()
                        .body(walletResponse));
    }

    @GetMapping("/{address}/peb")
    public Mono<ResponseEntity<PebResponse>> getBalance(@PathVariable String address) {
        return caverWalletService.getBalance(address)
                .map(peb -> ResponseEntity.ok()
                        .body(new PebResponse(peb)));
    }

    @GetMapping("/{address}/klay")
    public Mono<ResponseEntity<KlayResponse>> getCoin(@PathVariable String address) {
        return caverWalletService.getCoin(address)
                .map(klay -> ResponseEntity.ok()
                        .body(new KlayResponse(klay)));
    }
}
