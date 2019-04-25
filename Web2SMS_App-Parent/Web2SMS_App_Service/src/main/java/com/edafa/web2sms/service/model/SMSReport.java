package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SMSReport", namespace = "http://www.edafa.com/web2sms/service/model/")
public class SMSReport implements Comparable<SMSReport> {

	@XmlElement(required = true, nillable = false)
	String senderName;
	@XmlElement(required = true, nillable = false)
	int totalSMS;
	@XmlElement(required = true, nillable = false)
	int deliveredSMS;
	@XmlElement(required = true, nillable = false)
	int unDeliveredSMS;
	@XmlElement(required = true, nillable = false)
	int pendingSMS;
	@XmlElement(required = true, nillable = false)
	int failedSMS;
	@XmlElement(required = true, nillable = false)
	int totalSMSSegment;
	@XmlElement(required = true, nillable = false)
	int deliveredSMSSegment;
	@XmlElement(required = true, nillable = false)
	int unDeliveredSMSSegment;
	@XmlElement(required = true, nillable = false)
	int pendingSMSSegment;
	@XmlElement(required = true, nillable = false)
	int failedSMSSegment;

	public SMSReport() {
	}

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
	
	public void incDeliveredSMS(){
		this.deliveredSMS++;
	}
	
	public void incUnDeliveredSMS(){
		this.unDeliveredSMS++;
	}
	
	public void incFailedSMS(){
		this.failedSMS++;
	}
	
	public void incPendingSMS(){
		this.pendingSMS++;
	}
	
	public void incTotalSMS(){
		this.totalSMS++;
	}

	@Override
	public int compareTo(SMSReport o) {
		return	this.senderName.compareToIgnoreCase(o.getSenderName());
		
	}


}
