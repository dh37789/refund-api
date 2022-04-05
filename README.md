
# 삼쩜삼 백엔드 엔지니어 채용과제

## 1. API 구축문제

### 삼쩜삼은 유저의 환급액을 계산해 주는 기능이 있습니다.

- 사용자가 삼쩜삼에 가입해야 합니다.
- 가입한 유저의 정보를 스크랩하여 환급액이 있는지 조회합니다.
- 조회된 금액을 계산한 후 유저에게 실제 환급액을 알려줍니다.
- UI를 제외하고 간소화된 REST API만 구현하시면 됩니다.

## 기능 요구사항

- 과제 구현시 Java, Spring Boot, JPA, H2, Gradle을 빠짐없이 모두 활용합니다.
- DB는 H2 Embeded DB를 사용합니다.
- 회원가입, 환급액 계산, 유저 정보 조회 API를 구현합니다.
- 모든 요청에 대해 application/json타입으로 응답합니다.
- 각 기능 및 제약사항에 대한 단위 테스트를 작성합니다.
- swagger를 이용하여 API확인 및 API실행이 가능합니다.
- 민감정보 (주민등록번호, 비밀번호)등은 암호화된 상태로 저장합니다.
- README파일을 이용하여 요구사항 구현여부, 구현방법, 그리고 검증결과에 대해 작성 합니다.

## 개발 스택

- openJdk 11.0.1
- Spring Boot 2.6.5
- JPA
- H2 database
- Gradle
- Redis
- JUnit5

## API 명세서

## Swagger

http://localhost:8080/swagger-ui/index.html

## Redis

내장 Redis가 구현되어 있지만, Docker-compose를 통해 Redis 서버 컨테이너를 띄울 수 있습니다.  
Redis 서버를 띄울 경우 프로젝트 profile을 dev로 설정해야 합니다. 

```shell
# redis 서버 실행
docker-compose -f ./deploy/docker-compose.yml up -d
# redis 클라이언트 실행
docker exec -it redis_boot redis-cli
```

#### 공통 HttpStatus

| 상태코드  | 설명                |
|-------|-------------------|
| `200` | 정상                |
| `201` | 정상적으로 생성          |
| `400` | 유효하지 않은 요청        |
| `404` | 요청한 바를 찾을 수 없습니다. |
| `500` | 시스템 에러            |

#### 응답 코드

| 응답코드 | 설명                | 상태코드 |
| -------- |-------------------|-----------|
| `E000` | 성공                | `200`     |
| `E001` | API 요청 완료          | `200`     |
| `E002`    | 1분뒤 다시 시도해주세요.        | `200`     |
| `E101`    | 이미 가입한 회원입니다. | `400`     |
| `E102`    | 사용중인 ID 입니다.            | `400`     |
| `E103`    | 옳지 않은 주민등록번호입니다.            | `400`     |
| `E104`    | 존재 하지 않는 회원입니다.            | `404`     |
| `E105`    | 맞지 않는 비밀번호 입니다.            | `400`     |
| `E106`    | 옳지 않은 파라미터입니다.            | `400`     |
| `E201`    | 인증토큰 에러입니다.            | `401`     |
| `E202`    | 만료된 토큰입니다.            | `401`     |
| `E203`    | 인증 토큰이 없습니다.            | `401`     |
| `E204`    | 토큰이 옳바른 형식이 아닙니다.            | `401`     |
| `E301`    | 급여내역 정보를 찾을 수 없습니다.            | `404`     |
| `E302`    | 산출세액 정보를 찾을 수 없습니다.            | `404`     |
| `E303`    | 유저의 급여정보를 찾을 수 없습니다.            | `404`     |
| `E304`    | 유저 정보가 없습니다.            | `400`     |
| `E401`    | 유저정보를 수집중 입니다.            | `400`     |
| `E501`    | 올바르지 않은 HTTP 호출 방식 입니다.            | `404`     |
| `E502`    | 올바르지 않은 API 입니다.            | `404`     |

