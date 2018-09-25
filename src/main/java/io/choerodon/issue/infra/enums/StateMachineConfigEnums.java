package io.choerodon.issue.infra.enums;

/**
 * @author peng.jiang@hand-china.com
 */
public interface StateMachineConfigEnums {

    //条件
    String CONDITION_REPORTER = "reporter"; //仅允许报告人
    String CONDITION_HANDLER = "handler"; //仅允许经办人
    String CONDITION_IN_GROUP = "inGroup"; //在任何组内的用户
    String CONDITION_IN_PROJECTROLE = "inProjectRole"; //在任何项目角色内的用户
    String CONDITION_AUTHORITY = "authority"; //权限条件

    //验证
    String VALIDATOR = "validator"; //权限校验

    //后置处理
    String POSTPOSITION_ASSIGN_CURRENTUSER = "assignCurrentUser"; //分配给当前用户
    String POSTPOSITION_ASSIGN_REPORTER = "assignReporter"; //分配给报告人
    String POSTPOSITION_ASSIGN_DEVELOPER = "assignDeveloper"; //分配给负责开发人

}
