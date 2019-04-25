package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.TierTypesEnum;
import com.edafa.web2sms.dalayer.model.constants.TierTypeConst;

;

/**
 *
 * @author mayahmed
 */
@Entity
@Table(name = "TIER_TYPE")
@XmlRootElement
@ObjectTypeConverter(name = "TierTypesConverter", dataType = String.class, objectType = TierTypesEnum.class, defaultObjectValue = "POSTPAID", conversionValues = {
		@ConversionValue(dataValue = "POSTPAID", objectValue = "POSTPAID"),
		@ConversionValue(dataValue = "PREPAID", objectValue = "PREPAID") })
@NamedQueries({
		@NamedQuery(name = "TierType.findAll", query = "SELECT t FROM TierType t"),
		@NamedQuery(name = "TierType.findById", query = "SELECT t FROM TierType t WHERE t.tierTypeId =:tierTypeId") })
public class TierType  implements Serializable, TierTypeConst{
	private static final long serialVersionUID = 1L;

	@Id
	@Basic(optional = false)
	@Column(name = "TIER_TYPE_ID")
	private Integer tierTypeId;

	@Basic(optional = false)
	@Enumerated(EnumType.STRING)
	@Convert(value = "TierTypesConverter")
	@Column(name = "TIER_TYPE_NAME")
	private TierTypesEnum tierTypeName;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "tierType")
	private List<Tier> tiersList;

	public TierType() {
	}

	public TierType(Integer tierTypeId) {
		this.tierTypeId = tierTypeId;
	}

	public TierType(Integer tierTypeId, TierTypesEnum tierTypeName) {
		this.tierTypeId = tierTypeId;
		this.tierTypeName = tierTypeName;
	}

	public TierType(TierTypesEnum tierTypeName) {
		this.tierTypeId = tierTypeName.ordinal() + 1; // enum starts from 0 ids
														// from 1 .
		this.tierTypeName = tierTypeName;
	}

	public Integer getTierTypeId() {
		return tierTypeId;
	}

	public void setTierTypeId(Integer tierTypeId) {
		this.tierTypeId = tierTypeId;
	}

	public TierTypesEnum getTierTypeName() {
		return tierTypeName;
	}

	public void setTierTypeName(TierTypesEnum tierTypeName) {
		this.tierTypeName = tierTypeName;
	}

	@XmlTransient
	public List<Tier> getTiersList() {
		return tiersList;
	}

	public void setTiersList(List<Tier> tiersList) {
		this.tiersList = tiersList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (tierTypeId != null ? tierTypeId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof TierType)) {
			return false;
		}
		TierType other = (TierType) object;
		// if ((this.tierTypeId == null && other.tierTypeId != null) ||
		// (this.tierTypeId != null &&
		// !this.tierTypeId.equals(other.tierTypeId))) {
		return false;
		// }
		// return true;
	}

	@Override
	public String toString() {
		return "jpa.controllers.TierType[ tierTypeId=" + tierTypeId +" tier type name : "+ tierTypeName.name()+" ]";
	}

}
