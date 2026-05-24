package com.ops.app.courseregistration.course.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ops.app.courseregistration.course.dto.CourseResponseDto;
import com.ops.app.courseregistration.course.service.CourseService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<Page<CourseResponseDto>> searchCourses(
            @RequestParam(name = "name", required = false) String keyword,
            @PageableDefault(page = 0, size = 20, sort = "courseCode", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<CourseResponseDto> responses = courseService.searchCoursesByName(keyword, pageable);
        return ResponseEntity.ok(responses);
    }
}