package com.give928.blockchain.ethereum;

import io.reactivex.Flowable;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("이더리움 테스트넷 Sepolia 연결")
class SepoliaTest {
    private static final String SEPOLIA_URL = "https://rpc.sepolia.org/";
    private static Web3j web3j;

    @BeforeAll
    static void beforeAll() {
        // given
        web3j = Web3j.build(new HttpService(SEPOLIA_URL));
    }

    @DisplayName("클라이언트 동기 통신")
    @Test
    void getEthClientVersionSync() throws IOException {
        // when
        Web3ClientVersion web3ClientVersion = web3j.web3ClientVersion()
                .send();

        // then
        assertThat(web3ClientVersion.getWeb3ClientVersion()).isNotEmpty();
    }

    @DisplayName("클라이언트 비동기 통신")
    @Test
    void getEthClientVersionASync() {
        // when
        CompletableFuture<Web3ClientVersion> web3ClientVersionCompletableFuture = web3j.web3ClientVersion()
                .sendAsync();

        // then
        StepVerifier.create(Mono.fromFuture(web3ClientVersionCompletableFuture))
                .assertNext(web3ClientVersion -> assertThat(web3ClientVersion.getWeb3ClientVersion()).isNotEmpty())
                .verifyComplete();
    }

    @DisplayName("클라이언트 리액티브 통신")
    @Test
    void getEthClientVersionRx() {
        // when
        Flowable<Web3ClientVersion> web3ClientVersionFlowable = web3j.web3ClientVersion()
                .flowable();

        // then
        StepVerifier.create(web3ClientVersionFlowable)
                .assertNext(web3ClientVersion -> assertThat(web3ClientVersion.getWeb3ClientVersion()).isNotEmpty())
                .verifyComplete();
    }
}
