package com.edafa.web2sms.ui.admin;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.component.UICommand;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.Template;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AdminAccountManagementFacingLocal;
import com.edafa.web2sms.service.admin.interfaces.AdminTemplateManagementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.template.exception.InvalidTemplateException;
import com.edafa.web2sms.service.template.exception.TemplatesNotFoundException;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class SMSTemplateBean {

	@EJB
	AdminAccountManagementFacingLocal adminAccountManagement;
	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	@EJB
	AdminTemplateManagementBeanLocal templateManagementBeanLocal;
	@EJB
	private AccountManegementFacingLocal accountManagement;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication().getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	private boolean editFlag = false;

	ArrayList<Template> templateModels = new ArrayList<Template>();
	List<Account> userLists = new ArrayList<Account>();
	List<Account> searchList = new ArrayList<Account>();
	List<Account> archiveList = new ArrayList<Account>();;
	private Map<String, Boolean> checked;
	Template templateModel = new Template();

	private String languageSelected;
	private String searchUserToken;
	private boolean searchMode;

	private int firstRow;
	private int rowsPerPage = 10;

	/* add template to multiple users.. */
	private boolean viewDetailsFlag = false;
	private List<Account> accounts = new ArrayList<Account>();
	private String searchValue = "";
	private List<Account> selectedAccounts = new ArrayList<Account>();
	private Template templateAcc;

	// Functionality methods--------------------------------------

	@PostConstruct
	public void init() {
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		AdminTrxInfo trxInfo = CommonUtil.manageTrxInfo(trxID);
		try {
			logger.debug(trxInfo + "Getting all accounts");
			accounts = accountManagement.findAllAccounts(trxInfo.getAccountAdminTrxInfo());

		} catch (DBException e) {
			logger.error("DBException while finding all accounts", e);
		}
		fillPrps();
	}

	private void fillPrps() {
		fillTable(0);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable(long currentPage) {
		templateModels = new ArrayList<Template>();
		List list = null;
		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			list = templateManagementBeanLocal.viewTemplate(CommonUtil.manageTrxInfo(trxID), Long.valueOf(currentPage)
					.intValue(), pageSize);
			if (list != null && !list.isEmpty()) {
				templateModels = new ArrayList<Template>(list);
			}// end if
			else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("noResultFound")),
								null));
			}// end else

			long templatesCount = templateManagementBeanLocal.count(CommonUtil.manageTrxInfo(trxID));
			logger.info("templatesCount .. (" + templatesCount + ").");
			setRowCount(templatesCount);
		}// end try
		catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}// end catch
	}// end of method fillTable

	public String add() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		if (languageSelected.equals("true")) {
			languageSelected = "ENGLISH";
		} else {
			languageSelected = "ARABIC";
		}

		if (selectedAccounts == null || selectedAccounts.size() == 0) {
			logger.debug(logInfo + "Template :(" + templateModel + ") is For all accounts.");
			templateModel.setSystemTemplateFlag(true);
			templateModel.setAccountsList(selectedAccounts);

		} else {
			templateModel.setSystemTemplateFlag(false);
			templateModel.setAccountsList(selectedAccounts);
			logger.debug(logInfo + "Template :( " + templateModel + ") is For " + selectedAccounts.size()
					+ " accounts.");
		}

		if (editFlag) {

			try {
				logger.debug("Updating sms template : " + templateModel.toString());

				templateModel.setLanguage(templateManagementBeanLocal.getLanguageByName(CommonUtil.manageTrxInfo(),
						LanguageNameEnum.valueOf(languageSelected)));

				templateManagementBeanLocal.updateTemplate(CommonUtil.manageTrxInfo(), templateModel);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								String.format(bundle.getString("processSucceeded")), null));
			} catch (TemplatesNotFoundException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (InvalidTemplateException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR,
								String.format(bundle.getString("invalidTemplate")), null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (DBException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (Exception e) {

				if (e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									String.format(bundle.getString("dbViolation")), null));
				} else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
									.getString("processFailed")), null));
				}
