package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.SMSSegmentLogConst;

/**
 * 
 * @author akhalifah
 */
@Entity
@Table(name = "SMS_SEGMENTS_LOG")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "SMSSegmentLog.findAll", query = "SELECT s FROM SMSSegmentLog s"),
		@NamedQuery(name = "SMSSegmentLog.findBySMSId", query = "SELECT s FROM SMSSegmentLog s WHERE s.smsLog.smsId = :smsId"),
		@NamedQuery(name = "SMSSegmentLog.findByMessageId", query = "SELECT s FROM SMSSegmentLog s WHERE s.messageId = :messageId"),
		@NamedQuery(name = "SMSSegmentLog.findByDeliverd", query = "SELECT s FROM SMSSegmentLog s WHERE s.delivered = :deliverd"),
		@NamedQuery(name = "SMSSegmentLog.findBySendTimestamp", query = "SELECT s FROM SMSSegmentLog s WHERE s.sendReceiveDate = :sendTimestamp"),
		@NamedQuery(name = "SMSSegmentLog.findByDeliveryTimestamp", query = "SELECT s FROM SMSSegmentLog s WHERE s.deliveryDate = :deliveryTimestamp"),
		@NamedQuery(name = "SMSSegmentLog.findSMSLogByMessageId", query = "SELECT s.smsLog FROM SMSSegmentLog s WHERE s.messageId = :messageId"),
		@NamedQuery(name = "SMSSegmentLog.findSMSLogByMessageIdAndSMSCId", query = "SELECT s.smsLog FROM SMSSegmentLog s WHERE s.messageId = :messageId AND s.smscId = :smscId"),
		// @NamedQuery(name = "SMSSegmentLog.updateSentSMSInfo", query =
		// "UPDATE SMSSegmentLog s SET s.sendDate = :sendReceiveDate , s.sentSegCount = :sentSegCount, s.smscMessageId = :messageId, s.comments = :comments WHERE s.smsId = :smsId"),
		@NamedQuery(name = "SMSSegmentLog.updateDeliveredSMSSegmentInfo", query = "UPDATE SMSSegmentLog s SET s.deliveryDate = :deliveryDate ,s.delivered = :delivered WHERE s.messageId = :messageId  AND s.smscId = :smscId"),
// @NamedQuery(name = "SMSSegmentLog.updateDeliveredSMSInfoWithStatus", query =
// "UPDATE SMSSegmentLog s SET s.status = :status , s.deliveryDate = :deliveryDate , s.deliveredSegCount = :deliveredSegCount WHERE s.smsId = :smsId")
})
@NamedNativeQueries({
		@NamedNativeQuery(name = "SMSSegmentLog.dropPartition", query = "ALTER TABLE SMS_LOG DROP PARTITION ? "),
		@NamedNativeQuery(name = "SMSSegmentLog.createPartition", query = "ALTER TABLE SMS_LOG "
				+ "REORGANIZE PARTITION PART_MAX INTO " + "(PARTITION ? VALUES LESS THAN " + "(UNIX_TIMESTAMP"
				+ "('?')), " + "PARTITION PART_MAX VALUES LESS THAN MAXVALUE)") })
public class SMSSegmentLog implements Serializable, SMSSegmentLogConst {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "MESSAGE_ID")
	private String messageId;

	@JoinColumn(name = "SMS_ID", referencedColumnName = "SMS_ID")
	@ManyToOne(fetch = FetchType.LAZY)
	private SMSLog smsLog;

	@Basic(optional = false)
	@Column(name = "SEND_RECEIVE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date sendReceiveDate;

	@Basic(optional = true)
	@Column(name = "DELIVERY_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date deliveryDate;

	@Basic(optional = true)
	@Column(name = "DELIVERED")
	private boolean delivered;

	@Basic(optional = true)
	@Column(name = "SMSC_ID")
	private int smscId;

	@Basic(optional = true)
	@Column(name = "STATUS_ID")
	private int statusId = -2;

	public SMSSegmentLog() {
		// TODO Auto-generated constructor stub
	}

	public SMSSegmentLog(String smscMessageId) {
		this.messageId = smscMessageId;
	}

	public SMSSegmentLog(String smscMessageId, SMSLog smsLog, Integer segmentSequence, Date sendDate) {
		this.messageId = smscMessageId;
		this.smsLog = smsLog;
		this.sendReceiveDate = sendDate;
	}

	public void setSmsLog(SMSLog smsLog) {
		this.smsLog = smsLog;
	}

	public SMSLog getSmsLog() {
		return smsLog;
	}

	public Date getSendReceiveDate() {
		return sendReceiveDate;
	}

	public void setSendReceiveDate(Date sendDate) {
		this.sendReceiveDate = sendDate;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String smscMessageId) {
		this.messageId = smscMessageId;
	}

	public void setDelivered(boolean delivered) {
		this.delivered = delivered;
	}

	public boolean isDelivered() {
		return delivered;
	}

	public int getSmscId() {
		return smscId;
	}

	public void setSmscId(int smscId) {
		this.smscId = smscId;
	}

	public int getStatusId() {
		return statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (messageId != null ? messageId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SMSSegmentLog)) {
			return false;
		}
		SMSSegmentLog other = (SMSSegmentLog) object;

		if ((this.messageId == null && other.messageId != null)
				|| (this.messageId != null && !this.messageId.equals(other.messageId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SMSSegmentLog [messageId=" + messageId + ", smsId=" + smsLog.getSmsId() + ", sendReceiveDate="
				+ sendReceiveDate + ", delivered=" + delivered + ", deliveryDate=" + deliveryDate + "]";
	}

	// public String logSMSId() {
	// return "SMS[" + getSmsId() + "]: ";
	// }
	//
	// @Override
	// public String toString() {
	// return "SMSSegmentLog [smsId=" + smsId + ", sender=" + sender +
	// ", receiver="
	// + receiver + ", smsText=" + smsText + ", submitDate="
	// + submitDate + ", sendReceiveDate=" + sendReceiveDate + ", updateDate="
	// + updateDate + ", messageId=" + messageId + ", status="
	// + status + ", language=" + language + ", comments=" + comments
	// + "]";
	// }
}
