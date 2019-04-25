package com.edafa.web2sms.ui.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.eclipse.persistence.sessions.Login;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.IntraSender;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AdminAccountManagementFacingLocal;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderAlreadyExistException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderNotFoundException;
import com.edafa.web2sms.service.intrasender.exception.IntraSenderShouldHaveAcctListException;
import com.edafa.web2sms.service.intrasender.interfaces.IntraSenderManagementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

@ManagedBean(name = "intraSenderBean")
@ViewScoped
public class IntraSenderBean {
	private final Logger adminLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	@EJB
	private AccountManegementFacingLocal accountManagement;

	@EJB
	private IntraSenderManagementBeanLocal intraSenderManagementBeanLocal;

	@EJB
	private AdminAccountManagementFacingLocal adminManagement;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication().getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	private String intraSenderName = "";

	private List<Account> accounts = new ArrayList<Account>();

	private List<Account> selectedAccounts = new ArrayList<Account>();

	boolean viewDetailsFlag = false;

	private List<IntraSender> intraSenderList;

	boolean editFlag = false;

	private String searchValue = "";

	private List<Account> intraAccounts;

	private String intraSenderId;

	private IntraSender intraSender;
	
	private String intraSenderNameViewAccounts;

	/*------------------------- setters and getters -----------------*/

	
	
	public String getIntraSenderName() {
		return intraSenderName;
	}

	public String getIntraSenderNameViewAccounts() {
		return intraSenderNameViewAccounts;
	}

	public void setIntraSenderNameViewAccounts(String intraSenderNameViewAccounts) {
		this.intraSenderNameViewAccounts = intraSenderNameViewAccounts;
	}

	public IntraSender getIntraSender() {
		return intraSender;
	}

	public void setIntraSender(IntraSender intraSender) {
		this.intraSender = intraSender;
	}

	public String getIntraSenderId() {
		return intraSenderId;
	}

	public void setIntraSenderId(String intraSenderId) {
		this.intraSenderId = intraSenderId;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public List<Account> getIntraAccounts() {
		return intraAccounts;
	}

	public void setIntraAccounts(List<Account> intraAccounts) {
		this.intraAccounts = intraAccounts;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public String getSearchValue() {
		return searchValue;
	}

	public void setSearchValue(String searchValue) {
		this.searchValue = searchValue;
	}

	public void setIntraSenderName(String intraSenderName) {
		this.intraSenderName = intraSenderName;
	}

	public List<IntraSender> getIntraSenderList() {
		return intraSenderList;
	}

	public void setIntraSenderList(List<IntraSender> intraSenderList) {
		this.intraSenderList = intraSenderList;
	}

	public List<Account> getSelectedAccounts() {
		return selectedAccounts;
	}

	public void setSelectedAccounts(List<Account> selectedAccounts) {
		this.selectedAccounts = selectedAccounts;
	}

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public boolean isViewDetailsFlag() {
		return viewDetailsFlag;
	}

	public void setViewDetailsFlag(boolean viewDetailsFlag) {
		this.viewDetailsFlag = viewDetailsFlag;
	}

	/* ------------------------- Paging --------------------------- */

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

	/*------------------------- Action methods --------------------------------------*/

	@PostConstruct
	public void init() {

		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		AdminTrxInfo trxInfo = CommonUtil.manageTrxInfo(trxID);
		try {
			adminLogger.debug(trxInfo + "Getting all accounts");
			accounts = accountManagement.findAllAccounts(trxInfo.getAccountAdminTrxInfo());

		} catch (DBException e) {
			adminLogger.error("DBException while finding all accounts", e);
		}
		fillTable(0);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void fillTable(long currentPage) {

		intraSenderList = new ArrayList<IntraSender>();
		List list = null;
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {

			adminLogger.info(logInfo + "Fetching intra senders list paginated from=" + currentPage + " with max="
					+ pageSize);
			list = intraSenderManagementBeanLocal.findIntraSender(trxInfo, (int) currentPage, pageSize);

			if (list != null && !list.isEmpty()) {
				intraSenderList = new ArrayList<IntraSender>(list);
				adminLogger.info(logInfo + "Intra senders list paginated from=" + currentPage + " with max=" + pageSize
						+ " fetched from server with size=" + intraSenderList.size());
			} else {
				adminLogger.info(logInfo + "Intra senders list is emptey");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("noResultFound")),
								null));
			}
			setRowCount(intraSenderManagementBeanLocal.countIntraSendersNames(CommonUtil.manageTrxInfo(trxID)));
		} catch (Exception e) {
			adminLogger.error(logInfo + "Error while filling intra sender list", e);
		}
	}

