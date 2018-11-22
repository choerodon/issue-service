package io.choerodon.issue.infra.aspect;

import io.choerodon.issue.api.service.StateMachineSchemeService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shinan.chen
 * @since 2018/11/22
 */
@Aspect
@Component
public class ChangeSchemeStatusAspect {
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;

    @Pointcut("@annotation(io.choerodon.issue.infra.annotation.ChangeSchemeStatus)")
    public void updateStatusPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("updateStatusPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        return null;
    }
}
