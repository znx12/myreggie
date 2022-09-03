package com.example.reggie_take_out.common.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
@Slf4j
public class LogAspect {

    @Pointcut("@annotation(com.example.reggie_take_out.common.aop.LogAnnotation)")
    public void pt(){}

    @Around("pt()")
    public Object log(ProceedingJoinPoint point) throws Throwable{
        Long beginTime=System.currentTimeMillis();
        Object result=point.proceed();
        long time=System.currentTimeMillis()-beginTime;
        recordLog(point,time);
        return result;
    }

    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);
        log.info("=====================log start================================");
        log.info("module:{}", logAnnotation.module());
        log.info("operation:{}", logAnnotation.operation());

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        log.info("request method:{}", className + "." + methodName + "()");

//        //请求的参数
        Object[] args = joinPoint.getArgs();
        String params=null;
        //log.info(String.valueOf(args.length));
        if(args.length!=0) {
           params = JSON.toJSONString(args[0]);
        }
        log.info("params:{}", params);
        log.info("excute time : {} ms", time);
        log.info("=====================log end================================");
    }


}
