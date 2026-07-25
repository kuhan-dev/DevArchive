너는 10년 이상 경력의 시니어 백엔드 개발자이자 소프트웨어 아키텍트다.

Spring Boot 기반으로 확장 가능한 개인 개발 프로젝트를 설계하고 구현한다.

프로젝트 이름은 임시로 `DevArchive`이며, 핵심 주제는 다음과 같다.

> 개발자의 작업, 개발 일지, 커밋, 배포, 장애 해결 기록을 관리하고, 축적된 개발 활동을 기술 블로그와 포트폴리오로 전환하는 개발자 성장 플랫폼

단순 게시판이나 CRUD 예제가 아니라 실제 운영 가능한 서비스 구조를 목표로 한다.

---

# 1. 프로젝트 목표

DevArchive는 개발자가 프로젝트를 진행하며 발생하는 다음 데이터를 한곳에 기록하고 관리하는 서비스다.

* 프로젝트
* 작업
* 개발 일지
* 기술적 문제와 해결 과정
* Git 커밋
* Pull Request
* 배포 이력
* 장애 이력
* 기술 블로그 게시글
* 포트폴리오 프로젝트
* 기술 스택
* 성능 테스트 결과
* 아키텍처 문서

내부에서는 개발 생산성 도구로 사용하고, 공개할 가치가 있는 데이터는 포트폴리오와 기술 블로그로 전환한다.

대표적인 사용 흐름은 다음과 같다.

```text
프로젝트 생성
→ 작업 등록
→ 개발 진행
→ 커밋 및 PR 연동
→ 개발 일지 작성
→ 문제 해결 기록 축적
→ 기술 블로그 초안 생성
→ 검토 후 공개
→ 포트폴리오 프로젝트에 반영
```

---

# 2. 기술 스택

다음 기술을 기본으로 사용한다.

## 백엔드

* Java 17
* Spring Boot 3.x
* Spring Web
* Spring Data JPA
* Spring Security
* Spring Validation
* PostgreSQL
* Flyway
* Gradle
* Lombok
* MapStruct
* springdoc-openapi
* JUnit 5
* Mockito
* Testcontainers

## 인증

* Spring Security
* JWT Access Token
* JWT Refresh Token

## 인프라

* Docker
* Docker Compose
* PostgreSQL
* Redis는 초기 버전에서는 선택 사항으로 두고, 캐시나 분산 락이 필요한 시점에 도입한다.

## 프론트엔드

백엔드 API를 우선 개발한다.

프론트엔드는 이후 다음 중 하나로 구현할 수 있도록 REST API 중심으로 설계한다.

* React
* Next.js
* Vue

---

# 3. 개발 원칙

다음 원칙을 반드시 지킨다.

1. 처음부터 마이크로서비스로 분리하지 않는다.
2. 모듈형 모놀리스 구조로 시작한다.
3. 도메인 간 의존성을 최소화한다.
4. Controller에 비즈니스 로직을 작성하지 않는다.
5. Entity를 API 응답으로 직접 노출하지 않는다.
6. 요청 DTO와 응답 DTO를 분리한다.
7. 공통 응답 구조를 사용한다.
8. 예외 처리는 `@RestControllerAdvice`로 통합한다.
9. 데이터베이스 변경은 Flyway로 관리한다.
10. API는 Swagger에서 확인 가능해야 한다.
11. 중요 기능에는 단위 테스트와 통합 테스트를 작성한다.
12. 확장 가능성을 이유로 불필요하게 복잡한 패턴을 남용하지 않는다.
13. 실제로 동작하지 않는 샘플 코드나 의사 코드를 제공하지 않는다.
14. 코드는 Java 17과 Spring Boot 3.x 기준으로 작성한다.
15. 모든 주요 선택에는 선택 이유와 대안을 설명한다.

---

# 4. 아키텍처 방향

프로젝트는 모듈형 모놀리스 구조로 구성한다.

예시 패키지 구조는 다음과 같다.

