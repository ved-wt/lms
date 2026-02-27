package com.learn.lms.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Aspect
@Component
public class LoggingAspect {

    // Pointcut: Target all @RestController methods under /api/**
    @Pointcut(
        "within(@org.springframework.web.bind.annotation.RestController *) && execution(* com.learn.lms.controller..*(..))"
    )
    public void apiEndpoints() {}

    // Around advice: Run before and after the target method
    @Around("apiEndpoints()")
    public Object logAroundApiCalls(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = (
            (ServletRequestAttributes) RequestContextHolder.getRequestAttributes()
        ).getRequest();
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Log request
        log.info("AOP Request: Method={}, URI={}, Args={}", method, uri, joinPoint.getArgs());

        // Proceed with the controller method execution
        Object response = joinPoint.proceed();

        // Log response
        log.info("AOP Response: Method={}, URI={}, Response={}", method, uri, response);

        return response;
    }
}
