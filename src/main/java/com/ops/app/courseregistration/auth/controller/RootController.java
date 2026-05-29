package com.ops.app.courseregistration.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {
    @GetMapping("/")
    public String home() {
        // RootController가 이미 루트를 처리하고 있으니, 
        // 여기서 index.html을 반환하거나 forward 하도록 하세요.
        return "redirect:/index.html"; 
    }
}