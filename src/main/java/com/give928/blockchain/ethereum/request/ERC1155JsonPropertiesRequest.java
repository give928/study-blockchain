package com.give928.blockchain.ethereum.request;

public record ERC1155JsonPropertiesRequest(ERC1155JsonPropertyRequest name, ERC1155JsonPropertyRequest decimals,
                                           ERC1155JsonPropertyRequest description, ERC1155JsonPropertyRequest image,
                                           ERC1155JsonPropertyRequest properties) {
}
