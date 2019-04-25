package com.edafa.web2sms.ui.admin;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.service.admin.interfaces.AdminCampaignManagementBeanLocal;
import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignSearchParam;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.service.report.ReportManagementService;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.ui.util.WSClients;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class SearchCampaignsBean {
	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	@EJB
	AdminCampaignManagementBeanLocal campaignManagementBeanLocal;
	// ////////////////////////////////////////////////////////////////
	FacesContext facesContext = FacesContext.getCurrentInstance();
	String messageBundleName = facesContext.getApplication().getMessageBundle();
	Locale locale = facesContext.getViewRoot().getLocale();
	ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	String accountID = "";
	String companyName = "";
	String billingMSISDN = "";
	String senderName = "";
	String userName = "";
	String accountIDTmp;
	String companyNameTmp;
	String billingMSISDNTmp;
	String senderNameTmp;
	String userNameTmp;
	Date datefrom;
	Date dateto;
	ReportManagementService reportService;
	CampaignSearchParam campaignSearchParam;
	boolean editFlag = false;
	boolean filtered = false;

	ArrayList<CampaignStatusName> campaignStatusNames = new ArrayList<CampaignStatusName>();
	ArrayList<CampaignStatusName> campaignStatusNamesSelected = new ArrayList<CampaignStatusName>();
	ArrayList<CampaignStatusName> campaignStatusNamesSelectedTmp = new ArrayList<CampaignStatusName>();

	ArrayList<Campaign> campaigns = new ArrayList<Campaign>();
	Campaign campaign = new Campaign();

	@EJB
	WSClients servicePort;
	
	@PostConstruct
	public void init() {
		reportService = servicePort.getReportServicePort();
		fillTrxsStatus();
	}

	private void fillTrxsStatus() {
		campaignStatusNames = new ArrayList<CampaignStatusName>();

		for (CampaignStatusName statusName : CampaignStatusName.values()) {
			if (!statusName.equals(CampaignStatusName.UNKNOWN))
				campaignStatusNames.add(statusName);
		}

	}

	public String filter() {
		currentPage = 0;
		filtered = true;
		fillTable(0);

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable(int fromRow) {
		try {
			campaigns = new ArrayList<Campaign>();
			campaign = new Campaign();

			editFlag = false;

			if (isValide()) {

				if (campaignStatusNamesSelected == null || campaignStatusNamesSelected.isEmpty()) {
					campaignStatusNamesSelectedTmp = null;
				} else {
					campaignStatusNamesSelectedTmp = campaignStatusNamesSelected;
				}

				if (accountID == null || accountID.isEmpty()) {
					accountIDTmp = null;
				} else {
					accountIDTmp = accountID;
				}

				if (companyName == null || companyName.trim().isEmpty()) {
					companyNameTmp = null;
				} else {
					companyNameTmp = companyName;
				}

				if (billingMSISDN == null || billingMSISDN.trim().isEmpty()) {
					billingMSISDNTmp = null;
				} else {
					billingMSISDNTmp = billingMSISDN;
				}

				if (senderName == null || senderName.trim().isEmpty()) {
					senderName = null;
				} else {
					senderNameTmp = senderName;
				}

				if (userName == null || userName.trim().isEmpty()) {
					userName = null;
				} else {
					userNameTmp = userName;
				}

				campaignSearchParam = new CampaignSearchParam(datefrom, dateto, accountIDTmp, companyNameTmp,
						billingMSISDNTmp, senderNameTmp, userName, campaignStatusNamesSelectedTmp);

				List<Campaign> campaignList = campaignManagementBeanLocal.adminGetCampaign(CommonUtil.manageTrxInfo(),
						campaignSearchParam, fromRow, pageSize);

				campaigns.addAll(campaignList);

				int campaignCount = campaignManagementBeanLocal.adminCountCampaign(CommonUtil.manageTrxInfo(),
						campaignSearchParam).intValue();

				setRowCount(campaignCount);
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("processFailed")),
							null));
			logger.error(e.getMessage(), e);
		}
	}

	private boolean isValide() {
		if (datefrom != null && dateto != null && datefrom.after(dateto)) {
			FacesContext
					.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(
									FacesMessage.SEVERITY_ERROR,
									"Invalid Entry Start Date to, Please enter (Start Date to) value greater than the (Start Date from)",
									null));
			return false;
		}
		return true;
	}

	public String view() {
		editFlag = true;
		return null;
	}

	public void requestDetailedReportForCampaign(String campaignId) {
		AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageTrxInfo(trxID);
		try {

			logger.debug(trxInfo.logInfo()
					+ "Reuqsting detailed report for campaign with id["
					+ campaignId + "].");

			ResultStatus result = reportService
					.offlineGenerateDetailedCampaignReportForAdmin(trxInfo,
							campaignId);
			ResponseStatus status = result.getStatus();
			switch (status) {
			case SUCCESS:
				logger.debug(trxInfo.logInfo()
						+ "detailed report requested successfully.");
				FacesContext
				.getCurrentInstance()
				.addMessage(
						null,
						new FacesMessage(
								FacesMessage.SEVERITY_INFO,
								String.format("Detailed report requested successfully."),
								null));
				break;

			default:
				logger.debug(trxInfo.logInfo()
						+ "error while requesting detaield report.");
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										String.format("Error while requesting detailed report."),
										null));
				break;
			}
		} catch (Exception e) {
			logger.debug(trxInfo.logInfo()
					+ "exception while requesting detaield report: "
					+ e.getMessage());
			logger.debug(trxInfo.logInfo()
					+ "exception while requesting detaield report: "
					+ e.getMessage());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Error while requesting detailed report.", null));
		}

	}
	
	public String readFileFromServer() {
		try {
			AdminTrxInfo trxInfo = null;
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			logger.debug(trxInfo.logInfo()+ "Setting session attributes ([name=campaignReportId,value=" + campaign.getCampaignId() + "]");

			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
			session.setAttribute("campaignReportId", campaign.getCampaignId());
			session.setAttribute("adminTrxInfo", trxInfo);
		}// end try
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}// end catch

		return null;
	}// end of method readFileFromServer

	// ////////////////Paging/////////////////////////////

	int rowCount = 0;
	int currentPage = 0;
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
			int pagesMod = rowCount % pageSize;
			currentPage = rowCount - pagesMod;
			// currentPage = rowCount;
			if (pagesMod > 0) {
				fillTable(rowCount - pagesMod);
			} else {
				fillTable(rowCount - pageSize);
			}
		}// end if

		return null;
	}

	public int getRowCount() {
		return rowCount;
	}

	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	// ///////////////////////////end Paging///////////////

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

	public Date getDatefrom() {
		return datefrom;
	}

	public void setDatefrom(Date datefrom) {
		this.datefrom = datefrom;
	}

	public Date getDateto() {
		return dateto;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public void setDateto(Date dateto) {
		this.dateto = dateto;
	}

	public ArrayList<CampaignStatusName> getCampaignStatusNames() {
		return campaignStatusNames;
	}

	public void setCampaignStatusNames(ArrayList<CampaignStatusName> campaignStatusNames) {
		this.campaignStatusNames = campaignStatusNames;
	}

	public ArrayList<CampaignStatusName> getCampaignStatusNamesSelected() {
		return campaignStatusNamesSelected;
	}

	public void setCampaignStatusNamesSelected(ArrayList<CampaignStatusName> campaignStatusNamesSelected) {
		this.campaignStatusNamesSelected = campaignStatusNamesSelected;
	}

	public String getBillingMSISDN() {
		return billingMSISDN;
	}

	public void setBillingMSISDN(String billingMSISDN) {
		this.billingMSISDN = billingMSISDN;
	}

	public boolean isEditFlag() {
		return editFlag;
	}

	public void setEditFlag(boolean editFlag) {
		this.editFlag = editFlag;
	}

	public ArrayList<Campaign> getCampaigns() {
		return campaigns;
	}

	public void setCampaigns(ArrayList<Campaign> campaigns) {
		this.campaigns = campaigns;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Campaign getCampaign() {
		return campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
}
