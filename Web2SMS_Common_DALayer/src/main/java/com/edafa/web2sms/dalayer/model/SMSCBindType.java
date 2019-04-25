package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
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

import com.edafa.web2sms.dalayer.enums.BindTypeEnum;
import com.edafa.web2sms.dalayer.model.constants.BindTypeConst;

/**
 * 
 * @author akhalifah
 */
@Entity
@Table(name = "SMSC_BIND_TYPE")
@XmlRootElement
@ObjectTypeConverter(name = "BindTypeConverter", dataType = java.lang.String.class, objectType = BindTypeEnum.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "TRANSMITTER", objectValue = "TRANSMITTER"),
		@ConversionValue(dataValue = "RECEIVER", objectValue = "RECEIVER"),
		@ConversionValue(dataValue = "TRANSCEIVER", objectValue = "TRANSCEIVER") })
@NamedQueries({ @NamedQuery(name = "SMSCBindType.findAll", query = "SELECT b FROM SMSCBindType b"),
		@NamedQuery(name = "SMSCBindType.findById", query = "SELECT b FROM SMSCBindType b WHERE b.id = :id"),
		@NamedQuery(name = "SMSCBindType.findByType", query = "SELECT b FROM SMSCBindType b WHERE b.type = :type") })
@Access(AccessType.FIELD)
public class SMSCBindType implements Serializable, BindTypeConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "SMSC_BIND_TYPE_ID")
	private Short id;
	// @Basic(optional = false)
	// @Column(name = "TYPE")
	// private String type;
	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	@Convert(value = "BindTypeConverter")
	private BindTypeEnum type;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "bindType", fetch = FetchType.LAZY)
	private List<SMSCConfig> sMSCConfigList;

	public SMSCBindType() {
	}

	public SMSCBindType(Short id) {
		this.id = id;
	}

	public SMSCBindType(Short id, BindTypeEnum type) {
		this.id = id;
		this.type = type;
	}

	// public BindTypeEnum(Short id, String type) {
	// this.id = id;
	// this.type = type;
	// }

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public BindTypeEnum getType() {
		return type;
	}

	// public String getType() {
	// return type;
	// }
	public void setType(BindTypeEnum type) {
		this.type = type;
	}

	// public void setType(String type) {
	// this.type = type;
	// }

	@XmlTransient
	public List<SMSCConfig> getSMSCConfigList() {
		return sMSCConfigList;
	}

	public void setSMSCConfigList(List<SMSCConfig> sMSCConfigList) {
		this.sMSCConfigList = sMSCConfigList;
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
		if (!(object instanceof SMSCBindType)) {
			return false;
		}
		SMSCBindType other = (SMSCBindType) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "BindTypeEnum [id=" + id + ", type=" + type + "]";
	}

}
