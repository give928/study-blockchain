package com.give928.blockchain.klaytn.config;

import com.klaytn.caver.Caver;
import okhttp3.Credentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.http.HttpService;

@Configuration
public class CaverConfig {
    @Value("${caver.node-api-url}")
    private String nodeApiUrl;

    @Value("${caver.access-key-id}")
    private String accessKeyId;

    @Value("${caver.secret-access-key}")
    private String secretAccessKey;

    @Value("${caver.chain-id}")
    private String chainId;

    @Bean
    public Caver caver() {
        HttpService httpService = new HttpService(nodeApiUrl);
        httpService.addHeader("Authorization", Credentials.basic(accessKeyId, secretAccessKey));
        httpService.addHeader("x-chain-id", chainId);
        return new Caver(httpService);
    }
}
