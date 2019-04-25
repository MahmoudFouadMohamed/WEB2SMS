package com.edafa.web2sms.service.prov.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.adapters.cloud.callback.model.StatusType;
import com.edafa.web2sms.adapters.cloud.exception.FailedToCallBackCloud;
import com.edafa.web2sms.adapters.tibco.exception.SRCreationFailed;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.ProvRequestArch;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountAlreadyActiveException;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.DuplicateProvioniongRequest;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidAccountStateException;
import com.edafa.web2sms.acc_manag.service.account.exception.InvalidSenderType;
import com.edafa.web2sms.acc_manag.service.account.exception.ProvRequestNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameAlreadyAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.user.exceptions.UserNotFoundException;
import com.edafa.web2sms.acc_manag.service.model.AccountModelFullInfo;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.service.model.ProvTrxInfo;
import com.edafa.web2sms.service.model.ProvisioningRequest;
import com.edafa.web2sms.service.model.TrxInfo;
import com.edafa.web2sms.service.prov.exception.InvalidProvRequestException;
import com.edafa.web2sms.utils.sms.exception.InvalidSMSSender;

@Local
public interface ServiceProvisioningLocal {

	// void handleCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest
	// provRequest);

	void activateService(ProvTrxInfo provTrxInfo, ProvisioningRequest provRequest) throws DBException,
			InvalidAccountException, AccountAlreadyActiveException, TierNotFoundException,
			ProvRequestNotFoundException, SenderNameAlreadyAttached, InvalidSMSSender, FailedToCallBackCloud,
			InvalidProvRequestException, Exception;

	// void handleCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest
	// provRequest) throws DBException,
	// DuplicateProvioniongRequest, InvalidAccountStateException,
	// AccountNotFoundException, TierNotFoundException,
	// InvalidSMSSender;

	void sendCloudCallBack(String provId, String callBackUrl, StatusType status) throws FailedToCallBackCloud;

	void sendCloudCallBack(String provId, String callBackUrl, StatusType status, String errMessage)
			throws FailedToCallBackCloud;

	void migrateService(ProvTrxInfo provTrxInfo, ProvisioningRequest provRequest) throws DBException,
			FailedToCallBackCloud, InvalidProvRequestException, InvalidAccountStateException, AccountNotFoundException,
			TierNotFoundException;

	ProvisioningRequest findProvisioningRequest(ProvTrxInfo provTrxInfo, String requestId)
			throws ProvRequestNotFoundException, DBException;

//	void updateProvisioningRequestStatus(ProvTrxInfo trxInfo, String requestId, ProvReqStatusName newStatus);

	void createProvisioningRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest, boolean archive)
			throws DuplicateProvioniongRequest, DBException, TierNotFoundException;

	void archiveProvisioningRequest(ProvTrxInfo trxInfo, String requestId, ProvReqStatusName archiveStatus)
			throws DBException, ProvRequestNotFoundException;

	AccountModelFullInfo findAccountByCoAdminFullInfo(TrxInfo trxInfo, String accountHolderId) throws DBException,
			AccountNotFoundException;

	void cancelProvisioningRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) throws DBException,
			ProvRequestNotFoundException, FailedToCallBackCloud;

	List<ProvisioningRequest> findActiveProvisioningRequests(TrxInfo trxInfo, String accountAdmin) throws DBException;

	void handleAsyncCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest) ;

	void handleCloudProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest, boolean createSR)
			throws TierNotFoundException, DuplicateProvioniongRequest, DBException, SRCreationFailed, InvalidSMSSender, 
			AccountNotFoundException, UserNotFoundException;

	// void createProvSR(ProvTrxInfo trxInfo, ProvisioningRequest provRequest)
	// throws SRCreationFailed;

	void requestChangeSender(ProvTrxInfo trxInfo, String oldSender, String newSender) throws DBException,
			AccountNotFoundException, SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed,
			DuplicateProvioniongRequest, InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException;

	void requestAddSender(ProvTrxInfo trxInfo, String sender) throws DBException, AccountNotFoundException,
			SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed, DuplicateProvioniongRequest,
			InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException;

	void requestDeleteSender(ProvTrxInfo trxInfo, String sender) throws DBException, AccountNotFoundException,
			SenderNameAlreadyAttached, InvalidSMSSender, SRCreationFailed, DuplicateProvioniongRequest,
			InvalidSenderType, SenderNameNotAttached, InvalidProvRequestException;

	AccountModelFullInfo findAccountByMSISDNFullInfo(TrxInfo trxInfo, String msisdn) throws DBException,
			AccountNotFoundException;

	List<ProvRequestArch> findProvArchByAccount(AdminTrxInfo trxInfo, String accountId) throws DBException;
	
//	 void HandleInternalSuspendProvRequest(ProvTrxInfo trxInfo, ProvisioningRequest provRequest);

}
