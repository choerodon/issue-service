package io.choerodon.issue.api.controller.v1;

import io.choerodon.base.annotation.Permission;
import io.choerodon.base.enums.ResourceType;
import io.choerodon.core.iam.InitRoleCode;
import io.choerodon.issue.api.vo.LookupTypeWithValuesVO;
import io.choerodon.issue.app.service.LookupValueService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by HuangFuqiang@choerodon.io on 2018/09/27.
 * Email: fuqianghuang01@gmail.com
 */
@RestController
@RequestMapping(value = "/v1/organizations/{organization_id}/lookup_values")
public class LookupValueController {

    @Autowired
    private LookupValueService lookupValueService;

    @Permission(type = ResourceType.PROJECT, roles = {InitRoleCode.PROJECT_MEMBER, InitRoleCode.PROJECT_OWNER})
    @ApiOperation("根据type code查询其下的value值")
    @GetMapping(value = "/{typeCode}")
    public ResponseEntity<LookupTypeWithValuesVO> queryLookupValueByCode(@ApiParam(value = "项目id", required = true)
                                                                         @PathVariable(name = "organization_id") Long organizationId,
                                                                         @ApiParam(value = "type code", required = true)
                                                                         @PathVariable String typeCode) {
        return new ResponseEntity<>(lookupValueService.queryLookupValueByCode(organizationId, typeCode), HttpStatus.OK);
    }

}