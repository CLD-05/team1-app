package com.ops.app.courseregistration.enrollment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EnrollmentPageController {
    
    // 이 메서드가 /enrollments 요청을 받아 enrollments.html 파일을 응답합니다.
    @GetMapping("/enrollments")
    public String myEnrollmentsPage() {
        return "enrollments"; 
    }
}