package com.edafa.web2sms.ui.admin;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidMSISDNException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.IneligibleUserException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.SendTempSmsFailException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.UserDetailsModel;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidAddressFormattingException;
import com.edafa.web2sms.acc_manag.utils.sms.exception.AccountManagInvalidMSISDNFormatException;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SendingRateLimiterDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.exception.InvalidCustomerForQuotaInquiry;
import com.edafa.web2sms.dalayer.exception.QuotaInquiryFailed;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;
import com.edafa.web2sms.dalayer.pojo.QuotaInfo;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AdminAccountManagementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.UserManagementFacingLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.prov.interfaces.ServiceProvisioningLocal;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class CustomerAccountBean {

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI
			.name());
	private final Logger appLogger = LogManager.getLogger(LoggersEnum.ADMIN_MNGMT.name());

	@EJB
	private AdminAccountManagementFacingLocal accountManagement;

	@EJB
	private UserManagementFacingLocal userManagement;

	@EJB
	private ServiceProvisioningLocal serviceProvisioningLocal;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication()
			.getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName,
			locale);

	private boolean editFlag = false;
	private boolean extraQuotaFlag = false;

	ArrayList<AccountUser> accounts = new ArrayList<AccountUser>();
	AccountUser account = new AccountUser();
	QuotaInfo quotaInfo = new QuotaInfo();

	private AccountSender sender;

	private String userName = "";
	private String accountID = "";
	private String companyName = "";
	private String billingMSISDN = "";
	private String userPhone = "";

	private AccountUser accountProfile = new AccountUser();
	private boolean viewProfileFlag = false;
	List<ProvRequestArch> provRequestsList = new ArrayList<ProvRequestArch>();
	private ArrayList<String> sendersList = new ArrayList<String>();
	private boolean viewLimitersFlag = false;

	private List<SendingRateLimiter> limiters = new ArrayList<SendingRateLimiter>();
	private SendingRateLimiter limiter = new SendingRateLimiter();
	private SendingRateLimiter removedLimit = new SendingRateLimiter();
	@EJB
	SendingRateLimiterDaoLocal sendingRateLimiterDao;

	@EJB
	AccountDaoLocal accountDao;

	private boolean viewUserDataFlag = false;

	@PostConstruct
	public void init() {
		fillPrps();

	}

	private void fillPrps() {
		fillTable(0,true);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable(long currentPage , boolean searchFlag) {
		accounts = new ArrayList<AccountUser>();
		if (searchFlag) {
			viewUserDataFlag = false;
			viewLimitersFlag = false;
			viewProfileFlag = false;
		}
		List list = null;
		AdminTrxInfo trxInfo = null;

		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			logger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(
							String.valueOf(trxInfo.getAdmin().getAdminId()),
							trxInfo.getAdmin().getAdminName())
					+ "Customer care representative searching for account with accountId="
					+ accountID + " or campanyName=" + companyName
					+ " billingMsisdn=" + billingMSISDN + "userPhone= "
					+ userPhone + ", paginated from=" + currentPage
					+ " with max=" + pageSize);

			list = userManagement.searchAccountUsers(
					trxInfo.getAccountAdminTrxInfo(), userName, accountID,
					companyName, billingMSISDN, userPhone,
					Long.valueOf(currentPage).intValue(), pageSize);

			if (list != null && !list.isEmpty()) {
				accounts = new ArrayList<AccountUser>(list);
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("noResultFound")),
								null));
			}

			long count = userManagement.countSearchAccountUsers(
					trxInfo.getAccountAdminTrxInfo(), userName, accountID,
					companyName, billingMSISDN, userPhone);

			setRowCount(count);
			logger.info("loading all sending rates ");
			limiters = sendingRateLimiterDao.findAll();
			logger.info("found list of sending rate with size: "
					+ limiters.size());
			if (logger.isDebugEnabled()) {
				logger.debug("list of sending rate : " + limiters);
			}
		}// end try
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}// end catch
	}// end of method fillTable

	public String viewQuota() {

		try {
			quotaInfo = accountManagement.getQuotaInfoByMSISDN(CommonUtil
					.manageTrxInfo().getAccountAdminTrxInfo(), account
					.getAccount().getBillingMsisdn());
		} catch (DBException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.debug(e.getMessage(), e);
		} catch (AccountNotFoundException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error(e.getMessage(), e);
		} catch (InvalidCustomerForQuotaInquiry e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error(e.getMessage(), e);
		} catch (QuotaInquiryFailed e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error(e.getMessage(), e);
		} catch (AccountManagInvalidMSISDNFormatException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error(e.getMessage(), e);
		} catch (AccountManagInvalidAddressFormattingException e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error(e.getMessage(), e);
		}

		editFlag = true;

		return null;
	}

	public String viewSenders() {
		sendersList = new ArrayList<String>();
		editFlag = true;

		for (int i = 0; i < account.getAccount().getAccountSendersList().size(); i++)
			sendersList.add(account.getAccount().getAccountSendersList().get(i)
					.getAccountSendersPK().getSenderName());

		for (int i = 0; i < account.getAccount().getIntraSendersList().size(); i++) {
			{
				String intraSender = account.getAccount().getIntraSendersList()
						.get(i).getSenderName();
				sendersList.add(intraSender);
			}

		}
		return null;
	}

	public String viewProfile() {
		viewUserDataFlag=false;
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		String logInfo = logTrxId(trxInfo.getTrxId())
				+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()),
						trxInfo.getAdmin().getAdminName());
		try {
			viewProfileFlag = false;
			logger.info(logInfo
					+ "getting arch provisioning request for account Id ("
					+ accountProfile.getAccount().getAccountId() + ").");
			provRequestsList = serviceProvisioningLocal.findProvArchByAccount(
					trxInfo, accountProfile.getAccount().getAccountId());
			logger.debug(logInfo
					+ "arch provisioning result for account Id ("
					+ accountProfile.getAccount().getAccountId()
					+ " is " + provRequestsList);
			logger.info(logInfo
					+ "getting arch provisioning request for account Id ("
					+ accountProfile.getAccount().getAccountId()
					+ ") with size(" + provRequestsList.size() + ").");

			if (provRequestsList.size() > 0)
				viewProfileFlag = true;
			else
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle
										.getString("account_hasn't_history")),
								null));
		} catch (DBException e) {
			logger.error(logInfo + e.getMessage(), e);
			viewProfileFlag = false;
		}

		return null;
	}

	public String viewLimiters() {
		viewLimitersFlag = true;
		viewUserDataFlag = false;
		return null;
	}

	public String addNewLimiter() {
		try {
			logger.info("adding limiter=" + limiter + " to account_id="
					+ account.getAccountId());
			for (SendingRateLimiter limit : limiters) {
				if (limiter.getLimiterId().equals(limit.getLimiterId())) {
					limiter = limit;
					break;
				}
			}
			limiter.getAccountsList().add(account.getAccount());
			if (!account.getAccount().getSendingRateLimiters()
					.contains(limiter)) {
				account.getAccount().getSendingRateLimiters().add(limiter);
			} else {

				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										String.format("Limiter is already attached with this account."),
										null));
			}
		}// end try
		catch (Exception e) {
			logger.error("Error while adding new limiter=" + limiter
					+ " for account_id=" + account.getAccountId());
		}// end catch

		return null;

	}

	public String removeLimiter() {
		try {
			logger.info("removing limiter=" + limiter + " form account_id="
					+ account.getAccountId());
			account.getAccount().getSendingRateLimiters().remove(removedLimit);
		}// end try
		catch (Exception e) {
			logger.error("Error while adding new limiter=" + limiter
					+ " for account_id=" + account.getAccountId());
		}// end catch

		return null;

	}

	public String saveLimiter() {
		try {
			logger.info("saving changes for account_id="
					+ account.getAccountId());
			accountDao.edit(account.getAccount());
			fillTable(currentPage,false);
			viewLimitersFlag = false;
			logger.info("Account limiters saved");

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String
							.format("Account is saved successfully"), null));
		}// end try
		catch (Exception e) {
			logger.error("error while saving account limiters", e);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));

		}// end catch

		return null;
	}// end of method activateSmsAPIService

	public String viewUserData() {
		viewUserDataFlag = true;
		viewLimitersFlag = false;
		viewProfileFlag= false;
		return "";
	}

	public String cancel() {
		try {
			logger.info("Cancel limiters changes for account_id="
					+ account.getAccountId());
			limiter = new SendingRateLimiter();
			limiters.clear();
			viewLimitersFlag = false;
			fillTable(currentPage,true);
		}// end try
		catch (Exception e) {
			logger.error("Error while cancelling adding limiter for account_id="
					+ account.getAccountId());
		}// end catch

		return null;
	}

	public String closeUserDataView() {
		viewUserDataFlag = false;

		return null;
	}

	public String unblockUser() {
		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			AdminTrxInfo trxInfo = CommonUtil.manageTrxInfo(trxID);
			String logInfo = logTrxId(trxInfo.getTrxId())
					+ userLogInfo(
							String.valueOf(trxInfo.getAdmin().getAdminId()),
							trxInfo.getAdmin().getAdminName());

			logger.info(logInfo
					+ " calling user management to unblock user with id : "
					+ account.getAccountId());
			userManagement.unblockUser(trxInfo.getAccountAdminTrxInfo(),
					account.getAccountUserId());
			fillTable(currentPage,false);
			for (AccountUser accountUser : accounts) {
				if(accountUser.getAccountId().equals(account.getAccountId())){
					account = accountUser;
					break;
				}
			}
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "User is unblocked now.",
							null));
		} catch (AccountNotFoundException e) {
			logger.error("Exception while unblocking user: "+ e.getMessage());
			appLogger.error("Exception while unblocking user: ", e);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (UserNotFoundException e) {
			logger.error("Exception while unblocking user: "+ e.getMessage());
			appLogger.error("Exception while unblocking user: ", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (DBException e) {
			logger.error("Exception while unblocking user: "+ e.getMessage());
			appLogger.error("Exception while unblocking user: ", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Exception while unblocking user: "+ e.getMessage());
			appLogger.error("Exception while unblocking user: ", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return null;
	}

	public String generateTempPass() {

		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			AdminTrxInfo trxInfo = CommonUtil.manageTrxInfo(trxID);
			String logInfo = logTrxId(trxInfo.getTrxId())
					+ userLogInfo(
							String.valueOf(trxInfo.getAdmin().getAdminId()),
							trxInfo.getAdmin().getAdminName());
			if (account.getPhoneNumber() != null
					&& !account.getPhoneNumber().trim().isEmpty()) {

				logger.info(logInfo
						+ " calling user management to generate temp pass for user id : "
						+ account.getAccountId());
				userManagement.generateTempPassword(
						trxInfo.getAccountAdminTrxInfo(),
						account.getAccountUserId());
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, "Password is sent to user.",
								null));
			} else {
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										String.format("Please, enter mobile number and save first."),
										null));
			}
		} catch (AccountNotFoundException e) {
			logger.error("Exception while generating temp pass to user: "+ e.getMessage());
			appLogger.error("Exception while generating temp pass to user: ", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (UserNotFoundException e) {
			logger.error("Exception while generating temp pass to user: "+ e.getMessage());
			appLogger.error("Exception while generating temp pass to user: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (DBException e) {
			logger.error("Exception while generating temp pass to user: "+ e.getMessage());
			appLogger.error("Exception while generating temp pass to user: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (SendTempSmsFailException e) {
			logger.error("Exception while generating temp pass to user: "+ e.getMessage());
			appLogger.error("Exception while generating temp pass to user: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Exception while generating temp pass to user: "+ e.getMessage());
			appLogger.error("Exception while generating temp pass to user: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return null;
	}
	
	public String saveMobileNumber() {

		try {
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			AdminTrxInfo trxInfo = CommonUtil.manageTrxInfo(trxID);
			String logInfo = logTrxId(trxInfo.getTrxId())
					+ userLogInfo(
							String.valueOf(trxInfo.getAdmin().getAdminId()),
							trxInfo.getAdmin().getAdminName());
			logger.info(logInfo
					+ " calling user management to save mobile number : "
					+ account.getPhoneNumber() + account.getAccountId());

			UserDetailsModel user = new UserDetailsModel();
			user.setAccountId(account.getAccountId());
			user.setPhoneNumber(account.getPhoneNumber());
			user.setUserId(account.getAccountUserId());
			userManagement.updateUserData(trxInfo.getAccountAdminTrxInfo(),
					user);
			fillTable(currentPage,false);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO,
							"User's data is saved successfully", null));
		} catch (IneligibleUserException e) {
			logger.error("Exception while saving mobile number: "+ e.getMessage());
			appLogger.error("Exception while saving mobile number: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));

		}catch(InvalidMSISDNException e){
			logger.warn("InvalidMSISDNException while saving mobile number: "+ e.getMessage());
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid mobile number, Please enter valid mobile number", null));
		} catch (DBException e) {
			logger.error("Exception while saving mobile number: "+ e.getMessage());
			appLogger.error("Exception while saving mobile number: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Exception while saving mobile number: "+ e.getMessage());
			appLogger.error("Exception while saving mobile number: ", e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		fillTable(currentPage, false);
		for (AccountUser accountUser : accounts) {
			if(accountUser.getAccountUserId().equals(account.getAccountUserId())){
				account = accountUser;
				break;
			}
		}
		return null;

	}

	public String filter() {
		currentPage = 0;
		fillTable(0,true);
		return null;
	}

	public String logTrxId(String trxId) {
		String sb = new String();
		sb = "Trx(" + trxId + "): ";
		return sb;
	}

	public String userLogInfo(String id, String userName) {
		String sb = new String();
		sb = "Customer Care(" + id + "," + userName + "): ";
		return sb;
	}

	// ////////////////Paging/////////////////////////////

	long rowCount = 0L;
	long currentPage = 0;
	int pageSize = 10;

	public String FirstPage() {
		currentPage = 0;
		fillTable(0,true);
		return null;
	}

	public String nextPage() {
		if (currentPage + pageSize < rowCount) {
			currentPage += pageSize;
			if (currentPage < rowCount) {
				fillTable(currentPage,true);
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
				fillTable(currentPage,true);
			}
		}

		return null;
	}

	public String LastPage() {
		if (rowCount != 0) {
			long pagesMod = rowCount % pageSize;
			currentPage = rowCount - pagesMod;
			if (pagesMod > 0) {
				fillTable(rowCount - pagesMod , true);
			} else {
				fillTable(rowCount - pageSize,true);
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

	// ///////////////////////////end Paging///////////////

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public ArrayList<AccountUser> getAccounts() {
		return accounts;
	}

	public void setAccounts(ArrayList<AccountUser> accounts) {
		this.accounts = accounts;
	}

	public AccountUser getAccount() {
		return account;
	}

	public void setAccount(AccountUser account) {
		this.account = account;
	}

	public boolean isExtraQuotaFlag() {
		return extraQuotaFlag;
	}

	public void setExtraQuotaFlag(boolean extraQuotaFlag) {
		this.extraQuotaFlag = extraQuotaFlag;
	}

	public AccountSender getSender() {
		return sender;
	}

	public void setSender(AccountSender sender) {
		this.sender = sender;
	}

	public QuotaInfo getQuotaInfo() {
		return quotaInfo;
	}

	public void setQuotaInfo(QuotaInfo quotaInfo) {
		this.quotaInfo = quotaInfo;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAccountID() {
		return accountID;
	}

	public void setAccountID(String accountID) {
		this.accountID = accountID;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getBillingMSISDN() {
		return billingMSISDN;
	}

	public void setBillingMSISDN(String billingMSISDN) {
		this.billingMSISDN = billingMSISDN;
	}

	public AccountUser getAccountProfile() {
		return accountProfile;
	}

	public void setAccountProfile(AccountUser accountProfile) {
		this.accountProfile = accountProfile;
	}

	public boolean isViewProfileFlag() {
		return viewProfileFlag;
	}

	public void setViewProfileFlag(boolean viewProfileFlag) {
		this.viewProfileFlag = viewProfileFlag;
	}

	public List<ProvRequestArch> getProvRequestsList() {
		return provRequestsList;
	}

	public void setProvRequestsList(List<ProvRequestArch> provRequestsList) {
		this.provRequestsList = provRequestsList;
	}

	public ArrayList<String> getSendersList() {
		return sendersList;
	}

	public void setSendersList(ArrayList<String> sendersList) {
		this.sendersList = sendersList;
	}

	public boolean isViewLimitersFlag() {
		return viewLimitersFlag;
	}

	public void setViewLimitersFlag(boolean viewLimitersFlag) {
		this.viewLimitersFlag = viewLimitersFlag;
	}

	public List<SendingRateLimiter> getLimiters() {
		return limiters;
	}

	public void setLimiters(List<SendingRateLimiter> limiters) {
		this.limiters = limiters;
	}

	public SendingRateLimiter getLimiter() {
		return limiter;
	}

	public void setLimiter(SendingRateLimiter limiter) {
		this.limiter = limiter;
	}

	public SendingRateLimiter getRemovedLimit() {
		return removedLimit;
	}

	public void setRemovedLimit(SendingRateLimiter removedLimit) {
		this.removedLimit = removedLimit;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public boolean isViewUserDataFlag() {
		return viewUserDataFlag;
	}

	public void setViewUserDataFlag(boolean viewUserDataFlag) {
		this.viewUserDataFlag = viewUserDataFlag;
	}

}// end of class CustomerAccountBean