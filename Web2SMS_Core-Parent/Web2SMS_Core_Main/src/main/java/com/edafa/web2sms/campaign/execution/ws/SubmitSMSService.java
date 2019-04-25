package com.edafa.web2sms.campaign.execution.ws;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.smshandler.exceptions.FailedSMSException;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSSender;
import com.edafa.smsgw.smshandler.smpp.SMPPModuleAdapter;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.smsgw.smshandler.sms.SMSId;
import com.edafa.smsgw.smshandler.sms.interfaces.SMSHandlingManagerLocal;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class SubmitSMSService
 */
@Stateless
@WebService(name = "SubmitSMSService", serviceName = "SubmitSMSService", targetNamespace = "http://www.edafa.com/web2sms/sms/submit", endpointInterface = "com.edafa.web2sms.campaign.execution.ws.SubmitSMSServicePort")
public class SubmitSMSService implements SubmitSMSServicePort {

	Logger appLogger = LogManager.getLogger(LoggersEnum.WEB2SMS_APP_UTILS.name());

	/**
	 * Default constructor.
	 */
	public SubmitSMSService() {
		// TODO Auto-generated constructor stub
	}

	@EJB
	SMSHandlingManagerLocal smsHandlingManager;

	SMPPModuleAdapter smppAdapter;

	@PostConstruct
	void init() {
		smppAdapter = smsHandlingManager.getSmppAdapter();
	}

	@Override
	@WebMethod(operationName = "submitSMS")
	public String submitSMS(LanguageNameEnum language, String sender, String receiver, String smsText,
			boolean registeredDelivery) throws Exception {
		int submitSMSWaitingTime = (int) Configs.SUBMIT_SMS_WAITING_TIME.getValue();

		if (!smppAdapter.isReadyForSubmitting()) {
			int waitingTime = 2000;
			appLogger.info("The smpp module is not ready for submitting, waiting " + waitingTime);
			try {
				smppAdapter.waitUntilReadyForSending(2000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (smppAdapter.isReadyForSubmitting()) {
			SMS sms;
			try {

				sms = new SMS();
				sms.setSubmitDate(new Date());
				sms.setProcessingDate(new Date());
				sms.setSMSId(SMSId.getSMSId());
				sms.setSender(sender);
				sms.setReceiver(receiver);
				sms.setRegisteredDelivery(registeredDelivery);
				sms.setSmsText(smsText);
				sms.setLanguage(LanguageEnum.valueOf(language.name()));
				appLogger.info(sms.logId() + "Submitting " + sms.toString());
				while (!sms.isSubmitted()) {
                                    smppAdapter.submitSMS(sms, submitSMSWaitingTime, TimeUnit.SECONDS);
				}
				appLogger.info(sms.logId() + "is successfully submitted");
			} catch (InvalidSMSSender | FailedSMSException e) {
				throw new Exception(e.getMessage());
			}

			return sms.getSMSId().getId();
		} else {
			appLogger.info("The SMS is not submitted");
		}

		return null;
	}
}
