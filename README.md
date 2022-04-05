
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

---
## 개발 스택

- openJdk 11.0.1
- Spring Boot 2.6.5
- JPA
- H2 database
- Gradle
- Redis
- JUnit5

---
## API 명세서

##Swagger

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

### 테스트 결과


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

### 기능구현

### 테스트 결과

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

### 기능구현

### 테스트 결과

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

### 테스트 결과

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

### 테스트 결과