//				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		} else {

			try {
				templateModel.setLanguage(templateManagementBeanLocal.getLanguageByName(
						CommonUtil.manageTrxInfo(trxID), LanguageNameEnum.valueOf(languageSelected)));
				templateManagementBeanLocal.createTemplate(CommonUtil.manageTrxInfo(trxID), templateModel);

				rowCount = templateManagementBeanLocal.count(CommonUtil.manageTrxInfo(trxID));
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								String.format(bundle.getString("processSucceeded")), null));
			} catch (InvalidTemplateException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (DBException e) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
								null));
				logger.error(e.getMessage(), e);
//				e.printStackTrace();
			} catch (Exception e) {

				if (e.getCause() != null && e.getCause().getCause() instanceof SQLIntegrityConstraintViolationException) {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR,
									String.format(bundle.getString("dbViolation")), null));
				} else {
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
									.getString("processFailed")), null));
				}
//				e.printStackTrace();
				logger.error(e.getMessage(), e);
			}
		}
		editFlag = false;
		templateModel = new Template();
		viewDetailsFlag = false;
		selectedAccounts = new ArrayList<Account>();
		fillTable(0);

		return null;
	}

	public String edit(String id) {
		for (int i = 0; i < templateModels.size(); i++) {
			if (templateModels.get(i).getTemplateId().equals(Integer.valueOf(id))) {
				logger.debug("Loading sms template for editing : " + templateModels.get(i).toString());
				setTemplateModel(templateModels.get(i));
			}
		}
		viewDetailsFlag = true;
		selectedAccounts = templateModel.getAccountsList();
		editFlag = true;

		return null;
	}

	public String delete(String id) {
		try {
			logger.debug("Removing sms template : " + id);

			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			templateManagementBeanLocal.deleteTemplate(CommonUtil.manageTrxInfo(trxID), Integer.valueOf(id));

			// paging
			// rowCount =
			// templateManagementBeanLocal.countTemplates(CommonUtil.manageTrxInfo(trxID));
			FirstPage();
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("processSucceeded")),
							null));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));

