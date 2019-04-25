/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.Language;

/**
 * 
 * @author yyaseen
 */
@Local
public interface LanguageDaoLocal extends Cachable<Language, LanguageNameEnum> {

	void create(Language languages) throws DBException;

	void edit(Language languages) throws DBException;

	void remove(Language languages) throws DBException;

	Language find(Object id) throws DBException;

	List<Language> findAll() throws DBException;

	List<Language> findRange(int[] range) throws DBException;

	int count() throws DBException;

	Language findByName(LanguageNameEnum name) throws DBException;

}