#### 공통 헤더

> Content-Type : application/json

#### 공통 응답

```json
{
    "success": false,
    "code": "E101",
    "msg": "이미 가입한 회원입니다.",
    "status": 400
}
```
---
### 회원가입

### REQUEST

> POST /szs/signup

| 파라미터      |   타입   | 설명               |   필수   |
|:----------|:------:|:-----------------|:------:|
| userId    | String | 회원가입시 요청할 유저ID   |   ○    |
| password  |    String    | 회원가입시 요청할 비밀번호   |   ○    |
| name      |    String    | 회원가입시 요청할 이름     |   ○    |
| regNo     |    String    | 회원가입시 요청할 주민등록번호 |   ○    |

```json
{
    "userId":"abcd123",
    "password":"123",
    "name":"홍길동",
    "regNo":"860824-1655068"
}
```

### RESPONSE

| 파라미터     |      타입      | 설명         |
|:----------|:------------:|:-----------|
| userId   |    String    | 회원가입한 유저ID |
| password |    String    | 회원가입한 비밀번호 |

```json
{
    "success": true,
    "code": "E000",
    "msg": "성공",
    "status": 200,
    "data": {
        "userId": "abcd123",
        "name": "홍길동"
    }
}
```

### 구현 방법

- 파라미터로 userId, password, name, regNo를 받아옵니다. 파라미터는 필수값이 없을 경우 예외처리를 적용하였습니다.
- 중복 회원가입, 존재하는 userId값, 옳바른 주민등록번호인지 판단하여 예외처리를 적용하였습니다. 
- 비밀번호는 단방향 암호화 알고리즘인 bcrypt 해시 알고리즘을 이용하여 암호화했습니다.
- 주민등록번호는 양방향 암호화 알고리즘인 AES/CBC/PKCS5Padding 암호화 알고리즘을 이용하여 암호화 했습니다.

---
### 로그인

### REQUEST

> POST /szs/login

- BODY

| 파라미터      |   타입   | 설명           |   필수   |
|:----------|:------:|:-------------|:------:|
| userId    | String | 로그인 요청할 유저ID |   ○    |
| password  |    String    | 로그인 요청할 비밀번호 |   ○    |

```json
{
  "userId":"abcd123",
  "password":"123"
}
```

### RESPONSE

- BODY

| 파라미터        |      타입      | 설명               |
|:------------|:------------:|:-----------------|
| userId      |    String    | 로그인한 userId      |
| accessToken |    String    | 로그인 accessToken |

```json
{
  "success": true,
  "code": "E000",
  "msg": "성공",
  "status": 200,
  "data": {
    "userId": "abcd123",
    "token": {
      "accessToken": "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhYmNkMTIzIiwiaWF0IjoxNjQ5MTczMDgzLCJleHAiOjE2NDkxNzQ4ODMsIjpuYW1lIjoi7ZmN6ri464-ZIn0.i3Cp3adP_6wuyLPpAow93qbqaDnOUeEFCXdDG_DCCBY"
    }
  }
}
```

### 구현 방법

- 파라미터로 userId, password를 받아옵니다. 파라미터는 필수값이 없을 경우 예외처리를 적용하였습니다.
- JWT토큰을 이용하여 로그인시 토큰 정보를 받아옵니다.
- SpringSecurity는 기본 설정만 추가하고 interceptor를 이용하여 token에 담겨진 user의 정보를 조회하여 회원 유무를 판단합니다. 만약 회원이 아닐경우 예외처리를 적용하였습니다.

---
### 내 정보 보기

### REQUEST

> GET /szs/me

- HEADER

| 파라미터      |   타입   | 설명                                |   필수   |
|:----------|:------:|:----------------------------------|:------:|
| Authorization    | String | Bearer 뒤에 accesstoken 값을 추가해서 보낸다 |   ○    |


### RESPONSE

