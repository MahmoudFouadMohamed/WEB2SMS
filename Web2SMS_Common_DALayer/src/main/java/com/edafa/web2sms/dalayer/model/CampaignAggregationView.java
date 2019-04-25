package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.constants.CampaignAggregationViewConst;

/**
 * 
 * @author khalid
 */
@Entity
@Table(name = "CAMP_AGG")
@XmlRootElement
@ObjectTypeConverter(name = "CampaignStatusConverter", dataType = String.class, objectType = CampaignStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "NEW", objectValue = "NEW"),
		@ConversionValue(dataValue = "RUNNING", objectValue = "RUNNING"),
		@ConversionValue(dataValue = "PARTIAL_RUN", objectValue = "PARTIAL_RUN"),
		@ConversionValue(dataValue = "PAUSED", objectValue = "PAUSED"),
		@ConversionValue(dataValue = "CANCELLED", objectValue = "CANCELLED"),
		@ConversionValue(dataValue = "FINISHED", objectValue = "FINISHED"),
		@ConversionValue(dataValue = "FAILED", objectValue = "FAILED"),
		@ConversionValue(dataValue = "OBSOLETE", objectValue = "OBSOLETE"),
		@ConversionValue(dataValue = "DELETED", objectValue = "DELETED"),
		@ConversionValue(dataValue = "ON_HOLD", objectValue = "ON_HOLD"),
                @ConversionValue(dataValue = "WAITING_APPROVAL", objectValue = "WAITING_APPROVAL"),
                @ConversionValue(dataValue = "APPROVED", objectValue = "APPROVED"),
                @ConversionValue(dataValue = "APPROVAL_OBSOLETE", objectValue = "APPROVAL_OBSOLETE"),
                @ConversionValue(dataValue = "SEND_OBSOLETE", objectValue = "SEND_OBSOLETE"),
                @ConversionValue(dataValue = "REJECTED", objectValue = "REJECTED")})
