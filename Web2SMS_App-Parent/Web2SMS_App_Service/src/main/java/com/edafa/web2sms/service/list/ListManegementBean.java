package com.edafa.web2sms.service.list;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.model.StylesTable;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.bea.common.security.xacml.InvalidAttributeException;
import com.edafa.csv.CSVFileValidationResult;
import com.edafa.csv.batchfile.CSVBatchFile;
import com.edafa.csv.record.CSVRecord;
import com.edafa.utils.timing.StopWatch;
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
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListContactPK;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.service.conversoin.ListConversionBean;
import com.edafa.web2sms.service.enums.FileType;
import com.edafa.web2sms.service.list.excel.XLSXSheethandler;
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
import com.edafa.web2sms.service.list.interfaces.IntraListManagementBeanLocal;
import com.edafa.web2sms.service.list.interfaces.ListHandlerBeanLocal;
import com.edafa.web2sms.service.list.interfaces.ListManegementBeanLocal;
import com.edafa.web2sms.service.model.ContactCSVRecord;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactListModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.FileValidation;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.FileNameUtils;
import com.edafa.web2sms.utils.configs.enums.AppSettings;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.SMSUtils;

import ezvcard.VCard;
import ezvcard.io.text.VCardReader;
import ezvcard.property.Telephone;

//	=================================================

/**
 * Session Bean implementation class ListServiceBean
 */
@Stateless
public class ListManegementBean implements ListManegementBeanLocal {

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger listLogger = LogManager.getLogger(LoggersEnum.LIST_MNGMT.name());
	int maxChar = (int) Configs.MAX_CONTACT_NAME_CHAR.getValue();

	@EJB
	private ContactListDaoLocal contactListDao;

	@EJB
	private IntraListManagementBeanLocal intraListManagementBean;

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
	ListManegementBeanLocal listManegementBean;

	@EJB
	AccountManegementFacingLocal accountManagement;

	@EJB
	AccountConversionFacingLocal accountConversion;

	@EJB
	CampaignManagementBeanLocal campaignManagementBean;

	@EJB
	IntraListInquiryDaoLocal intraListInquiryDao;

	// Constructor---------------------------------------------------------

	/**
	 * Default constructor.
	 */
	public ListManegementBean() {

	}