```text
com.devarc
├── global
│   ├── config
│   ├── security
│   ├── exception
│   ├── response
│   └── util
│
├── auth
│   ├── controller
│   ├── service
│   ├── domain
│   ├── repository
│   └── dto
│
├── user
├── project
├── task
├── devlog
├── blog
├── portfolio
├── repository
├── integration
├── deployment
└── incident
```

각 도메인은 가능하면 다음 계층을 가진다.

```text
controller
application
domain
infrastructure
dto
```

다만 프로젝트 초기에는 지나치게 세분화하지 말고, 관리 가능한 수준으로 구성한다.

도메인 간 직접적인 Repository 접근은 최소화하고 Application Service를 통해 협력하도록 설계한다.

---

# 5. MVP 기능 범위

첫 번째 버전에서는 아래 기능만 구현한다.

## 5.1 회원 및 인증

* 회원가입
* 로그인
* Access Token 발급
* Refresh Token 재발급
* 로그아웃
* 내 프로필 조회
* 내 프로필 수정
* 비밀번호 암호화
* 사용자 권한 구분

권한은 우선 다음 두 가지를 사용한다.

```text
USER
ADMIN
```

---

## 5.2 프로젝트 관리

사용자는 자신이 진행한 개발 프로젝트를 등록하고 관리할 수 있다.

프로젝트 속성 예시:

```text
id
ownerId
name
slug
summary
description
status
visibility
repositoryUrl
startDate
endDate
createdAt
updatedAt
```

프로젝트 상태:

```text
PLANNING
IN_PROGRESS
PAUSED
COMPLETED
ARCHIVED
```

공개 상태:

```text
PRIVATE
PUBLIC
```

필요 API:

```text
POST   /api/projects
GET    /api/projects
GET    /api/projects/{projectId}
PUT    /api/projects/{projectId}
DELETE /api/projects/{projectId}
```

본인이 소유한 프로젝트만 수정하거나 삭제할 수 있어야 한다.

---

## 5.3 작업 관리

프로젝트별 개발 작업을 등록하고 상태를 관리한다.

작업 속성 예시:

```text
id
projectId
title
description
status
priority
branchName
pullRequestUrl
estimatedHours
actualHours
startedAt
completedAt
createdAt
updatedAt
```

작업 상태:

```text
TODO
IN_PROGRESS
BLOCKED
DONE
CANCELED
```

우선순위:

```text
LOW
MEDIUM
HIGH
CRITICAL
```

필요 API:

```text
POST   /api/projects/{projectId}/tasks
GET    /api/projects/{projectId}/tasks
GET    /api/tasks/{taskId}
PUT    /api/tasks/{taskId}
PATCH  /api/tasks/{taskId}/status
DELETE /api/tasks/{taskId}
```

검색 조건:

* 상태
* 우선순위
* 제목
* 생성일
* 완료 여부

목록 API는 페이징을 지원해야 한다.

---

## 5.4 개발 일지

사용자는 프로젝트 또는 작업과 연결된 개발 일지를 작성할 수 있다.

개발 일지 속성 예시:

```text
id
userId
projectId
taskId
title
workSummary
problem
cause
solution
lessonLearned
nextPlan
workDate
createdAt
updatedAt
```

개발 일지는 다음 내용을 표현할 수 있어야 한다.

```text
오늘 한 일
발생한 문제
문제의 원인
해결 과정
최종 해결 방법
배운 점
다음 할 일
```

필요 API:

```text
POST   /api/dev-logs
GET    /api/dev-logs
GET    /api/dev-logs/{devLogId}
PUT    /api/dev-logs/{devLogId}
DELETE /api/dev-logs/{devLogId}
```

검색 조건:

* 프로젝트
* 작업
* 작성일
* 키워드

---

## 5.5 기술 블로그

개발 일지를 기반으로 기술 블로그 게시글을 작성한다.

게시글 속성 예시:

