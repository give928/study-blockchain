// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/token/ERC721/extensions/ERC721URIStorage.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Burnable.sol";
import "@openzeppelin/contracts/token/ERC721/extensions/ERC721Pausable.sol";
import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract NFTERC721 is Ownable, ERC721URIStorage, ERC721Burnable, ERC721Pausable {
    using Counters for Counters.Counter;
    Counters.Counter private _tokenId;

    constructor() ERC721("NFTERC721Tutorial", "NFTERC721") {
    }

    function mint(string memory uri) public onlyOwner returns (uint256) {
        _tokenId.increment();

        uint256 id = _tokenId.current();
        _safeMint(msg.sender, id);
        ERC721URIStorage._setTokenURI(id, uri);

        return id;
    }

    function tokenURI(uint256 tokenId) public view override(ERC721, ERC721URIStorage) returns (string memory) {
        return ERC721URIStorage.tokenURI(tokenId);
    }

    // ERC721Burnable
    function _burn(uint256 tokenId) internal override(ERC721, ERC721URIStorage) {
        super._burn(tokenId);
    }

    // ERC721Pausable
    function pause() public onlyOwner {
        _pause();
    }

    function unpause() public onlyOwner {
        _unpause();
    }

    function _beforeTokenTransfer(
        address from,
        address to,
        uint256 firstTokenId,
        uint256 batchSize)
    internal override(ERC721, ERC721Pausable) {
        super._beforeTokenTransfer(from, to, firstTokenId, batchSize);
    }
}
