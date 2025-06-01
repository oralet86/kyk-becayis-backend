package com.sazark.kykbecayis.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("within(@org.springframework.stereotype.Service *) || within(@org.springframework.web.bind.annotation.RestController *)")
    public void applicationBeans() {}

    @Before("applicationBeans()")
    public void logBefore(JoinPoint joinPoint) {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = methodSig.getName();
        String args = Arrays.toString(joinPoint.getArgs());

        logger.info("Entering {}.{}() with arguments {}", className, method, args);
    }

    @AfterReturning(pointcut = "applicationBeans()", returning = "result")
    public void logAfter(JoinPoint joinPoint, Object result) {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = methodSig.getName();

        logger.info("Exiting {}.{}() with result: {}", className, method, result);
    }

    @AfterThrowing(pointcut = "applicationBeans()", throwing = "ex")
    public void logExceptions(JoinPoint joinPoint, Throwable ex) {
        MethodSignature methodSig = (MethodSignature) joinPoint.getSignature();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String method = methodSig.getName();

        logger.error("Exception in {}.{}(): {}", className, method, ex.getMessage(), ex);
    }
}