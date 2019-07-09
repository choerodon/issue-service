package io.choerodon.issue.api.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.choerodon.base.domain.PageRequest;
import io.choerodon.core.exception.CommonException;
import io.choerodon.issue.api.dto.*;
import io.choerodon.issue.api.service.IssueTypeService;
import io.choerodon.issue.api.service.StateMachineSchemeConfigService;
import io.choerodon.issue.api.service.StateMachineService;
import io.choerodon.issue.domain.IssueType;
import io.choerodon.issue.infra.enums.InitIssueType;
import io.choerodon.issue.infra.mapper.IssueTypeMapper;
import io.choerodon.issue.infra.utils.PageUtil;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author shinan.chen
 * @Date 2018/8/8
 */
@Component
@RefreshScope
public class IssueTypeServiceImpl implements IssueTypeService {

    @Autowired
    private IssueTypeMapper issueTypeMapper;
    @Autowired
    private StateMachineSchemeConfigService stateMachineSchemeConfigService;
    @Autowired
    private StateMachineService stateMachineService;


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
    public PageInfo<IssueTypeWithInfoDTO> queryIssueTypeList(PageRequest pageRequest, Long organizationId, IssueTypeSearchDTO issueTypeSearchDTO) {
        PageInfo<Long> issuetypeIdsPage = PageHelper.startPage(pageRequest.getPage(), pageRequest.getSize(), PageUtil.sortToSql(pageRequest.getSort())).doSelectPageInfo(() -> issueTypeMapper.selectIssueTypeIds(organizationId, issueTypeSearchDTO));
        List<IssueTypeWithInfoDTO> issueTypeWithInfoDTOList = new ArrayList<>(issuetypeIdsPage.getList().size());
        if (issuetypeIdsPage.getList() != null && !issuetypeIdsPage.getList().isEmpty()) {
            issueTypeWithInfoDTOList.addAll(modelMapper.map(issueTypeMapper.queryIssueTypeList(organizationId, issuetypeIdsPage.getList()), new TypeToken<List<IssueTypeWithInfoDTO>>() {
            }.getType()));
        }

        return PageUtil.buildPageInfoWithPageInfoList(issuetypeIdsPage, issueTypeWithInfoDTOList);
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
        List<IssueType> issueTypes = issueTypeMapper.queryByOrgId(organizationId);
        return modelMapper.map(issueTypes, new TypeToken<List<IssueTypeDTO>>() {
        }.getType());
    }

    @Override
    public List<IssueTypeDTO> queryIssueTypeByStateMachineSchemeId(Long organizationId, Long schemeId) {
        List<IssueTypeDTO> issueTypeDTOS = queryByOrgId(organizationId);
        List<StateMachineSchemeConfigDTO> configDTOs = stateMachineSchemeConfigService.queryBySchemeId(true, organizationId, schemeId);
        Map<Long, StateMachineSchemeConfigDTO> configMap = configDTOs.stream().collect(Collectors.toMap(StateMachineSchemeConfigDTO::getIssueTypeId, x -> x));
        for (IssueTypeDTO issueTypeDTO : issueTypeDTOS) {
            StateMachineSchemeConfigDTO configDTO = configMap.get(issueTypeDTO.getId());
            if (configDTO != null) {
                StateMachineDTO stateMachineDTO = stateMachineService.queryStateMachineById(organizationId, configDTO.getStateMachineId());
                issueTypeDTO.setStateMachineName(stateMachineDTO.getName());
                issueTypeDTO.setStateMachineId(stateMachineDTO.getId());
            }
        }
        return issueTypeDTOS;
    }

    @Override
    public void initIssueTypeByConsumeCreateOrganization(Long organizationId) {
        for (InitIssueType initIssueType : InitIssueType.values()) {
            //创建默认问题类型
            createIssueType(new IssueType(initIssueType.getIcon(), initIssueType.getName(), initIssueType.getDescription(), organizationId, initIssueType.getColour(), initIssueType.getTypeCode(), true));
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
            for (InitIssueType initIssueType : InitIssueType.values()) {
                IssueType issueType = createIssueType(new IssueType(initIssueType.getIcon(), initIssueType.getName(), initIssueType.getDescription(), orgId, initIssueType.getColour(), initIssueType.getTypeCode(), true));
                temp.put(initIssueType.getTypeCode(), issueType.getId());
            }
            result.put(orgId, temp);
        }
        return result;
    }
}
