#!/usr/bin/env python3
"""
CCTV AI Detector - YOLOv8 기반 RTSP 스트림 객체 탐지 및 이벤트 전송
"""

import os
import cv2
import time
import json
import requests
import threading
from datetime import datetime, timedelta
from flask import Flask, Response, render_template_string
from ultralytics import YOLO
import numpy as np
from dotenv import load_dotenv

# 환경 변수 로드
load_dotenv()

app = Flask(__name__)

# 설정
API_BASE = os.getenv('API_BASE', 'http://localhost:8080')
SCORE_THRESHOLD = float(os.getenv('SCORE_THRESHOLD', '0.4'))

# RTSP 스트림 설정
RTSP_STREAMS = {
    "cam-001": "rtsp://210.99.70.120:1935/live/cctv001.stream",
    "cam-002": "rtsp://210.99.70.120:1935/live/cctv002.stream",
    "cam-003": "rtsp://210.99.70.120:1935/live/cctv003.stream",
    "cam-004": "rtsp://210.99.70.120:1935/live/cctv004.stream"
}

# 전역 변수
camera_frames = {cam_id: None for cam_id in RTSP_STREAMS.keys()}
camera_locks = {cam_id: threading.Lock() for cam_id in RTSP_STREAMS.keys()}
camera_status = {cam_id: "UNKNOWN" for cam_id in RTSP_STREAMS.keys()}
model = None

def load_yolo_model():
    """YOLOv8 모델 로드"""
    global model
    try:
        print("YOLOv8 모델 로딩 중...")
        # YOLOv8n 모델 로드 (가장 가벼운 최신 모델)
        model = YOLO('yolov8n.pt')
        print("✅ YOLOv8n 모델 로딩 완료")
        return True
    except Exception as e:
        print(f"❌ YOLOv8 모델 로딩 실패: {e}")
        print("⚠️ 더미 탐지 모드로 실행됩니다.")
        return False

def detect_objects_yolo(frame, camera_id):
    """YOLOv8을 사용한 객체 탐지 - 사람과 차량만 필터링"""
    detections = []
    
    # 사람과 차량 관련 클래스 정의
    PERSON_VEHICLE_CLASSES = {
        'person',      # 사람
        'car',         # 자동차
        'truck',       # 트럭
        'bus',         # 버스
        'motorcycle',  # 오토바이
        'bicycle'      # 자전거
    }
    
    if model is None:
        # 더미 탐지 (YOLOv8 로드 실패 시) - 사람과 차량만
        if np.random.random() < 0.05:  # 5% 확률로 이벤트 발생
            detection_type = np.random.choice(list(PERSON_VEHICLE_CLASSES))
            score = np.random.uniform(0.6, 0.9)
            x = np.random.randint(100, frame.shape[1] - 100)
            y = np.random.randint(100, frame.shape[0] - 100)
            w = np.random.randint(50, 150)
            h = np.random.randint(100, 200)
            
            detections.append({
                "type": detection_type,
                "severity": 3,  # 사람과 차량은 모두 높은 우선순위
                "score": score,
                "ts": datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3],
                "boundingBox": {"x": x, "y": y, "w": w, "h": h}
            })
        return detections
    
    try:
        # YOLOv8 탐지 수행
        results = model(frame, verbose=False)
        
        for result in results:
            boxes = result.boxes
            if boxes is not None:
                for box in boxes:
                    # 바운딩 박스 좌표
                    x1, y1, x2, y2 = box.xyxy[0].cpu().numpy()
                    x1, y1, x2, y2 = int(x1), int(y1), int(x2), int(y2)
                    
                    # 클래스 및 신뢰도
                    cls = int(box.cls[0].cpu().numpy())
                    conf = float(box.conf[0].cpu().numpy())
                    
                    if conf > SCORE_THRESHOLD:
                        # 클래스 이름 가져오기
                        class_name = model.names[cls]
                        
                        # 사람과 차량 클래스만 필터링
                        if class_name in PERSON_VEHICLE_CLASSES:
                            # 바운딩 박스 그리기 (사람과 차량만)
                            cv2.rectangle(frame, (x1, y1), (x2, y2), (0, 255, 0), 2)
                            
                            # 클래스 이름 및 신뢰도 표시
                            label = f'{class_name} {conf:.2f}'
                            cv2.putText(frame, label, (x1, y1-10), 
                                       cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)
                            
                            # 탐지 결과 저장 (사람과 차량만)
                            detections.append({
                                "type": class_name,
                                "severity": 3,  # 사람과 차량은 모두 높은 우선순위
                                "score": conf,
                                "ts": datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3],
                                "boundingBox": {
                                    "x": x1,
                                    "y": y1,
                                    "w": x2 - x1,
                                    "h": y2 - y1
                                }
                            })
        return detections
        
    except Exception as e:
        print(f"❌ {camera_id}: YOLOv8 탐지 중 오류 발생: {e}")
        return detections

