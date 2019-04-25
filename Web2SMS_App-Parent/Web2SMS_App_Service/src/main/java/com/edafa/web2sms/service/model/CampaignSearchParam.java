package com.edafa.web2sms.service.model;

import java.util.Date;
import java.util.List;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;

public class CampaignSearchParam {

	Date dateFrom, dateTo;
	String accountId, companyName, billingMsisdn, senderName, userName;
	List<CampaignStatusName> statuses;

	public CampaignSearchParam(Date datefrom, Date dateTo, String accountId, String companyName, String billingMsisdn, String senderName,
			String userName, List<CampaignStatusName> statuses) {
		this.dateFrom = datefrom;
		this.dateTo = dateTo;
		if (accountId == null || accountId.equals(""))
			this.accountId = null;
		else
			this.accountId = accountId;
		if (companyName == null || companyName.equals(""))
			this.companyName = null;
		else
			this.companyName = companyName;
		if (billingMsisdn == null || billingMsisdn.equals(""))
			this.billingMsisdn = null;
		else
			this.billingMsisdn = billingMsisdn;
		this.statuses = statuses;
		if (senderName == null || senderName.equals(""))
			this.senderName = null;
		else
			this.senderName = senderName;
		if (userName == null || userName.equals(""))
			this.userName = null;
		else
			this.userName = userName;

	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(Date from) {
		this.dateFrom = from;
	}

	public Date getDateTo() {
		return dateTo;
	}

	public void setDateTo(Date dateTo) {
		this.dateTo = dateTo;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getCompanyName() {
		return companyName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBillingMsisdn() {
		return billingMsisdn;
	}

	public void setBillingMsisdn(String billingMsisdn) {
		this.billingMsisdn = billingMsisdn;
	}

	public List<CampaignStatusName> getStatuses() {
		return statuses;
	}

	public void setStatuses(List<CampaignStatusName> statuses) {
		this.statuses = statuses;
	}

	@Override
	public String toString() {
		String str = "Campaign search parameters are:[";

		if (dateFrom != null) {
			str += "date from:";
			str += dateFrom;
		}

		if (dateTo != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", date to: ";
			} else {
				str += "date to:";
			}
			str += dateTo;
		}

		if (accountId != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", account ID: ";
			} else {
				str += "account ID:";
			}
			str += accountId;
		}

		if (companyName != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", company name: ";
			} else {
				str += "company name: ";
			}
			str += companyName;
		}

		if (billingMsisdn != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", billing MSISDN: ";
			} else {
				str += "billing MSISDN: ";
			}
			str += billingMsisdn;
		}

		if (senderName != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", sender name: ";
			} else {
				str += "sender name: ";
			}
			str += senderName;
		}

		if (userName != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", user name: ";
			} else {
				str += "user name: ";
			}
			str += userName;
		}

		if (statuses != null) {
			if (str.charAt(str.length()-1) != '[') {
				str += ", statuses: ";
			} else {
				str += "statuses: (";
			}
			int count = 1;
			for (CampaignStatusName csm : statuses) {
				str += csm.getName();
				if (statuses.size() > count) {
					str += ", ";
				}
				count++;
			}
			str += ")";
		}
		str += "].";

		return str.toString();
	}

}
