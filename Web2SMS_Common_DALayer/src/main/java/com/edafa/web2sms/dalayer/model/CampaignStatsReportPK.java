
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
public class CampaignStatsReportPK implements Serializable {

	private static final long serialVersionUID = -4337692166460838730L;

	@Basic(optional = false)
	@Column(name = "CAMPAIGN_ID", length = 150)
	private String campaignId;

	@Basic(optional = false)
	@Column(name = "ACCOUNT_USER_ID", nullable = false, length = 150)
	private String accountUserId;

	public CampaignStatsReportPK() {}

	public CampaignStatsReportPK(String campaignId, String accountUserId) {
		this.campaignId = campaignId;
		this.accountUserId = accountUserId;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getAccountUserId() {
		return accountUserId;
	}

	public void setAccountUserId(String accountUserId) {
		this.accountUserId = accountUserId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accountUserId == null) ? 0 : accountUserId.hashCode());
		result = prime * result + ((campaignId == null) ? 0 : campaignId.hashCode());
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
		CampaignStatsReportPK other = (CampaignStatsReportPK) obj;
		if (accountUserId == null) {
			if (other.accountUserId != null)
				return false;
		} else if (!accountUserId.equals(other.accountUserId))
			return false;
		if (campaignId == null) {
			if (other.campaignId != null)
				return false;
		} else if (!campaignId.equals(other.campaignId))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{campaignId=").append(campaignId).append(", accountUserId=").append(accountUserId).append("}");
		return builder.toString();
	}

}
