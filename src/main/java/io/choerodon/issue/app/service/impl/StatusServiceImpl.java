package io.choerodon.issue.app.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.app.service.StateMachineNodeService;
import io.choerodon.issue.app.service.StatusService;
import io.choerodon.issue.api.vo.*;
import io.choerodon.issue.infra.dto.StateMachineNode;
import io.choerodon.issue.infra.dto.Status;
import io.choerodon.issue.infra.dto.StatusWithInfo;
import io.choerodon.issue.infra.cache.InstanceCache;
import io.choerodon.issue.infra.enums.NodeType;
import io.choerodon.issue.infra.enums.StatusType;
import io.choerodon.issue.infra.exception.RemoveStatusException;
import io.choerodon.issue.infra.mapper.*;
import io.choerodon.issue.infra.util.EnumUtil;
import io.choerodon.issue.infra.util.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * @author peng.jiang, dinghuang123@gmail.com
 */
@Service
public class StatusServiceImpl implements StatusService {

    @Autowired
    private StatusMapper statusMapper;
    @Autowired
    private StateMachineNodeDraftMapper nodeDraftMapper;
    @Autowired
    private StateMachineNodeMapper nodeDeployMapper;
    @Autowired
    private StateMachineNodeService nodeService;
    @Autowired
    private StateMachineTransformDraftMapper transformDraftMapper;
    @Autowired
    private StateMachineTransformMapper transformDeployMapper;
    @Autowired
    private InstanceCache instanceCache;
    @Autowired
    private StateMachineMapper stateMachineMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @PostConstruct
    public void init() {
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
    }

