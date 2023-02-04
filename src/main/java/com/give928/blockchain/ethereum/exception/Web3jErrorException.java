package com.give928.blockchain.ethereum.exception;

import com.give928.blockchain.common.exception.ErrorException;
import lombok.Getter;
import org.web3j.protocol.core.Response;

@Getter
public class Web3jErrorException extends ErrorException {
    public Web3jErrorException(Response.Error error) {
        super(error.getCode(), error.getMessage(), error.getData());
    }
}
