package com.ops.app.courseregistration.course.dto;

import com.ops.app.courseregistration.course.entity.Course;

public record CourseResponseDto(
    Long courseId,
    String courseCode,
    String courseName,
    int capacity
) {
    // 엔티티를 DTO로 깔끔하게 변환해 주는 편의 메서드
    public static CourseResponseDto from(Course course) {
        return new CourseResponseDto(
            course.getCourseId(),
            course.getCourseCode(),
            course.getCourseName(),
            course.getCapacity()
        );
    }
}