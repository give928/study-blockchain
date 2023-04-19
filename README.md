# 블록체인 스터디

## 환경
- openjdk 17
- gradle 7.6
- spring boot 3.0.2
- webflux
- 이더리움
  - web3j 4.9.6
    - JSON-RPC 로 이더리움 네트워크의 클라이언트와 통신하는 라이브러리
    - https://docs.web3j.io/4.8.7/
      - 4.9.6 문서는 안보임
  - webclient 로 JSON-RPC 통신
    - https://ethereum.org/en/developers/docs/apis/json-rpc/
  - testnet
    - Goerli
      - https://eth-goerli.g.alchemy.com/v2/demo
    - **[Sepolia](https://sepolia.dev/)**
      - 특징
        - Network/Chain ID: 11155111
        - Genesis Hash: 0x25a5cc106..8993e6dd9
        - Consensus Engine: Proof of Stake
        - EVM Version: London
        - web3_clientVersion: Geth/v1.10.21-unstable-926b3e08-20220706/linux-amd64/go1.18.1
    - 다른 테스트넷 확인 https://ethereum.org/en/developers/docs/networks/#ethereum-testnets
    - private network 구성
      - 메인넷과 유사한 환경인 Sepolia 테스트넷에서 학습한다. 직접 구성하는 방법은 대략적인 내용만 알고 넘어간다.
      <details>
      <summary>접기/펼치기</summary>
      자세한 내용은 https://docs.web3j.io/4.8.7/getting_started/run_node_locally/ 에서 확인

      - [Geth](https://geth.ethereum.org/)
        - Mac
          ```shell
          $ brew install golang

          $ brew tap ethereum/ethereum
          $ brew install ethereum

          $ geth version
          Geth
          Version: 1.10.26-stable
          Architecture: arm64
          Go Version: go1.19.3
          Operating System: darwin
          GOPATH=/Users/joohokim/go
          GOROOT=

          $ geth account new --datadir ./data
          INFO [01-26|13:38:11.813] Maximum peer count                       ETH=50 LES=0 total=50
          Your new account is locked with a password. Please give a password. Do not forget this password.
          Password: test1234
          Repeat password: test1234
      
          Your new key was generated
      
          Public address of the key:   0x05BED210a9d2ae37eA252EB51317199811A29E80
          Path of the secret key file: data/keystore/UTC--2023-01-26T04-38-24.817721000Z--05bed210a9d2ae37ea252eb51317199811a29e80
      
          - You can share your public address with anyone. Others need it to interact with you.
          - You must NEVER share the secret key with anyone! The key controls access to your funds!
          - You must BACKUP your key file! Without the key, it's impossible to access account funds!
          - You must REMEMBER your password! Without the password, it's impossible to decrypt the key!
          ```
          geth/genesis.json
          ```json
          {
            "config": {
              "chainId": 1234,
              "homesteadBlock": 0,
              "eip150Block": 0,
              "eip150Hash": "0x0000000000000000000000000000000000000000000000000000000000000000",
              "eip155Block": 0,
              "eip158Block": 0,
              "byzantiumBlock": 0,
              "constantinopleBlock": 0
            },
            "alloc"      : {
              "0x05BED210a9d2ae37eA252EB51317199811A29E80": {
                "balance": "120000000000000000000000"
              }
            },
            "coinbase"   : "0x0000000000000000000000000000000000000000",
            "difficulty" : "0x4000",
            "extraData"  : "",
            "gasLimit"   : "0x80000000",
            "nonce"      : "0x0000000000000042",
            "mixhash"    : "0x0000000000000000000000000000000000000000000000000000000000000000",
            "parentHash" : "0x0000000000000000000000000000000000000000000000000000000000000000",
            "timestamp"  : "0x00"
          }
          ```
          ```shell
          $ geth --datadir ./data init ./genesis.json
          $ geth --networkid 1234 --http --http.port 8545 --http.corsdomain "*" --datadir ./data --port 30303 --nodiscover --http.api personal,eth,net,web3,txpool,miner console
          > eth.accounts
          ["0x05bed210a9d2ae37ea252eb51317199811a29e80"]
          > eth.getBalance("0x05bed210a9d2ae37ea252eb51317199811a29e80")
          1.2e+23
          ```
          기타 설정이 필요한 경우 [Geth 공식 홈페이지](https://geth.ethereum.org/) 참고
        - Hyperledger Besu
        - OpenEthereum
      </details>
  - mainnet
    - [Infura](https://www.infura.io/)
      ```java
      Web3j web3 = Web3j.build(new HttpService("https://mainnet.infura.io/v3/<project key>"));
      ```
  - node 설치
    ```shell
    $ brew install nvm
    $ nvm install 16.19.0
    $ node -v
    v16.19.0
    ```
  - solidity 설치
    - npm, docker 등 다른 방법도 있다. https://docs.soliditylang.org/en/v0.8.17/installing-solidity.html 참고
    - 0.5.6 부터 smart contract 지원
    ```shell
    $ brew update
    $ brew upgrade
    $ brew tap ethereum/ethereum
    $ brew install solidity
    ```
  - Java Wrapper 생성
    - gradle plugin 으로 생성
      - solc, truffle, maven 도 가능(https://kauri.io/#communities/Java%20Ethereum/generate-a-java-wrapper-from-your-smart-contract/ 참고)
      - node.js dependencies 설치
        ```shell
        $ ./gradlew installNodeDependencies
        ```
      - Smart Contract Java Wrapper 클래스 생성
        - build/contract/main/java/.../contract/*.java 파일이 생성된다.
          - IntelliJ 에서 Gradle > study-blockchain > Tasks > web3j > generateContractWrappers 를 실행해도 된다.
        ```shell
        $ ./gradlew generateContractWrappers
        ```
  - IPFS(InterPlanetary File System)
    - P2P 파일 시스템
    - pdf, 이미지 등을 저장하면 될 것 같다. 아직은 사용하지 않음. 나중에 학습.
  - NFT
    - ERC721 기준으로 코드 작성
    - ERC1155, ERC1155Upgradeable sol 파일 작성. 나중에 학습.
- 클레이튼
  - caver-java 1.10.0
    - https://ko.docs.klaytn.foundation/content/dapp/sdk/caver-java
  - JSON-RPC API
    - https://ko.docs.klaytn.foundation/content/dapp/json-rpc
  - testnet
    - Baobab
      - chain-id: 1001
  - mainnet
    - Cypress
      - chain-id: 8217

### 참고
- 학습 목적이라 개인키를 직접 사용하고 있다. 운영 환경에서는 환경 변수에서 불러오거나 암호화해서 사용해야 한다.
- Contract 생성은 주소를 얻기 위해 동기 처리한다.
- 나머지는 비동기 처리로 거래 해시만 즉시 반환한다.(조회가 아닌 경우 처리에 시간이 걸린다.)
- Web3j 에 의존해서 통신만 하기 때문에 테스트 코드를 작성하지 않았다.
  - mock 객체로 테스트할 수 있겠지만, 핑계를 대자면 api 를 호출해서 dto 로 반환만 한다.
  - 외부에서 코인을 획득해야 진행이 가능해서 처음부터 끝까지 시나리오 테스트를 할 수 없을 것 같다.
  - 실제 개발을 하게 된다면 도메인 클래스도 많이 생길 것이고 단위 테스트, 인수 테스트를 꼭 작성해야겠다.

## 이더리움

### Web3j

#### 지갑
- 지갑 생성
  <details>
  <summary>생성 방법 접기/펼치기</summary>

    - **파일 없이 생성**
      ```java
      Wallet.createStandard(password, Keys.createEcKeyPair());
      ```
    - 지갑을 생성하고 파일로 저장
      ```java
      String walletPassword = "secr3t";
      String walletDirectory = "/path/to/destination/";

      String walletName = WalletUtils.generateNewWalletFile(walletPassword, new File(walletDirectory)); // 내부에서 Wallet.createStandard 호출
      System.out.println("wallet location: " + walletDirectory + "/" + walletName);
      ```
  </details>

  ```shell
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/wallets" -d '{"password":"password1"}' -H "Content-Type: application/json"
  {"address":"0xed03d7b51465553babbf80715959a3b014cd3725","privateKey":"0x618ac58c4d0cebb313636633599361b3c812bf87afe63a50e25dbbe59fc70920"}
  ```
  - 생성된 지갑의 개인키를 application.yml 파일 ethereum.private-key 설정
    - 이후 진행할 Contract 에서 사용
    ```yaml
    ethereum:
      private-key: 0x618ac58c4d0cebb313636633599361b3c812bf87afe63a50e25dbbe59fc70920
    ```

- 지갑 잔액 확인
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/wallets/0xed03d7b51465553babbf80715959a3b014cd3725/wei"
  {"wei":0}
  ```

- Sepolia 수도꼭지에서 ETH 획득
  - https://ethereum.org/en/developers/docs/networks/#sepolia 참고
    - https://faucet.sepolia.dev/
      - TLS 문제로 연결 안됨
    - https://fauceth.komputing.org/
      - 테스트넷 목록에서 Sepolia 선택 안됨
  - 공식 가이드에서 제공하는 사이트에서 획득하지 못해서 구글링
    - https://sepolia-faucet.pk910.de/
      - 지갑 주소 입력하고 Start Mining 클릭
      - 채굴 화면에서 채굴되기를 조금 기다린다. 0.05 이하로 채굴하면 너무 적다고 한다. 0.05 이상 채굴하고 Stop Mining 클릭
      - Claim Mining Rewards 팝업이 열린다. Claim Rewards 클릭. 시간이 조금 걸린다.
      - 조금 기다리면 트랜잭션이 처리되고 트랜잭션 정보를 보여준다.
        https://sepolia.etherscan.io/tx/0x7a82300e904d45983fda7517bcceebe610b345eef01644156deb6d0ef518e8d8
      - 한번 채굴하면 10분 지나야 다시 시도할 수 있다.
    - https://www.allthatnode.com/faucet/ethereum.dsrv
      - Network: Ethereum : Sepolia Testnet 선택
      - Your Testnet Address: 지갑 주소 입력
      - Request Tokens 클릭
      - 조금 기다리면 트랜잭션이 처리되고 트랜잭션 정보를 보여준다.
        https://sepolia.etherscan.io/tx/0x9f03a58dcdabdc8ebadc53009ec2a757b0b72894288591d2c5fdfe73f0ead12a
      - 1일 1회만 요청 가능

- 지갑 확인
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/wallets/0xed03d7b51465553babbf80715959a3b014cd3725/wei"
  {"wei":25000000000000000}
  
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/wallets/0xed03d7b51465553babbf80715959a3b014cd3725/eth"
  {"eth":0.025}
  ```

#### 거래
- 거래 생성: 다른 지갑으로 코인(wei) 보내기
  - Specifying the Chain Id on Transactions (EIP-155)
    - London fork 부터 TransactionManager 에서 chain id 를 지정해야 한다.
    - 트랜잭션이 다른 체인으로 다시 브로드캐스트되는 것을 방지한다.
    - chain id 를 지정하지 않고 거래 시도할 때 예외 메시지: Error processing transaction request: only replay-protected (EIP-155) transactions allowed over RPC
  - 거래 상태
    - Pending
      - 트랜잭션이 메모리 풀에서 보내는 시간은 네트워크 정체(네트워크를 사용하는 사람 수)와 지불된 가스 요금이라는 두 가지 주요 요인에 따라 달라진다.
      - 당연히 가스 수수료가 높을수록 채굴자가 더 비싼 수수료를 처리하면 금전적 인센티브를 받게 되므로 더 빨리 블록에 포함될 가능성이 높아진다.
      - This txn hash was found in our secondary node and should be picked up by our indexer in a short while.
    - Indexing
      - This transaction has been included and will be reflected in a short while.
    - Success
    - Failed
      - Out of Gas, Reverted, Bad Instruction 의 이유로 실패한다.
      - 가스비의 일부가 소비된다.
    - Dropped and Replaced
      - 가스비가 너무 낮으면 노드에 브로드캐스트되지 않고 네트워크에서 삭제된다.
      - 거래금액과 가스비를 모두 돌려받는다.
      - 동일한 nonce 로 더 높은 가스비를 지불하는 다른 트랜잭션을 보내야 한다.
      - transactionHash 를 받더라도 거래가 삭제될 수 있다.
  - 거래 전송 방법
    - raw 트랜잭션에 서명하고 JSON-RPC를 통해 노드로 전송
      - eth_getTransactionCount 를 호출하여 발신자 계정에 대한 nonce 를 조회해서 직접 넣어줘야 함
        - 아직 블록에 포함되지 않은 보류 중인 트랜잭션을 포함하여 계정에서 보낸 총 트랜잭션 수
        - Dropped and Replaced 된 거래를 제외한 트랜잭션의 수 인것 같다.
    - TransactionManager, Transfer 클래스로 JSON-RPC를 통해 노드로 전송
      - RawTransactionManager 는 트랜잭션이 전송될 때마다 eth_getTransactionCount 를 호출
      - FastTransactionManager 는 메모리 내 트랜잭션 수를 유지하고 트랜잭션을 전송할 때마다 카운팅(시퀀스처럼 증가, nonce 생략 가능)
        - 카운트 관리는 동기화되므로 스레드로부터 안전
    - 한번에 여러 트랜잭션을 처리하려면 nonce 를 다르게 설정해야 한다.
      - pending 만 되고 indexing 되지 않은 거래는 eth_getTransactionCount 에서 카운팅되지 않았다.(nonce 에서 포함되지 않음)
      - 거래1이 indexing 되기전에 거래2를 요청하면 거래1과 동일한 nonce 에 거래2가 등록되고 거래1은 삭제되었다.
  ```shell
  # 받을 지갑 생성
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/wallets" -d '{"password":"password2"}' -H "Content-Type: application/json"
  {"address":"0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee","privateKey":"0x8d7cdefd6e756c4a88894972f3a1f247f26eb8aa53b6891449f61dca7cf882f5"}
  
  # 잔액 0
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/wallets/0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee/wei"
  {"wei":0}
  
  # 1 wei 보내기
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/transactions" -d '{"privateKey":"0x618ac58c4d0cebb313636633599361b3c812bf87afe63a50e25dbbe59fc70920", "to":"0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee", "value":1}' -H "Content-Type: application/json"
  {"transactionType":"TRANSACTION","transactionHash":"0xbbea4e2301c83e5dcf8f2063a470442bd229b0d35ba4020b6e3f9a1ea28c2656","status":null,"blockNumber":null,"timestamp":null,"from":null,"to":null,"value":null,"transactionFee":null,"gasPrice":null,"nonce":null,"errorMessage":null}
  
  # 잔액 1
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/wallets/0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee/wei"
  {"wei":1}
  ```

- 거래 조회
  - https://sepolia.etherscan.io/tx/0xbbea4e2301c83e5dcf8f2063a470442bd229b0d35ba4020b6e3f9a1ea28c2656 에서도 확인 가능
  - 동일하게 정보를 출력해주려고 하니 3번이나 조회가 필요하다.
    1. web3j.ethGetTransactionByHash(String transactionHash)
      - status, timestamp 를 제외한 정보는 여기서 확인 가능하다.
    2. web3j.ethGetTransactionReceipt(String transactionHash)
      - 여기서 status 만 사용한다.
    3. web3j.ethGetBlockByHash(String blockHash)
      - 구글링 해보니 블록의 timestamp 를 거래 시각으로 보는 것 같다.
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/transactions/0xbbea4e2301c83e5dcf8f2063a470442bd229b0d35ba4020b6e3f9a1ea28c2656"
  {"transactionType":"TRANSACTION","transactionHash":"0xbbea4e2301c83e5dcf8f2063a470442bd229b0d35ba4020b6e3f9a1ea28c2656","status":"SUCCESS","blockNumber":2794718,"timestamp":"2023-01-30T13:54:24","from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee","value":1,"transactionFee":52500000147000,"gasPrice":2500000007,"nonce":0,"errorMessage":null}
  ```

#### ERC-721
- Contract 생성(주소를 얻기 위해 동기 처리. 1분 미만 소요)
  ```shell
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c"}
  ```
- Contract 조회
  ```shell
  # ERC721NFT
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","contractBinary":"...","valid":true}
  ```
- NFT 생성
  - Metadata schemas https://nftschool.dev/reference/metadata-schemas/#intro-to-json-schemas
    <details>
    <summary>접기/펼치기</summary>

    - ERC-721
    ```json
    {
        "title": "Asset Metadata",
        "type": "object",
        "properties": {
            "name": {
                "type": "string",
                "description": "Identifies the asset to which this NFT represents"
            },
            "description": {
                "type": "string",
                "description": "Describes the asset to which this NFT represents"
            },
            "image": {
                "type": "string",
                "description": "A URI pointing to a resource with mime type image/* representing the asset to which this NFT represents. Consider making any images at a width between 320 and 1080 pixels and aspect ratio between 1.91:1 and 4:5 inclusive."
            }
        }
    }
    ```

    - ERC-1155
    ```json
    {
        "title": "Token Metadata",
        "type": "object",
        "properties": {
            "name": {
                "type": "string",
                "description": "Identifies the asset to which this token represents"
            },
            "decimals": {
                "type": "integer",
                "description": "The number of decimal places that the token amount should display - e.g. 18, means to divide the token amount by 1000000000000000000 to get its user representation."
            },
            "description": {
                "type": "string",
                "description": "Describes the asset to which this token represents"
            },
            "image": {
                "type": "string",
                "description": "A URI pointing to a resource with mime type image/* representing the asset to which this token represents. Consider making any images at a width between 320 and 1080 pixels and aspect ratio between 1.91:1 and 4:5 inclusive."
            },
            "properties": {
                "type": "object",
                "description": "Arbitrary properties. Values may be strings, numbers, object or arrays."
            }
        }
    }
    ```
    </details>
  ```shell
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c" -d '{"title": "ERC721 NFT Metadata1", "type": "object", "properties": {"name": {"type": "string", "description": "ERC721 NFT Metadata name1"}, "description": {"type": "string", "description": "ERC721 NFT Metadata description1"}, "image": {"type": null, "description": null}}}' -H "Content-Type: application/json"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","transactionHash":"0xcd6537207e23cd35bab12b37c7c95a087a5e08cda973526c40b22ffe63134e71"}
  ```
- NFT mint transaction 조회
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/transactions/0xcd6537207e23cd35bab12b37c7c95a087a5e08cda973526c40b22ffe63134e71"
  {"transactionType":"TRANSACTION","transactionHash":"0xcd6537207e23cd35bab12b37c7c95a087a5e08cda973526c40b22ffe63134e71","status":"SUCCESS","blockNumber":2830151,"timestamp":"2023-02-04T16:49:24","from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","tokens":[{"from":"0x0000000000000000000000000000000000000000","to":"0xed03d7b51465553babbf80715959a3b014cd3725","tokenId":1,"tokenType":"ERC721"}],"value":0,"transactionFee":947081522739492,"gasPrice":2420000007,"nonce":30,"errorMessage":null}
  ```
- 지갑 주소의 NFT 잔액 조회
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/balances/0xed03d7b51465553babbf80715959a3b014cd3725"
  {"balance":1}
  ```
- NFT 소유자 조회
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/1/owner"
  {"address":"0xed03d7b51465553babbf80715959a3b014cd3725"}
  ```
- NFT token uri 조회
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/1/tokenURI"
  {"title":"ERC721 NFT Metadata1","type":"object","properties":{"name":{"type":"string","description":"ERC721 NFT Metadata name1"},"description":{"type":"string","description":"ERC721 NFT Metadata description1"},"image":{"type":null,"description":null}}}
  ```
- NFT 소유권 전송(0xed03d7b51465553babbf80715959a3b014cd3725 -> 0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee)
  ```shell
  $ curl -X PATCH "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/1" -d '{"from": "0xed03d7b51465553babbf80715959a3b014cd3725", "to": "0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee"}' -H "Content-Type: application/json"
  {"transactionType":"TRANSACTION","transactionHash":"0x1ad2c95d71dfb2ad876e0e90b3e1a3215605a616c7ecd54f3f08dd421152839e"}

  # NFT 소유권 거래 조회
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/transactions/0x1ad2c95d71dfb2ad876e0e90b3e1a3215605a616c7ecd54f3f08dd421152839e"
  {"transactionType":"TRANSACTION","transactionHash":"0x1ad2c95d71dfb2ad876e0e90b3e1a3215605a616c7ecd54f3f08dd421152839e","status":"SUCCESS","blockNumber":2830167,"timestamp":"2023-02-04T16:52:36","from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","tokens":[{"from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee","tokenId":1,"tokenType":"ERC721"}],"value":0,"transactionFee":162087250000000,"gasPrice":2650000000,"nonce":31,"errorMessage":null}

  # NFT 소유자 조회(소유자 0xed03d7b51465553babbf80715959a3b014cd3725 -> 0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee 변경됨)
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/1/owner"
  {"address":"0xd689e6b55f603e8cfe1be7f2f0c84d82dec5d4ee"}
  ```
- NFT 소각
  ```shell
  # 2번 토큰 생성
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c" -d '{"title": "ERC721 NFT Metadata2", "type": "object", "properties": {"name": {"type": "string", "description": "ERC721 NFT Metadata name2"}, "description": {"type": "string", "description": "ERC721 NFT Metadata description2"}, "image": {"type": null, "description": null}}}' -H "Content-Type: application/json"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","transactionHash":"0x19685535887194f82c78547043a4e604e6ba18839049d1432be2038751c353b6"}

  # 2번 토큰 소각 -> _isApprovedOrOwner 만 소각 가능, 1번 토큰은 소유권을 넘겼으므로 소각할 수 없음
  $ curl -X DELETE "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/2"
  {"transactionType":"BURN","transactionHash":"0x27bc9ba2d0d05b19143e1e0685ae38e12ebc5259510a35e5641a1ff072345fc8"}

  # NFT 소유자 조회 -> 유효하지 않은 토큰 ID 예외 발생
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/2/owner"
  {"exception":"org.web3j.tx.exceptions.ContractCallException","message":"Contract Call has been reverted by the EVM with the reason: 'execution reverted: ERC721: invalid token ID'."}
  ```
- Contract 정지 및 정지 해제
  ```shell
  # 정지 상태 조회
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/paused"
  false
  
  # 정지
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/pause"
  {"transactionType":"PAUSE","transactionHash":"0x4883bcb47674d0f876e35e7cbba84fba95c839d77e3b04314bdbec98fffa80b7"}
  
  # 정지 상태 조회 -> 정지되어 있음
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/paused"
  true

  # 3번 토큰 생성 실패. TransactionReceipt, Block 의 logs, revertReason 등 에서 실패 사유를 확인할 수 없다.
  # 이더스캔은 에러 메시지가 나온다. "Fail with error 'ERC721Pausable: token transfer while paused'"
  # https://api.tenderly.co/api/v1/public-contract/{chainId}/tx/{txHash} 에서 조회할 수 있다. 한참 찾아도 다른 방법이 안보인다. 일단 webclient 로 받아왔다.
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c" -d '{"title": "ERC721 NFT Metadata3", "type": "object", "properties": {"name": {"type": "string", "description": "ERC721 NFT Metadata name3"}, "description": {"type": "string", "description": "ERC721 NFT Metadata description3"}, "image": {"type": null, "description": null}}}' -H "Content-Type: application/json"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","transactionHash":"0xb07a191323674e7021299ce5b2fa0e6567c265c92ac3afc21fc15cc6c5ac937a"}
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/transactions/0xb07a191323674e7021299ce5b2fa0e6567c265c92ac3afc21fc15cc6c5ac937a"
  {"transactionType":"TRANSACTION","transactionHash":"0xb07a191323674e7021299ce5b2fa0e6567c265c92ac3afc21fc15cc6c5ac937a","status":"FAIL","errorMessage":"ERC721Pausable: token transfer while paused","blockNumber":2830193,"timestamp":"2023-02-04T16:58:24","from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","tokens":[],"value":0,"transactionFee":120825000000000,"gasPrice":3000000000,"nonce":35}

  # 정지 해제
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/unpause"
  {"transactionType":"UNPAUSE","transactionHash":"0xf517b9e43a4bccd07bfc0ced81967f88972c75e3018c00a83c31de05dc312b5c"}

  # 정지 상태 조회 -> 해제되어 있음
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c/paused"
  false

  # 3번 토큰 생성 성공
  $ curl -X POST "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c" -d '{"title": "ERC721 NFT Metadata3", "type": "object", "properties": {"name": {"type": "string", "description": "ERC721 NFT Metadata name3"}, "description": {"type": "string", "description": "ERC721 NFT Metadata description3"}, "image": {"type": null, "description": null}}}' -H "Content-Type: application/json"
  {"contractAddress":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","transactionHash":"0x3ded0c14255cce3504104eee0ce4d6ddb3b9d1d8890b7ff44dcf77309d5b40f6"}
  $ curl -X GET "http://127.0.0.1:8080/v1/ethereum/web3j/contracts/erc721/transactions/0x3ded0c14255cce3504104eee0ce4d6ddb3b9d1d8890b7ff44dcf77309d5b40f6"
  {"transactionType":"TRANSACTION","transactionHash":"0x3ded0c14255cce3504104eee0ce4d6ddb3b9d1d8890b7ff44dcf77309d5b40f6","status":"SUCCESS","blockNumber":2831739,"timestamp":"2023-02-04T22:18:12","from":"0xed03d7b51465553babbf80715959a3b014cd3725","to":"0x1815d3d6b270f7bcd15ae57ba6c6317707e48b3c","tokens":[{"from":"0x0000000000000000000000000000000000000000","to":"0xed03d7b51465553babbf80715959a3b014cd3725","tokenId":3,"tokenType":"ERC721"}],"value":0,"transactionFee":935640002619792,"gasPrice":2500000007,"nonce":37,"errorMessage":""}
  ```

## 클레이튼

### Caver

- 공식 예제: https://github.com/klaytn/caver-java-examples

#### 지갑
- 지갑 생성
  ```shell
  $ curl -X POST "http://127.0.0.1:8080/v1/klaytn/caver/wallets" -d '{"password":"password1"}' -H "Content-Type: application/json"
  {"address":"0x4d6fa14deda3947217f268dd3fdb41f8cead4c77","privateKey":"0x66e94f18a0e5b0f3c3325388051449e974fe79a9a58b43826a2985b392a01468","publicKey":"0x0244919d4c64ee7973ce4c084ec389258d55912b71e27a5e92ba65119efcfb946d","klaytnWalletKey":"0x66e94f18a0e5b0f3c3325388051449e974fe79a9a58b43826a2985b392a014680x000x4d6fa14deda3947217f268dd3fdb41f8cead4c77"}
  ```
  - 생성된 지갑의 개인키를 application.yml 파일 klaytn.private-key 설정
    - 이후 진행할 Contract 에서 사용
    ```yaml
    klaytn:
      private-key: 0x66e94f18a0e5b0f3c3325388051449e974fe79a9a58b43826a2985b392a01468
    ```

- 지갑 잔액 확인
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/wallets/0x4d6fa14deda3947217f268dd3fdb41f8cead4c77/peb"
  {"peb":0}
  ```

- [KLAY Faucet](https://baobab.wallet.klaytn.foundation/faucet) 에서 계정 주소를 입력하고 클레이를 받는다.
  - 24시간 마다 150 클레이를 받을 수 있다.

- 지갑 확인
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/wallets/0x4d6fa14deda3947217f268dd3fdb41f8cead4c77/peb"
  {"peb":150000000000000000000}
  
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/wallets/0x4d6fa14deda3947217f268dd3fdb41f8cead4c77/klay"
  {"klay":150}
  ```

#### 거래
- 거래 생성: 다른 지갑으로 코인(peb) 보내기
  ```shell
  # 받을 지갑 생성
  $ curl -X POST "http://127.0.0.1:8080/v1/klaytn/caver/wallets" -d '{"password":"password2"}' -H "Content-Type: application/json"
  {"address":"0x792396164d09354ecf19e920bed3eab382d3a5b3","privateKey":"0xaade12a08a9fbb0665386f6c6491316bd66059aeeff9be29950d4b543f1b9ba5","publicKey":"0x0300cd00aebdfb2be78c071bbca7abf0968ad7954100f87f601964ce49cf00343c","klaytnWalletKey":"0xaade12a08a9fbb0665386f6c6491316bd66059aeeff9be29950d4b543f1b9ba50x000x792396164d09354ecf19e920bed3eab382d3a5b3"}
  
  # 잔액 0
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/wallets/0x792396164d09354ecf19e920bed3eab382d3a5b3/peb"
  {"peb":0}
  
  # 1 peb 보내기
  $ curl -X POST "http://127.0.0.1:8080/v1/klaytn/caver/transactions" -d '{"privateKey":"0x66e94f18a0e5b0f3c3325388051449e974fe79a9a58b43826a2985b392a01468", "to":"0x792396164d09354ecf19e920bed3eab382d3a5b3", "value":1}' -H "Content-Type: application/json"
  {"transactionType":"TRANSACTION","transactionHash":"0xddeab238f621cde9f00bd67f25c4bc1b665d704da5cd112f37f4a998ff93ca11","status":null,"blockNumber":null,"timestamp":null,"from":null,"to":null,"value":null,"transactionFee":null,"gasPrice":null,"nonce":null,"errorMessage":null}
  
  # 잔액 1
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/wallets/0x792396164d09354ecf19e920bed3eab382d3a5b3/peb"
  {"peb":1}
  ```

- 거래 조회
  - https://baobab.scope.klaytn.com/tx/0xddeab238f621cde9f00bd67f25c4bc1b665d704da5cd112f37f4a998ff93ca11 에서도 확인 가능
  - 동일하게 정보를 출력해주려고 하니 2번 조회가 필요하다.
    1. caver.rpc.klay.getTransactionReceipt(String transactionHash)
    - timestamp 제외한 모든 정보를 사용한다.
    2. caver.rpc.klay.getBlockByHash(String blockHash)
    - 구글링 해보니 블록의 timestamp 를 거래 시각으로 보는 것 같다.
  ```shell
  $ curl -X GET "http://127.0.0.1:8080/v1/klaytn/caver/transactions/0xddeab238f621cde9f00bd67f25c4bc1b665d704da5cd112f37f4a998ff93ca11"
  {"transactionType":"TRANSACTION","transactionHash":"0xddeab238f621cde9f00bd67f25c4bc1b665d704da5cd112f37f4a998ff93ca11","status":"SUCCESS","blockNumber":114012704,"timestamp":"2023-02-05T18:32:39","from":"0x4d6fa14deda3947217f268dd3fdb41f8cead4c77","to":"0x792396164d09354ecf19e920bed3eab382d3a5b3","value":1,"transactionFee":525000000000000,"gasPrice":50000000000,"nonce":0,"errorMessage":null}
  ```
