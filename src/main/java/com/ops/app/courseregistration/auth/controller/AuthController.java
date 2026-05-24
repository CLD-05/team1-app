package com.ops.app.courseregistration.auth.controller;

import com.ops.app.courseregistration.auth.dto.LoginForm;
import com.ops.app.courseregistration.auth.jwt.JwtCookieUtil;
import com.ops.app.courseregistration.auth.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtCookieUtil jwtCookieUtil;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginForm form, HttpServletResponse response) {
        String token = authService.login(form.email(), form.password());
        jwtCookieUtil.setAuthCookie(response, token);
        return "redirect:/";
    }

    @PostMapping("/logout")
    public String logout(HttpServletResponse response) {
        jwtCookieUtil.clearAuthCookie(response);
        SecurityContextHolder.clearContext();
        return "redirect:/login";
    }
}
