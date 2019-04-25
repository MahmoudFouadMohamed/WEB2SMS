/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/**
 * 
 * @author akhalifah
 */

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)

@Access(AccessType.FIELD)
public class ProvRequest implements Serializable {

	protected static final long serialVersionUID = -4680245274777566005L;

	@Id
	@Basic(optional = false)
	@NotNull
	// @Size(min = 1, max = 200)
	@Column(name = "REQUEST_ID")
	protected String requestId;
	@Basic(optional = false)
	@NotNull
	@Column(name = "ENTRY_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date entryDate;
	@Basic(optional = false)
	@NotNull
	// @Size(min = 1, max = 200)
	@Column(name = "COMPANY_ID")
	protected String companyId;
	// @Size(max = 50)
	@Column(name = "COMPANY_NAME")
	protected String companyName;

	@Column(name = "ACCT_HOLDER_ID")
	protected String companyAdmin;

	// @Size(max = 300)
	@Column(name = "CALLBACK_URL")
	protected String callbackUrl;
	@Basic(optional = true)
	@Column(name = "UPDATE_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date updateDate;
	// @Size(max = 50)
	@Column(name = "SENDER_NAME")
	protected String senderName;
	
	@Column(name = "NEW_SENDER_NAME")
	protected String newSenderName;
	
	@Column(name = "USER_ID")
	protected String userId;
	
	@JoinColumn(name = "TIER_ID", referencedColumnName = "TIER_ID")
	@ManyToOne(optional = false)
	protected Tier tier;
	@JoinColumn(name = "STATUS_ID", referencedColumnName = "PROV_STATUS_ID")
	@ManyToOne(optional = false)
	protected ProvRequestStatus status;
	@JoinColumn(name = "REQUEST_TYPE_ID", referencedColumnName = "PROV_REQ_TYPE_ID")
	@ManyToOne(optional = false)
	protected ProvRequestType requestType;

	public ProvRequest() {
	}

	public ProvRequest(ProvRequest provRequest) {
		this.requestId = provRequest.getRequestId();
		this.companyAdmin = provRequest.getAccountAdmin();
		this.callbackUrl = provRequest.getCallbackUrl();
		this.companyName = provRequest.getCompanyName();
		this.companyId = provRequest.getCompanyId();
		this.entryDate = provRequest.getEntryDate();
		this.requestType = provRequest.getRequestType();
		this.senderName = provRequest.getSenderName();
		this.newSenderName = provRequest.getNewSenderName();
		this.userId= provRequest.getUserId();
		this.status = provRequest.getStatus();
		this.tier = provRequest.getTier();
		this.updateDate = provRequest.getUpdateDate();
	}

	public ProvRequest(String requestId) {
		this.requestId = requestId;
	}

	public ProvRequest(String requestId, Date entryDate, String companyId, Date updateDate) {
		this.requestId = requestId;
		this.entryDate = entryDate;
		this.companyId = companyId;
		this.updateDate = updateDate;
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getAccountAdmin() {
		return companyAdmin;
	}

	public void setAccountAdmin(String accountAdmin) {
		this.companyAdmin = accountAdmin;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public Date getUpdateDate() {
		return updateDate;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getNewSenderName() {
		return newSenderName;
	}

	public void setNewSenderName(String newSenderName) {
		this.newSenderName = newSenderName;
	}
	
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public Tier getTier() {
		return tier;
	}

	public void setTier(Tier tier) {
		this.tier = tier;
	}

	public ProvRequestStatus getStatus() {
		return status;
	}

	public void setStatus(ProvRequestStatus status) {
		this.status = status;
	}

	public ProvRequestType getRequestType() {
		return requestType;
	}

	public void setRequestType(ProvRequestType requestType) {
		this.requestType = requestType;
	}

	@PrePersist
	private void prePersist() {
		preUpdate();
	}

	@PreUpdate
	private void preUpdate() {
		updateDate = new Date();
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (requestId != null ? requestId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ProvRequest)) {
			return false;
		}
		ProvRequest other = (ProvRequest) object;
		if ((this.requestId == null && other.requestId != null)
				|| (this.requestId != null && !this.requestId.equals(other.requestId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.edafa.web2sms.module.ProvRequests[ requestId=" + requestId + " ]";
	}

}
