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
@Table(name = "ALARM_DEFINITIONS")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AlarmDefinitions.findAll", query = "SELECT a FROM AlarmDefinitions a")
    , @NamedQuery(name = "AlarmDefinitions.findByAlarmId", query = "SELECT a FROM AlarmDefinitions a WHERE a.alarmId = :alarmId")
    , @NamedQuery(name = "AlarmDefinitions.findByDescriptions", query = "SELECT a FROM AlarmDefinitions a WHERE a.descriptions = :descriptions")})
public class AlarmDefinitions implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ALARM_ID")
    private Integer alarmId;
    @Size(max = 50)
    @Column(name = "DESCRIPTIONS")
    private String descriptions;
    @JoinColumn(name = "CATEGORYID", referencedColumnName = "CATEGORY_ID")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Categories categoryId;

    public AlarmDefinitions() {
    }

    public AlarmDefinitions(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public Integer getAlarmId() {
        return alarmId;
    }

    public void setAlarmId(Integer alarmId) {
        this.alarmId = alarmId;
    }

    public String getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(String descriptions) {
        this.descriptions = descriptions;
    }

    public Categories getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Categories categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (alarmId != null ? alarmId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AlarmDefinitions)) {
            return false;
        }
        AlarmDefinitions other = (AlarmDefinitions) object;
        if ((this.alarmId == null && other.alarmId != null) || (this.alarmId != null && !this.alarmId.equals(other.alarmId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AlarmDefinitions{" + "alarmId=" + alarmId + ", descriptions=" + descriptions + ", categoryid=" + categoryId.getCategoryId() + '}';
    }
    
}
