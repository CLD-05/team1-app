package com.ops.app.courseregistration.auth.jwt;

import com.ops.app.courseregistration.security.StudentPrincipal;
import com.ops.app.courseregistration.student.repository.StudentRepository;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final JwtCookieUtil jwtCookieUtil;
    private final StudentRepository studentRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        
        String path = request.getRequestURI();
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/") 
            || path.equals("/login") || path.equals("/api/auth/login") || path.equals("/favicon.ico")) {
            chain.doFilter(request, response);
            return;
        }

        // [수정된 부분] 1. 헤더에서 토큰을 먼저 찾습니다.
        String bearerToken = request.getHeader("Authorization");
        String token = null;

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            token = bearerToken.substring(7); // "Bearer " 뒷부분의 토큰 값만 추출
        } else {
            // [수정된 부분] 2. 헤더에 없다면 기존처럼 쿠키에서 찾습니다.
            Optional<String> tokenOpt = jwtCookieUtil.extractToken(request);
            if (tokenOpt.isPresent()) {
                token = tokenOpt.get();
            }
        }

        // 토큰이 없거나 이미 인증된 경우 필터 체인을 계속 진행
        if (token == null || SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            // [수정된 부분] 추출한 token 변수 사용
            Long studentId = jwtUtil.extractStudentId(token);
            String name = studentRepository.findById(studentId)
                    .map(s -> s.getName())
                    .orElse(null);
            
            StudentPrincipal principal = new StudentPrincipal(studentId, name);
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(principal, null, Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException e) {
            log.debug("Invalid JWT token: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        chain.doFilter(request, response);
    }
}
