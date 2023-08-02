package com.PDFExtraction.demo.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Aspect
@Slf4j
@Component
public class GeneralInterceptorAspect {

    Logger logger= LoggerFactory.getLogger(GeneralInterceptorAspect.class);
    @Pointcut("execution(* com.PDFExtraction.demo.controller.*.*(..))")
    public void loggingPointCut(){}

    @Before("loggingPointCut()")
    public void before(JoinPoint joinPoint){
        log.info("Before method invoked::"+joinPoint.getSignature());
    }

    @After("loggingPointCut()")
    public void after(JoinPoint joinPoint){
        log.info("After method invoked::"+joinPoint.getSignature());
    }
//    @AfterReturning(value = "execution(* com.PDFExtraction.demo.controller.*.*(..))",returning = "String")
//    public void after(JoinPoint joinPoint, String string){
//        log.info("After method invoked::"+string);
//    }

    @AfterThrowing(value = "execution(* com.PDFExtraction.demo.controller.*.*(..))", throwing = "e")
    public void after(JoinPoint joinPoint, Exception e){
        log.info("After method invoked::"+e.getMessage());
    }

    @Around("@annotation(com.PDFExtraction.demo.aspect.TrackExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = proceedingJoinPoint.proceed();
        long executionTime = System.currentTimeMillis() - startTime;
        logger.info("{} executed in {}ms",proceedingJoinPoint.getSignature(),executionTime);
        return proceed;
    }

}
