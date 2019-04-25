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
 * 
 * @author Khalid
 */
@Entity
@Table(name = "SMS_API_VIEW")
@XmlRootElement
@ObjectTypeConverter(name = "SMSConcTypeConverter", dataType = String.class, objectType = SMSConcatenationType.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "PAYLOAD", objectValue = "PAYLOAD"),
		@ConversionValue(dataValue = "FRAGMENTED", objectValue = "FRAGMENTED") })
@NamedQueries({
	@NamedQuery(name="SMSAPIView.findAccountAllSms", query="SELECT s FROM SMSAPIView s WHERE s.account.accountId = :account AND s.status.id IN (:deliverd,:undeliverd) AND s.processingDate >= :startDate AND s.processingDate <= :endDate"),
	@NamedQuery(name="SMSAPIView.findSMSbySmsIdList", query="SELECT s FROM SMSAPIView s WHERE s.smsId IN :smsIdList"),
        @NamedQuery(name="SMSAPIView.findByAccountAndMSISDNAllSms", query="SELECT s FROM SMSAPIView s WHERE s.account.accountId = :account AND s.receiver = :receiver AND s.processingDate >= :startDate AND s.processingDate <= :endDate order by s.processingDate desc"),
	@NamedQuery(name="SMSAPIView.findSmsApiOnly",query="SELECT s FROM SMSAPIView s WHERE s.groupId.campaignId IS NULL"),
	@NamedQuery(name="SMSAPIView.findSmsApiCampaign",query="SELECT s FROM SMSAPIView s WHERE s.groupId.campaignId IS NOT NULL"),
	@NamedQuery(name="SMSAPIView.findAccountSmsApiOnly",query="SELECT s FROM SMSAPIView s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account"),
	@NamedQuery(name="SMSAPIView.findAccountSmsApiCampaign",query="SELECT s FROM SMSAPIView s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account"),
	@NamedQuery(name="SMSAPIView.countDeliveredSmsApiOnly",query="SELECT COUNT(s) FROM SMSAPIView s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account AND s.status.id = :deliverd"),
	@NamedQuery(name="SMSAPIView.countUnDeliveredSmsApiOnly",query="SELECT COUNT(s) FROM SMSAPIView s WHERE s.groupId.campaignId IS NULL AND s.account.accountId = :account AND s.status.id = :undeliverd"),
	@NamedQuery(name="SMSAPIView.countDeliveredSmsApiCampaign",query="SELECT COUNT(s) FROM SMSAPIView s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account AND s.status.id = :deliverd"),
	@NamedQuery(name="SMSAPIView.countUnDeliveredSmsApiCampaign",query="SELECT COUNT(s) FROM SMSAPIView s WHERE s.groupId.campaignId IS NOT NULL AND s.account.accountId = :account AND s.status.id = :undeliverd"),
		
	@NamedQuery(name = "SMSAPIView.findAll", query = "SELECT s FROM SMSAPIView s")
})
	
