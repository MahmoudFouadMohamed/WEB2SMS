package com.edafa.web2sms.dalayer.pojo;

public class AggCDR {

	String billingMSISDN;
	String companyId;
	String sender;
	String msisdnType;
	Integer aggSMSSegCount;

	public AggCDR(String billingMSISDN, String companyId, String sender, Long aggSMSSegCount, String msisdnType) {
		super();
		this.billingMSISDN = billingMSISDN;
		this.companyId = companyId;
		this.sender = sender;
		this.msisdnType = msisdnType;
		this.aggSMSSegCount = aggSMSSegCount.intValue();
	}

	public AggCDR(String billingMSISDN, String companyId, String sender, Long aggSMSSegCount) {
		super();
		this.billingMSISDN = billingMSISDN;
		this.companyId = companyId;
		this.sender = sender;
		// this.msisdnType = msisdnType;
		this.aggSMSSegCount = aggSMSSegCount.intValue();
	}

	/**
	 * @return the billingMSISDN
	 */
	public String getBillingMSISDN() {
		return billingMSISDN;
	}

	/**
	 * @param billingMSISDN
	 *            the billingMSISDN to set
	 */
	public void setBillingMSISDN(String billingMSISDN) {
		this.billingMSISDN = billingMSISDN;
	}

	/**
	 * @return the companyId
	 */
	public String getCompanyId() {
		return companyId;
	}

	/**
	 * @param companyId
	 *            the companyId to set
	 */
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}

	/**
	 * @return the sender
	 */
	public String getSender() {
		return sender;
	}

	/**
	 * @param sender
	 *            the sender to set
	 */
	public void setSender(String sender) {
		this.sender = sender;
	}

	/**
	 * @return the msisdnType
	 */
	public String getMsisdnType() {
		return msisdnType;
	}

	/**
	 * @param msisdnType
	 *            the msisdnType to set
	 */
	public void setMsisdnType(String msisdnType) {
		this.msisdnType = msisdnType;
	}

	/**
	 * @return the aggSMSSegCount
	 */
	public Integer getAggSMSSegCount() {
		return aggSMSSegCount;
	}

	/**
	 * @param aggSMSSegCount
	 *            the aggSMSSegCount to set
	 */
	public void setAggSMSSegCount(Integer aggSMSSegCount) {
		this.aggSMSSegCount = aggSMSSegCount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AggCDR (billingMSISDN=" + billingMSISDN + ", companyId=" + companyId + ", sender=" + sender
				+ ", msisdnType=" + msisdnType + ", aggSMSSegCount=" + aggSMSSegCount + ")";
	}

}
