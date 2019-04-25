package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
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
 * @author akhalifah
 */
@Entity
@Table(name = "SMS_LOG")
@XmlRootElement
@ObjectTypeConverter(name = "SMSConcTypeConverter", dataType = String.class, objectType = SMSConcatenationType.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "PAYLOAD", objectValue = "PAYLOAD"),
		@ConversionValue(dataValue = "FRAGMENTED", objectValue = "FRAGMENTED") })
@NamedQueries({
		@NamedQuery(name = "SMSLog.findAll", query = "SELECT s FROM SMSLog s"),
		@NamedQuery(name = "SMSLog.findByCampIdAndStatus", query = "SELECT con FROM SMSLog s join s.serviceInfo ser join ser.contacts con WHERE s.campaign.campaignId = :campaignId AND s.status IN :statusList "),
		@NamedQuery(name = "SMSLog.findContactByCampIdAndStatus", query = "SELECT con FROM SMSLog s, Contact con  WHERE s.serviceInfo.listId = CON.listContactPK.listId AND SUBSTRING(s.receiver,3) = con.listContactPK.msisdn AND s.campaign.campaignId = :campaignId AND s.status IN :statusList "),
		@NamedQuery(name = "SMSLog.findBySmsId", query = "SELECT s FROM SMSLog s WHERE s.smsId = :smsId"),
		@NamedQuery(name = "SMSLog.findBySender", query = "SELECT s FROM SMSLog s WHERE s.sender = :sender"),
		@NamedQuery(name = "SMSLog.findByReceiver", query = "SELECT s FROM SMSLog s WHERE s.receiver = :receiver"),
		@NamedQuery(name = "SMSLog.findByMSISDNandDates", query = "SELECT s FROM SMSLog s WHERE s.account.accountId = :accountId AND s.receiver = :receiver AND s.sendReceiveDate >= :startDate AND s.sendReceiveDate <= :endDate AND s.campaign.campaignId is null"),
		@NamedQuery(name = "SMSLog.findByDates", query = "SELECT s FROM SMSLog s WHERE s.sendReceiveDate >= :startDate AND s.sendReceiveDate <= :endDate"),
		@NamedQuery(name = "SMSLog.findBySmsText", query = "SELECT s FROM SMSLog s WHERE s.smsText = :smsText"),
		@NamedQuery(name = "SMSLog.findBySubmitTimestamp", query = "SELECT s FROM SMSLog s WHERE s.submitDate = :submitDate"),
		@NamedQuery(name = "SMSLog.findByProcessingTimestamp", query = "SELECT s FROM SMSLog s WHERE s.processingDate = :processingDate"),
		@NamedQuery(name = "SMSLog.findByComments", query = "SELECT s FROM SMSLog s WHERE s.comments = :comments"),
		@NamedQuery(name = "SMSLog.countCampaignLogs", query = "SELECT COUNT(s) FROM SMSLog s WHERE s.campaign.campaignId = :campaignId"),
		@NamedQuery(name = "SMSLog.countAccountLogsByPeriod", query = "SELECT COUNT(s) FROM SMSLog s WHERE s.campaign.accountUser.account.accountId = :accountId AND s.processingDate >= :startDate AND s.processingDate < :endDate"),
		@NamedQuery(name = "SMSLog.countLogsByPeriod", query = "SELECT COUNT(s) FROM SMSLog s WHERE s.processingDate >= :startDate AND s.processingDate < :endDate"),
		@NamedQuery(name = "SMSLog.findByCampaignId", query = "SELECT s FROM SMSLog s WHERE s.campaign.campaignId = :campaignId"),
		@NamedQuery(name = "SMSLog.findDetailedByCampaignId", query = "SELECT s FROM SMSLog s WHERE s.campaign.campaignId = :campaignId ORDER BY s.processingDate"),
		@NamedQuery(name = "SMSLog.findDetailedSmsApiByDates", query = "SELECT s FROM SMSLog s WHERE s.campaign IS NULL AND s.account.accountId = :accountId AND s.processingDate >= :startDate AND s.processingDate < :endDate ORDER BY s.processingDate"),
		@NamedQuery(name = "SMSLog.findDetailedSmsApiBySenderNameAndDate", query = "SELECT s FROM SMSLog s WHERE s.campaign IS NULL AND s.account.accountId = :accountId AND s.sender like :senderName AND s.processingDate >= :startDate AND s.processingDate < :endDate ORDER BY s.processingDate"),
		@NamedQuery(name = "SMSLog.countDetailedSmsApiByDates", query = "SELECT count(s) FROM SMSLog s WHERE s.campaign IS NULL AND s.account.accountId = :accountId AND s.processingDate >= :startDate AND s.processingDate < :endDate ORDER BY s.processingDate"),
		@NamedQuery(name = "SMSLog.countDetailedSmsApiBySenderNameAndDate", query = "SELECT count(s) FROM SMSLog s WHERE s.campaign IS NULL AND s.account.accountId = :accountId AND s.sender like :senderName AND s.processingDate >= :startDate AND s.processingDate < :endDate ORDER BY s.processingDate"),
		@NamedQuery(name = "SMSLog.findByAccountIdAndPeriod", query = "SELECT s FROM SMSLog s WHERE s.campaign.accountUser.account.accountId = :accountId AND s.processingDate >= :startDate AND s.processingDate < :endDate"),
		@NamedQuery(name = "SMSLog.findLogsByPeriod", query = "SELECT s FROM SMSLog s WHERE s.processingDate >= :startDate AND s.processingDate < :endDate"),
		@NamedQuery(name = "SMSLog.updateDeliveredSMSInfo", query = "UPDATE SMSLog s SET s.status = :status WHERE s.smsId = :smsId"),
		@NamedQuery(name = "SMSLog.findSMSbySmsIdList", query = "SELECT s FROM SMSLog s WHERE s.smsId IN :smsIdList"),
		@NamedQuery(name = "SMSLog.findAccountAllSms", query = "SELECT s FROM SMSLog s WHERE s.account.accountId = :account AND s.status.id IN (:deliverd,:undeliverd) AND s.processingDate >= :startDate AND s.processingDate <= :endDate")
})
@NamedNativeQueries({ @NamedNativeQuery(name = "SMSLog.findQuotaHistory", query = "Select TO_CHAR(sl.PROCESSING_TIMESTAMP,'MM'), SUM(sl.SEGMENTS_COUNT) from SMS_LOG sl join CAMPAIGNS camp ON sl.GROUP_ID = camp.CAMPAIGN_ID  "
		+ "JOIN ACCOUNT_USERS ON camp.ACCOUNT_USER_ID = ACCOUNT_USERS.ACCOUNT_USER_ID "
		+ "JOIN ACCOUNTS acct ON acct.ACCOUNT_ID = ACCOUNT_USERS.ACCOUNT_ID "
		+ "WHERE ACCOUNT_USERS.ACCOUNT_ID = ? "
		+ "AND sl.PROCESSING_TIMESTAMP >= to_Timestamp(?) "
		+ "AND sl.PROCESSING_TIMESTAMP < to_Timestamp(?) "
		+ "group by TO_CHAR(sl.PROCESSING_TIMESTAMP,'MM')") })
