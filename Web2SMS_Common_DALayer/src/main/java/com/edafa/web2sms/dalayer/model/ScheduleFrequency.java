/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.model.constants.ScheduleFrequencyConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Cacheable
@Table(name = "SCHEDULE_FREQUENCY")
@XmlRootElement
@ObjectTypeConverter(name = "SchecduleFrequenceyNameConverter", dataType = String.class, objectType = ScheduleFrequencyName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "ONCE", objectValue = "ONCE"),
		@ConversionValue(dataValue = "DAILY", objectValue = "DAILY"),
		@ConversionValue(dataValue = "WEEKLY", objectValue = "WEEKLY"),
		@ConversionValue(dataValue = "MONTHLY", objectValue = "MONTHLY"),
		@ConversionValue(dataValue = "YEARLY", objectValue = "YEARLY") })
@NamedQueries({
		@NamedQuery(name = "ScheduleFrequency.findAll", query = "SELECT s FROM ScheduleFrequency s"),
		@NamedQuery(name = "ScheduleFrequency.findByScheduleFreqId", query = "SELECT s FROM ScheduleFrequency s WHERE s.scheduleFreqId = :scheduleFreqId"),
		@NamedQuery(name = "ScheduleFrequency.findByScheduleFreqName", query = "SELECT s FROM ScheduleFrequency s WHERE s.scheduleFreqName = :scheduleFreqName") })
public class ScheduleFrequency implements Serializable, ScheduleFrequencyConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "SCHEDULE_FREQ_ID")
	private Integer scheduleFreqId;
	// @Size(max = 30)
	@Column(name = "SCHEDULE_FREQ_NAME")
	@Enumerated(EnumType.STRING)
	@Convert("SchecduleFrequenceyNameConverter")
	private ScheduleFrequencyName scheduleFreqName;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "scheduleFrequency")
	private List<CampaignScheduling> campaignsList;

	public ScheduleFrequency() {
	}

	public ScheduleFrequency(Integer scheduleFreqId) {
		this.scheduleFreqId = scheduleFreqId;
	}

	public ScheduleFrequency(ScheduleFrequencyName freaquencyName) {
		this.scheduleFreqName = freaquencyName;
	}

	public Integer getScheduleFreqId() {
		return scheduleFreqId;
	}

	public void setScheduleFreqId(Integer scheduleFreqId) {
		this.scheduleFreqId = scheduleFreqId;
	}

	public ScheduleFrequencyName getScheduleFreqName() {
		return scheduleFreqName;
	}

	public void setScheduleFreqName(ScheduleFrequencyName scheduleFreqName) {
		this.scheduleFreqName = scheduleFreqName;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (scheduleFreqId != null ? scheduleFreqId.hashCode() : 0);
		return hash;
	}

	@XmlTransient
	public List<CampaignScheduling> getCampaignsList() {
		return campaignsList;
	}

	public void setCampaignsList(List<CampaignScheduling> campaignsList) {
		this.campaignsList = campaignsList;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ScheduleFrequency)) {
			return false;
		}
		ScheduleFrequency other = (ScheduleFrequency) object;
		if ((this.scheduleFreqId == null && other.scheduleFreqId != null)
				|| (this.scheduleFreqId != null && !this.scheduleFreqId.equals(other.scheduleFreqId))) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScheduleFrequency (scheduleFreqName=" + scheduleFreqName + ")";
	}

}
