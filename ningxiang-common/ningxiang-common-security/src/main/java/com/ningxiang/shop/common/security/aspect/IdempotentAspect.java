package com.ningxiang.shop.common.security.aspect;

import com.ningxiang.shop.common.exception.NingxiangException;
import com.ningxiang.shop.common.security.annotation.Idempotent;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 接口防重幂等切面
 */
@Aspect
@Component
public class IdempotentAspect {

    @Autowired(required = false)
    private StringRedisTemplate stringRedisTemplate;

    private final ExpressionParser parser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer discoverer = new DefaultParameterNameDiscoverer();

    @Around("@annotation(idempotent)")
    public Object around(ProceedingJoinPoint joinPoint, Idempotent idempotent) throws Throwable {
        if (stringRedisTemplate == null) {
            return joinPoint.proceed();
        }

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 1. 解析 Spring EL 动态 Key
        EvaluationContext context = new MethodBasedEvaluationContext(joinPoint.getTarget(), method, joinPoint.getArgs(), discoverer);
        String resolvedKey;
        try {
            resolvedKey = parser.parseExpression(idempotent.key()).getValue(context, String.class);
        } catch (Exception e) {
            // 如果解析 SpEL 异常，则使用 key 字符串本身作为常量
            resolvedKey = idempotent.key();
        }

        String redisKey = idempotent.prefix() + resolvedKey;

        // 2. 利用 SETNX 占位进行幂等判定 (PROCESSING 状态)
        Boolean success = stringRedisTemplate.opsForValue().setIfAbsent(
                redisKey,
                "PROCESSING",
                idempotent.expireTime(),
                idempotent.timeUnit()
        );

        if (Boolean.FALSE.equals(success)) {
            // 占位失败，说明重复请求或正在处理中
            throw new NingxiangException(idempotent.message());
        }

        try {
            // 3. 执行业务方法
            Object result = joinPoint.proceed();
            
            // 4. 业务执行成功，更新 Redis 状态为 SUCCESS（保持 TTL 防止重复请求）
            stringRedisTemplate.opsForValue().set(
                    redisKey,
                    "SUCCESS",
                    idempotent.expireTime(),
                    idempotent.timeUnit()
            );
            return result;
        } catch (Throwable throwable) {
            // 5. 业务异常，释放 Redis 锁（删除 key），允许发起重试
            stringRedisTemplate.delete(redisKey);
            throw throwable;
        }
    }
}
