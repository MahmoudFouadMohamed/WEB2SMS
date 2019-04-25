 package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.SMSCConfigConst;

/**
 * 
 * @author akhalifah
 */
@Entity
@Table(name = "SMSC_CONFIG")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "SMSCConfig.findAll", query = "SELECT s FROM SMSCConfig s"),
		@NamedQuery(name = "SMSCConfig.findAllCount", query = "SELECT COUNT(s) FROM SMSCConfig s"),
		@NamedQuery(name = "SMSCConfig.findBySmscId", query = "SELECT s FROM SMSCConfig s WHERE s.smscId = :smscId"),
		@NamedQuery(name = "SMSCConfig.findBySessionId", query = "SELECT s FROM SMSCConfig s WHERE s.sessionId = :sessionId"),
		@NamedQuery(name = "SMSCConfig.findBySmscSystemId", query = "SELECT s FROM SMSCConfig s WHERE s.smscSystemId = :smscSystemId"),
		@NamedQuery(name = "SMSCConfig.findByServiceType", query = "SELECT s FROM SMSCConfig s WHERE s.serviceType = :serviceType"),
		@NamedQuery(name = "SMSCConfig.findByAddress", query = "SELECT s FROM SMSCConfig s WHERE s.address = :address"),
		@NamedQuery(name = "SMSCConfig.findByPort", query = "SELECT s FROM SMSCConfig s WHERE s.port = :port"),
		@NamedQuery(name = "SMSCConfig.findBySocketTimeOut", query = "SELECT s FROM SMSCConfig s WHERE s.socketTimeOut = :socketTimeOut"),
		@NamedQuery(name = "SMSCConfig.findActive", query = "SELECT s FROM SMSCConfig s WHERE s.active = true ORDER BY s.sessionId") })
public class SMSCConfig implements Serializable, SMSCConfigConst {
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Basic(optional = false)
	@Column(name = "SESSION_ID", insertable = false)
	private Short sessionId;
	@Basic(optional = false)
	@Column(name = "SMSC_SYSTEM_ID")
	private String smscSystemId;
	@Basic(optional = false)
	@Column(name = "PASSWORD")
	private String password;
	@Column(name = "SERVICE_TYPE")
	private String serviceType;
	@Basic(optional = false)
	@Column(name = "ADDRESS")
	private String address;
	@Basic(optional = false)
	@Column(name = "PORT")
	private Integer port;
	@Basic(optional = false)
	@Column(name = "SOCKET_TIME_OUT")
	private Integer socketTimeOut;
	@Basic(optional = false)
	@Column(name = "ACTIVE")
	private boolean active;
	@JoinColumn(name = "BIND_TYPE_ID", referencedColumnName = "SMSC_BIND_TYPE_ID")
	@ManyToOne(optional = false)
	private SMSCBindType bindType;
	@Basic(optional = false)
	@Column(name = "SMSC_ID", insertable = false)
	private Short smscId;

	@Column(name = "MAX_SENDING_RATE")
	private Integer maxSendingRate;

	@Transient
	private String transientValue;

	public SMSCConfig() {
	}

	public SMSCConfig(Short sessionId) {
		this.sessionId = sessionId;
	}

	// public SMSCConfig(Short sessionId, String smscSystemId, String password,
	// String address, int port, int socketTimeOut) {
	// this.sessionId = sessionId;
	// this.smscSystemId = smscSystemId;
	// this.password = password;
	// this.address = address;
	// this.port = port;
	// this.socketTimeOut = socketTimeOut;
	// }

	public SMSCConfig(String smscSystemId, String password, String address,
			int port, int socketTimeOut, SMSCBindType sMSCBindType) {
		this.smscSystemId = smscSystemId;
		this.password = password;
		this.address = address;
		this.port = port;
		this.socketTimeOut = socketTimeOut;
		this.bindType = sMSCBindType;
	}

	public Short getSessionId() {
		return sessionId;
	}

	public void setSessionId(Short sessionId) {
		this.sessionId = sessionId;
	}

	public Short getSmscId() {
		return smscId;
	}

	public void setSmscId(Short smscId) {
		this.smscId = smscId;
	}

	public String getSmscSystemId() {
		return smscSystemId;
	}

	public void setSmscSystemId(String smscSystemId) {
		this.smscSystemId = smscSystemId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getSocketTimeOut() {
		return socketTimeOut;
	}

	public void setSocketTimeOut(int socketTimeOut) {
		this.socketTimeOut = socketTimeOut;
	}

	public SMSCBindType getBindType() {
		return bindType;
	}

	public void setBindType(SMSCBindType sMSCBindType) {
		this.bindType = sMSCBindType;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public int getMaxSendingRate() {
		return maxSendingRate;
	}

	public void setMaxSendingRate(int maxSendingRate) {
		this.maxSendingRate = maxSendingRate;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (sessionId != null ? sessionId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SMSCConfig)) {
			return false;
		}
		SMSCConfig other = (SMSCConfig) object;
		if ((this.sessionId == null && other.sessionId != null)
				|| (this.sessionId != null && !this.sessionId.equals(other.sessionId))
				|| this.active != other.active
				|| (this.address == null && other.address != null)
				|| (this.address != null && !this.address.equals(other.address))
				|| (this.bindType == null && other.bindType != null)
				|| (this.bindType != null && !this.bindType
						.equals(other.bindType))
				|| (this.maxSendingRate == null && other.maxSendingRate != null)
				|| (this.maxSendingRate != null && !this.maxSendingRate
						.equals(other.maxSendingRate))
				|| (this.smscSystemId == null && other.smscSystemId != null)
				|| (this.smscSystemId != null && !this.smscSystemId
						.equals(other.smscSystemId))
				|| (this.password == null && other.password != null)
				|| (this.password != null && !this.password
						.equals(other.password))
				|| (this.port == null && other.port != null)
				|| (this.port != null && !this.port.equals(other.port))
				|| (this.serviceType == null && other.serviceType != null)
				|| (this.serviceType != null && !this.serviceType
						.equals(other.serviceType))
				|| (this.socketTimeOut == null && other.socketTimeOut != null)
				|| (this.socketTimeOut != null && !this.socketTimeOut
						.equals(other.socketTimeOut))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SMSCConfig [sessionId=" + sessionId + ", smscId=" + smscId + ", smscSystemId="
				+ smscSystemId + ", serviceType=" + serviceType + ", address="
				+ address + ", port=" + port + ", socketTimeOut="
				+ socketTimeOut + ", bindType=" + bindType + "]";
	}

	public String logId() {
		return "SMSC(" + sessionId + ") ";
	}

	/**
	 * @return the transientValue
	 */
	public String getTransientValue() {
		return transientValue;
	}

	/**
	 * @param transientValue
	 *            the transientValue to set
	 */
	public void setTransientValue(String transientValue) {
		this.transientValue = transientValue;
	}
}
