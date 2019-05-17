package io.choerodon.issue.api.service.impl;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.issue.api.dto.PriorityDTO;
import io.choerodon.issue.api.dto.ProjectDTO;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.domain.Priority;
import io.choerodon.issue.infra.feign.AgileFeignClient;
import io.choerodon.issue.infra.feign.UserFeignClient;
import io.choerodon.issue.infra.mapper.PriorityMapper;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PriorityServiceImpl extends BaseServiceImpl<Priority> implements PriorityService {

    @Autowired
    private PriorityMapper priorityMapper;
    @Autowired
    private UserFeignClient userFeignClient;
    @Autowired
    private AgileFeignClient agileFeignClient;

    private ModelMapper modelMapper = new ModelMapper();

    private static final String NOT_FOUND = "error.priority.notFound";
    private static final String DELETE_ILLEGAL = "error.priority.deleteIllegal";
    private static final String LAST_ILLEGAL = "error.priority.lastIllegal";

    @Override
    public List<PriorityDTO> selectAll(PriorityDTO priorityDTO, String param) {
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        List<Priority> priorities = priorityMapper.fulltextSearch(priority, param);
        return modelMapper.map(priorities, new TypeToken<List<PriorityDTO>>() {
        }.getType());
    }

    @Override
    public PriorityDTO create(Long organizationId, PriorityDTO priorityDTO) {
        if (checkName(organizationId, priorityDTO.getName())) {
            throw new CommonException("error.priority.create.name.same");
        }
        priorityDTO.setSequence((priorityMapper.getNextSequence(organizationId)).add(new BigDecimal(1)));
        priorityDTO.setOrganizationId(organizationId);
        //若设置为默认值，则清空其他默认值
        if (priorityDTO.getDefault() != null && priorityDTO.getDefault()) {
            priorityMapper.cancelDefaultPriority(organizationId);
        } else {
            priorityDTO.setDefault(false);
        }
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        priority.setEnable(true);
        int isInsert = priorityMapper.insert(priority);
        if (isInsert != 1) {
            throw new CommonException("error.priority.create");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityDTO.class);
    }

    private Boolean checkNameUpdate(Long organizationId, Long priorityId, String name) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        Priority res = priorityMapper.selectOne(priority);
        return res != null && !priorityId.equals(res.getId());
    }

    @Override
    public PriorityDTO update(PriorityDTO priorityDTO) {
        if (checkNameUpdate(priorityDTO.getOrganizationId(), priorityDTO.getId(), priorityDTO.getName())) {
            throw new CommonException("error.priority.update.name.same");
        }
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        //若设置为默认值，则清空其他默认值
        if (priorityDTO.getDefault() != null && priorityDTO.getDefault()) {
            priorityMapper.cancelDefaultPriority(priorityDTO.getOrganizationId());
        } else {
            //如果只有一个默认优先级时，无法取消当前默认优先级
            Priority select = new Priority();
            select.setDefault(true);
            select.setOrganizationId(priorityDTO.getOrganizationId());
            Priority result = priorityMapper.selectOne(select);
            if (result.getId().equals(priority.getId())) {
                throw new CommonException("error.priority.illegal");
            }
        }
        int isUpdate = priorityMapper.updateByPrimaryKeySelective(priority);
        if (isUpdate != 1) {
            throw new CommonException("error.priority.update");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityDTO.class);
    }

    @Override
    public Boolean checkName(Long organizationId, String name) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        Priority res = priorityMapper.selectOne(priority);
        return res != null;
    }

    @Override
    public List<PriorityDTO> updateByList(List<PriorityDTO> list, Long organizationId) {
        int seq = 1;
        for (PriorityDTO priorityDTO : list) {
            Priority p = modelMapper.map(priorityDTO, Priority.class);
            p.setSequence(new BigDecimal(seq));
            seq++;
            int isUpdate = priorityMapper.updateSequenceById(p);
            if (isUpdate != 1) {
                throw new CommonException("error.priority.update");
            }
        }
        List<Priority> priorities = priorityMapper.fulltextSearch(new Priority(), null);
        return modelMapper.map(priorities, new TypeToken<List<PriorityDTO>>() {
        }.getType());
    }

    @Override
    public Map<Long, PriorityDTO> queryByOrganizationId(Long organizationId) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        List<Priority> priorities = priorityMapper.select(priority);
        Map<Long, PriorityDTO> result = new HashMap<>();
        for (Priority pri : priorities) {
            PriorityDTO priorityDTO = modelMapper.map(pri, new TypeToken<PriorityDTO>() {
            }.getType());
            result.put(priorityDTO.getId(), priorityDTO);
        }
        return result;
    }

    @Override
    public PriorityDTO queryDefaultByOrganizationId(Long organizationId) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setDefault(true);
        Priority result = priorityMapper.selectOne(priority);
        if (result == null) {
            throw new CommonException(NOT_FOUND);
        }
        return modelMapper.map(result, new TypeToken<PriorityDTO>() {
        }.getType());
    }

    @Override
    public List<PriorityDTO> queryByOrganizationIdList(Long organizationId) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        List<Priority> priorities = priorityMapper.select(priority);
        Collections.sort(priorities, Comparator.comparing(Priority::getSequence));
        return modelMapper.map(priorities, new TypeToken<List<PriorityDTO>>() {
        }.getType());
    }


    @Override
    public PriorityDTO queryById(Long organizationId, Long id) {
        Priority result = priorityMapper.selectByPrimaryKey(id);
        if (result == null) {
            throw new CommonException("error.priority.get");
        }
        return modelMapper.map(result, new TypeToken<PriorityDTO>() {
        }.getType());
    }

    private Priority savePrority(Long organizationId, String name, BigDecimal sequence, String colour, Boolean isDefault) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        priority.setSequence(sequence);
        priority.setColour(colour);
        priority.setDescription(name);
        priority.setDefault(isDefault);
        priority.setEnable(true);
        //保证幂等性
        List<Priority> list = priorityMapper.select(priority);
        if (list.isEmpty()) {
            if (priorityMapper.insert(priority) != 1) {
                throw new CommonException("error.prority.insert");
            }
        } else {
            priority = list.get(0);
        }

        return priority;
    }

    private Map<String, Long> initPrority(Long organizationId) {
        Map<String, Long> map = new HashMap<>();
        Priority high = savePrority(organizationId, "高", new BigDecimal(0), "#FFB100", false);
        Priority medium = savePrority(organizationId, "中", new BigDecimal(1), "#3575DF", true);
        Priority low = savePrority(organizationId, "低", new BigDecimal(2), "#979797", false);
        map.put("high", high.getId());
        map.put("medium", medium.getId());
        map.put("low", low.getId());
        return map;
    }

    @Override
    public Map<Long, Map<String, Long>> initProrityByOrganization(List<Long> organizationIds) {
        Map<Long, Map<String, Long>> result = new HashMap<>(organizationIds.size());
        for (Long organizationId : organizationIds) {
            result.put(organizationId, initPrority(organizationId));
        }
        return result;
    }

    @Override
    public PriorityDTO enablePriority(Long organizationId, Long id, Boolean enable) {
        if (!enable) {
            checkLastPriority(organizationId, id);
        }
        Priority priority = priorityMapper.selectByPrimaryKey(id);
        if (priority == null) {
            throw new CommonException(NOT_FOUND);
        }
        priority.setEnable(enable);
        updateOptional(priority, "enable");
        //失效之后再进行默认优先级的重置
        if (!enable && priority.getDefault()) {
            updateOtherDefault(organizationId);
        }
        return queryById(organizationId, id);
    }

    @Override
    public Long checkDelete(Long organizationId, Long id) {
        //查询出组织下的所有项目
        List<ProjectDTO> projectDTOs = userFeignClient.queryProjectsByOrgId(organizationId, 1, 0).getBody().getList();
        List<Long> projectIds = projectDTOs.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        Long count;
        if (projectIds == null || projectIds.isEmpty()) {
            count = 0L;
        } else {
            count = agileFeignClient.checkPriorityDelete(organizationId, id, projectIds).getBody();
        }
        return count;
    }

    @Override
    public Boolean delete(Long organizationId, Long priorityId, Long changePriorityId) {
        if (priorityId.equals(changePriorityId)) {
            throw new CommonException(DELETE_ILLEGAL);
        }
        checkLastPriority(organizationId, priorityId);
        Priority priority = priorityMapper.selectByPrimaryKey(priorityId);
        List<ProjectDTO> projectDTOs = userFeignClient.queryProjectsByOrgId(organizationId, 1, 0).getBody().getList();
        List<Long> projectIds = projectDTOs.stream().map(ProjectDTO::getId).collect(Collectors.toList());
        Long count;
        if (projectIds == null || projectIds.isEmpty()) {
            count = 0L;
        } else {
            count = agileFeignClient.checkPriorityDelete(organizationId, priorityId, projectIds).getBody();
        }
        //执行优先级转换
        if (!count.equals(0L)) {
            if (changePriorityId == null) {
                throw new CommonException(DELETE_ILLEGAL);
            }
            CustomUserDetails customUserDetails = DetailsHelper.getUserDetails();
            agileFeignClient.batchChangeIssuePriority(organizationId, priorityId, changePriorityId, customUserDetails.getUserId(), projectIds);
        }
        int isDelete = priorityMapper.deleteByPrimaryKey(priorityId);
        if (isDelete != 1) {
            throw new CommonException("error.priority.delete");
        }
        if (priority.getDefault()) {
            updateOtherDefault(organizationId);
        }
        return true;
    }

    /**
     * 操作的是最后一个有效优先级则无法删除/失效
     *
     * @param organizationId
     */
    private void checkLastPriority(Long organizationId, Long priorityId) {
        Priority priority = new Priority();
        priority.setEnable(true);
        priority.setOrganizationId(organizationId);
        List<Priority> priorities = priorityMapper.select(priority);
        if (priorities.size() == 1 && priorityId.equals(priorities.get(0).getId())) {
            throw new CommonException(LAST_ILLEGAL);
        }
    }

    /**
     * 当执行失效/删除时，若当前是默认优先级，则取消当前默认优先级，并设置第一个为默认优先级，要放在方法最后执行
     *
     * @param organizationId
     */
    private synchronized void updateOtherDefault(Long organizationId) {
        priorityMapper.cancelDefaultPriority(organizationId);
        priorityMapper.updateMinSeqAsDefault(organizationId);
    }
}
