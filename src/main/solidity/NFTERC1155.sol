// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts/access/Ownable.sol";
import "@openzeppelin/contracts/token/ERC1155/extensions/ERC1155URIStorage.sol";
import "@openzeppelin/contracts/token/ERC1155/extensions/ERC1155Burnable.sol";
import "@openzeppelin/contracts/token/ERC1155/extensions/ERC1155Pausable.sol";
import "@openzeppelin/contracts/utils/Counters.sol";

contract NFTERC1155 is Ownable, ERC1155URIStorage, ERC1155Burnable, ERC1155Pausable {
    using Counters for Counters.Counter;
    Counters.Counter private _tokenId;

    constructor() ERC1155("") {
    }

    function mint(
        address to,
        uint256 amount,
        bytes memory data,
        string memory tokenURI
    ) public onlyOwner {
        _tokenId.increment();

        uint256 id = _tokenId.current();
        _mint(to, id, amount, data);
        ERC1155URIStorage._setURI(id, tokenURI);
    }

    // ERC1155Burnable
    function uri(uint256 tokenId) public view override (ERC1155, ERC1155URIStorage) returns (string memory) {
        return ERC1155URIStorage.uri(tokenId);
    }

    function burn(
        address account,
        uint256 id,
        uint256 value
    ) public virtual override (ERC1155Burnable) onlyOwner {
        ERC1155Burnable.burn(account, id, value);
    }

    function burnBatch(
        address account,
        uint256[] memory ids,
        uint256[] memory values
    ) public virtual override (ERC1155Burnable) onlyOwner {
        ERC1155Burnable.burnBatch(account, ids, values);
    }

    // ERC1155Pausable
    function pause() public virtual onlyOwner {
        _pause();
    }

    function unpause() public virtual onlyOwner {
        _unpause();
    }

    function _beforeTokenTransfer(
        address operator,
        address from,
        address to,
        uint256[] memory ids,
        uint256[] memory amounts,
        bytes memory data
    ) internal virtual override(ERC1155, ERC1155Pausable) {
        super._beforeTokenTransfer(operator, from, to, ids, amounts, data);
    }
}
