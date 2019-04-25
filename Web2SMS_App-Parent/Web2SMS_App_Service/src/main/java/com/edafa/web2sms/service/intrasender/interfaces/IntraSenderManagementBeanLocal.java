package com.edafa.web2sms.service.intrasender.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderAlreadyExistException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderNotFoundException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderShouldHaveAcctListException;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

@Local
public interface IntraSenderManagementBeanLocal {

	void createIntraSender(AdminTrxInfo adminTrxInfo, IntraSender intraSenderObj) throws InvalidSMSSender, DBException, IntraSenderAlreadyExistException;


//	void createIntraSenderForAccount(AdminTrxInfo adminTrxInfo, String intrasender, Account account)
//			throws InvalidSMSSender, DBException, IntraSenderAlreadyExistException;

//	List<IntraSenders> findIntraSenderByAccountId(AdminTrxInfo trxInfo, String accountId) throws DBException;

	List<IntraSender> findIntraSender(AdminTrxInfo trxInfo, int frist, int max) throws DBException;

	int countIntraSendersNames(AdminTrxInfo trxInfo) throws DBException;

	void deleteIntraSender(AdminTrxInfo trxInfo, String id) throws DBException, IntraSenderNotFoundException;

	IntraSender findIntraSenderById(TrxInfo trxInfo, String intraId) throws DBException;

	List<IntraSender> findSystemIntraSender(AdminTrxInfo trxInfo) throws DBException;


	void editIntraSender(AdminTrxInfo trxInfo, IntraSender newIntraSender) throws InvalidSMSSender,
			IntraSenderAlreadyExistException, IntraSenderNotFoundException, DBException, IntraSenderShouldHaveAcctListException;
}
