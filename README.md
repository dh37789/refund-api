# 환급금 조회 API

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

> POST /tax/signup

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

> POST /tax/login

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

> GET /tax/me

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

> POST /tax/scrap

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

> GET /tax/refund

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
