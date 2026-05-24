package com.ops.app.courseregistration.course.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ops.app.courseregistration.course.entity.Course;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

	Page<Course> findByCourseNameStartingWith(String courseName, Pageable pageable);
}
