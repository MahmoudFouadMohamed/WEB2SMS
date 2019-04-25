package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.IntraSender;

@Local
public interface IntraSenderDaoLocal {
	@TransactionAttribute(TransactionAttributeType.SUPPORTS)
	void create(IntraSender intraSenders) throws DBException;

	void edit(IntraSender intraSenders) throws DBException;

	void remove(IntraSender intraSenders) throws DBException;

	IntraSender find(Object id) throws DBException;

	List<IntraSender> findAll() throws DBException;

	List<IntraSender> findRange(int[] range) throws DBException;

	public List<IntraSender> findRange(int frist, int max, String order) throws DBException;

	int count() throws DBException;

	int count(Object id) throws DBException;
	
	List<IntraSender> findSystemIntraSendersList() throws DBException;

	int countIntraSender(String intraSender) throws DBException;

	IntraSender findIntraSenderByName(String senderName) throws DBException;
}
