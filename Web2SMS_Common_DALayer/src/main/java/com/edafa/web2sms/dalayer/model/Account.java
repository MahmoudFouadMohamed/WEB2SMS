/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.AccountConst;

/**
 * 
 * @author akhalifah,yyaseen
 */
@Entity
@Table(name = "ACCOUNTS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Account.findAll", query = "SELECT a FROM Account a"),
		@NamedQuery(name = "Account.findByStatus", query = "SELECT a FROM Account a where a.status IN :statuses"),
		@NamedQuery(name = "Account.findByAccountId", query = "SELECT a FROM Account a WHERE a.accountId = :accountId"),
		@NamedQuery(name = "Account.findWithSMSAPIByAccountId", query = "SELECT a FROM Account a "
                        + "INNER JOIN FETCH a.accountSmsApi INNER JOIN FETCH a.accountSendersList WHERE a.accountId = :accountId"),
                @NamedQuery(name = "Account.findWithSMSAPICampByAccountId", query = "SELECT a FROM Account a "
                        + "INNER JOIN FETCH a.accountSmsApi INNER JOIN FETCH a.accountSendersList INNER JOIN FETCH a.accountUsers WHERE a.accountId = :accountId"),
		@NamedQuery(name = "Account.findByCompanyName", query = "SELECT a FROM Account a WHERE a.companyName = :companyName"),
		@NamedQuery(name = "Account.findByBillingMsisdn", query = "SELECT a FROM Account a WHERE a.billingMsisdn = :billingMsisdn"),
		@NamedQuery(name = "Account.updateAccountStatus", query = "UPDATE Account a SET a.status = :status WHERE a.accountId = :accountId"),
		@NamedQuery(name = "Account.searchByAccountName", query = "SELECT a FROM Account a WHERE LOWER(a.companyName) LIKE :companyName"),
