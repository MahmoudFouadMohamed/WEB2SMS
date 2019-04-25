package com.edafa.web2sms.campaign.execution;

import com.edafa.web2sms.core.execution.SmsHolder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.smshandler.exceptions.FailedSMSException;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSException;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSReceiver;
import com.edafa.smsgw.smshandler.exceptions.InvalidSMSSender;
import com.edafa.smsgw.smshandler.smpp.SMPPModuleAdapter;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.smsgw.smshandler.sms.SMSId;
import com.edafa.utils.rate.controller.exceptions.InvalidParameterException;
import com.edafa.utils.rate.controller.exceptions.NoSuchLimiterException;
import com.edafa.utils.string.StringUtilities;
import com.edafa.web2sms.campaign.execution.exception.CampaignExecutionException;
import com.edafa.web2sms.campaign.execution.exception.NoMoreContactsException;
import com.edafa.web2sms.campaign.execution.exception.RetrieveContactsException;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignAction;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignSMSDetails;
import com.edafa.web2sms.dalayer.model.CampaignScheduling;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.quota.exceptions.InSufficientQuotaException;
import com.edafa.web2sms.quota.interfaces.AccountQuotaHandle;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.Set;

public class CampaignHandle {
    
    protected Logger campaignsLogger = LogManager.getLogger(LoggersEnum.CAMP_EXE.name());
    protected transient boolean stateTranstion;
    protected boolean prepaidCamp;
    protected CampaignsHandler campaignsHandler;
    protected CampaignListsDaoLocal campaignListsDao;
    protected CampaignStatusDaoLocal campaignStatusDao;
    protected SMSStatusDaoLocal smsStatusDao;
    protected ContactDaoLocal contactDao;
    protected AccountQuotaHandle accountQuotaHandle;
    protected SMPPModuleAdapter smppAdapter;
    protected CampaignStatus campaignStatus;
    protected Campaign campaign;
    protected CampaignExecution campaignExecution;
    protected CampaignScheduling campaignScheduling;
    protected CampaignSMSDetails campaignSMSDetails;
    protected CampaignLists currentList;
    protected boolean reachedEOF;
    protected int positionIndex = 0;
    protected int contactIndex = 0;
    protected int listIndex = 0;
    protected long stopTime;
    protected SimpleDateFormat dtf = new SimpleDateFormat((String) Configs.LOG_TIMESTAMP_FORMAT.getValue());
    protected SimpleDateFormat df = new SimpleDateFormat((String) Configs.LOG_DATE_FORMAT.getValue());
    protected SimpleDateFormat tf = new SimpleDateFormat((String) Configs.LOG_TIME_FORMAT.getValue());

    protected List<CampaignLists> campaignLists;
    protected List<Contact> contacts;
    protected boolean rateEligible = true;
    protected SmsHolder delayedSmsHolder;
    protected Set<String> rateLimitersIds;

    public CampaignHandle(Campaign campaign, SMPPModuleAdapter smppAdapter, CampaignStatusDaoLocal campaignStatusDao,
            SMSStatusDaoLocal smsStatusDao, CampaignListsDaoLocal campaignListsDao, ContactDaoLocal contactDao,
            AccountQuotaHandle accountQuotaHandle, CampaignsHandler campaignsHandler, Set<String> rateLimitersIds) {
        this(campaign, smppAdapter, campaignStatusDao, smsStatusDao, campaignListsDao, contactDao, accountQuotaHandle,
                true, campaignsHandler, rateLimitersIds);
    }

    public CampaignHandle(Campaign campaign, SMPPModuleAdapter smppAdapter, CampaignStatusDaoLocal campaignStatusDao,
            SMSStatusDaoLocal smsStatusDao, CampaignListsDaoLocal campaignListsDao, ContactDaoLocal contactDao,
            AccountQuotaHandle accountQuotaHandle, boolean stateTranstion, CampaignsHandler campaignsHandler, Set<String> rateLimitersIds) {
        this.campaign = campaign;
        this.smppAdapter = smppAdapter;
        this.campaignStatusDao = campaignStatusDao;
        this.smsStatusDao = smsStatusDao;
        this.campaignListsDao = campaignListsDao;
        this.contactDao = contactDao;
        this.campaignExecution = campaign.getCampaignExecution();
        this.campaignSMSDetails = campaign.getSmsDetails();
        this.campaignScheduling = campaign.getCampaignScheduling();
        this.campaignStatus = campaign.getStatus();
        this.accountQuotaHandle = accountQuotaHandle;
        this.stateTranstion = stateTranstion;
        this.campaignsHandler = campaignsHandler;
        this.rateLimitersIds = rateLimitersIds;
    }