@Access(AccessType.FIELD)
public class SMSAPIView implements Serializable, SMSLogConst {
	protected static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "SMS_ID")
	protected String smsId;

	@Column(name = "TRX_INFO")
	protected String trxInfo;

	@Basic(optional = false)
	@Column(name = "SENDER")
	protected String sender;

	@Basic(optional = false)
	@Column(name = "RECEIVER")
	protected String receiver;

	@Basic(optional = false)
	@Column(name = "SMS_TEXT")
	protected String smsText;

	@Basic(optional = false)
	@Column(name = "SUBMIT_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date submitDate;

	@Basic(optional = false)
	@Column(name = "UPDATE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updateDate;

	@Basic(optional = false)
	@Column(name = "PROCESSING_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date processingDate;

	@Basic(optional = false)
	@Column(name = "SEND_RECEIVE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date sendReceiveDate;

	@Basic(optional = false)
	@Column(name = "DELIVERY_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date deliveryDate;
	
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "ACCOUNT_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Account.class)
	protected Account account;

	@JoinColumn(name = "STATUS_ID", referencedColumnName = "STATUS_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = javax.persistence.CascadeType.DETACH)
	protected SMSStatus status;

	@JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER, cascade = javax.persistence.CascadeType.DETACH)
	private Language language;

	@Basic
	@Column(name = "SEGMENTS_COUNT")
	private Integer segmentsCount = 0;

	@Basic(optional = false)
	@Column(name = "COMMENTS")
	private String comments;

	@Basic(optional = false)
	@Column(name = "CONC_TYPE")
	@Enumerated(EnumType.STRING)
	@Convert(value = "SMSConcTypeConverter")
	private SMSConcatenationType concType;

	@JoinColumn(name = "GROUP_ID", referencedColumnName = "CAMPAIGN_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Campaign.class)
	protected Campaign groupId;
//	@OneToMany(cascade = CascadeType.ALL, mappedBy = "smsLog", fetch = FetchType.LAZY)
//	private List<SMSSegmentLog> smsSegmetsLogList;

	public SMSAPIView() {
	}

	public SMSAPIView(String smsId) {
		this.smsId = smsId;
	}

	public String getSmsId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	public Date getSubmitDate() {
		return submitDate;
	}

	public void setSubmitDate(Date submitDate) {
		this.submitDate = submitDate;
	}

	public SMSStatus getSMSStatus() {
		return status;
	}

	public void setStatus(SMSStatus status) {
		this.status = status;
	}

	public Language getSMSLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Integer getSegmentsCount() {
		return segmentsCount;
	}

	public void setSegmentsCount(Integer segmentsCount) {
		this.segmentsCount = segmentsCount;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	public String getAppTrxInfo() {
		return trxInfo;
	}

	public void setAppTrxInfo(String trxInfo) {
		this.trxInfo = trxInfo;
	}

//	public void setSmsSegmetsLogList(List<SMSSegmentLog> smsSegmetsLogList) {
//		this.smsSegmetsLogList = smsSegmetsLogList;
//	}
//
//	public List<SMSSegmentLog> getSmsSegmetsLogList() {
//		return smsSegmetsLogList;
//	}

	public String getComments() {
		return comments;
	}

	/**
	 * @param comments
	 *            the comments to set
	 */
	public void setComments(String comments) {
		this.comments = comments;
	}

	public SMSConcatenationType getConcType() {
		return concType;
	}

	public void setConcType(SMSConcatenationType concType) {
		this.concType = concType;
	}

	public Date getSendReceiveDate() {
		return sendReceiveDate != null ? sendReceiveDate : getProcessingDate();
	}

	public Date getDeliveryDate() {
		return deliveryDate;
	}

	
	
	public Campaign getGroupId() {
		return groupId;
	}

	public void setGroupId(Campaign groupId) {
		this.groupId = groupId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (smsId != null ? smsId.hashCode() : 0);
		return hash;
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
		return "SMSAPIView [smsId=" + smsId + ", trxInfo=" + trxInfo
				+ ", sender=" + sender + ", receiver=" + receiver
				+ ", smsText=" + smsText + ", submitDate=" + submitDate
				+ ", updateDate=" + updateDate + ", processingDate="
				+ processingDate + ", sendReceiveDate=" + sendReceiveDate
				+ ", deliveryDate=" + deliveryDate + ", account=" + account
				+ ", status=" + status + ", language=" + language
				+ ", segmentsCount=" + segmentsCount + ", comments=" + comments
				+ ", concType=" + concType + "]";
	}
	
	

/*	@Override
	public String toString() {
		return "SMSAPIView [smsId=" + smsId + ", sender=" + sender + ", receiver=" + receiver + ", smsText=" + smsText
				+ ", submitDate=" + submitDate + ", processingDate=" + processingDate + ", account=" + account
				+ ", status=" + status + ", language=" + language + ", segmentsCount=" + segmentsCount + ", concType="
				+ concType + "]";
	}*/
	
}
