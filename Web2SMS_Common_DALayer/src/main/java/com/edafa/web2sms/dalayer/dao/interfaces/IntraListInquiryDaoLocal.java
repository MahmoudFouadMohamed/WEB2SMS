package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;


public interface IntraListInquiryDaoLocal {
	List<String> inquireIntraList(String billingMsisdn) throws InvalidCustomerForQuotaInquiry, IntraListInquiryFailed,
			DBException;

}
