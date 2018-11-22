package io.choerodon.issue.infra.aspect;

import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.domain.StateMachineScheme;
import io.choerodon.issue.infra.enums.StateMachineSchemeStatus;
import io.choerodon.issue.infra.mapper.StateMachineSchemeMapper;
import org.apache.ibatis.javassist.ClassClassPath;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;
import org.apache.ibatis.javassist.bytecode.CodeAttribute;
import org.apache.ibatis.javassist.bytecode.LocalVariableAttribute;
import org.apache.ibatis.javassist.bytecode.MethodInfo;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

/**
 * @author shinan.chen
 * @since 2018/11/22
 */
@Aspect
@Component
@Transactional(rollbackFor = Exception.class)
public class ChangeSchemeStatusAspect {
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
        Long schemeId = getSchemeId(pjp);
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

    private Long getSchemeId(ProceedingJoinPoint pjp) {
        try {
            String classType = pjp.getTarget().getClass().getName();
            Class<?> clazz = Class.forName(classType);
            String clazzName = clazz.getName();
            String methodName = pjp.getSignature().getName();
            Object[] args = pjp.getArgs();
            //获取参数名称和值
            Map<String, Object> nameAndArgs = getFieldsName(this.getClass(), clazzName, methodName, args);
            Long schemeId = (Long) nameAndArgs.get("schemeId");
            return schemeId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Map<String, Object> getFieldsName(Class cls, String clazzName, String methodName, Object[] args) throws Exception {
        Map<String, Object> map = new HashMap<>(args.length);
        ClassPool pool = ClassPool.getDefault();
        ClassClassPath classPath;
        classPath = new ClassClassPath(cls);
        pool.insertClassPath(classPath);

        CtClass cc = pool.get(clazzName);
        CtMethod cm = cc.getDeclaredMethod(methodName);
        MethodInfo methodInfo = cm.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        if (attr == null) {
            // exception
        }
        int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
        for (int i = 0; i < cm.getParameterTypes().length; i++) {
            map.put(attr.variableName(i + pos), args[i]);
        }
        return map;
    }
}
