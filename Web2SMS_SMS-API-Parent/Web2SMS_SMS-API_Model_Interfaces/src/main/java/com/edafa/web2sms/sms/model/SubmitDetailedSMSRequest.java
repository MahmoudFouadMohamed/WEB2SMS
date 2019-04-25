package com.edafa.web2sms.sms.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;

@XmlType(name = "SubmitDetailedSMSRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitDetailedSMSRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitDetailedSMSRequest extends SubmitSMSRequest implements Cacheable, Retriable {

	private static final long serialVersionUID = -7316739397742926621L;

	@XmlElement(name = "IP", required = true, nillable = false)
	protected String ip;

	@XmlElement(name = "TRX_ID", required = true, nillable = false)
	protected String trxId;

	@XmlElement(name = "SMSID_PREFIX")
	protected String smsIdPrefix;

	@XmlTransient
	protected Date cacheDate;

	@XmlTransient
	protected int validityMinutes;

	@XmlTransient
	protected int retryCount;

	@XmlTransient
	protected int retryExpiredCount;

	@XmlElement
	protected boolean cachedRequest;

	public SubmitDetailedSMSRequest() {

	}

	public SubmitDetailedSMSRequest(SubmitSMSRequest request, String ip, String trxId) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash = request.secureHash;
		this.SMSList = request.SMSList;
		this.ip = ip;
		this.trxId = trxId;
	}

	public SubmitDetailedSMSRequest(SubmitSMSRequest request, String ip, String SMSidPrefix, String trxId) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash = request.secureHash;
		this.SMSList = request.SMSList;
		this.ip = ip;
		this.smsIdPrefix = SMSidPrefix;
		this.trxId = trxId;

		for (SMSDetails smsDetails : SMSList) {
			smsDetails.setSmsId(SMSId.getSMSId(smsIdPrefix).getId());
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTrxId() {
		return trxId;
	}

	public void setTrxId(String trxId) {
		this.trxId = trxId;
	}

	public String getSmsIdPrefix() {
		return smsIdPrefix;
	}

	public void setSmsIdPrefix(String smsIdPrefix) {
		this.smsIdPrefix = smsIdPrefix;
	}

	public int getValidityMinutes() {
		return validityMinutes;
	}

	public void setValidityMinutes(int validityMinutes) {
		this.validityMinutes = validityMinutes;
	}

	@Override
	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public Date getCacheDate() {
		return cacheDate;
	}

	public void setCacheDate(Date cacheDate) {
		this.cacheDate = cacheDate;
	}

	public boolean isCachedRequest() {
		return cachedRequest;
	}

	public void setCachedRequest(boolean cachedRequest) {
		this.cachedRequest = cachedRequest;
	}

	public int getRetryExpiredCount() {
		return retryExpiredCount;
	}

	public void setRetryExpiredCount(int retryExpiredCount) {
		this.retryExpiredCount = retryExpiredCount;
	}
	@Override
	public int incrementRetryCount() {
		retryCount++;
		return retryCount;
	}

	public int incrementExpiredRetryCount() {
		retryExpiredCount++;
		return retryExpiredCount;
	}

	@Override
	public String toString() {
		StringBuilder str = new StringBuilder("SubmitDetailedSMSRequest{ip=");
		str = str.append(ip).append(", trxId=").append(trxId).append(", accountId=").append(accountId)
				.append(", secureHash=").append(secureHash).append(", SMSList=").append(SMSList).append(", cacheDate=")
				.append(cacheDate).append(", validityMinutes=").append(validityMinutes).append(", retryCount=")
				.append(retryCount).append(", retryExpiredCount=").append(retryExpiredCount).append(", cachedRequest=")
				.append(cachedRequest).append('}');
		return str.toString();
	}
}