//			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}

		return null;
	}// end of method delete

	public void fillUserTable() {
		checked = new HashMap<String, Boolean>();
		try {
			if (searchMode == true) {
				userLists.clear();
				for (Account account : archiveList) {
					userLists.add(account);
				}
				searchMode = false;
			} else {
				String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
				userLists = adminAccountManagement.findAllAccounts(CommonUtil.manageTrxInfo(trxID).getAccountAdminTrxInfo(), Long
						.valueOf(currentPage).intValue(), pageSize);
				archiveList.addAll(userLists);
			}

			if (userLists == null && userLists.isEmpty()) {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("noResultFound")),
								null));
			}
		} catch (Exception e) {
//			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	/* add template to multiple users.. */

	public void searchAccount() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		logger.info(logInfo + "Customer care representative searching for account with Company name= " + searchValue
				+ " paginated from=" + currentPage + " with max=" + pageSize);

		try {
//			if (searchValue == ""|| searchValue == " ")
//				  accounts = accountManagement.findAllAccounts(trxInfo);
//			else
				accounts = accountManagement.searchAccount(trxInfo.getAccountAdminTrxInfo(), null, searchValue, null);
		} catch (DBException e) {
			logger.error(logInfo + "DBException while searching account with: " + searchValue + e);

		}

		// if (accounts == null && accounts.isEmpty()) {
		//
		// FacesContext.getCurrentInstance()
		// .addMessage(
		// null,
		// new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle
		// .getString("noResultFound")), null));
		// }

	}// end of method searchAccount

	// public String getTemplateAccounts() {
	// AdminTrxInfo trxInfo = null;
	// String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
	// trxInfo = CommonUtil.manageTrxInfo(trxID);
	// String logInfo = logTrxId(trxInfo.getTrxId())
	// + userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()),
	// trxInfo.getAdmin().getAdminName());
	//
	// logger.info(logInfo + "View template accounts");
	// templateAcc = temp.getAccountList();
	// intraSenderNameViewAccounts = intraSender.getSenderName();
	// return null;
	// }

	public String viewDetails() {
		viewDetailsFlag = true;
		return null;
	}// end of method viewDetails

	// ////////////////Paging/////////////////////////////

	long rowCount = 0L;
	long currentPage = 0;
	int pageSize = 10;

	public String FirstPage() {
		currentPage = 0;
		fillTable(0);

		return null;
	}

	public String nextPage() {
		if (currentPage + pageSize < rowCount) {
			currentPage += pageSize;
			if (currentPage < rowCount) {
				fillTable(currentPage);
			} else {
				LastPage();
			}
		}

		return null;
	}

	public String previousPage() {
		if (currentPage != 0) {
			currentPage -= pageSize;
			if (currentPage < pageSize) {
				currentPage = 0;
				FirstPage();
			} else {
				fillTable(currentPage);
			}
		}

		return null;
	}

	public String LastPage() {
		if (rowCount != 0) {
			long pagesMod = rowCount % pageSize;
			currentPage = rowCount - pagesMod;
			if (pagesMod > 0) {
				fillTable(rowCount - pagesMod);
			} else {
				fillTable(rowCount - pageSize);
			}
		}// end if

		return null;
	}

	public long getRowCount() {
		return rowCount;
	}

	public void setRowCount(long rowCount) {
		this.rowCount = rowCount;
	}

	public long getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(long currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void pageFirst() {
		page(0);
	}

	public void pageNext() {
		page(firstRow + rowsPerPage);
	}

	private void page(int firstRow) {
		this.firstRow = firstRow;
		fillUserTable(); // Load requested page.
	}

	public void page(ActionEvent event) {
		page(((Integer) ((UICommand) event.getComponent()).getValue() - 1) * rowsPerPage);
	}

	// ///////////////////////////end Paging///////////////

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public ArrayList<Template> getTemplateModels() {
		return templateModels;
	}

	public void setTemplateModels(ArrayList<Template> templateModels) {
		this.templateModels = templateModels;
	}

	public Template getTemplateModel() {
		return templateModel;
	}

	public void setTemplateModel(Template templateModel) {
		this.templateModel = templateModel;
	}

	public String getSearchUserToken() {
		return searchUserToken;
	}

	public boolean isSearchMode() {
		return searchMode;
	}

	public void setSearchMode(boolean searchMode) {
		this.searchMode = searchMode;
	}

	public List<Account> getUserLists() {
		return userLists;
	}

	public Map<String, Boolean> getChecked() {
		return checked;
	}

	public void setChecked(Map<String, Boolean> checked) {
		this.checked = checked;
	}

	public void setUserLists(List<Account> userLists) {
		this.userLists = userLists;
	}

	public List<Account> getSearchList() {
		return searchList;
	}

	public void setSearchList(List<Account> searchList) {
		this.searchList = searchList;
	}

	public void setSearchUserToken(String searchUserToken) {
		this.searchUserToken = searchUserToken;
	}

	public String getLanguageSelected() {
		return languageSelected;
	}

	public void setLanguageSelected(String languageSelected) {
		this.languageSelected = languageSelected;
	}

	public boolean isViewDetailsFlag() {
		return viewDetailsFlag;
	}

	public void setViewDetailsFlag(boolean viewDetailsFlag) {
		this.viewDetailsFlag = viewDetailsFlag;
	}

	public String logTrxId(String trxId) {
		String sb = new String();
		sb = "Trx(" + trxId + "): ";
		return sb;
	}

	public String userLogInfo(String id, String userName) {
		String sb = new String();
		sb = "Admin(" + id + "," + userName + "): ";
		return sb;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public List<Account> getSelectedAccounts() {
		return selectedAccounts;
	}

	public void setSelectedAccounts(List<Account> selectedAccounts) {
		this.selectedAccounts = selectedAccounts;
	}

	public Template getTemplateAcc() {
		return templateAcc;
	}

	public void setTemplateAcc(Template templateAcc) {
		this.templateAcc = templateAcc;
	}

}
