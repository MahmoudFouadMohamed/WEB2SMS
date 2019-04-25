package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.SMSConcatenationType;
import com.edafa.web2sms.dalayer.model.constants.SMSLogConst;


/**
 * The persistent class for the "SMS_API_VIEW_Camp" database table.
 * 
 */
@Entity
@Table(name="\"SMS_API_VIEW_Camp\"")
@XmlRootElement
@ObjectTypeConverter(name = "SMSConcTypeConverter", dataType = String.class, objectType = SMSConcatenationType.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "PAYLOAD", objectValue = "PAYLOAD"),
		@ConversionValue(dataValue = "FRAGMENTED", objectValue = "FRAGMENTED") })
@NamedQueries({
	@NamedQuery(name="SMS_API_VIEW_Camp.findAll", query="SELECT s FROM SMS_API_VIEW_Camp s"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findAccountAllSms", query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.account.accountId = :account AND s.status.id IN (:deliverd,:undeliverd) AND s.sendReceiveDate >= :startDate AND s.sendReceiveDate <= :endDate"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findAccountMSISDNAllSms", query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.account.accountId = :account AND s.receiver = :msisdn AND s.sendReceiveDate >= :startDate AND s.sendReceiveDate <= :endDate"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findSmsApiOnly",query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NULL"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findSmsApiCampaign",query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NOT NULL"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findAccountSmsApiOnly",query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account"),
	@NamedQuery(name="SMS_API_VIEW_Camp.findAccountSmsApiCampaign",query="SELECT s FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account"),
	@NamedQuery(name="SMS_API_VIEW_Camp.countDeliveredSmsApiOnly",query="SELECT COUNT(s) FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account AND s.status.id = :deliverd"),
	@NamedQuery(name="SMS_API_VIEW_Camp.countUnDeliveredSmsApiOnly",query="SELECT COUNT(s) FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account AND s.status.id = :undeliverd"),
	@NamedQuery(name="SMS_API_VIEW_Camp.countDeliveredSmsApiCampaign",query="SELECT COUNT(s) FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account AND s.status.id = :deliverd"),
	@NamedQuery(name="SMS_API_VIEW_Camp.countUnDeliveredSmsApiCampaign",query="SELECT COUNT(s) FROM SMS_API_VIEW_Camp s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account AND s.status.id = :undeliverd")
})

//SELECT COUNT(c) FROM
	
@Access(AccessType.FIELD)
public class SMS_API_VIEW_Camp implements Serializable,SMSLogConst {
	private static final long serialVersionUID = 1L;

	@Basic(optional = false)
	@Column(name = "COMMENTS")
	protected String comments;

	@Basic(optional = false)
	@Column(name = "CONC_TYPE")
	@Enumerated(EnumType.STRING)
	@Convert(value = "SMSConcTypeConverter")
	protected SMSConcatenationType concType;

	@Basic(optional = false)
	@Column(name = "DELIVERY_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date deliveryDate;

	@JoinColumn(name = "GROUP_ID", referencedColumnName = "CAMPAIGN_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Campaign.class)
	protected Campaign groupId;

	@JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = javax.persistence.CascadeType.DETACH)
	protected Language language;

	@JoinColumn(name = "OWNER_ID", referencedColumnName = "ACCOUNT_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Account.class)
	protected Account account;

	@Basic(optional = false)
	@Column(name = "PROCESSING_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date processingDate;

	@Basic(optional = false)
	@Column(name = "RECEIVER")
	protected String receiver;

	@Basic
	@Column(name = "SEGMENTS_COUNT")
	protected Integer segmentsCount = 0;

	@Basic(optional = false)
	@Column(name = "SEND_RECEIVE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sendReceiveDate;

	@Basic(optional = false)
	@Column(name = "SENDER")
	protected String sender;

	@Id
	@Basic(optional = false)
	@Column(name = "SMS_ID")
	protected String smsId;

	@Basic(optional = false)
	@Column(name = "SMS_TEXT")
	protected String smsText;

	@JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = javax.persistence.CascadeType.DETACH)
	protected SMSStatus status;

	@Basic(optional = false)
	@Column(name = "SUBMIT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date submitDate;

	@Column(name = "TRX_INFO")
	protected String trxInfo;

	@Basic(optional = false)
	@Column(name = "UPDATE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updateDate;
	

	public SMS_API_VIEW_Camp() {
	}
	
	

	public SMS_API_VIEW_Camp(String smsId) {
		this.smsId = smsId;
	}
	
	

	public SMS_API_VIEW_Camp(String smsId, Campaign groupId, String receiver,
			Language language, String smsText, String sender, SMSStatus status,Integer segmentsCount,
			Date submitDate, Date deliveryDate, Date processingDate,
			Date sendReceiveDate, Date updateDate, Account account) {
		this.smsId = smsId;
		this.groupId = groupId;
		this.receiver = receiver;
		this.language = language;
		this.smsText = smsText;
		this.sender = sender;
		this.status = status;
		this.submitDate = submitDate;
		this.deliveryDate = deliveryDate;
		this.processingDate = processingDate;
		this.sendReceiveDate = sendReceiveDate;
		this.updateDate = updateDate;
		this.segmentsCount=segmentsCount;
		this.account = account;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public SMSConcatenationType getConcType() {
		return concType;
	}

	public void setConcType(SMSConcatenationType concType) {
		this.concType = concType;
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	public Campaign getGroupId() {
		return groupId;
	}

	public void setGroupId(Campaign groupId) {
		this.groupId = groupId;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Integer getSegmentsCount() {
		return segmentsCount;
	}

	public void setSegmentsCount(Integer segmentsCount) {
		this.segmentsCount = segmentsCount;
	}

	public Date getSendReceiveDate() {
		return sendReceiveDate;
	}

	public void setSendReceiveDate(Date sendReceiveDate) {
		this.sendReceiveDate = sendReceiveDate;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public SMSStatus getStatus() {
		return status;
	}

	public void setStatus(SMSStatus status) {
		this.status = status;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public String getTrxInfo() {
		return trxInfo;
	}

	public void setTrxInfo(String trxInfo) {
		this.trxInfo = trxInfo;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((smsId == null) ? 0 : smsId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SMSLog)) {
			return false;
		}
		SMSLog other = (SMSLog) object;
		if ((this.smsId == null && other.smsId != null) || (this.smsId != null && !this.smsId.equals(other.smsId))) {
			return false;
		}
		return true;
	}

	public String logSMSId() {
		return "SMS(" + getSmsId() + "): ";
	}

	@Override
	public String toString() {
		return "SMS_API_VIEW_Camp [comments=" + comments + ", concType="
				+ concType + ", deliveryDate=" + deliveryDate + ", groupId="
				+ groupId + ", language=" + language + ", account=" + account
				+ ", processingDate=" + processingDate + ", receiver="
				+ receiver + ", segmentsCount=" + segmentsCount
				+ ", sendReceiveDate=" + sendReceiveDate + ", sender=" + sender
				+ ", smsId=" + smsId + ", smsText=" + smsText + ", status="
				+ status + ", submitDate=" + submitDate + ", trxInfo="
				+ trxInfo + ", updateDate=" + updateDate + "]";
	}



}