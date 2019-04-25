/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;

/**
 * 
 * @author yyaseen
 */
@Local
public interface ContactListDaoLocal {

	void create(ContactList lists) throws DBException;

	void edit(ContactList lists) throws DBException;

	void remove(ContactList lists) throws DBException;

	ContactList find(Object id) throws DBException;

	List<ContactList> findAll() throws DBException;

	List<ContactList> findRange(int[] range) throws DBException;

	int count() throws DBException;

	ContactList findByListName(String name) throws DBException;

	List<ContactList> findByAccountId(String accountId) throws DBException;

	String getListIdByName(String listName) throws DBException;

	long countContacts(List<Integer> lists) throws DBException;

	long countContacts(Integer listId) throws DBException;

	void createUsingNewTx(ContactList entity) throws DBException;

	Future<Integer> removeByListId(Integer listId) throws DBException;

	public int count(Object id) throws DBException;

	List<ContactList> findListsByCampaignId(String campId) throws DBException;

	ContactList findByListIdAndAccountId(int listId, String accountId) throws DBException;

	int countByListIdAndAccountId(String name, String accountId) throws DBException;

	int countByListNameAndAccountId(String listName, String accountId) throws DBException;

	int updateListName(int listId, String listName, String accountId) throws DBException;

	int counByAccountIdAndType(String accountId, ListType listType) throws DBException;

	int counByAccountIdAndTypes(String accountId, List<ListType> listTypes) throws DBException;

	List<ContactList> findByAccountIdAndTypeWithCounts(String accountId, ListType listType) throws DBException;

	List<ContactList> findByAccountIdAndTypesWithCounts(String accountId, List<ListType> listTypes) throws DBException;

	List<ContactList> findByAccountIdAndTypeWithCounts(String accountId, ListType listType, int first, int max)
			throws DBException;

	List<ContactList> findByAccountIdAndTypesWithCounts(String accountId, List<ListType> listTypes, int first, int max)
			throws DBException;

	List<ContactList> findByAccountIdAndType(String accountId, ListType listType, int first, int max)
			throws DBException;

	List<ContactList> findByAccountIdAndType(String accountId, ListType listType) throws DBException;

	List<ContactList> findByAccountIdAndTypes(String accountId, List<ListType> listType) throws DBException;

	List<ContactList> searchByListNameAndType(String listName, ListType listType, String accountId) throws DBException;

	List<ContactList> searchByListNameAndTypeWithCounts(String listName, List<ListType> listType, String accountId)
			throws DBException;

	List<ContactList> findByAccountIdAndTypes(String accountId, List<ListType> listTypes, int first, int max)
			throws DBException;

	ContactList findByListNameAndAccountId(String listName, String accountId) throws DBException;

	List<ContactList> findByListIds(List<Integer> listIds) throws DBException;

	ContactList findByListId(int listId) throws DBException;
	
	Integer findByListNameAndAccountIdNativeSql(String listName, String accountId) throws DBException;

}
