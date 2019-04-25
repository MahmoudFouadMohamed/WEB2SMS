package com.edafa.web2sms.acc_manag.service.account.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;

@XmlType(name = "QuotaInquiryResult", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/model")
public class QuotaInquiryResult extends ResultStatus {

	@XmlElement(name = "QuotaInfo", required = true, nillable = false)
	QuotaInfo quotaInfo;

	public QuotaInquiryResult() {
	}

	public QuotaInquiryResult(ResponseStatus status, String errorMessage) {
		super(status, errorMessage);
	}

	public QuotaInquiryResult(ResponseStatus status) {
		super(status);
	}

	public QuotaInfo getQuotaInfo() {
		return quotaInfo;
	}

	public void setQuotaInfo(QuotaInfo quotaInfo) {
		this.quotaInfo = quotaInfo;
	}

}
