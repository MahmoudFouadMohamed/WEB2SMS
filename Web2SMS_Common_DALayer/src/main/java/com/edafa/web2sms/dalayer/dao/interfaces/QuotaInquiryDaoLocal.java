package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;

@Local
public interface QuotaInquiryDaoLocal {

	QuotaInfo inquireQuota(String billingMsisdn) throws InvalidCustomerForQuotaInquiry, QuotaInquiryFailed, DBException;
}
