package com.cctv.controlcenter.web;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.service.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class CameraViewController {

    private static final Logger log = LoggerFactory.getLogger(CameraViewController.class);

    private final CameraService cameraService;

    public CameraViewController(CameraService cameraService) {
        this.cameraService = cameraService;
    }

    @GetMapping("/cameras/{id}")
    public String cameraDetail(@PathVariable String id, Model model) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID

        log.info("카메라 상세 페이지 요청: id={} (사용자: {})", id, userId);

        try {
            Camera camera = cameraService.getCameraById(id, userId);
            model.addAttribute("camera", camera);
            log.info("카메라 정보 로드 완료: {}", camera.getName());
        } catch (Exception e) {
            log.error("카메라 정보 조회 실패: {}", id, e);
            model.addAttribute("error", "카메라를 찾을 수 없습니다");
        }

        return "camera-detail";
    }
}
