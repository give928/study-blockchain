package com.give928.blockchain.common.response;

public record ContractLoadingResponse(String contractAddress, String contractBinary, boolean valid) {
}
