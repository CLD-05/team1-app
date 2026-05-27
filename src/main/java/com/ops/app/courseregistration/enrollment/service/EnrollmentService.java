package com.ops.app.courseregistration.enrollment.service;

import com.ops.app.courseregistration.course.entity.Course;
import com.ops.app.courseregistration.course.repository.CourseRepository;
import com.ops.app.courseregistration.enrollment.entity.Enrollment;
import com.ops.app.courseregistration.enrollment.repository.EnrollmentRepository;
import com.ops.app.courseregistration.global.exception.BusinessException;
import com.ops.app.courseregistration.global.exception.ErrorCode;
import com.ops.app.courseregistration.student.entity.Student;
import com.ops.app.courseregistration.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentPeriodValidator periodValidator;


     // 내 수강 내역 조회
    @Transactional(readOnly = true)
    public List<Enrollment> getMyEnrollments(Long studentId) {
        return enrollmentRepository.findByStudentIdWithCourse(studentId);
    }


    // 총 신청 학점 계산
    @Transactional(readOnly = true)
    public int getTotalCredits(Long studentId) {
        return getMyEnrollments(studentId).stream()
                .mapToInt(e -> e.getCourse().getCredits())
                .sum();
    }

    /**
     * 수강신청
     *
     * [트랜잭션 흐름]
     * Step 1. UPDATE courses SET current_enrollment+1 WHERE current_enrollment < capacity
     *         → 0 rows affected : 정원 초과 → COURSE_FULL (롤백)
     *         → 1 rows affected : 자리 확보, 다음 단계 진행
     *
     * Step 2. INSERT enrollments
     *         → UNIQUE 위반 : 중복 신청 → DUPLICATE_ENROLLMENT (롤백 → Step1 UPDATE도 취소)
     *         → 성공 : COMMIT
     *
     * [동시성 처리]
     * InnoDB row-level lock이 Step1 UPDATE를 직렬화함
     * A, B가 동시에 마지막 1자리에 신청하면 한 명만 UPDATE 성공, 나머지는 0 rows affected → COURSE_FULL
     */
    @Transactional
    public void enroll(Long studentId, Long courseId) {
        periodValidator.validate();

        // 강의 존재 여부 확인 — 없으면 COURSE_NOT_FOUND
        courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        // Step 1. 정원 체크 + 카운터 원자적 증가
        int updated = courseRepository.incrementEnrollmentCount(courseId);
        if (updated == 0) {
            throw new BusinessException(ErrorCode.COURSE_FULL);
        }

        // Step 2. 프록시 참조로 불필요한 SELECT 없이 INSERT
        Student student = studentRepository.getReferenceById(studentId);
        Course course   = courseRepository.getReferenceById(courseId);

        try {
            enrollmentRepository.saveAndFlush(new Enrollment(student, course));
        } catch (DataIntegrityViolationException e) {
            // UNIQUE 위반 (student_id + course_id 중복) → 트랜잭션 롤백
            // → Step1의 current_enrollment+1도 함께 취소됨
            throw new BusinessException(ErrorCode.DUPLICATE_ENROLLMENT);
        }
    }

    /**
     * 수강 취소
     *
     * [트랜잭션 흐름]
     * Step 1. enrollment_id AND student_id로 조회 → 소유자 검증 + 존재 확인 동시 처리
     *         → 없으면 ENROLLMENT_NOT_FOUND (존재하지 않거나 다른 학생 것, 구분하지 않음)
     *
     * Step 2. DELETE enrollments
     *
     * Step 3. UPDATE courses SET current_enrollment-1 WHERE current_enrollment > 0
     *         → 음수 방어 조건 포함
     */
    @Transactional
    public void cancel(Long enrollmentId, Long studentId) {
        // Step 1. 소유자 검증 — 본인 것이 아니면 404
        Enrollment enrollment = enrollmentRepository
                .findByEnrollmentIdAndStudentStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        Long courseId = enrollment.getCourse().getCourseId();

        // Step 2. DELETE — flush로 즉시 반영 후 Step3 실행
        enrollmentRepository.delete(enrollment);
        enrollmentRepository.flush();

        // Step 3. 카운터 감소 (current_enrollment > 0 조건으로 음수 방어)
        courseRepository.decrementEnrollmentCount(courseId);
    }
}