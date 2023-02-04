package com.give928.blockchain.ethereum.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Configuration
@Slf4j
public class Web3jConfig {
    @Value("${ethereum.client-url}")
    private String clientUrl;

    // @formatter:off
    @Bean
    public Web3j web3j() {
        Web3j web3j = Web3j.build(new HttpService(clientUrl));
        web3j.web3ClientVersion()
                .sendAsync()
                .thenAccept(version -> log.info("Connected to Ethereum client web3j url: {} version: {}", clientUrl, version.getWeb3ClientVersion()));
        return web3j;
    }
    // @formatter:on
}
