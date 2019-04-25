package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.BindTypeEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SMSCBindType;

@Local
public interface SMSCBindTypeDaoLocal {
	void create(SMSCBindType bindType) throws DBException;

	void edit(SMSCBindType bindType) throws DBException;

	void remove(SMSCBindType bindType) throws DBException;

	SMSCBindType find(Object id) throws DBException;

	List<SMSCBindType> findAll() throws DBException;

	List<SMSCBindType> findRange(int[] range) throws DBException;

	int count() throws DBException;

	SMSCBindType findById(short id) throws DBException;

	SMSCBindType findByType(BindTypeEnum type) throws DBException;
}
