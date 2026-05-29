package com.ops.app.courseregistration.course.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ops.app.courseregistration.course.dto.CourseResponseDto;
import com.ops.app.courseregistration.course.service.CourseService;
import com.ops.app.courseregistration.enrollment.service.EnrollmentPeriodValidator;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentPeriodValidator periodValidator;

    @GetMapping("/courses")
    public String searchCourses(
            @RequestParam(name = "name", required = false) String keyword,
            @PageableDefault(page = 0, size = 20, sort = "courseCode", direction = Sort.Direction.ASC) Pageable pageable,
            Model model
    ) {
        Page<CourseResponseDto> courses = courseService.searchCoursesByName(keyword, pageable);
        
     // 로그 추가!
        System.out.println("데이터 개수 확인: " + courses.getContent().size());
     // List를 꺼내서 개별 요소를 확인합니다.
        if (!courses.getContent().isEmpty()) {
            CourseResponseDto firstCourse = courses.getContent().get(0); // 타입을 명시적으로 지정
            System.out.println("첫 번째 데이터 이름: " + firstCourse.courseName()); // record 타입은 get 대신 이름 그대로 호출
        }
      
        model.addAttribute("courses", courses.getContent());
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", courses.getNumber()); 
        model.addAttribute("totalPages", courses.getTotalPages());
        model.addAttribute("totalElements", courses.getTotalElements());
        model.addAttribute("enrollmentOpen", periodValidator.isOpen());
        
        return "courses";
    }
    
 // 2. [추가] API 방식 (JSON 데이터를 원할 때 호출)
    @GetMapping("/api/courses")
    @ResponseBody
    public Page<CourseResponseDto> getCoursesApi(
            @RequestParam(name = "name", required = false) String keyword,
            @PageableDefault(page = 0, size = 20, sort = "courseCode", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        // 기존 서비스 메서드를 그대로 재사용합니다.
        return courseService.searchCoursesByName(keyword, pageable);
    }
}