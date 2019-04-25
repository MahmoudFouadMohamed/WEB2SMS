/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import static javax.persistence.AccessType.FIELD;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.CampaignConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "CAMPAIGNS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Campaign.findAll", query = "SELECT c FROM Campaign c"),
		@NamedQuery(name = "Campaign.findByCampaignId", query = "SELECT c FROM Campaign c WHERE c.campaignId = :campaignId"),
		@NamedQuery(name = "Campaign.findByIdAndAccountId", query = "SELECT c FROM Campaign c WHERE c.accountUser.account.accountId = :accountId AND c.campaignId = :campaignId"),
		@NamedQuery(name = "Campaign.findByCampaignIds", query = "SELECT c FROM Campaign c join fetch c.campaignExecution join fetch c.campaignScheduling join fetch c.smsDetails WHERE c.campaignId IN :campaignIds"),
		@NamedQuery(name = "Campaign.findByName", query = "SELECT c FROM Campaign c WHERE c.name = :name"),
		@NamedQuery(name = "Campaign.findByDescription", query = "SELECT c FROM Campaign c WHERE c.description = :description"),
		@NamedQuery(name = "Campaign.findByCreationTimestamp", query = "SELECT c FROM Campaign c WHERE c.creationTimestamp = :creationTimestamp"),
		@NamedQuery(name = "Campaign.findByAccountAndStatuses", query = "SELECT c FROM Campaign c WHERE c.accountUser.account.accountId = :accountId and c.status IN :statuses ORDER BY c.campaignScheduling.scheduleStartTimestamp DESC, c.name ASC"),
		@NamedQuery(name = "Campaign.findByStatuses", query = "SELECT c FROM Campaign c WHERE c.status IN :statuses AND c.accountUser.account.status = :accountStatus"),
		@NamedQuery(name = "Campaign.findTimedoutCampaigns", query = "SELECT c FROM Campaign c WHERE (c.campaignScheduling.scheduleStartTimestamp < :validityTimestamp) OR (c.campaignScheduling.scheduleEndDate < CURRENT_DATE)"),
		@NamedQuery(name = "Campaign.findCampSMSStats", query = "SELECT new com.edafa.web2sms.dalayer.pojo.CampSMSStats(camp.campaignId, sl.status.name, COUNT(sl.smsId), SUM(sl.segmentsCount)) FROM Campaign as camp, SMSLog sl join CAMP.campaignExecution camp_exec WHERE camp = sl.campaign AND CAMP.campaignId IN :campaignIds AND sl.processingDate >= (SELECT min(camp_exec.startTimestamp) FROM CampaignExecution as camp_exec where CAMP.campaignId IN :campaignIds) group by camp.campaignId, sl.status.name"),
		@NamedQuery(name = "Campaign.findCampFrequency", query = "SELECT new com.edafa.web2sms.dalayer.pojo.CampaignFrequency(camp.campaignId, camp.campaignScheduling.scheduleFrequency.scheduleFreqName) FROM Campaign as camp join fetch CAMP.campaignScheduling Where camp.campaignId IN :campaignIds"),
		@NamedQuery(name = "Campaign.countUserCampaigns", query = "SELECT COUNT(c) FROM Campaign c WHERE c.accountUser.account.accountId = :accountId AND c.status In :statuses"),
		@NamedQuery(name = "Campaign.countByIdAndAccountIdAndStatus", query = "SELECT COUNT(c) FROM Campaign c WHERE c.accountUser.account.accountId = :accountId AND c.campaignId = :campaignId AND c.status IN :statuses"),
		@NamedQuery(name = "Campaign.countByNameAndAccountIdAndStatus", query = "SELECT COUNT(c) FROM Campaign c WHERE c.accountUser.account.accountId = :accountId AND c.name = :name AND c.status IN :statuses"),
		@NamedQuery(name = "Campaign.removeById", query = "DELETE FROM Campaign c WHERE c.campaignId = :campaignId"),
		@NamedQuery(name = "Campaign.getCampaignStatus", query = "SELECT c.status FROM Campaign c WHERE c.campaignId = :campaignId"),
		@NamedQuery(name = "Campaign.updateCampaignStatusByAccountId", query = "UPDATE Campaign c set c.status = :status WHERE c.accountUser.account.accountId = :accountId AND c.campaignId = :campaignId"),
		@NamedQuery(name = "Campaign.updateCampaignStatus", query = "UPDATE Campaign c set c.status = :status WHERE c.campaignId = :campaignId") })