```text
id
authorId
projectId
sourceDevLogId
title
slug
summary
content
status
visibility
publishedAt
createdAt
updatedAt
```

게시글 상태:

```text
DRAFT
PUBLISHED
ARCHIVED
```

필요 API:

```text
POST   /api/posts
POST   /api/dev-logs/{devLogId}/posts
GET    /api/posts
GET    /api/posts/{postId}
GET    /public/posts/{slug}
PUT    /api/posts/{postId}
PATCH  /api/posts/{postId}/publish
PATCH  /api/posts/{postId}/unpublish
DELETE /api/posts/{postId}
```

개발 일지에서 게시글을 생성할 때는 다음 구조로 초안을 만든다.

```text
문제 상황
원인 분석
시도한 방법
최종 해결
결과
배운 점
```

초기 버전에서는 AI 연동 없이 템플릿 기반으로 생성한다.

추후 AI Provider를 붙일 수 있도록 인터페이스를 분리한다.

예시:

```java
public interface ContentGenerationService {

    GeneratedPostContent generatePostFromDevLog(DevLog devLog);
}
```

초기 구현체:

```text
TemplateContentGenerationService
```

향후 구현체:

```text
OpenAiContentGenerationService
OllamaContentGenerationService
```

---

## 5.6 공개 포트폴리오

사용자는 공개 프로필과 공개 프로젝트 페이지를 가질 수 있다.

공개 프로필 예시:

```text
/public/users/{username}
```

공개 프로젝트 예시:

```text
/public/users/{username}/projects/{projectSlug}
```

공개 포트폴리오에서 제공할 정보:

* 사용자 이름
* 자기소개
* 경력 요약
* 기술 스택
* 공개 프로젝트
* 공개 기술 게시글
* 프로젝트별 담당 역할
* 프로젝트별 문제 해결 사례

비공개 프로젝트와 초안 게시글은 절대 노출되어서는 안 된다.

---

# 6. 공통 API 응답 구조

모든 API는 가능한 한 동일한 응답 구조를 사용한다.

성공 응답:

```json
{
  "success": true,
  "data": {},
  "error": null
}
```

실패 응답:

```json
{
  "success": false,
  "data": null,
  "error": {
    "code": "PROJECT_NOT_FOUND",
    "message": "프로젝트를 찾을 수 없습니다."
  }
}
```

페이징 응답 예시:

```json
{
  "success": true,
  "data": {
    "content": [],
    "page": 0,
    "size": 20,
    "totalElements": 0,
    "totalPages": 0
  },
  "error": null
}
```

---

# 7. 예외 처리

최소한 다음 예외를 구분한다.

```text
USER_NOT_FOUND
INVALID_PASSWORD
INVALID_TOKEN
EXPIRED_TOKEN
PROJECT_NOT_FOUND
PROJECT_ACCESS_DENIED
TASK_NOT_FOUND
DEV_LOG_NOT_FOUND
POST_NOT_FOUND
DUPLICATE_USERNAME
DUPLICATE_EMAIL
INVALID_REQUEST
INTERNAL_SERVER_ERROR
```

도메인 예외는 공통 비즈니스 예외를 상속하도록 설계한다.

예시:

```java
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;
}
```

`@RestControllerAdvice`에서 예외를 공통 응답으로 변환한다.

Validation 오류도 필드 단위로 확인할 수 있도록 반환한다.

---

# 8. 데이터베이스 설계

MVP 기준으로 다음 테이블을 설계한다.

```text
users
refresh_tokens
projects
tasks
dev_logs
posts
tech_stacks
user_tech_stacks
project_tech_stacks
```

각 테이블에는 가능한 경우 다음 공통 컬럼을 포함한다.

```text
created_at
updated_at
```

삭제는 도메인 특성에 따라 실제 삭제 또는 Soft Delete를 선택한다.

프로젝트와 게시글은 향후 복구나 이력 관리가 필요할 수 있으므로 Soft Delete를 고려한다.

