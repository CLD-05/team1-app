package com.ops.app.courseregistration.enrollment.dto;

import java.time.LocalDateTime;
import java.util.List;

//예시: EnrollmentResponseDTO
public record EnrollmentResponseDTO(
 Long enrollmentId,
 String courseCode,
 String courseName,
 int credits,
 LocalDateTime createdAt
) {}