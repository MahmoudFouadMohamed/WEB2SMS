/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.TierConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "TIERS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Tier.findAll", query = "SELECT t FROM Tier t"),
		@NamedQuery(name = "Tier.findByTierId", query = "SELECT t FROM Tier t WHERE t.tierId = :tierId"),
		@NamedQuery(name = "Tier.findTotalQuotaByTierId", query = "SELECT t.quota FROM Tier t WHERE t.tierId = :tierId"),
		@NamedQuery(name = "Tier.findByTierName", query = "SELECT t FROM Tier t WHERE t.tierName = :tierName"),
		@NamedQuery(name = "Tier.findByQuota", query = "SELECT t FROM Tier t WHERE t.quota = :quota"),
		@NamedQuery(name = "Tier.findByDescription", query = "SELECT t FROM Tier t WHERE t.description = :description"),
		@NamedQuery(name = "Tier.findByRatePlan", query = "SELECT t FROM Tier t WHERE t.ratePlan = :ratePlan"),
		@NamedQuery(name = "Tier.findByTierType", query = "SELECT t FROM Tier t WHERE t.tierType = :tierType"),
		@NamedQuery(name = "Tier.removeByTierId", query = "DELETE FROM Tier t WHERE t.tierId = :tierId"),

		@NamedQuery(name = "Tier.findByExpairyDays", query = "SELECT t FROM Tier t WHERE t.expairyDays = :expairyDays"),
// @NamedQuery(name = "Tier.getQuotaByAccountTierID", query =
// "SELECT t From Tier t where t.tierId = (SELECT a.tierId From AccountTier a Where a.accountTierId =:accountTierId) ")

})
public class Tier implements Serializable, TierConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@Column(name = "TIER_ID")
	private Integer tierId;

	@Basic(optional = false)
	@Column(name = "TIER_NAME")
	private String tierName;

	@Basic(optional = false)
	@Column(name = "QUOTA")
	private Integer quota;

	@Basic(optional = false)
	@Column(name = "RATE_PLAN")
	private String ratePlan;

	@Column(name = "DESCRIPTION")
	private String description;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tier", fetch = FetchType.LAZY)
	private List<Account> accountList;

	/* aded after changes for one off bundle */
	@Basic(optional = false)
	@NotNull
	@Column(name = "EXPAIRY_DAYS")
	private int expairyDays;

	@JoinColumn(name = "TIER_TYPE_ID", referencedColumnName = "TIER_TYPE_ID")
	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	private TierType tierType;
	

	/***/

	public Tier() {
		// just for test // default is normal
//		 tierTypeId = new TierType(TierTypesEnum.NORMAL);
	}

	public Tier(Integer tierId) {
		this.tierId = tierId;
	}

	public Tier(Integer tierId, String tierName, Integer quota) {
		this.tierId = tierId;
		this.tierName = tierName;
		this.quota = quota;
	}

	public Integer getTierId() {
		return tierId;
	}

	public void setTierId(Integer tierId) {
		this.tierId = tierId;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}

	public Integer getQuota() {
		return quota;
	}

	public void setQuota(Integer quota) {
		this.quota = quota;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	public String getRatePlan() {
		return ratePlan;
	}

	public void setRatePlan(String ratePlan) {
		this.ratePlan = ratePlan;
	}

	public int getExpairyDays() {
		return expairyDays;
	}

	public void setExpairyDays(int expairyDays) {
		this.expairyDays = expairyDays;
	}

	public TierType getTierType() {
		return tierType;
	}

	public void setTierType(TierType tierTypeId) {
		this.tierType = tierTypeId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (tierId != null ? tierId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Tier)) {
			return false;
		}
		Tier other = (Tier) object;
		if ((this.tierId == null && other.tierId != null)
				|| (this.tierId != null && !this.tierId.equals(other.tierId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "[Tier(" + tierId + "): " + tierName + ", ratePlan:" + ratePlan
				+ "]" ;
	}

	public String logInfo() {
		return "[Tier(" + tierId + "): " + tierName + ", ratePlan:" + ratePlan
				+ "]";
	}
}
