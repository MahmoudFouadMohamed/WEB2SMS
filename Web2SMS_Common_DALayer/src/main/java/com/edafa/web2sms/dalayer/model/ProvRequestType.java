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

import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.model.constants.ProvRequestTypesConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "PROV_REQUEST_TYPES")
@XmlRootElement
@ObjectTypeConverter(name = "ProvRequestTypeNameConverter", dataType = String.class, objectType = ProvRequestTypeName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "ACTIVATE_ACCOUNT", objectValue = "ACTIVATE_ACCOUNT"),
		@ConversionValue(dataValue = "DEACTIVATE_ACCOUNT", objectValue = "DEACTIVATE_ACCOUNT"),
		@ConversionValue(dataValue = "SUSPEND_ACCOUNT", objectValue = "SUSPEND_ACCOUNT"),
		@ConversionValue(dataValue = "REACTIVATE_ACCT_AFTER_SUSPENSION", objectValue = "REACTIVATE_ACCT_AFTER_SUSPENSION"),
		@ConversionValue(dataValue = "ADD_SENDER_NAME", objectValue = "ADD_SENDER_NAME"),
		@ConversionValue(dataValue = "DELETE_SENDER_NAME", objectValue = "DELETE_SENDER_NAME"),
		@ConversionValue(dataValue = "USER_ADD", objectValue = "USER_ADD"),
		@ConversionValue(dataValue = "USER_DELETE", objectValue = "USER_DELETE"),
		@ConversionValue(dataValue = "CHANGE_SENDER_NAME", objectValue = "CHANGE_SENDER_NAME")})
@NamedQueries({
		@NamedQuery(name = "ProvRequestType.findAll", query = "SELECT p FROM ProvRequestType p"),
		@NamedQuery(name = "ProvRequestType.findByProvReqTypeId", query = "SELECT p FROM ProvRequestType p WHERE p.provReqTypeId = :provReqTypeId"),
		@NamedQuery(name = "ProvRequestType.findByProvReqTypeName", query = "SELECT p FROM ProvRequestType p WHERE p.provReqTypeName = :provReqTypeName")})
public class ProvRequestType implements Serializable, ProvRequestTypesConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "PROV_REQ_TYPE_ID")
	private Integer provReqTypeId;
	@Size(max = 100)
	@Column(name = "PROV_REQ_TYPE_NAME")
	@Enumerated(EnumType.STRING)
	@Convert
	private ProvRequestTypeName provReqTypeName;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "requestType")
	private List<ProvRequestActive> provRequestsList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "requestType")
	private List<ProvRequestArch> provRequestsArchList;

	public ProvRequestType() {
	}

	public ProvRequestType(Integer provReqTypeId) {
		this.provReqTypeId = provReqTypeId;
	}

	public Integer getProvReqTypeId() {
		return provReqTypeId;
	}

	public void setProvReqTypeId(Integer provReqTypeId) {
		this.provReqTypeId = provReqTypeId;
	}

	public ProvRequestTypeName getProvReqTypeName() {
		return provReqTypeName;
	}

	public void setProvReqTypeName(ProvRequestTypeName provReqTypeName) {
		this.provReqTypeName = provReqTypeName;
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
		hash += (provReqTypeId != null ? provReqTypeId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ProvRequestType)) {
			return false;
		}
		ProvRequestType other = (ProvRequestType) object;
		if ((this.provReqTypeId == null && other.provReqTypeId != null)
				|| (this.provReqTypeId != null && !this.provReqTypeId.equals(other.provReqTypeId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.edafa.web2sms.module.ProvRequestsTypes[ provReqTypeId=" + provReqTypeId + " ]";
	}

}
