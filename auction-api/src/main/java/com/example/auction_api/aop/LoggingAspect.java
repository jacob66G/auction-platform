package com.example.auction_api.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("within(com.example.auction_api.service..*)")
    public void serviceMethods() {}

    @Around("serviceMethods()")
    public Object logWrite(ProceedingJoinPoint joinPoint) throws Throwable {
        String signature = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        log.info("▶ Entering: {} with args={}", signature, args);

        Object result = joinPoint.proceed();

        if (result instanceof Collection<?>) {
            log.info("◀ Completed {} returned collection size={}", signature, ((Collection<?>) result).size());
        } else {
            log.info("◀ Completed {} returned={}", signature, result);
        }

        return result;
    }
}