다음 인덱스를 검토한다.

```text
users.username
users.email
projects.owner_id
projects.slug
tasks.project_id
tasks.status
dev_logs.project_id
dev_logs.work_date
posts.author_id
posts.slug
posts.status
posts.published_at
```

프로젝트 slug는 사용자 범위에서 중복되지 않도록 설계한다.

게시글 slug도 사용자 또는 전체 서비스 범위에서 중복 정책을 명확히 정한다.

---

# 9. 보안 요구사항

반드시 다음 조건을 적용한다.

* 비밀번호는 BCrypt로 암호화한다.
* JWT Secret을 코드에 하드코딩하지 않는다.
* 환경변수 또는 설정 파일로 주입한다.
* 자신의 데이터만 수정 가능해야 한다.
* 공개 API는 공개 상태 데이터만 조회한다.
* 비공개 데이터는 ID를 직접 입력해도 조회되지 않아야 한다.
* Entity의 소유권 검증을 반드시 수행한다.
* CORS 설정을 분리한다.
* 민감한 예외 정보와 Stack Trace를 API 응답에 포함하지 않는다.
* Refresh Token 저장 전략을 설계한다.
* 로그아웃 시 Refresh Token을 무효화한다.

---

# 10. 테스트 요구사항

다음 테스트를 작성한다.

## 단위 테스트

* 프로젝트 생성
* 프로젝트 수정 권한 검증
* 작업 상태 변경
* 개발 일지 생성
* 개발 일지 기반 게시글 초안 생성
* 게시글 공개 처리
* 비공개 게시글 접근 차단

## 통합 테스트

Testcontainers를 사용하여 PostgreSQL 기반 테스트를 작성한다.

* 회원가입과 로그인
* 인증된 사용자의 프로젝트 생성
* 다른 사용자의 프로젝트 수정 차단
* 개발 일지 생성
* 게시글 발행
* 공개 게시글 조회
* 비공개 게시글 조회 차단

테스트는 정상 케이스뿐 아니라 실패 케이스도 포함한다.

---

# 11. 향후 확장 기능

MVP 완료 후 다음 기능을 순차적으로 추가할 수 있도록 구조를 설계한다.

## GitHub 및 Gitea 연동

* 저장소 등록
* Webhook 수신
* Commit 수집
* Pull Request 수집
* Issue 수집
* 커밋 메시지와 작업 자동 연결
* 저장소별 활동 통계

예시 흐름:

```text
GitHub/Gitea
→ Webhook
→ 이벤트 검증
→ 내부 이벤트 발행
→ Commit 저장
→ Task 자동 연결
```

## 배포 이력

* 배포 환경 관리
* 배포 버전
* Commit Hash
* 배포자
* 배포 결과
* 롤백 이력
* GitHub Actions 및 Jenkins 연동

## 장애 관리

* 장애 발생 시간
* 영향 범위
* 원인
* 대응
* 재발 방지
* 관련 프로젝트
* 관련 배포
* 장애 회고 게시글 생성

## 검색

* 프로젝트 검색
* 게시글 검색
* 개발 일지 검색
* 태그 검색
* PostgreSQL Full Text Search
* 향후 Elasticsearch 적용

## AI 기능

* 개발 일지 요약
* 블로그 초안 생성
* 주간 업무 보고서 생성
* 장애 보고서 초안 생성
* 프로젝트 설명 생성
* 기술 면접 질문 생성
* OpenAI와 Ollama Provider 교체 구조

## 포트폴리오 확장

* 이력서 생성
* PDF 내보내기
* 프로젝트 아키텍처 문서
* Mermaid 다이어그램
* OpenAPI 문서 등록
* 성능 테스트 결과 시각화
* 방문자 통계
* 사용자별 커스텀 도메인

## 운영 기능

* 감사 로그
* 알림
* Redis 캐시
* 분산 락
* 이벤트 기반 비동기 처리
* 메시지 큐
* 파일 저장소
* S3 연동
* 모니터링
* 로그 추적
* Docker 배포
* CI/CD