    public void startCampaign() throws CampaignExecutionException, RetrieveContactsException {
        Date now = new Date();
        campaignExecution.setProcessingTimestamp(now);
        campaignsLogger.info(campaign.logId() + "Will be started");
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "processingTimestamp: " + dtf.format(now) + ", campaign details: " + campaign);
        }
        setStopTime();
        try {
            switch (campaignStatus.getCampaignStatusName()) {
                case NEW:
                case WAITING_APPROVAL:
                case APPROVED:    
                    if (campaignExecution.getStartTimestamp() == null) {
                        campaignExecution.setStartTimestamp(now);
                    }
                    campaignsLogger.info(campaign.logId() + "Campaign startTimestamp=" + dtf.format(campaignExecution.getStartTimestamp()));
                case PARTIAL_RUN:
                    if (campaignScheduling.getScheduleFrequency().getScheduleFreqName().equals(ScheduleFrequencyName.ONCE)) {
                        loadSubmittableCampaignLists();
                    } else {
                        loadCampaignLists();
                    }
                    break;
                case PAUSED:
                case ON_HOLD:
                    loadSubmittableCampaignLists();
                    break;

                default:
                    break;
            }

            bufferCampaignListContacts();
        } catch (NoMoreContactsException | DBException e) {
            CampaignExecutionException campException = new CampaignExecutionException("Contacts list is null or there are no contact to submit SMS to.");
            handleExecutionException(campException);
            throw campException;
        }
        // Increment execution counter
        int exeCount = campaignExecution.getExecutionCount();
        exeCount += 1;
        campaignExecution.setExecutionCount(exeCount);
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Increment execution counter, execution count=" + exeCount);
        }

        // Setting the new status to RUNNING
        setCampaignStatus(CampaignStatusName.RUNNING);
    }

    protected void loadCampaignLists() throws DBException, NoMoreContactsException {
        campaignsLogger.info(campaign.logId() + "Retrieving all campaign lists");
        campaignLists = campaignListsDao.findByCampaignIdOrdered(campaign.getCampaignId());
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Found (" + campaignLists.size() + ") campaign lists");
        }

        // Reset submittion counters for frequent campaigns
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Reset submittion counters");
        }
        for (CampaignLists campaignList : campaignLists) {
            if (campaignsLogger.isTraceEnabled()) {
                campaignsLogger.trace(campaign.logId() + "List " + campaignList.logId() + "set submittedSMSCount=" + campaignList.getSubmittedSMSCount());
            }
            campaignList.setSubmittedSMSCount(0);
            if (campaignsLogger.isTraceEnabled()) {
                campaignsLogger.trace(campaign.logId() + "List " + campaignList.logId() + "submitted SMS count is reset");
            }
        }

        if (this.listIndex < campaignLists.size()) {
            CampaignLists currentList = campaignLists.get(listIndex);
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "Current list " + currentList.logId());
            }
            positionIndex = currentList.getSubmittedSMSCount();
        } else {
            throw new NoMoreContactsException(campaign.getCampaignId());
        }

    }

    protected void loadSubmittableCampaignLists() throws NoMoreContactsException, DBException {
        campaignsLogger.info(campaign.logId() + "Retrieving submittable campaign lists");
        campaignLists = campaignListsDao.findSubmittableByCampaignIdOrdered(campaign.getCampaignId());
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Found (" + campaignLists.size() + ") submittable campaign lists");
        }

        if (this.listIndex < campaignLists.size()) {
            CampaignLists currentList = campaignLists.get(listIndex);
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "Current list " + currentList.logId());
            }
            positionIndex = currentList.getSubmittedSMSCount();
        } else {
            throw new NoMoreContactsException(campaign.getCampaignId());
        }
    }

    protected void setStopTime() {
        Date stopTime = campaignScheduling.getScheduleStopTime();
        if (stopTime != null) {
            Calendar refTimestamp = Calendar.getInstance();
            // if
            // (campaignScheduling.getScheduleFrequency().getScheduleFreqName().equals(ScheduleFrequencyName.ONCE))
            // {
            // refTimestamp.setTime(campaignScheduling.getScheduleStartTimestamp());
            // }
            Calendar cal = Calendar.getInstance();
            cal.setTime(stopTime);
            cal.set(Calendar.YEAR, refTimestamp.get(Calendar.YEAR));
            cal.set(Calendar.MONTH, refTimestamp.get(Calendar.MONTH));
            cal.set(Calendar.DAY_OF_MONTH, refTimestamp.get(Calendar.DAY_OF_MONTH));
            this.stopTime = cal.getTimeInMillis();
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "stop time: " + dtf.format(cal.getTime()) + ", in milles=" + this.stopTime);
            }
        } else {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "No campaign stop time, stopTime=" + this.stopTime);
            }
        }
    }

    protected boolean reachedStopTime() {
        return stopTime > 0 ? System.currentTimeMillis() >= stopTime : false;
    }

    protected void bufferCampaignListContacts() throws RetrieveContactsException {
        int bulkSMSBufferSize = (int) Configs.CONTACTS_BUFFER_SIZE.getValue();
        if (campaignsLogger.isTraceEnabled()) {
            campaignsLogger.trace(campaign.logId() + "listIndex=" + listIndex + ", campaignLists size=" + campaignLists.size());
        }

        // To determine if the current list does not has any more
        // contacts from
        // the last buffer cycle
        if (currentList != null && contacts != null && contacts.size() < bulkSMSBufferSize) {
            listIndex++;
            contactIndex = 0;
            positionIndex = 0;
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "No more contacts from list " + currentList.logId() + "check for more lists and contacts");
            }
        }

        while (listIndex < campaignLists.size()) {
            retrieveContacts();

            if (!contacts.isEmpty()) {
                break;
            }
            // The current list contacts are all submitted as SMSs
            // Increment the list index to the next list
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "No more contacts in list " + currentList.logId() + "check for more lists and contacts");
            }
            listIndex++;
            contactIndex = 0;
            positionIndex = 0;
        }

        if (listIndex >= campaignLists.size()) {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "No more lists, reached end of campaign lists for execution");
            }
            reachedEOF = true;
        }
    }

    private void retrieveContacts() throws RetrieveContactsException {
        int bulkSMSBufferSize = (int) Configs.CONTACTS_BUFFER_SIZE.getValue();
        currentList = campaignLists.get(listIndex);
        // positionIndex = currentList.getSubmittedSMSCount();
        campaignsLogger.info(campaign.logId() + "Current list " + currentList.logId() + "retrieve contacts, positionIndex=" + positionIndex + ", bulkSMSBufferSize=" + bulkSMSBufferSize);
        try {
            contacts = contactDao.findByListID(currentList.getCampaignListsPK().getListId(), positionIndex, bulkSMSBufferSize);
        } catch (Exception e) {
            throw new RetrieveContactsException(e.getMessage(), e);
        }

        if (contacts.size() > 0) {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "Retrieved (" + contacts.size() + ") contacts from list " + currentList.logId());
            }
            contactIndex = 0;
        }
    }

    public int submitNewSMS() throws CampaignExecutionException, RetrieveContactsException {
        if (contacts == null) {
            CampaignExecutionException campException = new CampaignExecutionException("Contacts list is null or there are no contact to submit SMS to.");
            handleExecutionException(campException);
            throw campException;
        }

        SMS sms = null;
        int segCount = 0;
        try {

            if (reachedStopTime()) {
                campaignsLogger.warn(campaign.logId() + "Campaign reached stop time, stopTime: " + dtf.format(campaignScheduling.getScheduleStopTime()));
            }

            if (contactIndex >= contacts.size()) {
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(campaign.logId() + "Submitted SMS for all buffered contacts, try to buffer more contacts");
                }
                bufferCampaignListContacts();
            }

            if (reachedEOF) {
                campaignsLogger.info(campaign.logId() + "Campaign reached the end, submittedSMS=" + campaignExecution.getSubmittedSmsCount());
                return 0;
            }

            if (campaignsLogger.isTraceEnabled()) {
                campaignsLogger.trace(campaign.logId() + "Submitting SMS(" + (positionIndex + 1) + ")");
            }

            Contact contact = contacts.get(contactIndex);

            sms = new SMS();
            sms.setSMSId(SMSId.getSMSId());
            sms.setServiceInfo(currentList.getCampaignListsPK().getListId());
            sms.setSubmitDate(campaign.getCreationTimestamp());
            sms.setProcessingDate(new Date());
            sms.setLanguage(LanguageEnum.valueOf(campaignSMSDetails.getLanguage().getLanguageName().name()));
            sms.setRecordIdentifier(positionIndex);
            sms.setOwnerId(campaign.getAccountUser().getAccountId());
            sms.setGroupId(campaign.getCampaignId());
            sms.setRegisteredDelivery(campaignSMSDetails.getRegisteredDelivery());

            sms.setSender(campaignSMSDetails.getSenderName());
            sms.setReceiver(contact.getListContactsPK().getMsisdn());

            //TODO: edit for customized
            if (campaign.getType().getCampaignTypeName().equals(CampaignTypeName.CUSTOMIZED_CAMPAIGN)) {

                String smsText = getTextFromScript(campaignSMSDetails.getSMSText(), contact);

                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace("Checking the Language of the SmS text...");
                }
                String smsTextType = checkLanguage(smsText);

                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace("SMS Text Type is : " + smsTextType);
                }

                if (smsTextType.equals("ENGLISH")) {
                    if (campaignsLogger.isTraceEnabled()) {
                        campaignsLogger.trace("Setting the language of the SMS to (ENGLISH) ");
                    }
                    sms.setLanguage(LanguageEnum.ENGLISH);
                } else {
                    if (campaignsLogger.isTraceEnabled()) {
                        campaignsLogger.trace("Setting the language of the SMS to (ARABIC) ");
                    }
                    sms.setLanguage(LanguageEnum.ARABIC);
                }

                sms.setSmsText(smsText);

            } else {
                sms.setSmsText(campaignSMSDetails.getSMSText());

            }

            SmsHolder smsHolder = new SmsHolder(sms, rateLimitersIds, campaignsLogger, campaign.logId());
            smsHolder.setSmsSendingRateController(campaignsHandler.getRateController());

            if (smsHolder.isSMSAllowedToSend()) {
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(smsHolder.getLogId() + " allowed to send");
                }
                sendSms(smsHolder.getSms(), smsHolder.getLogId());
            } else {
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(smsHolder.getLogId() + " NOT allowed to send, will send it leater");
                }
                delayedSmsHolder = smsHolder;
                rateEligible = false;
                campaignsHandler.incrementRateIneligibleCampaigns();
            }
            incrementPosition();
        } catch (InvalidSMSSender e) {
            sms.setComment(e.toString());
            sms.setStatus(com.edafa.smsgw.dalayer.enums.SMSStatusName.FAILED_TO_SEND);
            campaignsLogger.error(campaign.logId() + e.getMessage() + " SMS: " + sms.logFailedToSend());
            incrementPosition();
            CampaignExecutionException campException = new CampaignExecutionException(e);
            handleExecutionException(campException);
            throw campException;
        } catch (InvalidSMSReceiver | NoSuchLimiterException | InvalidParameterException e) {
            sms.setComment(e.toString());
            sms.setStatus(com.edafa.smsgw.dalayer.enums.SMSStatusName.FAILED_TO_SEND);
            campaignsLogger.error(campaign.logId() + e.getMessage() + " SMS: " + sms.logFailedToSend());
            incrementSubmitted();
            incrementPosition();
        } catch (RetrieveContactsException e) {
            throw e;
        } catch (Exception e) {
            CampaignExecutionException campException = new CampaignExecutionException(e);
            handleExecutionException(campException);
            throw campException;
        }
        return sms.getSubmittedSegCount();
    }
    
    private void sendSms(SMS sms, String logID) throws CampaignExecutionException {
        int segCount = 0;
        int submittedSMSSegments = 0;
        try {
            int submitSMSWaitingTime = (int) Configs.SUBMIT_SMS_WAITING_TIME.getValue();
            segCount = accountQuotaHandle.drainReserved(sms.getLanguage(), sms.getSmsText());

            submittedSMSSegments = smppAdapter.submitSMS(sms, submitSMSWaitingTime, TimeUnit.SECONDS);
            int submittedSMSSegCount = campaignExecution.getSubmittedSmsSegCount() != null ? campaignExecution.getSubmittedSmsSegCount() : 0;
            campaignExecution.setSubmittedSmsSegCount(submittedSMSSegCount + submittedSMSSegments);

            if (!sms.isSubmitted()) {
                sms.setStatus(com.edafa.smsgw.dalayer.enums.SMSStatusName.FAILED_TO_SEND);
                campaignsLogger.warn(logID + "SMS submitted partially: " + sms.logFailedToSend());
                accountQuotaHandle.reserveRollback(segCount);
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(campaign.logId() + segCount + " reserved SMS rollbacked.");
                }

            } else {
                accountQuotaHandle.consumeUnit(segCount);
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(logID + "Consume from reserved: " + segCount);
                }
            }

            incrementSubmitted();
        } catch (FailedSMSException e) {
            sms.setStatus(com.edafa.smsgw.dalayer.enums.SMSStatusName.FAILED_TO_SEND);
            sms.setComment(e.toString());
            campaignsLogger.error(campaign.logId() + e + " SMS: " + sms.logFailedToSend());
            accountQuotaHandle.reserveRollback(segCount);
            if (campaignsLogger.isTraceEnabled()) {
                campaignsLogger.trace(campaign.logId() + segCount + "reserved SMS rollbacked.");
            }
            incrementSubmitted();
        } catch (InSufficientQuotaException e) {
            accountQuotaHandle.reserveRollback(segCount);
            campaignsLogger.warn(campaign.logId() + segCount + " Quota can't be negative value");
            incrementSubmitted();
        } catch (Exception e) {
            CampaignExecutionException campException = new CampaignExecutionException(e);
            handleExecutionException(campException);
            throw campException;
        }
    }

    public String checkLanguage(String smsText) {
        String gsm7bitChars = "[]{}@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

        boolean engMsg = true;
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug("Parsing the text character by character to check if there is an Arabic letter...");
        }
        if (smsText != null) {
            for (int i = 0; i < smsText.length(); i++) {
                if (engMsg || smsText.length() == 1) {
                    if (gsm7bitChars.indexOf(smsText.charAt(i)) == -1) {
                        engMsg = false;
                        break;
                    }
                }
            }
        }
        if (engMsg) {
            return "ENGLISH";
        } else {
            return "ARABIC";
        }
    }

    private String getTextFromScript(String smsText, Contact contact) {
        smsText = StringUtilities.fastReplace(smsText, "$1", contact.getValue1() != null ? contact.getValue1() : "");
        smsText = StringUtilities.fastReplace(smsText, "$2", contact.getValue2() != null ? contact.getValue2() : "");
        smsText = StringUtilities.fastReplace(smsText, "$3", contact.getValue3() != null ? contact.getValue3() : "");
        smsText = StringUtilities.fastReplace(smsText, "$4", contact.getValue4() != null ? contact.getValue4() : "");
        smsText = StringUtilities.fastReplace(smsText, "$5", contact.getValue5() != null ? contact.getValue5() : "");
        return smsText;
    }

    private void incrementSubmitted() {
        int submittedSMSCount = campaignExecution.getSubmittedSmsCount() != null ? campaignExecution.getSubmittedSmsCount() : 0;
        campaignExecution.setSubmittedSmsCount(submittedSMSCount + 1);
        currentList.setSubmittedSMSCount(currentList.getSubmittedSMSCount() + 1);
        currentList.setTotalSubmittedSMSCount(currentList.getTotalSubmittedSMSCount() + 1);
    }

    private void incrementPosition() {
        contactIndex += 1;
        positionIndex += 1;
    }

    public void finalizeCampaign() {
        String logMsg;
        if (campaignScheduling.getScheduleFrequency().getScheduleFreqName().equals(ScheduleFrequencyName.ONCE)) {
            campaignExecution.setEndTimestamp(new Date());
            if (campaignsLogger.isDebugEnabled()) {
                logMsg = campaign.logId() + "is finished";
                campaignsLogger.debug(logMsg);
            }
            setCampaignStatus(CampaignStatusName.FINISHED);
        } else {
//            Date schedEndDate = campaignScheduling.getScheduleEndDate();
//            Calendar now = Calendar.getInstance();
            Calendar expectedNextRunTime = getExpectedNextExeTime();

            // Check if valid for future execution
            if (isValidForExecution()) {
                // if (schedEndDate.after(now.getTime())) {
                // if (expectedNextRunTime.compareTo(now) > 0) {
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(campaign.logId() + "The campaign end date is not reached yet, expected next run time=" + dtf.format(expectedNextRunTime.getTime()));
                }
                if (campaignsLogger.isDebugEnabled()) {
                    logMsg = campaign.logId() + "Campaign is partially run and will stop now";
                    campaignsLogger.debug(logMsg);
                }
                    setCampaignStatus(CampaignStatusName.PARTIAL_RUN);
                // } else {
                // campaignsLogger
                // .debug(campaign.logId()
                // +
                // "The campaign end date is not reached yet, however there are no expected executions in the future");
                // campaignExecution.setEndTimestamp(new Date());
                // logMsg = campaign.logId() + "is finished";
                // campaignsLogger.debug(logMsg);
                // setCampaignStatus(CampaignStatusName.FINISHED);
                // }
            } else {
                campaignExecution.setEndTimestamp(new Date());
                if (campaignsLogger.isDebugEnabled()) {
                    logMsg = campaign.logId() + "is finished";
                    campaignsLogger.debug(logMsg);
                }
                setCampaignStatus(CampaignStatusName.FINISHED);
            }
        }
    }

    public void pauseCampaign() {
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "will be paused");
        }
        setCampaignStatus(CampaignStatusName.PAUSED);
    }

    public void holdCampaign(String holdReason) {
        campaignExecution.setComments(holdReason);
        campaignsLogger.info("Will be held");
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "has been held. Reason: " + holdReason);
        }
        setCampaignStatus(CampaignStatusName.ON_HOLD);
    }

    public void resumeCampaign() throws CampaignExecutionException, RetrieveContactsException {
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "will be resumed");
        }
        startCampaign();
    }

    public void cancelCampaign() {
        // TODO: Should cancel sent sms but delivered requests
        int submittedSMS = campaignExecution.getSubmittedSmsSegCount();
        int reservedValue = campaignSMSDetails.getSMSSegCount();
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "will be cancelled with  submittedSMS:" + submittedSMS + ", reservedValue:" + reservedValue + ".");
        }
        campaignExecution.setEndTimestamp(new Date());
        setCampaignStatus(CampaignStatusName.CANCELLED);
