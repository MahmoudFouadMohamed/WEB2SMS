package com.edafa.web2sms.service.list.interfaces;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.service.list.files.FileDetails;
import com.edafa.web2sms.service.list.model.ContactListInfoResultSet;
import com.edafa.web2sms.service.list.model.ContactListResultSet;
import com.edafa.web2sms.service.list.model.ContactResultSet;
import com.edafa.web2sms.service.list.model.FileResult;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactListModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.FileTokenResult;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.model.UserTrxInfo;

@WebService(name = "ListsManegementService", portName = "ListsManegementServicePort", targetNamespace = "http://www.edafa.com/web2sms/service/lists")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
public interface ListsManegementService {

	@WebMethod(operationName = "getContactListsInfo")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListInfoResultSet getContactListsInfo(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListTypeNames", partName = "listTypeNames") List<ListTypeName> listTypeNames);
	
	@WebMethod(operationName = "getContactListsInfoWithFlag")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListInfoResultSet getContactListsInfoWithFlag(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListTypeNames", partName = "listTypeNames") List<ListTypeName> listTypeNames);

	@WebMethod(operationName = "countContactListsInfo")
	@WebResult(name = "ContactListsCounter", partName = "contactListsCounter")
	public int countContactListsInfo(@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListTypeNames", partName = "listTypeNames") List<ListTypeName> listTypeNames);

	@WebMethod(operationName = "getContactListsInfobyCampId")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListInfoResultSet getContactListsInfobyCampId(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "CampId", partName = "campId") String campId);

	@WebMethod(operationName = "getContactListsInfoPagination")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListInfoResultSet getContactListsInfoPagination(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "firstIndex", partName = "firstIndex") int first,
			@WebParam(name = "max", partName = "max") int max,
			@WebParam(name = "ListTypeNames", partName = "listTypeNames") List<ListTypeName> listTypeNames);
	
	
	@WebMethod(operationName = "getContactListsInfoPaginationWithFlag")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListInfoResultSet getContactListsInfoPaginationWithFlag(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "firstIndex", partName = "firstIndex") int first,
			@WebParam(name = "max", partName = "max") int max,
			@WebParam(name = "ListTypeNames", partName = "listTypeNames") List<ListTypeName> listTypeNames);



	@WebMethod(operationName = "getContactList")
	@WebResult(name = "ContactModelResultSet", partName = "contactModelResultSet")
	public ContactResultSet getContactList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "listId", partName = "listId") int listId);

	@WebMethod(operationName = "getContactListWithPagination")
	@WebResult(name = "ContactModelResultSet", partName = "contactModelResultSet")
	public ContactResultSet getContactListWithPagination(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "listId", partName = "listId") int listId,
			@WebParam(name = "firstIndex", partName = "firstIndex") int firstIndex,
			@WebParam(name = "maxIndex", partName = "maxIndex") int maxIndex);
	
	@WebMethod(operationName = "searchLists")
	@WebResult(name = "ContactLists", partName = "contactLists")
	public ContactListResultSet searchLists(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListName", partName = "listName") String listName,
			@WebParam(name = "ListTypeName", partName = "listTypeNames") List<ListTypeName> listTypeNames);

	@WebMethod(operationName = "createNewList")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus createNewList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ContactsList", partName = "contactsList") ContactListModel contactsList);
	
	@WebMethod(operationName = "createNewListWithFlag")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus createNewListWithFlag(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ContactsList", partName = "contactsList") ContactListModel contactsList);

	@WebMethod(operationName = "handleVirtualList")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus handleVirtualList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
	@WebParam(name = "ContactsList", partName = "contactsList") List<ContactModel> contactsList);
	@WebMethod(operationName = "copyToNewList")
	@WebResult(name = "CopyToNewList", partName = "copyToNewList")
	public ResultStatus copyToNewList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListInfo", partName = "listInfo") ContactListInfoModel contactsList,
			@WebParam(name = "OldListId", partName = "oldListId") int oldListId);

	@WebMethod(operationName = "createNewListFromFile")
	@WebResult(name = "FileResult", partName = "fileResult")
	public FileResult createNewListFromFile(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "FileDetails", partName = "fileDetails") FileDetails fileDetails);

	@WebMethod(operationName = "expandContactList")
	@WebResult(name = "ContactResultSet", partName = "contactResultSet")
	public ContactResultSet expandContactList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ContactsList", partName = "contactsList") ContactListModel contactsList);

	@WebMethod(operationName = "deleteSubContactList")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus deleteSubContactList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ContactsList", partName = "contactsList") ContactListModel contactsList);

	@WebMethod(operationName = "deleteContactList")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus deleteContactList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ContactsList", partName = "contactsList") ContactListInfoModel contactsInfoList);

	// @WebMethod(operationName = "exportListToFile")
	// // @WebResult(name = "FileDetails", partName = "fileDetails")
	// public void exportListToFile(@WebParam(name = "RequestInfo", partName =
	// "requestInfo") UserTrxInfo userTrxInfo,
	// @WebParam(name = "ListId", partName = "listId") Integer listId);

	@WebMethod(operationName = "exportListToCSVFile")
	@WebResult(name = "FileToken", partName = "fileToken")
	public FileTokenResult exportListToCsvFile(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListId", partName = "listId") Integer listId);

	@WebMethod(operationName = "searchContacts")
	@WebResult(name = "ContactResultSet", partName = "contactResultSet")
	public ContactResultSet searchContacts(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "MSISDN", partName = "msisdn") String contact,
			@WebParam(name = "ListId", partName = "listId") Integer listId);

	@WebMethod(operationName = "editContactListName")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus editContactListName(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "ListId", partName = "listId") Integer listId,
			@WebParam(name = "NewListName", partName = "NewListName") String newListName);

	@WebMethod(operationName = "editContact")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus editContacts(@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo,
			@WebParam(name = "UpdatedContact", partName = "updatedContact") ContactModel contact,
			@WebParam(name = "MSISDN", partName = "msisdn") String msisdn,
			@WebParam(name = "ListId", partName = "listId") Integer listId);

//	@WebMethod(operationName = "createIntraList")
//	@WebResult(name = "ResultStatus", partName = "resultStatus")
//	public ResultStatus createIntraList(
//			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo);

	@WebMethod(operationName = "handleIntraList")
	@WebResult(name = "ResultStatus", partName = "resultStatus")
	public ResultStatus handleIntraList(
			@WebParam(name = "RequestInfo", partName = "requestInfo") UserTrxInfo userTrxInfo);

	

	
	

}
