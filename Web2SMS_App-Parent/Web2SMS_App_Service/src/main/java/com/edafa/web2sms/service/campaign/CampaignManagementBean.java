package com.edafa.web2sms.service.campaign;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.utils.string.StringUtilities;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignActionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignExecutionDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignListsDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.CampaignStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ContactDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.LanguageDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ListTypeDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSLogDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.SMSStatusDaoLocal;
import com.edafa.web2sms.dalayer.enums.CampaignActionName;
import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.enums.ListTypeName;
import com.edafa.web2sms.dalayer.enums.SMSStatusName;
import com.edafa.web2sms.dalayer.enums.ScheduleFrequencyName;
import com.edafa.web2sms.dalayer.enums.ActionName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountUser;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.dalayer.model.CampaignExecution;
import com.edafa.web2sms.dalayer.model.CampaignLists;
import com.edafa.web2sms.dalayer.model.CampaignSMSDetails;
import com.edafa.web2sms.dalayer.model.CampaignScheduling;
import com.edafa.web2sms.dalayer.model.CampaignStatus;
import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ContactList;
import com.edafa.web2sms.dalayer.model.ListType;
import com.edafa.web2sms.dalayer.model.SMSStatus;
import com.edafa.web2sms.dalayer.pojo.CampSMSStats;
import com.edafa.web2sms.acc_manag.service.account.exception.IneligibleAccountException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.AccountQuotaNotFoundException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.InsufficientQuotaException;
import com.edafa.web2sms.acc_manag.service.accountQuota.exceptions.NotPrePaidAccountException;
import com.edafa.web2sms.service.admin.interfaces.AdminCampaignManagementBeanLocal;
import com.edafa.web2sms.service.campaign.exception.CampaignNotFoundException;
import com.edafa.web2sms.service.campaign.exception.CampaignTypeNotDefinedException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampListException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignException;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignStateException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanLocal;
import com.edafa.web2sms.service.conversoin.CampaignConversionBean;
import com.edafa.web2sms.service.conversoin.ListConversionBean;
import com.edafa.web2sms.service.conversoin.ReportConversionBean;
import com.edafa.web2sms.service.enums.CampaignValidationStatus;
import com.edafa.web2sms.service.list.interfaces.ListManegementBeanLocal;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.CampaignDetails;
import com.edafa.web2sms.service.model.CampaignModel;
import com.edafa.web2sms.service.model.CampaignSearchParam;
import com.edafa.web2sms.service.model.ContactListInfoModel;
import com.edafa.web2sms.service.model.ContactModel;
import com.edafa.web2sms.service.model.SubmittedCampaignModel;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.dalayer.enums.CampaignTypeName;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountManegementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountQuotaManagementFacingLocal;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.UserManagementFacingLocal;
import com.edafa.web2sms.service.campaign.exception.InvalidCampaignActionException;
import com.edafa.web2sms.service.campaign.interfaces.CampaignManagementBeanRemote;
import com.edafa.web2sms.service.model.UserModel;
import com.edafa.web2sms.service.model.UserTrxInfo;
import com.edafa.web2sms.utils.DateTimeUtils;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.SMSUtils;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;
import java.util.Collections;


//import com.edafa.web2sms.service.model.SMS;
/**
 * Session Bean implementation class CampaignManagementBean
 */
@Stateless
//@LocalBean
public class CampaignManagementBean implements CampaignManagementBeanLocal,CampaignManagementBeanRemote, AdminCampaignManagementBeanLocal {

    private Logger campLogger = LogManager.getLogger(LoggersEnum.CAMP_MNGMT.name());

    @EJB
    AccountManegementFacingLocal accountManagement;

    @EJB
    ReportConversionBean rcBean;

    @EJB
    private UserManagementFacingLocal userManagement;
    @EJB
    private CampaignDaoLocal campaignDao;

    @EJB
    LanguageDaoLocal languageDao;

    @EJB
    private CampaignStatusDaoLocal campaignStatusDao;

    @EJB
    private ListManegementBeanLocal listManegementBean;

    @EJB
    private ListTypeDaoLocal contactListTypeDao;

    @EJB
    AccountConversionFacingLocal accountConversion;

    @EJB
    CampaignConversionBean campaignConversionBean;

    @EJB
    ListConversionBean listConversionBean;

    @EJB
    CampaignListsDaoLocal campaignListsDao;

    @EJB
    CampaignExecutionDaoLocal campaignExcutionDao;

    @EJB
    CampaignActionDaoLocal campaignActionDao;

    @EJB
    AccountQuotaManagementFacingLocal accountQuotaManagement;

    @EJB
    ContactDaoLocal contactDao;

    @EJB
    SMSStatusDaoLocal smsStatusDao;

    @EJB
    SMSLogDaoLocal smsLogDao;

    /**
     * Default constructor.
     */
    public CampaignManagementBean() {
        // TODO Auto-generated constructor stub
    }

    public boolean validatCampName(UserTrxInfo userTrxInfo, String campaignName) throws DBException {
        campLogger.debug(userTrxInfo.logId() + "Validating campaign name");
        int count = campaignDao.countByNameAndAccountIdAndStatus(userTrxInfo.getUser().getAccountId(), campaignName,
                getAllCampaignStatusList());
        return count == 0;
    }

