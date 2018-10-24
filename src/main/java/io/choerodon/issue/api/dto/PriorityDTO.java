package io.choerodon.issue.api.dto;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.math.BigDecimal;

/**
 * @author cong.cheng
 * @Date 2018/8/21
 */

public class PriorityDTO {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String description;
    private String colour;
    private Long organizationId;
    private Boolean isDefault;
    private Long objectVersionNumber;
    private BigDecimal sequence;

    @Override
    public String toString() {
        return "PriorityDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", colour='" + colour + '\'' +
                ", organizationId=" + organizationId +
                ", isDefault='" + isDefault + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", sequence=" + sequence +
                '}';
    }

    public BigDecimal getSequence() {
        return sequence;
    }

    public void setSequence(BigDecimal sequence) {
        this.sequence = sequence;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }

    public Boolean getDefault() {
        return isDefault;
    }
}
