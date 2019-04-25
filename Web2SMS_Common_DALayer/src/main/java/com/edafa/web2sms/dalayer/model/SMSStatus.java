/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.model.constants.SMSStatusConst;

/**
 * 
 * @author akhalifah
 */
@Cacheable(value = true)
@Entity
@Table(name = "SMS_STATUS")
@XmlRootElement
@ObjectTypeConverter(name = "SMSStatusNameConverter", dataType = java.lang.String.class, objectType = SMSStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "SUBMITTED", objectValue = "SUBMITTED"),
		@ConversionValue(dataValue = "SENT", objectValue = "SENT"),
		@ConversionValue(dataValue = "DELIVERED", objectValue = "DELIVERED"),
		@ConversionValue(dataValue = "TIMED_OUT", objectValue = "TIMED_OUT"),
		@ConversionValue(dataValue = "NOT_DELIVERED", objectValue = "NOT_DELIVERED"),
		@ConversionValue(dataValue = "FAILED", objectValue = "FAILED"),
		@ConversionValue(dataValue = "FAILED_TO_SEND", objectValue = "FAILED_TO_SEND"),
		@ConversionValue(dataValue = "REJECTED", objectValue = "REJECTED"),
		@ConversionValue(dataValue = "RECEIVED", objectValue = "RECEIVED"),
		@ConversionValue(dataValue = "EXPIRED", objectValue = "EXPIRED") })
@NamedQueries({
		@NamedQuery(name = "SMSStatus.findAll", query = "SELECT s FROM SMSStatus s"),
		@NamedQuery(name = "SMSStatus.findById", query = "SELECT s FROM SMSStatus s WHERE s.id = :id"),
		@NamedQuery(name = "SMSStatus.findByStatusName", query = "SELECT s FROM SMSStatus s WHERE s.name = :statusName"),
		@NamedQuery(name = "SMSStatus.findByDescription", query = "SELECT s FROM SMSStatus s WHERE s.description = :description") })
public class SMSStatus implements Serializable, SMSStatusConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "STATUS_ID")
	private Integer id;
	@Basic(optional = false)
	@Column(name = "STATUS_NAME")
	@Enumerated(EnumType.STRING)
	@Convert(value = "SMSStatusNameConverter")
	private SMSStatusName name;

	@Column(name = "DESCRIPTION")
	private String description;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "status", fetch = FetchType.LAZY)
	private List<SMSLog> sMSLogList;

	public SMSStatus() {
	}

	public SMSStatus(Integer id) {
		this.id = id;
	}

	public SMSStatus(Integer id, SMSStatusName name) {
		this.id = id;
		this.name = name;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public SMSStatusName getName() {
		return name;
	}

	public void setName(SMSStatusName name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public List<SMSLog> getSMSLogList() {
		return sMSLogList;
	}

	public void setSMSLogList(List<SMSLog> sMSLogList) {
		this.sMSLogList = sMSLogList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SMSStatus)) {
			return false;
		}
		SMSStatus other = (SMSStatus) object;
		if ((this.id == null && other.id != null)
				|| (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SMSStatus [id=" + id + ", name=" + name + ", description="
				+ description + "]";
	}
}
