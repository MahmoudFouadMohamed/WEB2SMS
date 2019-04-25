package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Embeddable
public class CampAggViewId implements Serializable {

	private static final long serialVersionUID = -7228280286629001223L;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "CAMPAIGN_ID")
	private String campaignId;

	@Basic(optional = false)
	@Column(name = "STATUS_ID")
	private Integer smsStatusId;

	@Column(name = "FLAG")
	private Boolean updatedCamp;

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getSmsStatusId() {
		return smsStatusId;
	}

	public void setSmsStatusId(Integer smsStatusId) {
		this.smsStatusId = smsStatusId;
	}

	public Boolean isUpdatedCamp() {
		return updatedCamp;
	}

	public void setUpdatedCamp(Boolean updatedCamp) {
		this.updatedCamp = updatedCamp;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((smsStatusId == null) ? 0 : smsStatusId.hashCode());
		result = prime * result + ((updatedCamp == null) ? 0 : updatedCamp.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CampAggViewId other = (CampAggViewId) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (smsStatusId == null) {
			if (other.smsStatusId != null)
				return false;
		} else if (!smsStatusId.equals(other.smsStatusId))
			return false;
		if (updatedCamp == null) {
			if (other.updatedCamp != null)
				return false;
		} else if (!updatedCamp.equals(other.updatedCamp))
			return false;
		return true;
	}

}
