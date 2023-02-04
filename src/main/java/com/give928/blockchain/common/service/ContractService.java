package com.give928.blockchain.common.service;

import com.give928.blockchain.common.response.ContractDeploymentResponse;
import com.give928.blockchain.common.response.ContractLoadingResponse;
import reactor.core.publisher.Mono;

public interface ContractService {
    Mono<ContractDeploymentResponse> deploy();

    Mono<ContractLoadingResponse> load(String contractAddress);
}
