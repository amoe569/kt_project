# CCTV AI 관제 데모

Spring Boot + Python/YOLOv5를 활용한 CCTV AI 관제 시스템 데모입니다.

## 🎯 프로젝트 목표

- 로컬 동영상(가짜 CCTV) → Python(OpenCV+YOLOv5) 분석 → Spring Boot로 이벤트/영상메타 전송
- 웹 UI에서: GIS 맵(카메라 상태), 라이브 스트림 임베드(MJPEG), 이벤트 실시간 알림(SSE), 저장 영상 조회/재생(Range)

## 🏗️ 기술 스택

- **Backend**: Java 17, Spring Boot 3.x, Gradle, JPA(H2 dev / PostgreSQL prod)
- **Frontend**: Thymeleaf + Leaflet(지도) + SSE(EventSource) + 최소 JS
- **Detector**: Python 3.10, ultralytics/yolov5, OpenCV, Flask/FastAPI
- **Packaging**: Docker Compose

## 🚀 빠른 시작

### 로컬 개발 환경

```bash
# 1. Spring Boot 앱 실행
cd control-center
./gradlew bootRun

# 2. Python Detector 실행
cd detector
python app.py

# 3. 브라우저에서 http://localhost:8080 접속
```

### Docker 환경

```bash
# 전체 시스템 실행
docker compose up -d

# 개별 서비스 실행
docker compose up control-center -d
docker compose up detector -d
```

## 📁 프로젝트 구조

```
cctv-ai-demo/
├── control-center/           # Spring Boot 애플리케이션
│   ├── src/main/java/...    # Java 소스 코드
│   └── src/main/resources/  # 설정 파일 및 템플릿
├── detector/                 # Python AI Detector
│   ├── app.py               # 메인 애플리케이션
│   ├── requirements.txt     # Python 의존성
│   └── videos/              # 샘플 CCTV 영상
├── infra/                   # 인프라 설정
│   ├── docker-compose.yml   # Docker Compose 설정
│   └── .env.example         # 환경 변수 예시
└── README.md                # 프로젝트 문서
```

## 🔌 API 엔드포인트

### 이벤트 관리
- `POST /api/events` - AI 탐지 이벤트 등록
- `GET /api/events/stream` - 실시간 이벤트 스트림 (SSE)

### 비디오 관리
- `POST /api/videos` - 녹화 영상 메타데이터 등록
- `GET /api/videos` - 영상 목록 조회
- `GET /api/videos/{id}/stream` - 영상 스트리밍 (Range 지원)

### 카메라 관리
- `GET /api/cameras` - 사용자 소유 카메라 목록

### 웹 인터페이스
- `GET /` - 메인 대시보드 (GIS 맵)
- `GET /cameras/{id}` - 카메라 상세 페이지

## 🧪 테스트

### 이벤트 등록 테스트
```bash
curl -X POST http://localhost:8080/api/events \
  -H "Content-Type: application/json" \
  -d '{
    "cameraId": "cam-001",
    "type": "person",
    "score": 0.85,
    "bbox": {"x": 100, "y": 150, "w": 50, "h": 100},
    "ts": "2024-01-01T12:00:00Z"
  }'
```

### SSE 연결 테스트
```bash
curl -N http://localhost:8080/api/events/stream
```

### 비디오 등록 테스트
```bash
curl -X POST http://localhost:8080/api/videos \
  -H "Content-Type: application/json" \
  -d '{
    "cameraId": "cam-001",
    "startTs": "2024-01-01T12:00:00Z",
    "endTs": "2024-01-01T12:30:00Z",
    "path": "/recordings/cam-001_20240101_120000.mp4"
  }'
```

## 🔧 개발 환경 설정

### 필수 요구사항
- Java 17+
- Python 3.10+
- Docker & Docker Compose
- Gradle 7.0+

### 환경 변수
```bash
# .env 파일 생성
cp infra/.env.example infra/.env

# 주요 설정
API_BASE=http://localhost:8080
CAMERA_ID=cam-001
SCORE_THRESHOLD=0.5
```

## 📊 데이터 모델

- **users**: 사용자 정보 (UUID 기반)
- **cameras**: 카메라 정보 (위치, 상태, 스트림 URL)
- **videos**: 녹화 영상 메타데이터
- **events**: AI 탐지 이벤트
- **alerts**: 경보 상태 관리

## 🚨 문제 해결

### 일반적인 문제
1. **포트 충돌**: 8080, 5001 포트가 사용 중인지 확인
2. **의존성 문제**: `./gradlew clean build` 실행
3. **Python 패키지**: `pip install -r requirements.txt` 재실행

### 로그 확인
```bash
# Spring Boot 로그
./gradlew bootRun

# Docker 로그
docker compose logs -f control-center
docker compose logs -f detector
```

## 📝 라이선스

MIT License

## 🤝 기여

이슈 및 풀 리퀘스트 환영합니다.
