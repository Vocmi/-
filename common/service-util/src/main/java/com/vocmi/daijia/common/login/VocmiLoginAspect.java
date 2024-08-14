package com.vocmi.daijia.common.login;

import cn.hutool.core.util.StrUtil;
import com.vocmi.daijia.common.constant.RedisConstant;
import com.vocmi.daijia.common.execption.VocmiException;
import com.vocmi.daijia.common.result.ResultCodeEnum;
import com.vocmi.daijia.common.util.AuthContextHolder;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Author vocmi
 * @Email 2686782542@qq.com
 * @Date 2024-08-14
 */
@Component
@Aspect
public class VocmiLoginAspect {

    @Resource
    private RedisTemplate redisTemplate;

    @Around("execution(* com.vocmi.daijia.*.controller.*.*(..)) && @annotation(vocmiLogin)")
    public Object login(ProceedingJoinPoint proceedingJoinPoint,VocmiLogin vocmiLogin) throws Throwable {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        HttpServletRequest request = attributes.getRequest();

        String token = request.getHeader("token");
        if(StrUtil.isBlank(token)){
            throw  new VocmiException(ResultCodeEnum.LOGIN_AUTH);
        }

        String customerId = (String) redisTemplate.opsForValue().get(RedisConstant.USER_LOGIN_KEY_PREFIX + token);
        if (StrUtil.isNotBlank(customerId)){
            AuthContextHolder.setUserId(Long.parseLong(customerId));
        }

        return proceedingJoinPoint.proceed();
    }
}
