package com.edafa.web2sms.sms.model;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SubmitCampaignRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "SubmitCampaignRequest", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class SubmitCampaignRequest implements Serializable {

	private static final long serialVersionUID = 6849346883074975432L;

	@XmlElement(name = "CampaignName")
	protected String CampaignName;
	@XmlElement(name = "AccountId")
	protected String accountId;
	@XmlElement(name = "Password")
	protected String password;
	@XmlElement(name = "SecureHash")
	protected String secureHash;
	@XmlElement(name = "SMSText")
	protected String SMSText;
	@XmlElement(name = "SenderName")
	protected String senderName;
	@XmlTransient
	protected String language;
	@XmlElement(name = "MSISDNsList")
	protected CampaignRecieverDetails msisdns;

	
	@XmlTransient
	boolean plainToString = false;
	
	
	public boolean isPlainToString() {
		return plainToString;
	}

	public void setPlainToString(boolean plainToString) {
		this.plainToString = plainToString;
	}

	public SubmitCampaignRequest() {
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSecureHash() {
		return secureHash;
	}

	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}

	public String getLanguage() {
		return language;
	}

	public CampaignRecieverDetails getMsisdns() {
		return msisdns;
	}

	public void setMsisdns(CampaignRecieverDetails msisdns) {
		this.msisdns = msisdns;
	}

	public String getSMSText() {
		return SMSText;
	}

	public void setSMSText(String sMSText) {
		SMSText = sMSText;
	}

	public String getCampaignName() {
		return CampaignName;
	}

	public void setCampaignName(String campaignName) {
		CampaignName = campaignName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String setLanguage() {

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
		return language;
	}

	@XmlTransient
	public String logId() {
		return "Account(" + accountId + ") | ";
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SubmitCampaignRequest [");
		if (CampaignName != null) {
			builder.append("CampaignName=");
			builder.append(CampaignName);
			builder.append(", ");
		}
		if (accountId != null) {
			builder.append("accountId=");
			builder.append(accountId);
			builder.append(", ");
		}
		if (password != null) {
			builder.append("password=");
			builder.append(password);
			builder.append(", ");
		}
		if (secureHash != null) {
			builder.append("secureHash=");
			builder.append(secureHash);
			builder.append(", ");
		}
		if (SMSText != null) {
			builder.append("SMSText=");
			builder.append(SMSText);
			builder.append(", ");
		}
		if (senderName != null) {
			builder.append("senderName=");
			builder.append(senderName);
			builder.append(", ");
		}
		if (language != null) {
			builder.append("language=");
			builder.append(language);
			builder.append(", ");
		}
		if (msisdns != null) {
			builder.append("msisdns=");
			builder.append(msisdns);
			builder.append(", ");
		}
		builder.append("plainToString=");
		builder.append(plainToString);
		
		builder.append("]");
		if(!plainToString)
			return " ";
		else 
			return builder.toString();
	}

	
	

}
