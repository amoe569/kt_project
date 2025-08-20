package com.cctv.controlcenter.web;

import com.cctv.controlcenter.domain.Camera;
import com.cctv.controlcenter.domain.Event;
import com.cctv.controlcenter.service.CameraService;
import com.cctv.controlcenter.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
public class EventWebController {
    
    private static final Logger log = LoggerFactory.getLogger(EventWebController.class);
    
    private final EventService eventService;
    private final CameraService cameraService;
    
    public EventWebController(EventService eventService, CameraService cameraService) {
        this.eventService = eventService;
        this.cameraService = cameraService;
    }
    
    @GetMapping("/events")
    public String eventsPage(
            @RequestParam(defaultValue = "") String cameraId,
            @RequestParam(defaultValue = "") String eventType,
            @RequestParam(defaultValue = "") String startDate,
            @RequestParam(defaultValue = "") String endDate,
            @RequestParam(defaultValue = "1") int severity,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            Model model) {
        
        log.info("이벤트 관리 페이지 요청 - 카메라: {}, 타입: {}, 시작일: {}, 종료일: {}, 심각도: {}, 페이지: {}",
                cameraId, eventType, startDate, endDate, severity, page);
        
        try {
            // TODO: 실제 사용자 ID를 보안 컨텍스트에서 가져와야 함
            UUID userId = UUID.fromString("550e8400-e29b-41d4-a716-446655440001");
            
            // 카메라 목록 조회
            List<Camera> cameras = cameraService.getCamerasByUserId(userId);
            model.addAttribute("cameras", cameras);
            
            // 페이징 설정
            Pageable pageable = PageRequest.of(page, size);
            
            // 날짜 파싱
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            
            try {
                if (!startDate.isEmpty()) {
                    startDateTime = LocalDateTime.parse(startDate + "T00:00:00");
                }
                if (!endDate.isEmpty()) {
                    endDateTime = LocalDateTime.parse(endDate + "T23:59:59");
                }
            } catch (Exception e) {
                log.warn("날짜 파싱 오류: {}", e.getMessage());
            }
            
            // 이벤트 조회 (필터링 적용)
            Page<Event> events = eventService.getEventsWithFilters(
                    cameraId.isEmpty() ? null : cameraId,
                    eventType.isEmpty() ? null : eventType,
                    startDateTime,
                    endDateTime,
                    severity,
                    pageable
            );
            
            model.addAttribute("events", events);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", events.getTotalPages());
            model.addAttribute("totalElements", events.getTotalElements());
            
            // 검색 조건 유지
            model.addAttribute("searchCameraId", cameraId);
            model.addAttribute("searchEventType", eventType);
            model.addAttribute("searchStartDate", startDate);
            model.addAttribute("searchEndDate", endDate);
            model.addAttribute("searchSeverity", severity);
            
            // 사용자 정보
            model.addAttribute("userEmail", "admin@example.com");
            
        } catch (Exception e) {
            log.error("이벤트 관리 페이지 로드 중 오류 발생", e);
            model.addAttribute("error", "이벤트 데이터를 불러오는 중 오류가 발생했습니다: " + e.getMessage());
        }
        
        return "events";
    }
}
