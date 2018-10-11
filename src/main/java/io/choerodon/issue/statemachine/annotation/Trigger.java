package io.choerodon.issue.statemachine.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author shinan.chen
 * @date 2018/10/9
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Trigger {
    String code();
    String name();
    String description();
}
