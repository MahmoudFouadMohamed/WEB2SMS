package com.edafa.web2sms.service.conversoin;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestStatusDaoLocal;
import com.edafa.web2sms.dalayer.dao.interfaces.ProvRequestTypeDaoLocal;
import com.edafa.web2sms.dalayer.enums.ProvReqStatusName;
import com.edafa.web2sms.dalayer.enums.ProvRequestTypeName;
import com.edafa.web2sms.dalayer.model.ProvRequest;
import com.edafa.web2sms.dalayer.model.ProvRequestActive;
import com.edafa.web2sms.dalayer.model.ProvRequestStatus;
import com.edafa.web2sms.dalayer.model.ProvRequestType;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.acc_manag.service.account.exception.TierNotFoundException;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AccountConversionFacingLocal;
import com.edafa.web2sms.service.model.ProvisioningRequest;


/**
 * Session Bean implementation class ProvConversionBean
 */
@Stateless
@LocalBean
public class ProvConversionBean {

	@EJB
	private AccountConversionFacingLocal acctConversion;

	@EJB
	private ProvRequestTypeDaoLocal provRequestTypeDao;

	@EJB
	private ProvRequestStatusDaoLocal provRequestStatusDao;

	public ProvConversionBean() {
		// TODO Auto-generated constructor stub
	}

	public ProvRequest getProvRequest(ProvisioningRequest provRequest) throws TierNotFoundException {
		ProvRequest provisioningRequest = new ProvRequestActive();
		ProvRequestTypeName typeName = provRequest.getRequestType();
		ProvRequestType type = provRequestTypeDao.getCachedObjectByName(typeName);
		provisioningRequest.setRequestType(type);

		provisioningRequest.setRequestId(provRequest.getRequestId());
		provisioningRequest.setAccountAdmin(provRequest.getAccountAdmin().toLowerCase());
		provisioningRequest.setCompanyId(provRequest.getCompanyId());
		provisioningRequest.setCompanyName(provRequest.getCompanyName().toLowerCase());
		provisioningRequest.setEntryDate(provRequest.getEntryDate());
		provisioningRequest.setCallbackUrl(provRequest.getCallbackUrl());

		ProvReqStatusName statusName = provRequest.getStatus();
		if (statusName != null) {
			ProvRequestStatus status = provRequestStatusDao.getCachedObjectByName(statusName);
			provisioningRequest.setStatus(status);
		}

		if (provRequest.getTier() != null) {
			Tier tier = acctConversion.getTier(provRequest.getTier());
			if (tier == null)
				throw new TierNotFoundException(provRequest.getTier().getTierId());
			provisioningRequest.setTier(tier);
		}

		if (provRequest.getSenderName() != null && !provRequest.getSenderName().isEmpty()) {
			provisioningRequest.setSenderName(provRequest.getSenderName());
		}
		if (provRequest.getNewSenderName() != null && !provRequest.getNewSenderName().isEmpty()) {
			provisioningRequest.setNewSenderName(provRequest.getNewSenderName());
		}
		if (provRequest.getUserId() != null && !provRequest.getUserId().isEmpty()) {
			provisioningRequest.setUserId(provRequest.getUserId());
		}
		return provisioningRequest;
	}

	// public ProvRequestActive getProvRequest(ProvisioningRequest provRequest)
	// throws TierNotFoundException {
	// ProvRequestActive provisioningRequest = new ProvRequestActive();
	// ProvRequestTypeName typeName = provRequest.getRequestType();
	// ProvRequestType type =
	// provRequestTypeDao.getCachedObjectByName(typeName);
	// provisioningRequest.setRequestType(type);
	//
	// provisioningRequest.setRequestId(provRequest.getRequestId());
	// provisioningRequest.setAccountAdmin(provRequest.getAccountAdmin());
	// provisioningRequest.setCompanyId(provRequest.getCompanyId());
	// provisioningRequest.setCompanyName(provRequest.getCompanyName());
	// provisioningRequest.setEntryDate(provRequest.getEntryDate());
	// provisioningRequest.setCallbackUrl(provRequest.getCallbackUrl());
	//
	// ProvReqStatusName statusName = provRequest.getStatus();
	// if (statusName != null) {
	// ProvRequestStatus status =
	// provRequestStatusDao.getCachedObjectByName(statusName);
	// provisioningRequest.setStatus(status);
	// }
	//
	// if (provRequest.getTier() != null) {
	// Tier tier = acctConversion.getTier(provRequest.getTier());
	// if (tier == null)
	// throw new TierNotFoundException(provRequest.getTier().getTierId());
	// provisioningRequest.setTier(tier);
	// }
	//
	// if (provRequest.getSenderName() != null &&
	// !provRequest.getSenderName().isEmpty()) {
	// provisioningRequest.setSenderName(provRequest.getSenderName());
	// }
	// return provisioningRequest;
	// }

	public ProvisioningRequest getProvRequest(ProvRequest provRequestActive) {
		ProvisioningRequest provisioningRequest = new ProvisioningRequest();
		ProvRequestTypeName typeName = provRequestActive.getRequestType().getProvReqTypeName();
		provisioningRequest.setRequestType(typeName);

		ProvReqStatusName statusName = provRequestActive.getStatus().getStatusName();
		provisioningRequest.setStatus(statusName);

		provisioningRequest.setRequestId(provRequestActive.getRequestId());
		provisioningRequest.setAccountAdmin(provRequestActive.getAccountAdmin());
		provisioningRequest.setCompanyId(provRequestActive.getCompanyId());
		provisioningRequest.setCompanyName(provRequestActive.getCompanyName());
		provisioningRequest.setEntryDate(provRequestActive.getEntryDate());
		provisioningRequest.setCallbackUrl(provRequestActive.getCallbackUrl());

		if (provRequestActive.getTier() != null) {
			provisioningRequest.setTier(acctConversion.getTierModel(provRequestActive.getTier()));
		}

		if (provRequestActive.getSenderName() != null && !provRequestActive.getSenderName().isEmpty()) {
			provisioningRequest.setSenderName(provRequestActive.getSenderName());
		}
		
		if (provRequestActive.getNewSenderName() != null && !provRequestActive.getNewSenderName().isEmpty()) {
			provisioningRequest.setNewSenderName(provRequestActive.getNewSenderName());
		}
		
		if (provRequestActive.getUserId() != null && !provRequestActive.getUserId().isEmpty()) {
			provisioningRequest.setUserId(provRequestActive.getUserId());
		}
		return provisioningRequest;
	}

	public List<ProvisioningRequest> getProvRequests(List<ProvRequestActive> provRequests) {
		List<ProvisioningRequest> provisioningRequests = new ArrayList<>();
		for (ProvRequestActive provRequestActive : provRequests) {
			ProvisioningRequest req = getProvRequest(provRequestActive);
			provisioningRequests.add(req);
		}
		return provisioningRequests;
	}
}
