# Synclife-StudyRoom-Reservation
* 동시성 제어를 통한 스터디룸 예약 시스템
## 기술 스택

### Backend
- **Java 17**
- **Spring Boot 3.5.6**
- **Spring Data JPA**
### Database
- **PostgreSQL 15**

*** 

## 실행 방법
```bash
# 1. 프로젝트 빌드
./gradlew build

# 2. Docker로 실행
docker compose up

# 3. 애플리케이션 접속
http://localhost:8080
```

*** 

## ERD 설계도
<img width="935" height="459" alt="erd" src="https://github.com/user-attachments/assets/8c2d1b46-2468-49f1-b35e-339b09763b99" />

*** 

# API 명세서

## 인증
```
Authorization: Bearer admin-token           # ADMIN 권한
Authorization: Bearer user-token-{userId}   # USER 권한 (예: user-token-2)
```

## API 엔드포인트

| Method | Endpoint | 권한 | 설명 |
|--------|----------|------|------|
| POST | `/api/rooms` | ADMIN | 회의실 등록 |
| GET | `/api/rooms?date=YYYY-MM-DD` | USER | 가용성 조회 |
| POST | `/api/reservations` | USER | 예약 생성 |
| DELETE | `/api/reservations/{id}` | OWNER/ADMIN | 예약 취소 |

## 요청 예시

### 회의실 등록
```bash
curl -X POST http://localhost:8080/api/rooms \
  -H "Authorization: Bearer admin-token" \
  -H "Content-Type: application/json" \
  -d '{"name": "회의실A", "location": "2층", "capacity": 10}'
```

### 예약 생성
```bash
curl -X POST http://localhost:8080/api/reservations \
  -H "Authorization: Bearer user-token-2" \
  -H "Content-Type: application/json" \
  -d '{"roomId": 1, "startAt": "2025-09-29T14:00:00", "endAt": "2025-09-29T16:00:00"}'
```

***

## LLM 사용 내역

### 사용 구간:
- 방별 가용 시간대 계산 로직 구현 (예약된 시간을 제외한 빈 시간대 도출)
- Docker 환경 구성 (Dockerfile 및 docker-compose.yml 작성)