//		accountQuotaHandle.consumeUnit(submittedSMS);
        // rollback increment reservation so in this case it should receive
        // negative value to decrement the reservation value
        accountQuotaHandle.reserveRollback(submittedSMS - reservedValue);
    }
    
        public void rejectCampaign() {
        int submittedSMS = campaignExecution.getSubmittedSmsSegCount();
        int reservedValue = campaignSMSDetails.getSMSSegCount();
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "will be rejected with submittedSMS:" + submittedSMS + ", reservedValue:" + reservedValue + ".");
        }
        campaignExecution.setEndTimestamp(new Date());
        setCampaignStatus(CampaignStatusName.REJECTED);
//		accountQuotaHandle.consumeUnit(submittedSMS);
        // rollback increment reservation so in this case it should receive
        // negative value to decrement the reservation value
        accountQuotaHandle.reserveRollback(submittedSMS - reservedValue);
    }

    public void obsoleteCampaign() {
        CampaignStatusName campStatus = getCampaign().getStatus().getCampaignStatusName();
        switch (campStatus) {
            case WAITING_APPROVAL:
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Will be Approval Obsoleted");
                }
                setCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE);
                break;
            case APPROVED:
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Will be Send Obsoleted");
                }
                setCampaignStatus(CampaignStatusName.SEND_OBSOLETE);
                break;
            default:
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Will be obsoleted");
                }
                setCampaignStatus(CampaignStatusName.OBSOLETE);
        }    

        int submittedSMS = campaignExecution.getSubmittedSmsSegCount();
        int reservedValue = campaignSMSDetails.getSMSSegCount();
