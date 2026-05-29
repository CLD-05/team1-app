package com.ops.app.courseregistration.enrollment.dto;

import java.util.List;

public record EnrollmentPageResponseDTO(
	    String studentName,
	    int totalCredits,
	    List<EnrollmentResponseDTO> enrollments
	) {}