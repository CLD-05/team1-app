package com.ops.app.courseregistration.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public String handleBadCredentials(BadCredentialsException e) {
        log.info("Login failed: {}", e.getMessage());
        return "redirect:/login?error=true";
    }
}
