# DevArchive

개발 활동을 기록하고 포트폴리오로 만드는 서비스입니다.

## 1단계 구현 범위

- Java 17 / Spring Boot 3 / Maven
- Oracle / Spring Data JPA / Flyway
- Spring Security / BCrypt
- JWT Access Token 및 Refresh Token 발급
- Swagger UI
- 회원가입 및 로그인
- 공통 API 응답과 전역 예외 처리

## 기본 실행(H2 인메모리 DB)

Java 17과 Maven 3.9 이상이 필요합니다. 기본 설정은 별도 DB 설치 없이 H2
인메모리 DB를 사용합니다.

```powershell
$env:JWT_SECRET="change-this-to-a-random-secret-at-least-32-bytes"

mvn spring-boot:run
```

애플리케이션을 종료하면 H2 데이터는 초기화됩니다.

- 초기 관리자: `admin`
- 초기 비밀번호: `admin`
- H2 콘솔: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:devarchive`
- 사용자명: `sa`
- 비밀번호: 없음

초기 관리자 정보는 `INITIAL_ADMIN_USERNAME`, `INITIAL_ADMIN_PASSWORD`,
`INITIAL_ADMIN_EMAIL` 환경변수로 변경할 수 있습니다.

## Oracle로 실행

```powershell
docker compose up -d

$env:SPRING_PROFILES_ACTIVE="oracle"
$env:DB_URL="jdbc:oracle:thin:@localhost:1521/FREEPDB1"
$env:DB_USERNAME="devarchive"
$env:DB_PASSWORD="devarchive"
$env:JWT_SECRET="change-this-to-a-random-secret-at-least-32-bytes"

mvn spring-boot:run
```

## 테스트

테스트는 별도 Oracle 없이 H2의 Oracle 호환 모드로 실행됩니다.

```powershell
mvn test
```

## API

- `POST /api/auth/signup`: 회원가입
- `POST /api/auth/login`: 로그인 및 Access/Refresh Token 발급
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

회원가입 요청:

```json
{
  "username": "developer",
  "email": "developer@example.com",
  "password": "password123!"
}
```

로그인 요청:

```json
{
  "username": "developer",
  "password": "password123!"
}
```
