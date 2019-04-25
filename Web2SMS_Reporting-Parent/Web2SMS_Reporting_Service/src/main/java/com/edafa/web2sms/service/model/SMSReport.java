package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SMSReport", namespace = "http://www.edafa.com/web2sms/reporting/service/model/")
public class SMSReport implements Comparable<SMSReport> {

	@XmlElement(required = true, nillable = false)
	private String senderName;

	@XmlElement(required = true, nillable = false)
	private int totalSMS;

	@XmlElement(required = true, nillable = false)
	private int deliveredSMS;

	@XmlElement(required = true, nillable = false)
	private int unDeliveredSMS;

	@XmlElement(required = true, nillable = false)
	private int pendingSMS;

	@XmlElement(required = true, nillable = false)
	private int failedSMS;

	@XmlElement(required = true, nillable = false)
	private int totalSMSSegment;

	@XmlElement(required = true, nillable = false)
	private int deliveredSMSSegment;

	@XmlElement(required = true, nillable = false)
	private int unDeliveredSMSSegment;

	@XmlElement(required = true, nillable = false)
	private int pendingSMSSegment;

	@XmlElement(required = true, nillable = false)
	private int failedSMSSegment;

	@XmlElement(required = true, nillable = false)
	private int vfSentSms;
	@XmlElement(required = true, nillable = false)
	private int ogSentSms;
	@XmlElement(required = true, nillable = false)
	private int etSentSms;
	@XmlElement(required = true, nillable = false)
	private int weSentSms;
	@XmlElement(required = true, nillable = false)
	private int interSentSms;
	@XmlElement(required = true, nillable = false)
	private int vfDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int ogDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int etDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int weDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int interDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int vfNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int ogNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int etNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int weNotDeliveredSms;
	@XmlElement(required = true, nillable = false)
	private int interNotDeliveredSms;

	@XmlElement(required = true, nillable = false)
	private int vfSentSegments;
	@XmlElement(required = true, nillable = false)
	private int ogSentSegments;
	@XmlElement(required = true, nillable = false)
	private int etSentSegments;
	@XmlElement(required = true, nillable = false)
	private int weSentSegments;
	@XmlElement(required = true, nillable = false)
	private int interSentSegments;
	@XmlElement(required = true, nillable = false)
	private int vfDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int ogDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int etDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int weDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int interDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int vfNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int ogNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int etNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int weNotDeliveredSegments;
	@XmlElement(required = true, nillable = false)
	private int interNotDeliveredSegments;

	public SMSReport() {}

