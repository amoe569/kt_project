.PHONY: help build run test clean fmt docker-up docker-down

# 기본 타겟
help: ## 도움말 표시
	@echo "사용 가능한 명령어:"
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-20s\033[0m %s\n", $$1, $$2}'

# Spring Boot 관련
build: ## Spring Boot 애플리케이션 빌드
	cd control-center && ./gradlew clean build

run: ## Spring Boot 애플리케이션 실행
	cd control-center && ./gradlew bootRun

test: ## 테스트 실행
	cd control-center && ./gradlew test

fmt: ## 코드 포맷팅 (Spotless)
	cd control-center && ./gradlew spotlessApply

# Python Detector 관련
det-up: ## Python Detector 실행
	cd detector && python app.py

det-install: ## Python 의존성 설치
	cd detector && pip install -r requirements.txt

# Docker 관련
docker-up: ## Docker Compose로 전체 시스템 실행
	docker compose -f infra/docker-compose.yml up -d

docker-down: ## Docker Compose 중지
	docker compose -f infra/docker-compose.yml down

docker-logs: ## Docker 로그 확인
	docker compose -f infra/docker-compose.yml logs -f

# 개발 환경 설정
setup: ## 개발 환경 초기 설정
	@echo "개발 환경 설정 중..."
	@mkdir -p control-center/src/main/java
	@mkdir -p control-center/src/main/resources
	@mkdir -p detector/videos
	@mkdir -p infra
	@echo "환경 설정 완료!"

# 전체 빌드 및 실행
all: setup build docker-up ## 전체 시스템 빌드 및 실행

# 정리
clean: ## 정리
	cd control-center && ./gradlew clean
	docker compose -f infra/docker-compose.yml down -v