@NamedQueries({
		@NamedQuery(name = "CampaignAggregationView.findByAccountId", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountId", query = "SELECT COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId"),
		//
		@NamedQuery(name = "CampaignAggregationView.findByAccountIdAndName", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND LOWER(c.campaignName) LIKE :campName ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountIdAndName", query = "SELECT COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND LOWER(c.campaignName) LIKE :campName"),

		@NamedQuery(name = "CampaignAggregationView.findByAccountIdNameAndDateRangeOrdered", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo AND LOWER(c.campaignName) LIKE :campName ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountIdNameAndDates", query = "SELECT COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND  c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo AND  LOWER(c.campaignName) LIKE :campName"),

		@NamedQuery(name = "CampaignAggregationView.findByAccountIdAndStatusAndDateRangeOrdered", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo AND c.campaignStatus IN :statuses ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountIdAndStatusAndDates", query = "SELECT COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo AND c.campaignStatus IN :statuses "),

		@NamedQuery(name = "CampaignAggregationView.findByAccountIdAndDateRangeOrdered", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountIdAndDates", query = "SELECT COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.startTimestamp >= :startTimestampFrom AND c.startTimestamp <= :startTimestampTo"),

		@NamedQuery(name = "CampaignAggregationView.findByAccountIdAndStatus", query = "SELECT c FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.campaignStatus IN :statuses ORDER BY c.startTimestamp DESC"),
		@NamedQuery(name = "CampaignAggregationView.countByAccountIdAndStatus", query = "SELECT  COUNT(DISTINCT(c.campAggViewId.campaignId)) FROM CampaignAggregationView c WHERE c.accountUser.account.accountId = :accountId AND c.campaignStatus IN :statuses"),
		@NamedQuery(name="CampaignAggregationView.findByCampaignId", query="SELECT c FROM CampaignAggregationView c WHERE c.campAggViewId.campaignId =:campId")

})
public class CampaignAggregationView implements Serializable, CampaignAggregationViewConst {

	private static final long serialVersionUID = 1L;

	@EmbeddedId
	protected CampAggViewId campAggViewId;

	// @Basic(optional = false)
	// @NotNull
	// @Id
	// @Size(min = 1, max = 50)
	// @Column(name = "CAMPAIGN_ID")
	// private String campaignId;

	// @Basic(optional = false)
	// @NotNull
	// @Size(max = 50)
	// @JoinColumn(name = "ACCOUNT_USER_ID", referencedColumnName =
	// "ACCOUNT_USER_ID", insertable = false, updatable = false)
	// @ManyToOne(fetch=FetchType.EAGER, optional = false)

	@JoinColumn(name = "ACCOUNT_USER_ID")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private AccountUser accountUser;

	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "CAMPAIGN_NAME")
	private String campaignName;

	@Size(min = 1, max = 50)
	@Column(name = "CAMPAIGN_STATUS")
	@Convert("CampaignStatusConverter")
	@Enumerated(EnumType.STRING)
	private CampaignStatusName campaignStatus;

	@Column(name = "SENDER_NAME")
	private String sender;

	@Column(name = "SMS_TEXT")
	private String smsText;

	@Column(name = "SMS_COUNT")
	private Integer smsCount;

	@Column(name = "SMS_SEG_COUNT")
	private Integer smsSegCount;

	@Column(name = "SUBMITTED_SMS_COUNT")
	private Integer submittedSMSCount;

	@Column(name = "SUBMITTED_SMS_SEG_COUNT")
	private Integer submittedSMSSegCount;

	// @JoinColumn(name = "STATUS_ID")
	// @ManyToOne(fetch = FetchType.EAGER, optional = false)
	// private SMSStatus smsStatus;

	@Column(name = "SMS_STATUS_COUNT")
	private Integer smsStatusCount;

	@Column(name = "SMS_STATUS_SEGMENTS_COUNT")
	private Integer smsStatusSegCount;

	@Column(name = "LISTS_COUNT")
	private Integer listsCount;

	@Column(name = "CREATION_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTimestamp;

	@Column(name = "START_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTimestamp;

	@Column(name = "END_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTimestamp;

	@Column(name = "EXECUTION_COUNT")
	private Integer executionCount;

	// @Column(name = "FLAG")
	// private Boolean updatedCamp;

	public CampaignAggregationView() {

	}

	public String getCampaignId() {
		return campAggViewId != null ? campAggViewId.getCampaignId() : null;
	}

	// public void setCampaignId(String campaignId) {
	// this.campaignId = campaignId;
	// }

	public CampaignStatusName getCampaignStatus() {
		return campaignStatus;
	}

	public void setCampaignStatus(CampaignStatusName campaignStatus) {
		this.campaignStatus = campaignStatus;
	}

	public AccountUser getAccountId() {
		return accountUser;
	}

	public void setAccountId(AccountUser accountId) {
		this.accountUser = accountId;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public Integer getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(Integer smsCount) {
		this.smsCount = smsCount;
	}

	public Integer getSmsSegCount() {
		return smsSegCount;
	}

	public void setSmsSegCount(Integer smsSegCount) {
		this.smsSegCount = smsSegCount;
	}

	public Integer getSubmittedSMSCount() {
		return submittedSMSCount;
	}

	public void setSubmittedSMSCount(Integer submittedSMSCount) {
		this.submittedSMSCount = submittedSMSCount;
	}

	public Integer getSubmittedSMSSegCount() {
		return submittedSMSSegCount;
	}

	public void setSubmittedSMSSegCount(Integer submittedSMSSegCount) {
		this.submittedSMSSegCount = submittedSMSSegCount;
	}

	public Integer getListsCount() {
		return listsCount;
	}

	public void setListsCount(Integer listsCount) {
		this.listsCount = listsCount;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Date getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Date startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Date getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Date endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public Integer getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
	}

	public String getSmsText() {
		return smsText;
	}

	public void setSmsText(String smsText) {
		this.smsText = smsText;
	}

	// public SMSStatus getSMSStatus() {
	// return smsStatus;
	// }

	public Integer getSMSStatus() {
		return campAggViewId != null ? campAggViewId.getSmsStatusId() : null;
	}

	// public void setSMSStatus(SMSStatus smsStatus) {
	// this.smsStatus = smsStatus;
	// }

	public Integer getSmsStatusCount() {
		return smsStatusCount;
	}

	public void setSmsStatusCount(Integer smsStatusCount) {
		this.smsStatusCount = smsStatusCount;
	}

	public Integer getSmsStatusSegCount() {
		return smsStatusSegCount;
	}

	// public SMSStatus getSmsStatus() {
	// return smsStatus;
	// }
	//
	// public void setSmsStatus(SMSStatus smsStatus) {
	// this.smsStatus = smsStatus;
	// }

	public Boolean isUpdatedCamp() {
		return campAggViewId != null ? campAggViewId.isUpdatedCamp() : null;
	}

	//
	// public void setUpdatedCamp(Boolean updatedCamp) {
	// this.updatedCamp = updatedCamp;
	// }

	public void setSmsStatusSegCount(Integer smsStatusSegCount) {
		this.smsStatusSegCount = smsStatusSegCount;
	}

	// @Override
	// public String toString() {
	// return "CampaignAggregationView [campaignId=" + campaignId +
	// ", accountUser=" + accountUser + ", campaignName="
	// + campaignName + ", campaignStatus=" + campaignStatus + ", sender=" +
	// sender + ", smsText=" + smsText
	// + ", smsCount=" + smsCount + ", smsSegCount=" + smsSegCount +
	// ", submittedSMSCount="
	// + submittedSMSCount + ", submittedSMSSegCount=" + submittedSMSSegCount +
	// ", smsStatus=" + smsStatus
	// + ", smsStatusCount=" + smsStatusCount + ", smsStatusSegCount=" +
	// smsStatusSegCount + ", listsCount="
	// + listsCount + ", creationTimestamp=" + creationTimestamp +
	// ", startTimestamp=" + startTimestamp
	// + ", endTimestamp=" + endTimestamp + ", executionCount=" + executionCount
	// + ", updatedCamp="
	// + updatedCamp + "]";
	// }

	@Override
	public String toString() {
		return "CampaignAggregationView [id=" + campAggViewId + ", accountUser=" + accountUser + ", campaignName="
				+ campaignName + ", campaignStatus=" + campaignStatus + ", sender=" + sender + ", smsText=" + smsText
				+ ", smsCount=" + smsCount + ", smsSegCount=" + smsSegCount + ", submittedSMSCount="
				+ submittedSMSCount + ", submittedSMSSegCount=" + submittedSMSSegCount 
//				+ ", smsStatus=" + smsStatus
				+ ", smsStatusCount=" + smsStatusCount + ", smsStatusSegCount=" + smsStatusSegCount + ", listsCount="
				+ listsCount + ", creationTimestamp=" + creationTimestamp + ", startTimestamp=" + startTimestamp
				+ ", endTimestamp=" + endTimestamp + ", executionCount=" + executionCount + "]";
	}

}