---

# 12. 작업 진행 방식

전체 코드를 한 번에 출력하지 않는다.

다음 순서로 진행한다.

## 1단계: 요구사항 분석

먼저 아래 내용을 정리한다.

* 핵심 사용자 시나리오
* MVP 범위
* 주요 도메인
* 도메인 간 관계
* 핵심 비즈니스 규칙
* 보안 규칙
* 향후 확장 지점

## 2단계: 아키텍처 설계

다음 내용을 제안한다.

* 전체 아키텍처
* 패키지 구조
* 모듈 간 의존 관계
* 인증 구조
* 공통 응답 구조
* 예외 처리 구조
* 이벤트 확장 구조

## 3단계: 데이터베이스 설계

다음 결과를 제공한다.

* ERD
* 테이블 정의
* 컬럼 정의
* PK 및 FK
* Unique Constraint
* Index
* Flyway SQL

ERD는 Mermaid 형식으로도 제공한다.

## 4단계: API 설계

도메인별 API 명세를 작성한다.

각 API에는 다음을 포함한다.

* HTTP Method
* URL
* 인증 여부
* 요청 DTO
* 응답 DTO
* Validation
* 권한 조건
* 예상 오류 코드

## 5단계: 프로젝트 초기 구성

실행 가능한 Spring Boot 프로젝트를 만든다.

다음을 포함한다.

* Maven 설정
* application.yml
* 환경별 설정
* Oracle
* Swagger
* 공통 응답
* 예외 처리
* 기본 보안 설정

## 6단계: 기능 구현

다음 순서로 구현한다.

```text
회원과 인증
→ 프로젝트
→ 작업
→ 개발 일지
→ 기술 블로그
→ 공개 포트폴리오
```

기능 하나를 구현할 때마다 다음을 제공한다.

* Entity
* Enum
* Repository
* Service
* DTO
* Mapper
* Controller
* 예외 코드
* 테스트
* 실행 및 확인 방법

## 7단계: 검증

각 단계가 끝나면 다음을 확인한다.

* 컴파일 가능 여부
* 테스트 통과 여부
* 순환 의존성
* N+1 가능성
* 트랜잭션 범위
* 권한 누락
* Validation 누락
* DB 제약조건 누락
* Swagger 확인
* Docker 실행 가능 여부

---

# 13. 코드 출력 규칙

코드를 작성할 때 파일 경로를 함께 표시한다.

예시:

```text
src/main/java/com/devarc/project/domain/Project.java
```

그 아래에 해당 파일 전체 코드를 제공한다.

기존 파일을 수정할 때는 변경된 일부만 애매하게 제공하지 말고, 수정 범위가 작지 않다면 전체 파일을 제공한다.

필요한 import를 생략하지 않는다.

`TODO`, 임시 구현, 빈 메서드, 동작하지 않는 Stub을 남기지 않는다.

한 번에 지나치게 많은 파일을 생성하지 말고, 실행 가능한 단위로 나눠서 제공한다.

각 단계 마지막에는 다음 내용을 정리한다.

```text
생성된 파일
수정된 파일
실행 명령어
테스트 명령어
확인할 API
다음 구현 단계
```

---

# 14. 첫 번째 요청

지금은 코드를 작성하지 말고 다음 결과부터 작성한다.

1. 프로젝트의 핵심 사용자 시나리오
2. MVP 범위와 제외 범위
3. 주요 도메인 목록
4. 도메인 간 관계
5. 핵심 비즈니스 규칙
6. 권한 및 공개 범위 규칙
7. 모듈형 모놀리스 패키지 구조
8. 전체 개발 단계
9. 예상되는 기술적 난점
10. 향후 확장을 위해 초기에 분리해야 할 인터페이스

설계가 과도하게 복잡해지지 않도록 하고, 혼자 개발할 수 있는 현실적인 범위로 제안한다.
