package com.cctv.controlcenter.web;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.service.CameraService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.UUID;

@Controller
public class HomeController {
    
    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    
    private final CameraService cameraService;
    
    public HomeController(CameraService cameraService) {
        this.cameraService = cameraService;
    }
    
    @GetMapping("/")
    public String home(Model model) {
        // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
        UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001"); // data.sql의 사용자 ID
        
        log.info("홈 페이지 요청 (사용자: {})", userId);
        
        try {
            List<Camera> cameras = cameraService.getCamerasByUserId(userId);
            model.addAttribute("cameras", cameras);
            log.info("카메라 {}개 로드됨", cameras.size());
        } catch (Exception e) {
            log.error("카메라 목록 조회 실패", e);
            model.addAttribute("cameras", List.of());
        }
        
        return "index";
    }
}
