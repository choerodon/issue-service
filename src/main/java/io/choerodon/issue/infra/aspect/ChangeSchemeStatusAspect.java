package io.choerodon.issue.infra.aspect;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.infra.enums.StateMachineSchemeStatus;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author shinan.chen
 * @since 2018/11/22
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class ChangeSchemeStatusAspect {
    private static final Logger logger = LoggerFactory.getLogger(ChangeSchemeStatusAspect.class);
    @Autowired
    private StateMachineSchemeMapper schemeMapper;
    @Autowired
    private StateMachineSchemeService stateMachineSchemeService;

    @Pointcut("@annotation(io.choerodon.issue.infra.annotation.ChangeSchemeStatus)")
    public void updateStatusPointcut() {
        throw new UnsupportedOperationException();
    }

    @Around("updateStatusPointcut()")
    public Object interceptor(ProceedingJoinPoint pjp) {
        // 下面两个数组中，参数值和参数名的个数和位置是一一对应的。
        Object[] args = pjp.getArgs();
        String[] argNames = ((MethodSignature) pjp.getSignature()).getParameterNames();
        Long schemeId = null;
        for (int i = 0; i < argNames.length; i++) {
            if (argNames[i].equals("schemeId")) {
                schemeId = Long.valueOf(args[i] + "");
            }
        }
        logger.info("schemeId:{}", schemeId);
        StateMachineScheme scheme = schemeMapper.selectByPrimaryKey(schemeId);
        if (scheme == null) {
            throw new CommonException("error.scheme.notFound");
        }
        if (scheme.getStatus().equals(StateMachineSchemeStatus.ACTIVE)) {
            scheme.setStatus(StateMachineSchemeStatus.DRAFT);
            stateMachineSchemeService.updateOptional(scheme, "status");
        }

        try {
            return pjp.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return null;
    }
}
