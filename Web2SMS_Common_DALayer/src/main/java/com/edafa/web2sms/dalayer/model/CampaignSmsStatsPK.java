
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Embeddable
public class CampaignSmsStatsPK implements Serializable {

	private static final long serialVersionUID = 2031609046801007102L;

	@Basic(optional = false)
	@Column(name = "CAMPAIGN_ID", nullable = false, length = 150)
	private String campaignId;

	@Basic(optional = false)
	@Column(name = "OWNER_ID", nullable = false, length = 150)
	private String ownerId;

	public CampaignSmsStatsPK() {}

	public CampaignSmsStatsPK(String campaignId, String ownerId) {
		super();
		this.campaignId = campaignId;
		this.ownerId = ownerId;
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
		CampaignSmsStatsPK other = (CampaignSmsStatsPK) obj;
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
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{campaignId=").append(campaignId).append(", ownerId=").append(ownerId).append("}");
		return builder.toString();
	}

}
