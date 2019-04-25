package com.edafa.web2sms.service.list.interfaces;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.ejb.Local;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.xml.sax.SAXException;

import com.bea.common.security.xacml.InvalidAttributeException;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.IntraListInquiryFailed;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.list.exception.ContactNotFoundException;
import com.edafa.web2sms.service.list.exception.DuplicateContactException;
import com.edafa.web2sms.service.list.exception.DuplicateListNameException;
import com.edafa.web2sms.service.list.exception.EmptyConatctListException;
import com.edafa.web2sms.service.list.exception.InvalidFileException;
import com.edafa.web2sms.service.list.exception.InvalidRequestException;
import com.edafa.web2sms.service.list.exception.ListNotFoundException;
import com.edafa.web2sms.service.list.exception.ListTypeExpansionException;
import com.edafa.web2sms.service.list.exception.ListTypeNotFoundException;
import com.edafa.web2sms.service.list.exception.LockedListException;
import com.edafa.web2sms.service.list.files.FileDetails;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactListModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.FileValidation;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.model.UserTrxInfo;

@Local
public interface ListManegementBeanLocal {

	void copyToNewList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsList, int oldListId)
			throws IneligibleAccountException, DuplicateListNameException, DBException, InvalidRequestException, ListTypeNotFoundException;

	Integer countContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeName) throws DBException, IneligibleAccountException;

	List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, int first, int max,
			List<ListTypeName> listTypeName) throws DBException, IneligibleAccountException;

	ContactList createTempList(TrxInfo TrxInfo, Account acct, List<ContactModel> contacts) throws DBException;

	void createNewList(UserTrxInfo userTrxInfo, ContactListModel contactsList) throws IneligibleAccountException,
			DuplicateListNameException, DBException, EmptyConatctListException, ListTypeNotFoundException;
	

	FileValidation createNewList(UserTrxInfo userTrxInfo, FileDetails fileDeitals) throws IneligibleAccountException,
			DuplicateListNameException, IOException, InvalidFileException, DBException, InvalidFileException,
			OpenXML4JException, SAXException;

	public Integer countContactsInLists(TrxInfo trxInfo, List<Integer> lists) throws DBException;

	List<ContactListInfoModel> searchLists(UserTrxInfo userTrxInfo, String listName, List<ListTypeName> listType) throws IneligibleAccountException,
			DBException, ListNotFoundException;

	void expandContactList(UserTrxInfo userTrxInfo, ContactListModel contactsList) throws IneligibleAccountException,
			DuplicateContactException, LockedListException, ListNotFoundException, DBException,
			InvalidAttributeException, ListTypeNotFoundException, ListTypeExpansionException;

	void deleteSubContactList(UserTrxInfo userTrxInfo, ContactListModel contactsList)
			throws IneligibleAccountException, LockedListException, ListNotFoundException, DBException;

	void deleteContactList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsListInfoModel)
			throws LockedListException, ListNotFoundException, IneligibleAccountException, DBException;

	public FileDetails exportListToFile(UserTrxInfo userTrxInfo, Integer listId) throws IneligibleAccountException,
			DBException;

	public String exportListToCsvFile(UserTrxInfo userTrxInfo, Integer listId) throws IneligibleAccountException,
			DBException, IOException;

	public List<ContactModel> searchContacts(UserTrxInfo userTrxInfo, String contact, Integer listId)
			throws IneligibleAccountException, DBException;

	List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, String campId) throws DBException,
			IneligibleAccountException, CampaignNotFoundException;

	List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames)
			throws DBException, IneligibleAccountException;

	void editContacts(UserTrxInfo userTrxInfo, ContactModel contact, String msisdn, Integer listId)
			throws IneligibleAccountException, DBException, ContactNotFoundException;

	void editContactListName(UserTrxInfo userTrxInfo, Integer listId, String newListName)
			throws IneligibleAccountException, DBException, ListNotFoundException, LockedListException,
			DuplicateListNameException;

	List<ContactModel> getContactList(UserTrxInfo userTrxInfo, int listId) throws DBException,
			IneligibleAccountException;

	void handleVirtualList(UserTrxInfo userTrxInfo, List<ContactModel> contactModels) throws DBException,
			InvalidAttributeException, IneligibleAccountException, DuplicateContactException, LockedListException,
			ListNotFoundException, EmptyConatctListException, ListTypeNotFoundException, ListTypeExpansionException;

	void handleIntraList(UserTrxInfo userTrxInfo) throws DBException, InterruptedException, ExecutionException,
			ListNotFoundException, IntraListInquiryFailed, InvalidCustomerForQuotaInquiry;

	List<ContactModel> getContactList(UserTrxInfo userTrxInfo, int listId, int first, int max) throws DBException,
			IneligibleAccountException;

	List<ContactList> getContactLists(UserTrxInfo userTrxInfo, String campaignId, List<ListType> listTypes,
			boolean withCounts) throws DBException;

	List<Contact> getContacts(UserTrxInfo userTrxInfo, int listId) throws DBException, IneligibleAccountException;

	Map<Integer, List<ContactModel>> createResentCampList(UserTrxInfo userTrxInfo, List<Contact> contacts) throws DBException;


}
