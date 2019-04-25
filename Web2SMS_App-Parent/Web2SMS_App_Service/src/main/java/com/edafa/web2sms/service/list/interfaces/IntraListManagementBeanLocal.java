package com.edafa.web2sms.service.list.interfaces;

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.service.list.exception.ListNotFoundException;
import com.edafa.web2sms.service.model.UserTrxInfo;

@Local
public interface IntraListManagementBeanLocal {

	int createIntraList(UserTrxInfo userTrxInfo) throws DBException;

	void removeIntraSubLists(UserTrxInfo userTrxInfo) throws DBException;

	void updateIntraSubLists(UserTrxInfo userTrxInfo, List<Contact> intraListContacts) throws DBException;

	List<Contact> fetchIntraListContacts(UserTrxInfo userTrxInfo, int listId) throws DBException, InterruptedException,
			ExecutionException, IntraListInquiryFailed, InvalidCustomerForQuotaInquiry;

	void emptyIntraList(UserTrxInfo userTrxInfo) throws DBException, InterruptedException, ExecutionException, ListNotFoundException;

	List<Contact> validateIntraSubLists(UserTrxInfo userTrxInfo, List<Contact> intraListContacts) throws DBException;

	int validateIntraSubListsInDB(UserTrxInfo userTrxInfo, Integer newSubIntraListId) throws DBException;


}
