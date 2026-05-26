# 대규모 트래픽 방어를 위한 EKS 기반 수강신청 인프라 및 GitOps 파이프라인 구축

> Spring Boot 기반 수강신청 서비스를 EKS 위에 배포하고, GitOps 파이프라인으로 자동화한 클라우드 네이티브 프로젝트
>

<br>

## 📎 레포지토리

| 구분 | URL |
| --- | --- |
| App (소스코드) | https://github.com/CLD-05/team1-app |
| Config (매니페스트) | https://github.com/CLD-05/team1-config |

<br>

## 🏗️ 아키텍처 개요

```
사용자 요청
    │
    ▼
Route 53 → ALB (Ingress Controller)
    │
    ▼
EKS Cluster
 ├── Spring Boot Pod (수강신청 서비스)
 └── ArgoCD (GitOps CD)
    │
    ▼
RDS MySQL (수강신청 DB)

ECR (컨테이너 이미지 저장소)
S3 + DynamoDB (Terraform Backend)
```

**CI 흐름 (App 레포)**

```
Push → GitHub Actions (OIDC) → Docker Build → ECR Push → Config 레포 image tag 갱신
```

**CD 흐름 (Config 레포)**

```
Config 레포 변경 감지 → ArgoCD → EKS 자동 배포
```

<br>

## 🛠️ 기술 스택

### Application

| 분류 | 기술 |
| --- | --- |
| Language | Java 17 |
| Framework | Spring Boot 3.3 |
| Build | Maven |
| DB | MySQL 8.0 (RDS) |
| ORM | Spring Data JPA |
| Auth | JWT (Cookie 기반) |
| Template | Thymeleaf |
| Migration | Flyway |

### Infrastructure

| 분류 | 기술 |
| --- | --- |
| Container | Docker (멀티스테이지), Amazon ECR |
| Orchestration | Amazon EKS (Kubernetes) |
| IaC | Terraform (S3 + DynamoDB Backend) |
| CI | GitHub Actions (OIDC) |
| CD | ArgoCD |
| Ingress | AWS ALB Controller + IRSA |
| Monitoring | Prometheus, Grafana, CloudWatch, k6 |

<br>

## 📁 레포지토리 구조

### team1-app (App 레포)

```
src/
├── main/
│   ├── java/com/ops/app/courseregistration/
│   │   ├── auth/           # 로그인/로그아웃, JWT 발급
│   │   ├── course/         # 강의 검색
│   │   ├── enrollment/     # 수강신청/취소/조회
│   │   ├── student/        # 학생 엔티티
│   │   ├── security/       # JWT 필터, Security 설정
│   │   └── global/         # 공통 예외 처리
│   └── resources/
│       ├── templates/      # Thymeleaf 템플릿
│       ├── application.yml
│       ├── application-local.yml
│       └── db/
│           ├── migration/  # Flyway DDL
│           └── seed/       # 테스트 데이터
Dockerfile                  # 멀티스테이지 빌드
docker-compose.yml          # 로컬 개발용
```

### team1-config (Config 레포)

<br>
<br>

## 🌿 브랜치 전략 & PR 규칙

### 브랜치 구조

```
main
 └── develop
      └── feature/이름/기능명     # 예: feature/yueun/subject_search
```

- `feature/*` → `develop` 방향으로만 PR 생성
- CI는 App 레포, CD는 Config 레포 책임 분리 (GitOps 정석)

### 커밋 컨벤션

| prefix | 용도 |
| --- | --- |
| `feat` | 새 기능 추가 |
| `fix` | 버그 수정 |
| `refactor` | 코드 개선 |
| `chore` | 설정, 의존성 변경 |
| `docs` | 문서, 주석 |

### PR 규칙

1. `feature/*` → `develop` 방향으로만 PR 생성
2. PR 제목 형식: `[feat] 강의 검색 API`
3. PR 본문 필수 항목
    - 무엇을 / 왜 / 어떻게
    - 영향 범위
    - 테스트 결과
4. 인프라 변경 PR은 `terraform plan` 결과 본문 첨부 필수
5. 팀원 1인 이상 리뷰 후 approve → merge
6. approve 없이 merge 금지

<br>

## 📋 프로젝트 진행 흐름

```
주제 확정 (프로젝트1 연계)
    → 레포 분리 설계 (App / Config)
    → 역할 분담
    → Spring Boot 컨테이너화 점검 + Dockerfile 멀티스테이지 정비
    → Terraform 모듈 작성 (VPC / EKS / RDS / ECR / IAM)
       + S3 + DynamoDB Backend 구성
    → EKS 클러스터 생성 + ALB Controller / IRSA 설정
    → k8s 매니페스트 작성 (Deployment / Service / Ingress / ConfigMap / Secret)
    → GitHub Actions CI 구성 (OIDC → ECR Push → Config 레포 image tag 갱신)
    → ArgoCD 설치 → Application 등록 → Config 레포 추적 시작
    → 서비스 검증 + AWS 아키텍처 다이어그램 정리
    → 산출물 정리 → 제출 → 발표
```

<br>

## 👥 팀원

| 이름 | 담당 |
| --- | --- |
| 정지찬 | 백엔드/프론트엔드 개발, 모니터링 (Prometheus, Grafana, k6) |
| 이유은 | 백엔드/프론트엔드 개발, Terraform IaC |
| 김우연 | 백엔드/프론트엔드 개발, Kubernetes 매니페스트 |
| 배성민 | Terraform IaC, Kubernetes 매니페스트 |
| 전지훈 | CI/CD GitOps (GitHub Actions, ArgoCD) |
| 이윤범 | CI/CD GitOps (GitHub Actions, ArgoCD) |
| 유준영 | 모니터링 (CloudWatch, k6 부하테스트) |

<br>

## 📋 환경 변수

| 변수 | 설명 | 기본값 (local) |
| --- | --- | --- |
| `DB_URL` | MySQL 접속 URL | `jdbc:mysql://localhost:3306/course_registration` |
| `DB_USERNAME` | DB 유저명 | `app` |
| `DB_PASSWORD` | DB 비밀번호 | `app` |
| `JWT_SECRET` | JWT 서명 키 | 로컬 개발용 임시값 |

> EKS 배포 시 k8s Secret으로 주입