| 파라미터   |      타입      | 설명                    |
|:-------|:------------:|:----------------------|
| userId |    String    | token 정보의 userId      |
| name   |    String    | token 정보의 유저이름        |
| regNo  |    String    | token 정보의 마스킹된 주민등록번호 |


```json
{
  "success": true,
  "code": "E000",
  "msg": "성공",
  "status": 200,
  "data": {
    "userId": "abcd123",
    "name": "홍길동",
    "regNo": "860824-1******"
  }
}
```

### 구현 방법

- Header에서 token 정보를 받아와 회원 정보를 조회해옵니다.
- redis를 이용하여 한번 이상 내 정보를 조회한 회원은 redis를 통해 정보를 불러오게 됩니다.
- redis와 db의 정합성 관련 이슈는 해당 정보들이 쉽게 바뀌지 않고, USER PATCH 요청시 해당 데이터를 맞춰주면 된다고 생각하여서 redis를 통해 응답을 받도록 하였습니다.

### 테스트 결과

![유저 테스트1](./static/usertest2.PNG)
![유저 테스트2](./static/usertest3.PNG)
![유저 테스트3](./static/usertest1.PNG)

---
### 스크랩정보 요청

### REQUEST

> POST /szs/scrap

- HEADER

| 파라미터      |   타입   | 설명                                |   필수   |
|:----------|:------:|:----------------------------------|:------:|
| Authorization    | String | Bearer 뒤에 accesstoken 값을 추가해서 보낸다 |   ○    |


### RESPONSE

```json
{
  "success": true,
  "code": "E001",
  "msg": "API 요청 완료",
  "status": 200,
  "data": null
}
```

### 기능구현

- Header에서 token 정보를 받아와 일치하는 회원의 정보를 scrap api로 요청합니다.
- 요청후 사용자의 무분별한 응답대기를 해결하기 위해 WebFlux의 non-block 방식의 통신으로 스크랩 데이터를 요청합니다.
- 무분별한 중복 요청을 방지하기 위해 redis에 토큰정보를 담아 1분간 중복요청을 방지하였습니다.
- 비동기통신으로 받아온 데이터를 각 DTO(스크랩유저, 소득정보, 세금정보, 응답정보, 응답상태)에 담아서 저장합니다.
- 이미 저장된 데이터가 있을 경우 데이터 정합성을 위하여 삭제하고 다시 저장해줍니다.

---
### 유저의 환급액 조회

### REQUEST

> GET /szs/refund

- HEADER

| 파라미터      |   타입   | 설명                                |   필수   |
|:----------|:------:|:----------------------------------|:------:|
| Authorization    | String | Bearer 뒤에 accesstoken 값을 추가해서 보낸다 |   ○    |


### RESPONSE

| 파라미터 |      타입      | 설명     |
|:-----|:------------:|:-------|
| 이름   |    String    | 유저의 이름 |
| 한도   |    String    | 한도 금액  |
| 공제액  |    String    | 공제 금액  |
| 환급액  |    String    | 환급 금액  |

```json
{
  "success": true,
  "code": "E000",
  "msg": "성공",
  "status": 200,
  "data": {
    "이름": "홍길동",
    "한도": "74만원",
    "공제액": "92만 5천원",
    "환급액": "74만원"
  }
}
```

### 기능구현

- Header에 저장된 token값을 가져와 회원의 scrap정보를 불러옵니다.
- scrap데이터가 없지만 redis에 token값이 존재할 경우 scrap처리중이라는 안내 응답을 보내도록 예외처리 했습니다.
- scrap데이터가 존재하여 데이터를 가져왔을 경우 해당 값으로 환급액을 계산하여 반환합니다.
- 한번 이상 조회 할 경우 redis에 저장 후 일정 시간동안 해당값을 반환하도록 설정하여 재요청에 대한 트래픽을 최소화 하였습니다.


### 테스트 결과