	// === Copying contacts to new List ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void copyToNewList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsList, int oldListId)
			throws IneligibleAccountException, DuplicateListNameException, DBException, InvalidRequestException,
			ListTypeNotFoundException {

		userTrxInfo.addUserAction(ActionName.CREATE_LIST);
		listLogger.info(userTrxInfo.logInfo() + "list parameters are: " + contactsList);

		UserModel user = userTrxInfo.getUser();

		// create list in contactList table
		ContactList list = new ContactList();

		// catch DBException
		try {
			listLogger.debug(userTrxInfo.logId() + "checking account eligibility to: " + userTrxInfo.getUserActions());
			// checker throw IneligibleAccountException if account is
			// IneligibleAccount otherwise do nothing.
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + "account is eligible");

			// check list name uniqueness
			listLogger.debug(userTrxInfo.logId() + "checking list name uniqueness....");
			ContactList originalList = contactListDao.findByListNameAndAccountId(contactsList.getListName(),
					user.getAccountId());
			if (originalList == null) {
				listLogger.debug(userTrxInfo.logId() + "list name is unique... Creating list in process.");
				list.setAccount(accountManagement.getAccount(user.getAccountId()));
				listLogger.trace(userTrxInfo.logId() + "list assigned Account ID :" + user.getAccountId());
				list.setListName(contactsList.getListName());
				listLogger.trace(userTrxInfo.logId() + "list assigned List name :" + contactsList.getListName());
				ListTypeName listTypeName = contactsList.getListType();
				if (listTypeName == null) {
					throw new ListTypeNotFoundException();
				}
				ListType listType = listTypeDao.getCachedObjectByName(listTypeName);
				list.setListType(listType);
				listLogger.trace(userTrxInfo.logId() + "list assigned " + listType);
				list.setDescription(contactsList.getDescription());
				listLogger.trace(userTrxInfo.logId() + "list assigned List Description :"
						+ contactsList.getDescription());
				contactListDao.createUsingNewTx(list);
				listLogger.debug(userTrxInfo.logId() + list.logId() + " created.");

				// result show how many rows inserted into database.
				int persistedContsCount = contactDao.copyList(list.getListId(), oldListId);

				if (persistedContsCount != 0) {
					listLogger
							.debug(userTrxInfo.logId() + list.logId() + persistedContsCount + " contacts are copied.");
					list.setContactsCount((long) persistedContsCount);
					contactListDao.edit(list);
					listLogger.info(userTrxInfo.logId() + "Finished from copying contactlist[" + oldListId
							+ "] successfully to new " + list.toString());
				} else {
					listLogger.info(userTrxInfo.logId() + "contactlist[" + oldListId + "] is EMPTY!!!, "
							+ list.toString() + " will be deleted");
					contactListDao.remove(list);
					listLogger.debug(userTrxInfo.logId() + "Empty list deleted successfully");
					throw new InvalidRequestException("Empty list selected");
				}
			} else {
				listLogger.error(userTrxInfo.logId() + "duplicate list name: " + contactsList.getListName());
				throw new DuplicateListNameException(contactsList.getListName());
			}

		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
			contactListDao.remove(list);
			listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
			throw e;
		}

	}

	/*
	 * // === Blank List creation request ===
	 * 
	 * @Override
	 * 
	 * @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) public void
	 * createNewList(UserTrxInfo usertxInfo, ContactListModel contactsList)
	 * throws IneligibleAccountException, DuplicateListNameException,
	 * DBException, EmptyConatctListException {
	 * 
	 * usertxInfo.addUserAction(UserActionName.CREATE_LIST);
	 * listLogger.info(usertxInfo.logInfo() + "list parameters are: " +
	 * contactsList);
	 * 
	 * AccManagUserModel user = usertxInfo.getUser(); if (contactsList.getListContacts()
	 * != null && contactsList.getListContacts().size() != 0) { try {
	 * listLogger.debug(usertxInfo.logId() + "checking account eligibility to: "
	 * + usertxInfo.getUserAction()); // checker throw
	 * IneligibleAccountException if account is // IneligibleAccount otherwise
	 * do nothing. accountManagement.checkAccountAndUserEligibility(usertxInfo);
	 * listLogger.debug(usertxInfo.logId() + "account is eligible");
	 * 
	 * // check list name uniqueness listLogger.debug(usertxInfo.logId() +
	 * "checking list name uniqueness..."); ContactList originalList =
	 * contactListDao.findByListNameAndAccountId(contactsList.getListInfo()
	 * .getListName(), user.getAccountId()); if (originalList == null) {
	 * listLogger.debug(usertxInfo.logId() +
	 * "list name is unique... Creating list in process."); // create list in
	 * contactList table ContactList list = new ContactList();
	 * list.setAccount(accountManagement.getAccount(user.getAccountId()));
	 * listLogger.trace(usertxInfo.logId() + "list assigned Account ID :" +
	 * user.getAccountId());
	 * list.setListName(contactsList.getListInfo().getListName());
	 * listLogger.trace(usertxInfo.logId() + "list assigned List name :" +
	 * contactsList.getListInfo().getListName()); ListType listType =
	 * listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST);
	 * list.setListType(listType); listLogger.trace(usertxInfo.logId() +
	 * "list assigned " + listType);
	 * list.setDescription(contactsList.getListInfo().getDescription());
	 * listLogger.trace(usertxInfo.logId() + "list assigned List Description :"
	 * + contactsList.getListInfo().getDescription());
	 * contactListDao.create(list); listLogger.debug(usertxInfo.logId() +
	 * list.logId() + " created."); // create contacts after eliminating the
	 * duplication // listConversionBean.getContacts extract contacts from //
	 * contactModel Set<Contact> newContacts = new
	 * HashSet<Contact>(listConversionBean.getContacts(list.getListId(),
	 * contactsList.getListContacts())); listLogger.debug(usertxInfo.logId() +
	 * list.logId() + " has " + newContacts.size() +
	 * " contacts will be persisted"); if (newContacts != null &&
	 * newContacts.size() != 0) { for (Contact entity : newContacts) {
	 * contactDao.create(entity); } listLogger.debug(usertxInfo.logId() +
	 * "update list contact(s) count(" + newContacts.size() + ").");
	 * list.setContactsCount((long) newContacts.size());
	 * contactListDao.edit(list); listLogger.debug(usertxInfo.logId() +
	 * "Contacts persisted successfully in " + list.logId()); } else { throw new
	 * EmptyConatctListException("No vaild contacts found"); } } else {
	 * listLogger.error(usertxInfo.logId() + "duplicate list name: " +
	 * contactsList.getListInfo().getListName()); throw new
	 * DuplicateListNameException(contactsList.getListInfo().getListName()); } }
	 * catch (DBException e) { listLogger.error(usertxInfo.logId() +
	 * "failed to persist list", e); throw e; } catch
	 * (IneligibleAccountException e) { listLogger.error(usertxInfo.logId() +
	 * e.getMessage()); throw e; } } else { throw new
	 * EmptyConatctListException("At least you should add one contact to the list."
	 * ); } listLogger.info(usertxInfo.logId() +
	 * "Finished from creating new list successfully "); }
	 */

	// === Blank List creation request ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createNewList(UserTrxInfo usertxInfo, ContactListModel contactsList) throws IneligibleAccountException,
			DuplicateListNameException, DBException, EmptyConatctListException, ListTypeNotFoundException {

		usertxInfo.addUserAction(ActionName.CREATE_LIST);
		listLogger.info(usertxInfo.logInfo() + "list parameters are: " + contactsList);

		UserModel user = usertxInfo.getUser();
		if (contactsList.getListContacts() != null && contactsList.getListContacts().size() != 0) {
			try {
				listLogger.debug(usertxInfo.logId() + "checking account eligibility to: " + usertxInfo.getUserActions());
				// checker throw IneligibleAccountException if account is
				// IneligibleAccount otherwise do nothing.
				accountManagement.checkAccountAndUserEligibility(usertxInfo.getAccountUserTrxInfo());
				listLogger.debug(usertxInfo.logId() + "account is eligible");

				// check list name uniqueness
				listLogger.debug(usertxInfo.logId() + "checking list name uniqueness...");
				ContactList originalList = contactListDao.findByListNameAndAccountId(contactsList.getListInfo()
						.getListName(), user.getAccountId());
				ListTypeName listTypeName = contactsList.getListInfo().getListType();
//				System.out.println(listTypeName.name());
				if (originalList == null && listTypeName != null) {
					listLogger.debug(usertxInfo.logId() + "list name is unique... Creating list in process.");
					// create list in contactList table
					ContactList list = new ContactList();
					list.setAccount(accountManagement.getAccount(user.getAccountId()));
					listLogger.trace(usertxInfo.logId() + "list assigned Account ID :" + user.getAccountId());
					list.setListName(contactsList.getListInfo().getListName());
					listLogger.trace(usertxInfo.logId() + "list assigned List name :"
							+ contactsList.getListInfo().getListName());
					ListType listType = null;
					// System.out.println("cashed: "+
					// listTypeDao.getCachedObjectByName(ListTypeName.PRE_INTRA_SUB_LIST));

					switch (listTypeName) {
					case INTRA_LIST:
					case INTRA_SUB_LIST: {
						listType = listTypeDao.getCachedObjectByName(ListTypeName.PRE_INTRA_SUB_LIST);
						break;
					}
					case NORMAL_LIST: {
						listType = listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST);
						break;
					}
					case CUSTOMIZED_LIST: {
						listType = listTypeDao.getCachedObjectByName(ListTypeName.CUSTOMIZED_LIST);
						break;
					}

					case PRENORMAL_LIST:
						break;
					case TEMP_LIST:
						break;
					case UNKNOWN:
						break;
					case VIRTUAL_LIST:
						break;
					default:
						break;

					}
					list.setListType(listType);
					listLogger.debug(usertxInfo.logId() + "list assigned " + listType);
					list.setDescription(contactsList.getListInfo().getDescription());
					listLogger.trace(usertxInfo.logId() + "list assigned List Description :"
							+ contactsList.getListInfo().getDescription());
					contactListDao.create(list);
					listLogger.debug(usertxInfo.logId() + list.logId() + " created.");
					// create contacts after eliminating the duplication
					// listConversionBean.getContacts extract contacts from
					// contactModel
					Set<Contact> newContacts = new HashSet<Contact>(listConversionBean.getContacts(list.getListId(),
							contactsList.getListContacts()));
					// TODO
					// create list with type pre intra sub list, presist
					// contacts, then call validate
					// and remove the return contacts from created sub list then
					// change type of it to intra sub.
					// if (listTypeName.equals(ListTypeName.INTRA_SUB_LIST)){
					// newContacts = new
					// HashSet<Contact>(intraListManagementBean.validateIntraSubLists(usertxInfo,
					// new ArrayList<Contact>(newContacts)));
					//
					// }
					listLogger.debug(usertxInfo.logId() + list.logId() + " has " + newContacts.size()
							+ " contacts will be persisted");
					if (newContacts != null && newContacts.size() != 0) {
						for (Contact entity : newContacts) {
							contactDao.create(entity);
						}
						listLogger.debug(usertxInfo.logId() + "update list contact(s) count(" + newContacts.size()
								+ ").");
						list.setContactsCount((long) newContacts.size());
						contactListDao.edit(list);
						listLogger.debug(usertxInfo.logId() + "Contacts persisted successfully in " + list.logId());
					} else {
						throw new EmptyConatctListException("No vaild contacts found");
					}
//					System.out.println("test : "
//							+ list.getListType().getListTypeName().equals(ListTypeName.PRE_INTRA_SUB_LIST));
					if (list.getListType().getListTypeName().equals(ListTypeName.PRE_INTRA_SUB_LIST)) {
						listLogger.debug(usertxInfo.logInfo() + "Validating preIntraSub List contacts with id:("
								+ list.getListId() + ").");

						int numOfdeletedRows = intraListManagementBean.validateIntraSubListsInDB(usertxInfo,
								list.getListId());

						listLogger.debug(usertxInfo.logInfo() + "deletion of (" + numOfdeletedRows
								+ ") not valid contacts done successfully.");
						list.setContactsCount(list.getContactsCount() - numOfdeletedRows);
						listLogger.debug(usertxInfo.logInfo() + "no invalid contacts in sublist");
						list.setListType(listTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST));
						contactListDao.edit(list);
						listLogger.debug(usertxInfo.logInfo() + "list converted to intra sublist successfully.");
					}

				} else if (originalList != null) {
					listLogger.error(usertxInfo.logId() + "duplicate list name: "
							+ contactsList.getListInfo().getListName());
					throw new DuplicateListNameException(contactsList.getListInfo().getListName());
				} else {
					listLogger.error(usertxInfo.logId() + "list type not defined: "
							+ contactsList.getListInfo().getListName());
					throw new ListTypeNotFoundException();
				}
			} catch (DBException e) {
				listLogger.error(usertxInfo.logId() + "failed to persist list", e);
				throw e;
			} catch (IneligibleAccountException e) {
				listLogger.error(usertxInfo.logId() + e.getMessage());
				throw e;
			}
		} else {
			throw new EmptyConatctListException("At least you should add one contact to the list.");
		}

		listLogger.info(usertxInfo.logId() + "Finished from creating new list successfully ");
	}

	// === Create New List From File Request ====
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public FileValidation createNewList(UserTrxInfo userTrxInfo, FileDetails fileDetails)
			throws IneligibleAccountException, IOException, InvalidFileException, DuplicateListNameException,
			DBException {

		userTrxInfo.addUserAction(ActionName.CREATE_LIST);
		FileValidation result = new FileValidation();
		listLogger.info(userTrxInfo.logInfo() + "file parameters are: " + fileDetails);
		UserModel user = userTrxInfo.getUser();
		ContactList list = new ContactList();
		// catch DBException
		try {
			// Check account Eligibility
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible.. and checking list name uniqueness... ");
			// check list name uniqueness
			ContactList originalList = contactListDao.findByListNameAndAccountId(fileDetails.getListName(),
					user.getAccountId());
			if (originalList == null) {
				listLogger.debug(userTrxInfo.logId() + "list name is unique.");
				// create list in contactList table
				list = new ContactList();
				list.setAccount(accountManagement.getAccount(user.getAccountId()));
				listLogger.trace(userTrxInfo.logId() + "list assigned Account ID :" + user.getAccountId());
				list.setListName(fileDetails.getListName());
				listLogger.trace(userTrxInfo.logId() + "list assigned List name :" + fileDetails.getListName());

				ListType listType;
				if (fileDetails.getValue1ColNumber() == -1 && fileDetails.getValue2ColNumber() == -1
						&& fileDetails.getValue3ColNumber() == -1 && fileDetails.getValue4ColNumber() == -1
						&& fileDetails.getValue5ColNumber() == -1) {
					listType = listTypeDao.getCachedObjectByName(ListTypeName.PRENORMAL_LIST);
				} else {
					listType = listTypeDao.getCachedObjectByName(ListTypeName.PRECUSTOMIZED_LIST);
				}

				listLogger.trace(userTrxInfo.logId() + "list assigned List Type :" + listType);
				list.setListType(listType);
				listLogger.trace(userTrxInfo.logId() + "list assigned " + listType);
				list.setDescription(fileDetails.getListDescription());
				listLogger.trace(userTrxInfo.logId() + "list assigned List Description :"
						+ fileDetails.getListDescription());

				// new transaction to use this ListID in creating the contacts
				listLogger.debug(userTrxInfo.logId() + "Creating " + list.logId() + "using new Tx. ");
				contactListDao.createUsingNewTx(list);
				listLogger.debug(userTrxInfo.logId() + list.logId() + " created. ");
				FileType receivedFile = null;
				/*
				 * Handle file type
				 * " fetching contacts from file according to file type "
				 */
				receivedFile = FileType.valueOf(fileDetails.getFileType());
				listLogger.debug(userTrxInfo.logId() + "file format is: " + receivedFile.toString());
				switch (receivedFile) {
				case CSV: {
					try {
						listLogger.debug(userTrxInfo.logId() + " parsing CSV file ...");
						result = parseCsvContacts(userTrxInfo, fileDetails, list.getListId());
						listLogger.debug(userTrxInfo.logId() + " parsing CSV file  finished. successfully");
						break;
					} catch (DBException e) {
						listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (IOException e) {
						listLogger.error(userTrxInfo.logId() + "failed to read/write file", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (InvalidFileException e) {
						listLogger.error(userTrxInfo.logId() + "Invalid File .. " + e.getMessage());
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					}
				}
				case XLS:
				case XLSX: {
					try {
						listLogger.debug(userTrxInfo.logId() + " parsing Excel file ...");
						result = parseExcelContacts(userTrxInfo, fileDetails, list.getListId());
						listLogger.debug(userTrxInfo.logId() + " parsing Excel file  finished. successfully");
					} catch (DBException e) {
						listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (IOException e) {
						listLogger.error(userTrxInfo.logId() + "failed to read/write file", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (InvalidFileException e) {
						listLogger.error(userTrxInfo.logId() + "Invalid File .. " + e.getMessage());
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (Exception e) {
						listLogger.error(userTrxInfo.logId() + "Exception while parsing excel file: ", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					}
					break;
				}
				case VCF:
					try {
						listLogger.debug(userTrxInfo.logId() + " parsing VCF file ...");
						result = parseVCFContacts(userTrxInfo, fileDetails, list.getListId());
						listLogger.debug(userTrxInfo.logId() + " parsing VCF file  finished. successfully");
					} catch (DBException e) {
						listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (IOException e) {
						listLogger.error(userTrxInfo.logId() + "failed to read/write file", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (InvalidFileException e) {
						listLogger.error(userTrxInfo.logId() + "Invalid File .. " + e.getMessage());
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					} catch (Exception e) {
						listLogger.error(userTrxInfo.logId() + "Exception while parsing VCF file: ", e);
						contactListDao.remove(list);
						listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
						throw e;
					}
					break;
				default: {
					listLogger.error(userTrxInfo.logId() + "Invalid File type"
							+ new InvalidFileException("file type undefined").getMessage());
					contactListDao.remove(list);
					listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
					throw new InvalidFileException("file type undefined");
				}
				}
				listLogger.debug(userTrxInfo.logId() + "update list contact(s) count(" + result.getValidContacts()
						+ ").");
				list.setContactsCount((long) result.getValidContacts());
				contactListDao.edit(list);
				listLogger.info(userTrxInfo.logId() + list.toString() + " created successfully.");
			} else {
				listLogger.error(userTrxInfo.logId() + "duplicate list name: " + fileDetails.getListName());
				throw new DuplicateListNameException(fileDetails.getListName());
			}
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
			contactListDao.remove(list);
			listLogger.debug(userTrxInfo.logId() + " list deleted successfully");
			throw e;
		}
		return result;
	}

	// === Create Temp list Request ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public ContactList createTempList(TrxInfo trxInfo, Account acct, List<ContactModel> contacts) throws DBException {

		listLogger.info(trxInfo.logInfo() + "The list contain: " + (contacts != null ? contacts.size() : 0)
				+ " contact(s). ");
		ContactList newList = new ContactList();
		newList.setAccount(acct);
		listLogger.trace(trxInfo.logId() + "list accountID:" + acct.getAccountId());
		// setting random unique name to the temp list
		String str = UUID.randomUUID().toString();
		newList.setListName(str);
		listLogger.trace(trxInfo.logId() + "list_name: " + str);
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.TEMP_LIST);
		newList.setListType(listType);
		listLogger.trace(trxInfo.logId() + "list assigned " + listType);
		// Persist the temp list
		contactListDao.create(newList);
		listLogger.trace(trxInfo.logId() + "list created with id: " + newList.getListId()
				+ ", persisting contact in progress.");
		// persist the list contacts
		List<Contact> listContacts = new ArrayList<Contact>(contacts.size());
		for (ContactModel contactModel : contacts) {
			Contact newContact = listConversionBean.getContact(newList.getListId(), contactModel);
			listLogger.trace(trxInfo.logId() + "Contact to be persisted: " + newContact);
			if (newContact != null) {
				contactDao.create(newContact);
				listContacts.add(newContact);
			} else {
				listLogger.debug(trxInfo.logId() + " " + contactModel + " isn't valid contact");
			}

		}
		// optionally add the persisted contacts to the list
		newList.setListContacts(listContacts);
		newList.setContactsCount((long) listContacts.size());
		contactListDao.edit(newList);
		listLogger.info(trxInfo.logId() + listContacts.size() + " contact(s) persisted successfully.");
		return newList;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Map<Integer, List<ContactModel>> createResentCampList(UserTrxInfo userTrxInfo, List<Contact> contacts)
			throws DBException {
		listLogger.info(userTrxInfo.logInfo() + "the list contain: " + (contacts != null ? contacts.size() : 0)
				+ " contact(s). ");

		Map<Integer, List<ContactModel>> contactToListMap = new HashMap<>();

		for (Contact contact : contacts) {
			List<ContactModel> contactList = contactToListMap.get(contact.getListContactsPK().getListId());
			if (contactList != null) {
				contactList.add(listConversionBean.getContactModel(contact));
			} else {
				contactList = new ArrayList<>();
				contactList.add(listConversionBean.getContactModel(contact));
				contactToListMap.put(contact.getListContactsPK().getListId(), contactList);
			}
		}

		return contactToListMap;
	}

	@Override
	public void handleVirtualList(UserTrxInfo userTrxInfo, List<ContactModel> contactModels) throws DBException,
			InvalidAttributeException, IneligibleAccountException, DuplicateContactException, LockedListException,
			ListNotFoundException, EmptyConatctListException, ListTypeNotFoundException, ListTypeExpansionException {

		listLogger.info(userTrxInfo.logInfo() + "assign " + contactModels.size() + " contacts to virtual list.");
            if(contactModels != null && !contactModels.isEmpty()){
		List<ContactList> virtualLists;
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.VIRTUAL_LIST);

		listLogger.debug(userTrxInfo.logId() + "searching for virtualList.");
		virtualLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger.debug(userTrxInfo.logId() + "found " + virtualLists.size() + " virtualList.");

		if (virtualLists == null || virtualLists.isEmpty()) {
			listLogger.debug(userTrxInfo.logId() + "virtual list doesn't exist, creating new virtual list");
                        userTrxInfo.addUserAction(ActionName.CREATE_LIST);
			Account acct;
			acct = accountManagement.getAccount(userTrxInfo.getUser().getAccountId());
			createVirtualList(userTrxInfo, acct, contactModels);
		} else {
			listLogger.debug(userTrxInfo.logId() + "virtual list exist, list will be expanded ");
			ContactList virtualList = virtualLists.get(0);
			ContactListModel virtualContactListModel = listConversionBean.getContactListModel(virtualList);
			virtualContactListModel.setListContacts(contactModels);
			ContactListInfoModel info = new ContactListInfoModel();
			info.setListType(ListTypeName.VIRTUAL_LIST);
			info.setContactsCount(contactModels.size());
			info.setListId(virtualList.getListId());
			info.setListName(virtualList.getListName());
			virtualContactListModel.setListInfo(info);

			listLogger.debug(userTrxInfo.logId() + "virtual list with id " + virtualList.getListId()
					+ " will be expanded by : " + contactModels.size());
			expandContactList(userTrxInfo, virtualContactListModel);
		}
            }else {
                throw new EmptyConatctListException("At least you should add one contact to the list.");
            }

	}

	// === Create Virtual list Request ===
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public void createVirtualList(UserTrxInfo userTrxInfo, Account acct, List<ContactModel> contactModels)
			throws DBException, EmptyConatctListException {

		try {
			listLogger.info(userTrxInfo.logInfo() + "creating virtual list for " + acct + " and contacts are: "
					+ contactModels);
			if (contactModels != null && contactModels.size() != 0) {
				ContactList newList = new ContactList();
				List<Contact> contacts = new ArrayList<>();
				newList.setAccount(acct);
				listLogger.trace(userTrxInfo.logId() + "list accountID:" + acct.getAccountId());
				// setting default name to the virtual list
				String listName = (String) Configs.DEFAULT_VIRTUAL_LIST_NAME.getValue();
				newList.setListName(listName);
				listLogger.trace(userTrxInfo.logId() + "list_name: " + listName);
				ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.VIRTUAL_LIST);
				newList.setListType(listType);
				listLogger.trace(userTrxInfo.logId() + "list assigned " + listType);
				// Persist the temp list
				contactListDao.create(newList);
				listLogger.trace(userTrxInfo.logId() + "list created with id: " + newList.getListId() + ", convert "
						+ contactModels.size() + " Contact model to contact.");
				contacts = listConversionBean.getContacts(newList.getListId(), contactModels);
				listLogger.debug(userTrxInfo.logId()
						+ "persist contacts into DB after converted them from Contact Model successfully.");
				for (int i = 0; i < contacts.size(); i++) {
					contactDao.create(contacts.get(i));
				}

				listLogger.debug(userTrxInfo.logId() + "persist " + contacts.size() + " contacts success.");
				List<Contact> con = contactDao.findByListID(newList.getListId());
				listLogger.trace(userTrxInfo.logId() + " contacts count : " + con.size());
				newList.setContactsCount((long) con.size());
				contactListDao.edit(newList);
			} else {
				throw new EmptyConatctListException("At least you should add one contact to the list.");
			}

		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to persist into DB", e);

			throw e;
		}

	}

	// === Get List of Contacts from ListID Request ===
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<ContactModel> getContactList(UserTrxInfo userTrxInfo, int listId) throws DBException,
			IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.VIEW_LIST_CONTACTS);
		listLogger.info(userTrxInfo.logInfo() + "list parameter are:(listId=" + listId + ").");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());

		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "eligible account, start reading from DB");
		List<Contact> contacts = contactDao.findByListID(listId);
		listLogger.info(userTrxInfo.logId() + "request found " + contacts.size() + " contacts successfully.");
		return listConversionBean.getContactsModel(contacts);

	}

	// === Get List of Contacts from ListID Request ===
	@Override
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	public List<Contact> getContacts(UserTrxInfo userTrxInfo, int listId) throws DBException,
			IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.VIEW_LIST_CONTACTS);
		listLogger.info(userTrxInfo.logInfo() + "list parameter are:(listId=" + listId + ").");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());

		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "eligible account, start reading from DB");
		List<Contact> contacts = contactDao.findByListID(listId);
		listLogger.info(userTrxInfo.logId() + "request found " + contacts.size() + " contacts successfully.");
		return contacts;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ContactModel> getContactList(UserTrxInfo userTrxInfo, int listId, int first, int max)
			throws DBException, IneligibleAccountException {

		userTrxInfo.addUserAction(ActionName.VIEW_LIST_CONTACTS);
		listLogger.info(userTrxInfo.logInfo() + "list parameter are:(listId=" + listId + ", first=" + first + ", max="
				+ max + "). ");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());

		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "eligible account, start reading from DB");

		List<Contact> contacts = contactDao.findByListID(listId, first, max);
		listLogger.info(userTrxInfo.logId() + "request found " + contacts.size() + " contacts successfully.");
		return listConversionBean.getContactsModel(contacts);

	}

	// === Get count of ContactListInfo ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public Integer countContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames) throws DBException,
			IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.VIEW_LISTS);
		listLogger.info(userTrxInfo.logInfo() + "counting contact list info ");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "eligible account, start fetching from DB");
		UserModel user = userTrxInfo.getUser();
		try {
			List<ListType> listTypes = new ArrayList<>();
			for (ListTypeName listTypeName : listTypeNames) {
				listTypes.add(listTypeDao.getCachedObjectByName(listTypeName));
			}
			int result = contactListDao.counByAccountIdAndTypes(user.getAccountId(), listTypes);
			listLogger.info(userTrxInfo.logId() + "request success, found contactLists=" + result);
			return result;
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to find list", e);
			throw e;
		}
	}

	// === Get List of ContactListInfo ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, List<ListTypeName> listTypeNames)
			throws DBException, IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.VIEW_LISTS);
		listLogger.info(userTrxInfo.logInfo() + "getting all contact lists info..");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "eligible account, retreive from DB");
		UserModel user = userTrxInfo.getUser();
		if (listTypeNames != null) {
			try {
				List<ListType> listTypes = new ArrayList<>();

				for (ListTypeName listTypeName : listTypeNames) {
					listTypes.add(listTypeDao.getCachedObjectByName(listTypeName));
				}
				// listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST));
				// listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST));
				// if (!intraListFlag) {
				//
				// listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST));
				// listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName.VIRTUAL_LIST));
				// }
				listLogger.debug(userTrxInfo.logId() + "List types: " + listTypes);
				List<ContactListInfoModel> contactLists = new ArrayList<>();
				List<ContactList> lists = contactListDao.findByAccountIdAndTypes(user.getAccountId(), listTypes);

				listLogger.debug(userTrxInfo.logId() + " lists, convert to list models, count=" + lists.size());
				// Set<ContactList> listsSet = new HashSet<>(lists);
				// checkVirtualList(userTrxInfo, listsSet);

				for (ContactList contactList : lists) {
					listLogger.trace(userTrxInfo.logId() + "convert " + contactList.logId() + " to list info model");
					ContactListInfoModel list = listConversionBean.getContactListInfoModel(contactList);
					contactLists.add(list);
				}

				listLogger.info(userTrxInfo.logId() + "Retrieved contact lists, count=" + contactLists.size());
				return contactLists;
			} catch (DBException e) {
				listLogger.error(userTrxInfo.logId() + "failed to find list", e);
				throw e;
			}
		} else {
			return null;
		}
	}

	/*
	 * // === Get List of ContactListInfo ===
	 * 
	 * @Override
	 * 
	 * @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) public
	 * List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo,
	 * int first, int max) throws DBException, IneligibleAccountException {
	 * userTrxInfo.addUserAction(UserActionName.VIEW_LISTS);
	 * listLogger.info(userTrxInfo.logInfo() + "paginating range [first: " +
	 * first + ", count:" + max + "]."); listLogger.debug(userTrxInfo.logId() +
	 * " checking account eligibility to: " + userTrxInfo.getUserActions());
	 * accountManagement.checkAccountAndUserEligibility(userTrxInfo);
	 * listLogger.debug(userTrxInfo.logId() +
	 * "Eligible account, retrieving from database"); AccManagUserModel user =
	 * userTrxInfo.getUser(); try { List<ListType> listTypes = new
	 * ArrayList<>();
	 * listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName
	 * .NORMAL_LIST));
	 * listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName
	 * .INTRA_LIST));
	 * listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName
	 * .INTRA_SUB_LIST));
	 * listTypes.add(listTypeDao.getCachedObjectByName(ListTypeName
	 * .VIRTUAL_LIST)); listLogger.debug(userTrxInfo.logId() + "List types: " +
	 * listTypes); List<ContactListInfoModel> contactLists = new ArrayList<>();
	 * 
	 * // List<ContactList> lists = //
	 * contactListDao.findByAccountIdAndTypesWithCounts(user.getAccountId(), //
	 * listTypes, List<ContactList> lists = contactListDao
	 * .findByAccountIdAndTypes(user.getAccountId(), listTypes, first, max);
	 * 
	 * listLogger.debug(userTrxInfo.logId() +
	 * " lists, convert to list models, count=" + lists.size()); // if(first ==
	 * 0){ // Set<ContactList> listsSet = new HashSet<>(lists); //
	 * checkVirtualList(userTrxInfo, listsSet); // } for (ContactList
	 * contactList : lists) { listLogger.trace(userTrxInfo.logId() + "convert "
	 * + contactList.logId() + " to list info model"); ContactListInfoModel list
	 * = listConversionBean.getContactListInfoModel(contactList);
	 * contactLists.add(list); }
	 * 
	 * listLogger.info(userTrxInfo.logId() + "Retrieved contact lists, count=" +
	 * contactLists.size()); return contactLists; } catch (DBException e) {
	 * listLogger.error(userTrxInfo.logId() + "failed to find list", e); throw
	 * e; } }
	 */

	// === Get List of ContactListInfo ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, int first, int max,
			List<ListTypeName> listTypeNames) throws DBException, IneligibleAccountException {
		userTrxInfo.addUserAction(ActionName.VIEW_LISTS);
		listLogger.info(userTrxInfo.logInfo() + "paginating range [first: " + first + ", count:" + max + "].");
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "Eligible account, retrieving from database");
		UserModel user = userTrxInfo.getUser();
		try {
			List<ListType> listTypes = new ArrayList<>();
			for (ListTypeName listTypeName : listTypeNames) {
				listTypes.add(listTypeDao.getCachedObjectByName(listTypeName));
			}
			listLogger.debug(userTrxInfo.logId() + "List types: " + listTypes);
			List<ContactListInfoModel> contactLists = new ArrayList<>();

			// List<ContactList> lists =
			// contactListDao.findByAccountIdAndTypesWithCounts(user.getAccountId(),
			// listTypes,
			List<ContactList> lists = contactListDao
					.findByAccountIdAndTypes(user.getAccountId(), listTypes, first, max);
			listLogger.debug(userTrxInfo.logId() + "Checking for Intra List.");
			List<ContactList> intraList = null;
			if (first == 0) {
				// Set<ContactList> listsSet = new HashSet<>(lists);
				ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST);
				int intraListCount = contactListDao.counByAccountIdAndType(user.getAccountId(), listType);
				if (intraListCount == 0) {
					try {
						listManegementBean.handleIntraList(userTrxInfo);
						intraList = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
					} catch (InterruptedException e) {
						listLogger.error(userTrxInfo.logId() + "InterruptedException, Failed to handle intra list", e);

					} catch (ExecutionException e) {
						listLogger.error(userTrxInfo.logId() + "ExecutionException, Failed to handle intra list", e);

					} catch (ListNotFoundException e) {
						listLogger.error(userTrxInfo.logId() + "ListNotFoundException, Failed to handle intra list", e);

					} catch (IntraListInquiryFailed e) {
						listLogger
								.error(userTrxInfo.logId() + "IntraListInquiryFailed, Failed to handle intra list", e);

					} catch (InvalidCustomerForQuotaInquiry e) {
						listLogger.error(userTrxInfo.logId()
								+ "InvalidCustomerForQuotaInquiry, Failed to handle intra list", e);

					}
				}
			}
			if (intraList != null && !intraList.isEmpty()) {
				lists.add(0, intraList.get(0));
			}
			listLogger.debug(userTrxInfo.logId() + " lists, convert to list models, count=" + lists.size());
			for (ContactList contactList : lists) {
				listLogger.trace(userTrxInfo.logId() + "convert " + contactList.logId() + " to list info model");
				ContactListInfoModel list = listConversionBean.getContactListInfoModel(contactList);
				contactLists.add(list);
			}

			listLogger.info(userTrxInfo.logId() + "Retrieved contact lists, count=" + contactLists.size());
			return contactLists;
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to find list", e);
			throw e;
		}
	}

	// === Get List of ContactListInfo ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<ContactListInfoModel> getContactListsInfo(UserTrxInfo userTrxInfo, String campId) throws DBException,
			IneligibleAccountException, CampaignNotFoundException {
		userTrxInfo.addUserAction(ActionName.VIEW_LISTS);
		listLogger.info(userTrxInfo.logInfo() + "requesting getContactListsInfo by campaignId:" + campId);
		try {
			List<ContactList> lists = campaignListsDao.findListsByCampaignIdAndAccountId(campId, userTrxInfo.getUser()
					.getAccountId());
			if (!lists.isEmpty()) {
				listLogger.info(userTrxInfo.logId() + "request success.");
				return listConversionBean.getContactListsInfoModel(lists);
			}
			throw new CampaignNotFoundException(campId);
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to find list", e);
			throw e;
		}
	}

	@Override
	public List<ContactList> getContactLists(UserTrxInfo userTrxInfo, String campaignId, List<ListType> listTypes,
			boolean withCounts) throws DBException {
		String accountId = userTrxInfo.getUser().getAccountId();
		listLogger.debug(userTrxInfo.logId() + "Retrieving contact lists, accountId=" + accountId + ", campaignId="
				+ campaignId + ", listTypes: " + listTypes);
		List<ContactList> lists = null;
		// if (withCounts) {
		// // lists =
		// //
		// campaignListsDao.findListsByCampaignIdAndAccountIdWithCounts(campaignId,
		// // accountId, listTypes);
		// lists =
		// campaignListsDao.findListsByCampaignIdAndAccountId(campaignId,
		// accountId, listTypes);
		// } else {
		lists = campaignListsDao.findListsByCampaignIdAndAccountId(campaignId, accountId, listTypes);
		// }
		listLogger.debug(userTrxInfo.logId() + "Retrieved contact lists, count=" + lists.size());
		return lists;
	}

	// === Count SMS in list Request ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public Integer countContactsInLists(TrxInfo trxInfo, List<Integer> lists) throws DBException {
		listLogger.info(trxInfo.logInfo() + "count contacts from listsId:" + lists + " .");
		List<ContactList> ListOfContactList = contactListDao.findByListIds(lists);
		int result = 0;
		for (ContactList contactList : ListOfContactList) {
			result += contactList.getContactsCount();
		}
		listLogger.debug(trxInfo.logId() + result + " contacts found.");
		return result;
	}

	// === Search in list ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<ContactListInfoModel> searchLists(UserTrxInfo userTrxInfo, String listName, List<ListTypeName> listType)
			throws DBException, ListNotFoundException, IneligibleAccountException {
		
		userTrxInfo.addUserAction(ActionName.VIEW_LISTS);
		listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
		accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
		listLogger.debug(userTrxInfo.logId() + "Eligible account");
		
		
		listLogger.info(userTrxInfo.logInfo() + "List_Name: " + listName);
		UserModel user = userTrxInfo.getUser();
		try {
			// List<ListType> listType = new ArrayList<ListType>();
			// listType.add(listTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST));
			// listType.add(listTypeDao.getCachedObjectByName(ListTypeName.CUSTOMIZED_LIST));
			// listType.add(listTypeDao.getCachedObjectByName(ListTypeName.VIRTUAL_LIST));
			// listType.add(listTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST));
			// listType.add(listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST));
			List<ListType> listTypes = new ArrayList<>();
			for (ListTypeName listTypeName : listType) {
				listTypes.add(listTypeDao.getCachedObjectByName(listTypeName));
			}
			List<ContactList> list = contactListDao.searchByListNameAndTypeWithCounts(listName, listTypes,
					user.getAccountId());

			if (list == null) {
				listLogger.error(userTrxInfo.logId() + " list name: " + listName + " not found!");
				throw new ListNotFoundException(listName);
			}
			listLogger.debug(userTrxInfo.logId() + "starting search in list: " + listName + ". ");

			List<ContactListInfoModel> newList = new ArrayList<ContactListInfoModel>();
			for (int i = 0; i < list.size(); i++) {
				newList.add(listConversionBean.getContactListInfoModel(list.get(i)));
			}
			listLogger.info(userTrxInfo.logInfo() + "requesting searchLists success.");
			return newList;
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "DBException: " + e.getMessage() + " ", e);
			throw e;
		}
	}

	@Override
	public void editContactListName(UserTrxInfo userTrxInfo, Integer listId, String newListName)
			throws IneligibleAccountException, DBException, ListNotFoundException, LockedListException,
			DuplicateListNameException {

		userTrxInfo.addUserAction(ActionName.EDIT_LIST);
		listLogger
				.info(userTrxInfo.logInfo() + "want to replance list[" + listId + "] name to: [" + newListName + "].");
		UserModel user = userTrxInfo.getUser();
		// catch DBException
		try {
			// Check account Eligibility
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible.. and searching for list name ... ");

			ContactList oldList = contactListDao.findByListIdAndAccountId(listId, user.getAccountId());

			if (oldList != null) {

				listLogger.debug(userTrxInfo.logId() + " list[" + listId + "] found.");
				int newList = contactListDao.countByListNameAndAccountId(newListName, user.getAccountId());
				if (newList == 0) {
					int updateResult = contactListDao.updateListName(listId, newListName, user.getAccountId());
					listLogger.debug(userTrxInfo.logId() + updateResult + " row edited");
				} else {

					listLogger.debug(userTrxInfo.logId() + "list named: " + newListName + " already exist.");

					throw new DuplicateListNameException(newListName);
				}

			} else {
				listLogger.debug(userTrxInfo.logId() + " can't find list_Id[" + listId + "].");
				throw new ListNotFoundException(String.valueOf(listId));

			}
		} catch (DBException e) {

			listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
			throw e;

		}

	}

	/*
	 * Phase 2 implementation of merge two lists into one list
	 * 
	 * @Override public ResultStatus expandContactList(UserTrxInfo userTrxInfo,
	 * ContactListModel contactsList) throws IneligibleAccountException {
	 * 
	 * ResultStatus result = new ResultStatus();
	 * result.setStatus(ResponseStatus.SUCCESS.toString()); List<Contact>
	 * newContactList =
	 * listConversionBean.getContacts(contactsList.getListInfo().getListId(),
	 * contactsList.getListContacts()); AccManagUserModel user = userTrxInfo.getUser();
	 * 
	 * try { accountManagement.checkAccountAndUserEligibility(user.getAccountId(),
	 * UserActionName.CREATE_LIST);
	 * 
	 * // Search for the list ContactList originalList =
	 * contactListDao.findByListName(contactsList.getListInfo().getListName());
	 * if (originalList != null) {
	 * 
	 * List<CampaignStatusName> statusList = new ArrayList<>();
	 * 
	 * statusList.add(CampaignStatusName.ON_HOLD);
	 * statusList.add(CampaignStatusName.PAUSED);
	 * statusList.add(CampaignStatusName.RUNNING);
	 * statusList.add(CampaignStatusName.NEW);
	 * 
	 * boolean listToActiveCampaignFlag =
	 * campaignListsDao.isListAssociatedToCampaignStatus(contactsList
	 * .getListInfo().getListId(), statusList);
	 * 
	 * if (!listToActiveCampaignFlag) {
	 * 
	 * List<Contact> oldContactList = originalList.getListContacts();
	 * 
	 * /* This hash map tagged old contacts with false and the new contacts with
	 * true so we can persist non-duplicate contacts only
	 * 
	 * 
	 * HashMap<Contact, Boolean> expandedlist = new HashMap<>(); for (Contact
	 * contact : oldContactList) { expandedlist.put(contact, false); }
	 * 
	 * // put non- duplicate contacts into hash map with true // tag for
	 * (Contact contact : newContactList) { if
	 * (!expandedlist.containsKey(contact)) { expandedlist.put(contact, true); }
	 * 
	 * } // put out hash map into set to iterate it's fields
	 * newContactList.clear(); Set<Entry<Contact, Boolean>> allContactSet =
	 * expandedlist.entrySet();
	 * 
	 * if (!allContactSet.isEmpty()) {
	 * 
	 * for (Entry<Contact, Boolean> contact : allContactSet) { if
	 * (contact.getValue()) { newContactList.add(contact.getKey()); } }
	 * 
	 * } if (!newContactList.isEmpty()) { contactDao.create(newContactList); }
	 * else {
	 * 
	 * result.setErrorMessage("there is no new contact(s) to add");
	 * result.setStatus(ResponseStatus.LIST_UPDATE_FAIL.toString());
	 * 
	 * } } else {
	 * result.setErrorMessage("This List accociated to active campaign");
	 * result.
	 * setStatus(ResponseStatus.LIST_ASSOCIATED_TO_ACTIVE_CAMPAIGN.toString());
	 * 
	 * } } else { result.setErrorMessage("This List isn't exist");
	 * result.setStatus(ResponseStatus.LIST_NOT_FOUND.toString()); }
	 * 
	 * } catch (DBException e) { e.printStackTrace();
	 * result.setErrorMessage(e.getMessage());
	 * result.setStatus(ResponseStatus.FAIL.toString()); }
	 * 
	 * return result;
	 * 
	 * }
	 */

	// === Update list Request -expand- ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void expandContactList(UserTrxInfo userTrxInfo, ContactListModel contactsList)
			throws IneligibleAccountException, DuplicateContactException, LockedListException, ListNotFoundException,
			DBException, InvalidAttributeException, ListTypeNotFoundException, ListTypeExpansionException {

		userTrxInfo.addUserAction(ActionName.EDIT_LIST);
		ListTypeName listTypeName = contactsList.getListInfo().getListType();

		listLogger.info(userTrxInfo.logInfo() + "expand list: " + contactsList.getListInfo().getListName() + ", Type: "
				+ contactsList.getListInfo().getListType());
		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: " + userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}

		if (listTypeName == null) {
			listLogger.error(userTrxInfo.logId() + "List Type can't be null.");
			throw new ListTypeNotFoundException();
		}

		listLogger.info(userTrxInfo.logInfo() + "expand list: " + contactsList.getListInfo().getListName() + ", Type: "
				+ listTypeName);
		List<Contact> newContactList = listConversionBean.getContacts(contactsList.getListInfo().getListId(),
				contactsList.getListContacts());

		switch (listTypeName) {
		case INTRA_LIST: {
			listLogger.error(userTrxInfo.logInfo() + "list of type " + listTypeName.name() + " can't be expanded.");
			throw new ListTypeExpansionException();
		}
		case INTRA_SUB_LIST: {
//			newContactList = intraListManagementBean.validateIntraSubLists(userTrxInfo, newContactList);
			break;
		}
		case NORMAL_LIST:
			break;
		case CUSTOMIZED_LIST:
			break;
		case PRENORMAL_LIST: {
			listLogger.error(userTrxInfo.logInfo() + "list of type " + listTypeName.name() + " can't be expanded.");
			throw new ListTypeExpansionException();
		}
		case TEMP_LIST: {
			listLogger.error(userTrxInfo.logInfo() + "list of type " + listTypeName.name() + " can't be expanded.");
			throw new ListTypeExpansionException();
		}
		case UNKNOWN: {
			listLogger.error(userTrxInfo.logInfo() + "list of type " + listTypeName.name() + " can't be expanded.");
			throw new ListTypeExpansionException();
		}
		case VIRTUAL_LIST:
			break;
		default: {
			listLogger.error(userTrxInfo.logInfo() + "list of type " + listTypeName.name() + " can't be expanded.");
			throw new ListTypeExpansionException();
		}
		}
		List<Contact> duplicateList = new ArrayList<>();
		boolean duplicateContactFlag = false, persistContactFlag = false;

		listLogger.debug(userTrxInfo.logId() + "searching for list:" + contactsList.getListInfo().getListName());
		// Search for the list
		// ContactList originalList =
		// contactListDao.findByListName(contactsList.getListInfo().getListName());
		ContactList originalList = contactListDao.findByListId(contactsList.getListInfo().getListId());

		if (originalList != null) {
			listLogger.debug(userTrxInfo.logId() + " list found, check editing ability");

			List<CampaignStatus> activeCampStatusList = campaignManagementBean.getActiveCampaignStatusList();
			boolean listToActiveCampaignFlag = campaignListsDao.isListAssociatedToCampaignStatus(contactsList
					.getListInfo().getListId(), activeCampStatusList);
			if (!listToActiveCampaignFlag) {

				listLogger.debug(userTrxInfo.logId() + " list can be updated");
				for (Contact contact : newContactList) {
					if (contactDao.count(contact.getListContactsPK()) == 0) {
						contactDao.create(contact);
						persistContactFlag = true;
					} else {
						listLogger.trace(userTrxInfo.logId() + contact + " already in database.");
						// throw new DuplicateContactException(contact);
						duplicateList.add(contact);
						duplicateContactFlag = true;
					}
				}
				// if(listTypeName.equals(lis))
				listLogger.debug(userTrxInfo + " list found: " + originalList.toString());
				Long contactsCount = contactDao.countContactInList(originalList.getListId());
				listLogger.debug(userTrxInfo.logId() + " list contact count(" + contactsCount + ").");
				originalList.setContactsCount(contactsCount);
				contactListDao.edit(originalList);

				if (persistContactFlag && duplicateContactFlag) {
					// all contacts valid but there is duplication
					listLogger.debug(userTrxInfo.logId() + "all contacts are vaild with some duplication");
					throw new DuplicateContactException(duplicateList, false);
				} else if (!persistContactFlag && duplicateContactFlag) {
					// all contacts are duplicated
					listLogger.error(userTrxInfo.logId() + " all contacts are already exist in DB.");
					throw new DuplicateContactException(duplicateList, true);
				} else if (!persistContactFlag && !duplicateContactFlag) {
					listLogger.error(userTrxInfo.logId() + " all contacts are not valid.");
					throw new InvalidAttributeException("there is no valid contacts to update");
				}

				if (listTypeName.equals(ListTypeName.INTRA_SUB_LIST)) {
					listLogger.debug(userTrxInfo.logInfo() + "validating sub intra list with id :("
							+ contactsList.getListInfo().getListId() + " ) after expansion ");
					int numOfDeletedRows = intraListManagementBean.validateIntraSubListsInDB(userTrxInfo, contactsList
							.getListInfo().getListId());
					Long newContactCount = originalList.getContactsCount() - numOfDeletedRows;
					listLogger.debug(userTrxInfo.logInfo() + "delete :(" + numOfDeletedRows
							+ ") not valid contacts, and will update original list contact count to: ("
							+ newContactCount + ").");
					originalList.setContactsCount(newContactCount);
					contactListDao.edit(originalList);
					listLogger.debug(userTrxInfo.logInfo() + "original list contact count updated successfully");
				}
			} else {
				listLogger.error(userTrxInfo.logId() + contactsList + " can't be updated. ");
				throw new LockedListException();
			}
		} else {
			listLogger.error(userTrxInfo.logId() + contactsList + "can't be found in database.");
			throw new ListNotFoundException(contactsList.getListInfo().getListName());
		}
	}

	// === Update list Request -delete- ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteSubContactList(UserTrxInfo userTrxInfo, ContactListModel contactsListModel)
			throws LockedListException, ListNotFoundException, IneligibleAccountException, DBException {

		listLogger.info(userTrxInfo.logInfo() + "delete sublist: " + contactsListModel.getListInfo().getListName());

		userTrxInfo.addUserAction(ActionName.EDIT_LIST);
		List<Contact> newContactList = listConversionBean.getContacts(contactsListModel.getListInfo().getListId(),
				contactsListModel.getListContacts());
		List<ContactNotFoundException> contactMissException = new ArrayList<>();

		try {
			try {
				listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: "
						+ userTrxInfo.getUserActions());
				accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
				listLogger.debug(userTrxInfo.logId() + " account is eligible");
			} catch (IneligibleAccountException e) {
				listLogger.error(userTrxInfo.logId() + e.getMessage());
				throw e;
			}

			listLogger.debug(userTrxInfo.logId() + "searching for list:"
					+ contactsListModel.getListInfo().getListName());
			// Search for the list
			ContactList originalList = contactListDao.findByListId(contactsListModel.getListInfo().getListId());

			// ContactList originalList =
			// contactListDao.findByListName(contactsListModel.getListInfo().getListName());
			if (originalList != null) {

				listLogger.debug(userTrxInfo.logId() + " list found, check editing ability");
				List<CampaignStatus> activeCampStatusList = campaignManagementBean.getActiveCampaignStatusList();
				boolean listToActiveCampaignFlag = campaignListsDao.isListAssociatedToCampaignStatus(contactsListModel
						.getListInfo().getListId(), activeCampStatusList);
				if (!listToActiveCampaignFlag) {

					listLogger.debug(userTrxInfo.logId() + " list can be modified");
					for (Contact contact : newContactList) {
						if (contactDao.count(contact.getListContactsPK()) == 1) {
							contactDao.remove(contact);
						} else {
							listLogger.error(userTrxInfo.logId() + contact + " not found in the list.");
							contactMissException.add(new ContactNotFoundException(contact));
						}
					}

					Long contactsCount = contactDao.countContactInList(originalList.getListId());
					originalList.setContactsCount(contactsCount);
					contactListDao.edit(originalList);

				} else {
					listLogger.error(userTrxInfo.logId() + contactsListModel.getListInfo().getListName()
							+ " list that can't be updated. ");
					throw new LockedListException();
				}
			} else {
				listLogger.error(userTrxInfo.logId() + "list name: " + contactsListModel.getListInfo().getListName()
						+ " is not found");
				throw new ListNotFoundException(contactsListModel.getListInfo().getListName());
			}
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
			throw e;
		}
	}

	// === delete list ===
	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteContactList(UserTrxInfo userTrxInfo, ContactListInfoModel contactsListInfoModel)
			throws LockedListException, ListNotFoundException, IneligibleAccountException, DBException {

		listLogger.info(userTrxInfo.logInfo() + "delete list: " + contactsListInfoModel.getListName());
		userTrxInfo.addUserAction(ActionName.DELETE_LIST);
		try {
			try {
				listLogger.debug(userTrxInfo.logId() + " checking account eligibility to: "
						+ userTrxInfo.getUserActions());
				accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
				listLogger.debug(userTrxInfo.logId() + " account is eligible");
			} catch (IneligibleAccountException e) {
				listLogger.error(userTrxInfo.logId() + e.getMessage());
				throw e;
			}
			listLogger.debug(userTrxInfo.logId() + "searching for list:" + contactsListInfoModel.getListName());
			// Search for the list
			// ContactList originalList =
			// contactListDao.findByListName(contactsListInfoModel.getListName());
			ContactList originalList = contactListDao.findByListId(contactsListInfoModel.getListId());

			if (originalList != null) {
				listLogger.debug(userTrxInfo.logId() + " list found, check editing ability");
				List<CampaignStatus> activeCampStatusList = campaignManagementBean.getActiveCampaignStatusList();
				boolean listToActiveCampaignFlag = campaignListsDao.isListAssociatedToCampaignStatus(
						contactsListInfoModel.getListId(), activeCampStatusList);
				if (!listToActiveCampaignFlag) {
					List<Future<Integer>> asyncoResultList = new ArrayList<>();
					Future<Integer> asyncoResult = null;
					listLogger.debug(userTrxInfo.logId() + " list can be deleted");

					asyncoResult = contactListDao.removeByListId(originalList.getListId());
					listLogger.debug(userTrxInfo.logId() + " list delete requested");
					if (asyncoResult != null) {
						asyncoResultList.add(asyncoResult);
						listHandler.handleAsyncoResult(asyncoResultList, originalList.getListId());
					}
				} else {
					listLogger.error(userTrxInfo.logId() + contactsListInfoModel.getListName()
							+ " list that can't be updated. ");
					throw new LockedListException();
				}
			} else {
				listLogger.error(userTrxInfo.logId() + "list name: " + contactsListInfoModel.getListName()
						+ " is not found");
				throw new ListNotFoundException(contactsListInfoModel.getListName());
			}
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + "failed to persist list", e);
			throw e;
		}
	}

	// === Export list to file ===
	// keeps in for phase 2 as export to xls/xlsx files
	public FileDetails exportListToFile(UserTrxInfo userTrxInfo, Integer listId) throws IneligibleAccountException,
			DBException {

		listLogger.info(userTrxInfo.logInfo() + "listID:" + listId);
		userTrxInfo.addUserAction(ActionName.EXPORT_LIST_TO_FILE);

		List<Contact> contactList = null;
		int rownum = 0, cellnum = 0;
		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: "
					+ userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}

		try {
			contactList = contactDao.findByListID(listId);
		} catch (DBException e) {
			throw e;
		}

		// sample code

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("Sample sheet");

		if (contactList != null) {
			for (Contact contact : contactList) {
				cellnum = 0;
				Row row = sheet.createRow(rownum);

				Cell cell = row.createCell(cellnum);
				cell.setCellValue(contact.getListContactsPK().getMsisdn());
				cellnum++;

				cell = row.createCell(cellnum);
				cell.setCellValue(contact.getFirstName());
				cellnum++;

				cell = row.createCell(cellnum);
				cell.setCellValue(contact.getLastName());

				rownum++;
			}
		}

		try {
			OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("/data/work/" + listId + ".xls")));
			workbook.write(out);
			out.flush();
			out.close();
			// System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// === export list of contact into CSV file and return filetoken ===
	public String exportListToCsvFile(UserTrxInfo userTrxInfo, Integer listId) throws IneligibleAccountException,
			DBException, IOException {

		userTrxInfo.addUserAction(ActionName.EXPORT_LIST_TO_FILE);
		ContactCSVRecord csvRecord = new ContactCSVRecord();
		CSVBatchFile batchFile = null;
		StopWatch sw = new StopWatch();
		listLogger.info(userTrxInfo.logInfo() + "listID:" + listId);

		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: "
					+ userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}

		List<Contact> contactList;
		String outFileToken = null;
		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String delim = (String) Configs.EXPORTED_LISTS_CSV_DELIM.getValue();
		String quoteChar = (String) Configs.EXPORTED_LISTS_CSV_QUOTE_CHAR.getValue();
		String outPath = (String) Configs.EXPORTED_LISTS_FILES_PATH.getValue();
		Boolean compressed = (Boolean) Configs.EXPORTED_LISTS_FILE_COMPRESSED.getValue();
		String outFullPath = basePath + outPath;

		String baseFileName = null;
		// BufferedWriter out = null;
		int chunk = (int) Configs.EXPORTED_LISTS_CHUNK_SIZE.getValue();
		int contactsCount, first = 0;

		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: "
					+ userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}

		try {
			listLogger.debug(userTrxInfo.logId() + "Count no. of contacts in the list");

			// contactList = contactDao.findByListID(listId);
			contactsCount = (int) contactListDao.countContacts(listId);
			listLogger.debug(userTrxInfo.logId() + "read " + contactsCount + " contacts, start writing the file.");

			if (contactsCount > 0) {
				int chunkNumber = 1;
				listLogger.debug(userTrxInfo.logId() + "contacts will be read in " + (contactsCount / chunk)
						+ " iterations");
				baseFileName = getUniqueFileName(listId.toString());
				batchFile = openContactCSV(csvRecord, outFullPath, baseFileName, delim, quoteChar,
						StandardCharsets.UTF_8, compressed);
				listLogger.debug(userTrxInfo.logId() + "file " + baseFileName + " created");

				batchFile.writeCSVRecordHeader();

				for (first = 0; first < contactsCount; first += chunk) {

					if (first < contactsCount) {

						listLogger.debug(userTrxInfo.logId() + "start calling chunk from [" + first + "].");
						sw.start();
						contactList = contactDao.findByListID(listId, first, chunk);
						sw.stop();
						listLogger.debug(userTrxInfo.logId() + "chunk " + chunkNumber++
								+ (contactList != null ? ", list size= " + contactList.size() : "") + " loaded in: "
								+ (sw.getElapsedTime(TimeUnit.SECONDS) + "s."));

						sw.start();
						for (Contact contact : contactList) {
							updateContactCSVRecord(contact, csvRecord);
							batchFile.writeCSVRecord(csvRecord);
							// listLogger.trace(userTrxInfo.logId() +
							// "writing contact [First Name:"
							// + csvRecord.getFirstName() + ", Last Name:" +
							// csvRecord.getFirstName()
							// + ", MSISDN:" + csvRecord.getMSISDN() + "].");
						}
						sw.stop();

					}
				}

				listLogger.debug(userTrxInfo.logId() + "Closing the csv batch file");
				batchFile.close();
				outFileToken = FileNameUtils.encodeFileToken(outPath + batchFile.getFileName());
				listLogger.info(userTrxInfo.logId() + "The list[" + listId + "] is exported to ["
						+ batchFile.getFileName() + "], output fileToken: " + outFileToken + ", exported in: "
						+ (sw.getElapsedTime(TimeUnit.SECONDS) + " s."));

			}
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId(), e);
			appLogger.error(userTrxInfo.logId() + "DB failed to persist ", e);
			throw e;
		} catch (IOException e) {
			listLogger.error(userTrxInfo.logId(), e);
			appLogger.error(userTrxInfo.logId() + "IO Exception ", e);
			throw e;
		}

		return outFileToken;

	}

	// === Search for contacts ===
	@Override
	public List<ContactModel> searchContacts(UserTrxInfo userTrxInfo, String contact, Integer listId)
			throws IneligibleAccountException, DBException {

		listLogger.info(userTrxInfo.logInfo() + "search for contact:" + contact + " in listID:" + listId);
		userTrxInfo.addUserAction(ActionName.SEARCH_FOR_CONTCAT);
		List<ContactModel> result;

		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: "
					+ userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

			result = listConversionBean.getContactsModel(contactDao.searchContacts(contact, listId));
			listLogger.debug(userTrxInfo.logId() + (result != null ? result.size() : 0) + " contacts found matches:"
					+ contact);
			return result;
		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}
	}

	@Override
	public void editContacts(UserTrxInfo userTrxInfo, ContactModel contact, String msisdn, Integer listId)
			throws IneligibleAccountException, DBException, ContactNotFoundException {

		listLogger.info(userTrxInfo.logInfo() + "Editing contact:" + contact + " in listID:" + listId);
		userTrxInfo.addUserAction(ActionName.EDIT_CONTCAT);

		try {
			listLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: "
					+ userTrxInfo.getUserActions());
			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			listLogger.debug(userTrxInfo.logId() + " account is eligible");

			int searchResult = contactDao.isContactExist(msisdn, listId);
			if (searchResult == 1) {
				listLogger.debug(userTrxInfo.logId() + " contact's MSISDN: " + msisdn + " found");
				Contact editedContact = listConversionBean.getContact(listId, contact);

				if (editedContact != null) {
					int updateResult = contactDao.updateContact(editedContact, msisdn);
					listLogger.debug(userTrxInfo.logId() + updateResult + " " + editedContact + " updated");
				} else {
					listLogger.debug(userTrxInfo.logId() + " " + contact + " isn't valid contact");

				}

			} else {
				throw new ContactNotFoundException(null);
			}
		} catch (IneligibleAccountException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		} catch (DBException e) {
			listLogger.error(userTrxInfo.logId() + e.getMessage());
			throw e;
		}
	}

	// === helper method to parse excel file ===
	private FileValidation parseExcelContacts(UserTrxInfo userTrxInfo, FileDetails fileDetails, Integer listId)
			throws DBException, InvalidFileException, IOException {

		listLogger.info(userTrxInfo.logInfo() + "starting parsing excel sheet...");

		FileValidation validation = new FileValidation();
		validation.setCreatedlistId(listId);
		Iterator<Row> rowIterator = null;
		Set<Contact> validContactSet = new HashSet<>();

		try {
			// To Define is it XLS or XLSX
			listLogger.debug(userTrxInfo.logId() + "Defining excel file type [XLS | XLSX].");
			// return rowIterator object if file is a xls and null if file is
			// xlsx
			rowIterator = excelTypesHandler(userTrxInfo, fileDetails);
			listLogger.debug(userTrxInfo.logId() + "excel file type defined.");

		} catch (IOException e) {
			listLogger.error(userTrxInfo.logId() + "failed to read/write file", e);
			throw e;

		}

		// Starting row iteration
		listLogger.debug(userTrxInfo.logId() + "start parsing the file");
		if (rowIterator != null) {
			while (rowIterator.hasNext()) {

				Row row = rowIterator.next();
				Iterator<Cell> cellIterator = row.cellIterator();

				// Initialize container and variables
				String tmpContent = "";
				Map<String, String> tmpContactParam = new HashMap<>();
				// tmpContactParam.put("msisdn", "");
				// tmpContactParam.put("firstName", "");
				// tmpContactParam.put("lastname", "");
				// tmpContactParam.put("value1", "");
				// tmpContactParam.put("value2", "");
				// tmpContactParam.put("value3", "");
				// tmpContactParam.put("value4", "");
				// tmpContactParam.put("value5", "");
				boolean validContactFlag = false;
				int cellNumber = 0;
				int maxColNum = 0;

				// Defining the max column number
				maxColNum = Math.max(fileDetails.getMsisdnColNumber(), fileDetails.getFnameColNumber()) > maxColNum ? Math
						.max(fileDetails.getMsisdnColNumber(), fileDetails.getFnameColNumber()) : maxColNum;
				maxColNum = Math.max(fileDetails.getLnameColNumber(), fileDetails.getValue1ColNumber()) > maxColNum ? Math
						.max(fileDetails.getLnameColNumber(), fileDetails.getValue1ColNumber()) : maxColNum;
				maxColNum = Math.max(fileDetails.getValue2ColNumber(), fileDetails.getValue3ColNumber()) > maxColNum ? Math
						.max(fileDetails.getValue2ColNumber(), fileDetails.getValue3ColNumber()) : maxColNum;
				maxColNum = Math.max(fileDetails.getValue4ColNumber(), fileDetails.getValue5ColNumber()) > maxColNum ? Math
						.max(fileDetails.getValue4ColNumber(), fileDetails.getValue5ColNumber()) : maxColNum;

				// Starting cell iteration
				while (cellNumber <= maxColNum) {

					if (cellIterator.hasNext()) {
						Cell cell = cellIterator.next();

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_NUMERIC: {
							/*
							 * Cell.getNumericCellValue return zero for empty
							 * cell
							 */
							tmpContent = String.valueOf(Math.round(cell.getNumericCellValue()));
							tmpContent = tmpContent.equals("0") ? "" : tmpContent;
							break;
						}
						case Cell.CELL_TYPE_BOOLEAN:
						case Cell.CELL_TYPE_STRING: {
							tmpContent = cell.getStringCellValue();
							break;
						}
						case Cell.CELL_TYPE_BLANK:
						case Cell.CELL_TYPE_ERROR:
						case Cell.CELL_TYPE_FORMULA:
						default:
							tmpContent = "";
						}

						if (cellNumber == fileDetails.getMsisdnColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							if (SMSUtils.validateLocalAddress(tmpContent)
									|| ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue() && SMSUtils
											.validateInternationalAddress(tmpContent))) {

								tmpContactParam.put("msisdn", tmpContent);
								validContactFlag = true;
							}
						} else if (cellNumber == fileDetails.getFnameColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("firstName", tmpContent);
						} else if (cellNumber == fileDetails.getLnameColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("lastName", tmpContent);
						} else if (cellNumber == fileDetails.getValue1ColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("value1", tmpContent);
						} else if (cellNumber == fileDetails.getValue2ColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("value2", tmpContent);
						} else if (cellNumber == fileDetails.getValue3ColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("value3", tmpContent);
						} else if (cellNumber == fileDetails.getValue4ColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("value4", tmpContent);
						} else if (cellNumber == fileDetails.getValue5ColNumber() && tmpContent != null
								&& !tmpContent.isEmpty()) {
							tmpContactParam.put("value5", tmpContent);
						}
					}
					++cellNumber;
				}// iterate over all cells of current row
				if (validContactFlag) {
					Contact contact = createContant(tmpContactParam, listId);
					validContactSet.add(contact);
					validation.incValidContacts();
				} else {
					validation.incInvalidContacts();
				}
			}// iterate over all rows of current sheet
		} else {
			try {
				String basePath = AppSettings.BaseDir.getEnvEntryValue();
				String origFilePath = FileNameUtils.decodeFileToken(fileDetails.getFileToken());
				String locPath = basePath + origFilePath;

				listLogger.debug(userTrxInfo.logId() + "Opening file " + locPath);
				OPCPackage pkg = OPCPackage.open(new File(locPath));
				XSSFReader reader = new XSSFReader(pkg);

				XLSXSheethandler sheetHandler = new XLSXSheethandler(fileDetails, validation, validContactSet);

				StylesTable styles = reader.getStylesTable();
				ReadOnlySharedStringsTable sharedStrings = new ReadOnlySharedStringsTable(pkg);
				ContentHandler handler = new XSSFSheetXMLHandler(styles, sharedStrings, sheetHandler, true);

				XMLReader parser = XMLReaderFactory.createXMLReader();
				parser.setContentHandler(handler);
				parser.parse(new InputSource(reader.getSheetsData().next()));
				// validContactSet = sheetHandler.getValidContactSet();
				pkg.close();
			} catch (OpenXML4JException e) {
				listLogger.error(userTrxInfo.logId() + "OpenXML4JException", e);

			} catch (SAXException e) {
				listLogger.error(userTrxInfo.logId() + "SAXException", e);

			}

		}
		listLogger.debug(userTrxInfo.logId() + "checking contacts validity. ");
		if (validation.getValidContacts() != 0 && validContactSet != null && !validContactSet.isEmpty()) {
			try {
				listLogger.debug(userTrxInfo.logId() + validContactSet.size()
						+ " valid contact(s) found, starting persisting contacts...");
				persistContactSet(userTrxInfo, validContactSet, listId);
				listLogger.debug(userTrxInfo.logId() + "persisting contacts success.");
				validation.setValidContacts(validContactSet.size());
			} catch (DBException e) {
				throw e;
			}
		} else {
			throw new InvalidFileException("File has no vaild contacts");
		}
		listLogger.info(userTrxInfo.logInfo() + "requesting parse excel file success.");
		return validation;
	}

	// === helper method to parse vcf file ===
	private FileValidation parseVCFContacts(UserTrxInfo userTrxInfo, FileDetails fileDetails, Integer listId)
			throws DBException, InvalidFileException, IOException {

		listLogger.info(userTrxInfo.logInfo() + "starting parsing vcf sheet...");

		FileValidation validation = new FileValidation();
		validation.setCreatedlistId(listId);
		Set<Contact> validContactSet = new HashSet<>();
		Map<String, String> tmpContactParam = new HashMap<>();
		// tmpContactParam.put("msisdn", "");
		// tmpContactParam.put("firstName", "");
		// tmpContactParam.put("lastname", "");

		listLogger.debug(userTrxInfo.logId() + "start parsing the file");
		String locPath = null;
		try {
			String basePath = AppSettings.BaseDir.getEnvEntryValue();
			String origFilePath = FileNameUtils.decodeFileToken(fileDetails.getFileToken());
			locPath = basePath + origFilePath;

			listLogger.debug(userTrxInfo.logId() + "Opening file " + locPath);
			VCardReader reader = new VCardReader(new File(locPath));
			VCard vcard = null;
			String telephone = null;
			while ((vcard = reader.readNext()) != null) {
				boolean validContactFlag = false;
				String tmpMsisdn = null;

				if (vcard.getTelephoneNumbers() != null && !vcard.getTelephoneNumbers().isEmpty()) {

					if (vcard.getFormattedName() != null) {
						String name = vcard.getFormattedName().getValue();
						String[] names = name.split(" ");
						// tmpFName = names[0];
						tmpContactParam.put("firstName", names[0]);

						if (names.length > 1) {
							// tmpLName = names[1];
							tmpContactParam.put("lastName", names[1]);

						}
					}

					for (Telephone tele : vcard.getTelephoneNumbers()) {
						if (tele.getParameters().getTypes().contains("PREF")) {
							tmpMsisdn = tele.getText();
							if (SMSUtils.validateLocalAddress(tmpMsisdn)) {
								validation.incValidContacts();
								validContactFlag = true;
								telephone = tele.getText();
							} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
									&& SMSUtils.validateInternationalAddress(tmpMsisdn)) {
								validation.incValidContacts();
								validContactFlag = true;
								telephone = tele.getText();
							} else {
								validation.incInvalidContacts();
							}
						}
					}

					if (telephone == null || telephone.trim().isEmpty()) {
						for (Telephone tele : vcard.getTelephoneNumbers()) {
							if (!tele.getParameters().getTypes().contains("PREF")) {
								if (SMSUtils.validateLocalAddress(tele.getText())) {
									validation.incValidContacts();
									validContactFlag = true;
									// tmpMsisdn = tele.getText();
									tmpContactParam.put("msisdn", tele.getText());
									break;
								} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
										&& SMSUtils.validateInternationalAddress(tele.getText())) {
									validation.incValidContacts();
									validContactFlag = true;
									// tmpMsisdn = tele.getText();
									tmpContactParam.put("msisdn", tele.getText());
									break;
								} else {
									validation.incInvalidContacts();
								}
							}
						}
					}

				}
				telephone = null;
				if (validContactFlag == true) {

					Contact contact = createContant(tmpContactParam, listId);
					if (contact != null)
						validContactSet.add(contact);
					else {
						validation.setValidContacts(validation.getValidContacts() - 1);
						validation.setInvalidContacts(validation.getInvalidContacts() + 1);
					}

				}// iterate over all cells of current row
			}
			reader.close();
		} catch (IOException e) {
			listLogger.error(userTrxInfo.logId() + "Error while parsing vcf file=" + locPath, e);
		} catch (Exception ex) {
			listLogger.error(userTrxInfo.logId() + "Error while parsing vcf file=" + locPath, ex);
		}

		listLogger.debug(userTrxInfo.logId() + "checking contacts validity. ");
		if (validation.getValidContacts() != 0 && validContactSet != null && !validContactSet.isEmpty()) {
			try {
				listLogger.debug(userTrxInfo.logId() + validContactSet.size()
						+ " valid contact(s) found, starting persisting contacts...");
				persistContactSet(userTrxInfo, validContactSet, listId);
				listLogger.debug(userTrxInfo.logId() + "persisting contacts success.");
				validation.setValidContacts(validContactSet.size());
			} catch (DBException e) {
				throw e;
			}
		} else {
			throw new InvalidFileException("File has no vaild contacts");
		}
		listLogger.info(userTrxInfo.logInfo() + "requesting parse vcf file success.");
		return validation;
	}

	// ===helper method to persist set of valid contacts ===
	private void persistContactSet(UserTrxInfo userTrxInfo, Set<Contact> validContactSet, int listId)
			throws DBException {

		int countChunk = 0;
		int chunkNum = 1;
		int chunkSize = (int) Configs.PERSIST_CONTACTS_CHUNK_SIZE.getValue();
		List<Future<Integer>> asyncoResultList = new ArrayList<>();
		List<Contact> contactsList = new ArrayList<>();

		listLogger.trace(userTrxInfo.logId() + "start persisting with chunk size: " + chunkSize
				+ ", the contacts count is: " + validContactSet.size());
		Iterator<Contact> contactIter = validContactSet.iterator();
		while (contactIter.hasNext()) {
			Contact contact = contactIter.next();
			if (contact != null) {
				contactsList.add(contact);
				countChunk++;
			}

			if (countChunk % chunkSize == 0) {
				try {
					listLogger.trace(userTrxInfo.logId() + "chunk no.:" + chunkNum + " will be persist");
					List<Contact> contactToBePersisted = new ArrayList<>(contactsList);
					Future<Integer> asyncoResult = contactDao.create(contactToBePersisted);
					asyncoResultList.add(asyncoResult);
					listLogger.trace(userTrxInfo.logId() + "chunk no.:" + chunkNum + " persisted");
					contactsList = new ArrayList<>();
					chunkNum++;
				} catch (DBException e) {
					throw e;
				}
			}
		}

		// for persist the last part
		if (contactsList.size() != 0) {
			try {
				listLogger.trace(userTrxInfo.logId() + "the last chunk(" + chunkNum + ") contain("
						+ contactsList.size() + "), will be persist");
				List<Contact> contactToBePersisted = new ArrayList<>(contactsList);
				Future<Integer> asyncoResult = contactDao.create(contactToBePersisted);
				asyncoResultList.add(asyncoResult);
				listLogger.trace(userTrxInfo.logId() + "final chunk:" + chunkNum + " persisted");
				contactsList.clear();

			} catch (DBException e) {
				throw e;
			}
		}
		listLogger.trace(userTrxInfo.logId() + "persisting finished successfully. ");
		listHandler.handleAsyncoResult(asyncoResultList, listId);

	}

	// === helper method create contact ===
	private Contact createContant(Map<String, String> contactParams, Integer listId) {
		String msisdn = contactParams.get("msisdn"), firstName = contactParams.get("firstName"), lastName = contactParams
				.get("lastName"), value1 = contactParams.get("value1"), value2 = contactParams.get("value2"), value3 = contactParams
				.get("value3"), value4 = contactParams.get("value4"), value5 = contactParams.get("value5");
		Contact contact = new Contact(listId, msisdn);
		if (firstName != null && !firstName.isEmpty())
			contact.setFirstName(firstName.length() <= maxChar ? firstName : firstName.substring(0, maxChar));
		if (lastName != null && !lastName.isEmpty())
			contact.setLastName(lastName.length() <= maxChar ? lastName : lastName.substring(0, maxChar));
		if (value1 != null && !value1.isEmpty())
			contact.setValue1(value1.length() <= maxChar ? value1 : value1.substring(0, maxChar));
		if (value2 != null && !value2.isEmpty())
			contact.setValue2(value2.length() <= maxChar ? value2 : value2.substring(0, maxChar));
		if (value3 != null && !value3.isEmpty())
			contact.setValue3(value3.length() <= maxChar ? value3 : value3.substring(0, maxChar));
		if (value4 != null && !value4.isEmpty())
			contact.setValue4(value4.length() <= maxChar ? value4 : value4.substring(0, maxChar));
		if (value5 != null && !value5.isEmpty())
			contact.setValue5(value5.length() <= maxChar ? value5 : value5.substring(0, maxChar));

		return contact;

	}

	// === helper method to define excel file type ===
	private Iterator<Row> excelTypesHandler(UserTrxInfo userTrxInfo, FileDetails fileDetails) throws IOException {
		Iterator<Row> rowIterator;

		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String origFilePath = FileNameUtils.decodeFileToken(fileDetails.getFileToken());
		String locPath = basePath + origFilePath;

		try {

			listLogger.debug(userTrxInfo.logId() + "Opening file " + locPath);
			BufferedInputStream fileInStream = new BufferedInputStream(new FileInputStream(new File(locPath)));

			if (fileDetails.getFileType().equalsIgnoreCase(FileType.XLSX.toString())) {

				//
				// listLogger.trace(userTrxInfo.logId() +
				// "getting xlsx file workbook instance"); // Get the workbook
				// instance for XLSX file XSSFWorkbook workbook = new
				// XSSFWorkbook(fileInStream);
				//
				// listLogger.trace(userTrxInfo.logId() +
				// "getting xlsx first sheet instance"); // Get first sheet from
				// the workbook XSSFSheet sheet = workbook.getSheetAt(0);
				//
				// listLogger.trace(userTrxInfo.logId() +
				// "getting xlsx first sheet iterator");
				//
				// // Get iterator to all the rows in current sheet rowIterator
				// = sheet.iterator();
				//

				listLogger.debug(userTrxInfo.logId() + "Using SAX parser in handling xlsx file.");
				fileInStream.close();
				listLogger.trace(userTrxInfo.logId() + "filestream has been closed.");
				return null;
			} else if (fileDetails.getFileType().equalsIgnoreCase(FileType.XLS.toString())) {

				listLogger.trace(userTrxInfo.logId() + "getting xls file workbook instance");
				// Get the workbook instance for XLS file
				HSSFWorkbook workbook = new HSSFWorkbook(fileInStream);

				listLogger.trace(userTrxInfo.logId() + "getting xls first sheet instance");
				// Get first sheet from the workbook
				HSSFSheet sheet = workbook.getSheetAt(0);

				// Get iterator to all the rows in current sheet
				rowIterator = sheet.iterator();
				fileInStream.close();
				listLogger.trace(userTrxInfo.logId() + "returning xls first sheet iterator");
				return rowIterator;

			}
			fileInStream.close();

		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw new IOException(e);
		}
		listLogger.error(userTrxInfo.logId() + "excel file not supported. ");
		return null;

	}

	// === helper method to parse CSV file ===
	private FileValidation parseCsvContacts(UserTrxInfo userTrxInfo, FileDetails fileDetails, Integer listId)
			throws IOException, InvalidFileException, DBException {

		listLogger.info(userTrxInfo.logInfo() + "starting csv contacts parsing ...");
		FileValidation validation = new FileValidation();
		ContactCSVRecord templateRecord = new ContactCSVRecord();

		String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String validFilePath = (String) Configs.IMPORTED_LISTS_FILES_PATH.getValue();
		String origFilePath = FileNameUtils.decodeFileToken(fileDetails.getFileToken());
		String origFileFullPath = basePath + origFilePath;
		String validFileFullPath = basePath + validFilePath + getUniqueFileName(listId.toString()) + "_valid.csv";

		Set<Contact> validContacts = new HashSet<>();
		String delim = (String) Configs.IMPORTED_LISTS_CSV_DELIM.getValue();
		String quoteChar = (String) Configs.IMPORTED_LISTS_CSV_QUOTE_CHAR.getValue();

		// the file itself
		File file = new File(origFileFullPath);
		// the new file with valid contacts only
		File validFile = new File(validFileFullPath);

		listLogger.debug(userTrxInfo.logId() + "Start processing " + origFileFullPath);

		// Preparing template csv record
		listLogger.debug(userTrxInfo.logId() + "File fields sequence: MSISDN[" + fileDetails.getMsisdnColNumber()
				+ "], First Name[" + fileDetails.getFnameColNumber() + "], Last Name["
				+ fileDetails.getLnameColNumber() + "], preparing csv record");
		templateRecord.getMSISDN().setFieldSeq(fileDetails.getMsisdnColNumber());
		templateRecord.getFirstName().setFieldSeq(fileDetails.getFnameColNumber());
		templateRecord.getLastName().setFieldSeq(fileDetails.getLnameColNumber());
		templateRecord.reRegisterFields();

		try {
			listLogger.debug(userTrxInfo.logId() + "Start validating the file");
			CSVBatchFile bf = new CSVBatchFile(templateRecord, file, delim, quoteChar);
			CSVBatchFile validBFile = new CSVBatchFile(templateRecord, validFile, delim, quoteChar,
					StandardOpenOption.CREATE);

			CSVFileValidationResult validationResult = bf.validateAndSplitFile(validBFile);

			validation.setInvalidContacts((int) validationResult.getInvalidRecords());
			validation.setValidContacts((int) validationResult.getValidRecoreds());
			validation.setTotalContacts((int) validationResult.getnRecords());

			listLogger.debug(userTrxInfo.logId() + validationResult.toString());

			validBFile.closeWriter();
			listLogger.debug(userTrxInfo.logId() + "Created new file of valid contacts, found valid contacts="
					+ validationResult.getValidRecoreds() + " contact");

			ContactCSVRecord record = (ContactCSVRecord) validBFile.readCSVRecord();

			listLogger.debug(userTrxInfo.logId() + "Start creating file for valid contacts. ");
			while (record != null && validation.getValidContacts() != 0) {
				Contact contact = new Contact();

				ListContactPK pk = new ListContactPK();
				pk.setMsisdn(record.getMSISDN().getValue());
				pk.setListId(listId);
				contact.setListContactsPK(pk);

				if (record.getFirstName() != null && !record.getFirstName().getValue().isEmpty()
						&& record.getFirstName().getValue().length() <= maxChar)
					contact.setFirstName(record.getFirstName().getValue());

				if (record.getLastName() != null && !record.getLastName().getValue().isEmpty()
						&& record.getLastName().getValue().length() <= maxChar)
					contact.setLastName(record.getLastName().getValue());

				listLogger.trace(userTrxInfo.logId() + contact + " added to valid file.");
				validContacts.add(contact);
				record = (ContactCSVRecord) validBFile.readCSVRecord();

			}
			bf.close();
			validBFile.closeReader();

		} catch (IOException e) {
			throw e;
		}
		try {
			if (validation.getValidContacts() != 0 && validContacts != null && !validContacts.isEmpty()) {
				listLogger.debug(userTrxInfo.logId() + validContacts.size() + " Record(s) are going to persist. ");
				persistContactSet(userTrxInfo, validContacts, listId);
				listLogger.debug(userTrxInfo.logId() + "Records persisted successfully. ");
				validation.setValidContacts(validContacts.size());

			} else {
				throw new InvalidFileException("File has no vaild contacts");

			}
		} catch (DBException e) {
			throw e;
		}
		listLogger.info(userTrxInfo.logId() + "csv file parsed and persisted to DB successfully.");
		validation.setCreatedlistId(listId);
		return validation;
	}

	private String getUniqueFileName(String postfix) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HH-mm-ss");
		return df.format(new Date()) + "_" + postfix;
	}

	private void updateContactCSVRecord(Contact contact, ContactCSVRecord csvRecord) {
		csvRecord.getMSISDN().setValue(contact.getListContactsPK().getMsisdn());
		csvRecord.getFirstName().setValue(contact.getFirstName());
		csvRecord.getLastName().setValue(contact.getLastName());
	}

	private CSVBatchFile openContactCSV(CSVRecord csvLog, String outPathName, String outFileBaseName, String delimiter,
			String quoteChar, Charset charset, boolean compressed) throws IOException {
		CSVBatchFile batchFile;
		String outFileName = null;
		if (compressed) {
			outFileName = outFileBaseName + ".zip";
			FileOutputStream fos = new FileOutputStream(outPathName + outFileName);
			ZipOutputStream zos = new ZipOutputStream(fos);
			// add a new Zip Entry to the ZipOutputStream
			ZipEntry ze = new ZipEntry(outFileBaseName + ".csv");
			zos.putNextEntry(ze);
			batchFile = new CSVBatchFile(csvLog, null, (OutputStream) zos, delimiter, quoteChar, charset);
		} else {
			outFileName = outFileBaseName + ".csv";
			Path outFilePath = Paths.get(outPathName + outFileName);
			batchFile = new CSVBatchFile(csvLog, outFilePath.toFile(), delimiter, quoteChar, charset,
					StandardOpenOption.CREATE);
		}
		batchFile.setFileName(outFileName);
		return batchFile;
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void handleIntraList(UserTrxInfo userTrxInfo) throws DBException, InterruptedException, ExecutionException,
			ListNotFoundException, IntraListInquiryFailed, InvalidCustomerForQuotaInquiry {

		userTrxInfo.addUserAction(ActionName.CREATE_LIST);
		listLogger.info(userTrxInfo.logInfo() + "Handling intralist");
		UserModel user = userTrxInfo.getUser();
		ListType listType = listTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST);

		List<ContactList> intraLists;
		List<Contact> intraListContacts;

		listLogger.debug(userTrxInfo.logId() + "searching for IntraList.");
		intraLists = contactListDao.findByAccountIdAndType(user.getAccountId(), listType);
		listLogger.debug(userTrxInfo.logId() + "found " + intraLists != null ? intraLists.size() : 0 + " intraList.");

		if (intraLists != null && !intraLists.isEmpty()) {
			try {
				intraListManagementBean.emptyIntraList(userTrxInfo);
			} catch (ListNotFoundException e) {
				intraListManagementBean.removeIntraSubLists(userTrxInfo);
				throw e;
			}
			intraListContacts = intraListManagementBean.fetchIntraListContacts(userTrxInfo, intraLists.get(0)
					.getListId());
			if (intraListContacts != null)
				intraListManagementBean.updateIntraSubLists(userTrxInfo, intraListContacts);
			else
				throw new IntraListInquiryFailed("Intra List is empty, cann't update sub intra lists.");

		} else {
			// Should create intra list firstly using intralist management bean.
			listLogger.debug(userTrxInfo.logId() + "Intra list doesn't exist, creating new Intra list");
			int listId = intraListManagementBean.createIntraList(userTrxInfo);
			listLogger.debug(userTrxInfo.logId() + "listId : " + listId);
			intraListContacts = intraListManagementBean.fetchIntraListContacts(userTrxInfo, listId);
		}

	}

	/*
	 * @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW) public
	 * List<Contact> fetchIntraListContacts(UserTrxInfo userTrxInfo, int listId)
	 * throws DBException, InterruptedException, ExecutionException,
	 * EmptyIntraListFoundException {
	 * userTrxInfo.addUserAction(UserActionName.REFRESH_LIST);
	 * 
	 * AccManagUserModel user = userTrxInfo.getUser(); List<Contact> newContactList =
	 * new ArrayList<Contact>(); try { // calling intra list stored procedure
	 * List<String> contacts =
	 * intraListInquiryDao.inquireIntraList(user.getAccountId());
	 * 
	 * if (contacts == null || contacts.isEmpty()) {
	 * listLogger.error(userTrxInfo.logId() + "Empty IntraList found"); throw
	 * new EmptyIntraListFoundException(user.getAccountId()); }
	 * 
	 * 
	 * for (String msisdn : contacts) { Contact contact = new Contact();
	 * ListContactPK listContactPk = new ListContactPK();
	 * listContactPk.setListId(listId); if
	 * (SMSUtils.validateLocalAddress(msisdn)) { if ((boolean)
	 * Configs.ALLOW_INTERNATOINAL_SMS.getValue() &&
	 * SMSUtils.validateInternationalAddress(msisdn)) {
	 * listContactPk.setMsisdn(msisdn); } }
	 * contact.setListContactsPK(listContactPk); newContactList.add(contact); }
	 * 
	 * // create contacts after eliminating the duplication Set<Contact>
	 * newContacts = new HashSet<Contact>(newContactList);
	 * listLogger.debug(userTrxInfo.logId() + "List(" + listId + ") has " +
	 * newContacts.size() + " contact(s) to be persisted"); newContactList = new
	 * ArrayList<Contact>(newContacts);
	 * 
	 * Future<Integer> confirmedCount = contactDao.create(newContactList);
	 * 
	 * listLogger.debug(userTrxInfo.logId() + confirmedCount.get() +
	 * " Contact(s) persisted successfully in " + "List(" + listId + ").");
	 * 
	 * } catch (IntraListInquiryFailed e) { listLogger.error(userTrxInfo.logId()
	 * + "Error while inquiring intra list ", e); } catch
	 * (InvalidCustomerForQuotaInquiry e) { listLogger.error(userTrxInfo.logId()
	 * + "Error, could't  inquire intra list for user=" + user.getAccountId() +
	 * " and name=" + user.getUsername(), e); } catch (DBException e) {
	 * listLogger.error(userTrxInfo.logId() +
	 * "DBException while creaing intra list in the dataBase ", e); throw e; }
	 * catch (InterruptedException e) { listLogger.error(userTrxInfo.logId() +
	 * "InterruptedException while creaing intra list in the dataBase ", e);
	 * throw e; } catch (ExecutionException e) {
	 * listLogger.error(userTrxInfo.logId() +
	 * "ExecutionException while creaing intra list in the dataBase ", e); throw
	 * e; } if (newContactList != null && !newContactList.isEmpty()) return
	 * newContactList; listLogger.error(userTrxInfo.logId() +
	 * "found empty contact list"); throw new
	 * EmptyIntraListFoundException(user.getAccountId()); }
	 */
}
