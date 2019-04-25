package com.edafa.web2sms.service.template;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.LanguageDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.TemplateDaoLocal;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.Language;
import com.edafa.web2sms.dalayer.model.Template;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.admin.interfaces.AdminTemplateManagementBeanLocal;
import com.edafa.web2sms.service.conversoin.TemplateConversionBean;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.TemplateModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.service.template.exception.InvalidTemplateException;
import com.edafa.web2sms.service.template.exception.TemplatesNotFoundException;
import com.edafa.web2sms.service.template.interfaces.TemplateManegementBeanLocal;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class TemplateManegementBean
 */
@Stateless
@LocalBean
public class TemplateManegementBean implements TemplateManegementBeanLocal, AdminTemplateManagementBeanLocal {

	Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger templateLogger = LogManager.getLogger(LoggersEnum.TEMPLATE_MNGMT.name());

	@EJB
	private TemplateDaoLocal templateDao;

	@EJB
	private TemplateConversionBean templateConversionBean;

	@EJB
	AccountManegementFacingLocal accountManagement;

	@EJB
	private LanguageDaoLocal languageDao;

	/**
	 * Default constructor.
	 */
	public TemplateManegementBean() {

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TemplateModel> getUserTemplates(UserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException, TemplatesNotFoundException {

		List<TemplateModel> result;
		userTrxInfo.addUserAction(ActionName.VIEW_TEMPLATES);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + " account is eligible");

			List<Template> templateList = templateDao.findByAccountId(userTrxInfo.getUser().getAccountId());

//			List<Template> templateList = templateDao.findByUserAndAdmin(userTrxInfo.getUser().getAccountId());

			if (templateList == null) {

				throw new TemplatesNotFoundException(userTrxInfo);
			} else {
				templateLogger.debug(userTrxInfo.logId() + " converting templates to templatesModel");
				result = templateConversionBean.getTemplateModel(templateList);
			}
		} catch (DBException e) {

			throw e;
		}
		templateLogger.debug(userTrxInfo.logId() + "success requesting VIEW_TEMPLATE");
		return result;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TemplateModel> getUserAndAdminTemplates(UserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException {

		List<TemplateModel> result;
		userTrxInfo.addUserAction(ActionName.VIEW_TEMPLATES);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + " account is eligible");

			List<Template> templateList = templateDao.findByUserAndAdmin(userTrxInfo.getUser().getAccountId());

			if (templateList == null) {

				throw new TemplatesNotFoundException(userTrxInfo);
			} else {
				templateLogger.debug(userTrxInfo.logId() + " converting templates to templatesModel");
				result = templateConversionBean.getTemplateModel(templateList);
			}
		} catch (DBException e) {

			throw e;
		}
		templateLogger.debug(userTrxInfo.logId() + "success requesting VIEW_TEMPLATE");
		return result;

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void createTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) throws DBException, IneligibleAccountException,
			InvalidTemplateException {

		userTrxInfo.addUserAction(ActionName.CREATE_TEMPLATE);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + " account is eligible");

			templateLogger.debug(userTrxInfo.logId() + "creating new template");

			Template template = templateConversionBean.getTemplate(templateModel);
			List <Account> accounts = new ArrayList<Account> ();
			accounts.add(accountManagement.getAccount(userTrxInfo.getUser().getAccountId()));
			template.setAccountsList(accounts);

			templateLogger.debug(userTrxInfo.logId() + "persisting new template into database");

