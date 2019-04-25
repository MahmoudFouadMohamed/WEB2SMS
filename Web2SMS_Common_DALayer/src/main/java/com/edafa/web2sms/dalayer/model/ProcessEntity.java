package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author loay
 */
@Entity
@Table(name = "PROCESS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ProcessEntity.findAll", query = "SELECT p FROM ProcessEntity p")
    , @NamedQuery(name = "ProcessEntity.findByProcessId", query = "SELECT p FROM ProcessEntity p WHERE p.processId = :processId")
    , @NamedQuery(name = "ProcessEntity.findByProcessName", query = "SELECT p FROM ProcessEntity p WHERE p.processName = :processName")})
public class ProcessEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "PROCESS_ID")
    private Integer processId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "PROCESS_NAME")
    private String processName;
    @JoinColumn(name = "COMPONENT_ID", referencedColumnName = "COMPONENT_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Component componentId;
    @JoinColumn(name = "NODEID", referencedColumnName = "NODE_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Node nodeId;
    @JoinColumn(name = "SERVICEID", referencedColumnName = "SERVICE_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private Service serviceId;

    public ProcessEntity() {
    }

    public ProcessEntity(Integer processId) {
        this.processId = processId;
    }

    public ProcessEntity(Integer processId, String processName) {
        this.processId = processId;
        this.processName = processName;
    }

    public Integer getProcessId() {
        return processId;
    }

    public void setProcessId(Integer processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public Component getComponentId() {
        return componentId;
    }

    public void setComponentId(Component componentId) {
        this.componentId = componentId;
    }

    public Node getNodeId() {
        return nodeId;
    }

    public void setNodeId(Node nodeId) {
        this.nodeId = nodeId;
    }

    public Service getServiceId() {
        return serviceId;
    }

    public void setServiceId(Service serviceId) {
        this.serviceId = serviceId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (processId != null ? processId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ProcessEntity)) {
            return false;
        }
        ProcessEntity other = (ProcessEntity) object;
        if ((this.processId == null && other.processId != null) || (this.processId != null && !this.processId.equals(other.processId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "ProcessEntity{" + "processId=" + processId + ", processName=" + processName + ", componentId=" + componentId.getComponentId() + ", nodeid=" + nodeId.getNodeId() + ", serviceid=" + serviceId.getServiceId() + '}';
    }
    
}
