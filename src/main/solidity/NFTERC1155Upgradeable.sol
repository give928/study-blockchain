// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

import "@openzeppelin/contracts-upgradeable/access/OwnableUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/token/ERC1155/extensions/ERC1155URIStorageUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/token/ERC1155/extensions/ERC1155BurnableUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/token/ERC1155/extensions/ERC1155PausableUpgradeable.sol";
import "@openzeppelin/contracts-upgradeable/utils/CountersUpgradeable.sol";

contract NFTERC1155Upgradeable is OwnableUpgradeable, ERC1155URIStorageUpgradeable, ERC1155BurnableUpgradeable, ERC1155PausableUpgradeable {
    using CountersUpgradeable for CountersUpgradeable.Counter;
    CountersUpgradeable.Counter private _tokenId;

    function initialize() public initializer {
        __ERC1155_init("NFTERC1155");
        __ERC1155URIStorage_init();
        __ERC1155Burnable_init();
        __ERC1155Pausable_init();
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
        _setURI(id, tokenURI);
    }

    // ERC1155BurnableUpgradeable
    function uri(uint256 tokenId) public view override (ERC1155Upgradeable, ERC1155URIStorageUpgradeable) returns (string memory) {
        return ERC1155URIStorageUpgradeable.uri(tokenId);
    }

    function burn(
        address account,
        uint256 id,
        uint256 value
    ) public virtual override (ERC1155BurnableUpgradeable) onlyOwner {
        ERC1155BurnableUpgradeable.burn(account, id, value);
    }

    function burnBatch(
        address account,
        uint256[] memory ids,
        uint256[] memory values
    ) public virtual override (ERC1155BurnableUpgradeable) onlyOwner {
        ERC1155BurnableUpgradeable.burnBatch(account, ids, values);
    }

    // ERC1155PausableUpgradeable
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
    ) internal virtual override(ERC1155Upgradeable, ERC1155PausableUpgradeable) {
        super._beforeTokenTransfer(operator, from, to, ids, amounts, data);
    }
}