def send_event_to_api(camera_id, detection):
    """Spring Boot API로 이벤트 전송"""
    event_data = {
        "cameraId": camera_id,
        "type": detection["type"],
        "severity": detection["severity"],
        "score": detection["score"],
        "ts": detection["ts"],
        "boundingBox": detection["boundingBox"],
        "videoId": f"{camera_id}-{int(time.time())}"
    }
    try:
        response = requests.post(
            f"{API_BASE}/api/events",
            json=event_data,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        if response.status_code == 200:
            print(f"✅ {camera_id}: 이벤트 전송 성공 - {detection['type']}")
        else:
            print(f"❌ {camera_id}: 이벤트 전송 실패 - HTTP {response.status_code}")
    except Exception as e:
        print(f"❌ {camera_id}: 이벤트 전송 오류: {e}")

def send_video_metadata(camera_id, frame):
    """비디오 메타데이터 전송"""
    metadata = {
        "cameraId": camera_id,
        "filename": f"{camera_id}_{datetime.now().strftime('%Y%m%d_%H%M%S')}.mp4",
        "duration": 5,
        "size": frame.shape[0] * frame.shape[1] * 3,
        "format": "MP4",
        "createdAt": datetime.now().strftime("%Y-%m-%dT%H:%M:%S.%f")[:-3]
    }
    try:
        response = requests.post(
            f"{API_BASE}/api/videos",
            json=metadata,
            headers={"Content-Type": "application/json"},
            timeout=5
        )
        if response.status_code == 200:
            print(f"✅ {camera_id}: 비디오 메타데이터 전송 성공")
        else:
            print(f"❌ {camera_id}: 비디오 메타데이터 전송 실패 - HTTP {response.status_code}")
    except Exception as e:
        print(f"❌ {camera_id}: 비디오 메타데이터 전송 오류: {e}")

def capture_rtsp_stream(camera_id, rtsp_url):
    """RTSP 스트림에서 프레임을 지속적으로 캡처"""
    print(f"🎥 {camera_id}: RTSP 스트림 연결 시작 - {rtsp_url}")
    
    reconnect_delay = 5  # 재연결 대기 시간 (초)
    max_reconnect_attempts = 10  # 최대 재연결 시도 횟수
    reconnect_count = 0
    
    while reconnect_count < max_reconnect_attempts:
        try:
            cap = cv2.VideoCapture(rtsp_url)
            cap.set(cv2.CAP_PROP_BUFFERSIZE, 1)
            cap.set(cv2.CAP_PROP_FPS, 10)  # FPS 설정
            cap.set(cv2.CAP_PROP_FRAME_WIDTH, 640)  # 너비 설정
            cap.set(cv2.CAP_PROP_FRAME_HEIGHT, 480)  # 높이 설정

            if not cap.isOpened():
                print(f"❌ {camera_id}: RTSP 스트림 연결 실패 (시도 {reconnect_count + 1}/{max_reconnect_attempts})")
                camera_status[camera_id] = "ERROR"
                reconnect_count += 1
                time.sleep(reconnect_delay)
                continue

            camera_status[camera_id] = "ONLINE"
            print(f"✅ {camera_id}: RTSP 스트림 연결 성공")
            reconnect_count = 0  # 성공 시 재연결 카운트 리셋

            frame_count = 0
            last_detection_time = time.time()
            consecutive_failures = 0  # 연속 실패 카운트

            while True:
                ret, frame = cap.read()
                if not ret:
                    consecutive_failures += 1
                    print(f"⚠️ {camera_id}: 프레임 읽기 실패 ({consecutive_failures}회 연속)")
                    
                    if consecutive_failures >= 5:  # 5회 연속 실패 시 재연결
                        print(f"🔄 {camera_id}: 연속 실패로 인한 재연결 시도")
                        camera_status[camera_id] = "ERROR"
                        break
                    
                    time.sleep(1)
                    continue

                consecutive_failures = 0  # 성공 시 실패 카운트 리셋
                frame_count += 1
                camera_status[camera_id] = "ONLINE"

                # YOLOv8 객체 탐지 수행 (cam-001, cam-002에서만)
                detections = []
                if camera_id in ['cam-001', 'cam-002']:
                    detections = detect_objects_yolo(frame, camera_id)
                    # 탐지된 객체가 있으면 이벤트 전송
                    for detection in detections:
                        if detection['score'] >= SCORE_THRESHOLD:
                            send_event_to_api(camera_id, detection)
                            last_detection_time = time.time()

                with camera_locks[camera_id]:
                    camera_frames[camera_id] = frame.copy()

                # 5초마다 비디오 메타데이터 전송
                if frame_count % 150 == 0:  # 30fps * 5초
                    send_video_metadata(camera_id, frame)

                time.sleep(0.01)

        except Exception as e:
            print(f"❌ {camera_id}: 스트림 처리 오류: {e}")
            camera_status[camera_id] = "ERROR"
        
        finally:
            if 'cap' in locals():
                cap.release()
        
        if reconnect_count < max_reconnect_attempts:
            print(f"🔄 {camera_id}: {reconnect_delay}초 후 재연결 시도 ({reconnect_count}/{max_reconnect_attempts})")
            time.sleep(reconnect_delay)
    
    print(f"🔴 {camera_id}: 최대 재연결 시도 횟수 초과, 스트림 연결 종료")
    camera_status[camera_id] = "ERROR"

def generate_mjpeg_stream(camera_id):
    """MJPEG 스트림 생성"""
    while True:
        with camera_locks[camera_id]:
            if camera_frames[camera_id] is not None:
                frame = camera_frames[camera_id].copy()
            else:
                # 프레임이 없으면 더미 프레임 생성
                frame = np.zeros((480, 640, 3), dtype=np.uint8)
                frame[:] = (64, 64, 64)
                cv2.putText(frame, f"Camera {camera_id}", (50, 200), 
                           cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
                cv2.putText(frame, "No Signal", (50, 250), 
                           cv2.FONT_HERSHEY_SIMPLEX, 1, (255, 255, 255), 2)
                cv2.putText(frame, "RTSP Connection Failed", (50, 300), 
                           cv2.FONT_HERSHEY_SIMPLEX, 0.7, (255, 255, 255), 2)

        # 프레임에 카메라 ID와 상태 표시
        status = camera_status.get(camera_id, "UNKNOWN")
        status_color = (0, 255, 0) if status == "ONLINE" else (0, 0, 255)
        cv2.putText(frame, f"{camera_id} - {status}", (10, 30), 
                   cv2.FONT_HERSHEY_SIMPLEX, 0.7, status_color, 2)

        # 현재 시간 표시
        current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
        cv2.putText(frame, current_time, (10, 60), 
                   cv2.FONT_HERSHEY_SIMPLEX, 0.5, (255, 255, 255), 1)

        # 프레임을 JPEG로 인코딩
        ret, buffer = cv2.imencode('.jpg', frame, [cv2.IMWRITE_JPEG_QUALITY, 90])
        if ret:
            frame_data = buffer.tobytes()
            yield (b'--frame\r\n'
                   b'Content-Type: image/jpeg\r\n'
                   b'Content-Length: ' + str(len(frame_data)).encode() + b'\r\n\r\n' + frame_data + b'\r\n')

        time.sleep(0.1)  # 10fps로 스트리밍

@app.route('/')
def index():
    """메인 페이지"""
    html = """
    <!DOCTYPE html>
    <html>
    <head>
        <title>CCTV AI Detector - YOLOv8 RTSP Demo</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 20px; background: #1a1a1a; color: white; }}
            .container {{ max-width: 1200px; margin: 0 auto; }}
            .header {{ text-align: center; margin-bottom: 30px; }}
            .status {{ background: #333; padding: 20px; border-radius: 10px; margin: 20px 0; }}
            .cameras {{ display: grid; grid-template-columns: repeat(auto-fit, minmax(400px, 1fr)); gap: 20px; }}
            .camera {{ background: #444; padding: 20px; border-radius: 10px; text-align: center; }}
            .camera h3 {{ margin-bottom: 15px; color: #4CAF50; }}
            .stream {{ margin: 20px 0; }}
            .stream img {{ max-width: 100%; border-radius: 10px; border: 2px solid #666; }}
            .online {{ color: #4CAF50; }}
            .error {{ color: #f44336; }}
            .offline {{ color: #FF9800; }}
        </style>
    </head>
    <body>
        <div class="container">
            <div class="header">
                <h1>🎥 CCTV AI Detector - YOLOv8 RTSP Demo</h1>
                <p>실시간 RTSP 스트림 처리 및 YOLOv8 객체 탐지</p>
            </div>
            
            <div class="status">
                <h2>📊 시스템 상태</h2>
                <p><strong>API 서버:</strong> <span class="online">{api_base}</span></p>
                <p><strong>탐지 임계값:</strong> {threshold}</p>
                <p><strong>YOLOv8 모델:</strong> <span class="{model_status_class}">{model_status}</span></p>
                <p><strong>RTSP 스트림:</strong> {rtsp_count}개 카메라 연결</p>
                <p><strong>YOLOv8 적용:</strong> cam-001, cam-002 (2개 카메라)</p>
                <p><strong>탐지 대상:</strong> 사람(person), 차량(car/truck/bus/motorcycle/bicycle)만</p>
                <p><strong>이벤트 전송:</strong> 사람과 차량 탐지 시에만 Spring Boot API로 전송</p>
            </div>
            
            <div class="cameras">
                <div class="camera">
                    <h3>📹 {cam_001_name} <span style="color: #4CAF50;">[YOLOv8]</span></h3>
                    <p>상태: <span class="{cam_001_status_class}">{cam_001_status}</span></p>
                    <p>RTSP: {cam_001_rtsp}</p>
                    <div class="stream">
                        <img src="/stream/cam-001" alt="Camera 1 Stream" />
                    </div>
                </div>

                <div class="camera">
                    <h3>📹 {cam_002_name} <span style="color: #4CAF50;">[YOLOv8]</span></h3>
                    <p>상태: <span class="{cam_002_status_class}">{cam_002_status}</span></p>
                    <p>RTSP: {cam_002_rtsp}</p>
                    <div class="stream">
                        <img src="/stream/cam-002" alt="Camera 2 Stream" />
                    </div>
                </div>

                <div class="camera">
                    <h3>📹 {cam_003_name} <span style="color: #FF9800;">[스트림만]</span></h3>
                    <p>상태: <span class="{cam_003_status_class}">{cam_003_status}</span></p>
                    <p>RTSP: {cam_003_rtsp}</p>
                    <div class="stream">
                        <img src="/stream/cam-003" alt="Camera 3 Stream" />
                    </div>
                </div>

                <div class="camera">
                    <h3>📹 {cam_004_name} <span style="color: #FF9800;">[스트림만]</span></h3>
                    <p>상태: <span class="{cam_004_status_class}">{cam_004_status}</span></p>
                    <p>RTSP: {cam_004_rtsp}</p>
                    <div class="stream">
                        <img src="/stream/cam-004" alt="Camera 4 Stream" />
                    </div>
                </div>
            </div>
            
            <div class="status">
                <h2>🧪 API 테스트</h2>
                <p><a href="/test" target="_blank">Spring Boot API 연결 테스트</a></p>
                <p><a href="/status" target="_blank">카메라 상태 상세 정보</a></p>
            </div>
        </div>
    </body>
    </html>
    """
    
    model_status = "로드됨" if model is not None else "더미 모드"
    model_status_class = "online" if model is not None else "error"
    
    return html.format(
        api_base=API_BASE,
        threshold=SCORE_THRESHOLD,
        model_status=model_status,
        model_status_class=model_status_class,
        rtsp_count=len(RTSP_STREAMS),
        cam_001_name="세집매 삼거리",
        cam_001_status=camera_status.get("cam-001", "UNKNOWN"),
        cam_001_status_class="online" if camera_status.get("cam-001") == "ONLINE" else "error",
        cam_001_rtsp=RTSP_STREAMS["cam-001"],
        cam_002_name="서부역 입구 삼거리",
        cam_002_status=camera_status.get("cam-002", "UNKNOWN"),
        cam_002_status_class="online" if camera_status.get("cam-002") == "ONLINE" else "error",
        cam_002_rtsp=RTSP_STREAMS["cam-002"],
        cam_003_name="역말 오거리",
        cam_003_status=camera_status.get("cam-003", "UNKNOWN"),
        cam_003_status_class="online" if camera_status.get("cam-003") == "ONLINE" else "error",
        cam_003_rtsp=RTSP_STREAMS["cam-003"],
        cam_004_name="천안로사거리",
        cam_004_status=camera_status.get("cam-004", "UNKNOWN"),
        cam_004_status_class="online" if camera_status.get("cam-004") == "ONLINE" else "error",
        cam_004_rtsp=RTSP_STREAMS["cam-004"]
    )

@app.route('/stream/<camera_id>')
def stream(camera_id):
    """MJPEG 스트림 엔드포인트"""
    if camera_id not in RTSP_STREAMS:
        return "Camera not found", 404
    
    def generate():
        for frame_data in generate_mjpeg_stream(camera_id):
            yield frame_data
    
    response = Response(
        generate(),
        mimetype='multipart/x-mixed-replace; boundary=frame'
    )
    
    # CORS 헤더 추가
    response.headers['Access-Control-Allow-Origin'] = '*'
    response.headers['Access-Control-Allow-Methods'] = 'GET, POST, OPTIONS'
    response.headers['Access-Control-Allow-Headers'] = 'Content-Type'
    
    return response

@app.route('/test')
def test_api():
    """Spring Boot API 연결 테스트"""
    try:
        response = requests.get(f"{API_BASE}/api/cameras", timeout=5)
        if response.status_code == 200:
            cameras = response.json()
            return f"""
            <html>
            <head><title>API 테스트 결과</title></head>
            <body style="font-family: Arial, sans-serif; background: #1a1a1a; color: white; padding: 20px;">
                <h1>✅ API 연결 성공!</h1>
                <p><strong>응답:</strong> {response.text[:200]}...</p>
                <p><strong>카메라 수:</strong> {len(cameras)}</p>
                <p><a href="/" style="color: #4CAF50;">← 메인으로 돌아가기</a></p>
            </body>
            </html>
            """
        else:
            return f"❌ API 오류: HTTP {response.status_code}"
    except Exception as e:
        return f"❌ 연결 실패: {e}"

@app.route('/status')
def camera_status_page():
    """카메라 상태 상세 정보"""
    status_html = """
    <html>
    <head><title>카메라 상태</title></head>
    <body style="font-family: Arial, sans-serif; background: #1a1a1a; color: white; padding: 20px;">
        <h1>📊 카메라 상태 상세 정보</h1>
        <table border="1" style="border-collapse: collapse; width: 100%; margin-top: 20px;">
            <tr style="background: #333;">
                <th style="padding: 10px;">카메라 ID</th>
                <th style="padding: 10px;">상태</th>
                <th style="padding: 10px;">RTSP URL</th>
                <th style="padding: 10px;">마지막 업데이트</th>
            </tr>
    """
    
    for cam_id, rtsp_url in RTSP_STREAMS.items():
        status = camera_status.get(cam_id, "UNKNOWN")
        status_color = "#4CAF50" if status == "ONLINE" else "#f44336" if status == "ERROR" else "#FF9800"
        
        status_html += f"""
            <tr>
                <td style="padding: 10px;">{cam_id}</td>
                <td style="padding: 10px; color: {status_color};">{status}</td>
                <td style="padding: 10px;">{rtsp_url}</td>
                <td style="padding: 10px;">{datetime.now().strftime('%Y-%m-%d %H:%M:%S')}</td>
            </tr>
        """
    
    status_html += """
        </table>
        <p style="margin-top: 20px;"><a href="/" style="color: #4CAF50;">← 메인으로 돌아가기</a></p>
    </body>
    </html>
    """
    return status_html

if __name__ == '__main__':
    print("🚀 CCTV AI Detector YOLOv8 RTSP Demo 시작 중...")
    print(f"📹 RTSP 스트림: {len(RTSP_STREAMS)}개 카메라")
    print(f"🌐 API 서버: {API_BASE}")
    print(f"🎯 탐지 임계값: {SCORE_THRESHOLD}")
    print(f"🎯 탐지 대상: 사람(person), 차량(car/truck/bus/motorcycle/bicycle)만")
    print(f"📡 이벤트 전송: 사람과 차량 탐지 시에만 API 전송")
    print(f"🚀 YOLOv8n 모델: 가장 가벼운 최신 모델 (6.7MB)")
    
    # YOLOv8 모델 로드
    model_loaded = load_yolo_model()
    
    # RTSP 스트림 처리 스레드 시작
    for camera_id, rtsp_url in RTSP_STREAMS.items():
        thread = threading.Thread(
            target=capture_rtsp_stream,
            args=(camera_id, rtsp_url),
            daemon=True
        )
        thread.start()
        print(f"🔄 {camera_id} RTSP 스트림 처리 스레드 시작")

    print("✅ 모든 RTSP 스트림 처리 스레드가 시작되었습니다.")
    print("🌐 웹 인터페이스: http://localhost:5001")
    print("📡 MJPEG 스트림: http://localhost:5001/stream/<camera_id>")
    print("🧪 API 테스트: http://localhost:5001/test")
    print("📊 상태 정보: http://localhost:5001/status")
    print("\n💡 Spring Boot를 실행한 후 이 페이지에서 실시간 YOLOv8 객체 탐지를 확인하세요!")

    # Flask 앱 실행
    app.run(host='0.0.0.0', port=5001, debug=False, threaded=True)
