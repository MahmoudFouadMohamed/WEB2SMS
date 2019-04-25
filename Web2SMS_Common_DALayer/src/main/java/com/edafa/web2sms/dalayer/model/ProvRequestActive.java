/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.ProvRequestActiveConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "PROV_REQUESTS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "ProvRequestActive.findAll", query = "SELECT p FROM ProvRequestActive p"),
		@NamedQuery(name = "ProvRequestActive.findByRequestId", query = "SELECT p FROM ProvRequestActive p WHERE p.requestId = :requestId"),
		@NamedQuery(name = "ProvRequestActive.findByEntryDate", query = "SELECT p FROM ProvRequestActive p WHERE p.entryDate = :entryDate"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyId", query = "SELECT p FROM ProvRequestActive p WHERE p.companyId = :companyId"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyName", query = "SELECT p FROM ProvRequestActive p WHERE p.companyName = :companyName"),
		@NamedQuery(name = "ProvRequestActive.findByCallbackUrl", query = "SELECT p FROM ProvRequestActive p WHERE p.callbackUrl = :callbackUrl"),
		@NamedQuery(name = "ProvRequestActive.findByUpdateDate", query = "SELECT p FROM ProvRequestActive p WHERE p.updateDate = :updateDate"),
		@NamedQuery(name = "ProvRequestActive.findBySenderName", query = "SELECT p FROM ProvRequestActive p WHERE p.senderName = :senderName"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyIdAndStatus", query = "SELECT p FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.status IN :statuses"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyAdminAndStatuses", query = "SELECT p FROM ProvRequestActive p WHERE p.companyAdmin = :companyAdmin AND p.status IN :statuses"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyIdAndTypeAndStatus", query = "SELECT p FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status = :status"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyIdAndTypeAndStatuses", query = "SELECT p FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status IN :statuses"),
		@NamedQuery(name = "ProvRequestActive.findByCompanyIdTypeStatusAndSender", query = "SELECT p FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status IN :statuses AND p.senderName = :senderName"),
		@NamedQuery(name = "ProvRequestActive.countByCompanyIdAndTypeAndStatus", query = "SELECT COUNT(p) FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status = :status"),
		@NamedQuery(name = "ProvRequestActive.countByCompanyIdAndTypeAndStatuses", query = "SELECT COUNT(p) FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status IN :statuses"),
		@NamedQuery(name = "ProvRequestActive.countByCompanyIdTypeStatusAndSender", query = "SELECT COUNT(p) FROM ProvRequestActive p WHERE p.companyId = :companyId AND p.requestType = :type AND p.status = :status AND p.senderName = :senderName"),

		@NamedQuery(name = "ProvRequestActive.updateStatus", query = "UPDATE ProvRequestActive p SET p.status = :status WHERE p.requestId = :requestId") })
public class ProvRequestActive extends ProvRequest implements Serializable, ProvRequestActiveConst {

	private static final long serialVersionUID = 6438023143969132780L;

	
	public ProvRequestActive() {

	}

	public ProvRequestActive(ProvRequestActive req) {
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
		return "com.edafa.web2sms.module.ProvRequests[ requestId=" + getRequestId() + " ]";
	}

}
