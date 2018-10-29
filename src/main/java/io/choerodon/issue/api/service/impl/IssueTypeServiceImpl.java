package io.choerodon.issue.api.service.impl;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.IssueTypeDTO;
import io.choerodon.issue.api.service.IssueTypeService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.infra.enums.IssueTypeE;
import io.choerodon.issue.infra.feign.StateMachineFeignClient;
import io.choerodon.issue.infra.feign.dto.StateMachineDTO;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeConfigMapper;
import io.choerodon.issue.infra.mapper.IssueTypeSchemeMapper;
import io.choerodon.issue.infra.mapper.StateMachineSchemeConfigMapper;
import io.choerodon.issue.infra.utils.ConvertUtils;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
@Component
@RefreshScope
public class IssueTypeServiceImpl extends BaseServiceImpl<IssueType> implements IssueTypeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(IssueTypeServiceImpl.class);

    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private StateMachineSchemeConfigMapper schemeConfigMapper;

    @Autowired
    private StateMachineFeignClient stateMachineServiceFeign;

    @Autowired
    private IssueTypeSchemeMapper issueTypeSchemeMapper;

    @Autowired
    private IssueTypeSchemeConfigMapper issueTypeSchemeConfigMapper;

    private final ModelMapper modelMapper = new ModelMapper();

    public IssueTypeServiceImpl(IssueTypeMapper issueTypeMapper) {
        this.issueTypeMapper = issueTypeMapper;
    }


    @Override
    public IssueTypeDTO queryById(Long organizationId, Long issueTypeId) {
        IssueType issueType = issueTypeMapper.selectByPrimaryKey(issueTypeId);
        if (issueType != null) {
            return modelMapper.map(issueType, IssueTypeDTO.class);
        }
        return null;
    }

    @Override
    public IssueTypeDTO create(Long organizationId, IssueTypeDTO issueTypeDTO) {
        if (!checkName(organizationId, issueTypeDTO.getName(), null)) {
            throw new CommonException("error.issueType.checkName");
        }
        issueTypeDTO.setOrganizationId(organizationId);
        IssueType issueType = modelMapper.map(issueTypeDTO, IssueType.class);
        return modelMapper.map(createIssueType(issueType), IssueTypeDTO.class);
    }

    @Override
    public IssueTypeDTO update(IssueTypeDTO issueTypeDTO) {
        if (issueTypeDTO.getName() != null && !checkName(issueTypeDTO.getOrganizationId(), issueTypeDTO.getName(), issueTypeDTO.getId())) {
            throw new CommonException("error.issueType.checkName");
        }
        IssueType issueType = modelMapper.map(issueTypeDTO, IssueType.class);
        int isUpdate = issueTypeMapper.updateByPrimaryKeySelective(issueType);
        if (isUpdate != 1) {
            throw new CommonException("error.issueType.update");
        }
        issueType = issueTypeMapper.selectByPrimaryKey(issueType.getId());
        return modelMapper.map(issueType, IssueTypeDTO.class);
    }

    @Override
    public Map<String, Object> checkDelete(Long organizationId, Long issueTypeId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        IssueType issueType = issueTypeMapper.selectByPrimaryKey(issueTypeId);
        if (issueType == null) {
            throw new CommonException("error.base.notFound");
        } else if (!issueType.getOrganizationId().equals(organizationId)) {
            throw new CommonException("error.issueType.illegal");
        }
        //判断要删除的issueType是否有使用中的issue【toDo】

        return result;
    }

    @Override
    public Boolean delete(Long organizationId, Long issueTypeId) {
        Map<String, Object> result = checkDelete(organizationId, issueTypeId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            int isDelete = issueTypeMapper.deleteByPrimaryKey(issueTypeId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
            //关联删除一些东西【toDo】
        } else {
            return false;
        }
        //校验
        return true;
    }

    @Override
    public Page<IssueTypeDTO> pageQuery(PageRequest pageRequest, IssueTypeDTO issueTypeDTO, String param) {
        IssueType issueType = modelMapper.map(issueTypeDTO, IssueType.class);
        Page<IssueType> pages = PageHelper.doPageAndSort(pageRequest,
                () -> issueTypeMapper.fulltextSearch(issueType, param));

        Page<IssueTypeDTO> pagesDTO = new Page<>();
        pagesDTO.setNumber(pages.getNumber());
        pagesDTO.setNumberOfElements(pages.getNumberOfElements());
        pagesDTO.setSize(pages.getSize());
        pagesDTO.setTotalElements(pages.getTotalElements());
        pagesDTO.setTotalPages(pages.getTotalPages());
        pagesDTO.setContent(modelMapper.map(pages.getContent(), new TypeToken<List<IssueTypeDTO>>() {
        }.getType()));
        return pagesDTO;
    }

    @Override
    public Boolean checkName(Long organizationId, String name, Long id) {
        IssueType select = new IssueType();
        select.setName(name);
        select.setOrganizationId(organizationId);
        select = issueTypeMapper.selectOne(select);
        if (select != null) {
            //若传了id，则为更新校验（更新校验不校验本身），不传为创建校验返回false
            return select.getId().equals(id);
        }
        return true;
    }

    @Override
    public List<IssueTypeDTO> queryByOrgId(Long organizationId) {
        IssueType issueType = new IssueType();
        issueType.setOrganizationId(organizationId);
        List<IssueType> issueTypes = issueTypeMapper.select(issueType);
        return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
        }.getType());
    }

    @Override
    public List<IssueTypeDTO> queryIssueType(Long organizationId, Long schemeId) {
        List<IssueType> list = issueTypeMapper.queryIssueType(organizationId, schemeId);
        List<IssueTypeDTO> dtos = ConvertUtils.convertIssueTypesToDTOs(list);
        for (IssueTypeDTO issueTypeDTO : dtos) {
            if (issueTypeDTO.getStateMachineSchemeConfigDTO() != null && issueTypeDTO.getStateMachineSchemeConfigDTO().getStateMachineId() != null) {
                StateMachineDTO stateMachineDTO = stateMachineServiceFeign.queryStateMachineById(organizationId, issueTypeDTO.getStateMachineSchemeConfigDTO().getStateMachineId()).getBody();
                issueTypeDTO.getStateMachineSchemeConfigDTO().setStateMachineName(stateMachineDTO.getName());
            }
        }
        return dtos;
    }

    @Override
    public void initIssueTypeByConsumeCreateOrganization(Long organizationId) {
        for (IssueTypeE issueTypeE : IssueTypeE.values()) {
            //创建默认问题类型
            createIssueType(new IssueType(issueTypeE.getIcon(), issueTypeE.getName(), issueTypeE.getDescription(), organizationId, issueTypeE.getColour(), issueTypeE.getTypeCode(), true));
        }
    }


    private IssueType createIssueType(IssueType issueType) {
        //保证幂等性
        List<IssueType> issueTypes = issueTypeMapper.select(issueType);
        if (!issueTypes.isEmpty()) {
            return issueTypes.get(0);
        }

        if (issueTypeMapper.insert(issueType) != 1) {
            throw new CommonException("error.issueType.create");
        }
        return issueTypeMapper.selectByPrimaryKey(issueType);
    }

    @Override
    public Map<Long, IssueTypeDTO> listIssueTypeMap(Long organizationId) {
        IssueType issueType = new IssueType();
        issueType.setOrganizationId(organizationId);
        List<IssueType> issueTypes = issueTypeMapper.select(issueType);
        Map<Long, IssueTypeDTO> issueTypeDTOMap = new HashMap<>();
        for (IssueType iType : issueTypes) {
            issueTypeDTOMap.put(iType.getId(), modelMapper.map(iType, new TypeToken<IssueTypeDTO>() {
            }.getType()));
        }
        return issueTypeDTOMap;
    }

    @Override
    public Map<Long, Map<String, Long>> initIssueTypeData(Long organizationId, List<Long> orgIds) {
        Map<Long, Map<String, Long>> result = new HashMap<>();
        for (Long orgId : orgIds) {
            Map<String, Long> temp = new HashMap<>();
            for (IssueTypeE issueTypeE : IssueTypeE.values()) {
                IssueType issueType = createIssueType(new IssueType(issueTypeE.getIcon(), issueTypeE.getName(), issueTypeE.getDescription(), orgId, issueTypeE.getColour(), issueTypeE.getTypeCode(), true));
                temp.put(issueTypeE.getTypeCode(), issueType.getId());
            }
            result.put(orgId, temp);
        }
        return result;
    }
}
