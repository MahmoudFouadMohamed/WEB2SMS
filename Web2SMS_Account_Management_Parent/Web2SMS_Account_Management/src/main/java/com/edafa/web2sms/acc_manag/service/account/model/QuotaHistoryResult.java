package com.edafa.web2sms.acc_manag.service.account.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.QuotaHistoryModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

@XmlType(name = "QuotaHistoryResult", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/model")
public class QuotaHistoryResult extends ResultStatus {

	@XmlElement(name = "QuotaHistory", required = true, nillable = false)
	QuotaHistoryModel quotaHistoryModel;

	public QuotaHistoryResult() {
	}

	public QuotaHistoryResult(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);

	}
	
	public QuotaHistoryResult(ResponseStatus status) {
		super(status);

	}
	
	public QuotaHistoryModel getQuotaHistoryModel() {
		return quotaHistoryModel;
	}

	public void setQuotaHistoryModel(QuotaHistoryModel quotaHistoryModel) {
		this.quotaHistoryModel = quotaHistoryModel;
	}
}
