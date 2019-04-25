package com.edafa.web2sms.ui.login;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.service.admin.UIUserLogginMangementBean;
import com.edafa.web2sms.service.admin.exception.AdminNotFoundException;
import com.edafa.web2sms.service.admin.exception.DuplicateUserNameException;
import com.edafa.web2sms.service.admin.exception.InvalidAdminRequest;
import com.edafa.web2sms.service.admin.interfaces.AdminManagementBeanLocal;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.security.interfaces.HashUtilsLocal;

public class SystemLogin {
	@EJB
	private AdminManagementBeanLocal adminManagementBeanLocal;
	@EJB
	private UIUserLogginMangementBean uiUserLogginMangementBean;
	@EJB
	HashUtilsLocal hashingUtilsLocal;
	// @EJB
	// private UIUserLogginMangementBean uiUserLogginMangementBean;
	// @EJB
	// private PropertyDaoLocal propertyDaoLocal;

	Admin systemUser = new Admin();

	String loginResult = "";
	String oldPasw = "";
	String newPasw = "";
	String confirmPsw = "";
	String WeakPsw = ",12345678,abcdefgh,password,abc,";
	FacesContext context = FacesContext.getCurrentInstance();
	private String messageBundleName = context.getApplication().getMessageBundle();
	private Locale locale = context.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	private int numberOfDays = 30;// Days

	@PostConstruct
	public void init() {
		// String auditProcess = UIAudit.PAGE_ACCESS;
		// try
		// {
		// UIAudit.auditProcess((HttpServletRequest)
		// FacesContext.getCurrentInstance().getExternalContext()
		// .getRequest(), auditProcess);
		// }
		// catch (UnknownHostException e)
		// {
		// e.printStackTrace();
		// logger.error(e.getMessage(), e);
		// }
		String unAuthorized = (String) CommonUtil.getObjectFromSession("loginResult");
		if (unAuthorized != null) {
			loginResult = unAuthorized;
			CommonUtil.putObjectIntoSession("loginResult", "");
		}
		String reset = (String) CommonUtil.getObjectFromSession("reset");
		if (reset != null && !reset.equals("true")) {

			systemUser = new Admin();
			CommonUtil.putObjectIntoSession("user", null);
		}
	}

	public String AuthUser() {
		String outcome = null;

		loginResult = "";

		try {
			systemUser = adminManagementBeanLocal.adminLogin(CommonUtil.manageTrxInfo(), systemUser.getUsername(), systemUser.getPassword());

			// systemUserDaoLocal.find(systemUser.getUserName(),
			// systemUser.getPassWord());
			// uiUserLogginMangementBean.find(systemUser.getUserName());
			CommonUtil.putObjectIntoSession("user", systemUser);
		} catch (InvalidAdminRequest e) {
			logger.warn(e.getMessage());
			systemUser = null;
//			e.printStackTrace();
		} catch (DBException e) {
			logger.error(e.getMessage(), e);
			systemUser = null;
//			e.printStackTrace();
		}catch(Exception e){
			logger.error(e.getMessage(), e);
			systemUser = null;
//			e.printStackTrace();
		}
		if (systemUser == null) {
			// String unAuthorized =
			// propertyDaoLocal.getProperty(ModuleNameType.WEB,
			// "UnauthorizedAccess").getPropertyValue();
			// if (unAuthorized.isEmpty())
			loginResult = bundle.getString("authenticationFailed");

			// else {
			// loginResult = unAuthorized;
			// }
			systemUser = new Admin();
			return null;
		} else {

			try {

				if (!systemUser.getActiveFlag()) {
					outcome = null;
					logger.error("User is deactivated and trying to login, User Name:" + systemUser.getUsername());
					loginResult = bundle.getString("deactivatedAdmin");
				} else if (needResetPsw(systemUser.getResetDate())) {
					CommonUtil.putObjectIntoSession("reset", "true");
					FacesContext.getCurrentInstance().getExternalContext()
							.redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/Login/reset.xhtml?faces-redirect=true");
					return "";
				} else
				// Add to active sessions Map
				if (uiUserLogginMangementBean.find(systemUser.getUsername())) {
					loginResult = bundle.getString("adminLoggedIn");
					outcome = null;
				} else {
					logger.info(systemUser.getRoleId().getDirectory());
					outcome = systemUser.getRoleId().getDirectory();

				}
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			}
			// //////////////////////////////

			// UIAudit.auditProcess((HttpServletRequest)
			// FacesContext.getCurrentInstance().getExternalContext().getRequest(),
			// UIAudit.LOGIN);

			// if (systemUser.isLockedAccount()) {
			// logger.info("The account is locked");
			// loginResult = "The User account is locked";
			// return null;
			// } else if (systemUser.isPasswordEXpired()) {
			// CommonUtil.moveToPage(PgwNavigationHandler.LOGIN_RESET_PASSWORD_PAGE);
			// outcome = "pretty:home";
			// } else if (systemUser.isLoggedIn()) {
			// logger.info("User already logged in ");
			// loginResult = "This User is already logged in";
			// CommonUtil.putObjectIntoSession("user", null);
			// outcome = null;

		}

		return outcome;
	}

