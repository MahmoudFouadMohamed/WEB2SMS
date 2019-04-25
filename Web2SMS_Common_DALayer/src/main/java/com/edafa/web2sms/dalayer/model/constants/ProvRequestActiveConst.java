/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.ProvRequestActive;

/**
 * 
 * @author akhalifah
 */
public interface ProvRequestActiveConst {
	String CLASS_NAME = ProvRequestActive.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String UPDATE_REQUEST_STATUS = PREFIX + "updateStatus";
	String FIND_BY_COMP_ID_AND_STATUS = PREFIX + "findByCompanyIdAndStatus";
	String FIND_BY_COMP_ADMIN_AND_STATUSES = PREFIX + "findByCompanyAdminAndStatuses";
//	String FIND_BY_MSISDN_AND_STATUSES = PREFIX + "findByMSISDNAndStatuses";
	String FIND_BY_COMP_ID_AND_TYPE_AND_STATUS = PREFIX + "findByCompanyIdAndTypeAndStatus";
	String FIND_BY_COMP_ID_AND_TYPE_AND_STATUSES = PREFIX + "findByCompanyIdAndTypeAndStatuses";
	String FIND_BY_COMP_ID_TYPE_STATUS_AND_SENDER = PREFIX +"findByCompanyIdTypeStatusAndSender";
	String COUNT_BY_COMP_ID_AND_TYPE_AND_STATUS = PREFIX + "countByCompanyIdAndTypeAndStatus";
	String COUNT_BY_COMP_ID_AND_TYPE_AND_STATUSES = PREFIX + "countByCompanyIdAndTypeAndStatuses";
	String COUNT_BY_COMP_ID_TYPE_STATUS_AND_SENDER = PREFIX +"countByCompanyIdTypeStatusAndSender";

	String STATUS = "status";
	String REQUEST_ID = "requestId";
	String STATUES = "statuses";
	String COMPANY_ID = "companyId";
	String COMPANY_ADMIN = "companyAdmin";
	String TYPE = "type";
	String SENDER_NAME = "senderName";
//	String MSISDN = "msisdn";
}