![환급 테스트1](./static/refundtest1.PNG)
![환급 테스트2](./static/refundtest2.PNG)
![스크랩 테스트1](./static/scraptest1.PNG)
![스크랩 테스트2](./static/scraptest2.PNG)
![레디스 테스트2](./static/redisservicetest1.PNG)

---

## 개선 방안

1. 인메모리 저장공간인 Redis를 이용해 줄여 응답성능을 높였습니다.
2. WebFlux의 non blocking 방식을 통해 사용자의 무분별한 응답 대기현상을 최소화 하였습니다.
4. 요청온 토큰값을 Redis에 set하고 데이터에 timeout을 걸어 scrap API의 무분별한 중복호출을 방지하여 트래픽을 최소화 하였습니다.

# 주관식 문제

1. 테스트코드 작성시 setup 해야 할 데이터가 대용량이라면 어떤 방식으로 테스트코드를
   작성하실지 서술해 주세요.

- 

2. 이벤트 드리븐 기반으로 서비스를 만들 때 이벤트를 구성하는 방식과 실패 처리하는 방
   식에 대해 서술해 주세요.

- 

3. MSA 구성하는 방식에는 어떤 것들이 있고, 그중 선택하신다면 어떤 방식을 선택하실
   건가요?

- 

4. 외부 의존성이 높은 서비스를 만들 때 고려해야 할 사항이 무엇인지 서술해 주세요.

- 

5. 일정이 촉박한 프로젝트를 진행하게 되었습니다. 이 경우 본인의 평소 습관에 맞춰 개발
   을 진행할지, 회사의 코드 컨벤션에 맞춰 개발할지 선택해 주세요. 그리고 그 이유를 서
   술해 주세요.

- 회사의 코드 컨벤션에 맞춰 개발할 것입니다. SI를 직접 경험하다 보니, 일정에 쫓겨 개발을 진행한 경험이 있습니다. 일정에 맞춰 개발을 하는 것도 중요하기 때문에 많은 사람이 회사 내 가이드라인보다 개인의 습관을 우선시하여 개발을 진행하게 됐는데 이후 중간중간 이슈가 터져 문제를 수정하거나, 개발이 끝난 뒤 유지보수를 진행하는 동안 초기의 가이드라인에 준수하여 작성된 코드와 후반의 코드들의 처리작업 효율과 리팩토링의 차이를 몸소 느낄 수 있었습니다.

6. 민감정보 암호화 알고리즘에는 어떤 것들이 있고, 그중 선택하신다면 어떤 것을 선택하
   실 건가요? 그 이유는 무엇인가요?

- 민감정보의 종류에는 비밀번호, 바이오정보, 주민등록번호 등이 있으며, 각각 정보통신망법과, 개인정보보호법에서는 해당 정보들을 암호화하여 정보를 보호하도록 규정되어 있습니다. 먼저 비밀번호의 경우 해시알고리즘으로 암호화하는걸 규정으로 하고있기 때문에, Bcrypt 단방향 알고리즘을 선택할 것입니다. 해당 알고리즘은 기존 sha256같은 탐색에 목적이 아닌 비밀번호를 암호화 하기 위해 탄생한 알고리즘일 뿐더러, 반복횟수를 지정해 여러번 해시 할 수있기 때문입니다. 또한 SpringSecurity 에서도 해당 암호화 알고리즘이 구현되어있기 때문에 쉽게 적용 할 수가 있다.
  그외의 정보들은 대칭키 암호알고리즘을 통해 암호화 하도록 규정되어있는데, kisa(한국인터넷진흥원)에 따르면 국산 암호들을 권장하고 있지만, 자료들이 많이 없기 때문에 주로 AES관련 암호화 알고리즘이 사용되고 있습니다. 그중 AES/CBC/PKCS5Padding 알고리즘을 사용할 것입니다. 권장알고리즘 중 많은 레퍼런스로인해 구현이 쉽게 가능하기 때문입니다.










