package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.StateMachineConfigDTO;
import io.choerodon.issue.api.dto.StateMachineSchemeDTO;
import io.choerodon.issue.api.service.IssueService;
import io.choerodon.issue.api.service.StateMachineConfigService;
import io.choerodon.issue.api.service.StateMachineSchemeService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.Issue;
import io.choerodon.issue.infra.enums.CloopmCommonString;
import io.choerodon.issue.infra.enums.StateMachineConfigType;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.ExecuteResult;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.feign.dto.TransformInfo;
import io.choerodon.issue.infra.mapper.IssueMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.*;

import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author peng.jiang@hand-china.com
 */
@Component
public class StateMachineServiceImpl implements StateMachineService {

    private static final Logger logger = LoggerFactory.getLogger(StateMachineServiceImpl.class);

    @Value("${spring.application.name:default}")
    private String serverCode;
    @Autowired
    private StateMachineFeignClient stateMachineClient;
    @Autowired
    private StateMachineSchemeService schemeService;
    @Autowired
    private IssueService issueService;
    @Autowired
    private IssueMapper issueMapper;
    @Autowired
    private AnalyzeServiceManager analyzeServiceManager;

    @Override
    public ResponseEntity<Page<StateMachineDTO>> pageQuery(Long organizationId, Integer page, Integer size, String[] sort, String name, String description, String[] param) {
        ResponseEntity<Page<StateMachineDTO>> responseEntity = stateMachineClient.pagingQuery(organizationId, page, size, sort, name, description, param);
        if (responseEntity != null && responseEntity.getBody() != null && responseEntity.getBody().getContent() != null) {
            for (StateMachineDTO stateMachineDTO : responseEntity.getBody().getContent()) {
                List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineDTO.getId());
                //列表去重
                List<StateMachineSchemeDTO> unique = list.stream().collect(
                        collectingAndThen(
                                toCollection(() -> new TreeSet<>(comparingLong(StateMachineSchemeDTO::getId))), ArrayList::new)
                );
                stateMachineDTO.setStateMachineSchemeDTOs(unique);
            }
        }
        return responseEntity;
    }

    @Override
    public ResponseEntity<Boolean> delete(Long organizationId, Long stateMachineId) {
        List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineId);
        if (list != null && !list.isEmpty()) {
            throw new CommonException("error.stateMachine.delete");
        }
        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            throw new CommonException("error.stateMachine.delete.noFound");
        }
        return stateMachineClient.delete(organizationId, stateMachineId);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long stateMachineId) {
        Map<String, Object> map = new HashMap<>();
        ResponseEntity<StateMachineDTO> responseEntity = stateMachineClient.queryStateMachineById(organizationId, stateMachineId);
        if (responseEntity == null || responseEntity.getBody() == null) {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("reason", "noFound");
            return map;
        }
        List<StateMachineSchemeDTO> list = schemeService.querySchemeByStateMachineId(organizationId, stateMachineId);
        if (list == null || list.isEmpty()) {
            map.put(CloopmCommonString.CAN_DELETE, true);
        } else {
            map.put(CloopmCommonString.CAN_DELETE, false);
            map.put("schemeUsed", list.size());
        }
        return map;
    }

    @Override
    public ResponseEntity<List<TransformInfo>> transfList(Long organizationId, Long projectId, Long issueId) {
        Long stateMachineId = issueService.getStateMachineId(projectId, issueId);
        Long currentStateId = issueMapper.selectByPrimaryKey(issueId).getStatusId();
        return stateMachineClient.transformList(organizationId, serverCode,
                stateMachineId, issueId, currentStateId);
    }

    @Override
    public ResponseEntity<ExecuteResult> doTransf(Long organizationId, Long projectId, Long issueId, Long transfId) {
        Long stateMachineId = issueService.getStateMachineId(projectId, issueId);
        Issue issue = issueService.selectByPrimaryKey(issueId);
        if (issue == null) {
            throw new CommonException("error.issue.noFound");
        }
        return stateMachineClient.executeTransform(organizationId, serverCode, stateMachineId, issueId,
                issue.getStatusId(), transfId);
    }

    @Override
    public List<TransformInfo> conditionFilter(Long organizationId, Long instanceId, List<TransformInfo> transfDTOS) {
        logger.info("状态机回调执行：conditionFilter,issueId:{},transfDTOS:{}", instanceId, transfDTOS);
        List<StateMachineConfigService> configServices = analyzeServiceManager.getConfigServices();
        for (StateMachineConfigService service : configServices) {
            if (service.matchConfigType(StateMachineConfigType.CONDITION)) {
                return service.conditionFilter(instanceId, transfDTOS);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public ExecuteResult configExecute(Long organizationId, Long instanceId, Long targetStateId, String type, String conditionStrategy, List<StateMachineConfigDTO> configDTOS) {
        logger.info("状态机回调执行：configExecute,type:{},issueId:{},configDTOS:{}", type, instanceId, configDTOS);
        //测试两种异常：
//        ExecuteResult xx=null;
//        if (true) {
//            //获取值时空指针异常
////            xx.getIsSuccess();
//            //手动抛出的异常
//            throw new RuntimeException("test");
//        }
        ExecuteResult executeResult = new ExecuteResult();
        Boolean isSuccess = false;
        List<StateMachineConfigService> configServices = analyzeServiceManager.getConfigServices();
        for (StateMachineConfigService service : configServices) {
            if (!StateMachineConfigType.POSTPOSITION.equals(type) && service.matchConfigType(type)) {
                isSuccess = service.configExecute(instanceId, conditionStrategy, configDTOS);
            } else if (StateMachineConfigType.POSTPOSITION.equals(type) && service.matchConfigType(type)) {
                isSuccess = service.configExecute(instanceId, targetStateId, configDTOS);
            }
        }
        executeResult.setSuccess(isSuccess);
        return executeResult;
    }
}
