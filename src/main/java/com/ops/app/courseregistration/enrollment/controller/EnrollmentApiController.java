package com.ops.app.courseregistration.enrollment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.app.courseregistration.enrollment.service.EnrollmentService;
import com.ops.app.courseregistration.security.StudentPrincipal;

@RestController // @ResponseBody가 기본으로 포함됨
@RequestMapping("/api") // 주소를 구분하기 위해 /api를 앞에 붙입니다
public class EnrollmentApiController {

    private final EnrollmentService enrollmentService;

    public EnrollmentApiController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping("/enrollments")
    public ResponseEntity<String> enroll(@RequestParam Long courseId,
    		@RequestParam(required = false) Long studentId) {
        // principal이 null이면 에러를 내지 말고, 테스트용 ID를 쓰거나 예외를 발생시키기
    	Long targetStudentId = (studentId != null) ? studentId : 1L;
        enrollmentService.enroll(targetStudentId, courseId);
        return ResponseEntity.ok("Success");
}
}