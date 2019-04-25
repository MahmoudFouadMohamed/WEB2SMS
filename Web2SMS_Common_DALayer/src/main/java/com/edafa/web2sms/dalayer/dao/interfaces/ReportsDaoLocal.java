package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Reports;

/**
*
* @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
*/
@Local
public interface ReportsDaoLocal {

	public void create(Reports entity) throws DBException;
	public void edit(Reports entity) throws DBException;
	public void remove(Reports entity) throws DBException;
	public Reports find(Object id) throws DBException;
	public List<Reports> findAll() throws DBException;
	public Reports refresh(Reports entity) throws DBException;

	public int countAdminReports() throws DBException;
	public int countUserReports(String ownerId) throws DBException;

	public List<Reports> getAdminReports(int start, int count) throws DBException;
	public List<Reports> getUserReports(String ownerId, int start, int count) throws DBException;

	public List<Reports> getPendingReports() throws DBException;
	public List<Reports> getFailedReports() throws DBException;

}
