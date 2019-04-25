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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.constants.CampaignStatusConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Cacheable
@Table(name = "CAMPAIGN_STATUS")
@XmlRootElement
@ObjectTypeConverter(name = "CampaignStatusConverter", dataType = String.class, objectType = CampaignStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "NEW", objectValue = "NEW"),
		@ConversionValue(dataValue = "RUNNING", objectValue = "RUNNING"),
		@ConversionValue(dataValue = "PARTIAL_RUN", objectValue = "PARTIAL_RUN"),
		@ConversionValue(dataValue = "PAUSED", objectValue = "PAUSED"),
		@ConversionValue(dataValue = "CANCELLED", objectValue = "CANCELLED"),
		@ConversionValue(dataValue = "FINISHED", objectValue = "FINISHED"),
		@ConversionValue(dataValue = "FAILED", objectValue = "FAILED"),
		@ConversionValue(dataValue = "OBSOLETE", objectValue = "OBSOLETE"),
		@ConversionValue(dataValue = "DELETED", objectValue = "DELETED"),
		@ConversionValue(dataValue = "ON_HOLD", objectValue = "ON_HOLD"),
                @ConversionValue(dataValue = "WAITING_APPROVAL", objectValue = "WAITING_APPROVAL"),
                @ConversionValue(dataValue = "APPROVED", objectValue = "APPROVED"),
                @ConversionValue(dataValue = "APPROVAL_OBSOLETE", objectValue = "APPROVAL_OBSOLETE"),
                @ConversionValue(dataValue = "SEND_OBSOLETE", objectValue = "SEND_OBSOLETE"),
                @ConversionValue(dataValue = "REJECTED", objectValue = "REJECTED")})
@NamedQueries({
		@NamedQuery(name = "CampaignStatus.findAll", query = "SELECT c FROM CampaignStatus c"),
		@NamedQuery(name = "CampaignStatus.findByCampaignStatusId", query = "SELECT c FROM CampaignStatus c WHERE c.campaignStatusId = :campaignStatusId"),
		@NamedQuery(name = "CampaignStatus.findByCampaignStatusName", query = "SELECT c FROM CampaignStatus c WHERE c.campaignStatusName = :campaignStatusName") })
public class CampaignStatus implements Serializable, CampaignStatusConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@Column(name = "CAMPAIGN_STATUS_ID")
	private Integer campaignStatusId;
	@Basic(optional = false)
	@Column(name = "CAMPAIGN_STATUS_NAME")
	@Enumerated(EnumType.STRING)
	@Convert("CampaignStatusConverter")
	private CampaignStatusName campaignStatusName;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "status")
	private List<Campaign> campaignsList;

	public CampaignStatus() {
	}

	public CampaignStatus(Integer campaignStatusId) {
		this.campaignStatusId = campaignStatusId;
	}

	public CampaignStatus(Integer campaignStatusId, CampaignStatusName campaignStatusName) {
		this.campaignStatusId = campaignStatusId;
		this.campaignStatusName = campaignStatusName;
	}

	public CampaignStatus(CampaignStatusName campaignStatusName) {
		this.campaignStatusName = campaignStatusName;
	}

	public Integer getCampaignStatusId() {
		return campaignStatusId;
	}

	public void setCampaignStatusId(Integer campaignStatusId) {
		this.campaignStatusId = campaignStatusId;
	}

	public CampaignStatusName getCampaignStatusName() {
		return campaignStatusName;
	}

	public void setCampaignStatusName(CampaignStatusName campaignStatusName) {
		this.campaignStatusName = campaignStatusName;
	}

	@XmlTransient
	public List<Campaign> getCampaignsList() {
		return campaignsList;
	}

	public void setCampaignsList(List<Campaign> campaignsList) {
		this.campaignsList = campaignsList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (campaignStatusId != null ? campaignStatusId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof CampaignStatus)) {
			return false;
		}
		CampaignStatus other = (CampaignStatus) object;
		if ((this.campaignStatusId == null && other.campaignStatusId != null)
				|| (this.campaignStatusId != null && !this.campaignStatusId.equals(other.campaignStatusId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + campaignStatusId + "," + campaignStatusName + ")";
	}

}
