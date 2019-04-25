package com.edafa.web2sms.quota;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.edafa.smsgw.dalayer.enums.LanguageEnum;
import com.edafa.smsgw.smshandler.sms.SMSUtils;
import com.edafa.web2sms.dalayer.dao.interfaces.AccountQuotaDaoLocal;
import com.edafa.web2sms.dalayer.model.AccountQuota;
import com.edafa.web2sms.dalayer.model.AccountTier;
import com.edafa.web2sms.dalayer.model.Campaign;
import com.edafa.web2sms.quota.exceptions.InSufficientQuotaException;
import com.edafa.web2sms.quota.interfaces.AccountQuotaHandle;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class PrepaidAccountQuotaHandle implements AccountQuotaHandle {

	protected Logger acctLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_HANDLER.name());

	protected AccountQuotaDaoLocal acctQuotaDao;
	protected final String accountId;
	protected AtomicInteger initalConsumed;
	protected AtomicInteger initalReserved;
	protected AtomicInteger consumedDelta;
	protected AtomicInteger reservedDelta;
	protected int totalQuota;
	protected AtomicInteger consumedQuota;
	protected AtomicInteger reservedQuota;
	protected AccountTier accountTier;
	protected AccountQuota accountQuota;
	protected Set<String> associatedCamps;

	public PrepaidAccountQuotaHandle(Campaign camp, AccountTier accountTier) {
		this.accountId = camp.getAccountUser().getAccountId();
		this.associatedCamps = new HashSet<String>();
		associatedCamps.add(camp.getCampaignId());
		updateHandle(accountTier);
		acctLogger.info(logId() + "Created Successfully.");
		acctLogger.trace(this.toString());

	}

	@Override
	public int drainReserved(LanguageEnum lang, String smsText) throws InSufficientQuotaException {
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "calculate the reserved segments from SMS:" + smsText + ", Lang:" + lang);
            }
		int segCount = SMSUtils.calcSegCount(lang, smsText);
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "SMSs to be consumed from reservation: " + segCount);
            }
		boolean isupdated = false;
		do {
			int expectedValue = this.reservedQuota.get();
			if (expectedValue >= segCount) {
				isupdated = this.reservedQuota.compareAndSet(expectedValue, expectedValue - segCount);
                            if (isupdated) {
                                if (acctLogger.isTraceEnabled()) {
                                    acctLogger.trace(logId() + "reservation remaining: " + (expectedValue - segCount));
                                }
                            }
			} else {
				String message = logId() + "Can't reserve  quota expectedValue:" + expectedValue
						+ "Segments to be deducted:" + segCount;
				throw new InSufficientQuotaException(message);
			}

		} while (!isupdated);
		return segCount;
	}

	@Override
	public int reserveRollback(int reserved) {
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "SMSs to be added to reservation: " + reserved);
            }
		int remaining = this.reservedQuota.addAndGet(reserved);
		return remaining;
	}

	@Override
	public boolean consumeUnit(int count) {
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "Apply Consuming to " + this.toString());
            }
		int consumed = this.consumedQuota.addAndGet(count);
		return (consumed + reservedQuota.get() <= totalQuota);
	}

	@Override
	public void associateCamp(String campaignId) {

		associatedCamps.add(campaignId);
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "Adding campaign(" + campaignId + ") to associated list of handle, list size= " + associatedCamps.size());
            }

	}

	@Override
	public boolean disassociateCamp(String campaignId) {
		associatedCamps.remove(campaignId);
            if (acctLogger.isTraceEnabled()) {
                acctLogger.trace(logId() + "removing campaign(" + campaignId + ") from associated list of handle, list size= " + associatedCamps.size());
            }
		return associatedCamps.isEmpty();
	}

	@Override
	public void calculateDelta() {
		acctLogger.trace(logId() + this.toString());
		this.consumedDelta = new AtomicInteger(consumedQuota.get());
		this.reservedDelta = new AtomicInteger(this.reservedQuota.get() - this.initalReserved.get());
		acctLogger.trace(logId() + "consumedDelta=" + consumedDelta + ", reservedDelta=" + reservedDelta);

	}

	@Override
	public void updateHandle(AccountTier accountTier) {
		this.accountTier = accountTier;
		this.accountQuota = accountTier.getAccountQuota();
		totalQuota = accountTier.getTier().getQuota();
		this.consumedDelta = new AtomicInteger(0);
		this.reservedDelta = new AtomicInteger(0);
		this.consumedQuota = new AtomicInteger(0);
		this.reservedQuota = new AtomicInteger(accountQuota.getReservedSmss() - consumedDelta.get());
		this.initalConsumed = new AtomicInteger(accountQuota.getConsumedSmss());
		this.initalReserved = new AtomicInteger(accountQuota.getReservedSmss() - consumedDelta.get());
	}

	@Override
	public void updateHandle() {

		this.consumedQuota = new AtomicInteger(0);
		this.consumedDelta = new AtomicInteger(0);
		this.reservedDelta = new AtomicInteger(0);
		this.reservedQuota = new AtomicInteger(accountQuota.getReservedSmss() - consumedDelta.get());
		this.initalConsumed = new AtomicInteger(accountQuota.getConsumedSmss());
		this.initalReserved = new AtomicInteger(accountQuota.getReservedSmss() - consumedDelta.get());
	}

	public int getTotalQuota() {
		return totalQuota;
	}

	public void setTotalQuota(int totalQuota) {
		this.totalQuota = totalQuota;
	}

	@Override
	public AtomicInteger getConsumedQuota() {
		return consumedQuota;
	}

	@Override
	public void setConsumedQuota(AtomicInteger consumedQuota) {
		this.consumedQuota = consumedQuota;
	}

	@Override
	public AtomicInteger getReservedQuota() {
		return reservedQuota;
	}

	@Override
	public void setReservedQuota(AtomicInteger reservedQuota) {
		this.reservedQuota = reservedQuota;
	}

	public AccountTier getAccountTier() {
		return accountTier;
	}

	public void setAccountTier(AccountTier accountTier) {
		this.accountTier = accountTier;
	}

	public AccountQuota getAccountQuota() {
		return accountQuota;
	}

	public void setAccountQuota(AccountQuota accountQuota) {
		this.accountQuota = accountQuota;
	}

	@Override
	public String getAccountId() {
		return accountId;
	}

	public AtomicInteger getInitalconsumed() {
		return initalConsumed;
	}

	public void setInitalconsumed(AtomicInteger initalconsumed) {
		this.initalConsumed = initalconsumed;
	}

	public AtomicInteger getInitalreserved() {
		return initalReserved;
	}

	public void setInitalreserved(AtomicInteger initalreserved) {
		this.initalReserved = initalreserved;
	}

	public Set<String> getAssociatedCamps() {
		return associatedCamps;
	}

	public void setAssociatedCamps(Set<String> associatedCamps) {
		this.associatedCamps = associatedCamps;
	}

	@Override
	public AtomicInteger getConsumedDelta() {
		return consumedDelta;
	}

	public void setConsumedDelta(AtomicInteger consumedDelta) {
		this.consumedDelta = consumedDelta;
	}

	@Override
	public AtomicInteger getReservedDelta() {
		return reservedDelta;
	}

	public void setReservedDelta(AtomicInteger reservedDelta) {
		this.reservedDelta = reservedDelta;
	}

	private String logId() {
		return "PrepaidAccountQuotaHandler(" + this.accountId + ") ";
	}

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("PrepaidAccountQuotaHandle[accountId=");
        str = str.append(accountId)
        .append(", initalConsumed=")
        .append(initalConsumed)
        .append(", initalReserved=")
        .append(initalReserved)
        .append(", consumedDelta=")
        .append(consumedDelta)
        .append(", reservedDelta=")
        .append(reservedDelta)
        .append(", totalQuota=")
        .append(totalQuota)
        .append(", consumedQuota=")
        .append(consumedQuota)
        .append(", reservedQuota=")
        .append(reservedQuota)
        .append(", associatedCamps=")
        .append(associatedCamps)
        .append("]");
        return str.toString();
    }
}
