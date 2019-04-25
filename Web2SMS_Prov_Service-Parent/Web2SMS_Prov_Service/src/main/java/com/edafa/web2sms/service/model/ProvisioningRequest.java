package com.edafa.web2sms.service.model;

import com.edafa.web2sms.acc_manag.service.model.TierModel;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;

@XmlRootElement(name = "ProvisioningRequest")
@XmlType(name = "ProvisioningRequest", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProvisioningRequest {

	@XmlElement(required = true, nillable = false)
	protected String requestId;
	@XmlElement(required = true, nillable = false)
	protected String accountAdmin;
	@XmlElement(required = true, nillable = false)
	protected String companyId;
	@XmlElement(required = true, nillable = false)
	protected String companyName;
	@XmlElement(required = true, nillable = false)
	protected String companyDomain;
	@XmlElement(required = true, nillable = false)
	protected String callbackUrl;
	@XmlElement(required = true, nillable = false)
	protected ProvRequestTypeName requestType;
	@XmlElement(required = true, nillable = false)
	protected Date entryDate;
	@XmlElement(required = true, nillable = true)
	protected String newSenderName;
	@XmlElement(required = true, nillable = true)
	protected String senderName;
	@XmlElement(required = true, nillable = true)
	protected TierModel tier;
	@XmlElement(required = true, nillable = true)
	protected String msisdn;
	@XmlElement(nillable = false, required = true)
	protected String ratePlan;
	@XmlElement(nillable = false, required = true)
	protected String userId;

	@XmlTransient
	private ProvReqStatusName status;

	@XmlTransient
	private boolean returnedSync = false;

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getAccountAdmin() {
		return accountAdmin;
	}

	public void setAccountAdmin(String accountAdmin) {
		this.accountAdmin = accountAdmin;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getCompanyDomain() {
		return companyDomain;
	}

	public void setCompanyDomain(String companyDomain) {
		this.companyDomain = companyDomain;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getNewSenderName() {
		return newSenderName;
	}

	public void setNewSenderName(String newSenderName) {
		this.newSenderName = newSenderName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String oldSenderName) {
		this.senderName = oldSenderName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public TierModel getTier() {
		return tier;
	}

	public void setTier(TierModel tier) {
		this.tier = tier;
	}

	public ProvReqStatusName getStatus() {
		return status;
	}

	public void setStatus(ProvReqStatusName status) {
		this.status = status;
	}

	public ProvRequestTypeName getRequestType() {
		return requestType;
	}

	public void setRequestType(ProvRequestTypeName requestType) {
		this.requestType = requestType;
	}

	public Date getEntryDate() {
		return entryDate;
	}

	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}

	public String getMSISDN() {
		return msisdn;
	}

	public void setMSISDN(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getRatePlan() {
		return ratePlan;
	}

	public void setRatePlan(String ratePlan) {
		this.ratePlan = ratePlan;
	}

	public boolean isReturnedSync() {
		return returnedSync;
	}

	public void setReturnedSync(boolean returnedSync) {
		this.returnedSync = returnedSync;
	}

	@Override
	public String toString() {
		return "ProvisioningRequest (requestId=" + requestId + ", companyId=" + companyId + ", companyName="
				+ companyName + ", callbackUrl=" + callbackUrl + ", oldSenderName=" + senderName + ", newSenderName="
				+ newSenderName + (tier != null ? ", tierId=" + tier.getTierId() : "")  + ", requestType=" + requestType
				+ ", entryDate=" + entryDate + ")";
	}

	@XmlTransient
	public String logRequest() {
		String log = "requestId (" + requestId + "): requestType=" + requestType;
		if (status != null) {
			log += ", status=" + status;
		}
		log += ", entryDate=" + entryDate + ", companyId=" + companyId + ", companyName=" + companyName+ ", AccountAdmin="+ accountAdmin;
		if (senderName != null) {
			log += ", senderName=" + senderName;
		}
		if (newSenderName != null) {
			log += ", newSenderName=" + newSenderName;
		}
		if (tier != null && tier.getTierId() != null) {
			log += ", tierId=" + tier.getTierId();
		}

		if (msisdn != null)
			log += ", MSISDN=" + msisdn;

		if (callbackUrl != null)
			log += ", callbackUrl=" + callbackUrl + ") ";

		return log;
	}

	@XmlTransient
	public boolean isValid() {
		boolean valid = true;
		if (requestId == null || isEmptyStr(requestId) || isEmptyStr(companyId) || isEmptyStr(companyName)
				|| isEmptyStr(callbackUrl) || entryDate == null || requestType == null) {
			valid = false;
		}

		if (valid && tier != null && !tier.isValid()) {
			valid = false;
		}

		if (valid && senderName != null && !senderName.isEmpty() && newSenderName != null && !newSenderName.isEmpty()) {
			valid = false;
		}
		return valid;
	}

	protected boolean isEmptyStr(String s) {
		return s == null || s.isEmpty();
	}
}