//		accountQuotaHandle.consumeUnit(submittedSMS);
        accountQuotaHandle.reserveRollback(submittedSMS - reservedValue);
    }

    public void approveCampaign() {
        setCampaignStatus(CampaignStatusName.APPROVED);
    }

    public void handleExecutionException(Exception campException) {
        String logMsg = "Campaign excution failed: " + campException;
        setCampaignStatus(CampaignStatusName.FAILED);
        setCampaignStatusComment(campException.toString());
        campaignsLogger.error(campaign.logId() + logMsg);
        int submittedSMS = campaignExecution.getSubmittedSmsSegCount();
        int reservedValue = campaignSMSDetails.getSMSSegCount();
//		accountQuotaHandle.consumeUnit(submittedSMS);
        accountQuotaHandle.reserveRollback(submittedSMS - reservedValue);
    }

    public boolean isFinished() {
        return reachedEOF || reachedStopTime();
    }

    public Campaign getCampaign() {
        return campaign;
    }

    public void setCampaign(Campaign campaign) {
        this.campaign = campaign;
    }

    void setCampaignStatus(CampaignStatusName statusName) {
        CampaignStatus oldStatus = campaignStatus;
        campaignStatus = campaignStatusDao.getCachedObjectByName(statusName);
        campaign.setStatus(campaignStatus);
        campaignsLogger.info(campaign.logId() + "status changed from " + oldStatus.getCampaignStatusName().name() + " to " + campaignStatus.getCampaignStatusName().name());
    }

    public boolean hasCancelAction() {
        CampaignAction campaignAction;
        if ((campaignAction = campaignExecution.getAction()) != null) {
            return campaignAction.getCampaignActionName() == CampaignActionName.CANCEL ? true : false;
        }
        return false;
    }

    public boolean hasPauseAction() {
        CampaignAction campaignAction;
        if ((campaignAction = campaignExecution.getAction()) != null) {
            return campaignAction.getCampaignActionName() == CampaignActionName.PAUSE ? true : false;
        }
        return false;
    }

    public boolean hasHoldAction() {
        CampaignAction campaignAction;
        if ((campaignAction = campaignExecution.getAction()) != null) {
            return campaignAction.getCampaignActionName() == CampaignActionName.HOLD ? true : false;
        }
        return false;
    }

    public boolean hasResumeAction() {
        CampaignAction campaignAction;
        if ((campaignAction = campaignExecution.getAction()) != null) {
            return campaignAction.getCampaignActionName() == CampaignActionName.RESUME ? true : false;
        }
        return false;
    }

    public boolean hasUnHoldAction() {
        CampaignAction campaignAction;
        if ((campaignAction = campaignExecution.getAction()) != null) {
            return campaignAction.getCampaignActionName() == CampaignActionName.UN_HOLD ? true : false;
        }
        return false;
    }

    public boolean isRunning() {
        return (campaign.getStatus().getCampaignStatusName() == CampaignStatusName.RUNNING);
    }

    public void updateCampaignStatus(CampaignStatus status) {
        campaign.setStatus(status);
    }

    public void unHoldCampaign() throws CampaignExecutionException, RetrieveContactsException {
        startCampaign();
    }

    public void setCampaignAction(CampaignAction campaignAction) {
        campaignExecution.setAction(campaignAction);
    }

    public void setCampaignHandlerId(int handlerId) {
        campaignExecution.setHandlerId(String.valueOf(handlerId));
    }

    public void unSetCampaignHandler() {
        campaignExecution.setHandlerId(null);
    }

    public boolean isFailed() {
        return campaign.getStatus().getCampaignStatusName() == CampaignStatusName.FAILED;
    }

    /*
     * Check campaign validity in the future
     */
    //TODO .. REFRESH >> Explain 
    public boolean isValidForExecution() {
        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Check campaign validity for execution");
        }
        Calendar cal = Calendar.getInstance();
        Calendar validityCal = Calendar.getInstance();
        Calendar approvalValidityCal = Calendar.getInstance();
        int validityPeriod = 0;
        int approvalValidityPeriod = 0;
        int totalValidityPeriod = 0;
        boolean isValid = false;
        Calendar nextExeTime = getExpectedNextExeTime();
        switch (campaignStatus.getCampaignStatusName()) {
            case NEW:
            case PARTIAL_RUN:                
                validityPeriod = (int) Configs.CAMP_EXE_VALIDITY_PERIOD.getValue();
                validityCal.add(Calendar.HOUR_OF_DAY, -validityPeriod);
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Validity timestamp=" + dtf.format(validityCal.getTime()));
                }
                switch (campaignScheduling.getScheduleFrequency().getScheduleFreqName()) {
                    case ONCE:
                        isValid = (nextExeTime.compareTo(validityCal) >= 0);
                        break;
                    default:
                        Calendar schedStartTimestamp = Calendar.getInstance();
                        schedStartTimestamp.setTime(campaignScheduling.getScheduleStartTimestamp());
                        Calendar schedEndDate = Calendar.getInstance();
                        schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
                        schedEndDate.set(Calendar.HOUR, schedStartTimestamp.get(Calendar.HOUR_OF_DAY));
                        schedEndDate.set(Calendar.MINUTE, schedStartTimestamp.get(Calendar.MINUTE));
                        schedEndDate.set(Calendar.SECOND, schedStartTimestamp.get(Calendar.SECOND));
                        schedEndDate.set(Calendar.MILLISECOND, schedStartTimestamp.get(Calendar.MILLISECOND));

                        isValid = (nextExeTime.compareTo(validityCal) >= 0 && nextExeTime.compareTo(schedEndDate) <= 0);
                }
                break;
            case WAITING_APPROVAL:
                approvalValidityPeriod = (int) Configs.CAMP_APPROVAL_VALIDITY_PERIOD.getValue();
                approvalValidityCal.add(Calendar.HOUR_OF_DAY, -approvalValidityPeriod);
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Approval validity timestamp=" + dtf.format(approvalValidityCal.getTime()));
                }
                switch (campaignScheduling.getScheduleFrequency().getScheduleFreqName()) {
                    case ONCE:
                        isValid = (nextExeTime.compareTo(approvalValidityCal) >= 0);
                        break;
                    default:
                        Calendar schedStartTimestamp = Calendar.getInstance();
                        schedStartTimestamp.setTime(campaignScheduling.getScheduleStartTimestamp());
                        Calendar schedEndDate = Calendar.getInstance();
                        schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
                        schedEndDate.set(Calendar.HOUR, schedStartTimestamp.get(Calendar.HOUR_OF_DAY));
                        schedEndDate.set(Calendar.MINUTE, schedStartTimestamp.get(Calendar.MINUTE));
                        schedEndDate.set(Calendar.SECOND, schedStartTimestamp.get(Calendar.SECOND));
                        schedEndDate.set(Calendar.MILLISECOND, schedStartTimestamp.get(Calendar.MILLISECOND));

                        isValid = (nextExeTime.compareTo(approvalValidityCal) >= 0 && nextExeTime.compareTo(schedEndDate) <= 0);
                }
                break;
            case APPROVED:
                validityPeriod = (int) Configs.CAMP_EXE_VALIDITY_PERIOD.getValue();
                approvalValidityPeriod = (int) Configs.CAMP_APPROVAL_VALIDITY_PERIOD.getValue();
                totalValidityPeriod = validityPeriod + approvalValidityPeriod;
                approvalValidityCal.add(Calendar.HOUR_OF_DAY, -totalValidityPeriod);
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Approval validity timestamp=" + dtf.format(approvalValidityCal.getTime()));
                }
                switch (campaignScheduling.getScheduleFrequency().getScheduleFreqName()) {
                    case ONCE:
                        isValid = (nextExeTime.compareTo(approvalValidityCal) >= 0);
                        break;
                    default:
                        Calendar schedStartTimestamp = Calendar.getInstance();
                        schedStartTimestamp.setTime(campaignScheduling.getScheduleStartTimestamp());
                        Calendar schedEndDate = Calendar.getInstance();
                        schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
                        schedEndDate.set(Calendar.HOUR, schedStartTimestamp.get(Calendar.HOUR_OF_DAY));
                        schedEndDate.set(Calendar.MINUTE, schedStartTimestamp.get(Calendar.MINUTE));
                        schedEndDate.set(Calendar.SECOND, schedStartTimestamp.get(Calendar.SECOND));
                        schedEndDate.set(Calendar.MILLISECOND, schedStartTimestamp.get(Calendar.MILLISECOND));

                        isValid = (nextExeTime.compareTo(approvalValidityCal) >= 0 && nextExeTime.compareTo(schedEndDate) <= 0);
                }
                break;
            case PAUSED:
            case ON_HOLD:
                cal.setTime(campaignExecution.getProcessingTimestamp());
                validityPeriod = (int) Configs.CAMP_VALIDITY_PERIOD.getValue();
                validityCal.add(Calendar.HOUR_OF_DAY, -validityPeriod);
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(campaign.logId() + "Validity timestamp=" + dtf.format(validityCal.getTime()));
                }
                isValid = (cal.compareTo(validityCal) > 0);
                break;
            case RUNNING:
                isValid = true;
                break;
            default:
                isValid = false;
        }

        if (isValid) {
            campaignsLogger.info(campaign.logId() + "Campaign is valid for execution");
        } else {
            campaignsLogger.info(campaign.logId() + "Campaign is not valid for execution");
        }

        return isValid;
    }

    public boolean isTimeToExecute() {
        if (campaignsLogger.isTraceEnabled()) {
            campaignsLogger.trace(campaign.logId() + "Check if now is the time to execute campaign");
        }
        boolean timeToExe = false;
        int campaignValidityPeriod = 0;
        int campaignApprovalValidityPeriod = 0;
        int totalValidityPeriod = 0;
        Calendar now = Calendar.getInstance();
        Calendar nextRunTime = Calendar.getInstance();
        Calendar validityCal = Calendar.getInstance();
        campaignValidityPeriod = (int) Configs.CAMP_EXE_VALIDITY_PERIOD.getValue();
        campaignApprovalValidityPeriod = (int) Configs.CAMP_APPROVAL_VALIDITY_PERIOD.getValue();
        
        if (getCampaignStatus().getCampaignStatusName() == CampaignStatusName.APPROVED || getCampaignStatus().getCampaignStatusName() == CampaignStatusName.WAITING_APPROVAL) {
            totalValidityPeriod = campaignValidityPeriod + campaignApprovalValidityPeriod;
        } else {
            totalValidityPeriod = campaignValidityPeriod;
        }
        validityCal.add(Calendar.HOUR_OF_DAY, -totalValidityPeriod);
        nextRunTime = getExpectedNextExeTime();

        timeToExe = (nextRunTime.compareTo(validityCal) >= 0 && nextRunTime.compareTo(now) <= 0);

        if (timeToExe) {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "It's time to execute the campaign");
            }
        } else {
            if (campaignsLogger.isDebugEnabled()) {
                campaignsLogger.debug(campaign.logId() + "It's not the time to execute the campaign");
            }
        }

        return timeToExe;
    }

    private Calendar getExpectedNextExeTime() {
        if (campaignsLogger.isTraceEnabled()) {
            campaignsLogger.trace(campaign.logId() + "Calculate campaign expected next run time");
        }
        Calendar schedEndDate = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        Calendar today = Calendar.getInstance();
        Calendar nextRunCal = Calendar.getInstance();
        Calendar processingDate = Calendar.getInstance();
        Calendar validityCal = Calendar.getInstance();

        nextRunCal.setTime(campaignScheduling.getScheduleStartTimestamp());

        today.clear(Calendar.HOUR);
        today.clear(Calendar.MINUTE);
        today.clear(Calendar.SECOND);
        today.clear(Calendar.MILLISECOND);

        if (campaignExecution.getProcessingTimestamp() != null) {
            processingDate.setTime(campaignExecution.getProcessingTimestamp());
            processingDate.clear(Calendar.HOUR);
            processingDate.clear(Calendar.MINUTE);
            processingDate.clear(Calendar.SECOND);
            processingDate.clear(Calendar.MILLISECOND);
        } else {
            processingDate = null;
        }
        int campaignValidityPeriod = (int) Configs.CAMP_EXE_VALIDITY_PERIOD.getValue();
        validityCal.add(Calendar.HOUR_OF_DAY, -campaignValidityPeriod);

        int calUnit = 0;
        ScheduleFrequencyName frequency = campaignScheduling.getScheduleFrequency().getScheduleFreqName();

        if (!frequency.equals(ScheduleFrequencyName.ONCE)) {
            schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
            schedEndDate.set(Calendar.HOUR, nextRunCal.get(Calendar.HOUR_OF_DAY));
            schedEndDate.set(Calendar.MINUTE, nextRunCal.get(Calendar.MINUTE));
            schedEndDate.set(Calendar.SECOND, nextRunCal.get(Calendar.SECOND));
            schedEndDate.set(Calendar.MILLISECOND, nextRunCal.get(Calendar.MILLISECOND));
            schedEndDate.setTimeZone(nextRunCal.getTimeZone());

            switch (frequency) {
                case ONCE:
                    break;
                case DAILY:
                    calUnit = Calendar.DATE;
                    break;
                case WEEKLY:
                    calUnit = Calendar.WEEK_OF_YEAR;
                    break;
                case MONTHLY:
                    calUnit = Calendar.MONTH;
                    break;
                case YEARLY:
                    calUnit = Calendar.YEAR;
                    break;
                default:
                    calUnit = 1;
            }

            if (processingDate != null && processingDate.compareTo(today) == 0) {
                nextRunCal.add(calUnit, 1);
            }

            while ((nextRunCal.compareTo(now) < 0 && nextRunCal.compareTo(validityCal) < 0 && nextRunCal
                    .compareTo(schedEndDate) < 0)) {
                nextRunCal.add(calUnit, 1);
            }

        }

        if (campaignsLogger.isDebugEnabled()) {
            campaignsLogger.debug(campaign.logId() + "Campaign expected next execution time=" + dtf.format(nextRunCal.getTime()));
        }
        return nextRunCal;
    }

    //NOT used
    private int getExpectedExecutoinCount(Calendar refCal) {
        campaignsLogger.debug(campaign.logId() + "Calculating campaign expected excution count from now");
        Calendar schedEndDate = Calendar.getInstance();

        int calUnit = 0;
        int exeCount = campaignExecution.getExecutionCount() != null ? campaignExecution.getExecutionCount() : 0;
        int expectExeCount = 0;
        ScheduleFrequencyName frequency = campaignScheduling.getScheduleFrequency().getScheduleFreqName();

        if (frequency.equals(ScheduleFrequencyName.ONCE)) {
            expectExeCount = 1;
        } else {
            schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
            schedEndDate.set(Calendar.HOUR, refCal.get(Calendar.HOUR_OF_DAY));
            schedEndDate.set(Calendar.MINUTE, refCal.get(Calendar.MINUTE));
            schedEndDate.set(Calendar.SECOND, refCal.get(Calendar.SECOND));
            schedEndDate.set(Calendar.MILLISECOND, refCal.get(Calendar.MILLISECOND));
            schedEndDate.setTimeZone(refCal.getTimeZone());

            switch (frequency) {
                case ONCE:
                    break;
                case DAILY:
                    calUnit = Calendar.DATE;
                    break;
                case WEEKLY:
                    calUnit = Calendar.WEEK_OF_YEAR;
                    break;
                case MONTHLY:
                    calUnit = Calendar.MONTH;
                    break;
                case YEARLY:
                    calUnit = Calendar.YEAR;
                    break;
                default:
                    calUnit = 1;
            }

            while (refCal.compareTo(schedEndDate) <= 0) {
                expectExeCount++;
                refCal.add(calUnit, 1);
            }
        }

        campaignsLogger.debug(campaign.logId() + "Campaign expected excution count=" + expectExeCount);
        return (expectExeCount - exeCount) > 0 ? (expectExeCount - exeCount) : 0;

    }

    void setCampaignStatusComment(String comment) {
        campaignExecution.setComments(comment);
    }

    public List<CampaignLists> getCampaignLists() {
        return campaignLists;
    }

    public CampaignStatus getCampaignStatus() {
        return campaignStatus;
    }

    public void setStateTranstion() {
        this.stateTranstion = true;
    }

    public void unsetStateTranstion() {
        this.stateTranstion = false;
    }

    public boolean isStateTranstion() {
        return stateTranstion;
    }

    public boolean isPrepaidCamp() {
        return prepaidCamp;
    }

    public void setPrepaidCamp(boolean prepaidCamp) {
        this.prepaidCamp = prepaidCamp;
    }

    public AccountQuotaHandle getAccountQuotaHandle() {
        return accountQuotaHandle;
    }

    public void setAccountQuotaHandle(AccountQuotaHandle accountQuotaHandle) {
        this.accountQuotaHandle = accountQuotaHandle;
    }

    public boolean isRateEligible() {
        return rateEligible;
    }

    public void setRateEligible(boolean rateEligible) {
        this.rateEligible = rateEligible;
    }

    public boolean sendDelayedSms() throws CampaignExecutionException {
        try {
            if (delayedSmsHolder.isSMSAllowedToSend()) {
                if (campaignsLogger.isTraceEnabled()) {
                    campaignsLogger.trace(delayedSmsHolder.getLogId() + " allowed to send");
                }
                sendSms(delayedSmsHolder.getSms(), delayedSmsHolder.getLogId());
                delayedSmsHolder = null;
                return true;
            } else {
                if (campaignsLogger.isDebugEnabled()) {
                    campaignsLogger.debug(delayedSmsHolder.getLogId() + " NOT allowed to send, will send it leater");
                }
                rateEligible = false;
                campaignsHandler.incrementRateIneligibleCampaigns();
                return false;
            }
        } catch (NoSuchLimiterException | InvalidParameterException e) {
            delayedSmsHolder.getSms().setComment(e.toString());
            delayedSmsHolder.getSms().setStatus(com.edafa.smsgw.dalayer.enums.SMSStatusName.FAILED_TO_SEND);
            campaignsLogger.error(campaign.logId() + e.getMessage() + " SMS: " + delayedSmsHolder.getSms().logFailedToSend());
            delayedSmsHolder = null;
            return true;
        }
    }

    public boolean isDelayedSmsExist() {
        return delayedSmsHolder == null ? false : true;
    }
}
