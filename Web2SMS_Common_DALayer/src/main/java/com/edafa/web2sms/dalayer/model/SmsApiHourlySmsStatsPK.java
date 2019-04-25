
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigInteger;
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
public class SmsApiHourlySmsStatsPK implements Serializable {

	private static final long serialVersionUID = 1294720777732451971L;

	@Basic(optional = false)
	@Column(name = "PROCESSING_DATE", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingDate;

	@Basic(optional = false)
	@Column(name = "PROCESSING_HOUR", nullable = false)
	private BigInteger processingHour;

	@Basic(optional = false)
	@Column(name = "SENDER_NAME", nullable = false, length = 150)
	private String senderName;

	@Basic(optional = false)
	@Column(name = "OWNER_ID", nullable = false, length = 150)
	private String ownerId;

	public SmsApiHourlySmsStatsPK() {}

	public SmsApiHourlySmsStatsPK(Date processingDate, BigInteger processingHour, String senderName, String ownerId) {
		super();
		this.processingDate = processingDate;
		this.processingHour = processingHour;
		this.senderName = senderName;
		this.ownerId = ownerId;
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

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ownerId == null) ? 0 : ownerId.hashCode());
		result = prime * result + ((processingDate == null) ? 0 : processingDate.hashCode());
		result = prime * result + ((processingHour == null) ? 0 : processingHour.hashCode());
		result = prime * result + ((senderName == null) ? 0 : senderName.hashCode());
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
		SmsApiHourlySmsStatsPK other = (SmsApiHourlySmsStatsPK) obj;
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
		if (senderName == null) {
			if (other.senderName != null)
				return false;
		} else if (!senderName.equals(other.senderName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{processingDate=").append(processingDate).append(", processingHour=").append(processingHour)
				.append(", senderName=").append(senderName).append(", ownerId=").append(ownerId).append("}");
		return builder.toString();
	}

}
