package com.give928.blockchain.ethereum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.give928.blockchain.common.domain.TransactionType;
import com.give928.blockchain.common.request.ContractTokenTransactionRequest;
import com.give928.blockchain.common.response.*;
import com.give928.blockchain.common.service.ContractService;
import com.give928.blockchain.ethereum.domain.Web3jTransactionHelper;
import com.give928.blockchain.ethereum.request.ERC721JsonRequest;
import com.give928.blockchain.ethereum.web3j.contract.NFTERC721;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.*;
import org.web3j.tx.FastRawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;

@Service
@Slf4j
public class Web3jContractERC721Service implements ContractService {
    private static final String TOKEN_URI_PREFIX = "data:application/json;base64,";
    private static final String TOKEN_URI_FORMAT = TOKEN_URI_PREFIX + "%s";

    private final Web3j web3j;
    private final Web3jCommonService web3JCommonService;
    private final Web3jTransactionService web3jTransactionService;
    private final Web3jContractTransactionResponseMapper contractTransactionResponseMapper;
    private final ObjectMapper objectMapper;

    @Value("${ethereum.private-key}")
    private String privateKey;

    public Web3jContractERC721Service(Web3j web3j, Web3jCommonService web3JCommonService,
                                      Web3jTransactionService web3jTransactionService,
                                      Web3jContractTransactionResponseMapper contractTransactionResponseMapper,
                                      ObjectMapper objectMapper) {
        this.web3j = web3j;
        this.web3JCommonService = web3JCommonService;
        this.web3jTransactionService = web3jTransactionService;
        this.contractTransactionResponseMapper = contractTransactionResponseMapper;
        this.objectMapper = objectMapper;
    }

    // @formatter:off
    @Override
    public Mono<ContractDeploymentResponse> deploy() {
        return web3JCommonService.newPollingTransactionHelper(privateKey)
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(this::deploy)
                .doOnNext(nfterc721 -> log.info("contract address: {}", nfterc721.getContractAddress()))
                .map(nfterc721 -> new ContractDeploymentResponse(nfterc721.getContractAddress()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    // @formatter:off
    private Mono<NFTERC721> deploy(Web3jTransactionHelper web3jTransactionHelper) {
        FastRawTransactionManager fastRawTransactionManager = web3jTransactionHelper.getFastRawTransactionManager();
        StaticGasProvider staticGasProvider = web3jTransactionHelper.getStaticGasProvider();

        log.info("deploy contract. from: {}, nonce: {}, gasPrice: {}, gasLimit: {}",
                 fastRawTransactionManager.getFromAddress(), fastRawTransactionManager.getCurrentNonce(), staticGasProvider.getGasPrice(), staticGasProvider.getGasLimit());

        return Mono.from(NFTERC721.deploy(web3j, fastRawTransactionManager, staticGasProvider)
                                 .flowable());
    }
    // @formatter:on

    // @formatter:off
    @Override
    public Mono<ContractLoadingResponse> load(String contractAddress) {
        return loadViewNFTERC721(contractAddress)
                .map(nfterc721 -> {
                    try {
                        return new ContractLoadingResponse(nfterc721.getContractAddress(), nfterc721.getContractBinary(),
                                                           nfterc721.isValid());
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                });
    }
    // @formatter:on

    private Mono<NFTERC721> loadViewNFTERC721(String contractAddress) {
        return web3JCommonService.newDefaultTransactionHelper(privateKey)
                .subscribeOn(Schedulers.boundedElastic())
                .map(web3jTransactionHelper -> loadNFTERC721(contractAddress, web3jTransactionHelper));
    }

    private NFTERC721 loadNFTERC721(String contractAddress, Web3jTransactionHelper web3jTransactionHelper) {
        FastRawTransactionManager fastRawTransactionManager = web3jTransactionHelper.getFastRawTransactionManager();
        StaticGasProvider staticGasProvider = web3jTransactionHelper.getStaticGasProvider();
        return NFTERC721.load(contractAddress, web3j, fastRawTransactionManager, staticGasProvider);
    }

    // @formatter:off
    public Mono<ContractMintingResponse> mint(String contractAddress, ERC721JsonRequest erc721JsonRequest) {
        return loadTransactionNFTERC721(contractAddress)
                .flatMap(nfterc721 -> mint(nfterc721, erc721JsonRequest))
                .doOnNext(transactionReceipt -> log.info("mint transaction hash: {}", transactionReceipt.getTransactionHash()))
                .doOnNext(transactionReceipt -> log.info("mint transaction receipt: {}", transactionReceipt.toString()))
                .map(transactionReceipt -> new ContractMintingResponse(contractAddress, transactionReceipt.getTransactionHash()))
                .onErrorResume(throwable -> Mono.defer(() -> Mono.error(throwable)));
    }
    // @formatter:on

    private Mono<NFTERC721> loadTransactionNFTERC721(String contractAddress) {
        return web3JCommonService.newNoOpTransactionHelper(privateKey)
                .map(web3jTransactionHelper -> loadNFTERC721(contractAddress, web3jTransactionHelper));
    }

    private Mono<TransactionReceipt> mint(NFTERC721 nfterc721, ERC721JsonRequest erc721JsonRequest) {
        try {
            String json = objectMapper.writeValueAsString(erc721JsonRequest);
            String encodingJson = Base64.getEncoder()
                    .encodeToString(json.getBytes());
            log.info("mint nft. contractAddress: {}, data: {}, encoding json: {}", nfterc721.getContractAddress(), json,
                     encodingJson);
            return Mono.from(nfterc721.mint(String.format(TOKEN_URI_FORMAT, encodingJson))
                                     .flowable());
        } catch (JsonProcessingException e) {
            return Mono.error(new IllegalArgumentException(e));
        }
    }

    public Mono<ContractTransactionResponse> getTransaction(String transactionHash) {
        return web3jTransactionService.getEthTransactionAndReceiptAndBlock(transactionHash)
                .map(tuple -> toContractTransactionResponse(transactionHash, tuple.getT1(), tuple.getT2(),
                                                            tuple.getT3(), tuple.getT4().orElse(null)));
    }

    private ContractTransactionResponse toContractTransactionResponse(String transactionHash,
                                                                      EthTransaction ethTransaction,
                                                                      EthGetTransactionReceipt ethGetTransactionReceipt,
                                                                      EthBlock ethBlock,
                                                                      String errorMessage) {
        // 거래가 조회되지 않음(Dropped and Replaced)
        Optional<Transaction> transactionOptional = ethTransaction.getTransaction();
        if (ethTransaction.hasError() || transactionOptional.isEmpty()) {
            return contractTransactionResponseMapper.mapTransaction(TransactionType.TRANSACTION, transactionHash);
        }
        Transaction transaction = transactionOptional.get();

        // 거래 영수증이 조회되지 않음(Pending or Indexing, 아직 거래가 처리되지 않음)
        Optional<TransactionReceipt> transactionReceiptOptional = ethGetTransactionReceipt.getTransactionReceipt();
        if (ethGetTransactionReceipt.hasError() || transactionReceiptOptional.isEmpty()) {
            return contractTransactionResponseMapper.mapTransaction(TransactionType.TRANSACTION, transaction);
        }

        // Success or Failed, 블록 정보를 조회해서 블록의 timestamp 를 거래 시각으로 설정
        return contractTransactionResponseMapper.mapTransaction(TransactionType.TRANSACTION, transaction,
                                                                transactionReceiptOptional.get(), ethBlock.getBlock(), errorMessage);
    }

    public Mono<BalanceResponse> balanceOf(String contractAddress, String address) {
        return loadViewNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.balanceOf(address)
                                                        .flowable()))
                .map(BalanceResponse::new);
    }

    public Mono<OwnerResponse> ownerOf(String contractAddress, BigInteger tokenId) {
        return loadViewNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.ownerOf(tokenId)
                                                        .flowable()))
                .map(OwnerResponse::new);
    }

