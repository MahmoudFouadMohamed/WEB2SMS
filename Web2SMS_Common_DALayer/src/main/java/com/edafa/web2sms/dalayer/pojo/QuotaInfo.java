package com.edafa.web2sms.dalayer.pojo;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "QuotaInfo", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuotaInfo implements Serializable {

	@XmlElement(required = true, nillable = false)
	private String billingMsisdn;
	@XmlElement(required = true, nillable = false)
	private int grantedSMS;
	@XmlElement(required = true, nillable = false)
	private int consumedSMS;
	@XmlElement(required = true, nillable = false)
	private double consumedRatio;
	@XmlElement(required = true, nillable = false)
	private Date expiryDate;
	@XmlElement(required = true, nillable = false)
	private int  reservedSMS;
	
	

	public String getBillingMsisdn() {
		return billingMsisdn;
	}

	public void setBillingMsisdn(String billingMsisdn) {
		this.billingMsisdn = billingMsisdn;
	}

	public int getGrantedSMS() {
		return grantedSMS;
	}

	public void setGrantedSMS(int grantedSMS) {
		this.grantedSMS = grantedSMS;
	}

	public int getConsumedSMS() {
		return consumedSMS;
	}

	public void setConsumedSMS(int consumedSMS) {
		this.consumedSMS = consumedSMS;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date date) {
		this.expiryDate = date;
	}

	public double getConsumedRatio() {
		consumedRatio = (consumedSMS * 1.0) / (grantedSMS * 1.0);
		return consumedRatio;
	}

	
	
	

	public int getReservedSMS() {
		return reservedSMS;
	}

	public void setReservedSMS(int reservedSMS) {
		this.reservedSMS = reservedSMS;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// DateFormat df = new SimpleDateFormat(Configs.);
		return "(billingMsisdn=" + billingMsisdn + ", grantedSMS=" + grantedSMS + ", consumedSMS=" + consumedSMS
				+ ", consumedRatio=" + consumedRatio + ", expiryDate=" + expiryDate + ") ";
	}
}
