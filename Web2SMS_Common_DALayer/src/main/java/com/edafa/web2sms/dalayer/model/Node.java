package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
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
@Table(name = "NODE")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Node.findAll", query = "SELECT n FROM Node n")
    , @NamedQuery(name = "Node.findByNodeId", query = "SELECT n FROM Node n WHERE n.nodeId = :nodeId")
    , @NamedQuery(name = "Node.findByDescription", query = "SELECT n FROM Node n WHERE n.description = :description")})
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "NODE_ID")
    private Integer nodeId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 30)
    @Column(name = "DESCRIPTION")
    private String description;
    @JoinTable(name = "NODE_SERVICE", joinColumns = {
        @JoinColumn(name = "NODE_ID", referencedColumnName = "NODE_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "SERVICE_ID", referencedColumnName = "SERVICE_ID")})
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Service> serviceList;
    @OneToMany(mappedBy = "nodeId", fetch = FetchType.EAGER)
    private List<ProcessEntity> processList;

    public Node() {
    }

    public Node(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public Node(Integer nodeId, String description) {
        this.nodeId = nodeId;
        this.description = description;
    }

    public Integer getNodeId() {
        return nodeId;
    }

    public void setNodeId(Integer nodeId) {
        this.nodeId = nodeId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public List<Service> getServiceList() {
        return serviceList;
    }

    public void setServiceList(List<Service> serviceList) {
        this.serviceList = serviceList;
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
        hash += (nodeId != null ? nodeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Node)) {
            return false;
        }
        Node other = (Node) object;
        if ((this.nodeId == null && other.nodeId != null) || (this.nodeId != null && !this.nodeId.equals(other.nodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Node{" + "nodeId=" + nodeId + ", description=" + description + ", serviceListSize=" + serviceList.size() + ", processListSize=" + processList.size() + '}';
    }
    
}
