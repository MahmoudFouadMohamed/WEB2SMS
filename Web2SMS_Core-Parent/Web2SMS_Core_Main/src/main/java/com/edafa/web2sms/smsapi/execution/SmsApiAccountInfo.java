package com.edafa.web2sms.smsapi.execution;

/**
 *
 * @author mahmoud
 */
public class SmsApiAccountInfo {

    protected Counter accountConcurrentSmsCounter;
    protected boolean accountAllowedToSend = true;

    public SmsApiAccountInfo(int accountConcurrentSmsCounterMaxCount) {
        this.accountConcurrentSmsCounter = new Counter(accountConcurrentSmsCounterMaxCount);
    }

    public void setAccountAllowedToSend(boolean accountAllowedToSend) {
        this.accountAllowedToSend = accountAllowedToSend;
    }

    public int getAccountConcurrentSmsCounter() {
        return accountConcurrentSmsCounter.get();
    }

    public boolean isAccountAllowedToSend() {
        return accountAllowedToSend;
    }

    public boolean incrementAccountConcurrentSmsCounter() {
        return accountConcurrentSmsCounter.increment();
    }

    public void decrementAccountConcurrentSmsCounter() {
        accountConcurrentSmsCounter.decrement();
    }

    @Override
    public String toString() {
        return "SmsApiAccountInfo{" + "accountConcurrentSmsCounter=" + accountConcurrentSmsCounter.get() + ", accountAllowedToSend=" + accountAllowedToSend + '}';
    }

}