@NamedNativeQueries({ @NamedNativeQuery(name = "Campaign.findExecutableCampaigns", query = "SELECT C.CAMPAIGN_ID "
		+ "FROM CAMPAIGNS C "
		+ "LEFT OUTER JOIN ACCOUNT_USERS ACCT_USR ON C.ACCOUNT_USER_ID = ACCT_USR.ACCOUNT_USER_ID "
		+ "LEFT OUTER JOIN ACCOUNTS ACCT ON ACCT_USR.ACCOUNT_ID = ACCT.ACCOUNT_ID "
		+ "LEFT OUTER JOIN ACCOUNT_STATUS ACCT_ST ON ACCT_ST.ACCOUNT_STATUS_ID = ACCT.STATUS_ID "
		+ "LEFT OUTER JOIN CAMPAIGNS_EXECUTION CE ON CE.CAMPAIGN_ID = C.CAMPAIGN_ID "
		+ "LEFT OUTER JOIN CAMPAIGNS_SCHEDULING CSC ON CSC.CAMPAIGN_ID = C.CAMPAIGN_ID "
		+ "LEFT OUTER JOIN CAMPAIGNS_SMS_DETAILS CSD ON CSD.CAMPAIGN_ID = C.CAMPAIGN_ID "
		+ "LEFT OUTER JOIN CAMPAIGN_STATUS CS ON CS.CAMPAIGN_STATUS_ID = C.STATUS_ID "
		+ "LEFT OUTER JOIN SCHEDULE_FREQUENCY SCF ON SCF.SCHEDULE_FREQ_ID = CSC.SCHEDULE_FREQUENCY_ID "
		+ "LEFT OUTER JOIN CAMPAIGN_ACTIONS CA ON CA.CAMPAIGN_ACTION_ID = CE.ACTION_ID "
		+ "WHERE "
		+ "ACCT_ST.ACCOUNT_STATUS_NAME = 'ACTIVE' "
		+ "AND "
		+ "("
		+ "CE.HANDLER_ID IS NULL "
		+ "AND "
		+ "	("
		+ "    ("
		+ "    	 ("
		+ "      	CS.CAMPAIGN_STATUS_NAME = 'NEW' "
		+ "		 	OR "
		+ "		 	( " // For rescheduled campaigns
		+ "			  CS.CAMPAIGN_STATUS_NAME = 'PARTIAL_RUN' "
		+ "       	  AND "
		+ "           CE.PROCESSING_TIMESTAMP <= CSC.SCHEDULE_START_TIMESTAMP "
		+ "			) "
		+ "		 ) "
		+ "      AND"
		+ "      CAST(TRUNC(CSC.SCHEDULE_START_TIMESTAMP) AS DATE) = TRUNC(SYSDATE) "
		+ "      AND "
		+ "      (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) <= (SYSTIMESTAMP - TRUNC(SYSTIMESTAMP)) "
		+ "      AND "
		+ "      (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) >= ((CAST(SYSTIMESTAMP-(?)/24 AS TIMESTAMP))- TRUNC(SYSTIMESTAMP)) "
		+ "    ) "
		+ "    OR "
		+ "     ("
		+ "       ( "
		+ "      	CS.CAMPAIGN_STATUS_NAME = 'NEW' " // it the first time at
													// scheduleStartTime is
													// missed
		+ "		  	OR "
		+ "			CS.CAMPAIGN_STATUS_NAME = 'PARTIAL_RUN' "
		+ "       	AND "
		+ "         CAST(TRUNC(CE.PROCESSING_TIMESTAMP) AS DATE) != TRUNC(SYSDATE) "
		+ "		  ) "
		+ "       AND "
		+ "       (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) <= (SYSTIMESTAMP - TRUNC(SYSTIMESTAMP)) "
		+ "       AND "
		+ "       (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) >= ((CAST(SYSTIMESTAMP-(?)/24 AS TIMESTAMP))- TRUNC(SYSTIMESTAMP)) "
		+ "       AND "
		+ "       ("
		+ "         (  "
		+ "            SCF.SCHEDULE_FREQ_NAME = 'DAILY' "
		+ "            AND "
		+ "            TRUNC(CSC.SCHEDULE_START_TIMESTAMP) < TRUNC(SYSDATE) "
		+ "          ) "
		+ "          OR "
		+ "          ("
		+ "            SCF.SCHEDULE_FREQ_NAME = 'WEEKLY' "
		+ "            AND "
		+ "            MOD((TRUNC(CSC.SCHEDULE_START_TIMESTAMP)-TRUNC(SYSDATE)),7) = 0 "
		+ "            AND "
		+ "            TRUNC(CSC.SCHEDULE_START_TIMESTAMP) < TRUNC(SYSDATE) "
		+ "          ) "
		+ "          OR "
		+ "          ("
		+ "            SCF.SCHEDULE_FREQ_NAME = 'MONTHLY' "
		+ "            AND "
		+ "            TRUNC(CSC.SCHEDULE_START_TIMESTAMP) < TRUNC(SYSDATE) "
		+ "            AND "
		+ "			   MOD(MONTHS_BETWEEN(TRUNC(SYSDATE),TRUNC(CSC.SCHEDULE_START_TIMESTAMP)), 1) = 0 "
		+ "          ) "
		+ "          OR "
		+ "          ("
		+ "            SCF.SCHEDULE_FREQ_NAME = 'YEARLY' "
		+ "            AND "
		+ "            TRUNC(CSC.SCHEDULE_START_TIMESTAMP) < SYSDATE "
		+ "            AND "
		+ "            EXTRACT(MONTH FROM CSC.SCHEDULE_START_TIMESTAMP) = EXTRACT(MONTH FROM SYSDATE) "
		+ "            AND "
		+ "            EXTRACT(DAY FROM CSC.SCHEDULE_START_TIMESTAMP) = EXTRACT(DAY FROM SYSDATE) "
		+ "          ) "
		+ "        ) "
		+ "      ) "
		+ "      OR "
		+ "      (CS.CAMPAIGN_STATUS_NAME = 'NEW' AND CA.CAMPAIGN_ACTION_NAME = 'CANCEL') "
		+ "      OR "
		+ "      (CS.CAMPAIGN_STATUS_NAME = 'PARTIAL_RUN' AND CA.CAMPAIGN_ACTION_NAME = 'CANCEL') "
		+ "      OR "
		+ "      (CS.CAMPAIGN_STATUS_NAME = 'PAUSED' AND (CA.CAMPAIGN_ACTION_NAME = 'RESUME' OR CA.CAMPAIGN_ACTION_NAME = 'CANCEL')) "
		+ "      OR "
		+ "      (CS.CAMPAIGN_STATUS_NAME = 'ON_HOLD' AND (CA.CAMPAIGN_ACTION_NAME = 'UN_HOLD' OR CA.CAMPAIGN_ACTION_NAME = 'CANCEL')) "
                + "      OR "
                + "      (CS.CAMPAIGN_STATUS_NAME = 'WAITING_APPROVAL' AND (CA.CAMPAIGN_ACTION_NAME = 'APPROVE' OR CA.CAMPAIGN_ACTION_NAME = 'CANCEL' OR CA.CAMPAIGN_ACTION_NAME = 'REJECT'))"
                + "      OR " 
                + "      (CS.CAMPAIGN_STATUS_NAME = 'WAITING_APPROVAL'" 
                + "         AND " 
                + "         CAST(TRUNC(CSC.SCHEDULE_START_TIMESTAMP) AS DATE) = TRUNC(SYSDATE)" 
                + "         AND" 
                + "         (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) < ((CAST(SYSTIMESTAMP-(?)/24 AS TIMESTAMP))- TRUNC(SYSTIMESTAMP))"
                + "      )"
                + "      OR "
                + "      (CS.CAMPAIGN_STATUS_NAME = 'APPROVED' AND (CA.CAMPAIGN_ACTION_NAME = 'SEND' OR CA.CAMPAIGN_ACTION_NAME = 'CANCEL'))"
                + "      OR " 
                + "      (CS.CAMPAIGN_STATUS_NAME = 'APPROVED'" 
                + "         AND " 
                + "         CAST(TRUNC(CSC.SCHEDULE_START_TIMESTAMP) AS DATE) = TRUNC(SYSDATE)" 
                + "         AND" 
                + "         (CSC.SCHEDULE_START_TIMESTAMP-TRUNC(CSC.SCHEDULE_START_TIMESTAMP)) < ((CAST(SYSTIMESTAMP-(?)/24 AS TIMESTAMP))- TRUNC(SYSTIMESTAMP))"
                + "      )"
		+ "   ) " + ") " + "OR " + "(CE.HANDLER_ID IS NOT NULL AND CE.HANDLER_ID != '0'" + "AND" + " ("
		+ "        (CS.CAMPAIGN_STATUS_NAME = 'RUNNING' " + "AND (" + "		CA.CAMPAIGN_ACTION_NAME = 'PAUSE' "
		+ "		OR CA.CAMPAIGN_ACTION_NAME = 'HOLD' " + "		OR CA.CAMPAIGN_ACTION_NAME = 'CANCEL' " + "	)) " + "    )"
		+ ")") })
