package com.cctv.controlcenter.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TestController {

    @GetMapping("/test-stream")
    public String testStream() {
        return "test-stream";
    }
}
