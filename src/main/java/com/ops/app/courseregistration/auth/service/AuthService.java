package com.ops.app.courseregistration.auth.service;

import com.ops.app.courseregistration.auth.jwt.JwtUtil;
import com.ops.app.courseregistration.global.exception.BusinessException;
import com.ops.app.courseregistration.global.exception.ErrorCode;
import com.ops.app.courseregistration.student.entity.Student;
import com.ops.app.courseregistration.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String email, String password) {
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password, student.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
        }

        return jwtUtil.generateToken(student.getStudentId());
    }
    
    public String getName(String email) {
        // 1. 이메일로 학생 엔티티를 DB에서 조회합니다.
        Student student = studentRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        
        // 2. 엔티티에서 이름을 반환합니다.
        return student.getName();
    }
    
    public String getNameById(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.STUDENT_NOT_FOUND));
        return student.getName();
    }
}