//                @NamedQuery(name = "Account.findByIdAndUserNameAndAction", query = "SELECT a FROM Account a where a.accountId = :accountId and a.accountUsers.username = :userName and a.accountUsers.accountgroups.privileges.actionList.actionName = :actionName and a.status.privileges.actionList.actionName = :actionName")
//                @NamedQuery(name = "Account.findByIdAndUserNameAndAction", query = "SELECT a FROM Account a join a.accountUsers au where a.accountId = :accountId and au.username = :userName")
                  @NamedQuery(name = "Account.findByIdAndUserNameAndAction", query = "SELECT a FROM Account a join a.accountUsers au join a.status at join at.privileges atp join atp.actionList atpa join au.accountGroups ag join ag.privileges p join p.actionList al where a.accountId = :accountId and au.username = :username and al.actionName = :actionName and atpa.actionName = :actionName"),
                  @NamedQuery(name = "Account.findWithSMSAPIByIdAndAction", query = "SELECT a FROM Account a"
                          + " JOIN FETCH a.accountSmsApi JOIN FETCH a.accountSendersList"
                          + " join a.status at"
                          + " join at.privileges atp"
                          + " join atp.actionList atpa"
                          + " where a.accountId = :accountId and atpa.actionName = :actionName"),
                  @NamedQuery(name = "Account.findWithSMSAPICampByIdAndAction", query = "SELECT a FROM Account a"
                          + " JOIN FETCH a.accountSmsApi JOIN FETCH a.accountSendersList JOIN FETCH a.accountUsers"
                          + " join a.status at"
                          + " join at.privileges atp"
                          + " join atp.actionList atpa"
                          + " where a.accountId = :accountId and atpa.actionName IN :actionName")        
})
@Access(AccessType.FIELD)
public class Account implements Serializable, AccountConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ACCOUNT_ID")
	private String accountId;
	@Column(name = "COMPANY_NAME")
	private String companyName;
	@Basic(optional = false)
	@Column(name = "BILLING_MSISDN")
	private String billingMsisdn;
	@JoinColumn(name = "TIER_ID", referencedColumnName = "TIER_ID")
	@ManyToOne(optional = false)
	private Tier tier;
	@JoinColumn(name = "STATUS_ID", referencedColumnName = "ACCOUNT_STATUS_ID")
	@ManyToOne(optional = false)
	private AccountStatus status;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
	private List<ContactList> accountListList;

	@OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
	private List<AccountSender> senders;
        
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
	private List<AccountUser> accountUsers;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
	private AccountSMSAPI accountSmsApi;
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
	private QuotaHistory quotaHistory;

	@JoinTable(name = "INTRA_SENDERS_ACCOUNTS", joinColumns = { @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID") }, inverseJoinColumns = { @JoinColumn(name = "INTRA_SENDERS_ID", referencedColumnName = "INTRA_SENDER_ID") })
	@ManyToMany(fetch = FetchType.EAGER)
	private List<IntraSender> intraSendersList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.EAGER)
	private List<AccountSender> accountSendersList;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "account")
    private AccountTier accountTier;

	@JoinTable(name = "TEMPLATES_ACCOUNTS", joinColumns = { @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID") }, inverseJoinColumns = { @JoinColumn(name = "TEMPLATE_ID", referencedColumnName = "TEMPLATE_ID") })
	@ManyToMany
	private List<Template> templatesList;
        
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "ACCOUNT_SENDING_RATE_LIMITERS", joinColumns = {
        @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")}, inverseJoinColumns = {
        @JoinColumn(name = "LIMITER_ID", referencedColumnName = "LIMITER_ID")})
    private List<SendingRateLimiter> SendingRateLimiters;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "account", fetch = FetchType.LAZY)
	private List<AccountGroup> accountGroups;
    
	public Account() {
	}

	public Account(String accountId) {
		this.accountId = accountId;
	}

	public Account(String accountId, String billingMsisdn) {
		this.accountId = accountId;
		this.billingMsisdn = billingMsisdn;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBillingMsisdn() {
		return billingMsisdn;
	}

	public void setBillingMsisdn(String billingMsisdn) {
		this.billingMsisdn = billingMsisdn;
	}

	public Tier getTier() {
		return tier;
	}

	public void setTier(Tier tier) {
		this.tier = tier;
	}

	public AccountStatus getStatus() {
		return status;
	}

	public void setStatus(AccountStatus status) {
		this.status = status;
	}

	@XmlTransient
	public List<ContactList> getAccountListList() {
		return accountListList;
	}

	public void setAccountListList(List<ContactList> accountListList) {
		this.accountListList = accountListList;
	}

	public List<AccountSender> getSenders() {
		return senders;
	}

	public void setSenders(List<AccountSender> senders) {
		this.senders = senders;
	}

	@XmlTransient
	public List<IntraSender> getIntraSendersList() {
		return intraSendersList;
	}

	public void setIntraSendersList(List<IntraSender> intraSendersList) {
		this.intraSendersList = intraSendersList;
	}

	@XmlTransient
	public List<AccountSender> getAccountSendersList() {
		return accountSendersList;
	}

	public void setAccountSendersList(List<AccountSender> accountSendersList) {
		this.accountSendersList = accountSendersList;
	}

	@XmlTransient
	public List<AccountUser> getAccountUsers() {
		return accountUsers;
	}

	public void setAccountUsers(List<AccountUser> accountUsers) {
		this.accountUsers = accountUsers;
	}

	public AccountSMSAPI getAccountSmsApi() {
		return accountSmsApi;
	}

	public QuotaHistory getQuotaHistory() {
		return quotaHistory;
	}

	public void setQuotaHistory(QuotaHistory quotaHistory) {
		this.quotaHistory = quotaHistory;
	}

    @XmlTransient
    public AccountTier getAccountTier() {
        return accountTier;
    }

    public void setAccountTier(AccountTier accountTier) {
        this.accountTier = accountTier;
    }

    public void setSendingRateLimiters(List<SendingRateLimiter> SendingRateLimiters) {
        this.SendingRateLimiters = SendingRateLimiters;
    }

    public List<SendingRateLimiter> getSendingRateLimiters() {
        return SendingRateLimiters;
    }   
    
    public void setAccountGroups(List<AccountGroup> accountGroups) {
		this.accountGroups = accountGroups;
	}
    
	@XmlTransient
    public List<AccountGroup> getAccountGroups() {
		return accountGroups;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountId != null ? accountId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Account)) {
			return false;
		}
		Account other = (Account) object;
		if ((this.accountId == null && other.accountId != null)
				|| (this.accountId != null && !this.accountId.equals(other.accountId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(accountId=" + accountId + ", status=" + status + ", companyName=" + companyName + ", billingMsisdn="
				+ billingMsisdn + (tier != null ? ", tierId=" + tier.getTierId() : "") + ")";
	}

	public String logInfo() {
		String str = "(";

		// if (accountId != null) {
		// sb.append("accountId=");
		// sb.append(accountId);
		// sb.append(", ");
		// }

		if (status != null)
			str += "status=" + status + ", ";

		// sb.append("companyId=");
		// sb.append(companyId);

		str += /* "companyAdmin=" + accountAdmin + */", companyName=" + companyName + ", billingMsisdn="
				+ billingMsisdn + ", tier=" + tier.logInfo() + ")";

		return str;
	}
}
