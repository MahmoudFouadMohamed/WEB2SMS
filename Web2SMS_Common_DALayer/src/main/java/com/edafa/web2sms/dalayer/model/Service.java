package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author loay
 */
@Entity
@Table(name = "SERVICE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Service.findAll", query = "SELECT s FROM Service s")
    , @NamedQuery(name = "Service.findByServiceId", query = "SELECT s FROM Service s WHERE s.serviceId = :serviceId")
    , @NamedQuery(name = "Service.findByServiceName", query = "SELECT s FROM Service s WHERE s.serviceName = :serviceName")})
public class Service implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "SERVICE_ID")
    private Integer serviceId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "SERVICE_NAME")
    private String serviceName;
    @ManyToMany(mappedBy = "serviceList", fetch = FetchType.EAGER)
    private List<Node> nodeList;
    @OneToMany(mappedBy = "serviceId", fetch = FetchType.EAGER)
    private List<ProcessEntity> processList;

    public Service() {
    }

    public Service(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public Service(Integer serviceId, String serviceName) {
        this.serviceId = serviceId;
        this.serviceName = serviceName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @XmlTransient
    public List<Node> getNodeList() {
        return nodeList;
    }

    public void setNodeList(List<Node> nodeList) {
        this.nodeList = nodeList;
    }

    @XmlTransient
    public List<ProcessEntity> getProcessList() {
        return processList;
    }

    public void setProcessList(List<ProcessEntity> processList) {
        this.processList = processList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (serviceId != null ? serviceId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Service)) {
            return false;
        }
        Service other = (Service) object;
        if ((this.serviceId == null && other.serviceId != null) || (this.serviceId != null && !this.serviceId.equals(other.serviceId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Service{" + "serviceId=" + serviceId + ", serviceName=" + serviceName + ", nodeListSize=" + nodeList.size() + ", processListSize=" + processList.size() + '}';
    }
    
}
