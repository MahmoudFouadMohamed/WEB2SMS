package com.edafa.web2sms.service.admin.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Language;
import com.edafa.web2sms.dalayer.model.Template;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.template.exception.InvalidTemplateException;
import com.edafa.web2sms.service.template.exception.TemplatesNotFoundException;

@Local
public interface AdminTemplateManagementBeanLocal {

	void createTemplate(AdminTrxInfo adminTrxInfo, Template template) throws InvalidTemplateException, DBException;

	void updateTemplate(AdminTrxInfo adminTrxInfo, Template template) throws TemplatesNotFoundException, DBException, InvalidTemplateException;

	void deleteTemplate(AdminTrxInfo adminTrxInfo, Integer templateId) throws TemplatesNotFoundException, DBException;

	Template viewTemplate(AdminTrxInfo adminTrxInfo, Integer TemplateId) throws DBException, TemplatesNotFoundException;

	List<Template> viewTemplate(AdminTrxInfo adminTrxInfo, int first, int range) throws TemplatesNotFoundException, DBException;

	int count(AdminTrxInfo adminTrxInfo) throws DBException;

	Language getLanguageByName(AdminTrxInfo adminTrxInfo, LanguageNameEnum language) throws DBException;

}
