package com.cctv.controlcenter.service;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.repository.CameraRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CameraService {
    
    private static final Logger log = LoggerFactory.getLogger(CameraService.class);
    
    private final CameraRepository cameraRepository;
    
    public CameraService(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }
    
    public List<Camera> getCamerasByUserId(UUID userId) {
        log.info("사용자 ID {}의 카메라 목록 조회", userId);
        List<Camera> cameras = cameraRepository.findByUserId(userId);
        log.info("카메라 {}개 조회됨", cameras.size());
        return cameras;
    }
    
    public Camera getCameraById(String cameraId, UUID userId) {
        log.info("카메라 ID {} 조회 (사용자 ID: {})", cameraId, userId);
        
        Camera camera = cameraRepository.findById(cameraId)
                .orElseThrow(() -> new IllegalArgumentException("카메라를 찾을 수 없습니다: " + cameraId));
        
        // 사용자 소유권 확인
        if (!camera.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("해당 카메라에 대한 접근 권한이 없습니다");
        }
        
        return camera;
    }
}
