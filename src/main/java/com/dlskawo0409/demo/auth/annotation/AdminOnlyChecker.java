package com.dlskawo0409.demo.auth.annotation;

import com.dlskawo0409.demo.auth.domain.Accessor;
import org.springframework.stereotype.Component;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class AdminOnlyChecker {

    @Before("@annotation(hanglog.auth.AdminOnly)")
    public void check(final JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .filter(Accessor.class::isInstance)
                .map(Accessor.class::cast)
                .filter(Accessor::isAdmin)
                .findFirst()
                .orElseThrow(() -> new AdminException(INVALID_ADMIN_AUTHORITY));
    }
}