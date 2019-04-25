package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.ListTypeName;

/**
 *
 * @author khalid
 */
@Entity
@Table(name = "LIST_TYPE")
@XmlRootElement
@ObjectTypeConverter(name = "ListTypeNameConverter", dataType = String.class, objectType = ListTypeName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "NORMAL_LIST", objectValue = "NORMAL_LIST"),
		@ConversionValue(dataValue = "TEMP_LIST", objectValue = "TEMP_LIST"),
		@ConversionValue(dataValue = "VIRTUAL_LIST", objectValue = "VIRTUAL_LIST"),
		@ConversionValue(dataValue = "PRENORMAL_LIST", objectValue = "PRENORMAL_LIST"),
		@ConversionValue(dataValue = "INTRA_SUB_LIST", objectValue = "INTRA_SUB_LIST"),
		@ConversionValue(dataValue = "CUSTOMIZED_LIST", objectValue = "CUSTOMIZED_LIST"),
		@ConversionValue(dataValue = "PRECUSTOMIZED_LIST", objectValue = "PRECUSTOMIZED_LIST"),
		@ConversionValue(dataValue = "INTRA_LIST", objectValue = "INTRA_LIST"),
		@ConversionValue(dataValue = "PREINTRASUB_LIST", objectValue = "PRE_INTRA_SUB_LIST") })
@NamedQueries({
		@NamedQuery(name = "ListType.findAll", query = "SELECT l FROM ListType l"),
		@NamedQuery(name = "ListType.findByListTypeId", query = "SELECT l FROM ListType l WHERE l.listTypeId = :listTypeId"),
		@NamedQuery(name = "ListType.findByListTypeName", query = "SELECT l FROM ListType l WHERE l.listTypeName = :listTypeName") })
public class ListType implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "LIST_TYPE_ID")
	private Integer listTypeId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 20)
	@Enumerated(EnumType.STRING)
	@Convert(value = "ListTypeNameConverter")
	@Column(name = "LIST_TYPE_NAME")
	private ListTypeName listTypeName;

	public ListType() {
	}

	public ListType(Integer listTypeId) {
		this.listTypeId = listTypeId;
	}

	public ListType(Integer listTypeId, ListTypeName listTypeName) {
		this.listTypeId = listTypeId;
		this.listTypeName = listTypeName;
	}

	public Integer getListTypeId() {
		return listTypeId;
	}

	public void setListTypeId(Integer listTypeId) {
		this.listTypeId = listTypeId;
	}

	public ListTypeName getListTypeName() {
		return listTypeName;
	}

	public void setListTypeName(ListTypeName listTypeName) {
		this.listTypeName = listTypeName;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (listTypeId != null ? listTypeId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ListType)) {
			return false;
		}
		ListType other = (ListType) object;
		if ((this.listTypeId == null && other.listTypeId != null)
				|| (this.listTypeId != null && !this.listTypeId.equals(other.listTypeId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "ListType( listTypeId=" + listTypeId + ", listTypeName=" + listTypeName + " )";
	}

}