	public String addIntraSenderName() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		if (editFlag) {

			adminLogger.debug(logInfo + "Editing ");

			IntraSender intraSenderObj = new IntraSender();
			intraSenderObj.setSenderName(intraSenderName);
			intraSenderObj.setIntraSenderId(intraSenderId);
			if (selectedAccounts == null || selectedAccounts.size() == 0) {
				adminLogger.debug(logInfo + "Adding intra sender name :(" + intraSenderName + ") For all accounts.");
				intraSenderObj.setSystemSenderFlag(true);
				intraSenderObj.setAccountList(selectedAccounts);

			} else {
				intraSenderObj.setSystemSenderFlag(false);
				intraSenderObj.setAccountList(selectedAccounts);
				adminLogger.debug(logInfo + "Adding intra sender name :(" + intraSenderName + ") For "
						+ selectedAccounts.size() + " accounts.");
			}

			// update..

			try {
				intraSenderManagementBeanLocal.editIntraSender(trxInfo, intraSenderObj);

				adminLogger.debug(logInfo + "intra sender name with id :(" + intraSenderId + ") updated successfully.");

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle
								.getString("update_intra_senfer_succeeded")), null));
			} catch (DBException e) {
				adminLogger.error(logInfo + "DBException while Editing intra sender name :(" + intraSenderName + ")."
						+ e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error, please try later.", null));
			} catch (InvalidSMSSender e) {
				adminLogger.error(logInfo + " intra sender name :(" + intraSenderName + ") is Invalid." + e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
								.getString("Invalid_Intra_Sender")), null));

			} catch (IntraSenderAlreadyExistException e) {
				adminLogger.error(logInfo + " intra sender name :(" + intraSenderName + ") already exists." + e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
								.getString("Intra_Sender_name_already_exist")), null));
			} catch (IntraSenderNotFoundException e) {
				adminLogger.error(logInfo + "IntraSenderNotFoundException with id :(" + intraSenderId + ")." + e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error, please try later.", null));
			} catch (IntraSenderShouldHaveAcctListException e) {
				adminLogger.error(logInfo + "IntraSenderShouldHaveAcctListException." + e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error, please try later.", null));
			}

		} else {
			try {

				adminLogger.debug(logInfo + "Request to add intra sender name :(" + intraSenderName + ").");

				IntraSender intraSenderObj = new IntraSender();
				intraSenderObj.setSenderName(intraSenderName);

				if (selectedAccounts == null || selectedAccounts.size() == 0) {
					adminLogger
							.debug(logInfo + "Adding intra sender name :(" + intraSenderName + ") For all accounts.");
					intraSenderObj.setSystemSenderFlag(true);
					intraSenderObj.setAccountList(selectedAccounts);

				} else {
					intraSenderObj.setSystemSenderFlag(false);
					intraSenderObj.setAccountList(selectedAccounts);
					adminLogger.debug(logInfo + "Adding intra sender name :(" + intraSenderName + ") For "
							+ selectedAccounts.size() + " accounts.");
				}

				intraSenderManagementBeanLocal.createIntraSender(trxInfo, intraSenderObj);
				adminLogger.debug(" intra sender name :(" + intraSenderName + ") is added successfully.");

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle
								.getString("Add_intra_sender_succeed")), null));
			} catch (DBException e) {
				adminLogger.error(logInfo + "DBException while adding intra sender name :(" + intraSenderName + ")."
						+ e);
				FacesContext.getCurrentInstance().addMessage(null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error, please try later.", null));
			} catch (InvalidSMSSender e) {
				adminLogger.error(logInfo + " intra sender name :(" + intraSenderName + ") is Invalid." + e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
								.getString("Invalid_Intra_Sender")), null));

			} catch (IntraSenderAlreadyExistException e) {
				adminLogger.error(logInfo + " intra sender name :(" + intraSenderName + ") already exists." + e);
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
								.getString("Intra_Sender_name_already_exist")), null));
			}
		}

		viewDetailsFlag = false;
		editFlag = false;
		intraSenderName = "";
		searchValue="";
		selectedAccounts = new ArrayList<Account>();
		fillTable(0);
		return null;
	}

	public String editIntraSender() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		adminLogger.info(logInfo + "Edit intra sender with id=" + intraSender.getIntraSenderId());
		intraSenderName = intraSender.getSenderName();
		selectedAccounts = intraSender.getAccountList();
		if(selectedAccounts == null)
			selectedAccounts = new ArrayList<Account>();
		intraSenderId = intraSender.getIntraSenderId();
		viewDetailsFlag = true;
		editFlag = true;
		return null;
	}

	public String deleteIntraSender(String id) {

		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		try {
			adminLogger.info(logInfo + "Deleting intra sender with id=" + id);
			intraSenderManagementBeanLocal.deleteIntraSender(trxInfo, id);
			adminLogger.info(logInfo + "Intra sender with id=" + id + " deleted successfully");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle
							.getString("delete_intra_sender_succeed")), null));
		} catch (Exception e) {
			adminLogger.error(logInfo + "Error while deleting intra sender with id=" + id, e);
		}

		fillTable(0);
		return null;
	}

	public String viewDetails() {
		viewDetailsFlag = true;
		return null;
	}// end of method viewDetails

	public String reset() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());
		adminLogger.info(logInfo + "reset button is pressed.");
		intraSenderName = "";
		searchValue = "";
		selectedAccounts = new ArrayList<Account>();
		try {
			accounts = accountManagement.findAllAccounts(trxInfo.getAccountAdminTrxInfo());
		} catch (DBException e) {
			adminLogger.error(logInfo + "DBException while initializa accounts list.");
		}
		viewDetailsFlag = false;
		fillTable(currentPage);
		return null;
	}// end of method reset

	public void searchAccount() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		adminLogger.info(logInfo + "Customer care representative searching for account with Company name= "
				+ searchValue + " paginated from=" + currentPage + " with max=" + pageSize);

		try {
			accounts = accountManagement.searchAccount(trxInfo.getAccountAdminTrxInfo(), null, searchValue, null);
		} catch (DBException e) {
			adminLogger.error(logInfo + "DBException while searching account with: " + searchValue + e);

		}

		if (accounts == null && accounts.isEmpty()) {

			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle
									.getString("noResultFound")), null));
		}

	}// end of method searchAccount

	public String intraSenderAccounts() {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName());

		adminLogger.info(logInfo + "View intra Sender accounts");
		intraAccounts = intraSender.getAccountList();
		intraSenderNameViewAccounts = intraSender.getSenderName();
		return null;
	}// end of intraSenderAccounts

}// end of class intraSender

