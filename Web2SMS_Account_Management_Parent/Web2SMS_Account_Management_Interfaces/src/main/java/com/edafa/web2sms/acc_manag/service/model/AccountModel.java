package com.edafa.web2sms.acc_manag.service.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.AccountStatusName;
import java.io.Serializable;

@XmlType(name = "Account", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class AccountModel implements Serializable {
	@XmlElement(required = true, nillable = false)
	protected String accountId;

	@XmlElement(required = true, nillable = false)
	protected String companyId;

	@XmlElement(required = true, nillable = false)
	protected String accountUser;
	
	@XmlElement(required = true, nillable = false)
	protected boolean adminRole;

	@XmlElement(required = true, nillable = false)
	protected String companyName;

	@XmlElement(required = true, nillable = false)
	protected String companyDomain;

	@XmlElement(required = true, nillable = false)
	protected String billingMsisdn;

	@XmlElement(name = "Tier", required = true, nillable = false)
	protected TierModel tier;

	@XmlElement(required = true, nillable = false)
	protected AccountStatusName status;

	public AccountModel() {
	}

	public AccountModel(AccountModel account) {
		this.accountUser = account.getAccountUser();
		this.adminRole = account.isAdminRole();
		this.accountId = account.getAccountId();
		this.companyName = account.getCompanyName();
		this.billingMsisdn = account.getBillingMsisdn();
		this.companyDomain = account.getCompanyDomain();
		this.companyId = account.getCompanyId();
		this.status = account.getStatus();
		this.tier = account.getTier();
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCompanyId() {
		return companyId;
	}

	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	public String getAccountUser() {
		return accountUser;
	}

	public void setAccountUser(String accountUser) {
		this.accountUser = accountUser;
	}

	public boolean isAdminRole() {
		return adminRole;
	}

	public void setAdminRole(boolean adminRole) {
		this.adminRole = adminRole;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getCompanyDomain() {
		return companyDomain;
	}

	public void setCompanyDomain(String companyDomain) {
		this.companyDomain = companyDomain;
	}

	public String getBillingMsisdn() {
		return billingMsisdn;
	}

	public void setBillingMsisdn(String billingMsisdn) {
		this.billingMsisdn = billingMsisdn;
	}

	public TierModel getTier() {
		return tier;
	}

	public void setTier(TierModel tier) {
		this.tier = tier;
	}

	public AccountStatusName getStatus() {
		return status;
	}

	public void setStatus(AccountStatusName status) {
		this.status = status;
	}

	@XmlTransient
	public boolean isValid() {
		return !(tier == null || !tier.isValid() || companyId == null || companyId.isEmpty() || accountUser == null
				|| accountUser.isEmpty() || companyDomain == null || companyDomain.isEmpty() || billingMsisdn == null
				|| billingMsisdn.isEmpty());
	}

	@Override
	public String toString() {
		return "Account (accountId=" + accountId + ", status=" + status + ", companyId=" + companyId
				+ ", companyAdmin=" + accountUser + ", companyName=" + companyName + ", companyName=" + companyDomain
				+ ", billingMsisdn=" + billingMsisdn + ", tierId=" + tier.getTierId() + ")";
	}

	public String logId() {
		return "(" + accountId + ") ";
	}

	public String logInfo() {
		StringBuilder sb = new StringBuilder();
		sb.append("(");

		if (accountId != null) {
			sb.append("accountId=");
			sb.append(accountId);
			sb.append(", ");
		}

		if (status != null) {
			sb.append("status=");
			sb.append(status);
			sb.append(", ");
		}
		sb.append("companyId=");
		sb.append(companyId);

		sb.append(", companyAdmin=");
		sb.append(accountUser);

		sb.append(", companyName=");
		sb.append(companyName);

		sb.append(", companyName=");
		sb.append(companyDomain);

		sb.append(", billingMsisdn=");
		sb.append(billingMsisdn);

		sb.append(", tierId=");
		sb.append(tier.getTierId());

		sb.append(")");

		return sb.toString();
	}

}
