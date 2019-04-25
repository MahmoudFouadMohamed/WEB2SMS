
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Embeddable
public class CampaignDailySmsStatsPK implements Serializable {

	private static final long serialVersionUID = 8470234704225997218L;

	@Basic(optional = false)
	@Column(name = "PROCESSING_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingDate;

	@Basic(optional = false)
	@Column(name = "CAMPAIGN_ID", nullable = false, length = 150)
	private String campaignId;

	@Basic(optional = false)
	@Column(name = "OWNER_ID", nullable = false, length = 150)
	private String ownerId;

	public CampaignDailySmsStatsPK() {}

	public CampaignDailySmsStatsPK(Date processingDate, String campaignId, String ownerId) {
		super();
		this.processingDate = processingDate;
		this.campaignId = campaignId;
		this.ownerId = ownerId;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((processingDate == null) ? 0 : processingDate.hashCode());
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
		CampaignDailySmsStatsPK other = (CampaignDailySmsStatsPK) obj;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		if (ownerId == null) {
			if (other.ownerId != null)
				return false;
		} else if (!ownerId.equals(other.ownerId))
			return false;
		if (processingDate == null) {
			if (other.processingDate != null)
				return false;
		} else if (!processingDate.equals(other.processingDate))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{processingDate=").append(processingDate).append(", campaignId=").append(campaignId)
				.append(", ownerId=").append(ownerId).append("}");
		return builder.toString();
	}

}