    /**
     * this function editing in SubmittedCampaginModel parameter to set campId
     * in it, to use in SMS API
     *
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign)
            throws IneligibleAccountException, DBException, InvalidCampaignException, UserNotFoundException,
            InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException,
            AccountQuotaNotFoundException, CampaignTypeNotDefinedException {
        createCampaign(userTrxInfo, campaign, null);
    }

    /**
     * this function editing in SubmittedCampaginModel parameter to set campId
     * in it, to use in SMS API
     *
     */
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void createCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign, CampaignStatusName campaignStatusName)
            throws IneligibleAccountException, DBException, InvalidCampaignException, UserNotFoundException,
            InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException,
            AccountQuotaNotFoundException, CampaignTypeNotDefinedException {
        DateFormat df = DateTimeUtils.getLogTimestampFormater();
        campLogger.info(userTrxInfo.logInfo() + campaign);

        List<ActionName> allowedActions = null;
        if (!campaign.getCampaignType().equals(CampaignTypeName.API_CAMPAIGN)) {
            userTrxInfo.addUserAction(ActionName.CREATE_CAMPAIGN);
            userTrxInfo.addUserAction(ActionName.APPROVE_CAMPAIGN);

            campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
            allowedActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
            campLogger.debug(userTrxInfo.logId() + "Account is eligible for " + allowedActions);
        } 
        validateCampaign(userTrxInfo, campaign, true, true);

        // Submit the campaign
        campLogger.debug(userTrxInfo.logId() + "Getting account user by account id");
        AccountUser acctUsr = userManagement.getAccountUser(userTrxInfo.getAccountUserTrxInfo());
        campLogger.debug(userTrxInfo.logId() + "Convert submitted campaign model to campaign data model");
        Campaign newCampaign = campaignConversionBean.getCampaign(campaign);
        mergeCampaignLists(newCampaign.getContactLists(), campaign.getContactLists());
        campLogger.trace(userTrxInfo.logId() + "Setting the account user");
        newCampaign.setAccountUser(acctUsr);

        // Set campaign status
        CampaignStatus status;
        if (campaignStatusName != null) {
            status = campaignStatusDao.getCachedObjectByName(campaignStatusName);
        } else {
            if (allowedActions != null) {
                if (allowedActions.contains(ActionName.CREATE_CAMPAIGN) && allowedActions.contains(ActionName.APPROVE_CAMPAIGN)) {
                    status = campaignStatusDao.getCachedObjectByName(CampaignStatusName.NEW);
                } else if (allowedActions.contains(ActionName.CREATE_CAMPAIGN)) {
                    status = campaignStatusDao.getCachedObjectByName(CampaignStatusName.WAITING_APPROVAL);
                } else {
                    campLogger.error(userTrxInfo.logId() + "AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                            + " is ineligible to  [" + ActionName.CREATE_CAMPAIGN + "]");
                    throw new IneligibleAccountException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                            + " is ineligible to  [" + ActionName.CREATE_CAMPAIGN + "]");
                }
            } else {
                if (campaign.getCampaignType().equals(CampaignTypeName.API_CAMPAIGN)) {
                    status = campaignStatusDao.getCachedObjectByName(CampaignStatusName.NEW);
                } else {
                    campLogger.error(userTrxInfo.logId() + "AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                            + " is ineligible to  [" + ActionName.CREATE_CAMPAIGN + "]");
                    throw new IneligibleAccountException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                            + " is ineligible to  [" + ActionName.CREATE_CAMPAIGN + "]");
                }
            }
        }
        campLogger.debug(userTrxInfo.logId() + "Setting the campaign status to " + status);

        campLogger.trace(userTrxInfo.logId() + "Cached status:" + status);
        newCampaign.setStatus(status);

        Date creationDate = new Date();
        campLogger.debug(userTrxInfo.logId() + "Setting creation timestamp " + df.format(creationDate));
        newCampaign.setCreationTimestamp(creationDate);

        campLogger.debug(userTrxInfo.logId() + "Check for individual contacts");
        List<ContactModel> individualContacts = campaign.getIndividualContacts();

        if (individualContacts != null && !individualContacts.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Campaign has (" + individualContacts.size()
                    + ") individual Contacts");
            ContactList contactsTempList = listManegementBean.createTempList(userTrxInfo, acctUsr.getAccount(),
                    individualContacts);
            newCampaign.getContactLists().add(contactsTempList);
            campLogger.debug(userTrxInfo.logId() + "Added the individual contacts as temp list with id="
                    + contactsTempList.getListId());
        } else {
            campLogger.debug(userTrxInfo.logId() + "No individual contacts");
        }

        // Set expected execution counts
        setExpectedExecutionCount(userTrxInfo, newCampaign.getCampaignScheduling());

        // SMS counts
        List<Integer> listIds = new ArrayList<Integer>();
        for (ContactList list : newCampaign.getContactLists()) {
            listIds.add(list.getListId());
        }

        campLogger.debug(userTrxInfo.logId() + "Setting SMS counts for campaign: " + newCampaign.logId()
                + " and listsId: " + listIds);
        switch (newCampaign.getType().getCampaignTypeName()) {
            case CUSTOMIZED_CAMPAIGN: {
                setSMSCountsForCustomizedCamp(userTrxInfo, newCampaign, listIds, true);
                break;
            }
            case INTRA_CAMPAIGN:
            case NORMAL_CAMPAIGN:
            case API_CAMPAIGN: {
                setSMSCounts(userTrxInfo, newCampaign, listIds);
                break;
            }
            case UNKNOWN:
            default: {
                throw new CampaignTypeNotDefinedException();
            }

        }
        // submit campaign
//        if (campaignStatusName == null) {
        if (campaignStatusName == null || campaignStatusName != CampaignStatusName.OBSOLETE) {
            try {
                accountQuotaManagement.reserveAccountQuota(userTrxInfo.getAccountUserTrxInfo(), newCampaign);
            }// end try// end try
            catch (AccountQuotaNotFoundException e) {
                campLogger.warn(userTrxInfo.logId() + "Account type: Pre-Paid, but has no Quota defined");
                throw e;
            }// end catch
            catch (NotPrePaidAccountException e) {
                campLogger.warn(userTrxInfo.logId() + "Account type: Post-Paid");
            }// end catch
            catch (InsufficientQuotaException e) {
                campLogger.warn(userTrxInfo.logId() + "Quota Exceeded.");
                throw e;
            }
        }
        campLogger.debug(userTrxInfo.logId() + "Persisting the new campaign");
        persistCampaign(newCampaign);
        campaign.setCampaignId(newCampaign.getCampaignId());
//		System.out.println("camp id: " + newCampaign.getCampaignId());
        campLogger.info(userTrxInfo.logId() + "New campaign is persisted into database, campaign: "
                + newCampaign.logCreated());
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public SubmittedCampaignModel resendFailedFromCampaign(UserTrxInfo userTrxInfo, String campaignId)
            throws IneligibleAccountException, DBException, InvalidCampaignException, UserNotFoundException,
            InvalidCampListException, InsufficientQuotaException, NotPrePaidAccountException,
            AccountQuotaNotFoundException, CampaignTypeNotDefinedException {

        userTrxInfo.addUserAction(ActionName.CREATE_CAMPAIGN);
        campLogger.info(userTrxInfo.logInfo() + "Resend failed SMSs from campaign(" + campaignId + ").");

        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());

        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        campLogger.debug(userTrxInfo.logId() + "Account is eligible for " + ActionName.CREATE_CAMPAIGN);
        Campaign camp = campaignDao.findByIdAndAccountId(userTrxInfo.getUser().getAccountId(), campaignId, true);

        campLogger.debug(userTrxInfo.logId() + "Convert campaign data model submitted campaign model");
        SubmittedCampaignModel subCampaign = campaignConversionBean.getResendCampaignModel(camp);
        campLogger.debug(userTrxInfo.logId() + subCampaign);

        List<SMSStatus> smsStatusList = new ArrayList<>();

        // Change on DEC 20, 2017 to be failed, and failed to send only, according to business requirement
//		smsStatusList.add(smsStatusDao.findByStatusName(SMSStatusName.SENT));
//		smsStatusList.add(smsStatusDao.findByStatusName(SMSStatusName.NOT_DELIVERED));
        smsStatusList.add(smsStatusDao.findByStatusName(SMSStatusName.FAILED));
        smsStatusList.add(smsStatusDao.findByStatusName(SMSStatusName.FAILED_TO_SEND));

        campLogger.debug(userTrxInfo.logId() + "Finding contacts from SMS Log based on SMS status.");
        List<Contact> failedContactList = contactDao.findContactsByCampaignIdAndStatus(campaignId, smsStatusList);
        campLogger.debug(userTrxInfo.logId() + "Found " + (failedContactList != null ? failedContactList.size() : 0));

        Map<Integer, List<ContactModel>> contactToListMap = listManegementBean.createResentCampList(userTrxInfo,
                failedContactList);

        campLogger.debug(userTrxInfo.logId() + "Getting account by account id");
        Account acct = accountManagement.getAccount(userTrxInfo.getUser().getAccountId());

        campLogger.debug(userTrxInfo.logId() + "Assign list(s) to contacts ");

        List<Integer> resentContactListId = new ArrayList<>();
        for (List<ContactModel> contactList : contactToListMap.values()) {
            ContactList tmpContactList = null;
            tmpContactList = listManegementBean.createTempList(userTrxInfo, acct, contactList);
            if (tmpContactList != null) {
                resentContactListId.add(tmpContactList.getListId());
            }
        }
        campLogger.debug(userTrxInfo.logId() + "Create list(s) with id(s): " + resentContactListId);
        subCampaign.setContactLists(resentContactListId);

        createCampaign(userTrxInfo, subCampaign);
        camp.setResendFailedFlag(true);
        campaignDao.edit(camp);
        campLogger.debug(userTrxInfo.logId() + "Old campaign resendFlag edited ");

        return subCampaign;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel updatedCamp)
            throws IneligibleAccountException, CampaignNotFoundException, DBException, InvalidCampaignStateException,
            InvalidCampaignException, UserNotFoundException, InvalidCampListException, InsufficientQuotaException,
            NotPrePaidAccountException, AccountQuotaNotFoundException, CampaignTypeNotDefinedException {
        boolean rescheduled = false;
        boolean smsTextUpdated = false;
        Campaign origCampaign;

        int originalCampaignQuota;
        int updatedCampaignQuota;
        
        userTrxInfo.addUserAction(ActionName.EDIT_CAMPAIGN);
        userTrxInfo.addUserAction(ActionName.APPROVE_CAMPAIGN);
        
        userTrxInfo.getUser();
        campLogger.info(userTrxInfo.logInfo() + "The updated campaign: " + updatedCamp);
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        List<ActionName> userActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        if (!userActions.contains(ActionName.EDIT_CAMPAIGN)) {
            campLogger.error(userTrxInfo.logId() + "AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                    + " is ineligible to  [" + ActionName.EDIT_CAMPAIGN + "]");
            throw new IneligibleAccountException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                    + " is ineligible to  [" + ActionName.EDIT_CAMPAIGN + "]");
        }
        campLogger.debug(userTrxInfo.logId() + "Account is eligible for " + userTrxInfo.getUserActions());

        campLogger.debug(userTrxInfo.logId() + "Retrive the original campaign");
        origCampaign = campaignDao.findByIdAndAccountId(userTrxInfo.getUser().getAccountId(),
                updatedCamp.getCampaignId(), true);
        if (origCampaign == null) {
            CampaignNotFoundException e = new CampaignNotFoundException(updatedCamp.getCampaignId());
            campLogger.error(userTrxInfo + e.getMessage());
            throw e;
        }

        // Check if the campaign state for updating ie, is active and not
        // running
        campLogger.info(userTrxInfo.logId() + "Validate the campaign status for update");
        validateCampaignStateForUpdate(origCampaign.getStatus().getCampaignStatusName());

        campLogger.debug(userTrxInfo.logId() + "Check if the campaign is rescheduled");
        rescheduled = isRescheduled(origCampaign.getCampaignScheduling(), updatedCamp);
        campLogger.debug(userTrxInfo.logId() + "The campaign is rescheduled");

        validateCampaign(userTrxInfo, updatedCamp, rescheduled, false);

        campLogger.info(userTrxInfo.logId() + "Check if SMS text updated");
        smsTextUpdated = !origCampaign.getSmsDetails().getSMSText().equals(updatedCamp.getSmsText());

        if (smsTextUpdated) {
            campLogger.info(userTrxInfo.logId() + "SMS text is updated");
        } else {
            campLogger.info(userTrxInfo.logId() + "SMS text is not updated");
        }

        campLogger.debug(userTrxInfo.logId() + "Merge the updated campaign to the original campaign");

        originalCampaignQuota = origCampaign.getSmsDetails().getSMSSegCount();

        campaignConversionBean.mergeCampaign(origCampaign, updatedCamp);

        Campaign updatedCampaign = origCampaign;

        if (updatedCampaign.getStatus().getCampaignStatusName().equals(CampaignStatusName.NEW)) {
            mergeCampaignLists(origCampaign.getContactLists(), updatedCamp.getContactLists());
        }

        updatedCampaign.setCreationTimestamp(origCampaign.getCreationTimestamp());
        campLogger.debug(userTrxInfo.logId() + "Getting account user by account id");
        AccountUser acctUsr = userManagement.getAccountUser(userTrxInfo.getAccountUserTrxInfo());
        campLogger.debug(userTrxInfo.logId() + "Setting the account");
        updatedCampaign.setAccountUser(acctUsr);

        campLogger.debug(userTrxInfo.logId() + "Check for individual contacts");
        List<ContactModel> individualContacts = updatedCamp.getIndividualContacts();

        if (individualContacts != null && !individualContacts.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Campaign has (" + individualContacts.size()
                    + ") individual Contacts");
            ContactList contactsTempList = listManegementBean.createTempList(userTrxInfo, acctUsr.getAccount(),
                    individualContacts);
            updatedCampaign.getContactLists().add(contactsTempList);

            campLogger.debug(userTrxInfo.logId() + "Added the individual contacts as temp list with id="
                    + contactsTempList.getListId());
        } else {
            campLogger.debug(userTrxInfo.logId() + "No individual contacts");
        }

        // Set campaign status
        if (rescheduled) {

            CampaignStatus oldStatus = origCampaign.getStatus();
            CampaignStatus newStatus = origCampaign.getStatus();
            switch (oldStatus.getCampaignStatusName()) {
                case NEW:
                    newStatus = getCampaignStatus(CampaignStatusName.NEW);
                    break;
                case PARTIAL_RUN:
                case PAUSED:
                case ON_HOLD: // should not be editable

                    newStatus = getCampaignStatus(CampaignStatusName.PARTIAL_RUN);
                    campLogger.debug(userTrxInfo.logId() + "The campaign is rescheduled, update the campaign status to "
                            + newStatus);
                    updatedCampaign.setStatus(newStatus);

                    break;
                default:
                    break;
            }
            campLogger.debug(userTrxInfo.logId() + "The campaign is rescheduled, update the campaign status to "
                    + newStatus);
            // Set expected execution counts
            setExpectedExecutionCount(userTrxInfo, updatedCampaign.getCampaignScheduling());

            campLogger.info("The campaign is rescheduled, update campaign action to null");
            updatedCampaign.getCampaignExecution().setAction(null);

        }

        List<Integer> listIds = new ArrayList<Integer>();
        for (ContactList list : updatedCampaign.getContactLists()) {
            listIds.add(list.getListId());
        }

        // SMS counts
        if (smsTextUpdated || rescheduled
                || updatedCampaign.getStatus().getCampaignStatusName().equals(CampaignStatusName.NEW)) {

            switch (updatedCampaign.getType().getCampaignTypeName()) {
                case CUSTOMIZED_CAMPAIGN: {
                    setSMSCountsForCustomizedCamp(userTrxInfo, updatedCampaign, listIds, false);
                    break;
                }
                case INTRA_CAMPAIGN:
                case NORMAL_CAMPAIGN: {
                    setSMSCounts(userTrxInfo, updatedCampaign, listIds);
                    break;
                }
                case UNKNOWN:
                default: {
                    throw new CampaignTypeNotDefinedException();
                }

            }
        }// end if

        updatedCampaignQuota = updatedCampaign.getSmsDetails().getSMSSegCount();

        try {
            accountQuotaManagement.updateReserveAccountQuota(userTrxInfo.getAccountUserTrxInfo(), updatedCampaign.getCampaignId(),
                    originalCampaignQuota, updatedCampaignQuota);
        }// end try// end try
        catch (AccountQuotaNotFoundException e) {
            campLogger.warn(userTrxInfo.logId() + "Account type: Pre-Paid, but has no Quota defined");
            throw e;
        }// end catch
        catch (NotPrePaidAccountException e) {
            campLogger.warn(userTrxInfo.logId() + "Account type: Post-Paid");
        }// end catch

        // submit campaign
        if (!userActions.contains(ActionName.APPROVE_CAMPAIGN)) {
            updatedCampaign.setStatus(campaignStatusDao.getCachedObjectByName(CampaignStatusName.WAITING_APPROVAL));
        }
        campaignDao.edit(updatedCampaign);
        campLogger.info(userTrxInfo.logId() + "Updated the campaign sccessfully, updated campaign: "
                + updatedCampaign.logCreated());

    }

    private List<ContactList> mergeCampaignLists(List<ContactList> currentLists, List<Integer> lists) {

        List<ContactList> newLists = new ArrayList<ContactList>();
        if (lists != null) {
            newLists = listConversionBean.getContactLists(lists);

            if (currentLists != null) {
                // Adding new lists
                for (ContactList newList : newLists) {
                    if (!currentLists.contains(newList)) {
                        currentLists.add(newList);
                    }
                }

                // Remove the removed lists
                for (Iterator<ContactList> it = currentLists.iterator(); it.hasNext();) {
                    ContactList currentList = (ContactList) it.next();
                    if (!newLists.contains(currentList)) {
                        it.remove();
                    }
                }

                newLists = currentLists;
            }
        }

        return newLists;
    }

    private boolean isRescheduled(CampaignScheduling origCampSched, SubmittedCampaignModel updatedCamp) {
        boolean reschduled = !origCampSched.getScheduledFlag().equals(updatedCamp.getScheduledFlag())
                || !origCampSched.getScheduleFrequency().getScheduleFreqName()
                .equals(updatedCamp.getScheduleFrequency())
                || !origCampSched.getScheduleStartTimestamp().equals(updatedCamp.getScheduleStartTimestamp());

        if (!reschduled && origCampSched.getScheduleEndDate() != null) {
            reschduled = !origCampSched.getScheduleEndDate().equals(updatedCamp.getScheduleEndDate());
        }

        if (!reschduled && origCampSched.getScheduleStopTime() != null) {
            reschduled = !origCampSched.getScheduleStopTime().equals(updatedCamp.getScheduleStopTime());
        }

        return reschduled;
    }

    private void validateCampaignStateForUpdate(CampaignStatusName campStatus) throws InvalidCampaignStateException {
        if (!isActiveCampaign(campStatus) || isRunningCampaign(campStatus)) {
            throw new InvalidCampaignStateException("The campaign state is invalid to be updated, current status is "
                    + campStatus + ")");
        }
    }

    private boolean isActiveCampaign(CampaignStatusName campStatus) {
        for (CampaignStatus status : getActiveCampaignStatusList()) {
            if (status.getCampaignStatusName().equals(campStatus)) {
                return true;
            }
        }

        return false;
    }

    private boolean isRunningCampaign(CampaignStatusName campStatus) {
        return campStatus.equals(CampaignStatusName.RUNNING);
    }
    
    private boolean isNewCampaign(CampaignStatusName campStatus) {
        return campStatus.equals(CampaignStatusName.NEW);
    }

    private void setExpectedExecutionCount(UserTrxInfo userTrxInfo, CampaignScheduling campaignScheduling) {
        campLogger.info(userTrxInfo.logId() + "Calculate campaign expected excution count");
        Calendar schedStartTimestamp = Calendar.getInstance();
        Calendar schedEndDate = Calendar.getInstance();

        int calUnit = 0;
        int exeCount = 0;
        ScheduleFrequencyName frequency = campaignScheduling.getScheduleFrequency().getScheduleFreqName();

        schedStartTimestamp.setTime(campaignScheduling.getScheduleStartTimestamp());
        if (frequency.equals(ScheduleFrequencyName.ONCE)) {
            exeCount = 1;
        } else {
            schedEndDate.setTime(campaignScheduling.getScheduleEndDate());
            schedEndDate.set(Calendar.HOUR, schedStartTimestamp.get(Calendar.HOUR_OF_DAY));
            schedEndDate.set(Calendar.MINUTE, schedStartTimestamp.get(Calendar.MINUTE));
            schedEndDate.set(Calendar.SECOND, schedStartTimestamp.get(Calendar.SECOND));
            schedEndDate.set(Calendar.MILLISECOND, schedStartTimestamp.get(Calendar.MILLISECOND));

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

            do {
                exeCount++;
                schedStartTimestamp.add(calUnit, 1);
            } while (schedStartTimestamp.compareTo(schedEndDate) <= 0);

        }

        campLogger.info(userTrxInfo.logId() + "Campaign expected excution count=" + exeCount);
        campaignScheduling.setExpectedExecutionCount(exeCount);

    }

    private void setSMSCounts(UserTrxInfo userTrxInfo, Campaign campaign, List<Integer> lists) throws DBException,
            InvalidCampListException {
        CampaignSMSDetails smsDetails = campaign.getSmsDetails();
        CampaignExecution campaignExecution = campaign.getCampaignExecution();
        CampaignScheduling campaignScheduling = campaign.getCampaignScheduling();
        int totalSMSCount = 0;
        int totalSMSSegCount = 0;
        int contactsCount = listManegementBean.countContactsInLists(userTrxInfo, lists);
        int expectedExecutionCount = campaignScheduling.getExpectedExecutionCount();
        int smsSegments = SMSUtils.calcSegCount(smsDetails.getLanguage().getLanguageName(), smsDetails.getSMSText());
        campLogger.debug(userTrxInfo.logId() + "No of segments per SMS=" + smsSegments);

        int submittedSMSCount = campaignExecution.getSubmittedSmsCount();
        int submittedSMSSegCount = campaignExecution.getSubmittedSmsSegCount();

        boolean accumulateHistory = false;
        if (contactsCount == 0) {
            throw new InvalidCampListException(lists);
        }
        switch (campaign.getStatus().getCampaignStatusName()) {
            case NEW:
            case PAUSED:
            case ON_HOLD:
                accumulateHistory = false;
                break;

            case PARTIAL_RUN:
                accumulateHistory = true;
                break;
            default:
                break;
        }
        campLogger.trace(userTrxInfo.logId() + "accumulateHistory=" + accumulateHistory);
        if (accumulateHistory) {
            totalSMSCount = (expectedExecutionCount * contactsCount) + submittedSMSCount;
            totalSMSSegCount = (expectedExecutionCount * contactsCount * smsSegments) + submittedSMSSegCount;
            campLogger.debug(userTrxInfo.logId() + "Total contacts count=" + contactsCount + ", total SMSs count="
                    + totalSMSCount + ", total segments count=" + totalSMSSegCount + ", submitted SMS count="
                    + submittedSMSCount + ", submitted SMS Segment count=" + submittedSMSSegCount);
        } else {
            // int submittedSMSCount = campaignExecution.getSubmittedSMSCount();
            // int submittedSMSSegCount =
            // campaignExecution.getSubmittedSMSSegCount();
            int submittedSMSLastExecution = campaignListsDao.countSubmittedSMSInLists(campaign.getCampaignId());
            int remainingContactsLastExec = contactsCount - submittedSMSLastExecution;
            totalSMSCount = ((expectedExecutionCount - 1) > 0 ? (expectedExecutionCount - 1) : 0) * contactsCount
                    + remainingContactsLastExec + submittedSMSCount;
            totalSMSSegCount = ((expectedExecutionCount - 1) > 0 ? (expectedExecutionCount - 1) : 0) * contactsCount
                    * smsSegments + remainingContactsLastExec * smsSegments + submittedSMSSegCount;
            // totalSMSSegCount = (totalSMSCount * smsSegments);
            campLogger.debug(userTrxInfo.logId() + "Total contacts count=" + contactsCount
                    + ", submitted SMS last execution=" + submittedSMSLastExecution + ", remaining SMS last execution="
                    + remainingContactsLastExec + ", total SMSs count=" + totalSMSCount + ", total segments count="
                    + totalSMSSegCount + ", submitted SMS count=" + submittedSMSCount
                    + ", submitted SMS Segment count=" + submittedSMSSegCount);
        }

        smsDetails.setSMSCount(totalSMSCount);
        smsDetails.setSMSSegCount(totalSMSSegCount);
        campLogger.trace(userTrxInfo.logId()
                + "Total SMSs count and total segments count are set in campaign's SMS details");
    }

    private void setSMSCountsForCustomizedCamp(UserTrxInfo userTrxInfo, Campaign campaign, List<Integer> lists,
            boolean newCampFlag) throws DBException, InvalidCampListException, IneligibleAccountException {
        CampaignSMSDetails smsDetails = campaign.getSmsDetails();
        CampaignExecution campaignExecution = campaign.getCampaignExecution();
        CampaignScheduling campaignScheduling = campaign.getCampaignScheduling();
        String smsScript = smsDetails.getSMSText();
        List<Contact> campaignContacts = new ArrayList<>();
        int totalSMSCount = 0;
        int totalSMSSegCount = 0;
        int smsSegments = 0;
        // int contactsCount =
        // listManegementBean.countContactsInLists(userTrxInfo, lists);
        int expectedExecutionCount = campaignScheduling.getExpectedExecutionCount();

        for (int listId : lists) {
            campaignContacts.addAll(listManegementBean.getContacts(userTrxInfo, listId));
        }

        smsSegments = getSMSSegCount(smsScript, campaignContacts);
        campLogger.debug(userTrxInfo.logId() + "No of SMS segments per Execution cycle=" + smsSegments);

        int submittedSMSCount = campaignExecution.getSubmittedSmsCount();
        int submittedSMSSegCount = campaignExecution.getSubmittedSmsSegCount();

        boolean accumulateHistory = false;
        if (campaignContacts.isEmpty()) {
            throw new InvalidCampListException(lists);
        }
        switch (campaign.getStatus().getCampaignStatusName()) {
            case NEW:
            case PAUSED:
            case ON_HOLD:
                accumulateHistory = false;
                break;

            case PARTIAL_RUN:
                accumulateHistory = true;
                break;
            default:
                break;
        }
        campLogger.trace(userTrxInfo.logId() + "accumulateHistory=" + accumulateHistory);
        if (accumulateHistory) {
            totalSMSCount = (expectedExecutionCount * campaignContacts.size()) + submittedSMSCount;
            totalSMSSegCount = (expectedExecutionCount * smsSegments) + submittedSMSSegCount;
            campLogger.debug(userTrxInfo.logId() + "Total contacts count=" + campaignContacts.size()
                    + ", total SMSs count=" + totalSMSCount + ", total segments count=" + totalSMSSegCount
                    + ", submitted SMS count=" + submittedSMSCount + ", submitted SMS Segment count="
                    + submittedSMSSegCount);
        } else if (newCampFlag) {
            totalSMSCount = expectedExecutionCount * campaignContacts.size();
            totalSMSSegCount = expectedExecutionCount * smsSegments;
            campLogger.debug(userTrxInfo.logId() + "Total contacts count=" + campaignContacts.size()
                    + ", total SMSs count=" + totalSMSCount + ", total segments count=" + totalSMSSegCount
                    + ", submitted SMS count=" + submittedSMSCount + ", submitted SMS Segment count="
                    + submittedSMSSegCount);
        } else {
            campLogger.debug(campaign.logId() + "Retrieving submittable campaign lists");
            List<CampaignLists> campaignLists = campaignListsDao.findSubmittableByCampaignIdOrdered(campaign
                    .getCampaignId());
            campLogger.debug(campaign.logId() + "Found (" + campaignLists.size() + ") submittable campaign lists");

            List<Contact> remainingContacts = new ArrayList<>();
            for (CampaignLists campaignList : campaignLists) {
                campLogger.debug(campaign.logId() + "Current list " + campaignList.logId()
                        + "retrieve contacts, positionIndex=" + campaignList.getSubmittedSMSCount());
                remainingContacts.addAll(contactDao.findByListID(campaignList.getCampaignListsPK().getListId(),
                        campaignList.getSubmittedSMSCount(), -1));
            }
            int remainingSMSCount = remainingContacts.size();
            int remainingSMSSegCount = getSMSSegCount(smsScript, remainingContacts);
            totalSMSCount = ((expectedExecutionCount - 1) > 0 ? (expectedExecutionCount - 1) : 0)
                    * campaignContacts.size() + remainingSMSCount + submittedSMSCount;
            totalSMSSegCount = ((expectedExecutionCount - 1) > 0 ? (expectedExecutionCount - 1) : 0) * smsSegments
                    + remainingSMSSegCount + submittedSMSSegCount;
            campLogger.debug(userTrxInfo.logId() + "Total contacts count=" + campaignContacts.size()
                    + ", total SMSs count=" + totalSMSCount + ", total segments count=" + totalSMSSegCount
                    + ", submitted SMS count=" + submittedSMSCount + ", submitted SMS Segment count="
                    + submittedSMSSegCount);

        }

        smsDetails.setSMSCount(totalSMSCount);
        smsDetails.setSMSSegCount(totalSMSSegCount);
        campLogger.trace(userTrxInfo.logId()
                + "Total SMSs count and total segments count are set in campaign's SMS details");
    }

    private int getSMSSegCount(String smsScript, List<Contact> campaignContacts) {
        int smsSegments = 0;
        for (Contact contact : campaignContacts) {
            String customizedSMS = StringUtilities.fastReplace(smsScript, "$1",
                    contact.getValue1() != null ? contact.getValue1() : "");
            customizedSMS = StringUtilities.fastReplace(customizedSMS, "$2",
                    contact.getValue2() != null ? contact.getValue2() : "");
            customizedSMS = StringUtilities.fastReplace(customizedSMS, "$3",
                    contact.getValue3() != null ? contact.getValue3() : "");
            customizedSMS = StringUtilities.fastReplace(customizedSMS, "$4",
                    contact.getValue4() != null ? contact.getValue4() : "");
            customizedSMS = StringUtilities.fastReplace(customizedSMS, "$5",
                    contact.getValue5() != null ? contact.getValue5() : "");

            String lang = getSMSLanguage(customizedSMS);
            smsSegments += SMSUtils.calcSegCount(LanguageNameEnum.valueOf(lang), customizedSMS);
        }
        return smsSegments;
    }

    public String getSMSLanguage(String sms) {
        String gsm7bitChars = "\f|~^€[]{}@£$¥èéùìòÇ\nØø\rÅåΔ_ΦΓΛΩΠΨΣΘΞÆæßÉ !\"#¤%&'()*+,-./0123456789:;<=>?\\¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà";

        boolean engMsg = true;

        if (sms != null) {
            for (int i = 0; i < sms.length(); ++i) {
                if (((!(engMsg)) && (sms.length() != 1)) || (gsm7bitChars.indexOf(sms.charAt(i)) != -1)) {
                    continue;
                }
                engMsg = false;
                break;
            }

        }

        if (engMsg) {
            return "ENGLISH";
        } else {
            return "ARABIC";
        }
    }

    private void persistCampaign(Campaign campaign) throws DBException {
        campaignDao.create(campaign);
//		System.out.println("camp id: " + campaign.getCampaignId());
    }// end of method persistCampaign

    private void validateCampaign(UserTrxInfo userTrxInfo, SubmittedCampaignModel campaign, boolean validateScheduling,
            boolean newCamp) throws DBException, InvalidCampaignException {

        campLogger.debug(userTrxInfo.logId() + "Validate the campaign");
        List<CampaignValidationStatus> statusList = new ArrayList<CampaignValidationStatus>();

        if (campaign.getContactLists() != null && campaign.getContactLists().isEmpty()) {
            campLogger.debug(userTrxInfo.logId() + "No lists associated with the campaign");
            statusList.add(CampaignValidationStatus.NO_CONTACT_LISTS);
        }

        // Validate campaign name uniqueness
        // if (newCamp && !validatCampName(userTrxInfo,
        // campaign.getCampaignName())) {
        // campLogger.debug(userTrxInfo.logId() + "Duplicate campaign name");
        // statusList.add(CampaignValidationStatus.DUPLICAT_CAMPAIGN_NAME);
        // }
        
        String language = getSMSLanguage(campaign.getSmsText());
        campLogger.debug(userTrxInfo.logId() + "campaign language is: "
                + language + ", and lang from camp model is: "
                + campaign.getLanguage());
        if (!language.equals(campaign.getLanguage().name())) {
            statusList.add(CampaignValidationStatus.INVALID_CAMPAIN_LANG);
        }
        if (!validateScheduling) {
            campLogger.debug(userTrxInfo.logId() + "Will not validate campaign scheduling");

        } else {
            // Check campaign scheduling
            DateFormat timestampFormatter = DateTimeUtils.getLogTimestampFormater();
            DateFormat dateFormatter = DateTimeUtils.getLogDateFormater();

            ScheduleFrequencyName scheduleFrequency = campaign.getScheduleFrequency();
            Calendar validityDate = Calendar.getInstance();
            Integer campaignValidityPeriod = (Integer) Configs.CAMPAIGN_SUBMITTION_VALIDITY_PERIOD.getValue();
            validityDate.add(Calendar.MINUTE, -campaignValidityPeriod);
            campLogger.trace(userTrxInfo.logId() + "schedule start timestamp: "
                    + timestampFormatter.format(campaign.getScheduleStartTimestamp()) + ", "
                    + Configs.CAMPAIGN_SUBMITTION_VALIDITY_PERIOD.getProperty() + "=" + campaignValidityPeriod
                    + ", validity date: " + timestampFormatter.format(validityDate.getTime()));
            // Validate campaign start date
            if (validityDate.getTime().after(campaign.getScheduleStartTimestamp())) {
                campLogger.error(userTrxInfo.logId() + "Outdated campaign ScheduleStartTimestamp");
                statusList.add(CampaignValidationStatus.INVALID_SCHEDULING);
            } // Check if the campaign is scheduled more than once and it hasn't
            // end date
            else if (scheduleFrequency != ScheduleFrequencyName.ONCE && campaign.getScheduleEndDate() == null) {
                campLogger
                        .error(userTrxInfo.logId()
                                + "Invalid Campaign Scheduling: No end date however the campaign schedule frequency is not once, schedule frequency: "
                                + campaign.getScheduleEndDate());
                statusList.add(CampaignValidationStatus.INVALID_SCHEDULING);
            } // Validate scheduling start date should be before scheduling end
            // date
            else if (campaign.getScheduleStopTime() != null) {
                DateFormat timeFormatter = DateTimeUtils.getLogTimeFormater();
                Calendar startTime = Calendar.getInstance();
                startTime.setTime(campaign.getScheduleStartTimestamp());
                startTime.clear(Calendar.DAY_OF_WEEK);
                startTime.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
                startTime.clear(Calendar.DAY_OF_MONTH);
                startTime.clear(Calendar.DAY_OF_YEAR);
                startTime.clear(Calendar.WEEK_OF_MONTH);
                startTime.clear(Calendar.WEEK_OF_YEAR);
                startTime.clear(Calendar.YEAR);
                startTime.clear(Calendar.MONTH);

                Calendar stopTime = Calendar.getInstance();
                stopTime.setTime(campaign.getScheduleStopTime());
                stopTime.clear(Calendar.DAY_OF_WEEK);
                stopTime.clear(Calendar.DAY_OF_WEEK_IN_MONTH);
                stopTime.clear(Calendar.DAY_OF_MONTH);
                stopTime.clear(Calendar.DAY_OF_YEAR);
                stopTime.clear(Calendar.WEEK_OF_MONTH);
                stopTime.clear(Calendar.WEEK_OF_YEAR);
                stopTime.clear(Calendar.YEAR);
                stopTime.clear(Calendar.MONTH);

                campLogger.trace(userTrxInfo.logId() + "startTime=" + timeFormatter.format(startTime.getTime())
                        + ", stopTime=" + timeFormatter.format(stopTime.getTime()));

                if (!stopTime.after(startTime)) {
                    campLogger.error(userTrxInfo.logId()
                            + "Invalid Campaign Scheduling: Campaign start time is after stop time");
                    statusList.add(CampaignValidationStatus.INVALID_SCHEDULING);
                }
            } else if (campaign.getScheduleEndDate() != null) {
                Calendar startDate = Calendar.getInstance();
                startDate.setTime(campaign.getScheduleStartTimestamp());
                startDate.clear(Calendar.HOUR_OF_DAY);
                startDate.clear(Calendar.HOUR);
                startDate.clear(Calendar.MINUTE);
                startDate.clear(Calendar.SECOND);
                startDate.clear(Calendar.MILLISECOND);
                Calendar endDate = Calendar.getInstance();
                endDate.setTime(campaign.getScheduleEndDate());
                endDate.clear(Calendar.HOUR);
                endDate.clear(Calendar.HOUR_OF_DAY);
                endDate.clear(Calendar.MINUTE);
                endDate.clear(Calendar.SECOND);
                endDate.clear(Calendar.MILLISECOND);

                campLogger.trace(userTrxInfo.logId() + "startDate=" + dateFormatter.format(startDate.getTime())
                        + ", endDate=" + dateFormatter.format(endDate.getTime()));

                if (startDate.after(endDate)) {
                    campLogger.error(userTrxInfo.logId()
                            + "Invalid Campaign Scheduling: Campaign start date is after end date or they are equal");
                    statusList.add(CampaignValidationStatus.INVALID_SCHEDULING);
                }
            }
        }
        // Validate campaign sender name
        try {
            campLogger.debug(userTrxInfo.logId() + "Validate sender name");
            SMSUtils.getSenderType(campaign.getSenderName());
        } catch (InvalidSMSSender e) {
            campLogger.error(userTrxInfo.logId() + "Invalid sender name " + e.getMessage());
            statusList.add(CampaignValidationStatus.INVALID_SENDER_NAME);
        }

        if (statusList.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Valid campaign");
        } else {
            campLogger.info(userTrxInfo.logId() + "Invalid campaign, validation status list: " + statusList);
            throw new InvalidCampaignException(statusList);
        }
    }

    // @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<CampaignModel> getActiveCampaigns(UserTrxInfo userTrxInfo, int firstIndex, int count)
            throws DBException, IneligibleAccountException {
        userTrxInfo.addUserAction(ActionName.VIEW_ACTIVE_CAMPAINGS);
        campLogger.info(userTrxInfo.logInfo() + "Getting account active campaigns, firstIndex=" + firstIndex
                + ", count=" + count);

        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        campLogger.debug(userTrxInfo.logId() + "Account is eligible");

        campLogger.trace(userTrxInfo.logId() + "Prepare list of status for active campaigns");
        List<CampaignStatus> listOfStatus = getActiveCampaignStatusList();
        campLogger.trace(userTrxInfo.logId() + "Active campaigns status list: " + listOfStatus);

        List<CampaignModel> activeCampList = getAccountCampaigns(userTrxInfo, null, listOfStatus, firstIndex, count);

        if (!activeCampList.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Retrived active campaigns, count=" + activeCampList.size());
            getCampSMSStats(userTrxInfo, activeCampList);

        } else {
            campLogger.info(userTrxInfo.logId() + "There is no active campaigns");
        }

        return activeCampList;
    }

    public List<CampaignModel> searchCampaigns(UserTrxInfo userTrxInfo, String campaignName, int firstIndex, int count, List<CampaignStatusName> statuses)
            throws DBException, IneligibleAccountException {
        campLogger.info(userTrxInfo.logInfo() + "Getting account campaigns, CampaignName(" + campaignName + "), firstIndex=" + firstIndex
                + ", count=" + count + ", with statuses: " + statuses);
        
        campLogger.trace(userTrxInfo.logId() + "Prepare list of status for campaigns: " + statuses);
        List<CampaignStatus> listOfStatus = getCampaignStatusList(statuses);
        campLogger.trace(userTrxInfo.logId() + "Campaigns status list: " + listOfStatus);

        userTrxInfo.addUserAction(ActionName.VIEW_ACTIVE_CAMPAINGS);
        userTrxInfo.addUserAction(ActionName.VIEW_CAMPAIGNS_HISTORY);
        userTrxInfo.addUserAction(ActionName.VIEW_PENDING_CAMPAIGNS);
                
        
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
        List<ActionName> userAllowedActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        
        List<ActionName> statusAllowedActions = new ArrayList<>();
        for (CampaignStatus status : listOfStatus) {
            if (getActiveCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_ACTIVE_CAMPAINGS);
            }
            if (getArchiveCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_CAMPAIGNS_HISTORY);
            }
            if (getPendingCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_PENDING_CAMPAIGNS);
            }
            if (Collections.disjoint(userAllowedActions, statusAllowedActions)) { // no common actions
                campLogger.info(userTrxInfo.logId() + "AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + statusAllowedActions + "]");
                throw new IneligibleAccountException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + statusAllowedActions + "]");
            }
        }

 
        campLogger.debug(userTrxInfo.logId() + "Retrieve campaigns from database");
        List<CampaignModel> campList = getAccountCampaigns(userTrxInfo, campaignName, listOfStatus, firstIndex, count);

        if (!campList.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Retrived campaigns, count=" + campList.size());
            getCampSMSStats(userTrxInfo, campList);
        } else {
            campLogger.info(userTrxInfo.logId() + "There is campaigns with statuses: " + listOfStatus);
        }

        return campList;
    }

    public int countSearchCampaigns(UserTrxInfo userTrxInfo, String campaignName, List<CampaignStatusName> statuses)
            throws DBException, IneligibleAccountException {

        campLogger.info(userTrxInfo.logInfo() + "Getting campaigns search count, CampaignName(" + campaignName + ") with statuses: " + statuses);

        campLogger.trace(userTrxInfo.logId() + "Prepare list of status for campaigns: " + statuses);
        List<CampaignStatus> listOfStatus = getCampaignStatusList(statuses);
        campLogger.trace(userTrxInfo.logId() + "Campaigns status list: " + listOfStatus);

        userTrxInfo.addUserAction(ActionName.VIEW_ACTIVE_CAMPAINGS);
        userTrxInfo.addUserAction(ActionName.VIEW_CAMPAIGNS_HISTORY);
        userTrxInfo.addUserAction(ActionName.VIEW_PENDING_CAMPAIGNS);                
        
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for actions: " + userTrxInfo.getUserActions());
        List<ActionName> userAllowedActions = accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        
        List<ActionName> statusAllowedActions = new ArrayList<>();
        for (CampaignStatus status : listOfStatus) {
            if (getActiveCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_ACTIVE_CAMPAINGS);
            }
            if (getArchiveCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_CAMPAIGNS_HISTORY);
            }
            if (getPendingCampaignStatusList().contains(status)) {
                statusAllowedActions.add(ActionName.VIEW_PENDING_CAMPAIGNS);
            }
            if (Collections.disjoint(userAllowedActions, statusAllowedActions)) { // no common actions
                campLogger.info(userTrxInfo.logId() + "AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + statusAllowedActions + "]");
                throw new IneligibleAccountException("AccountUser(" + userTrxInfo.getUser().getUsername() + "), account(" + userTrxInfo.getUser().getAccountId() + ")"
                        + " is ineligible to  [" + statusAllowedActions + "]");
            }
        }

        campLogger.debug(userTrxInfo.logId() + "Retrieve search campaigns count from database");
        int campCount = campaignDao.countSearchCampaigns(userTrxInfo.getUser().getAccountId(), campaignName, listOfStatus);
        campLogger.info(userTrxInfo.logId() + "Retrived campaigns, count=" + campCount);

        return campCount;

    }

    @Override
    public List<CampaignModel> getCampaignsHistory(UserTrxInfo userTrxInfo, int firstIndex, int count)
            throws DBException, IneligibleAccountException {
        userTrxInfo.addUserAction(ActionName.VIEW_CAMPAIGNS_HISTORY);
        campLogger.info(userTrxInfo.logInfo() + "Getting account campaigns history, firstIndex=" + firstIndex
                + ", count=" + count);

        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        campLogger.debug(userTrxInfo.logId() + "Account is eligible");

        campLogger.trace(userTrxInfo.logId() + "Prepare list of status for campaigns history");
        List<CampaignStatus> listOfStatus = getArchiveCampaignStatusList();
        campLogger.trace(userTrxInfo.logId() + "Archive campaigns status list: " + listOfStatus);

        List<CampaignModel> campList = getAccountCampaigns(userTrxInfo, null, listOfStatus, firstIndex, count);

        if (!campList.isEmpty()) {
            campLogger.info(userTrxInfo.logId() + "Retrived campaigns history, count=" + campList.size());
        } else {
            campLogger.info(userTrxInfo.logId() + "There is no campaign history");
        }

        return campList;
    }

    private List<CampaignModel> getAccountCampaigns(UserTrxInfo userTrxInfo, String campaignName, List<CampaignStatus> listOfStatus,
            int firstIndex, int count) throws DBException {
        campLogger.debug(userTrxInfo.logId() + "Retrieve campaigns from database");
        //List<Campaign> campaignList = campaignDao.findCampaignsByAccountIdAndStatus(userTrxInfo.getUser().getAccountId(), listOfStatus, firstIndex, count);
        List<Campaign> campaignList = campaignDao.searchCampaigns(userTrxInfo.getUser().getAccountId(), campaignName, listOfStatus, firstIndex, count);
        campLogger.debug(userTrxInfo.logId() + "Retrieved campaigns from database, count=" + campaignList.size());
        List<CampaignModel> campList = new ArrayList<CampaignModel>();

        campLogger.debug(userTrxInfo.logId() + "Convert campaigns to campaign models");
        for (Campaign campaign : campaignList) {
            campLogger.trace(userTrxInfo.logId() + "Convert campaign " + campaign.logId() + " to campaign model");
            // campaign.setContactLists(listManegementBean.getContactLists(userTrxInfo,
            // campaign.getCampaignId()));
            campList.add(campaignConversionBean.getCampaignModel(campaign));
        }
        return campList;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCampaignAction(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName action)
            throws IneligibleAccountException, DBException {
        userTrxInfo.addUserAction(ActionName.UPDATE_CAMPAIGN_ACTION);
        // Check account eligibility
        campLogger.info(userTrxInfo.logInfo() + "campaignId = " + campaignId + ", campaignAction = " + action);
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());

        campLogger.debug(userTrxInfo.logId() + "Account is eligible for action: " + userTrxInfo.getUserActions()
                + ", will update the campaign action");

        campaignExcutionDao.updateCampaignAction(campaignId, campaignActionDao.getCachedObjectByName(action));
        campLogger.info(userTrxInfo.logId() + "Updated campaign action successfully");

    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int holdRunningCampaigns(TrxInfo trxInfo, String accountId) throws DBException {
        CampaignActionName action = CampaignActionName.HOLD;
        campLogger.info(trxInfo.logId() + "Update running campaigns for account (" + accountId + ") with action: "
                + action);
        List<CampaignStatus> statusList = new ArrayList<>();
        statusList.add(getCampaignStatus(CampaignStatusName.RUNNING));
        return updateCampaignsAction(trxInfo, accountId, action, statusList);
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public int updateCampaignsAction(TrxInfo trxInfo, String accountId, CampaignActionName action,
            List<CampaignStatus> statusList) throws DBException {
        // Check account eligibility
        campLogger.info(trxInfo.logId() + "Update campaigns with statuses " + statusList + " for account (" + accountId
                + ") with action: " + action);

        int updatedCount = campaignExcutionDao.updateCampaignsActionByAcctId(accountId,
                campaignActionDao.getCachedObjectByName(action), statusList);
        campLogger.info(trxInfo.logId() + "Updated campaigns action successfully, count=" + updatedCount);
        return updatedCount;
    }

    @Override
    public int getActiveCampaignsCount(UserModel user) throws DBException {
        List<CampaignStatus> listOfStatus = getActiveCampaignStatusList();
        int count = campaignDao.countAccountCampaigns(accountConversion.getAccount(user.getAccManagUserModel()), listOfStatus);
        return count;
    }

    @Override
    public int getArchiveCampaignsCount(UserModel user) throws DBException {
        List<CampaignStatus> listOfStatus = getArchiveCampaignStatusList();
        int count = campaignDao.countAccountCampaigns(accountConversion.getAccount(user.getAccManagUserModel()), listOfStatus);
        return count;
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void deleteCampaigns(UserTrxInfo userTrxInfo, List<String> campaignIds) throws IneligibleAccountException,
            DBException {
        userTrxInfo.addUserAction(ActionName.DELETE_CAMPAIGN);
        campLogger.info(userTrxInfo.logInfo() + "Campaign Ids= " + campaignIds + ", count=" + campaignIds.size());
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());
        CampaignStatus deletedCampStatus = getCampaignStatus(CampaignStatusName.DELETED);
        campLogger.debug(userTrxInfo.logId() + "Deleted campaigns status will be: " + deletedCampStatus);
        int updatedCampsCount = 0;
        int invalidCampStateCount = 0;
        int notFoundCampCount = 0;
        for (String campId : campaignIds) {
            campLogger.trace(userTrxInfo.logId() + "Camp(" + campId + ") check current status");
            CampaignStatus campStatus = campaignDao.getCampaignStatus(campId);
            campLogger.trace(userTrxInfo.logId() + "Camp(" + campId + ") current status=" + campStatus);
            if (isValidCampaignStateForDelete(campStatus.getCampaignStatusName())) {
                campLogger.trace(userTrxInfo.logId() + "Camp(" + campId + ") update status to " + deletedCampStatus);
                updatedCampsCount += campaignDao.updateCampaignStatus(userTrxInfo.getUser().getAccountId(), campId,
                        deletedCampStatus);
                campLogger.trace(userTrxInfo.logId() + "Camp(" + campId + ") status is updated ");
            } else {
                invalidCampStateCount++;
                campLogger.warn(userTrxInfo.logId() + "Campaign with id(" + campId
                        + ") is active and cannot be deleted");
            }

            notFoundCampCount = campaignIds.size() - updatedCampsCount - invalidCampStateCount;
        }

        campLogger.info(userTrxInfo.logInfo() + "Updated campaigns status to "
                + deletedCampStatus.getCampaignStatusName() + ", campaigns Ids " + campaignIds + ", count="
                + campaignIds.size() + ", actual updated count=" + updatedCampsCount
                + ", invalid campaign state count=" + invalidCampStateCount + ", not found campaigns count="
                + notFoundCampCount);
    }

    public CampaignDetails getCampaignDetails(UserTrxInfo userTrxInfo, String campId)
            throws IneligibleAccountException, DBException {
        CampaignDetails campaignDetails = new CampaignDetails();
        campLogger.info(userTrxInfo.logInfo() + "Get campaign details, campaignId=" + campId);

        campLogger.debug(userTrxInfo.logId() + "Retrieve normal and virtual lists with counts");
        List<ListType> listTypes = new ArrayList<>();
        // TODO may add intra lists, subintralist and customized list to the
        // list types.
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.NORMAL_LIST));
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.VIRTUAL_LIST));
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.CUSTOMIZED_LIST));
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.INTRA_LIST));
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.INTRA_SUB_LIST));

        List<ContactList> campaignLists = listManegementBean.getContactLists(userTrxInfo, campId, listTypes, true);
        List<ContactListInfoModel> contactListsInfo;
        List<ContactModel> individualContacts = new ArrayList<>();
        if (campaignLists != null) {
            contactListsInfo = listConversionBean.getContactListsInfoModel(campaignLists);
        } else {
            contactListsInfo = new ArrayList<ContactListInfoModel>();
        }
        campaignDetails.setContactLists(contactListsInfo);

        campLogger.debug(userTrxInfo.logInfo() + "Retrieve temp lists for individual contacts");
        listTypes.clear();
        listTypes.add(contactListTypeDao.getCachedObjectByName(ListTypeName.TEMP_LIST));
        campaignLists = listManegementBean.getContactLists(userTrxInfo, campId, listTypes, false);
        campLogger.debug(userTrxInfo.logInfo() + "Retrieved temp lists, lists count=" + campaignLists.size());
        for (ContactList contactList : campaignLists) {
            List<Contact> listContacts = contactList.getListContacts();
            List<ContactModel> contacts;
            if (listContacts != null) {
                campLogger.trace(userTrxInfo.logInfo() + "Temp list " + contactList.logId()
                        + " retrieved with contacts, contacts count=" + listContacts.size()
                        + ", convert contacts to contact model");
                contacts = listConversionBean.getContactsModel(listContacts);
            } else {
                campLogger.trace(userTrxInfo.logInfo() + "Temp list " + contactList.logId()
                        + " retrieved without contacts, retrieve contacts");
                contacts = listManegementBean.getContactList(userTrxInfo, contactList.getListId());
                campLogger.trace(userTrxInfo.logInfo() + "Temp list " + contactList.logId()
                        + "contacts retrieved, contacts count=" + contacts.size());
            }
            campLogger.trace(userTrxInfo.logId() + "Added individual contacts from " + contactList.logId() + ", count="
                    + contacts.size());
            individualContacts.addAll(contacts);
        }

        campaignDetails.setIndividualContacts(individualContacts);
        campLogger.info(userTrxInfo.logId() + "Retrived campaign datails, contact lists: " + contactListsInfo
                + ", individualContacts: " + individualContacts);
        return campaignDetails;
    }

    @Override
    public CampaignStatus getCampaignStatus(CampaignStatusName statusName) {
        return campaignStatusDao.getCachedObjectByName(statusName);
    }

    @Override
    public List<Campaign> adminGetCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param) {

        campLogger.info(adminTrxInfo.logInfo() + param);
        List<Campaign> result;
        try {
            result = campaignDao.adminGetCampaign(param.getDateFrom(), param.getDateTo(), param.getAccountId(),
                    param.getCompanyName(), param.getBillingMsisdn(), param.getSenderName(), param.getUserName(),
                    param.getStatuses());
            campLogger.debug(adminTrxInfo.logId() + "called successfully.");
            return result;

        } catch (IllegalArgumentException e) {
            String logMsg = adminTrxInfo.logId() + "sql fromed incorrecty";
            campLogger.error(logMsg, e);
            campLogger.error(logMsg + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Campaign> adminGetCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param, int first, int max) {
        campLogger.info(adminTrxInfo.logInfo() + param);
        List<Campaign> result;
        try {
            result = campaignDao.adminGetCampaign(param.getDateFrom(), param.getDateTo(), param.getAccountId(),
                    param.getCompanyName(), param.getBillingMsisdn(), param.getSenderName(), param.getUserName(),
                    param.getStatuses(), first, max);
            campLogger.debug(adminTrxInfo.logId() + "called successfully.");
            return result;

        } catch (IllegalArgumentException e) {
            String logMsg = adminTrxInfo.logId() + "sql fromed incorrecty";
            campLogger.error(logMsg, e);
            campLogger.error(logMsg + e.getMessage());
            throw e;
        }
    }

    @Override
    public Long adminCountCampaign(AdminTrxInfo adminTrxInfo, CampaignSearchParam param) {

        campLogger.info(adminTrxInfo.logInfo() + param);
        long result;
        try {
            result = campaignDao.adminCountCampaign(param.getDateFrom(), param.getDateTo(), param.getAccountId(),
                    param.getCompanyName(), param.getBillingMsisdn(), param.getSenderName(), param.getUserName(),
                    param.getStatuses());

            campLogger.trace(adminTrxInfo.logId() + "the query has: " + result + " campaigns");
            return result;

        } catch (IllegalArgumentException e) {
            String logMsg = adminTrxInfo.logId() + "sql fromed incorrecty";
            campLogger.error(logMsg, e);
            campLogger.error(logMsg + e.getMessage());
            throw e;
        }
    }

    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void updateCampaignStatus(UserTrxInfo userTrxInfo, String campaignId, CampaignActionName actionToUpdateStatus)
            throws IneligibleAccountException, CampaignNotFoundException, InvalidCampaignActionException, DBException {
        
        ActionName requiredAction=getUserActionRequiredForStatusUpdate(actionToUpdateStatus);
        if(requiredAction==null){
            throw new InvalidCampaignActionException("Invalid action to update campaigns's status");
        }
        userTrxInfo.addUserAction(requiredAction);
        
        // Check account eligibility
        campLogger.info(userTrxInfo.logInfo() + "update campaign status campaignId = " + campaignId + ", campaignAction = " + actionToUpdateStatus);
        campLogger.debug(userTrxInfo.logId() + "Check account eligibility for action: " + userTrxInfo.getUserActions());
        accountManagement.checkAccountAndUserEligibility(userTrxInfo.getAccountUserTrxInfo());

        campLogger.debug(userTrxInfo.logId() + "Account is eligible for action: " + userTrxInfo.getUserActions());
        
        Campaign campaign = campaignDao.find(campaignId);
        if (campaign == null) {
            throw new CampaignNotFoundException(campaignId);
        }
        
        boolean isSendAtApprovalMode = (boolean) Configs.CAMP_SEND_AT_APPROVAL.getValue();
        if (!validActionsForStatusUpdate(campaign.getStatus().getCampaignStatusName(), actionToUpdateStatus, isSendAtApprovalMode)) {
            throw new InvalidCampaignActionException("Invalid action to update current campaigns's status");
        }
        
        CampaignStatusName newStatusName = getNewCampaignStatus(actionToUpdateStatus, isSendAtApprovalMode);
        if (newStatusName != null) {
            CampaignStatus newStatus = getCampaignStatus(newStatusName);
            campaignDao.updateCampaignStatus(userTrxInfo.getUser().getAccountId(), campaignId, newStatus);
            campLogger.info(userTrxInfo.logId() + "Updated campaign status successfully");
        } else {
            throw new InvalidCampaignActionException("Invalid action to update current campaigns's status");
        }

    }

    @Override
    public List<CampaignStatus> getAllCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));

        return listOfStatus;
    }

    @Override
    public List<CampaignStatus> getActiveCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.NEW));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PAUSED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.RUNNING));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.ON_HOLD));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.PARTIAL_RUN));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
        return listOfStatus;
    }

    @Override
    public List<CampaignStatus> getArchiveCampaignStatusList() {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.CANCELLED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FAILED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.FINISHED));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.APPROVAL_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.SEND_OBSOLETE));
        listOfStatus.add(getCampaignStatus(CampaignStatusName.REJECTED));
        return listOfStatus;
    }
    
    @Override
    public List<CampaignStatus> getPendingCampaignStatusList() {
      List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        listOfStatus.add(getCampaignStatus(CampaignStatusName.WAITING_APPROVAL));
        return listOfStatus;
    }

    private List<CampaignStatus> getCampaignStatusList(List<CampaignStatusName> statuses) {
        List<CampaignStatus> listOfStatus = new ArrayList<CampaignStatus>();
        for (CampaignStatusName campaignStatusName : statuses) {
            listOfStatus.add(getCampaignStatus(campaignStatusName));
        }
        return listOfStatus;
    }

    private void getCampSMSStats(UserTrxInfo trxInfo, List<CampaignModel> campaigns) throws DBException {

        String campaignNums = "Campaigns[";
        for (CampaignModel campaignModel : campaigns) {

            campaignNums += campaignModel.getCampaignId() + ", ";
        }
        campaignNums = campaignNums.substring(0, campaignNums.length() - 2) + "].";

        campLogger.info(trxInfo.logInfo() + "Getting SMS status for " + campaignNums);
        List<String> campIdList = new ArrayList<>();
        for (CampaignModel campaign : campaigns) {
            campIdList.add(campaign.getCampaignId());
        }
        List<CampSMSStats> campSMSStats = campaignDao.findCampSMSStats(campIdList);
        if (campSMSStats != null && !campSMSStats.isEmpty()) {
            campLogger.debug(trxInfo.logId() + "Found " + campSMSStats.size()
                    + "record(s), adding Campaign SMS stats to Campaign Model");
            rcBean.getActiveCampaignStats(campSMSStats, campaigns);
            campLogger.info(trxInfo.logId() + "Campaign SMS stats added to Campaign Model successfully.");
        } else {
            // ERROR Shouldn't happend
            campLogger.warn(trxInfo.logId() + "Found: " + campSMSStats);
        }
    }

    private boolean isValidCampaignStateForDelete(CampaignStatusName campStatus) {
        if (isActiveCampaign(campStatus)) {
            return false;
        }
        return true;
    }
    
    private ActionName getUserActionRequiredForStatusUpdate(CampaignActionName actionToUpdateStatus) {
        if (actionToUpdateStatus == CampaignActionName.SEND) {
            return ActionName.SEND_CAMPAIGN;
        }else if (actionToUpdateStatus == CampaignActionName.APPROVE) {
            return ActionName.APPROVE_CAMPAIGN;
        }else if (actionToUpdateStatus == CampaignActionName.REJECT){
            return ActionName.REJECT_CAMPAIGN;
        }
        return null;
    }
    
    private boolean validActionsForStatusUpdate(CampaignStatusName campaignCurrentStatus, CampaignActionName actionToUpdateStatus, boolean isSendAtApprovalMode) {
        if (isSendAtApprovalMode && campaignCurrentStatus == CampaignStatusName.APPROVED && actionToUpdateStatus == CampaignActionName.SEND) {
            return true;
        }
        if (campaignCurrentStatus == CampaignStatusName.WAITING_APPROVAL
                && (actionToUpdateStatus == CampaignActionName.APPROVE || actionToUpdateStatus == CampaignActionName.REJECT)) {
            return true;
        }
        return false;
    }

    private CampaignStatusName getNewCampaignStatus(CampaignActionName actionToUpdateStatus, boolean sendAtApprovalMode) {
        if (actionToUpdateStatus == CampaignActionName.REJECT) {
            return CampaignStatusName.REJECTED;
        } else if (sendAtApprovalMode) {
            if (actionToUpdateStatus == CampaignActionName.APPROVE) {
                return CampaignStatusName.NEW;
            }
        } else {
            if (actionToUpdateStatus == CampaignActionName.APPROVE) {
                return CampaignStatusName.APPROVED;
            }else if (actionToUpdateStatus == CampaignActionName.SEND) {
                return CampaignStatusName.NEW;
            }
        }
        return null;
    }

}
