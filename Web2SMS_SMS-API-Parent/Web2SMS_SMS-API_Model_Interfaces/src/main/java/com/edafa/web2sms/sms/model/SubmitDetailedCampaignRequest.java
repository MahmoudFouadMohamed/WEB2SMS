package com.edafa.web2sms.sms.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SubmitDetailedCampaignRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitDetailedCampaignRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitDetailedCampaignRequest extends SubmitCampaignRequest implements Retriable, Cacheable {


	private static final long serialVersionUID = 288482507619449948L;

	@XmlElement(name = "IP")
	protected String ip;

	@XmlElement(name = "TRX_ID")
	protected String trxId;

	@XmlElement(name = "CampaignId")
	protected String campaignId;
	
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
	

	public SubmitDetailedCampaignRequest() {

	}

	public SubmitDetailedCampaignRequest(SubmitCampaignRequest request, String ip, String trxId, String campaignId) {
		this.accountId = request.accountId;
		this.password = request.password;
		this.secureHash = request.secureHash;
		this.SMSText = request.SMSText;
		this.senderName = request.senderName;
		this.CampaignName = request.CampaignName;
		this.msisdns = request.msisdns;
		this.ip = ip;
		this.trxId = trxId;
		this.campaignId = campaignId;
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

	/* (non-Javadoc)
	 * @see com.edafa.web2sms.sms.model.Cacheable#getValidityMinutes()
	 */
	@Override
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

	/* (non-Javadoc)
	 * @see com.edafa.web2sms.sms.model.Cacheable#getCacheDate()
	 */
	@Override
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

	@XmlTransient
	public String logAccountId() {
		return "Account(" + accountId + ") | ";
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}
	

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmitDetailedCampaignRequest [CampaignName=").append(CampaignName).append(", accountId=")
				.append(accountId).append(", password=").append(password).append(", secureHash=").append(secureHash)
				.append(", SMSText=").append(SMSText).append(", senderName=").append(senderName).append(", language=")
				.append(language).append(", msisdns=").append(msisdns).append(", ip=").append(ip).append(", trxId=")
				.append(trxId).append(", campaignId=").append(campaignId).append(", cacheDate=").append(cacheDate)
				.append(", validityMinutes=").append(validityMinutes).append(", retryCount=").append(retryCount)
                                .append(", retryExpiredCount=").append(retryExpiredCount)
				.append("]");
		return builder.toString();
	}

}