    @Override
    public PageInfo<StatusWithInfoVO> queryStatusList(PageRequest pageRequest, Long organizationId, StatusSearchVO statusSearchVO) {
        PageInfo<Long> statusIdsPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort()))
                .doSelectPageInfo(() -> statusMapper.selectStatusIds(organizationId, statusSearchVO));
        List<StatusWithInfoVO> statusWithInfoVOList = new ArrayList<>();
        if (!statusIdsPage.getList().isEmpty()) {
            List<StatusWithInfo> statuses = statusMapper.queryStatusList(organizationId, statusIdsPage.getList());
            statusWithInfoVOList = modelMapper.map(statuses, new TypeToken<List<StatusWithInfoVO>>() {
            }.getType());
        }
        return PageUtil.buildPageInfoWithPageInfoList(statusIdsPage, statusWithInfoVOList);
    }

    @Override
    public StatusVO create(Long organizationId, StatusVO statusVO) {
        if (checkName(organizationId, statusVO.getName()).getStatusExist()) {
            throw new CommonException("error.statusName.exist");
        }
        if (!EnumUtil.contain(StatusType.class, statusVO.getType())) {
            throw new CommonException("error.status.type.illegal");
        }
        statusVO.setOrganizationId(organizationId);
        Status status = modelMapper.map(statusVO, Status.class);
        List<Status> select = statusMapper.select(status);
        if (select.isEmpty()) {
            int isInsert = statusMapper.insert(status);
            if (isInsert != 1) {
                throw new CommonException("error.status.create");
            }
        } else {
            status = select.get(0);
        }
        status = statusMapper.queryById(organizationId, status.getId());
        return modelMapper.map(status, StatusVO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long statusId, String name) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        status.setName(name);
        Status res = statusMapper.selectOne(status);
        return res != null && !statusId.equals(res.getId());
    }

    @Override
    public StatusVO update(StatusVO statusVO) {
        if (checkNameUpdate(statusVO.getOrganizationId(), statusVO.getId(), statusVO.getName())) {
            throw new CommonException("error.statusName.exist");
        }
        if (!EnumUtil.contain(StatusType.class, statusVO.getType())) {
            throw new CommonException("error.status.type.illegal");
        }
        Status status = modelMapper.map(statusVO, Status.class);
        int isUpdate = statusMapper.updateByPrimaryKeySelective(status);
        if (isUpdate != 1) {
            throw new CommonException("error.status.update");
        }
        status = statusMapper.queryById(status.getOrganizationId(), status.getId());
        return modelMapper.map(status, StatusVO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long statusId) {
        Status status = statusMapper.queryById(organizationId, statusId);
        if (status == null) {
            throw new CommonException("error.status.delete.nofound");
        }
        Long draftUsed = nodeDraftMapper.checkStateDelete(organizationId, statusId);
        Long deployUsed = nodeDeployMapper.checkStateDelete(organizationId, statusId);
        if (draftUsed != 0 || deployUsed != 0) {
            throw new CommonException("error.status.delete");
        }
        if (status.getCode() != null) {
            throw new CommonException("error.status.illegal");
        }
        int isDelete = statusMapper.deleteByPrimaryKey(statusId);
        if (isDelete != 1) {
            throw new CommonException("error.status.delete");
        }
        return true;
    }

    @Override
    public StatusInfoVO queryStatusById(Long organizationId, Long stateId) {
        Status status = statusMapper.queryById(organizationId, stateId);
        if (status == null) {
            throw new CommonException("error.queryStatusById.notExist");
        }
        return modelMapper.map(status, StatusInfoVO.class);
    }

    @Override
    public List<StatusVO> queryAllStatus(Long organizationId) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        List<Status> statuses = statusMapper.select(status);
        return modelMapper.map(statuses, new TypeToken<List<StatusVO>>() {
        }.getType());
    }

    @Override
    public Map<Long, StatusMapVO> queryAllStatusMap(Long organizationId) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        List<Status> statuses = statusMapper.select(status);
        Map<Long, StatusMapVO> statusMap = new HashMap<>();
        for (Status sta : statuses) {
            StatusMapVO statusMapVO = modelMapper.map(sta, new TypeToken<StatusMapVO>() {
            }.getType());
            statusMap.put(statusMapVO.getId(), statusMapVO);
        }
        return statusMap;
    }

    @Override
    public StatusCheckVO checkName(Long organizationId, String name) {
        Status status = new Status();
        status.setOrganizationId(organizationId);
        status.setName(name);
        Status res = statusMapper.selectOne(status);
        StatusCheckVO statusCheckVO = new StatusCheckVO();
        if (res != null) {
            statusCheckVO.setStatusExist(true);
            statusCheckVO.setId(res.getId());
            statusCheckVO.setName(res.getName());
            statusCheckVO.setType(res.getType());
        } else {
            statusCheckVO.setStatusExist(false);
        }
        return statusCheckVO;
    }

    @Override
    public Map<Long, Status> batchStatusGet(List<Long> ids) {
        if (!ids.isEmpty()) {
            List<Status> statuses = statusMapper.batchStatusGet(ids);
            Map<Long, Status> map = new HashMap();
            for (Status status : statuses) {
                map.put(status.getId(), status);
            }
            return map;
        } else {
            return new HashMap<>();
        }

    }

    @Override
    public StatusVO createStatusForAgile(Long organizationId, Long stateMachineId, StatusVO statusVO) {
        if (stateMachineId == null) {
            throw new CommonException("error.stateMachineId.notNull");
        }
        if (stateMachineMapper.queryById(organizationId, stateMachineId) == null) {
            throw new CommonException("error.stateMachine.notFound");
        }

        String statusName = statusVO.getName();
        Status select = new Status();
        select.setName(statusName);
        select.setOrganizationId(organizationId);
        Status status = statusMapper.selectOne(select);
        if (status == null) {
            statusVO = create(organizationId, statusVO);
        } else {
            statusVO = modelMapper.map(status, StatusVO.class);
        }
        //将状态加入状态机中，直接加到发布表中
        nodeService.createNodeAndTransformForAgile(organizationId, stateMachineId, statusVO);
        //清理状态机实例
        instanceCache.cleanStateMachine(stateMachineId);
        return statusVO;
    }

    @Override
    public void removeStatusForAgile(Long organizationId, Long stateMachineId, Long statusId) {
        if (statusId == null) {
            throw new CommonException("error.statusId.notNull");
        }
        StateMachineNode stateNode = new StateMachineNode();
        stateNode.setOrganizationId(organizationId);
        stateNode.setStateMachineId(stateMachineId);
        stateNode.setStatusId(statusId);
        StateMachineNode res = nodeDeployMapper.selectOne(stateNode);
        if (res == null) {
            throw new RemoveStatusException("error.status.exist");
        }
        if (res.getType().equals(NodeType.INIT)) {
            throw new RemoveStatusException("error.status.illegal");
        }
        if (res.getId() != null) {
            //删除节点
            nodeDeployMapper.deleteByPrimaryKey(res.getId());
            //删除节点关联的转换
            transformDeployMapper.deleteByNodeId(res.getId());
            //删除节点
            nodeDraftMapper.deleteByPrimaryKey(res.getId());
            //删除节点关联的转换
            transformDraftMapper.deleteByNodeId(res.getId());
        }
        //清理状态机实例
        instanceCache.cleanStateMachine(stateMachineId);
    }

    @Override
    public List<StatusVO> queryByStateMachineIds(Long organizationId, List<Long> stateMachineIds) {
        if (!stateMachineIds.isEmpty()) {
            List<Status> statuses = statusMapper.queryByStateMachineIds(organizationId, stateMachineIds);
            return modelMapper.map(statuses, new TypeToken<List<StatusVO>>() {
            }.getType());
        }
        return Collections.emptyList();
    }
}
