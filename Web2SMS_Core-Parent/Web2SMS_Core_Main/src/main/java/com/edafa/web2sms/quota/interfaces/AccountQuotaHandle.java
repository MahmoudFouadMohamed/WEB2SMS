package com.edafa.web2sms.quota.interfaces;

import java.util.concurrent.atomic.AtomicInteger;

import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.smshandler.sms.SMS;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.quota.exceptions.InSufficientQuotaException;

public interface AccountQuotaHandle {

	int drainReserved(LanguageEnum lang, String smsText) throws InSufficientQuotaException;

	int reserveRollback(int reserved);

	boolean consumeUnit(int count);

	void associateCamp(String campaignId);

	boolean disassociateCamp(String campaignId);

	void calculateDelta();

	void updateHandle(AccountTier accountTier);

	void updateHandle();

	AtomicInteger getConsumedQuota();

	void setConsumedQuota(AtomicInteger consumedQuota);

	AtomicInteger getReservedQuota();

	void setReservedQuota(AtomicInteger reservedQuota);

	String getAccountId();

	AtomicInteger getConsumedDelta();

	AtomicInteger getReservedDelta();
	
}
