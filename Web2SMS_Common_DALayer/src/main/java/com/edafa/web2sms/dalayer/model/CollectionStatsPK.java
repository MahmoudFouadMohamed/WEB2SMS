
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.edafa.web2sms.dalayer.enums.StatsStatsType;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Embeddable
public class CollectionStatsPK implements Serializable {

	private static final long serialVersionUID = -1240631552214535117L;

	@Basic(optional = false)
	@Column(name = "PROCESSING_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingDate;

	@Basic(optional = false)
	@Column(name = "PROCESSING_HOUR", nullable = false)
	private BigInteger processingHour;

	@Basic(optional = false)
	@Column(name = "GROUP_ID", nullable = false, length = 150)
	private String groupId;

	@Basic(optional = false)
	@Column(name = "OWNER_ID", nullable = false, length = 150)
	private String ownerId;

	@Basic(optional = false)
	@Column(name = "STATS_TYPE", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private StatsStatsType statsType;

	public CollectionStatsPK() {}

	public CollectionStatsPK(Date processingDate, BigInteger processingHour, String groupId, String ownerId,
			StatsStatsType statsType) {
		this.processingDate = processingDate;
		this.processingHour = processingHour;
		this.groupId = groupId;
		this.ownerId = ownerId;
		this.statsType = statsType;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public BigInteger getProcessingHour() {
		return processingHour;
	}

	public void setProcessingHour(BigInteger processingHour) {
		this.processingHour = processingHour;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public StatsStatsType getStatsType() {
		return statsType;
	}

	public void setStatsType(StatsStatsType statsType) {
		this.statsType = statsType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((processingDate == null) ? 0 : processingDate.hashCode());
		result = prime * result + ((processingHour == null) ? 0 : processingHour.hashCode());
		result = prime * result + ((statsType == null) ? 0 : statsType.hashCode());
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
		CollectionStatsPK other = (CollectionStatsPK) obj;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
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
		if (processingHour == null) {
			if (other.processingHour != null)
				return false;
		} else if (!processingHour.equals(other.processingHour))
			return false;
		if (statsType != other.statsType)
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{processingDate=").append(processingDate).append(", processingHour=").append(processingHour)
				.append(", groupId=").append(groupId).append(", ownerId=").append(ownerId).append(", statsType=")
				.append(statsType).append("}");
		return builder.toString();
	}

}
