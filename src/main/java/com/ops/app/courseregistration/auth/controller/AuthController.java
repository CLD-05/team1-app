package com.ops.app.courseregistration.auth.controller;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ops.app.courseregistration.auth.dto.LoginForm;
import com.ops.app.courseregistration.auth.jwt.JwtCookieUtil;
import com.ops.app.courseregistration.auth.service.AuthService;
import com.ops.app.courseregistration.global.exception.BusinessException;
import com.ops.app.courseregistration.security.StudentPrincipal;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RestController // 1. 모든 반환값이 JSON이 됩니다.
@RequestMapping("/api/auth") // 2. API 경로를 그룹화합니다.
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    // 3. 더 이상 페이지를 반환하지 않습니다. (페이지는 별도 정적 파일로 처리)
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm form) {
        try {
            String token = authService.login(form.email(), form.password());
            return ResponseEntity.ok(Collections.singletonMap("token", token));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        jwtCookieUtil.clearAuthCookie(response);
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok("logged out");
    }
    
    @GetMapping("/info")
    public ResponseEntity<?> getStudentInfo(Authentication authentication) {
        StudentPrincipal principal = (StudentPrincipal) authentication.getPrincipal();
        
        // DB 조회 없이 principal 안에 있는 이름 바로 반환!
        return ResponseEntity.ok(Map.of("name", principal.getName()));
    }
}