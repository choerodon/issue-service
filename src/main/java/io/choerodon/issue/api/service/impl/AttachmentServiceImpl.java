package io.choerodon.issue.api.service.impl;

import io.choerodon.issue.api.dto.AttachmentDTO;
import io.choerodon.issue.api.service.AttachmentService;
import io.choerodon.issue.api.service.UserService;
import io.choerodon.issue.domain.Attachment;
import io.choerodon.issue.infra.feign.FileFeignClient;
import io.choerodon.issue.infra.mapper.AttachmentMapper;
import io.choerodon.core.convertor.ConvertHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.service.BaseServiceImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author jiameng.cao
 * @date 2018/9/3
 */
@Component
@RefreshScope
public class AttachmentServiceImpl extends BaseServiceImpl<Attachment> implements AttachmentService {
    private static final String BACKETNAME = "cloopm-service";
    private static final String INSERT_ERROR = "error.Attachment.create";

    @Autowired
    AttachmentMapper attachmentMapper;
    @Autowired
    private FileFeignClient fileFeignClient;

    @Autowired
    private UserService userService;

    private final ModelMapper modelMapper = new ModelMapper();

    @Value("${services.attachment.url}")
    private String attachmentUrl;

    @Override
    public List<AttachmentDTO> listQuery(AttachmentDTO attachmentDTO, String params) {
        Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
        List<Attachment> attachmentList = attachmentMapper.fulltextSearch(attachment, params);
        return modelMapper.map(attachmentList, new TypeToken<List<AttachmentDTO>>() {
        }.getType());
    }

    @Override
    public List<AttachmentDTO> queryByIssue(AttachmentDTO attachmentDTO, Long issueId) {
        Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
        List<Attachment> attachmentList = attachmentMapper.queryByIssue(attachment, issueId);
        List<AttachmentDTO> attachmentDTOS = modelMapper.map(attachmentList, new TypeToken<List<AttachmentDTO>>() {}.getType());
        //补全用户信息
        userService.handleAttachmentUser(attachmentDTOS);
        return attachmentDTOS;
    }

    @Override
    public List<AttachmentDTO> create(Long projectId, AttachmentDTO attachmentDTO, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (files != null && !files.isEmpty()) {
            for (MultipartFile multipartFile : files) {
                String fileName = multipartFile.getOriginalFilename();
                ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
                if (response == null || response.getStatusCode() != HttpStatus.OK) {
                    throw new CommonException("error.attachment.upload");
                }
                dealIssue(attachmentDTO);
            }
        }
        Attachment attachment = new Attachment();
        attachment.setResourceId(attachmentDTO.getResourceId());
        attachment.setResourceType(attachmentDTO.getResourceType());
        return ConvertHelper.convertList(attachmentMapper.select(attachment), AttachmentDTO.class);
    }

    private void dealIssue(AttachmentDTO attachmentDTO) {
        Attachment attachment = modelMapper.map(attachmentDTO, Attachment.class);
        createAttachemnt(attachment);
    }

    private String dealUrl(String url) {
        String dealUrl = null;
        try {
            URL netUrl = new URL(url);
            dealUrl = netUrl.getFile().substring(BACKETNAME.length() + 2);
        } catch (MalformedURLException e) {
            throw new CommonException(e.getMessage());
        }
        return dealUrl;
    }


    @Override
    public Boolean delete(Long projectId, Long attachmentId) {
        Map<String, Object> result = checkDelete(projectId, attachmentId);
        Boolean canDelete = (Boolean) result.get("canDelete");
        if (canDelete) {
            String url = null;
            try {
                Attachment attachment = attachmentMapper.selectByPrimaryKey(attachmentId);
                url = URLDecoder.decode(attachment.getFileUrl(), "UTF-8");
            } catch (IOException i) {
                throw new CommonException(i.getMessage());
            }
            ResponseEntity<String> response = fileFeignClient.deleteFile(BACKETNAME, attachmentUrl + url);
            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                throw new CommonException("error.attachment.delete");
            }
            int isDelete = attachmentMapper.deleteByPrimaryKey(attachmentId);
            if (isDelete != 1) {
                throw new CommonException("error.state.delete");
            }
        } else {
            return false;
        }
        return true;

    }

    @Override
    public Map<String, Object> checkDelete(Long projectId, Long attachmentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        Attachment attachment = attachmentMapper.selectByPrimaryKey(attachmentId);
        if (attachment == null) {
            throw new CommonException("error.base.notFound");
        } else if (!attachment.getProjectId().equals(projectId)) {
            throw new CommonException("error.issueReply.illegal");
        }
        return result;
    }

    @Override
    public Attachment createAttachemnt(Attachment attachment) {
        if (attachmentMapper.insert(attachment) != 1) {
            throw new CommonException(INSERT_ERROR);
        }
        return attachmentMapper.selectByPrimaryKey(attachment.getId());
    }

    @Override
    public List<String> uploadForAddress(Long projectId, HttpServletRequest request) {
        List<MultipartFile> files = ((MultipartHttpServletRequest) request).getFiles("file");
        if (!(files != null && !files.isEmpty())) {
            throw new CommonException("error.attachment.exits");
        }
        List<String> result = new ArrayList<>();
        for (MultipartFile multipartFile : files) {
            String fileName = multipartFile.getOriginalFilename();
            ResponseEntity<String> response = fileFeignClient.uploadFile(BACKETNAME, fileName, multipartFile);
            if (response == null || response.getStatusCode() != HttpStatus.OK) {
                throw new CommonException("error.attachment.upload");
            }
            result.add(dealUrl(response.getBody()));
        }
        return result;
    }
}