			if (template.isvalid()) {
				templateDao.create(template);
			} else {
				throw new InvalidTemplateException(template);
			}
			templateLogger.info(userTrxInfo.logId() + "Success creating Template.");

		} catch (DBException e) {

			throw e;

		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteTemplate(UserTrxInfo userTrxInfo, Integer templateId) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException {
		userTrxInfo.addUserAction(ActionName.DELETE_TEMPLATE);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + "checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + "account is eligible");

			Template tmp = templateDao.find(templateId);
			if (tmp != null) {

				templateLogger.debug(userTrxInfo.logId() + "removing " + tmp);

				templateDao.remove(tmp);
			} else {

				throw new TemplatesNotFoundException(templateId);
			}

			templateLogger.info(userTrxInfo.logId() + "Success deleting Template.");

		} catch (DBException e) {

			throw e;
		}

	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void updateTemplate(UserTrxInfo userTrxInfo, TemplateModel templateModel) throws DBException, IneligibleAccountException,
			TemplatesNotFoundException, InvalidTemplateException {

		userTrxInfo.addUserAction(ActionName.EDIT_TEMPLATE);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + " account is eligible");

			if (templateDao.count(templateModel.getTemplateId()) == 1) {

				
				Template tmp = templateDao.find(templateModel.getTemplateId());
				List <Account> accounts = new ArrayList<Account> ();
				accountManagement.findAccountById(userTrxInfo.getAccountTrxInfo(), userTrxInfo.getUser().getAccountId());
				accounts.add(accountManagement.getAccount(userTrxInfo.getUser().getAccountId()));
				tmp.setAccountsList(accounts);
//				tmp.setAccount(accountManagement.getAccount(userTrxInfo.getUser().getAccountId()));

				if (tmp.getSystemTemplateFlag()) {

					TemplateModel newTmp = templateConversionBean.getTemplateModel(tmp);
					newTmp.setTemplateType(false);
					createTemplate(userTrxInfo, newTmp);

				} else {

					if (templateModel.getTemplateName() != null) {
						tmp.setTemplateName(templateModel.getTemplateName());
						templateLogger.debug(userTrxInfo.logId() + "template name updated to: " + templateModel.getTemplateName());
					}
					if (templateModel.getText() != null) {
						tmp.setText(templateModel.getText());
						templateLogger.debug(userTrxInfo.logId() + "template text updated to: " + templateModel.getText());
					}
					if (templateModel.getLanguage() != null) {
						tmp.setLanguage(findLanguageByName(templateModel.getLanguage()));
						templateLogger.debug(userTrxInfo.logId() + "template  language updated to: " + templateModel.getLanguage());
					}

					templateDao.edit(tmp);

				}
			} else {
				throw new TemplatesNotFoundException(templateModel.getTemplateId());
			}

			templateLogger.info(userTrxInfo.logId() + "Success editing Template.");

		} catch (DBException e) {

			throw e;
		} catch (AccountNotFoundException e) {
			// TODO 
//			throw e;
		}
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<TemplateModel> getAdminTemplates(UserTrxInfo userTrxInfo) throws DBException, IneligibleAccountException, TemplatesNotFoundException {

		List<TemplateModel> result;
		userTrxInfo.addUserAction(ActionName.VIEW_ADMIN_TEMPLATES);

		templateLogger.info(userTrxInfo.logInfo());
		try {

			templateLogger.debug(userTrxInfo.logId() + " checking account eligibility for action: " + userTrxInfo.getUserActions());

			accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
			templateLogger.debug(userTrxInfo.logId() + " account is eligible");

			List<Template> templateList = templateDao.findAdminTemplates();

			if (templateList == null) {
				throw new TemplatesNotFoundException();
			} else {
				result = templateConversionBean.getTemplateModel(templateList);
			}
		} catch (DBException e) {

			throw e;
		}
		templateLogger.debug(userTrxInfo.logId() + "Success retreiving admin templates");
		return result;
	}

	@Override
	public void createTemplate(AdminTrxInfo adminTrxInfo, Template template) throws InvalidTemplateException, DBException {
		templateLogger.info(adminTrxInfo.logInfo());
		try {

			templateLogger.debug(adminTrxInfo.logId() + "creating new template");

			if (template.isvalid()) {
				templateDao.create(template);
			} else {
				throw new InvalidTemplateException(template);
			}

			templateLogger.info(adminTrxInfo.logId() + "Success creating Template.");

		} catch (DBException e) {

			throw e;

		}

	}

	@Override
	public void updateTemplate(AdminTrxInfo adminTrxInfo, Template template) throws TemplatesNotFoundException, DBException, InvalidTemplateException {

		templateLogger.info(adminTrxInfo.logInfo());
		try {

//			if (templateDao.count(template.getTemplateId()) == 1) {
//
//				Template tmp = templateDao.find(template.getTemplateId());
//
//				if (template.getTemplateName() != null) {
//					tmp.setTemplateName(template.getTemplateName());
//					templateLogger.debug(adminTrxInfo.logId() + "template name updated to: " + template.getTemplateName());
//				}
//				if (template.getText() != null) {
//					tmp.setText(template.getText());
//					templateLogger.debug(adminTrxInfo.logId() + "template test updated to: " + template.getText());
//				}
//
//				templateDao.edit(tmp);
			Template temp = templateDao.find(template.getTemplateId());
			if(temp!=null )
			{
				if(template.getTemplateName()!=null && !template.getTemplateName().equals("")&& template.getText() != null && !template.getText().equals(""))
				{
					templateDao.edit(template);
				}
				else 
					throw new InvalidTemplateException(template);
			} else {
				throw new TemplatesNotFoundException(template.getTemplateId());
			}

			templateLogger.info(adminTrxInfo.logId() + "Success editing Template.");

		} catch (DBException e) {

			throw e;
		}

	}

	@Override
	public void deleteTemplate(AdminTrxInfo adminTrxInfo, Integer templateId) throws TemplatesNotFoundException, DBException {
		templateLogger.info(adminTrxInfo.logInfo());
		try {
			Template tmp = templateDao.find(templateId);
			if (tmp != null) {

				templateLogger.debug(adminTrxInfo.logId() + "removing " + tmp);

				templateDao.remove(tmp);
			} else {

				throw new TemplatesNotFoundException(templateId);
			}

			templateLogger.info(adminTrxInfo.logId() + "Success deleting Template.");

		} catch (DBException e) {

			throw e;
		}

	}

	@Override
	public Template viewTemplate(AdminTrxInfo adminTrxInfo, Integer templateId) throws DBException, TemplatesNotFoundException {
		templateLogger.info(adminTrxInfo.logInfo());
		Template result;
		try {

			result = templateDao.find(templateId);
			if (result == null) {
				throw new TemplatesNotFoundException(templateId);
			}
		} catch (DBException e) {
			throw e;
		}
		return result;
	}

	@Override
	public List<Template> viewTemplate(AdminTrxInfo adminTrxInfo, int first, int max) throws TemplatesNotFoundException, DBException {
		List<Template> result;

		templateLogger.info(adminTrxInfo.logInfo());
		try {

			result = templateDao.findAll(first, max);
//			result = templateDao.findRange();

			if (result == null) {
				throw new TemplatesNotFoundException();
			}
		} catch (DBException e) {

			throw e;
		}
		return result;
	}

	@Override
	public int count(AdminTrxInfo adminTrxInfo) throws DBException {
		templateLogger.info(adminTrxInfo.logInfo());
		int result;
		try {
			result = templateDao.count();
		} catch (DBException e) {
			throw e;
		}
		return result;

	}

	private Language findLanguageByName(LanguageNameEnum langName) {
		Language language;
		try {
			language = languageDao.findByName(langName);
			appLogger.info("Language with name " + langName + " is retrieved");
			return language;
		} catch (Exception e) {
			appLogger.error("Error while finding language object using language name", e);
			return null;
		}
	}

	@Override
	public Language getLanguageByName(AdminTrxInfo adminTrxInfo, LanguageNameEnum language) throws DBException {
		try {
			templateLogger.info(adminTrxInfo.logInfo() + " find language by language enum name=" + language);
			return languageDao.findByName(language);
		} catch (DBException e) {
			templateLogger.error(adminTrxInfo.logInfo() + "Error while getting language object by language enum name=" + language, e);
			return null;
		}

	}
}
