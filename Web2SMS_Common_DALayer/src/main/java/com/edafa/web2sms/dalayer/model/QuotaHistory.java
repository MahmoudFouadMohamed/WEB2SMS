package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.enums.MonthName;
import com.edafa.web2sms.dalayer.model.constants.QuotaHistoryConst;

/**
 *
 * @author khalid
 */
@Entity
@Table(name = "QUOTA_HISTORY")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "QuotaHistory.findAll", query = "SELECT q FROM QuotaHistory q"),
		@NamedQuery(name = "QuotaHistory.findByAccountId", query = "SELECT q FROM QuotaHistory q WHERE q.accountId = :accountId"),
		@NamedQuery(name = "QuotaHistory.findByUpdateTimestamp", query = "SELECT q FROM QuotaHistory q WHERE q.updateTimestamp = :updateTimestamp") })
public class QuotaHistory implements Serializable , QuotaHistoryConst{
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "ACCOUNT_ID")
	private String accountId;
	@Column(name = "JAN")
	private int jan;
	@Column(name = "FEB")
	private int feb;
	@Column(name = "MAR")
	private int mar;
	@Column(name = "APR")
	private int apr;
	@Column(name = "MAY")
	private int may;
	@Column(name = "JUNE")
	private int june;
	@Column(name = "JULY")
	private int july;
	@Column(name = "AUG")
	private int aug;
	@Column(name = "SEPT")
	private int sept;
	@Column(name = "OCT")
	private int oct;
	@Column(name = "NOV")
	private int nov;
	@Column(name = "DEC")
	private int dec;
	@Column(name = "UPDATE_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	private Date updateTimestamp;
	@JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID", insertable = false, updatable = false)
	@OneToOne(optional = false, cascade = CascadeType.DETACH)
	private Account account;

	@Transient
	private int[] historyArr;

	public QuotaHistory() {
		historyArr = new int[12];
	}

	public QuotaHistory(String accountId) {
		this.accountId = accountId;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public int getJan() {
		return jan;
	}

	public void setJan(int jan) {
		this.jan = jan;
	}

	public int getFeb() {
		return feb;
	}

	public void setFeb(int feb) {
		this.feb = feb;
	}

	public int getMar() {
		return mar;
	}

	public void setMar(int mar) {
		this.mar = mar;
	}

	public int getApr() {
		return apr;
	}

	public void setApr(int apr) {
		this.apr = apr;
	}

	public int getMay() {
		return may;
	}

	public void setMay(int may) {
		this.may = may;
	}

	public int getJune() {
		return june;
	}

	public void setJune(int june) {
		this.june = june;
	}

	public int getJuly() {
		return july;
	}

	public void setJuly(int july) {
		this.july = july;
	}

	public int getAug() {
		return aug;
	}

	public void setAug(int aug) {
		this.aug = aug;
	}

	public int getSept() {
		return sept;
	}

	public void setSept(int sept) {
		this.sept = sept;
	}

	public int getOct() {
		return oct;
	}

	public void setOct(int oct) {
		this.oct = oct;
	}

	public int getNov() {
		return nov;
	}

	public void setNov(int nov) {
		this.nov = nov;
	}

	public int getDec() {
		return dec;
	}

	public void setDec(int dec) {
		this.dec = dec;
	}

	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public Account getAccounts() {
		return account;
	}

	public void setAccounts(Account accounts) {
		this.account = accounts;
	}

	public int getMonthValue(MonthName month) {
		int result;
		switch (month) {
		case JAN:
			result = jan;
			break;
		case FEB:
			result = feb;
			break;
		case MAR:
			result = mar;
			break;
		case APR:
			result = apr;
			break;
		case MAY:
			result = may;
			break;
		case JUNE:
			result = june;
			break;
		case JULY:
			result = july;
			break;
		case AUG:
			result = aug;
			break;
		case SEPT:
			result = sept;
			break;
		case OCT:
			result = oct;
			break;
		case NOV:
			result = nov;
			break;
		case DEC:
			result = dec;
			break;
		default:
			result = 0;
			break;
		}
		return result;
	}

	public void setMonthValue(MonthName month, int value) {
		switch (month) {
		case JAN:
			this.jan = value;
			break;
		case FEB:
			this.feb = value;
			break;
		case MAR:
			this.mar = value;
			break;
		case APR:
			this.apr = value;
			break;
		case MAY:
			this.may = value;
			break;
		case JUNE:
			this.june = value;
			break;
		case JULY:
			this.july = value;
			break;
		case AUG:
			this.aug = value;
			break;
		case SEPT:
			this.sept = value;
			break;
		case OCT:
			this.oct = value;
			break;
		case NOV:
			this.nov = value;
			break;
		case DEC:
			this.dec = value;
			break;
		default:
			break;
		}
	}

	public int getMonthValue(int month) {
		int result;
		switch (month) {
		case 0:
			result = jan;
			break;
		case 1:
			result = feb;
			break;
		case 2:
			result = mar;
			break;
		case 3:
			result = apr;
			break;
		case 4:
			result = may;
			break;
		case 5:
			result = june;
			break;
		case 6:
			result = july;
			break;
		case 7:
			result = aug;
			break;
		case 8:
			result = sept;
			break;
		case 9:
			result = oct;
			break;
		case 10:
			result = nov;
			break;
		case 11:
			result = dec;
			break;
		default:
			result = 0;
			break;
		}
		return result;
	}

	public void setMonthValue(int month, int value) {
		switch (month) {
		case 0:
			this.jan = value;
			break;
		case 1:
			this.feb = value;
			break;
		case 2:
			this.mar = value;
			break;
		case 3:
			this.apr = value;
			break;
		case 4:
			this.may = value;
			break;
		case 5:
			this.june = value;
			break;
		case 6:
			this.july = value;
			break;
		case 7:
			this.aug = value;
			break;
		case 8:
			this.sept = value;
			break;
		case 9:
			this.oct = value;
			break;
		case 10:
			this.nov = value;
			break;
		case 11:
			this.dec = value;
			break;
		default:
			break;
		}
	}

	public int[] getQuotaHistory() {
		if (updateTimestamp == null) {
			return null;
		}
		// Create calendar instance to get month of updateTimestamp
		Calendar updateDate = Calendar.getInstance();
		updateDate.setTime(updateTimestamp);
		// Jan ==> 0 , Feb ==> 1, ... etc.
		int month = updateDate.get(Calendar.MONTH);
		int i = month;
		for (int j = 0; j < 12; j++) {
			if (i == -1) {
				i = 11;
			}
			historyArr[j] = getMonthValue(--i);
		}
		return historyArr;

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
		if (!(object instanceof QuotaHistory)) {
			return false;
		}
		QuotaHistory other = (QuotaHistory) object;
		if ((this.accountId == null && other.accountId != null)
				|| (this.accountId != null && !this.accountId.equals(other.accountId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "QuotaHistory [accountId=" + accountId + ", jan=" + jan + ", feb=" + feb + ", mar=" + mar + ", apr="
				+ apr + ", may=" + may + ", june=" + june + ", july=" + july + ", aug=" + aug + ", sept=" + sept
				+ ", oct=" + oct + ", nov=" + nov + ", dec=" + dec + ", updateTimestamp=" + updateTimestamp
				+ "]";
	}

	

}
