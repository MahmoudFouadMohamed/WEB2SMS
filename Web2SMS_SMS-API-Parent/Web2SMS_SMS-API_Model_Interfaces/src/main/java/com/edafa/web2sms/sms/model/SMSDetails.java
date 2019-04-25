package com.edafa.web2sms.sms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.smsgw.smshandler.sms.SMSId;

import java.util.Arrays;
import java.util.Set;

@XmlType(name = "SMSDetails", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class SMSDetails implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -638316466983964593L;

	@XmlElement(name = "SenderName")
	protected String senderName;
	@XmlElement(name = "ReceiverMSISDN")
	protected String receiverMSISDN;
	@XmlTransient
	protected String language;
	@XmlElement(name = "SMSText")
	protected String SMSText;
	@XmlTransient
	protected boolean deliveryReport;
	@XmlTransient
	protected String accountId;
	@XmlElement(name = "smsId", required=false)
	protected String smsId;
    @XmlElement
    protected boolean cachedSMS;
    @XmlTransient
    protected Set<String> rateLimitersIds;

	public SMSDetails() {
//		this.smsId = SMSId.getSMSId().getId();
	}

	public SMSDetails(String senderName, String receiverMSISDN, String SMSText, String accountId, String smsId) {
		this();

		this.senderName = senderName;
		this.receiverMSISDN = receiverMSISDN;
		this.SMSText = SMSText;
		this.accountId = accountId;
		this.smsId = smsId;
		language = null;

	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getReceiverMSISDN() {
		return receiverMSISDN;
	}

	public void setReceiverMSISDN(String receiverMSISDN) {
		this.receiverMSISDN = receiverMSISDN;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage() {

		String gsm7bitChars = "[]{}@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

		boolean engMsg = true;

		if (SMSText != null) {
			for (int i = 0; i < SMSText.length(); i++) {
				if (engMsg || SMSText.length() == 1) {
					if (gsm7bitChars.indexOf(SMSText.charAt(i)) == -1) {
						engMsg = false;
						break;
					}
				}
			}
		}
		if (engMsg) {
			language = "ENGLISH";
		} else {
			language = "ARABIC";
		}

	}

	public String setSMSIdPrefix(String SMSidPrefix) {
		this.smsId = SMSId.getSMSId().getId();
		this.smsId = this.smsId.substring(1);
		this.smsId = SMSidPrefix + this.smsId;
		return this.smsId;
	}

	public String getSMSText() {
		return SMSText;
	}

	public void setSMSText(String sMSText) {
		SMSText = sMSText;
	}

	public boolean isDeliveryReport() {
		return deliveryReport;
	}

	public void setDeliveryReport(boolean deliveryReport) {
		this.deliveryReport = deliveryReport;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getSMSId() {
		return smsId;
	}

	public void setSmsId(String smsId) {
		this.smsId = smsId;
	}

    public boolean isCachedSMS() {
        return cachedSMS;
    }

    public void setCachedSMS(boolean cachedSMS) {
        this.cachedSMS = cachedSMS;
    }
    public Set<String> getRateLimitersIds() {
        return rateLimitersIds;
    }

    public void setRateLimitersIds(Set<String> rateLimitersIds) {
        this.rateLimitersIds = rateLimitersIds;
    }               

   

    @Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SMSDetails [");
		if (senderName != null) {
			builder.append("senderName=");
			builder.append(senderName);
			builder.append(", ");
		}
		if (receiverMSISDN != null) {
			builder.append("receiverMSISDN=");
			builder.append(receiverMSISDN);
			builder.append(", ");
		}
		if (language != null) {
			builder.append("language=");
			builder.append(language);
			builder.append(", ");
		}
		if (SMSText != null) {
			builder.append("SMSText=");
			builder.append(SMSText);
			builder.append(", ");
		}
		builder.append("deliveryReport=");
		builder.append(deliveryReport);
		builder.append(", ");
		if (accountId != null) {
			builder.append("accountId=");
			builder.append(accountId);
			builder.append(", ");
		}
		if (smsId != null) {
			builder.append("smsId=");
			builder.append(smsId);
			builder.append(", ");
		}
		builder.append("cachedSMS=");
		builder.append(cachedSMS);
		builder.append(", ");
		if (rateLimitersIds != null) {
			builder.append("rateLimitersIds=");
			builder.append(rateLimitersIds);
		}
		builder.append("]");
		return builder.toString();
	}

	public String dumpAllData() {
        StringBuilder str = new StringBuilder("SMSDetails{senderName=");
        str = str.append(senderName)
                .append(", receiverMSISDN=")
                .append(receiverMSISDN)
                .append(", language=")
                .append(language)
                .append(", SMSText=")
                .append(SMSText)
                .append(", deliveryReport=")
                .append(deliveryReport)
                .append(", accountId=")
                .append(accountId)
                .append(", smsId=")
                .append(smsId)
                .append(", rateLimitersIds=")
                .append(rateLimitersIds)
                .append(", cachedSMS=")
                .append(cachedSMS)
                .append('}');

        return str.toString();
    }
}
