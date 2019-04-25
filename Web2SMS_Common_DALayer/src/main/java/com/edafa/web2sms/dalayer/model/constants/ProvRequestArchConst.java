/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.ProvRequestArch;

/**
 * 
 * @author yyaseen
 */
public interface ProvRequestArchConst {
	String CLASS_NAME = ProvRequestArch.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";


	String FIND_BY_UPDATEDATE_AND_STATUS = PREFIX + "findByUpdateDateAndStatus";
	String FIND_BY_ACCOUNT_ID = PREFIX + "findByAccountId";

	String START_DATE = "startDate";
	String END_DATE = "endDate";
	String STATUS = "status";
	String COMPANY_ID = "companyId";
}
