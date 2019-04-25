package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.Module;

@Local
public interface ModuleDaoLocal extends Cachable<Module, String> {
	void create(Module module) throws DBException;

	void edit(Module module) throws DBException;

	void remove(Module module) throws DBException;

	Module find(Object id) throws DBException;

	List<Module> findAll() throws DBException;

	List<Module> findRange(int[] range) throws DBException;

	int count() throws DBException;

}
