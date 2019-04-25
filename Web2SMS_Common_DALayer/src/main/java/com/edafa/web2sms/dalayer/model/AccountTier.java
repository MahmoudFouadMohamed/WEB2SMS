package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.model.constants.AccountTierConst;

/**
 *
 * @author mayahmed
 */
@Entity
@Table(name = "ACCOUNT_TIERS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "AccountTier.findAll", query = "SELECT a FROM AccountTier a"),
		@NamedQuery(name = "AccountTier.findByBillingMsisdn", query = "SELECT a FROM AccountTier a WHERE a.billingMsisdn = :billingMsisdn"),
		@NamedQuery(name = "AccountTier.findByAccountId", query = "SELECT a FROM AccountTier a WHERE a.account.accountId = :accountId"),
		@NamedQuery(name = "AccountTier.findByAccountTiersId", query = "SELECT a FROM AccountTier a WHERE a.accountTiersId = :accountTiersId") })
public class AccountTier implements Serializable, AccountTierConst {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "ACCOUNT_TIERS_ID")
	@SequenceGenerator(name = "AccTiersSeq", sequenceName = "ACC_TIERS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AccTiersSeq")
	private Integer accountTiersId;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "accountTiers")
	private AccountQuota accountQuota;

	@JoinColumn(name = "TIER_ID", referencedColumnName = "TIER_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Tier tier;

	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
	@OneToOne(optional = false)
	private Account account;

	@Column(name = "BILLING_MSISDN")
	private String billingMsisdn;

	public AccountTier() {
	}

	public AccountTier(Integer accountTiersId) {
		this.accountTiersId = accountTiersId;
	}
	
//	public AccountTier(Integer accountTiersId ) {
//		this.accountTiersId = accountTiersId;
//		this.account.setAccountId(accountId);
//	}

	public Integer getAccountTiersId() {
		return accountTiersId;
	}

	public void setAccountTiersId(Integer accountTiersId) {
		this.accountTiersId = accountTiersId;
	}

	public AccountQuota getAccountQuota() {
		return accountQuota;
	}

	public void setAccountQuota(AccountQuota accountQuota) {
		this.accountQuota = accountQuota;
	}

	public Tier getTier() {
		return tier;
	}

	public void setTierId(Tier tier) {
		this.tier = tier;
	}

	public Account getAccountId() {
		return account;
	}

	public void setAccountId(Account accountId) {
		this.account = accountId;
	}
	
	
	
	public String getBillingMsisdn() {
		return billingMsisdn;
	}

	public void setBillingMsisdn(String billingMsisdn) {
		this.billingMsisdn = billingMsisdn;
	}

	public boolean isPrepaidTier(){
		if(tier.getTierType().getTierTypeName().equals(TierTypesEnum.PREPAID))
			return true;
		else
			return false;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (accountTiersId != null ? accountTiersId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof AccountTier)) {
			return false;
		}
		AccountTier other = (AccountTier) object;
		if ((this.accountTiersId == null && other.accountTiersId != null)
				|| (this.accountTiersId != null && !this.accountTiersId.equals(other.accountTiersId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "AccountTier[ accountTiersId=" + accountTiersId + " billinfMsisdn= "+billingMsisdn+ " accountQuota= "+accountQuota+ " ]";
	}

}