@Access(FIELD)
public class Campaign implements Serializable, CampaignConst {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@NotNull
	@SequenceGenerator(name = "CampaignIdSeq", sequenceName = "CAMPAIGN_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CampaignIdSeq")
	@Column(name = "CAMPAIGN_ID")
	private String campaignId;
	@Basic(optional = false)
	// @NotNull
	// @Size(min = 1, max = 100)
	@Column(name = "NAME")
	private String name;
	// @Size(max = 255)
	@Column(name = "DESCRIPTION")
	private String description;
	@Basic(optional = false)
	// @NotNull
	@Column(name = "CREATION_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTimestamp;

	@JoinTable(name = "CAMPAIGN_LISTS", joinColumns = { @JoinColumn(name = "CAMPAIGN_ID", referencedColumnName = "CAMPAIGN_ID") }, inverseJoinColumns = { @JoinColumn(name = "LIST_ID", referencedColumnName = "LIST_ID") })
	@ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	private List<ContactList> contactLists;

	@JoinColumn(name = "STATUS_ID", referencedColumnName = "CAMPAIGN_STATUS_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH)
	private CampaignStatus status;

	@JoinColumn(name = "ACCOUNT_USER_ID", referencedColumnName = "ACCOUNT_USER_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
	private AccountUser accountUser;

	@OneToOne(mappedBy = "campaign", targetEntity = CampaignExecution.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	CampaignExecution campaignExecution;

	@OneToOne(mappedBy = "campaign", targetEntity = CampaignScheduling.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	CampaignScheduling campaignScheduling;

	@OneToOne(mappedBy = "campaign", targetEntity = CampaignSMSDetails.class, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	CampaignSMSDetails smsDetails;
	
	@JoinColumn(name = "TYPE_ID", referencedColumnName = "CAMPAIGN_TYPE_ID")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private CampaignType type;
	
	@Column(name = "RESEND_FAILED_FLAG")
	private boolean resendFailedFlag;
	
	

	@Transient
	double SubmittedSMSRatio = 0;

	public double getSubmittedSMSRatio() 
	{
		if (campaignExecution.getSubmittedSmsCount() == null 
				|| smsDetails.getSMSCount() == null 
				|| smsDetails.getSMSCount() == 0)
		{
			return 0;
		}// end if
		else
		{
			SubmittedSMSRatio = ((double) campaignExecution.getSubmittedSmsCount() / (double) smsDetails.getSMSCount()) * 100;
			return SubmittedSMSRatio;
		}// end else
	}

	public Campaign() {
	}

	public Campaign(String campaignId) {
		this.campaignId = campaignId;
	}

	public Campaign(String campaignId, String name, Date creationTimestamp) {
		this.campaignId = campaignId;
		this.name = name;
		this.creationTimestamp = creationTimestamp;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Date creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public List<ContactList> getContactLists() {
		return contactLists;
	}

	public void setContactLists(List<ContactList> listsList) {
		this.contactLists = listsList;
	}

	public CampaignStatus getStatus() {
		return status;
	}

	public void setStatus(CampaignStatus statusId) {
		this.status = statusId;
	}


	public CampaignExecution getCampaignExecution() {
		return campaignExecution;
	}

	public void setCampaignExecution(CampaignExecution campaignExecution) {
		this.campaignExecution = campaignExecution;
	}

	public CampaignScheduling getCampaignScheduling() {
		return campaignScheduling;
	}

	public void setCampaignScheduling(CampaignScheduling campaignScheduling) {
		this.campaignScheduling = campaignScheduling;
	}

	public CampaignSMSDetails getSmsDetails() {
		return smsDetails;
	}

	public void setSmsDetails(CampaignSMSDetails smsDetails) {
		this.smsDetails = smsDetails;
	}

	public CampaignType getType() {
		return type;
	}

	public void setType(CampaignType type) {
		this.type = type;
	}

	
	public boolean isResendFailedFlag() {
		return resendFailedFlag;
	}

	public void setResendFailedFlag(boolean resendFailedFlag) {
		this.resendFailedFlag = resendFailedFlag;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (campaignId != null ? campaignId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Campaign)) {
			return false;
		}
		Campaign other = (Campaign) object;
		if ((this.campaignId == null && other.campaignId != null)
				|| (this.campaignId != null && !this.campaignId.equals(other.campaignId))) {
			return false;
		}
		return true;
	}

	public String logId() {
		return "Camp(" + campaignId + ") ";
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("(campaignId=");
        str = str.append(campaignId)
        .append(", name=")
        .append(name)
        .append(", description=")
        .append(description)
        .append(", creationTimestamp=")
        .append(creationTimestamp)
        .append(", contactLists=")
        .append(contactLists)
        .append(", status=")
        .append(status)
        .append(", account=")
        .append(accountUser.getAccount())
        .append(", smsDetails=")
        .append(smsDetails)
        .append(", campaignScheduling=")
        .append(campaignScheduling)
        .append(", campaignExecution=")
        .append(campaignExecution)
        .append(")");
        return str.toString();
    }

    public String logCreated() {
        StringBuilder str = new StringBuilder("(campaignId=");
        str = str.append(campaignId)
        .append(", name=")
        .append(name)
        .append(", description=")
        .append(description)
        .append(", creationTimestamp=")
        .append(creationTimestamp)
        .append(", contactLists=")
        .append(contactLists)
        .append(", status=")
        .append(status)
        .append(", accountId=")
        .append(accountUser.getAccount().getAccountId())
        .append(", accountUser=")
        .append(accountUser.getUsername())
        .append(", smsDetails=")
        .append(smsDetails)
        .append(", campaignScheduling=")
        .append(campaignScheduling)
        .append(")");
        return str.toString();
    }

	public String logUpdated() {
		return logCreated();
	}

    public String logExecutionState() {
        StringBuilder str = new StringBuilder("(status=");
        str = str.append(status)
        .append((campaignExecution != null ? ", " + campaignExecution.logExecutionFullInfo() : ""))
        .append(")")
        .append(campaignScheduling)
        .append(")");
        return str.toString();
    }

	public AccountUser getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(AccountUser accountUser) {
		this.accountUser = accountUser;
	}

}
