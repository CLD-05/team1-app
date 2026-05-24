package com.ops.app.courseregistration.course.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ops.app.courseregistration.course.dto.CourseResponseDto;
import com.ops.app.courseregistration.course.repository.CourseRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<CourseResponseDto> searchCoursesByName(String keyword, Pageable pageable) {
        // 1. 만약 검색어가 비어있거나 null이면 빈 목록을 반환하거나 전체 조회를 할 수 있습니다. (여기서는 빈 목록 반환)
        if (keyword == null || keyword.isBlank()) {
            return Page.empty(pageable);
        }

        // 2. 레포지토리를 통해 'keyword%' 조건으로 데이터베이스에서 강의 엔티티 목록을 조회합니다.
        return courseRepository.findByCourseNameStartingWith(keyword, pageable)
                .map(CourseResponseDto::from);
    }
}