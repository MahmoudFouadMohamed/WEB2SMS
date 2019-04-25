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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.Reports;
import com.edafa.web2sms.reporting.service.model.GetCountResult;
import com.edafa.web2sms.reporting.service.model.ReportsResult;
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

public class OfflineReports {
	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI
			.name());

	FacesContext facesContext = FacesContext.getCurrentInstance();
	String messageBundleName = facesContext.getApplication().getMessageBundle();
	Locale locale = facesContext.getViewRoot().getLocale();
	ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);
	List<com.edafa.web2sms.service.report.Reports> reports = new ArrayList<com.edafa.web2sms.service.report.Reports>();
	ReportManagementService reportService;
	@EJB
	WSClients servicePort;

	@PostConstruct
	public void init() {
		reportService = servicePort.getReportServicePort();
		fillTable(0);
	}

	private void fillReportsList(int fromRow) {
		com.edafa.web2sms.reporting.service.model.AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageReportingTrxInfo(trxID);
		try {

			logger.debug(CommonUtil.logInfo(trxInfo) + "fill requested reports table.");

			ReportsResult status = reportService.getAllAdminReports(trxInfo,
					fromRow, pageSize);
			switch (status.getStatus()) {
			case SUCCESS:
				reports = status.getReports();
				logger.debug(CommonUtil.logInfo(trxInfo)
						+ "list of request reports is retrived successfully from: ["
						+ fromRow + ", to:" + pageSize + "].");
				break;

			default:
				reports = new ArrayList<com.edafa.web2sms.service.report.Reports>();
				logger.debug(CommonUtil.logInfo(trxInfo)
						+ "failed to retrive requested reports list from: ["
						+ fromRow + ", to:" + pageSize + "].");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String
								.format("Error, Please try later."), null));
				break;
			}
		} catch (Exception e) {
			logger.debug(CommonUtil.logInfo(trxInfo)
					+ "Exception while retriving requested reports list from: ["
					+ fromRow + ", to:" + pageSize + "].");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format("Error, Please try later."), null));

		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void fillTable(int fromRow) {
		com.edafa.web2sms.reporting.service.model.AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageReportingTrxInfo(trxID);
		try {
			reports = new ArrayList<com.edafa.web2sms.service.report.Reports>();
			fillReportsList(fromRow);
			GetCountResult countResult = reportService
					.getAdminReportsCount(trxInfo);

			switch (countResult.getStatus()) {
			case SUCCESS:
				setRowCount(countResult.getCount());
				logger.debug(CommonUtil.logInfo(trxInfo)
						+ "Reports count is retrived successfully");
				break;

			default:
				logger.debug(CommonUtil.logInfo(trxInfo)
						+ "Error while retriving reports count.");
				break;
			}

		} catch (Exception e) {
			logger.error(CommonUtil.logInfo(trxInfo)+e.getMessage(), e);
		}
	}

	public String readFileFromServer(String fileToken) {
		try {
			AdminTrxInfo trxInfo = null;
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			logger.debug(trxInfo.logInfo()
					+ "Setting session attributes ([fileToken: " + fileToken
					+ "]");

			FacesContext facesContext = FacesContext.getCurrentInstance();
			HttpSession session = (HttpSession) facesContext
					.getExternalContext().getSession(true);
			session.setAttribute("Token", fileToken);
			session.setAttribute("adminTrxInfo", trxInfo);
		}// end try
		catch (Exception e) {
			logger.error(e.getMessage(), e);
		}// end catch

		return null;
	}// end of method readFileFromServer

	public void cancelReport(int reportId) {

		com.edafa.web2sms.reporting.service.model.AdminTrxInfo trxInfo = null;
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		trxInfo = CommonUtil.manageReportingTrxInfo(trxID);
		logger.debug(CommonUtil.logInfo(trxInfo) + "cancel report with id: " + reportId
				+ "]");
		try {

			com.edafa.web2sms.reporting.service.model.ResultStatus result = reportService.cancelAdminReport(trxInfo,
					reportId);
			ResponseStatus status = result.getStatus();
			switch (status) {
			case SUCCESS:
				logger.debug(CommonUtil.logInfo(trxInfo)
						+ " The requested report is canceled successfully.");
				currentPage = 0;
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO,
								"Report is canceled successfully", null));
				fillReportsList(0);
				break;

			default:
				logger.debug(CommonUtil.logInfo(trxInfo) + "  error while cancelling report.");
				FacesContext
						.getCurrentInstance()
						.addMessage(
								null,
								new FacesMessage(
										FacesMessage.SEVERITY_ERROR,
										"Error while canceling report, please try later",
										null));
				break;
			}
		} catch (Exception e) {
			logger.error(CommonUtil.logInfo(trxInfo) + " exception while cancelling report: "
					+ e.getMessage());
			logger.error(CommonUtil.logInfo(trxInfo) + " exception while cancelling report: "
					+ e.getMessage());

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							"Error while canceling report, please try later",
							null));

		}

	}

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

	public List<com.edafa.web2sms.service.report.Reports> getReports() {
		return reports;
	}

	public void setReports(
			List<com.edafa.web2sms.service.report.Reports> reports) {
		this.reports = reports;
	}

}
