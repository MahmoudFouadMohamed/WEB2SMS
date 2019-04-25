package com.edafa.web2sms.service.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactListDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.IntraListInquiryDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListContactPK;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccManagUserModel;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.service.conversoin.ListConversionBean;
import com.edafa.web2sms.service.list.exception.ListNotFoundException;
import com.edafa.web2sms.service.list.interfaces.IntraListManagementBeanLocal;
import com.edafa.web2sms.service.list.interfaces.ListHandlerBeanLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.remote.AccountManegementRemotePoolsLocal;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.MsisdnFormat;
import com.edafa.web2sms.utils.sms.SMSUtils;
import com.edafa.web2sms.utils.sms.exception.InvalidAddressFormattingException;
import com.edafa.web2sms.utils.sms.exception.InvalidMSISDNFormatException;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * Session Bean implementation class IntraListManagementBean
 */
@Stateless
@LocalBean
public class IntraListManagementBean implements IntraListManagementBeanLocal {
	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger listLogger = LogManager.getLogger(LoggersEnum.LIST_MNGMT.name());

	@EJB
	private ContactListDaoLocal contactListDao;

	@EJB
	private ContactDaoLocal contactDao;

	@EJB
	private ListHandlerBeanLocal listHandler;

	@EJB
	private CampaignListsDaoLocal campaignListsDao;

	@EJB
	private ListTypeDaoLocal listTypeDao;

	@EJB
	ListConversionBean listConversionBean;
	
        @EJB
        AccountManegementFacingLocal accountManagement;

        @EJB
	AccountConversionFacingLocal accountConversion;

	@EJB
	CampaignManagementBeanLocal campaignManagementBean;

	@EJB
	IntraListInquiryDaoLocal intraListInquiryDao;

