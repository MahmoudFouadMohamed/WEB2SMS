package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
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
@Table(name = "COMPONENT")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Component.findAll", query = "SELECT c FROM Component c")
    , @NamedQuery(name = "Component.findByComponentId", query = "SELECT c FROM Component c WHERE c.componentId = :componentId")
    , @NamedQuery(name = "Component.findByComponentName", query = "SELECT c FROM Component c WHERE c.componentName = :componentName")})
public class Component implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "COMPONENT_ID")
    private Integer componentId;
    @Size(max = 30)
    @Column(name = "COMPONENT_NAME")
    private String componentName;
    @OneToMany(mappedBy = "componentId", fetch = FetchType.EAGER)
    private List<ProcessEntity> processList;

    public Component() {
    }

    public Component(Integer componentId) {
        this.componentId = componentId;
    }

    public Integer getComponentId() {
        return componentId;
    }

    public void setComponentId(Integer componentId) {
        this.componentId = componentId;
    }

    public String getComponentName() {
        return componentName;
    }

    public void setComponentName(String componentName) {
        this.componentName = componentName;
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
        hash += (componentId != null ? componentId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Component)) {
            return false;
        }
        Component other = (Component) object;
        if ((this.componentId == null && other.componentId != null) || (this.componentId != null && !this.componentId.equals(other.componentId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Component{" + "componentId=" + componentId + ", componentName=" + componentName + ", processListSize=" + processList.size() + '}';
    }
    
}
