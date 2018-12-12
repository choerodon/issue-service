package io.choerodon.issue.api.controller

import io.choerodon.issue.IntegrationTestConfiguration
import io.choerodon.issue.api.dto.IssueRecordViewDTO
import io.choerodon.issue.api.service.IssueRecordService
import io.choerodon.issue.domain.Issue
import io.choerodon.issue.domain.IssueRecord
import io.choerodon.issue.infra.enums.IssueRecordEnums
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import spock.lang.Shared
import spock.lang.Specification
import spock.lang.Stepwise

/**
 * @author shinan.chen
 * @date 2018/8/13
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@ActiveProfiles("test")
@Stepwise
class IssueRecordServiceSpec extends Specification {
    @Autowired
    IssueRecordService issueRecordService;
    @Shared
    Long testOrginzationId = 1L;
    @Shared
    Long testProjectId = 1L;
    @Shared
    Long testIssueId = 1L;

    def "create"() {
        given: '准备工作'
        IssueRecord issueRecord = new IssueRecord();
        issueRecord.setFieldName(Issue.FIELD_DESCRIPTION);
        issueRecord.setFieldSource(IssueRecordEnums.FieldSource.SYSTEM.value());
        issueRecord.setIssueId(testIssueId);
        issueRecord.setNewValue("newValue");
        issueRecord.setOldValue("oldValue");

        when: '创建事件单修改记录'
        List<IssueRecord> issueRecordList = issueRecordService.create(testProjectId, testIssueId, issueRecord);

        then: '创建记录成功，返回事件单下所有修改记录'
        issueRecordList.size() > 0
    }

    def "createList"() {
        given: '准备工作'
        List<IssueRecord> insertList = new ArrayList<>();
        IssueRecord issueRecord1 = new IssueRecord();
        issueRecord1.setFieldName("fileName");
        issueRecord1.setFieldSource(IssueRecordEnums.FieldSource.ATTACHMENT.value());
        issueRecord1.setIssueId(testIssueId);
        issueRecord1.setNewValue("file.txt");

        IssueRecord issueRecord2 = new IssueRecord();
        issueRecord2.setFieldName("fileName");
        issueRecord2.setFieldSource(IssueRecordEnums.FieldSource.ATTACHMENT.value());
        issueRecord2.setIssueId(testIssueId);
        issueRecord2.setNewValue("file.txt");

        IssueRecord issueRecord3 = new IssueRecord();
        issueRecord3.setFieldName("fileName");
        issueRecord3.setFieldSource(IssueRecordEnums.FieldSource.ATTACHMENT.value());
        issueRecord3.setIssueId(testIssueId);
        issueRecord3.setNewValue("file.txt");

        insertList.add(issueRecord1);
        insertList.add(issueRecord2);
        insertList.add(issueRecord3);

        when: '创建事件单修改记录'
        List<IssueRecord> issueRecordList = issueRecordService.create(testProjectId, testIssueId, insertList);

        then: '创建记录成功，返回事件单下所有修改记录'
        issueRecordList.size() > 0
    }

    def "queryByIssueId"() {
        when: '查询事件单修改记录'
        List<IssueRecordViewDTO> viewDTOList = issueRecordService.queryByIssueId(testProjectId, issueId)

        then: '创建记录成功，返回事件单下所有修改记录'
        viewDTOList.size() == size

        where: '测试用例：'
        issueId     || size
        testIssueId || 2
        10          || 0
    }

}
