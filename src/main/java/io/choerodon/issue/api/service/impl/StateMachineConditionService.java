package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineConfigService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.infra.enums.StateMachineConfigEnums;
import io.choerodon.issue.statemachine.enums.StateMachineConfigType;
import io.choerodon.issue.infra.enums.TransformConditionStrategy;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author peng.jiang@hand-china.com
 */
@Service
public class StateMachineConditionService implements StateMachineConfigService {

    @Autowired
    private IssueService issueService;

    @Override
    public String configType() {
        return StateMachineConfigType.CONDITION;
    }

    /**
     * 条件过滤 入口
     *
     * @param instanceId
     * @param transfDTOS
     * @return
     */
    @Override
    public List<TransformInfo> conditionFilter(Long instanceId, List<TransformInfo> transfDTOS) {
        if (transfDTOS == null || transfDTOS.isEmpty()) {
            return Collections.emptyList();
        }
        List<TransformInfo> resultTransf = new ArrayList<>();
        Issue issue = issueService.selectByPrimaryKey(instanceId);
        for (TransformInfo transfDTO:transfDTOS) {
            if (transfDTO.getConditions() == null || transfDTO.getConditions().isEmpty()){
                resultTransf.add(transfDTO);    //没有配置条件，直接跳过，视为满足条件
                continue;
            }
            Boolean transfConditionValidator = false;   //满足所有条件
            if (TransformConditionStrategy.ONE.equalsIgnoreCase(transfDTO.getConditionStrategy())){
                transfConditionValidator = true;    //满足条件之一
            }
            for (StateMachineConfigDTO configDTO : transfDTO.getConditions()) {
                switch (configDTO.getCode()) {
                    case StateMachineConfigEnums.CONDITION_REPORTER:
                        transfConditionValidator = reporter(issue, configDTO);
                        break;
                    case StateMachineConfigEnums.CONDITION_HANDLER:
                        transfConditionValidator = agent(issue, configDTO);
                        break;
                    case StateMachineConfigEnums.CONDITION_IN_GROUP:
                        transfConditionValidator = inGroup(issue, configDTO);
                        break;
                    case StateMachineConfigEnums.CONDITION_IN_PROJECTROLE:
                        transfConditionValidator = inProjectRole(issue, configDTO);
                        break;
                    case StateMachineConfigEnums.CONDITION_AUTHORITY:
                        transfConditionValidator = authority(issue, configDTO);
                        break;
                    default:
                        throw new CommonException("error.configDTO.condition.noMatch");
                }
                if(TransformConditionStrategy.ONE.equalsIgnoreCase(transfDTO.getConditionStrategy()) && transfConditionValidator){
                    break;  //满足条件之一  有一个验证成功后，后续验证不再需要执行
                }
            }
            if (transfConditionValidator) {
                resultTransf.add(transfDTO);
            }
        }
        return resultTransf;
    }

    /**
     * 条件执行 入口
     *
     * @param instanceId
     * @param conditionStrategy
     * @param configDTOS
     * @return
     */
    @Override
    public Boolean configExecute(Long instanceId, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        Issue issue = issueService.selectByPrimaryKey(instanceId);
        Boolean transfConditionValidator = false;   //满足所有条件
        if (TransformConditionStrategy.ONE.equalsIgnoreCase(conditionStrategy)){
            transfConditionValidator = true;    //满足条件之一
        }
        for (StateMachineConfigDTO configDTO : configDTOS) {
            switch (configDTO.getCode()) {
                case StateMachineConfigEnums.CONDITION_REPORTER:
                    transfConditionValidator = reporter(issue, configDTO);
                    break;
                case StateMachineConfigEnums.CONDITION_HANDLER:
                    transfConditionValidator = agent(issue, configDTO);
                    break;
                case StateMachineConfigEnums.CONDITION_IN_GROUP:
                    transfConditionValidator = inGroup(issue, configDTO);
                    break;
                case StateMachineConfigEnums.CONDITION_IN_PROJECTROLE:
                    transfConditionValidator = inProjectRole(issue, configDTO);
                    break;
                case StateMachineConfigEnums.CONDITION_AUTHORITY:
                    transfConditionValidator = authority(issue, configDTO);
                    break;
                default:
                    throw new CommonException("error.configDTO.condition.noMatch");
            }
            if(TransformConditionStrategy.ONE.equalsIgnoreCase(conditionStrategy) && transfConditionValidator){
                return true;  //满足条件之一  有一个验证成功后，后续验证不再需要执行
            }
        }
        return transfConditionValidator;
    }

    /**
     * reporter 仅允许报告人
     *
     * @param configDTO
     * @return
     */
    private Boolean reporter(Issue issue, StateMachineConfigDTO configDTO) {
        Long reporterId = DetailsHelper.getUserDetails().getUserId();
        return issue != null && issue.getReporterId() != null && issue.getReporterId().equals(reporterId);
    }

    /**
     * agent 仅允许经办人
     *
     * @param configDTO
     * @return
     */
    private Boolean agent(Issue issue, StateMachineConfigDTO configDTO) {
        Long handlerId = DetailsHelper.getUserDetails().getUserId();
        return issue != null && issue.getHandlerId() != null && issue.getHandlerId().equals(handlerId);
    }

    /**
     * inGroup 在任何组内的用户
     *
     * @param issue
     * @param configDTO
     * @return
     */
    private Boolean inGroup(Issue issue, StateMachineConfigDTO configDTO) {
        // todo 实现
        return false;
    }

    /**
     * inProjectRole 在任何项目角色内的用户
     *
     * @param issue
     * @param configDTO
     * @return
     */
    private Boolean inProjectRole(Issue issue, StateMachineConfigDTO configDTO) {
        // todo 实现
        return false;
    }

    /**
     * authority 权限条件
     *
     * @param issue
     * @param configDTO
     * @return
     */
    private Boolean authority(Issue issue, StateMachineConfigDTO configDTO) {
        // todo 实现
        return false;
    }
}
