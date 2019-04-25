package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;
import java.util.concurrent.Future;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.SMSStatus;

@Local
public interface ContactDaoLocal {
	List<Contact> findByListID(int listId) throws DBException;

	public void create(Contact entity) throws DBException ;
	
//	public void create(List<Contact> list) throws DBException;

	public void edit(Contact entity) throws DBException ;
	public void remove(Contact entity) throws DBException ;

	public Contact find(Object id) throws DBException ;

	public List<Contact> findAll() throws DBException ;

	public List<Contact> findRange(int[] range) throws DBException ;

	public List<Contact> findRange(int frist, int max, String order)
			throws DBException ;
	
	public List<Contact> findRange(int frist, int max, String order,List<Contact> list,String condition)
			throws DBException;

	public int count() throws DBException;

	List<Contact> findByListID(int listId, int first, int max)
			throws DBException;

	public Future<Integer> create(List<Contact> list) throws DBException;
	

	List<Contact> searchContacts(String contact,Integer listId) throws DBException;

	public int count(Object id) throws DBException ;

	public int copyList(int newListId, int oldListId) throws DBException;

	public Integer isContactExist(String msisdn, int listId) throws DBException;

	public int updateContact(Contact contact, String msisdn) throws DBException;

	long countContactInList(Integer listId) throws DBException;

	Future<Integer> removeByListId(Integer listId) throws DBException;

	List<Contact> findContactsByCampaignIdAndStatus(String campaignId, List<SMSStatus> status) throws DBException;

	List<Contact> findDiffContancts(Integer newSubListId, Integer internalListId) throws DBException;
	
//	int deteteContactsFromList(Integer listId, List<Contact> contactsToRemove) throws DBException;

	int findDifferenceAndDelete(Integer newSubListId, Integer internalListId) throws DBException;
	
}