	public SMSReport(String sender) {
		this.senderName = sender;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public int getTotalSMS() {
		return totalSMS;
	}

	public void setTotalSMS(int totalSMS) {
		this.totalSMS = totalSMS;
	}

	public int getDeliveredSMS() {
		return deliveredSMS;
	}

	public void setDeliveredSMS(int deliveredSMS) {
		this.deliveredSMS = deliveredSMS;
	}

	public int getPendingSMS() {
		return pendingSMS;
	}

	public void setPendingSMS(int pendingSMS) {
		this.pendingSMS = pendingSMS;
	}

	public int getFailedSMS() {
		return failedSMS;
	}

	public void setFailedSMS(int failedSMS) {
		this.failedSMS = failedSMS;
	}

	public int getUnDeliveredSMS() {
		return unDeliveredSMS;
	}

	public void setUnDeliveredSMS(int unDeliveredSMS) {
		this.unDeliveredSMS = unDeliveredSMS;
	}

	public int getTotalSMSSegment() {
		return totalSMSSegment;
	}

	public void setTotalSMSSegment(int totalSMSSegment) {
		this.totalSMSSegment = totalSMSSegment;
	}

	public int getDeliveredSMSSegment() {
		return deliveredSMSSegment;
	}

	public void setDeliveredSMSSegment(int deliveredSMSSegment) {
		this.deliveredSMSSegment = deliveredSMSSegment;
	}

	public int getUnDeliveredSMSSegment() {
		return unDeliveredSMSSegment;
	}

	public void setUnDeliveredSMSSegment(int unDeliveredSMSSegment) {
		this.unDeliveredSMSSegment = unDeliveredSMSSegment;
	}

	public int getPendingSMSSegment() {
		return pendingSMSSegment;
	}

	public void setPendingSMSSegment(int pendingSMSSegment) {
		this.pendingSMSSegment = pendingSMSSegment;
	}

	public int getFailedSMSSegment() {
		return failedSMSSegment;
	}

	public void setFailedSMSSegment(int failedSMSSegment) {
		this.failedSMSSegment = failedSMSSegment;
	}

	public int getVfSentSms() {
		return vfSentSms;
	}

	public void setVfSentSms(int vfSentSms) {
		this.vfSentSms = vfSentSms;
	}

	public int getOgSentSms() {
		return ogSentSms;
	}

	public void setOgSentSms(int ogSentSms) {
		this.ogSentSms = ogSentSms;
	}

	public int getEtSentSms() {
		return etSentSms;
	}

	public void setEtSentSms(int etSentSms) {
		this.etSentSms = etSentSms;
	}

	public int getWeSentSms() {
		return weSentSms;
	}

	public void setWeSentSms(int weSentSms) {
		this.weSentSms = weSentSms;
	}

	public int getInterSentSms() {
		return interSentSms;
	}

	public void setInterSentSms(int interSentSms) {
		this.interSentSms = interSentSms;
	}

	public int getVfDeliveredSms() {
		return vfDeliveredSms;
	}

	public void setVfDeliveredSms(int vfDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
	}

	public int getOgDeliveredSms() {
		return ogDeliveredSms;
	}

	public void setOgDeliveredSms(int ogDeliveredSms) {
		this.ogDeliveredSms = ogDeliveredSms;
	}

	public int getEtDeliveredSms() {
		return etDeliveredSms;
	}

	public void setEtDeliveredSms(int etDeliveredSms) {
		this.etDeliveredSms = etDeliveredSms;
	}

	public int getWeDeliveredSms() {
		return weDeliveredSms;
	}

	public void setWeDeliveredSms(int weDeliveredSms) {
		this.weDeliveredSms = weDeliveredSms;
	}

	public int getInterDeliveredSms() {
		return interDeliveredSms;
	}

	public void setInterDeliveredSms(int interDeliveredSms) {
		this.interDeliveredSms = interDeliveredSms;
	}

	public int getVfNotDeliveredSms() {
		return vfNotDeliveredSms;
	}

	public void setVfNotDeliveredSms(int vfNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
	}

	public int getOgNotDeliveredSms() {
		return ogNotDeliveredSms;
	}

	public void setOgNotDeliveredSms(int ogNotDeliveredSms) {
		this.ogNotDeliveredSms = ogNotDeliveredSms;
	}

	public int getEtNotDeliveredSms() {
		return etNotDeliveredSms;
	}

	public void setEtNotDeliveredSms(int etNotDeliveredSms) {
		this.etNotDeliveredSms = etNotDeliveredSms;
	}

	public int getWeNotDeliveredSms() {
		return weNotDeliveredSms;
	}

	public void setWeNotDeliveredSms(int weNotDeliveredSms) {
		this.weNotDeliveredSms = weNotDeliveredSms;
	}

	public int getInterNotDeliveredSms() {
		return interNotDeliveredSms;
	}

	public void setInterNotDeliveredSms(int interNotDeliveredSms) {
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public int getVfSentSegments() {
		return vfSentSegments;
	}

	public void setVfSentSegments(int vfSentSegments) {
		this.vfSentSegments = vfSentSegments;
	}

	public int getOgSentSegments() {
		return ogSentSegments;
	}

	public void setOgSentSegments(int ogSentSegments) {
		this.ogSentSegments = ogSentSegments;
	}

	public int getEtSentSegments() {
		return etSentSegments;
	}

	public void setEtSentSegments(int etSentSegments) {
		this.etSentSegments = etSentSegments;
	}

	public int getWeSentSegments() {
		return weSentSegments;
	}

	public void setWeSentSegments(int weSentSegments) {
		this.weSentSegments = weSentSegments;
	}

	public int getInterSentSegments() {
		return interSentSegments;
	}

	public void setInterSentSegments(int interSentSegments) {
		this.interSentSegments = interSentSegments;
	}

	public int getVfDeliveredSegments() {
		return vfDeliveredSegments;
	}

	public void setVfDeliveredSegments(int vfDeliveredSegments) {
		this.vfDeliveredSegments = vfDeliveredSegments;
	}

	public int getOgDeliveredSegments() {
		return ogDeliveredSegments;
	}

	public void setOgDeliveredSegments(int ogDeliveredSegments) {
		this.ogDeliveredSegments = ogDeliveredSegments;
	}

	public int getEtDeliveredSegments() {
		return etDeliveredSegments;
	}

	public void setEtDeliveredSegments(int etDeliveredSegments) {
		this.etDeliveredSegments = etDeliveredSegments;
	}

	public int getWeDeliveredSegments() {
		return weDeliveredSegments;
	}

	public void setWeDeliveredSegments(int weDeliveredSegments) {
		this.weDeliveredSegments = weDeliveredSegments;
	}

	public int getInterDeliveredSegments() {
		return interDeliveredSegments;
	}

	public void setInterDeliveredSegments(int interDeliveredSegments) {
		this.interDeliveredSegments = interDeliveredSegments;
	}

	public int getVfNotDeliveredSegments() {
		return vfNotDeliveredSegments;
	}

	public void setVfNotDeliveredSegments(int vfNotDeliveredSegments) {
		this.vfNotDeliveredSegments = vfNotDeliveredSegments;
	}

	public int getOgNotDeliveredSegments() {
		return ogNotDeliveredSegments;
	}

	public void setOgNotDeliveredSegments(int ogNotDeliveredSegments) {
		this.ogNotDeliveredSegments = ogNotDeliveredSegments;
	}

	public int getEtNotDeliveredSegments() {
		return etNotDeliveredSegments;
	}

	public void setEtNotDeliveredSegments(int etNotDeliveredSegments) {
		this.etNotDeliveredSegments = etNotDeliveredSegments;
	}

	public int getWeNotDeliveredSegments() {
		return weNotDeliveredSegments;
	}

	public void setWeNotDeliveredSegments(int weNotDeliveredSegments) {
		this.weNotDeliveredSegments = weNotDeliveredSegments;
	}

	public int getInterNotDeliveredSegments() {
		return interNotDeliveredSegments;
	}

	public void setInterNotDeliveredSegments(int interNotDeliveredSegments) {
		this.interNotDeliveredSegments = interNotDeliveredSegments;
	}

	public void setSentSms(int vfSentSms, int ogSentSms, int etSentSms, int weSentSms, int interSentSms) {
		this.vfSentSms = vfSentSms;
		this.ogSentSms = ogSentSms;
		this.etSentSms = etSentSms;
		this.weSentSms = weSentSms;
		this.interSentSms = interSentSms;
	}

	public void setDeliveredSms(int vfDeliveredSms, int ogDeliveredSms, int etDeliveredSms, int weDeliveredSms,
			int interDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
		this.ogDeliveredSms = ogDeliveredSms;
		this.etDeliveredSms = etDeliveredSms;
		this.weDeliveredSms = weDeliveredSms;
		this.interDeliveredSms = interDeliveredSms;
	}

	public void setNotDeliveredSms(int vfNotDeliveredSms, int ogNotDeliveredSms, int etNotDeliveredSms,
			int weNotDeliveredSms, int interNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
		this.ogNotDeliveredSms = ogNotDeliveredSms;
		this.etNotDeliveredSms = etNotDeliveredSms;
		this.weNotDeliveredSms = weNotDeliveredSms;
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public void setSentSegments(int vfSentSegments, int ogSentSegments, int etSentSegments, int weSentSegments,
			int interSentSegments) {
		this.vfSentSegments = vfSentSegments;
		this.ogSentSegments = ogSentSegments;
		this.etSentSegments = etSentSegments;
		this.weSentSegments = weSentSegments;
		this.interSentSegments = interSentSegments;
	}

	public void setDeliveredSegments(int vfDeliveredSegments, int ogDeliveredSegments, int etDeliveredSegments,
			int weDeliveredSegments, int interDeliveredSegments) {
		this.vfDeliveredSegments = vfDeliveredSegments;
		this.ogDeliveredSegments = ogDeliveredSegments;
		this.etDeliveredSegments = etDeliveredSegments;
		this.weDeliveredSegments = weDeliveredSegments;
		this.interDeliveredSegments = interDeliveredSegments;
	}

	public void setNotDeliveredSegments(int vfNotDeliveredSegments, int ogNotDeliveredSegments,
			int etNotDeliveredSegments, int weNotDeliveredSegments, int interNotDeliveredSegments) {
		this.vfNotDeliveredSegments = vfNotDeliveredSegments;
		this.ogNotDeliveredSegments = ogNotDeliveredSegments;
		this.etNotDeliveredSegments = etNotDeliveredSegments;
		this.weNotDeliveredSegments = weNotDeliveredSegments;
		this.interNotDeliveredSegments = interNotDeliveredSegments;
	}

	public void incDeliveredSMS(int count) {
		this.deliveredSMS += count;
	}

	public void incUnDeliveredSMS(int count) {
		this.unDeliveredSMS += count;
	}

	public void incFailedSMS(int count) {
		this.failedSMS += count;
	}

	public void incPendingSMS(int count) {
		this.pendingSMS += count;
	}

	public void incTotalSMS(int count) {
		this.totalSMS += count;
	}

	public void incDeliveredSegments(int count) {
		this.deliveredSMSSegment += count;
	}

	public void incUnDeliveredSegments(int count) {
		this.unDeliveredSMSSegment += count;
	}

	public void incFailedSegments(int count) {
		this.failedSMSSegment += count;
	}

	public void incPendingSegments(int count) {
		this.pendingSMSSegment += count;
	}

	public void incTotalSegments(int count) {
		this.totalSMSSegment += count;
	}
	@Override
	public int compareTo(SMSReport o) {
		return this.senderName.compareToIgnoreCase(o.getSenderName());

	}

}
