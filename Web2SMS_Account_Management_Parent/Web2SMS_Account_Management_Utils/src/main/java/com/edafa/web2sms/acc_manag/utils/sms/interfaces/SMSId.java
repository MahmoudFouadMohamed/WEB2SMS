package com.edafa.web2sms.acc_manag.utils.sms.interfaces;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

public class SMSId implements Serializable {
	private static final long serialVersionUID = 2658748542274493342L;
	private static final int RESET_TIME_FRAME = 1000;
	private static final String PREFIX = "3";
	private static final AtomicLong sequence = new AtomicLong();
	private static volatile Long resetDate = Long.valueOf(System.currentTimeMillis() + 1000L);
	private static final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
	private static final String seqStrFormat = "%06d";
	String smsId;

	public SMSId(){}
	
	public static void setRefSMSId(long refSMSId) {
		sequence.set(refSMSId);
	}

	public SMSId(String smsId) {
		this.smsId = smsId;
	}

	public static SMSId getSMSId() {
		Date now = new Date();
		long nowL = now.getTime();

		if (nowL >= resetDate.longValue()) {
			synchronized (resetDate) {
				if (nowL >= resetDate.longValue()) {
					sequence.set(0L);
					resetDate = Long.valueOf(nowL + 1000L);
				}
			}
		}

		String id = "2" + df.format(now)
				+ String.format("%06d", new Object[] { Long.valueOf(sequence.incrementAndGet()) });
		return new SMSId(id);
	}

    public static SMSId getSMSId(String prefix) {
        Date now = new Date();
        long nowL = now.getTime();

        if (nowL >= resetDate) {
            synchronized (resetDate) {
                if (nowL >= resetDate) {
                    sequence.set(0);
                    resetDate = nowL + 1000;
                }
            }
        }

        String id = prefix + df.format(now) + String.format(seqStrFormat, sequence.incrementAndGet());
        return new SMSId(id);
    }
	
	public String getId() {
		return this.smsId;
	}

	public String toString() {
		return String.valueOf(this.smsId);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SMSId))
			return false;
		SMSId other = (SMSId) obj;
		return this.smsId.equals(other.smsId);
	}
        
    public String dumpId() {
        return "SMS(" + smsId + "): ";
    }
}