/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.IntraSendersConst;

/**
 *
 * @author mayahmed
 */
@Entity
@Table(name = "INTRA_SENDERS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "IntraSender.findAll", query = "SELECT i FROM IntraSender i"),
		@NamedQuery(name = "IntraSender.findByIntraSenderId", query = "SELECT i FROM IntraSender i WHERE i.intraSenderId = :intraSenderId"),
		@NamedQuery(name = "IntraSender.findSystemSender", query = "SELECT i FROM IntraSender i WHERE i.systemSenderFlag = true "),
		@NamedQuery(name = "IntraSender.findBySenderName", query = "SELECT i FROM IntraSender i WHERE i.senderName = :senderName"),
		@NamedQuery(name = "IntraSender.countBySenderName", query = "SELECT COUNT(i) FROM IntraSender i WHERE i.senderName = :senderName"),
		@NamedQuery(name = "IntraSender.findBySystemSenderFlag", query = "SELECT i FROM IntraSender i WHERE i.systemSenderFlag = :systemSenderFlag") })
public class IntraSender implements Serializable, IntraSendersConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 20)
	@SequenceGenerator(name = "IntraSenderIdSeq", sequenceName = "INTRA_SENDER_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "IntraSenderIdSeq")
	@Column(name = "INTRA_SENDER_ID")
	private String intraSenderId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 20)
	@Column(name = "SENDER_NAME")
	private String senderName;
	@Column(name = "SYSTEM_SENDER_FLAG")
	private boolean systemSenderFlag;
	@ManyToMany
	@JoinTable(name = "INTRA_SENDERS_ACCOUNTS", joinColumns = @JoinColumn(name = "INTRA_SENDERS_ID"), inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID"))
	private List<Account> accountList;

	public IntraSender() {
	}

	public IntraSender(String intraSenderId) {
		this.intraSenderId = intraSenderId;
	}

	public IntraSender(String intraSenderId, String senderName) {
		this.intraSenderId = intraSenderId;
		this.senderName = senderName;
	}

	public String getIntraSenderId() {
		return intraSenderId;
	}

	public void setIntraSenderId(String intraSenderId) {
		this.intraSenderId = intraSenderId;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean getSystemSenderFlag() {
		return systemSenderFlag;
	}

	public void setSystemSenderFlag(boolean systemSenderFlag) {
		this.systemSenderFlag = systemSenderFlag;
	}

	@XmlTransient
	public List<Account> getAccountList() {
		return accountList;
	}

	public void setAccountList(List<Account> accountList) {
		this.accountList = accountList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (intraSenderId != null ? intraSenderId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof IntraSender)) {
			return false;
		}
		IntraSender other = (IntraSender) object;
		if ((this.intraSenderId == null && other.intraSenderId != null)
				|| (this.intraSenderId != null && !this.intraSenderId.equals(other.intraSenderId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "IntraSender [intraSenderId=" + intraSenderId + ", senderName=" + senderName + ", systemSenderFlag="
				+ systemSenderFlag + ", accountList=" + accountList + "]";
	}

}