@Access(AccessType.FIELD)
public class SMSLog implements Serializable, SMSLogConst {
	protected static final long serialVersionUID = 1L;
	// @Id
	// @Basic(optional = false)
	// @Column(name = "ID", insertable = false)
	// protected Long smsLogId;

	@Id
	@Basic(optional = false)
	@Column(name = "SMS_ID")
	protected String smsId;

	@JoinColumn(name = "TRX_INFO", referencedColumnName = "LIST_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.ContactList.class)
//	@Column(name = "TRX_INFO")
//	protected String trxInfo;
	protected ContactList serviceInfo;

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

	@JoinColumn(name = "GROUP_ID", referencedColumnName = "CAMPAIGN_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Campaign.class)
	// @Column(name = "GROUP_ID")
	protected Campaign campaign;
	
	@JoinColumn(name = "OWNER_ID", referencedColumnName = "ACCOUNT_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = javax.persistence.CascadeType.DETACH, targetEntity = com.edafa.web2sms.dalayer.model.Account.class)
	// @Column(name = "GROUP_ID")
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

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "smsLog", fetch = FetchType.LAZY)
	private List<SMSSegmentLog> smsSegmetsLogList;

	public SMSLog() {
	}

	public SMSLog(String smsId) {
		this.smsId = smsId;
	}

//	public SMSLog(String smsId, String trxInfo, String sender, String receiver, String smsText, Date submitDate,
//			Date processingDate, Campaign campaign, SMSStatus status, Language language, Integer segmentsCount,
//			String comments, SMSConcatenationType concType) {
//		this.smsId = smsId;
//		this.trxInfo = trxInfo;
//		this.sender = sender;
//		this.receiver = receiver;
//		this.smsText = smsText;
//		this.submitDate = submitDate;
//		this.processingDate = processingDate;
//		this.campaign = campaign;
//		this.status = status;
//		this.language = language;
//		this.segmentsCount = segmentsCount;
//		this.comments = comments;
//		this.concType = concType;
//	}

	// s.smsId, s.sender, s.receiver, s.status, s.language, s.segmentsCount,
	// s.comments, s.sendReceiveDate, s.deliveryDate

	public SMSLog(String smsId, String sender, String receiver, SMSStatus status, Language language,
			Integer segmentsCount, String comments, Date sendReceiveDate, Date deliveryDate) {
		this.smsId = smsId;
		this.sender = sender;
		this.receiver = receiver;
		// this.smsText = smsText;
		this.status = status;
		this.language = language;
		this.segmentsCount = segmentsCount;
		this.comments = comments;
		this.sendReceiveDate = sendReceiveDate;
		// this.delivered = !delivered.equals("0");
		this.deliveryDate = deliveryDate;
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

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	

public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	//	public String getAppTrxInfo() {
//		return trxInfo;
//	}
//
//	public void setAppTrxInfo(String trxInfo) {
//		this.trxInfo = trxInfo;
//	}
	public ContactList getServiceInfo() {
		return serviceInfo;
	}
	
	public void setServiceInfo(ContactList serviceInfo) {
		this.serviceInfo = serviceInfo;
	}

	public void setSmsSegmetsLogList(List<SMSSegmentLog> smsSegmetsLogList) {
		this.smsSegmetsLogList = smsSegmetsLogList;
	}


	public List<SMSSegmentLog> getSmsSegmetsLogList() {
		return smsSegmetsLogList;
	}

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

	
	
	public Date getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(Date updateDate) {
		this.updateDate = updateDate;
	}

	public SMSStatus getStatus() {
		return status;
	}

	public Language getLanguage() {
		return language;
	}

	public void setSendReceiveDate(Date sendReceiveDate) {
		this.sendReceiveDate = sendReceiveDate;
	}

	public void setDeliveryDate(Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	@Override
	public String toString() {
		return "SMSLog [smsId=" + smsId + ", serviceInfo=" + serviceInfo
				+ ", sender=" + sender + ", receiver=" + receiver
				+ ", smsText=" + smsText + ", submitDate=" + submitDate
				+ ", updateDate=" + updateDate + ", processingDate="
				+ processingDate + ", sendReceiveDate=" + sendReceiveDate
				+ ", deliveryDate=" + deliveryDate + ", campaign=" + campaign
				+ ", accountID=" + account.getAccountId() + ", status=" + status.getName() + ", language="
				+ language + ", segmentsCount=" + segmentsCount + ", comments="
				+ comments + ", concType=" + concType + ", smsSegmetsLogList="
				+ smsSegmetsLogList + "]";
	}

}
