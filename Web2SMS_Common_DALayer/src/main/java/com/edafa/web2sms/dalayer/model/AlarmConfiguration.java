package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "CONFIGURATION")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlarmConfiguration.findAll", query = "SELECT c FROM AlarmConfiguration c")
    , @NamedQuery(name = "AlarmConfiguration.findByPropertyId", query = "SELECT c FROM AlarmConfiguration c WHERE c.propertyId = :propertyId")
    , @NamedQuery(name = "AlarmConfiguration.findByPropertyName", query = "SELECT c FROM AlarmConfiguration c WHERE c.propertyName = :propertyName")
    , @NamedQuery(name = "AlarmConfiguration.findByPropertyValue", query = "SELECT c FROM AlarmConfiguration c WHERE c.propertyValue = :propertyValue")
    , @NamedQuery(name = "AlarmConfiguration.findByPropertyDescription", query = "SELECT c FROM AlarmConfiguration c WHERE c.propertyDescription = :propertyDescription")})
public class AlarmConfiguration implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "PROPERTY_ID")
    private Integer propertyId;
    @Size(max = 30)
    @Column(name = "PROPERTY_NAME")
    private String propertyName;
    @Size(max = 40)
    @Column(name = "PROPERTY_VALUE")
    private String propertyValue;
    @Size(max = 200)
    @Column(name = "PROPERTY_DESCRIPTION")
    private String propertyDescription;

    public AlarmConfiguration() {
    }

    public AlarmConfiguration(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public Integer getPropertyId() {
        return propertyId;
    }

    public void setPropertyId(Integer propertyId) {
        this.propertyId = propertyId;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    public String getPropertyValue() {
        return propertyValue;
    }

    public void setPropertyValue(String propertyValue) {
        this.propertyValue = propertyValue;
    }

    public String getPropertyDescription() {
        return propertyDescription;
    }

    public void setPropertyDescription(String propertyDescription) {
        this.propertyDescription = propertyDescription;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (propertyId != null ? propertyId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlarmConfiguration)) {
            return false;
        }
        AlarmConfiguration other = (AlarmConfiguration) object;
        if ((this.propertyId == null && other.propertyId != null) || (this.propertyId != null && !this.propertyId.equals(other.propertyId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AlarmConfiguration{" + "propertyId=" + propertyId + ", propertyName=" + propertyName + ", propertyValue=" + propertyValue + ", propertyDescription=" + propertyDescription + '}';
    }

}
