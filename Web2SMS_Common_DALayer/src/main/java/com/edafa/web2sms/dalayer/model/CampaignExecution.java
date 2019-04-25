package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.edafa.web2sms.dalayer.model.constants.CampaignExecutionConst;

/**
 * The persistent class for the CAMPAIGNS_EXECUTION database table.
 * 
 */
@Entity
@Table(name = "CAMPAIGNS_EXECUTION")
@NamedQueries({
		@NamedQuery(name = "CampaignExecution.findAll", query = "SELECT c FROM CampaignExecution c"),
		@NamedQuery(name = "CampaignExecution.updateAction", query = "update CampaignExecution c set c.action = :action where c.campaign.campaignId = :campaignId"),
		@NamedQuery(name = "CampaignExecution.updateActionByAccountIdAndCampStatuses", query = "UPDATE CampaignExecution c set c.action = :action WHERE c.campaign.accountUser.account.accountId = :accountId AND c.campaign.status IN :statuses"),
		@NamedQuery(name = "CampaignExecution.updateExecutionInfo", query = "UPDATE CampaignExecution c set c.submittedSmsCount = :submittedSmsCount, c.submittedSmsSegCount = :submittedSmsSegCount WHERE c.campaign.campaignId = :campaignId"),
		@NamedQuery(name = "CampaignExecution.updateExecutionState", query = "UPDATE CampaignExecution c set c.submittedSmsCount = :submittedSmsCount, c.submittedSmsSegCount = :submittedSmsSegCount, c.startTimestamp = :startTimestamp, c.endTimestamp = :endTimestamp, c.processingTimestamp = :processingTimestamp, c.comments = :comments, c.handlerId = :handlerId, c.executionCount = :executionCount WHERE c.campaign.campaignId = :campaignId") })
public class CampaignExecution implements Serializable, CampaignExecutionConst {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CAMPAIGNS_EXECUTION_ID_GENERATOR", sequenceName = "CAMP_EXE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAMPAIGNS_EXECUTION_ID_GENERATOR")
	@Column(unique = true, nullable = false, precision = 10)
	private long id;

	@Column
	private String comments;

	@Column(name = "PROCESSING_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date processingTimestamp;

	@Column(name = "START_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startTimestamp;

	@Column(name = "END_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endTimestamp;

	@Column(name = "SUBMITTED_SMS_COUNT")
	private Integer submittedSmsCount = 0;

	@Column(name = "SUBMITTED_SMS_SEG_COUNT")
	private Integer submittedSmsSegCount = 0;

	@Column(name = "EXECUTION_COUNT")
	private Integer executionCount = 0;

	@Column(name = "UPDATE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	Date updateTimestamp;

	@Column(name = "HANDLER_ID")
	private String handlerId;

	@JoinColumn(name = "ACTION_ID", referencedColumnName = "CAMPAIGN_ACTION_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH)
	private CampaignAction action;

	@OneToOne
	@JoinColumn(name = "CAMPAIGN_ID")
	private Campaign campaign;

	public CampaignExecution() {
	}

	public CampaignExecution(Campaign campaign) {
		this.campaign = campaign;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public CampaignAction getAction() {
		return action;
	}

	public void setAction(CampaignAction action) {
		this.action = action;
	}

	public String getComments() {
		return this.comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Date getEndTimestamp() {
		return this.endTimestamp;
	}

	public void setEndTimestamp(Date endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public String getHandlerId() {
		return this.handlerId;
	}

	public void setHandlerId(String handlerId) {
		this.handlerId = handlerId;
	}

	public Date getProcessingTimestamp() {
		return this.processingTimestamp;
	}

	public void setProcessingTimestamp(Date processingTimestamp) {
		this.processingTimestamp = processingTimestamp;
	}

	public Date getStartTimestamp() {
		return this.startTimestamp;
	}

	public void setStartTimestamp(Date startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Integer getSubmittedSmsCount() {
		return submittedSmsCount;
	}

	public void setSubmittedSmsCount(Integer submittedSmsCount) {
		this.submittedSmsCount = submittedSmsCount;
	}

	public Integer getSubmittedSmsSegCount() {
		return submittedSmsSegCount;
	}

	public void setSubmittedSmsSegCount(Integer submittedSmsSegCount) {
		this.submittedSmsSegCount = submittedSmsSegCount;
	}

	public Integer getExecutionCount() {
		return executionCount;
	}

	public void setExecutionCount(Integer executionCount) {
		this.executionCount = executionCount;
	}

	public Date getUpdateTimestamp() {
		return this.updateTimestamp;
	}

	public Campaign getCampaign() {
		return this.campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@PrePersist
	private void prePersist() {
		preUpdate();
	}

	@PreUpdate
	private void preUpdate() {
		updateTimestamp = new Date();
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("(startTimestamp=");
        str = str.append(startTimestamp)
        .append(", processingTimestamp=")
        .append(processingTimestamp)
        .append(", endTimestamp=")
        .append(endTimestamp)
        .append(", submittedSmsCount=")
        .append(submittedSmsCount)
        .append(", submittedSmsSegCount=")
        .append(submittedSmsSegCount)
        .append(", action=")
        .append(action)
        .append(", handlerId=")
        .append(handlerId)
        .append(", comments=")
        .append(comments)
        .append(")");
        return str.toString();
    }

    public String logExecutionFullInfo() {
        StringBuilder str = new StringBuilder("startTimestamp=");
        str = str.append(startTimestamp)
        .append(", processingTimestamp=")
        .append(processingTimestamp)
        .append(", updateTimestamp=")
        .append(updateTimestamp)
        .append((endTimestamp != null ? ", endTimestamp=" + endTimestamp : ""))
        .append(", submittedSmsCount=")
        .append(submittedSmsCount)
        .append(", submittedSmsSegCount=")
        .append(submittedSmsSegCount)
        .append(", action=")
        .append(action)
        .append(", comments=")
        .append(comments)
        .append(", handlerId=")
        .append(handlerId);
        return str.toString();
    }

    public String logExecutionInfo() {
        StringBuilder str = new StringBuilder("(submittedSmsCount=");
        str = str.append(submittedSmsCount)
        .append(", submittedSmsSegCount=")
        .append(submittedSmsSegCount)
        .append(")");
        return str.toString();
    }
}
