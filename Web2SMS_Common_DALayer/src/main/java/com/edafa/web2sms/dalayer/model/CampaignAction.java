/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.model.constants.CampaignActionConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "CAMPAIGN_ACTIONS")
@XmlRootElement
@ObjectTypeConverter(name = "CampaignActionConverter", dataType = String.class, objectType = CampaignActionName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "PAUSE", objectValue = "PAUSE"),
		@ConversionValue(dataValue = "RESUME", objectValue = "RESUME"),
		@ConversionValue(dataValue = "CANCEL", objectValue = "CANCEL"),
		@ConversionValue(dataValue = "HOLD", objectValue = "HOLD"),
		@ConversionValue(dataValue = "UN_HOLD", objectValue = "UN_HOLD"),
                @ConversionValue(dataValue = "APPROVE", objectValue = "APPROVE"),
                @ConversionValue(dataValue = "SEND", objectValue = "SEND"),
                @ConversionValue(dataValue = "REJECT", objectValue = "REJECT")})
                @NamedQueries({
		@NamedQuery(name = "CampaignAction.findAll", query = "SELECT c FROM CampaignAction c"),
		@NamedQuery(name = "CampaignAction.findByCampaignActionId", query = "SELECT c FROM CampaignAction c WHERE c.campaignActionId = :campaignActionId"),
		@NamedQuery(name = "CampaignAction.findByCampaignActionName", query = "SELECT c FROM CampaignAction c WHERE c.campaignActionName = :campaignActionName") })
public class CampaignAction implements Serializable, CampaignActionConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "CAMPAIGN_ACTION_ID")
	private Integer campaignActionId;
	@Size(max = 50)
	@Column(name = "CAMPAIGN_ACTION_NAME")
	@Enumerated(EnumType.STRING)
	@Convert("CampaignActionConverter")
	private CampaignActionName campaignActionName;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "action")
	private List<CampaignExecution> campaignList;

	public CampaignAction() {
	}

	public CampaignAction(Integer campaignActionId) {
		this.campaignActionId = campaignActionId;
	}

	public Integer getCampaignActionId() {
		return campaignActionId;
	}

	public void setCampaignActionId(Integer campaignActionId) {
		this.campaignActionId = campaignActionId;
	}

	public CampaignActionName getCampaignActionName() {
		return campaignActionName;
	}

	public void setCampaignActionName(CampaignActionName campaignActionName) {
		this.campaignActionName = campaignActionName;
	}

	@XmlTransient
	public List<CampaignExecution> getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List<CampaignExecution> campaignList) {
		this.campaignList = campaignList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (campaignActionId != null ? campaignActionId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof CampaignAction)) {
			return false;
		}
		CampaignAction other = (CampaignAction) object;
		if ((this.campaignActionId == null && other.campaignActionId != null)
				|| (this.campaignActionId != null && !this.campaignActionId.equals(other.campaignActionId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + campaignActionId + "," + campaignActionName + ")";
	}

}