    public Mono<String> tokenURI(String contractAddress, BigInteger tokenId) {
        return loadViewNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.tokenURI(tokenId)
                                                        .flowable()))
                .map(tokenURI -> new String(Base64.getDecoder()
                                                    .decode(tokenURI.replace(TOKEN_URI_PREFIX, "")),
                                            StandardCharsets.UTF_8));
    }

    public Mono<ContractTransactionResponse> safeTransferFrom(String contractAddress, BigInteger tokenId,
                                                              ContractTokenTransactionRequest contractTokenTransactionRequest) {
        return loadTransactionNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(
                        nfterc721.safeTransferFrom(contractTokenTransactionRequest.getFrom(),
                                                   contractTokenTransactionRequest.getTo(),
                                                   tokenId)
                                .flowable()))
                .doOnNext(transactionReceipt -> log.info("safe transfer token transaction receipt: {}",
                                                         transactionReceipt.toString()))
                .map(transactionReceipt -> contractTransactionResponseMapper.mapTransaction(TransactionType.TRANSACTION,
                                                                                            transactionReceipt.getTransactionHash()));
    }

    public Mono<Boolean> paused(String contractAddress) {
        return loadViewNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.paused()
                                                        .flowable()));
    }

    public Mono<ContractTransactionResponse> burn(String contractAddress, BigInteger tokenId) {
        return loadTransactionNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.burn(tokenId)
                                                        .flowable()))
                .doOnNext(transactionReceipt -> log.info("burn token transaction receipt: {}", transactionReceipt.toString()))
                .map(transactionReceipt -> contractTransactionResponseMapper.mapTransaction(TransactionType.BURN,
                                                                                            transactionReceipt.getTransactionHash()));

    }

    public Mono<ContractTransactionResponse> pause(String contractAddress) {
        return loadTransactionNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.pause()
                                                        .flowable()))
                .doOnNext(transactionReceipt -> log.info("pause contract transaction receipt: {}", transactionReceipt.toString()))
                .map(transactionReceipt -> contractTransactionResponseMapper.mapTransaction(TransactionType.PAUSE,
                                                                                            transactionReceipt.getTransactionHash()));
    }

    public Mono<ContractTransactionResponse> unpause(String contractAddress) {
        return loadTransactionNFTERC721(contractAddress)
                .flatMap(nfterc721 -> Mono.from(nfterc721.unpause()
                                                        .flowable()))
                .doOnNext(transactionReceipt -> log.info("unpause contract transaction receipt: {}", transactionReceipt.toString()))
                .map(transactionReceipt -> contractTransactionResponseMapper.mapTransaction(TransactionType.UNPAUSE,
                                                                                            transactionReceipt.getTransactionHash()));
    }
}
