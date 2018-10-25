package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.PriorityDTO;
import io.choerodon.issue.api.service.PriorityService;
import io.choerodon.issue.domain.Priority;
import io.choerodon.issue.infra.enums.PriorityType;
import io.choerodon.issue.infra.mapper.PriorityMapper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */
@Service
@RefreshScope
public class PriorityServiceImpl extends BaseServiceImpl<Priority> implements PriorityService {

    @Autowired
    private PriorityMapper priorityMapper;

    private ModelMapper modelMapper = new ModelMapper();

    @Override
    public List<PriorityDTO> selectAll(PriorityDTO priorityDTO, String param) {
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        List<Priority> priorities = priorityMapper.fulltextSearch(priority, param);
        return modelMapper.map(priorities, new TypeToken<List<PriorityDTO>>() {
        }.getType());
    }

    @Override
    @Transactional
    public PriorityDTO create(Long organizationId, PriorityDTO priorityDTO) {
        priorityDTO.setSequence((priorityMapper.getNextSequence(organizationId)).add(new BigDecimal(1)));
        priorityDTO.setOrganizationId(organizationId);
        //若设置为默认值，则清空其他默认值
        if (priorityDTO.getDefault()) {
            priorityMapper.updateDefaultPriority(organizationId);
        }
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        if (!(this.checkName(organizationId, priorityDTO.getId(), priorityDTO.getName()))) {
            throw new CommonException("error.priority.create.name.same");
        }
        int isInsert = priorityMapper.insert(priority);
        if (isInsert != 1) {
            throw new CommonException("error.priority.create");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityDTO.class);
    }

    @Override
    public Boolean delete(Long organizationId, Long priorityId) {
        int isDelete = priorityMapper.deleteByPrimaryKey(priorityId);
        if (isDelete != 1) {
            throw new CommonException("error.priority.delete");
        }
        return true;
    }

    @Override
    @Transactional
    public PriorityDTO update(PriorityDTO priorityDTO) {
        Priority priority = modelMapper.map(priorityDTO, Priority.class);
        if (!(this.checkName(priorityDTO.getOrganizationId(), priorityDTO.getId(), priorityDTO.getName()))) {
            throw new CommonException("error.priority.update.name.same");
        }
        //若设置为默认值，则清空其他默认值
        if (priorityDTO.getDefault()) {
            priorityMapper.updateDefaultPriority(priorityDTO.getOrganizationId());
        }
        int isUpdate = priorityMapper.updateByPrimaryKeySelective(priority);
        if (isUpdate != 1) {
            throw new CommonException("error.priority.update");
        }
        priority = priorityMapper.selectByPrimaryKey(priority);
        return modelMapper.map(priority, PriorityDTO.class);
    }

    @Override
    public Boolean checkName(Long organizationId, Long priorityId, String name) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        priority = priorityMapper.selectOne(priority);
        if (priority != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验
            return priority.getId().equals(priorityId);
        }
        return true;
    }

    @Override
    @Transactional
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
            PriorityDTO priorityDTO = modelMapper.map(pri, new TypeToken<PriorityDTO>(){}.getType());
            result.put(priorityDTO.getId(), priorityDTO);
        }
        return result;
    }

    @Override
    public List<PriorityDTO> queryByOrganizationIdList(Long organizationId) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        List<Priority> priorities = priorityMapper.select(priority);
        Collections.sort(priorities, Comparator.comparing(Priority::getSequence));
        return modelMapper.map(priorities, new TypeToken<List<PriorityDTO>>(){}.getType());
    }


    @Override
    public PriorityDTO queryById(Long organizationId, Long id) {
        Priority result = priorityMapper.selectByPrimaryKey(id);
        if (result == null) {
            throw new CommonException("error.priority.get");
        }
        return modelMapper.map(result, new TypeToken<PriorityDTO>(){}.getType());
    }

    private Priority savePrority(Long organizationId, String name, BigDecimal sequence, String colour, Boolean isDefault) {
        Priority priority = new Priority();
        priority.setOrganizationId(organizationId);
        priority.setName(name);
        priority.setSequence(sequence);
        priority.setColour(colour);
        priority.setDescription(name);
        priority.setDefault(isDefault);
        if (priorityMapper.insert(priority) != 1) {
            throw new CommonException("error.prority.insert");
        }
        return priority;
    }

    private Map<String, Long> initPrority(Long organizationId) {
        Map<String, Long> map = new HashMap<>();
        Priority high = savePrority(organizationId, "高", new BigDecimal(0), "#FEBA3D", false);
        Priority medium = savePrority(organizationId, "中", new BigDecimal(1), "#3B79E0", true);
        Priority low = savePrority(organizationId, "低", new BigDecimal(2), "#969696", false);
        map.put("high", high.getId());
        map.put("medium", medium.getId());
        map.put("low", low.getId());
        return map;
    }

    @Override
    public Map<Long, Map<String, Long>> initProrityByOrganization(List<Long> organizationIds) {
        Map<Long, Map<String, Long>> result = new HashMap<>();
        for (Long organizationId : organizationIds) {
            result.put(organizationId, initPrority(organizationId));
        }
        return result;
    }
}
