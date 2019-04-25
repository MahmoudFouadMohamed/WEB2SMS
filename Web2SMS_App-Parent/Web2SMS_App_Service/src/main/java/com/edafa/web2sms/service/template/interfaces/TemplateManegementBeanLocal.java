package com.edafa.web2sms.service.template.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.model.TemplateModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.template.exception.InvalidTemplateException;
import com.edafa.web2sms.service.template.exception.TemplatesNotFoundException;

@Local
public interface TemplateManegementBeanLocal {

	List<TemplateModel> getUserTemplates(UserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException;

	List<TemplateModel> getUserAndAdminTemplates(UserTrxInfo userTrxInfo) throws DBException,
			IneligibleAccountException, TemplatesNotFoundException;

	void createTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) throws DBException,
			IneligibleAccountException, InvalidTemplateException;

	void deleteTemplate(UserTrxInfo userTrxInfo, Integer templateId) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException;

	void updateTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) throws DBException,
			IneligibleAccountException, TemplatesNotFoundException, InvalidTemplateException;

	List<TemplateModel> getAdminTemplates(UserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException;

}