	/**
	 * Default constructor.
	 */
	public IntraListManagementBean() {
		// TODO Auto-generated constructor stub
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public int createIntraList(UserTrxInfo userTrxInfo) throws DBException {
		userTrxInfo.addUserAction(ActionName.CREATE_LIST);
		listLogger.info(userTrxInfo.logInfo() + "Creating account Intra List ");

		ContactList list = new ContactList();

		AccManagUserModel user = userTrxInfo.getAccManagUserModel();

		listLogger.trace(userTrxInfo.logId() + "list assigned Account ID :" + user.getAccountId());
		Account account = accountConversion.getAccount(user);
		list.setAccount(account);
		String listName = (String) Configs.DEFAULT_INTRA_LIST_NAME.getValue();
		list.setListName(listName);
		listLogger.trace(userTrxInfo.logId() + "list assigned List name :" + "IntraList");
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST);
		list.setListType(listType);
		listLogger.trace(userTrxInfo.logId() + "list assigned " + listType);
		String description = "list of internal employees of your company.";
		list.setDescription(description);
		listLogger.trace(userTrxInfo.logId() + "list assigned List Description : " + description);
		try {
			contactListDao.create(list);
			listLogger.debug(userTrxInfo.logId() + list.logId() + " created.");
			return list.getListId();
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "DBException while creating Intra list", e);
			throw e;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<Contact> fetchIntraListContacts(UserTrxInfo userTrxInfo, int listId) throws DBException,
			InterruptedException, ExecutionException, IntraListInquiryFailed, InvalidCustomerForQuotaInquiry {
		userTrxInfo.addUserAction(ActionName.REFRESH_LIST);

		UserModel user = userTrxInfo.getUser();
		List<Contact> newContactList = new ArrayList<Contact>();
		try {
			Account account = accountManagement.findAccountById(userTrxInfo.getAccountUserTrxInfo(), user.getAccountId());

			String billingMsisdn = account.getBillingMsisdn();
			// calling intra list stored procedure
			listLogger.info(userTrxInfo.logInfo() + "Inquiry for quota on billing MSISDN: (" + billingMsisdn + ")");
			String formattedMSISDN = SMSUtils.formatAddress(billingMsisdn, MsisdnFormat.NATIONAL);

			listLogger.debug(userTrxInfo.logId() + "Formatted billing MSISDN:" + formattedMSISDN);

			List<String> contactMSISDNList = intraListInquiryDao.inquireIntraList(formattedMSISDN);

			if (contactMSISDNList != null && !contactMSISDNList.isEmpty()) {
				for (String msisdn : contactMSISDNList) {
					Contact contact = new Contact();
					ListContactPK listContactPk = new ListContactPK();
					listContactPk.setListId(listId);
					if (SMSUtils.validateLocalAddress(msisdn)
							|| ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue() && SMSUtils
									.validateInternationalAddress(msisdn))) {
						listContactPk.setMsisdn(msisdn);
						contact.setListContactsPK(listContactPk);
						newContactList.add(contact);
					}
				}
				// eliminate the duplication from contact list.
				Set<Contact> newContacts = new HashSet<Contact>(newContactList);
				listLogger.debug(userTrxInfo.logId() + "List(" + listId + ") has " + newContacts.size()
						+ " contact(s) to be persisted");
				newContactList = new ArrayList<Contact>(newContacts);

				Future<Integer> confirmedCount = contactDao.create(newContactList);

				listLogger.debug(userTrxInfo.logId() + confirmedCount.get() + " Contact(s) persisted successfully in "
						+ "List(" + listId + ").");

				ContactList intraList = contactListDao.findByListId(listId);
				long contactNum = newContactList.size();
				intraList.setContactsCount(contactNum);
				contactListDao.edit(intraList);

				return newContactList;
			}

		} catch (IntraListInquiryFailed e) {
			listLogger.error(userTrxInfo.logId() + "Error while inquiring intra list ", e);
			throw e;
		} catch (InvalidCustomerForQuotaInquiry e) {
			listLogger.error(userTrxInfo.logId() + "Error, could't  inquire intra list for user=" + user.getAccountId()
					+ " and name=" + user.getUsername(), e);
			throw e;
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "DBException while creaing intra list in the dataBase ", e);
			throw e;
		} catch (InterruptedException e) {
			listLogger.error(userTrxInfo.logId() + "InterruptedException while creaing intra list in the dataBase ", e);
			throw e;
		} catch (ExecutionException e) {
			listLogger.error(userTrxInfo.logId() + "ExecutionException while creaing intra list in the dataBase ", e);
			throw e;
		} catch (AccountNotFoundException e) {
			listLogger.error(userTrxInfo.logId() + "Account not found ", e);
		} catch (InvalidMSISDNFormatException e) {
			listLogger.error(userTrxInfo.logId() + "Invalid MSISDN format ", e);
		} catch (InvalidAddressFormattingException e) {
			listLogger.error(userTrxInfo.logId() + "Invalid address format ", e);
		}
		return null;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	private void removeIntraLists(UserTrxInfo userTrxInfo) throws DBException {
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void emptyIntraList(UserTrxInfo userTrxInfo) throws DBException, InterruptedException, ExecutionException,
			ListNotFoundException {
		userTrxInfo.addUserAction(ActionName.REFRESH_LIST);
		listLogger.info(userTrxInfo.logInfo() + "Deleting all contacts from intralist");
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST);

		List<ContactList> intraLists;
		List<Contact> intraListContacts;

		listLogger.debug(userTrxInfo.logId() + "searching for IntraList.");
		intraLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger
				.debug(userTrxInfo.logId() + "found " + intraLists != null ? intraLists.size() : 0 + " intraList(s).");

		if (intraLists != null && !intraLists.isEmpty()) {
			listLogger.debug(userTrxInfo.logId()
					+ "Intra list exist,all contacts assigned to the list will be removed. ");
			ContactList intraList = intraLists.get(0);
			Future<Integer> confirmRemove = contactDao.removeByListId(intraList.getListId());
			int removedCount = confirmRemove.get();
			listLogger.debug(userTrxInfo.logId() + removedCount
					+ " Contact(s) removed successfully from the intra list.");
			intraList.setContactsCount(0L);
			contactListDao.edit(intraList);
		} else {
			throw new ListNotFoundException("Intra list");
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void removeIntraSubLists(UserTrxInfo userTrxInfo) throws DBException {
		userTrxInfo.addUserAction(ActionName.DELETE_LIST);
		listLogger.info(userTrxInfo.logInfo() + "Deleting all contacts from intra sub list");
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST);

		List<ContactList> intraSubLists;

		listLogger.debug(userTrxInfo.logId() + "searching for Intra sub Lists.");
		intraSubLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger.debug(userTrxInfo.logId() + "found " + intraSubLists.size() + " intraList.");

		for (ContactList contactList : intraSubLists) {
			contactListDao.remove(contactList);
		}
		listLogger.info(userTrxInfo.logId() + "(" + intraSubLists.size() + ") intra sub List(s) removed successfully.");

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void updateIntraSubLists(UserTrxInfo userTrxInfo, List<Contact> intraListContacts) throws DBException {
		userTrxInfo.addUserAction(ActionName.REFRESH_LIST);
		listLogger.info(userTrxInfo.logInfo() + "updating all contacts from intra sub list");
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST);

		List<ContactList> intraSubLists;

		listLogger.debug(userTrxInfo.logId() + "searching for Intra sub Lists.");
		intraSubLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger.debug(userTrxInfo.logId() + "found " + intraSubLists.size() + " intraList.");

		for (ContactList intraSubList : intraSubLists) {
			listLogger.debug(userTrxInfo.logId() + "updating " + intraSubList);

			List<Contact> intraSubListContacts = intraSubList.getListContacts();
			List<String> contactMSISDN = new ArrayList<String>();

			for (Contact intraContact : intraListContacts) {
				contactMSISDN.add(intraContact.getListContactsPK().getMsisdn());
			}

			Iterator<Contact> contactIter = intraSubListContacts.iterator();
			while (contactIter.hasNext()) {
				Contact contact = contactIter.next();
				if (!contactMSISDN.contains(contact.getListContactsPK().getMsisdn())) {
					contactIter.remove();
				}
			}
			listLogger.debug(userTrxInfo.logId() + intraSubListContacts.size() + " updated, persisting the changes");

			contactDao.removeByListId(intraSubList.getListId());
			contactDao.create(intraSubListContacts);
			intraSubList.setContactsCount((long) intraSubListContacts.size());
			contactListDao.edit(intraSubList);

			listLogger.debug(userTrxInfo.logId() + "Changes persisted successfully.");

		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Contact> validateIntraSubLists(UserTrxInfo userTrxInfo, List<Contact> intraSubListContacts)
			throws DBException {
		userTrxInfo.addUserAction(ActionName.CREATE_LIST);
		listLogger.info(userTrxInfo.logInfo() + "validating all contacts is a subset from intralist");
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST);

		List<ContactList> intraLists;

		listLogger.debug(userTrxInfo.logId() + "searching for Intra List.");
		intraLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger
				.debug(userTrxInfo.logId() + "found " + intraLists != null ? intraLists.size() : 0 + " intraList(s).");

		if (intraLists == null || intraLists.isEmpty()) {
			return null;
		}
		List<Contact> intraListContacts = intraLists.get(0).getListContacts();

		if (intraListContacts == null || intraListContacts.isEmpty()) {
			return null;
		}
		List<String> contactMSISDN = new ArrayList<String>();

		for (Contact intraContact : intraListContacts) {
			contactMSISDN.add(intraContact.getListContactsPK().getMsisdn());
		}

		Iterator<Contact> contactIter = intraSubListContacts.iterator();
		while (contactIter.hasNext()) {
			Contact contact = contactIter.next();
			if (!contactMSISDN.contains(contact.getListContactsPK().getMsisdn())) {
				contactIter.remove();
			}
		}
		listLogger.debug(userTrxInfo.logId() + "List of contacts has been verified.");
		return intraSubListContacts;
	}

	@Override
	public int validateIntraSubListsInDB(UserTrxInfo userTrxInfo, Integer newSubIntraListId) throws DBException {
		listLogger.info(userTrxInfo.logInfo() + "validating all contacts of presublist (" + newSubIntraListId + ") "
				+ "is a subset from intralist");
		UserModel user = userTrxInfo.getUser();

		listLogger.debug(userTrxInfo.logInfo() + " getting internal list associated with account id:("
				+ user.getAccountId() + ") with name: (" + Configs.DEFAULT_INTRA_LIST_NAME.getValue().toString() + ")");
		Integer intraListid = contactListDao.findByListNameAndAccountIdNativeSql(Configs.DEFAULT_INTRA_LIST_NAME
				.getValue().toString(), user.getAccountId());
		listLogger.debug(userTrxInfo.logInfo() + "find internal list for account id (" + user.getAccountId()
				+ " ) .. with id (" + intraListid + " )");
		listLogger.debug(userTrxInfo.logInfo() + "getting invalid contacts in new sub list: (" + newSubIntraListId
				+ " )");
		int numOfdeletedRows = contactDao.findDifferenceAndDelete(newSubIntraListId, intraListid);
		listLogger.debug(userTrxInfo.logInfo() + "found not valid Contacts : (" + numOfdeletedRows + ").");

		return numOfdeletedRows;

	}
}

