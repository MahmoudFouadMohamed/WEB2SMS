package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;
import java.util.Map;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ErrorDefinitionEntity;
import com.edafa.web2sms.dalayer.model.Module;

public interface ErrorDefinitionDaoLocal {
	void create(ErrorDefinitionEntity errorDefinitionEntity) throws DBException;

	void edit(ErrorDefinitionEntity errorDefinitionEntity) throws DBException;

	void remove(ErrorDefinitionEntity errorDefinitionEntity) throws DBException;

	ErrorDefinitionEntity find(Object id) throws DBException;

	List<ErrorDefinitionEntity> findAll() throws DBException;

	List<ErrorDefinitionEntity> findRange(int[] range) throws DBException;

	int count() throws DBException;

	List<ErrorDefinitionEntity> findByModule(Module module) throws DBException;

	List<ErrorDefinitionEntity> findByModules(List<Module> modules) throws DBException;

	Map<String, List<ErrorDefinitionEntity>> findModulesErrorDefinition() throws DBException;
}
