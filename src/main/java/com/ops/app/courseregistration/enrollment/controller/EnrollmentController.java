package com.ops.app.courseregistration.enrollment.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ops.app.courseregistration.enrollment.dto.EnrollmentPageResponseDTO;
import com.ops.app.courseregistration.enrollment.dto.EnrollmentResponseDTO;
import com.ops.app.courseregistration.enrollment.service.EnrollmentService;
import com.ops.app.courseregistration.security.StudentPrincipal;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    // 1. 내 수강 내역 조회 (GET)
    @GetMapping
    public ResponseEntity<EnrollmentPageResponseDTO> getMyEnrollments(@AuthenticationPrincipal StudentPrincipal principal) {
        Long studentId = principal.getStudentId();
        
        // 서비스에서 DTO 리스트를 받아옴
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getMyEnrollments(studentId);
        int totalCredits = enrollmentService.getTotalCredits(studentId);
        
        // 전체 데이터를 담을 DTO 생성
        EnrollmentPageResponseDTO response = new EnrollmentPageResponseDTO(
            principal.getName(),
            totalCredits, // 서비스에 이 메서드가 있어야 함
            enrollments
        );
        
        return ResponseEntity.ok(response);
    }

    // 2. 수강 신청 (POST)
    @PostMapping
    public ResponseEntity<?> enroll(@RequestBody Map<String, Long> request, 
                                    @AuthenticationPrincipal StudentPrincipal principal) {
        Long courseId = request.get("courseId");
        enrollmentService.enroll(principal.getStudentId(), courseId);
        return ResponseEntity.ok("수강신청이 완료되었습니다.");
    }

    // 3. 수강 취소 (DELETE)
    @DeleteMapping("/{enrollmentId}")
    public ResponseEntity<?> cancel(@PathVariable Long enrollmentId, 
                                    @AuthenticationPrincipal StudentPrincipal principal) {
        enrollmentService.cancel(enrollmentId, principal.getStudentId());
        return ResponseEntity.ok("수강이 취소되었습니다.");
    }
}