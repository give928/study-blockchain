package com.give928.blockchain.ethereum.api;

import com.give928.blockchain.common.request.ContractTokenTransactionRequest;
import com.give928.blockchain.common.response.*;
import com.give928.blockchain.ethereum.request.ERC721JsonRequest;
import com.give928.blockchain.ethereum.service.Web3jContractERC721Service;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigInteger;

@RequestMapping("/v1/ethereum/web3j/contracts/erc721")
@RestController
public class Web3jContractERC721Controller {
    private final Web3jContractERC721Service web3JERC721ServiceContract;

    public Web3jContractERC721Controller(Web3jContractERC721Service web3JERC721ServiceContract) {
        this.web3JERC721ServiceContract = web3JERC721ServiceContract;
    }

    @PostMapping("")
    public Mono<ResponseEntity<ContractDeploymentResponse>> deploy() {
        return web3JERC721ServiceContract.deploy()
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{contractAddress}")
    public Mono<ResponseEntity<ContractLoadingResponse>> load(@PathVariable String contractAddress) {
        return web3JERC721ServiceContract.load(contractAddress)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @PostMapping("/{contractAddress}")
    public Mono<ResponseEntity<ContractMintingResponse>> mint(@PathVariable String contractAddress,
                                                              @RequestBody ERC721JsonRequest erc721JsonRequest) {
        return web3JERC721ServiceContract.mint(contractAddress, erc721JsonRequest)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/transactions/{transactionHash}")
    public Mono<ResponseEntity<ContractTransactionResponse>> getTransaction(@PathVariable String transactionHash) {
        return web3JERC721ServiceContract.getTransaction(transactionHash)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{contractAddress}/balances/{address}")
    public Mono<ResponseEntity<BalanceResponse>> balanceOf(@PathVariable String contractAddress,
                                                           @PathVariable String address) {
        return web3JERC721ServiceContract.balanceOf(contractAddress, address)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{contractAddress}/{tokenId}/owner")
    public Mono<ResponseEntity<OwnerResponse>> ownerOf(@PathVariable String contractAddress,
                                                       @PathVariable BigInteger tokenId) {
        return web3JERC721ServiceContract.ownerOf(contractAddress, tokenId)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{contractAddress}/{tokenId}/tokenURI")
    public Mono<ResponseEntity<String>> tokenURI(@PathVariable String contractAddress,
                                                 @PathVariable BigInteger tokenId) {
        return web3JERC721ServiceContract.tokenURI(contractAddress, tokenId)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @PatchMapping("/{contractAddress}/{tokenId}")
    public Mono<ResponseEntity<ContractTransactionResponse>> safeTransferFrom(@PathVariable String contractAddress,
                                                                              @PathVariable BigInteger tokenId,
                                                                              @RequestBody ContractTokenTransactionRequest contractTokenTransactionRequest) {
        return web3JERC721ServiceContract.safeTransferFrom(contractAddress, tokenId, contractTokenTransactionRequest)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @GetMapping("/{contractAddress}/paused")
    public Mono<ResponseEntity<Boolean>> paused(@PathVariable String contractAddress) {
        return web3JERC721ServiceContract.paused(contractAddress)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @DeleteMapping("/{contractAddress}/{tokenId}")
    public Mono<ResponseEntity<ContractTransactionResponse>> burn(@PathVariable String contractAddress,
                                                                              @PathVariable BigInteger tokenId) {
        return web3JERC721ServiceContract.burn(contractAddress, tokenId)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @PostMapping("/{contractAddress}/pause")
    public Mono<ResponseEntity<ContractTransactionResponse>> pause(@PathVariable String contractAddress) {
        return web3JERC721ServiceContract.pause(contractAddress)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }

    @PostMapping("/{contractAddress}/unpause")
    public Mono<ResponseEntity<ContractTransactionResponse>> unpause(@PathVariable String contractAddress) {
        return web3JERC721ServiceContract.unpause(contractAddress)
                .map(result -> ResponseEntity.ok()
                        .body(result));
    }
}
