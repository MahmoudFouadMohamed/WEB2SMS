/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.ScheduleFrequency;

/**
 * 
 * @author yyaseen
 */
@Local
public interface ScheduleFrequencyDaoLocal extends
		Cachable<ScheduleFrequency, ScheduleFrequencyName> {

	void create(ScheduleFrequency scheduleFrequency) throws DBException;

	void edit(ScheduleFrequency scheduleFrequency) throws DBException;

	void remove(ScheduleFrequency scheduleFrequency) throws DBException;

	ScheduleFrequency find(Object id) throws DBException;

	List<ScheduleFrequency> findAll() throws DBException;

	List<ScheduleFrequency> findRange(int[] range) throws DBException;

	int count() throws DBException;

	ScheduleFrequency findByScheduleFreqName(String scheduleFreqName)
			throws DBException;

}
