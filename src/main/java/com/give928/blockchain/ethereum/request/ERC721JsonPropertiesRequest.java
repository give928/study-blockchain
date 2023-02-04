package com.give928.blockchain.ethereum.request;

public record ERC721JsonPropertiesRequest(ERC721JsonPropertyRequest name, ERC721JsonPropertyRequest description,
                                          ERC721JsonPropertyRequest image) {
}
