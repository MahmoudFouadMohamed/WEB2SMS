package com.edafa.web2sms.quota;

import java.util.concurrent.atomic.AtomicInteger;

import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.quota.exceptions.InSufficientQuotaException;
import com.edafa.web2sms.quota.interfaces.AccountQuotaHandle;

public class PostpaidAccountQuotaHandle implements AccountQuotaHandle {

	

	@Override
	public AtomicInteger getConsumedQuota() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setConsumedQuota(AtomicInteger consumedQuota) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AtomicInteger getReservedQuota() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setReservedQuota(AtomicInteger reservedQuota) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getAccountId() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void associateCamp(String campaignId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean disassociateCamp(String campaignId) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void calculateDelta() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AtomicInteger getConsumedDelta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AtomicInteger getReservedDelta() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateHandle(AccountTier accountTier) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int reserveRollback(int reserved) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean consumeUnit(int count) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int drainReserved(LanguageEnum lang, String smsText) throws InSufficientQuotaException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void updateHandle() {
		// TODO Auto-generated method stub
		
	}

	

}
