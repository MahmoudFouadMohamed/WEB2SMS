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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.model.constants.ProvRequestStatusConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "PROV_REQUEST_STATUS")
@XmlRootElement
@ObjectTypeConverter(name = "ProvReqStatusNameConverter", dataType = String.class, objectType = ProvReqStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "PENDING", objectValue = "PENDING"),
		@ConversionValue(dataValue = "SUCCESS", objectValue = "SUCCESS"),
		@ConversionValue(dataValue = "CANCELLED", objectValue = "CANCELLED"),
		@ConversionValue(dataValue = "FAIL", objectValue = "FAIL") })
@NamedQueries({
		@NamedQuery(name = "ProvRequestStatus.findAll", query = "SELECT p FROM ProvRequestStatus p"),
		@NamedQuery(name = "ProvRequestStatus.findByProvStatusId", query = "SELECT p FROM ProvRequestStatus p WHERE p.provStatusId = :provStatusId"),
		@NamedQuery(name = "ProvRequestStatus.findByStatusName", query = "SELECT p FROM ProvRequestStatus p WHERE p.statusName = :statusName"),
		@NamedQuery(name = "ProvRequestStatus.findByDescription", query = "SELECT p FROM ProvRequestStatus p WHERE p.description = :description") })
public class ProvRequestStatus implements Serializable, ProvRequestStatusConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "PROV_STATUS_ID")
	private Integer provStatusId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 200)
	@Column(name = "STATUS_NAME")
	@Enumerated(EnumType.STRING)
	@Convert(value = "ProvReqStatusNameConverter")
	private ProvReqStatusName statusName;

	@Size(max = 200)
	@Column(name = "DESCRIPTION")
	private String description;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "status")
	private List<ProvRequestActive> provRequestsList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "status")
	private List<ProvRequestArch> provRequestsArchList;

	public ProvRequestStatus() {
	}

	public ProvRequestStatus(Integer provStatusId) {
		this.provStatusId = provStatusId;
	}

	public ProvRequestStatus(Integer provStatusId, ProvReqStatusName statusName) {
		this.provStatusId = provStatusId;
		this.statusName = statusName;
	}

	public Integer getProvStatusId() {
		return provStatusId;
	}

	public void setProvStatusId(Integer provStatusId) {
		this.provStatusId = provStatusId;
	}

	public ProvReqStatusName getStatusName() {
		return statusName;
	}

	public void setStatusName(ProvReqStatusName statusName) {
		this.statusName = statusName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlTransient
	public List<ProvRequestActive> getProvRequestsList() {
		return provRequestsList;
	}

	public void setProvRequestsList(List<ProvRequestActive> provRequestsList) {
		this.provRequestsList = provRequestsList;
	}

	@XmlTransient
	public List<ProvRequestArch> getProvRequestsArchList() {
		return provRequestsArchList;
	}

	public void setProvRequestsArchList(List<ProvRequestArch> provRequestsArchList) {
		this.provRequestsArchList = provRequestsArchList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (provStatusId != null ? provStatusId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ProvRequestStatus)) {
			return false;
		}
		ProvRequestStatus other = (ProvRequestStatus) object;
		if ((this.provStatusId == null && other.provStatusId != null)
				|| (this.provStatusId != null && !this.provStatusId.equals(other.provStatusId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.edafa.web2sms.module.ProvRequestStatus[ provStatusId=" + provStatusId + " ]";
	}

}