	public String cancelReset() {

		return logOut();
	}

	private boolean needResetPsw(Date dateReset) {
		if (dateReset != null) {
			Calendar ccCalendar = Calendar.getInstance();
			ccCalendar.setTime(dateReset);
			numberOfDays = (Integer) Configs.ADMIN_RESET_PSW_PERIOD.getValue();
			ccCalendar.add(Calendar.DATE, numberOfDays);
			if (ccCalendar.getTime().before(new Date())) {
				return true;
			}
		}
		return false;
	}

	public String logOut() {

		try {
			// Remove from map of active sessions
			uiUserLogginMangementBean.updateLogIn(((Admin) CommonUtil.getObjectFromSession("user")).getUsername());

			FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath() + "/home.html?faces-redirect=true");
		} catch (IOException e) {

			logger.error(e.getMessage(), e);
//			e.printStackTrace();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
//			e.printStackTrace();
		}

		return "";
	}

	public String resetPassword() {
		String outcome = null;

		loginResult = "";
		FacesMessage message = new FacesMessage();
		message.setSeverity(FacesMessage.SEVERITY_ERROR);
		boolean alphabeticFlag = false;
		boolean numericFlag = false;
		boolean repeatedFlag = false;
		systemUser = (Admin) CommonUtil.getObjectFromSession("user");

		for (int i = 0; i < newPasw.length(); i++) {
			if (i != 0) {
				if (newPasw.charAt(i) == newPasw.charAt(i - 1)) {
					repeatedFlag = true;
				}
			}
			if (Character.isLetter(newPasw.charAt(i))) {
				alphabeticFlag = true;
			}
			if (Character.isDigit(newPasw.charAt(i))) {
				numericFlag = true;
			}
		}
		if (!newPasw.equals(confirmPsw)) {
			loginResult = "The New password entered is not equal to confirm password value";
			// message.setSummary("The New password entered is not equal to confirm password value");
			// message.setDetail("The New password entered is not equal to confirm password value");
			// throw new ValidatorException(message);
		} else if (!systemUser.getPassword().equals(hashingUtilsLocal.hashWord(oldPasw))) {
			loginResult = "You have entered wrong old password";
//			message.setSummary("You have to enter different password");
//			message.setDetail("You have to enter different password");
		} else if (newPasw.length() < 8) {
			loginResult = "The New password length is less than 8 characters";
			// message.setSummary("The New password length is less than 8 characters");
			// message.setDetail("The New password length is less than 8 characters");
			// throw new ValidatorException(message);
		} else if (newPasw.equals(oldPasw)) {
			loginResult = "You have to enter different password";
			// message.setSummary("You have to enter different password");
			// message.setDetail("You have to enter different password");
			// throw new ValidatorException(message);
		} else if (newPasw.equals(systemUser.getUsername())) {
			loginResult = "You can not set your password exactly like your username";
			// message.setSummary("You can not set your password exactly like your username");
			// message.setDetail("You can not set your password exactly like your username");
		} else if (WeakPsw.contains("," + newPasw + ",")) {
			loginResult = "This is a very weak password, please make it harder to be guessed";
			// message.setSummary("This is a very weak password, please make it harder to be guessed");
			// message.setDetail("This is a very weak password, please make it harder to be guessed");

		} else if (!(alphabeticFlag && numericFlag)) {
			loginResult = "You must use alphanumeric password";
			// message.setSummary("You must use alphanumeric password");
			// message.setDetail("You must use alphanumeric password");
		} else if (repeatedFlag) {
			loginResult = "You can not use two successive identical characters in the password";
			// message.setSummary("You can not use two successive identical characters in the password");
			// message.setDetail("You can not use two successive identical characters in the password");
		} else {

			try {
				systemUser.setPassword(newPasw);
				// systemUserDaoLocal.updatePassword(oldPasw, newPasw,
				// systemUser.getSystemUserId(), noOfPasswordHistory);
				systemUser = adminManagementBeanLocal.resetAdminPass(CommonUtil.manageTrxInfo(), systemUser);

				CommonUtil.putObjectIntoSession("user", systemUser);

				// if (systemUser.isPasswordEXpired()) {
				// CommonUtil.moveToPage(PgwNavigationHandler.LOGIN_PAGE);
				// }

				logger.info(systemUser.getRoleId().getDirectory());
				outcome = systemUser.getRoleId().getDirectory();

				// } catch (EPGException e) {
				// loginResult = e.getException().getMessage();
				// message.setSummary(e.getException().getMessage());
				// message.setDetail(e.getException().getMessage());
				// logger.error(e.getException().getMessage(),
				// e.getException());
				// e.getException().printStackTrace();
				// outcome = null;
				// } catch (Exception e) {
				// FacesContext.getCurrentInstance().addMessage(null,
				// new FacesMessage(FacesMessage.SEVERITY_ERROR,
				// "Error occurred while resetting password.", null));
				// logger.error(e.getMessage(), e);
				// e.printStackTrace();
				// outcome = null;
				// }// end catch

			} catch (InvalidAdminRequest e) {
				loginResult = bundle.getString("authenticationFailed");
//				e.printStackTrace();
				logger.debug(e.getMessage(), e);
			} catch (DuplicateUserNameException e) {
				loginResult = bundle.getString("authenticationFailed");
//				e.printStackTrace();
				logger.debug(e.getMessage(), e);
			} catch (DBException e) {
				loginResult = bundle.getString("authenticationFailed");
//				e.printStackTrace();
				logger.debug(e.getMessage(), e);
			} catch (AdminNotFoundException e) {
				loginResult = bundle.getString("authenticationFailed");
//				e.printStackTrace();
				logger.debug(e.getMessage(), e);
			}
		}

		return outcome;
	}

	public String getLoginResult() {
		return loginResult;
	}

	public void setLoginResult(String loginResult) {
		this.loginResult = loginResult;
	}

	public Admin getSystemUser() {
		return systemUser;
	}

	public void setSystemUser(Admin systemUser) {
		this.systemUser = systemUser;
	}

	public String getOldPasw() {
		return oldPasw;
	}

	public void setOldPasw(String oldPasw) {
		this.oldPasw = oldPasw;
	}

	public String getNewPasw() {
		return newPasw;
	}

	public void setNewPasw(String newPasw) {
		this.newPasw = newPasw;
	}

	public String getConfirmPsw() {
		return confirmPsw;
	}

	public void setConfirmPsw(String confirmPsw) {
		this.confirmPsw = confirmPsw;
	}

}
