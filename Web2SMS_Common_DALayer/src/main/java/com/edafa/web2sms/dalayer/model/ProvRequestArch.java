/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import static javax.persistence.AccessType.FIELD;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.ProvRequestArchConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "PROV_REQUESTS_ARCH")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ProvRequestArch.findAll", query = "SELECT p FROM ProvRequestArch p"),
		@NamedQuery(name = "ProvRequestArch.findByRequestArchId", query = "SELECT p FROM ProvRequestArch p WHERE p.requestId = :requestId"),
		@NamedQuery(name = "ProvRequestArch.findByEntryDate", query = "SELECT p FROM ProvRequestArch p WHERE p.entryDate = :entryDate"),
		@NamedQuery(name = "ProvRequestArch.findByAccountId", query = "SELECT p FROM ProvRequestArch p WHERE p.companyId = :companyId"),
		@NamedQuery(name = "ProvRequestArch.findByCompanyName", query = "SELECT p FROM ProvRequestArch p WHERE p.companyName = :companyName"),
		@NamedQuery(name = "ProvRequestArch.findByCallbackUrl", query = "SELECT p FROM ProvRequestArch p WHERE p.callbackUrl = :callbackUrl"),
		@NamedQuery(name = "ProvRequestArch.findByUpdateDate", query = "SELECT p FROM ProvRequestArch p WHERE p.updateDate = :updateDate"),
		@NamedQuery(name = "ProvRequestArch.findByUpdateDateAndStatus", query = "SELECT  NEW com.edafa.web2sms.dalayer.pojo.ProvisioningEvent(p.updateDate, p.companyId)  FROM ProvRequestArch p WHERE p.updateDate >= :startDate AND p.updateDate < :endDate and p.status= :status AND p.companyId IN (SELECT c.accountUser.account.accountId FROM Campaign c, SMSLog s WHERE s.campaign.campaignId = c.campaignId and s.processingDate >= :startDate AND s.processingDate < :endDate ) ORDER BY p.updateDate ASC"),
		@NamedQuery(name = "ProvRequestArch.findBySenderName", query = "SELECT p FROM ProvRequestArch p WHERE p.senderName = :senderName")})
@Access(FIELD)
public class ProvRequestArch extends ProvRequest implements Serializable, ProvRequestArchConst {
	private static final long serialVersionUID = -7946102434955988318L;

	public ProvRequestArch() {
	}

	public ProvRequestArch(ProvRequestActive req) {
		super(req);
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
	public String toString() {
		return "com.edafa.web2sms.module.ProvRequestArch[ requestId=" + requestId + " ]";
	}

}
