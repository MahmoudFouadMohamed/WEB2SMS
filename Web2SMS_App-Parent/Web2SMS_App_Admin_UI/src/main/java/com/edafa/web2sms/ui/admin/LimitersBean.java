package com.edafa.web2sms.ui.admin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.service.admin.interfaces.AdminConfigManagementBeanLocal;
import com.edafa.web2sms.ui.models.LimiterModel;
import com.edafa.web2sms.ui.util.SystemLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.ws.utils.configs.DBException_Exception;
import com.edafa.ws.utils.configs.FailedToReadConfigsException_Exception;
import com.edafa.ws.utils.configs.FailedToSaveConfigsException_Exception;
import com.edafa.ws.utils.configs.SendingRateLimiter;
import com.sun.xml.rpc.processor.modeler.j2ee.xml.string;

@ManagedBean
@ViewScoped
public class LimitersBean {

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI
			.name());
	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication()
			.getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName,
			locale);
	LimiterModel sendingRateLimiter = new LimiterModel();
	LimiterModel newSendingRateLimiter = new LimiterModel();
	List<SendingRateLimiter> sendingRateLimiters = new ArrayList<SendingRateLimiter>();
	List<SendingRateLimiter> runningSendingRateLimiters = new ArrayList<SendingRateLimiter>();
	List<LimiterModel> limiterModelList = new ArrayList<LimiterModel>();

	List<LimiterModel> defaultLimiters = new ArrayList<LimiterModel>();
	LimiterModel campDefault;
	LimiterModel smsDefault;

	List<LimiterModel> systemLimiters = new ArrayList<LimiterModel>();

	LimiterModel campSysLimiter;
	LimiterModel smsSysLimiter;

	@EJB
	AdminConfigManagementBeanLocal adminConfigManagementBeanLocal;

	@PostConstruct
	public void init() {
		fillPrps();
	}

	private void fillPrps() {
		fillTable();
	}

	private void fillTable() {
		sendingRateLimiters = new ArrayList<SendingRateLimiter>();
		runningSendingRateLimiters = new ArrayList<SendingRateLimiter>();

		List<SendingRateLimiter> list = new ArrayList<SendingRateLimiter>();
		try {
			logger.info("Reading limiters ");
			list = adminConfigManagementBeanLocal.readLimiters();
			logger.info("found ["+list.size()+" ] limiters ");

			if (list != null && !list.isEmpty()) {
				sendingRateLimiters = new ArrayList<SendingRateLimiter>(list);
			} else {
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("noResultFound")),
								null));
			}
			list = new ArrayList<SendingRateLimiter>();
			logger.info("get Running limiters ");

			list = adminConfigManagementBeanLocal.getRunningLimiters();
			logger.info("found ["+list.size()+" ] running limiters ");

			runningSendingRateLimiters = new ArrayList<SendingRateLimiter>(list);
			
			logger.info("converting limiters and running limiters into limiters Model");

			limiterModelList = sendingRateLimiter.convertToLimiterModelList(
					sendingRateLimiters, runningSendingRateLimiters);

			logger.info("getting default limiters");

			defaultLimiters = sendingRateLimiter
					.getDefaultLimiters(runningSendingRateLimiters);
			logger.info("found ["+ defaultLimiters.size()+"] default limiters");

			for (LimiterModel limit : defaultLimiters) {
				if (limit.getLimiterId().equals(
						SystemLimiters.SmsapiDefaultLimiter.toString()))
					smsDefault = limit;
				if (limit.getLimiterId().equals(
						SystemLimiters.CampDefaultLimiter.toString()))
					campDefault = limit;

			}
			logger.info("getting system limiters");

			systemLimiters = sendingRateLimiter
					.getSystemLimiters(runningSendingRateLimiters);
			logger.info("found ["+ systemLimiters.size()+"] system limiters");

			for (LimiterModel limit : systemLimiters) {
				if (limit.getLimiterId().equals(
						SystemLimiters.CampSystemLimiter.toString()))
					campSysLimiter = limit;
				if (limit.getLimiterId().equals(
						SystemLimiters.SmsapiSystemLimiter.toString()))
					smsSysLimiter = limit;

			}

			if (limiterModelList.size() == 0) {
				logger.info("No Limiters are found");

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format("No Limiters are found"), null));
			}
		} catch (FailedToReadConfigsException_Exception e1) {
			logger.error("Error while filling table", e1);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error("Error while filling table", e);
		}
	}

	public String AddLimiter() {
		boolean campExceed = false;
		boolean smsExceed = false;
		
		logger.info("Trying to create new limiter " + newSendingRateLimiter);

		SendingRateLimiter sendingRateLimiterEntity = newSendingRateLimiter
				.convertToEntity(newSendingRateLimiter);
		try {

			int campLimiters = 0;
			int smsLimiters = 0;
			for (SendingRateLimiter limiter : sendingRateLimiters) {
				if (limiter.isCampEnabled())
					campLimiters += limiter.getMaxPermits();
				if (limiter.isSmsapiEnabled())
					smsLimiters += limiter.getMaxPermits();
			}
			if (sendingRateLimiterEntity.isCampEnabled()) {
				newSendingRateLimiter = new LimiterModel();
				if (campSysLimiter.getMaxPermits() < (campDefault
						.getMaxPermits() + campLimiters + sendingRateLimiterEntity
							.getMaxPermits())) {
					campExceed = true;
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format("The total configured campaign limiters and default campaign limiter exceed the campaign sending rate"),
											null));
					logger.info("The total configured campaign limiters and default campaign limiter exceed the campaign sending rate");
				}
			}
			if (!campExceed && sendingRateLimiterEntity.isSmsapiEnabled()) {
				newSendingRateLimiter = new LimiterModel();

				if (smsSysLimiter.getMaxPermits() < (smsDefault.getMaxPermits()
						+ smsLimiters + sendingRateLimiterEntity
							.getMaxPermits())) {
					smsExceed = true;

					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format("The total configured SMS API limiters and default SMS API limiter exceed the SMS API sending rate"),
											null));
					logger.info("The total configured SMS API limiters and default SMS API limiter exceed the SMS API sending rate");

				}
			}
			if (!smsExceed && !campExceed) {
				adminConfigManagementBeanLocal
						.createLimiter(sendingRateLimiterEntity);

				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format("Limiter is created successfully"),
								null));
				logger.info("Limiter ["+sendingRateLimiterEntity+"] is created successfully");
			}
			fillPrps();

		} catch (FailedToSaveConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error("Error while filling table", e);
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
			logger.error("Error while filling table", e);
		}
		return "";
	}

	public String updateSendingRates() {
		try {
			logger.info("Trying to update limiter " + sendingRateLimiter);

			SendingRateLimiter sendingRateLimiterEntity = sendingRateLimiter
					.convertToEntity(sendingRateLimiter);
			boolean campExceed = false;
			boolean smsExceed = false;
			int campLimiters = 0;
			int smsLimiters = 0;
//			sendingRateLimiters.remove(sendingRateLimiter);
			
			
			for (SendingRateLimiter limiter : sendingRateLimiters) {
				if(limiter.getLimiterId().equals(sendingRateLimiter.getLimiterId()))
					continue;
				if (limiter.isCampEnabled())
					campLimiters += limiter.getMaxPermits();
				if (limiter.isSmsapiEnabled())
					smsLimiters += limiter.getMaxPermits();
			}
			if (sendingRateLimiterEntity.isCampEnabled()) {
				newSendingRateLimiter = new LimiterModel();
				if (campSysLimiter.getMaxPermits() < (campDefault
						.getMaxPermits() + campLimiters + sendingRateLimiterEntity
							.getMaxPermits())) {
					campExceed = true;
					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format("The total configured campaign limiters and default campaign limiter exceed the campaign sending rate"),
											null));
					logger.info("The total configured campaign limiters and default campaign limiter exceed the campaign sending rate");
				}
			}
			if (!campExceed && sendingRateLimiterEntity.isSmsapiEnabled()) {
				newSendingRateLimiter = new LimiterModel();

				if (smsSysLimiter.getMaxPermits() < (smsDefault.getMaxPermits()
						+ smsLimiters + sendingRateLimiterEntity
							.getMaxPermits())) {
					smsExceed = true;

					FacesContext
							.getCurrentInstance()
							.addMessage(
									null,
									new FacesMessage(
											FacesMessage.SEVERITY_ERROR,
											String.format("The total configured SMS API limiters and default SMS API limiter exceed the SMS API sending rate"),
											null));
					logger.info("The total configured SMS API limiters and default SMS API limiter exceed the SMS API sending rate");

				}
			}

			if (!smsExceed && !campExceed) {
				adminConfigManagementBeanLocal
						.saveLimiter(sendingRateLimiterEntity);
				logger.debug("limiter["+sendingRateLimiterEntity +"] updated successfully");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String
								.format(bundle.getString("processSucceeded")),
								null));
				fillTable();
			}
		} catch (FailedToSaveConfigsException_Exception e) {
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));

			logger.error(
					"Failed to save configuartions exception while updating configurations ",
					e);
		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return "";
	}

	public String RemoveSendingRate() {
		try {
			logger.info("Trying to remove limiter " + sendingRateLimiter);

			SendingRateLimiter sendingRateLimiterEntity = sendingRateLimiter
					.convertToEntity(sendingRateLimiter);
			adminConfigManagementBeanLocal
					.deleteLimiterAndRefresh(sendingRateLimiterEntity);
			logger.info("limiter"+sendingRateLimiterEntity+" is deleted successfully");

			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, String
									.format(bundle
											.getString("processSucceeded")),
									null));
			fillTable();
		} catch (FailedToSaveConfigsException_Exception e) {
			logger.error("Error while removing limiter", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Error while removing limiter", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return "";
	}

	public String applyConfigs() {
		try {
			logger.info("Applying limiter ["+sendingRateLimiter+"]");

			SendingRateLimiter sendingRateLimiterEntity = sendingRateLimiter
					.convertToEntity(sendingRateLimiter);

			adminConfigManagementBeanLocal
					.refreshLimiter(sendingRateLimiterEntity);
			logger.info(" limiter ["+sendingRateLimiterEntity+"] is applied successfully");

			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, String
									.format(bundle
											.getString("processSucceeded")),
									null));

			fillTable();
		} catch (FailedToSaveConfigsException_Exception e) {
			logger.error("FailedToSaveConfigsException_Exception", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return "";
	}

	public String applyAllConfigs() {
		try {
			logger.info("Applying all limiters");
			adminConfigManagementBeanLocal.refreshLimiters(sendingRateLimiters);
			logger.info("all limiters are applied successfully");
			FacesContext.getCurrentInstance()
					.addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_INFO, String
									.format(bundle
											.getString("processSucceeded")),
									null));
			fillTable();
		} catch (FailedToSaveConfigsException_Exception e) {
			logger.error("FailedToSaveConfigsException_Exception", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		} catch (Exception e) {
			logger.error("Error while updating configurations", e);
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String
							.format(bundle.getString("processFailed")), null));
		}
		return "";
	}

	public LimiterModel getSendingRateLimiter() {
		return sendingRateLimiter;
	}

	public void setSendingRateLimiter(LimiterModel sendingRateLimiter) {
		this.sendingRateLimiter = sendingRateLimiter;
	}

	public List<LimiterModel> getLimiterModelList() {
		return limiterModelList;
	}

	public void setLimiterModelList(ArrayList<LimiterModel> limiterModelList) {
		this.limiterModelList = limiterModelList;
	}

	public LimiterModel getNewSendingRateLimiter() {
		return newSendingRateLimiter;
	}

	public void setNewSendingRateLimiter(LimiterModel newSendingRateLimiter) {
		this.newSendingRateLimiter = newSendingRateLimiter;
	}

	public List<LimiterModel> getDefaultLimiters() {
		return defaultLimiters;
	}

	public void setDefaultLimiters(List<LimiterModel> defaultLimiters) {
		this.defaultLimiters = defaultLimiters;
	}

}