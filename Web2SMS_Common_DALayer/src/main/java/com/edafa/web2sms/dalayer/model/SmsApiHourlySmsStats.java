
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Entity
@Table(name = "SMSAPI_HOURLY_SMS_STATS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "SmsApiHourlySmsStats.findAll", query = "SELECT s FROM SmsApiHourlySmsStats s"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findByProcessingDate", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.processingDate = :processingDate"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findByProcessingHour", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.processingHour = :processingHour"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findBySenderName", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.senderName = :senderName"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findByOwnerId", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.ownerId = :ownerId"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findByOwnerIdAndSenderAndDates", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.ownerId = :ownerId AND s.smsApiHourlySmsStatsPK.senderName = :senderName AND s.smsApiHourlySmsStatsPK.processingDate >= :from and s.smsApiHourlySmsStatsPK.processingDate <= :to"),
		@NamedQuery(name = "SmsApiHourlySmsStats.countByOwnerIdAndSenderAndDates", query = "SELECT count(s) FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.ownerId = :ownerId AND s.smsApiHourlySmsStatsPK.senderName = :senderName AND s.smsApiHourlySmsStatsPK.processingDate >= :from and s.smsApiHourlySmsStatsPK.processingDate <= :to"),
		@NamedQuery(name = "SmsApiHourlySmsStats.findByOwnerIdAndDates", query = "SELECT s FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.ownerId = :ownerId AND s.smsApiHourlySmsStatsPK.processingDate >= :from and s.smsApiHourlySmsStatsPK.processingDate <= :to"),
		@NamedQuery(name = "SmsApiHourlySmsStats.countByOwnerIdAndDates", query = "SELECT count(s) FROM SmsApiHourlySmsStats s WHERE s.smsApiHourlySmsStatsPK.ownerId = :ownerId AND s.smsApiHourlySmsStatsPK.processingDate >= :from and s.smsApiHourlySmsStatsPK.processingDate <= :to") })
public class SmsApiHourlySmsStats implements Serializable {

	private static final long serialVersionUID = -6096538622288979180L;

	@EmbeddedId
	protected SmsApiHourlySmsStatsPK smsApiHourlySmsStatsPK;

	@Column(name = "VF_NEW_SEG")
	private Integer vfNewSeg;
	@Column(name = "VF_NEW_SMS")
	private Integer vfNewSms;
	@Column(name = "VF_SUBMITTED_SEG")
	private Integer vfSubmittedSeg;
	@Column(name = "VF_SUBMITTED_SMS")
	private Integer vfSubmittedSms;
	@Column(name = "VF_SENT_SEG")
	private Integer vfSentSeg;
	@Column(name = "VF_SENT_SMS")
	private Integer vfSentSms;
	@Column(name = "VF_TIMED_OUT_SEG")
	private Integer vfTimedOutSeg;
	@Column(name = "VF_TIMED_OUT_SMS")
	private Integer vfTimedOutSms;
	@Column(name = "VF_FAILED_TO_SEND_SEG")
	private Integer vfFailedToSendSeg;
	@Column(name = "VF_FAILED_TO_SEND_SMS")
	private Integer vfFailedToSendSms;
	@Column(name = "VF_DELIVERED_SEG")
	private Integer vfDeliveredSeg;
	@Column(name = "VF_DELIVERED_SMS")
	private Integer vfDeliveredSms;
	@Column(name = "VF_NOT_DELIVERED_SEG")
	private Integer vfNotDeliveredSeg;
	@Column(name = "VF_NOT_DELIVERED_SMS")
	private Integer vfNotDeliveredSms;
	@Column(name = "VF_FAILED_SEG")
	private Integer vfFailedSeg;
	@Column(name = "VF_FAILED_SMS")
	private Integer vfFailedSms;
	@Column(name = "VF_RECEIVED_SEG")
	private Integer vfReceivedSeg;
	@Column(name = "VF_RECEIVED_SMS")
	private Integer vfReceivedSms;
	@Column(name = "VF_EXPIRED_SEG")
	private Integer vfExpiredSeg;
	@Column(name = "VF_EXPIRED_SMS")
	private Integer vfExpiredSms;
	@Column(name = "VF_UNKNOWN_SEG")
	private Integer vfUnknownSeg;
	@Column(name = "VF_UNKNOWN_SMS")
	private Integer vfUnknownSms;
	@Column(name = "VF_PROCESSING_SEG")
	private Integer vfProcessingSeg;
	@Column(name = "VF_PROCESSING_SMS")
	private Integer vfProcessingSms;
	@Column(name = "VF_PARTIAL_DELIVERED_SEG")
	private Integer vfPartialDeliveredSeg;
	@Column(name = "VF_PARTIAL_DELIVERED_SMS")
	private Integer vfPartialDeliveredSms;
	@Column(name = "VF_PARTIAL_RECEIVED_SEG")
	private Integer vfPartialReceivedSeg;
	@Column(name = "VF_PARTIAL_RECEIVED_SMS")
	private Integer vfPartialReceivedSms;

	@Column(name = "ET_NEW_SEG")
	private Integer etNewSeg;
	@Column(name = "ET_NEW_SMS")
	private Integer etNewSms;
	@Column(name = "ET_SUBMITTED_SEG")
	private Integer etSubmittedSeg;
	@Column(name = "ET_SUBMITTED_SMS")
	private Integer etSubmittedSms;
	@Column(name = "ET_SENT_SEG")
	private Integer etSentSeg;
	@Column(name = "ET_SENT_SMS")
	private Integer etSentSms;
	@Column(name = "ET_TIMED_OUT_SEG")
	private Integer etTimedOutSeg;
	@Column(name = "ET_TIMED_OUT_SMS")
	private Integer etTimedOutSms;
	@Column(name = "ET_FAILED_TO_SEND_SEG")
	private Integer etFailedToSendSeg;
	@Column(name = "ET_FAILED_TO_SEND_SMS")
	private Integer etFailedToSendSms;
	@Column(name = "ET_DELIVERED_SEG")
	private Integer etDeliveredSeg;
	@Column(name = "ET_DELIVERED_SMS")
	private Integer etDeliveredSms;
	@Column(name = "ET_NOT_DELIVERED_SEG")
	private Integer etNotDeliveredSeg;
	@Column(name = "ET_NOT_DELIVERED_SMS")
	private Integer etNotDeliveredSms;
	@Column(name = "ET_FAILED_SEG")
	private Integer etFailedSeg;
	@Column(name = "ET_FAILED_SMS")
	private Integer etFailedSms;
	@Column(name = "ET_RECEIVED_SEG")
	private Integer etReceivedSeg;
	@Column(name = "ET_RECEIVED_SMS")
	private Integer etReceivedSms;
	@Column(name = "ET_EXPIRED_SEG")
	private Integer etExpiredSeg;
	@Column(name = "ET_EXPIRED_SMS")
	private Integer etExpiredSms;
	@Column(name = "ET_UNKNOWN_SEG")
	private Integer etUnknownSeg;
	@Column(name = "ET_UNKNOWN_SMS")
	private Integer etUnknownSms;
	@Column(name = "ET_PROCESSING_SEG")
	private Integer etProcessingSeg;
	@Column(name = "ET_PROCESSING_SMS")
	private Integer etProcessingSms;
	@Column(name = "ET_PARTIAL_DELIVERED_SEG")
	private Integer etPartialDeliveredSeg;
	@Column(name = "ET_PARTIAL_DELIVERED_SMS")
	private Integer etPartialDeliveredSms;
	@Column(name = "ET_PARTIAL_RECEIVED_SEG")
	private Integer etPartialReceivedSeg;
	@Column(name = "ET_PARTIAL_RECEIVED_SMS")
	private Integer etPartialReceivedSms;

	@Column(name = "OG_NEW_SEG")
	private Integer ogNewSeg;
	@Column(name = "OG_NEW_SMS")
	private Integer ogNewSms;
	@Column(name = "OG_SUBMITTED_SEG")
	private Integer ogSubmittedSeg;
	@Column(name = "OG_SUBMITTED_SMS")
	private Integer ogSubmittedSms;
	@Column(name = "OG_SENT_SEG")
	private Integer ogSentSeg;
	@Column(name = "OG_SENT_SMS")
	private Integer ogSentSms;
	@Column(name = "OG_TIMED_OUT_SEG")
	private Integer ogTimedOutSeg;
	@Column(name = "OG_TIMED_OUT_SMS")
	private Integer ogTimedOutSms;
	@Column(name = "OG_FAILED_TO_SEND_SEG")
	private Integer ogFailedToSendSeg;
	@Column(name = "OG_FAILED_TO_SEND_SMS")
	private Integer ogFailedToSendSms;
	@Column(name = "OG_DELIVERED_SEG")
	private Integer ogDeliveredSeg;
	@Column(name = "OG_DELIVERED_SMS")
	private Integer ogDeliveredSms;
	@Column(name = "OG_NOT_DELIVERED_SEG")
	private Integer ogNotDeliveredSeg;
	@Column(name = "OG_NOT_DELIVERED_SMS")
	private Integer ogNotDeliveredSms;
	@Column(name = "OG_FAILED_SEG")
	private Integer ogFailedSeg;
	@Column(name = "OG_FAILED_SMS")
	private Integer ogFailedSms;
	@Column(name = "OG_RECEIVED_SEG")
	private Integer ogReceivedSeg;
	@Column(name = "OG_RECEIVED_SMS")
	private Integer ogReceivedSms;
	@Column(name = "OG_EXPIRED_SEG")
	private Integer ogExpiredSeg;
	@Column(name = "OG_EXPIRED_SMS")
	private Integer ogExpiredSms;
	@Column(name = "OG_UNKNOWN_SEG")
	private Integer ogUnknownSeg;
	@Column(name = "OG_UNKNOWN_SMS")
	private Integer ogUnknownSms;
	@Column(name = "OG_PROCESSING_SEG")
	private Integer ogProcessingSeg;
	@Column(name = "OG_PROCESSING_SMS")
	private Integer ogProcessingSms;
	@Column(name = "OG_PARTIAL_DELIVERED_SEG")
	private Integer ogPartialDeliveredSeg;
	@Column(name = "OG_PARTIAL_DELIVERED_SMS")
	private Integer ogPartialDeliveredSms;
	@Column(name = "OG_PARTIAL_RECEIVED_SEG")
	private Integer ogPartialReceivedSeg;
	@Column(name = "OG_PARTIAL_RECEIVED_SMS")
	private Integer ogPartialReceivedSms;

	@Column(name = "WE_NEW_SEG")
	private Integer weNewSeg;
	@Column(name = "WE_NEW_SMS")
	private Integer weNewSms;
	@Column(name = "WE_SUBMITTED_SEG")
	private Integer weSubmittedSeg;
	@Column(name = "WE_SUBMITTED_SMS")
	private Integer weSubmittedSms;
	@Column(name = "WE_SENT_SEG")
	private Integer weSentSeg;
	@Column(name = "WE_SENT_SMS")
	private Integer weSentSms;
	@Column(name = "WE_TIMED_OUT_SEG")
	private Integer weTimedOutSeg;
	@Column(name = "WE_TIMED_OUT_SMS")
	private Integer weTimedOutSms;
	@Column(name = "WE_FAILED_TO_SEND_SEG")
	private Integer weFailedToSendSeg;
	@Column(name = "WE_FAILED_TO_SEND_SMS")
	private Integer weFailedToSendSms;
	@Column(name = "WE_DELIVERED_SEG")
	private Integer weDeliveredSeg;
	@Column(name = "WE_DELIVERED_SMS")
	private Integer weDeliveredSms;
	@Column(name = "WE_NOT_DELIVERED_SEG")
	private Integer weNotDeliveredSeg;
	@Column(name = "WE_NOT_DELIVERED_SMS")
	private Integer weNotDeliveredSms;
	@Column(name = "WE_FAILED_SEG")
	private Integer weFailedSeg;
	@Column(name = "WE_FAILED_SMS")
	private Integer weFailedSms;
	@Column(name = "WE_RECEIVED_SEG")
	private Integer weReceivedSeg;
	@Column(name = "WE_RECEIVED_SMS")
	private Integer weReceivedSms;
	@Column(name = "WE_EXPIRED_SEG")
	private Integer weExpiredSeg;
	@Column(name = "WE_EXPIRED_SMS")
	private Integer weExpiredSms;
	@Column(name = "WE_UNKNOWN_SEG")
	private Integer weUnknownSeg;
	@Column(name = "WE_UNKNOWN_SMS")
	private Integer weUnknownSms;
	@Column(name = "WE_PROCESSING_SEG")
	private Integer weProcessingSeg;
	@Column(name = "WE_PROCESSING_SMS")
	private Integer weProcessingSms;
	@Column(name = "WE_PARTIAL_DELIVERED_SEG")
	private Integer wePartialDeliveredSeg;
	@Column(name = "WE_PARTIAL_DELIVERED_SMS")
	private Integer wePartialDeliveredSms;
	@Column(name = "WE_PARTIAL_RECEIVED_SEG")
	private Integer wePartialReceivedSeg;
	@Column(name = "WE_PARTIAL_RECEIVED_SMS")
	private Integer wePartialReceivedSms;

	@Column(name = "INTER_NEW_SEG")
	private Integer interNewSeg;
	@Column(name = "INTER_NEW_SMS")
	private Integer interNewSms;
	@Column(name = "INTER_SUBMITTED_SEG")
	private Integer interSubmittedSeg;
	@Column(name = "INTER_SUBMITTED_SMS")
	private Integer interSubmittedSms;
	@Column(name = "INTER_SENT_SEG")
	private Integer interSentSeg;
	@Column(name = "INTER_SENT_SMS")
	private Integer interSentSms;
	@Column(name = "INTER_TIMED_OUT_SEG")
	private Integer interTimedOutSeg;
	@Column(name = "INTER_TIMED_OUT_SMS")
	private Integer interTimedOutSms;
	@Column(name = "INTER_FAILED_TO_SEND_SEG")
	private Integer interFailedToSendSeg;
	@Column(name = "INTER_FAILED_TO_SEND_SMS")
	private Integer interFailedToSendSms;
	@Column(name = "INTER_DELIVERED_SEG")
	private Integer interDeliveredSeg;
	@Column(name = "INTER_DELIVERED_SMS")
	private Integer interDeliveredSms;
	@Column(name = "INTER_NOT_DELIVERED_SEG")
	private Integer interNotDeliveredSeg;
	@Column(name = "INTER_NOT_DELIVERED_SMS")
	private Integer interNotDeliveredSms;
	@Column(name = "INTER_FAILED_SEG")
	private Integer interFailedSeg;
	@Column(name = "INTER_FAILED_SMS")
	private Integer interFailedSms;
	@Column(name = "INTER_RECEIVED_SEG")
	private Integer interReceivedSeg;
	@Column(name = "INTER_RECEIVED_SMS")
	private Integer interReceivedSms;
	@Column(name = "INTER_EXPIRED_SEG")
	private Integer interExpiredSeg;
	@Column(name = "INTER_EXPIRED_SMS")
	private Integer interExpiredSms;
	@Column(name = "INTER_UNKNOWN_SEG")
	private Integer interUnknownSeg;
	@Column(name = "INTER_UNKNOWN_SMS")
	private Integer interUnknownSms;
	@Column(name = "INTER_PROCESSING_SEG")
	private Integer interProcessingSeg;
	@Column(name = "INTER_PROCESSING_SMS")
	private Integer interProcessingSms;
	@Column(name = "INTER_PARTIAL_DELIVERED_SEG")
	private Integer interPartialDeliveredSeg;
	@Column(name = "INTER_PARTIAL_DELIVERED_SMS")
	private Integer interPartialDeliveredSms;
	@Column(name = "INTER_PARTIAL_RECEIVED_SEG")
	private Integer interPartialReceivedSeg;
	@Column(name = "INTER_PARTIAL_RECEIVED_SMS")
	private Integer interPartialReceivedSms;

	public SmsApiHourlySmsStats() {}

	public SmsApiHourlySmsStatsPK getSmsApiHourlySmsStatsPK() {
		return smsApiHourlySmsStatsPK;
	}

	public void setSmsApiHourlySmsStatsPK(SmsApiHourlySmsStatsPK smsApiHourlySmsStatsPK) {
		this.smsApiHourlySmsStatsPK = smsApiHourlySmsStatsPK;
	}

	public Integer getVfNewSeg() {
		return vfNewSeg;
	}

	public void setVfNewSeg(Integer vfNewSeg) {
		this.vfNewSeg = vfNewSeg;
	}

	public Integer getVfNewSms() {
		return vfNewSms;
	}

	public void setVfNewSms(Integer vfNewSms) {
		this.vfNewSms = vfNewSms;
	}

	public Integer getVfSubmittedSeg() {
		return vfSubmittedSeg;
	}

	public void setVfSubmittedSeg(Integer vfSubmittedSeg) {
		this.vfSubmittedSeg = vfSubmittedSeg;
	}

	public Integer getVfSubmittedSms() {
		return vfSubmittedSms;
	}

	public void setVfSubmittedSms(Integer vfSubmittedSms) {
		this.vfSubmittedSms = vfSubmittedSms;
	}

	public Integer getVfSentSeg() {
		return vfSentSeg;
	}

	public void setVfSentSeg(Integer vfSentSeg) {
		this.vfSentSeg = vfSentSeg;
	}

	public Integer getVfSentSms() {
		return vfSentSms;
	}

	public void setVfSentSms(Integer vfSentSms) {
		this.vfSentSms = vfSentSms;
	}

	public Integer getVfTimedOutSeg() {
		return vfTimedOutSeg;
	}

	public void setVfTimedOutSeg(Integer vfTimedOutSeg) {
		this.vfTimedOutSeg = vfTimedOutSeg;
	}

	public Integer getVfTimedOutSms() {
		return vfTimedOutSms;
	}

	public void setVfTimedOutSms(Integer vfTimedOutSms) {
		this.vfTimedOutSms = vfTimedOutSms;
	}

	public Integer getVfFailedToSendSeg() {
		return vfFailedToSendSeg;
	}

	public void setVfFailedToSendSeg(Integer vfFailedToSendSeg) {
		this.vfFailedToSendSeg = vfFailedToSendSeg;
	}

	public Integer getVfFailedToSendSms() {
		return vfFailedToSendSms;
	}

	public void setVfFailedToSendSms(Integer vfFailedToSendSms) {
		this.vfFailedToSendSms = vfFailedToSendSms;
	}

	public Integer getVfDeliveredSeg() {
		return vfDeliveredSeg;
	}

	public void setVfDeliveredSeg(Integer vfDeliveredSeg) {
		this.vfDeliveredSeg = vfDeliveredSeg;
	}

	public Integer getVfDeliveredSms() {
		return vfDeliveredSms;
	}

	public void setVfDeliveredSms(Integer vfDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
	}

	public Integer getVfNotDeliveredSeg() {
		return vfNotDeliveredSeg;
	}

	public void setVfNotDeliveredSeg(Integer vfNotDeliveredSeg) {
		this.vfNotDeliveredSeg = vfNotDeliveredSeg;
	}

	public Integer getVfNotDeliveredSms() {
		return vfNotDeliveredSms;
	}

	public void setVfNotDeliveredSms(Integer vfNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
	}

	public Integer getVfFailedSeg() {
		return vfFailedSeg;
	}

	public void setVfFailedSeg(Integer vfFailedSeg) {
		this.vfFailedSeg = vfFailedSeg;
	}

	public Integer getVfFailedSms() {
		return vfFailedSms;
	}

	public void setVfFailedSms(Integer vfFailedSms) {
		this.vfFailedSms = vfFailedSms;
	}

	public Integer getVfReceivedSeg() {
		return vfReceivedSeg;
	}

	public void setVfReceivedSeg(Integer vfReceivedSeg) {
		this.vfReceivedSeg = vfReceivedSeg;
	}

	public Integer getVfReceivedSms() {
		return vfReceivedSms;
	}

	public void setVfReceivedSms(Integer vfReceivedSms) {
		this.vfReceivedSms = vfReceivedSms;
	}

	public Integer getVfExpiredSeg() {
		return vfExpiredSeg;
	}

	public void setVfExpiredSeg(Integer vfExpiredSeg) {
		this.vfExpiredSeg = vfExpiredSeg;
	}

	public Integer getVfExpiredSms() {
		return vfExpiredSms;
	}

	public void setVfExpiredSms(Integer vfExpiredSms) {
		this.vfExpiredSms = vfExpiredSms;
	}

	public Integer getVfUnknownSeg() {
		return vfUnknownSeg;
	}

	public void setVfUnknownSeg(Integer vfUnknownSeg) {
		this.vfUnknownSeg = vfUnknownSeg;
	}

	public Integer getVfUnknownSms() {
		return vfUnknownSms;
	}

	public void setVfUnknownSms(Integer vfUnknownSms) {
		this.vfUnknownSms = vfUnknownSms;
	}

	public Integer getVfProcessingSeg() {
		return vfProcessingSeg;
	}

	public void setVfProcessingSeg(Integer vfProcessingSeg) {
		this.vfProcessingSeg = vfProcessingSeg;
	}

	public Integer getVfProcessingSms() {
		return vfProcessingSms;
	}

	public void setVfProcessingSms(Integer vfProcessingSms) {
		this.vfProcessingSms = vfProcessingSms;
	}

	public Integer getVfPartialDeliveredSeg() {
		return vfPartialDeliveredSeg;
	}

	public void setVfPartialDeliveredSeg(Integer vfPartialDeliveredSeg) {
		this.vfPartialDeliveredSeg = vfPartialDeliveredSeg;
	}

	public Integer getVfPartialDeliveredSms() {
		return vfPartialDeliveredSms;
	}

	public void setVfPartialDeliveredSms(Integer vfPartialDeliveredSms) {
		this.vfPartialDeliveredSms = vfPartialDeliveredSms;
	}

	public Integer getVfPartialReceivedSeg() {
		return vfPartialReceivedSeg;
	}

	public void setVfPartialReceivedSeg(Integer vfPartialReceivedSeg) {
		this.vfPartialReceivedSeg = vfPartialReceivedSeg;
	}

	public Integer getVfPartialReceivedSms() {
		return vfPartialReceivedSms;
	}

	public void setVfPartialReceivedSms(Integer vfPartialReceivedSms) {
		this.vfPartialReceivedSms = vfPartialReceivedSms;
	}

	public Integer getEtNewSeg() {
		return etNewSeg;
	}

	public void setEtNewSeg(Integer etNewSeg) {
		this.etNewSeg = etNewSeg;
	}

	public Integer getEtNewSms() {
		return etNewSms;
	}

	public void setEtNewSms(Integer etNewSms) {
		this.etNewSms = etNewSms;
	}

	public Integer getEtSubmittedSeg() {
		return etSubmittedSeg;
	}

	public void setEtSubmittedSeg(Integer etSubmittedSeg) {
		this.etSubmittedSeg = etSubmittedSeg;
	}

	public Integer getEtSubmittedSms() {
		return etSubmittedSms;
	}

	public void setEtSubmittedSms(Integer etSubmittedSms) {
		this.etSubmittedSms = etSubmittedSms;
	}

	public Integer getEtSentSeg() {
		return etSentSeg;
	}

	public void setEtSentSeg(Integer etSentSeg) {
		this.etSentSeg = etSentSeg;
	}

	public Integer getEtSentSms() {
		return etSentSms;
	}

	public void setEtSentSms(Integer etSentSms) {
		this.etSentSms = etSentSms;
	}

	public Integer getEtTimedOutSeg() {
		return etTimedOutSeg;
	}

	public void setEtTimedOutSeg(Integer etTimedOutSeg) {
		this.etTimedOutSeg = etTimedOutSeg;
	}

	public Integer getEtTimedOutSms() {
		return etTimedOutSms;
	}

	public void setEtTimedOutSms(Integer etTimedOutSms) {
		this.etTimedOutSms = etTimedOutSms;
	}

	public Integer getEtFailedToSendSeg() {
		return etFailedToSendSeg;
	}

	public void setEtFailedToSendSeg(Integer etFailedToSendSeg) {
		this.etFailedToSendSeg = etFailedToSendSeg;
	}

	public Integer getEtFailedToSendSms() {
		return etFailedToSendSms;
	}

	public void setEtFailedToSendSms(Integer etFailedToSendSms) {
		this.etFailedToSendSms = etFailedToSendSms;
	}

	public Integer getEtDeliveredSeg() {
		return etDeliveredSeg;
	}

	public void setEtDeliveredSeg(Integer etDeliveredSeg) {
		this.etDeliveredSeg = etDeliveredSeg;
	}

	public Integer getEtDeliveredSms() {
		return etDeliveredSms;
	}

	public void setEtDeliveredSms(Integer etDeliveredSms) {
		this.etDeliveredSms = etDeliveredSms;
	}

	public Integer getEtNotDeliveredSeg() {
		return etNotDeliveredSeg;
	}

	public void setEtNotDeliveredSeg(Integer etNotDeliveredSeg) {
		this.etNotDeliveredSeg = etNotDeliveredSeg;
	}

	public Integer getEtNotDeliveredSms() {
		return etNotDeliveredSms;
	}

	public void setEtNotDeliveredSms(Integer etNotDeliveredSms) {
		this.etNotDeliveredSms = etNotDeliveredSms;
	}

	public Integer getEtFailedSeg() {
		return etFailedSeg;
	}

	public void setEtFailedSeg(Integer etFailedSeg) {
		this.etFailedSeg = etFailedSeg;
	}

	public Integer getEtFailedSms() {
		return etFailedSms;
	}

	public void setEtFailedSms(Integer etFailedSms) {
		this.etFailedSms = etFailedSms;
	}

	public Integer getEtReceivedSeg() {
		return etReceivedSeg;
	}

	public void setEtReceivedSeg(Integer etReceivedSeg) {
		this.etReceivedSeg = etReceivedSeg;
	}

	public Integer getEtReceivedSms() {
		return etReceivedSms;
	}

	public void setEtReceivedSms(Integer etReceivedSms) {
		this.etReceivedSms = etReceivedSms;
	}

	public Integer getEtExpiredSeg() {
		return etExpiredSeg;
	}

	public void setEtExpiredSeg(Integer etExpiredSeg) {
		this.etExpiredSeg = etExpiredSeg;
	}

	public Integer getEtExpiredSms() {
		return etExpiredSms;
	}

	public void setEtExpiredSms(Integer etExpiredSms) {
		this.etExpiredSms = etExpiredSms;
	}

	public Integer getEtUnknownSeg() {
		return etUnknownSeg;
	}

	public void setEtUnknownSeg(Integer etUnknownSeg) {
		this.etUnknownSeg = etUnknownSeg;
	}

	public Integer getEtUnknownSms() {
		return etUnknownSms;
	}

	public void setEtUnknownSms(Integer etUnknownSms) {
		this.etUnknownSms = etUnknownSms;
	}

	public Integer getEtProcessingSeg() {
		return etProcessingSeg;
	}

	public void setEtProcessingSeg(Integer etProcessingSeg) {
		this.etProcessingSeg = etProcessingSeg;
	}

	public Integer getEtProcessingSms() {
		return etProcessingSms;
	}

	public void setEtProcessingSms(Integer etProcessingSms) {
		this.etProcessingSms = etProcessingSms;
	}

	public Integer getEtPartialDeliveredSeg() {
		return etPartialDeliveredSeg;
	}

	public void setEtPartialDeliveredSeg(Integer etPartialDeliveredSeg) {
		this.etPartialDeliveredSeg = etPartialDeliveredSeg;
	}

	public Integer getEtPartialDeliveredSms() {
		return etPartialDeliveredSms;
	}

	public void setEtPartialDeliveredSms(Integer etPartialDeliveredSms) {
		this.etPartialDeliveredSms = etPartialDeliveredSms;
	}

	public Integer getEtPartialReceivedSeg() {
		return etPartialReceivedSeg;
	}

	public void setEtPartialReceivedSeg(Integer etPartialReceivedSeg) {
		this.etPartialReceivedSeg = etPartialReceivedSeg;
	}

	public Integer getEtPartialReceivedSms() {
		return etPartialReceivedSms;
	}

	public void setEtPartialReceivedSms(Integer etPartialReceivedSms) {
		this.etPartialReceivedSms = etPartialReceivedSms;
	}

	public Integer getOgNewSeg() {
		return ogNewSeg;
	}

	public void setOgNewSeg(Integer ogNewSeg) {
		this.ogNewSeg = ogNewSeg;
	}

	public Integer getOgNewSms() {
		return ogNewSms;
	}

	public void setOgNewSms(Integer ogNewSms) {
		this.ogNewSms = ogNewSms;
	}

	public Integer getOgSubmittedSeg() {
		return ogSubmittedSeg;
	}

	public void setOgSubmittedSeg(Integer ogSubmittedSeg) {
		this.ogSubmittedSeg = ogSubmittedSeg;
	}

	public Integer getOgSubmittedSms() {
		return ogSubmittedSms;
	}

	public void setOgSubmittedSms(Integer ogSubmittedSms) {
		this.ogSubmittedSms = ogSubmittedSms;
	}

	public Integer getOgSentSeg() {
		return ogSentSeg;
	}

	public void setOgSentSeg(Integer ogSentSeg) {
		this.ogSentSeg = ogSentSeg;
	}

	public Integer getOgSentSms() {
		return ogSentSms;
	}

	public void setOgSentSms(Integer ogSentSms) {
		this.ogSentSms = ogSentSms;
	}

	public Integer getOgTimedOutSeg() {
		return ogTimedOutSeg;
	}

	public void setOgTimedOutSeg(Integer ogTimedOutSeg) {
		this.ogTimedOutSeg = ogTimedOutSeg;
	}

	public Integer getOgTimedOutSms() {
		return ogTimedOutSms;
	}

	public void setOgTimedOutSms(Integer ogTimedOutSms) {
		this.ogTimedOutSms = ogTimedOutSms;
	}

	public Integer getOgFailedToSendSeg() {
		return ogFailedToSendSeg;
	}

	public void setOgFailedToSendSeg(Integer ogFailedToSendSeg) {
		this.ogFailedToSendSeg = ogFailedToSendSeg;
	}

	public Integer getOgFailedToSendSms() {
		return ogFailedToSendSms;
	}

	public void setOgFailedToSendSms(Integer ogFailedToSendSms) {
		this.ogFailedToSendSms = ogFailedToSendSms;
	}

	public Integer getOgDeliveredSeg() {
		return ogDeliveredSeg;
	}

	public void setOgDeliveredSeg(Integer ogDeliveredSeg) {
		this.ogDeliveredSeg = ogDeliveredSeg;
	}

	public Integer getOgDeliveredSms() {
		return ogDeliveredSms;
	}

	public void setOgDeliveredSms(Integer ogDeliveredSms) {
		this.ogDeliveredSms = ogDeliveredSms;
	}

	public Integer getOgNotDeliveredSeg() {
		return ogNotDeliveredSeg;
	}

	public void setOgNotDeliveredSeg(Integer ogNotDeliveredSeg) {
		this.ogNotDeliveredSeg = ogNotDeliveredSeg;
	}

	public Integer getOgNotDeliveredSms() {
		return ogNotDeliveredSms;
	}

	public void setOgNotDeliveredSms(Integer ogNotDeliveredSms) {
		this.ogNotDeliveredSms = ogNotDeliveredSms;
	}

	public Integer getOgFailedSeg() {
		return ogFailedSeg;
	}

	public void setOgFailedSeg(Integer ogFailedSeg) {
		this.ogFailedSeg = ogFailedSeg;
	}

	public Integer getOgFailedSms() {
		return ogFailedSms;
	}

	public void setOgFailedSms(Integer ogFailedSms) {
		this.ogFailedSms = ogFailedSms;
	}

	public Integer getOgReceivedSeg() {
		return ogReceivedSeg;
	}

	public void setOgReceivedSeg(Integer ogReceivedSeg) {
		this.ogReceivedSeg = ogReceivedSeg;
	}

	public Integer getOgReceivedSms() {
		return ogReceivedSms;
	}

	public void setOgReceivedSms(Integer ogReceivedSms) {
		this.ogReceivedSms = ogReceivedSms;
	}

	public Integer getOgExpiredSeg() {
		return ogExpiredSeg;
	}

	public void setOgExpiredSeg(Integer ogExpiredSeg) {
		this.ogExpiredSeg = ogExpiredSeg;
	}

	public Integer getOgExpiredSms() {
		return ogExpiredSms;
	}

	public void setOgExpiredSms(Integer ogExpiredSms) {
		this.ogExpiredSms = ogExpiredSms;
	}

	public Integer getOgUnknownSeg() {
		return ogUnknownSeg;
	}

	public void setOgUnknownSeg(Integer ogUnknownSeg) {
		this.ogUnknownSeg = ogUnknownSeg;
	}

	public Integer getOgUnknownSms() {
		return ogUnknownSms;
	}

	public void setOgUnknownSms(Integer ogUnknownSms) {
		this.ogUnknownSms = ogUnknownSms;
	}

	public Integer getOgProcessingSeg() {
		return ogProcessingSeg;
	}

	public void setOgProcessingSeg(Integer ogProcessingSeg) {
		this.ogProcessingSeg = ogProcessingSeg;
	}

	public Integer getOgProcessingSms() {
		return ogProcessingSms;
	}

	public void setOgProcessingSms(Integer ogProcessingSms) {
		this.ogProcessingSms = ogProcessingSms;
	}

	public Integer getOgPartialDeliveredSeg() {
		return ogPartialDeliveredSeg;
	}

	public void setOgPartialDeliveredSeg(Integer ogPartialDeliveredSeg) {
		this.ogPartialDeliveredSeg = ogPartialDeliveredSeg;
	}

	public Integer getOgPartialDeliveredSms() {
		return ogPartialDeliveredSms;
	}

	public void setOgPartialDeliveredSms(Integer ogPartialDeliveredSms) {
		this.ogPartialDeliveredSms = ogPartialDeliveredSms;
	}

	public Integer getOgPartialReceivedSeg() {
		return ogPartialReceivedSeg;
	}

	public void setOgPartialReceivedSeg(Integer ogPartialReceivedSeg) {
		this.ogPartialReceivedSeg = ogPartialReceivedSeg;
	}

	public Integer getOgPartialReceivedSms() {
		return ogPartialReceivedSms;
	}

	public void setOgPartialReceivedSms(Integer ogPartialReceivedSms) {
		this.ogPartialReceivedSms = ogPartialReceivedSms;
	}

	public Integer getWeNewSeg() {
		return weNewSeg;
	}

	public void setWeNewSeg(Integer weNewSeg) {
		this.weNewSeg = weNewSeg;
	}

	public Integer getWeNewSms() {
		return weNewSms;
	}

	public void setWeNewSms(Integer weNewSms) {
		this.weNewSms = weNewSms;
	}

	public Integer getWeSubmittedSeg() {
		return weSubmittedSeg;
	}

	public void setWeSubmittedSeg(Integer weSubmittedSeg) {
		this.weSubmittedSeg = weSubmittedSeg;
	}

	public Integer getWeSubmittedSms() {
		return weSubmittedSms;
	}

	public void setWeSubmittedSms(Integer weSubmittedSms) {
		this.weSubmittedSms = weSubmittedSms;
	}

	public Integer getWeSentSeg() {
		return weSentSeg;
	}

	public void setWeSentSeg(Integer weSentSeg) {
		this.weSentSeg = weSentSeg;
	}

	public Integer getWeSentSms() {
		return weSentSms;
	}

	public void setWeSentSms(Integer weSentSms) {
		this.weSentSms = weSentSms;
	}

	public Integer getWeTimedOutSeg() {
		return weTimedOutSeg;
	}

	public void setWeTimedOutSeg(Integer weTimedOutSeg) {
		this.weTimedOutSeg = weTimedOutSeg;
	}

	public Integer getWeTimedOutSms() {
		return weTimedOutSms;
	}

	public void setWeTimedOutSms(Integer weTimedOutSms) {
		this.weTimedOutSms = weTimedOutSms;
	}

	public Integer getWeFailedToSendSeg() {
		return weFailedToSendSeg;
	}

	public void setWeFailedToSendSeg(Integer weFailedToSendSeg) {
		this.weFailedToSendSeg = weFailedToSendSeg;
	}

	public Integer getWeFailedToSendSms() {
		return weFailedToSendSms;
	}

	public void setWeFailedToSendSms(Integer weFailedToSendSms) {
		this.weFailedToSendSms = weFailedToSendSms;
	}

	public Integer getWeDeliveredSeg() {
		return weDeliveredSeg;
	}

	public void setWeDeliveredSeg(Integer weDeliveredSeg) {
		this.weDeliveredSeg = weDeliveredSeg;
	}

	public Integer getWeDeliveredSms() {
		return weDeliveredSms;
	}

	public void setWeDeliveredSms(Integer weDeliveredSms) {
		this.weDeliveredSms = weDeliveredSms;
	}

	public Integer getWeNotDeliveredSeg() {
		return weNotDeliveredSeg;
	}

	public void setWeNotDeliveredSeg(Integer weNotDeliveredSeg) {
		this.weNotDeliveredSeg = weNotDeliveredSeg;
	}

	public Integer getWeNotDeliveredSms() {
		return weNotDeliveredSms;
	}

	public void setWeNotDeliveredSms(Integer weNotDeliveredSms) {
		this.weNotDeliveredSms = weNotDeliveredSms;
	}

	public Integer getWeFailedSeg() {
		return weFailedSeg;
	}

	public void setWeFailedSeg(Integer weFailedSeg) {
		this.weFailedSeg = weFailedSeg;
	}

	public Integer getWeFailedSms() {
		return weFailedSms;
	}

	public void setWeFailedSms(Integer weFailedSms) {
		this.weFailedSms = weFailedSms;
	}

	public Integer getWeReceivedSeg() {
		return weReceivedSeg;
	}

	public void setWeReceivedSeg(Integer weReceivedSeg) {
		this.weReceivedSeg = weReceivedSeg;
	}

	public Integer getWeReceivedSms() {
		return weReceivedSms;
	}

	public void setWeReceivedSms(Integer weReceivedSms) {
		this.weReceivedSms = weReceivedSms;
	}

	public Integer getWeExpiredSeg() {
		return weExpiredSeg;
	}

	public void setWeExpiredSeg(Integer weExpiredSeg) {
		this.weExpiredSeg = weExpiredSeg;
	}

	public Integer getWeExpiredSms() {
		return weExpiredSms;
	}

	public void setWeExpiredSms(Integer weExpiredSms) {
		this.weExpiredSms = weExpiredSms;
	}

	public Integer getWeUnknownSeg() {
		return weUnknownSeg;
	}

	public void setWeUnknownSeg(Integer weUnknownSeg) {
		this.weUnknownSeg = weUnknownSeg;
	}

	public Integer getWeUnknownSms() {
		return weUnknownSms;
	}

	public void setWeUnknownSms(Integer weUnknownSms) {
		this.weUnknownSms = weUnknownSms;
	}

	public Integer getWeProcessingSeg() {
		return weProcessingSeg;
	}

	public void setWeProcessingSeg(Integer weProcessingSeg) {
		this.weProcessingSeg = weProcessingSeg;
	}

	public Integer getWeProcessingSms() {
		return weProcessingSms;
	}

	public void setWeProcessingSms(Integer weProcessingSms) {
		this.weProcessingSms = weProcessingSms;
	}

	public Integer getWePartialDeliveredSeg() {
		return wePartialDeliveredSeg;
	}

	public void setWePartialDeliveredSeg(Integer wePartialDeliveredSeg) {
		this.wePartialDeliveredSeg = wePartialDeliveredSeg;
	}

	public Integer getWePartialDeliveredSms() {
		return wePartialDeliveredSms;
	}

	public void setWePartialDeliveredSms(Integer wePartialDeliveredSms) {
		this.wePartialDeliveredSms = wePartialDeliveredSms;
	}

	public Integer getWePartialReceivedSeg() {
		return wePartialReceivedSeg;
	}

	public void setWePartialReceivedSeg(Integer wePartialReceivedSeg) {
		this.wePartialReceivedSeg = wePartialReceivedSeg;
	}

	public Integer getWePartialReceivedSms() {
		return wePartialReceivedSms;
	}

	public void setWePartialReceivedSms(Integer wePartialReceivedSms) {
		this.wePartialReceivedSms = wePartialReceivedSms;
	}

	public Integer getInterNewSeg() {
		return interNewSeg;
	}

	public void setInterNewSeg(Integer interNewSeg) {
		this.interNewSeg = interNewSeg;
	}

	public Integer getInterNewSms() {
		return interNewSms;
	}

	public void setInterNewSms(Integer interNewSms) {
		this.interNewSms = interNewSms;
	}

	public Integer getInterSubmittedSeg() {
		return interSubmittedSeg;
	}

	public void setInterSubmittedSeg(Integer interSubmittedSeg) {
		this.interSubmittedSeg = interSubmittedSeg;
	}

	public Integer getInterSubmittedSms() {
		return interSubmittedSms;
	}

	public void setInterSubmittedSms(Integer interSubmittedSms) {
		this.interSubmittedSms = interSubmittedSms;
	}

	public Integer getInterSentSeg() {
		return interSentSeg;
	}

	public void setInterSentSeg(Integer interSentSeg) {
		this.interSentSeg = interSentSeg;
	}

	public Integer getInterSentSms() {
		return interSentSms;
	}

	public void setInterSentSms(Integer interSentSms) {
		this.interSentSms = interSentSms;
	}

	public Integer getInterTimedOutSeg() {
		return interTimedOutSeg;
	}

	public void setInterTimedOutSeg(Integer interTimedOutSeg) {
		this.interTimedOutSeg = interTimedOutSeg;
	}

	public Integer getInterTimedOutSms() {
		return interTimedOutSms;
	}

	public void setInterTimedOutSms(Integer interTimedOutSms) {
		this.interTimedOutSms = interTimedOutSms;
	}

	public Integer getInterFailedToSendSeg() {
		return interFailedToSendSeg;
	}

	public void setInterFailedToSendSeg(Integer interFailedToSendSeg) {
		this.interFailedToSendSeg = interFailedToSendSeg;
	}

	public Integer getInterFailedToSendSms() {
		return interFailedToSendSms;
	}

	public void setInterFailedToSendSms(Integer interFailedToSendSms) {
		this.interFailedToSendSms = interFailedToSendSms;
	}

	public Integer getInterDeliveredSeg() {
		return interDeliveredSeg;
	}

	public void setInterDeliveredSeg(Integer interDeliveredSeg) {
		this.interDeliveredSeg = interDeliveredSeg;
	}

	public Integer getInterDeliveredSms() {
		return interDeliveredSms;
	}

	public void setInterDeliveredSms(Integer interDeliveredSms) {
		this.interDeliveredSms = interDeliveredSms;
	}

	public Integer getInterNotDeliveredSeg() {
		return interNotDeliveredSeg;
	}

	public void setInterNotDeliveredSeg(Integer interNotDeliveredSeg) {
		this.interNotDeliveredSeg = interNotDeliveredSeg;
	}

	public Integer getInterNotDeliveredSms() {
		return interNotDeliveredSms;
	}

	public void setInterNotDeliveredSms(Integer interNotDeliveredSms) {
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public Integer getInterFailedSeg() {
		return interFailedSeg;
	}

	public void setInterFailedSeg(Integer interFailedSeg) {
		this.interFailedSeg = interFailedSeg;
	}

	public Integer getInterFailedSms() {
		return interFailedSms;
	}

	public void setInterFailedSms(Integer interFailedSms) {
		this.interFailedSms = interFailedSms;
	}

	public Integer getInterReceivedSeg() {
		return interReceivedSeg;
	}

	public void setInterReceivedSeg(Integer interReceivedSeg) {
		this.interReceivedSeg = interReceivedSeg;
	}

	public Integer getInterReceivedSms() {
		return interReceivedSms;
	}

	public void setInterReceivedSms(Integer interReceivedSms) {
		this.interReceivedSms = interReceivedSms;
	}

	public Integer getInterExpiredSeg() {
		return interExpiredSeg;
	}

	public void setInterExpiredSeg(Integer interExpiredSeg) {
		this.interExpiredSeg = interExpiredSeg;
	}

	public Integer getInterExpiredSms() {
		return interExpiredSms;
	}

	public void setInterExpiredSms(Integer interExpiredSms) {
		this.interExpiredSms = interExpiredSms;
	}

	public Integer getInterUnknownSeg() {
		return interUnknownSeg;
	}

	public void setInterUnknownSeg(Integer interUnknownSeg) {
		this.interUnknownSeg = interUnknownSeg;
	}

	public Integer getInterUnknownSms() {
		return interUnknownSms;
	}

	public void setInterUnknownSms(Integer interUnknownSms) {
		this.interUnknownSms = interUnknownSms;
	}

	public Integer getInterProcessingSeg() {
		return interProcessingSeg;
	}

	public void setInterProcessingSeg(Integer interProcessingSeg) {
		this.interProcessingSeg = interProcessingSeg;
	}

	public Integer getInterProcessingSms() {
		return interProcessingSms;
	}

	public void setInterProcessingSms(Integer interProcessingSms) {
		this.interProcessingSms = interProcessingSms;
	}

	public Integer getInterPartialDeliveredSeg() {
		return interPartialDeliveredSeg;
	}

	public void setInterPartialDeliveredSeg(Integer interPartialDeliveredSeg) {
		this.interPartialDeliveredSeg = interPartialDeliveredSeg;
	}

	public Integer getInterPartialDeliveredSms() {
		return interPartialDeliveredSms;
	}

	public void setInterPartialDeliveredSms(Integer interPartialDeliveredSms) {
		this.interPartialDeliveredSms = interPartialDeliveredSms;
	}

	public Integer getInterPartialReceivedSeg() {
		return interPartialReceivedSeg;
	}

	public void setInterPartialReceivedSeg(Integer interPartialReceivedSeg) {
		this.interPartialReceivedSeg = interPartialReceivedSeg;
	}

	public Integer getInterPartialReceivedSms() {
		return interPartialReceivedSms;
	}

	public void setInterPartialReceivedSms(Integer interPartialReceivedSms) {
		this.interPartialReceivedSms = interPartialReceivedSms;
	}

	public int getSmsTotal() {
		return this.getVfNewSms() + this.getOgNewSms() + this.getEtNewSms() + this.getWeNewSms() + this.getInterNewSms()
				+ this.getVfSubmittedSms() + this.getOgSubmittedSms() + this.getEtSubmittedSms()
				+ this.getWeSubmittedSms() + this.getInterSubmittedSms() + this.getVfFailedSms() + this.getOgFailedSms()
				+ this.getEtFailedSms() + this.getWeFailedSms() + this.getInterFailedSms() + this.getVfDeliveredSms()
				+ this.getOgDeliveredSms() + this.getEtDeliveredSms() + this.getWeDeliveredSms()
				+ this.getInterDeliveredSms() + this.getVfNotDeliveredSms() + this.getOgNotDeliveredSms()
				+ this.getEtNotDeliveredSms() + this.getWeNotDeliveredSms() + this.getInterNotDeliveredSms()
				+ this.getVfSentSms() + this.getOgSentSms() + this.getEtSentSms() + this.getWeSentSms()
				+ this.getInterSentSms() + this.getVfTimedOutSms() + this.getOgTimedOutSms() + this.getEtTimedOutSms()
				+ this.getWeTimedOutSms() + this.getInterTimedOutSms() + this.getVfFailedToSendSms()
				+ this.getOgFailedToSendSms() + this.getEtFailedToSendSms() + this.getWeFailedToSendSms()
				+ this.getInterFailedToSendSms() + this.getVfExpiredSms() + this.getOgExpiredSms()
				+ this.getEtExpiredSms() + this.getWeExpiredSms() + this.getInterExpiredSms() + this.getVfReceivedSms()
				+ this.getOgReceivedSms() + this.getEtReceivedSms() + this.getWeReceivedSms()
				+ this.getInterReceivedSms() + this.getVfUnknownSms() + this.getOgUnknownSms() + this.getEtUnknownSms()
				+ this.getWeUnknownSms() + this.getInterUnknownSms() + this.getVfProcessingSms()
				+ this.getOgProcessingSms() + this.getEtProcessingSms() + this.getWeProcessingSms()
				+ this.getInterProcessingSms() + this.getVfPartialDeliveredSms() + this.getOgPartialDeliveredSms()
				+ this.getEtPartialDeliveredSms() + this.getWePartialDeliveredSms() + this.getInterPartialDeliveredSms()
				+ this.getVfPartialReceivedSms() + this.getOgPartialReceivedSms() + this.getEtPartialReceivedSms()
				+ this.getWePartialReceivedSms() + this.getInterPartialReceivedSms();
	}

	public int getSegmentsTotal() {
		return this.getVfNewSeg() + this.getOgNewSeg() + this.getEtNewSeg() + this.getWeNewSeg() + this.getInterNewSeg()
				+ this.getVfSubmittedSeg() + this.getOgSubmittedSeg() + this.getEtSubmittedSeg()
				+ this.getWeSubmittedSeg() + this.getInterSubmittedSeg() + this.getVfFailedSeg() + this.getOgFailedSeg()
				+ this.getEtFailedSeg() + this.getWeFailedSeg() + this.getInterFailedSeg() + this.getVfDeliveredSeg()
				+ this.getOgDeliveredSeg() + this.getEtDeliveredSeg() + this.getWeDeliveredSeg()
				+ this.getInterDeliveredSeg() + this.getVfNotDeliveredSeg() + this.getOgNotDeliveredSeg()
				+ this.getEtNotDeliveredSeg() + this.getWeNotDeliveredSeg() + this.getInterNotDeliveredSeg()
				+ this.getVfSentSeg() + this.getOgSentSeg() + this.getEtSentSeg() + this.getWeSentSeg()
				+ this.getInterSentSeg() + this.getVfTimedOutSeg() + this.getOgTimedOutSeg() + this.getEtTimedOutSeg()
				+ this.getWeTimedOutSeg() + this.getInterTimedOutSeg() + this.getVfFailedToSendSeg()
				+ this.getOgFailedToSendSeg() + this.getEtFailedToSendSeg() + this.getWeFailedToSendSeg()
				+ this.getInterFailedToSendSeg() + this.getVfExpiredSeg() + this.getOgExpiredSeg()
				+ this.getEtExpiredSeg() + this.getWeExpiredSeg() + this.getInterExpiredSeg() + this.getVfReceivedSeg()
				+ this.getOgReceivedSeg() + this.getEtReceivedSeg() + this.getWeReceivedSeg()
				+ this.getInterReceivedSeg() + this.getVfUnknownSeg() + this.getOgUnknownSeg() + this.getEtUnknownSeg()
				+ this.getWeUnknownSeg() + this.getInterUnknownSeg() + this.getVfProcessingSeg()
				+ this.getOgProcessingSeg() + this.getEtProcessingSeg() + this.getWeProcessingSeg()
				+ this.getInterProcessingSeg() + this.getVfPartialDeliveredSeg() + this.getOgPartialDeliveredSeg()
				+ this.getEtPartialDeliveredSeg() + this.getWePartialDeliveredSeg() + this.getInterPartialDeliveredSeg()
				+ this.getVfPartialReceivedSeg() + this.getOgPartialReceivedSeg() + this.getEtPartialReceivedSeg()
				+ this.getWePartialReceivedSeg() + this.getInterPartialReceivedSeg();
	}

	public int getSmsNew() {
		return this.getVfNewSms() + this.getOgNewSms() + this.getEtNewSms() + this.getWeNewSms()
				+ this.getInterNewSms();
	}

	public int getSegmentsNew() {
		return this.getVfNewSeg() + this.getOgNewSeg() + this.getEtNewSeg() + this.getWeNewSeg()
				+ this.getInterNewSeg();
	}

	public int getSmsSubmitted() {
		return this.getVfSubmittedSms() + this.getOgSubmittedSms() + this.getEtSubmittedSms() + this.getWeSubmittedSms()
				+ this.getInterSubmittedSms();
	}

	public int getSegmentsSubmitted() {
		return this.getVfSubmittedSeg() + this.getOgSubmittedSeg() + this.getEtSubmittedSeg() + this.getWeSubmittedSeg()
				+ this.getInterSubmittedSeg();
	}

	public int getSmsFailed() {
		return this.getVfFailedSms() + this.getOgFailedSms() + this.getEtFailedSms() + this.getWeFailedSms()
				+ this.getInterFailedSms();
	}

	public int getSegmentsFailed() {
		return this.getVfFailedSeg() + this.getOgFailedSeg() + this.getEtFailedSeg() + this.getWeFailedSeg()
				+ this.getInterFailedSeg();
	}

	public int getSmsDelivered() {
		return this.getVfDeliveredSms() + this.getOgDeliveredSms() + this.getEtDeliveredSms() + this.getWeDeliveredSms()
				+ this.getInterDeliveredSms();
	}

	public int getSegmentsDelivered() {
		return this.getVfDeliveredSeg() + this.getOgDeliveredSeg() + this.getEtDeliveredSeg() + this.getWeDeliveredSeg()
				+ this.getInterDeliveredSeg();
	}

	public int getSmsNotDelivered() {
		return this.getVfNotDeliveredSms() + this.getOgNotDeliveredSms() + this.getEtNotDeliveredSms()
				+ this.getWeNotDeliveredSms() + this.getInterNotDeliveredSms();
	}

	public int getSegmentsNotDelivered() {
		return this.getVfNotDeliveredSeg() + this.getOgNotDeliveredSeg() + this.getEtNotDeliveredSeg()
				+ this.getWeNotDeliveredSeg() + this.getInterNotDeliveredSeg();
	}

	public int getSmsSent() {
		return this.getVfSentSms() + this.getOgSentSms() + this.getEtSentSms() + this.getWeSentSms()
				+ this.getInterSentSms();
	}

	public int getSegmentsSent() {
		return this.getVfSentSeg() + this.getOgSentSeg() + this.getEtSentSeg() + this.getWeSentSeg()
				+ this.getInterSentSeg();
	}

	public int getSmsTimedOut() {
		return this.getVfTimedOutSms() + this.getOgTimedOutSms() + this.getEtTimedOutSms() + this.getWeTimedOutSms()
				+ this.getInterTimedOutSms();
	}

	public int getSegmentsTimedOut() {
		return this.getVfTimedOutSeg() + this.getOgTimedOutSeg() + this.getEtTimedOutSeg() + this.getWeTimedOutSeg()
				+ this.getInterTimedOutSeg();
	}

	public int getSmsFailedToSend() {
		return this.getVfFailedToSendSms() + this.getOgFailedToSendSms() + this.getEtFailedToSendSms()
				+ this.getWeFailedToSendSms() + this.getInterFailedToSendSms();
	}

	public int getSegmentsFailedToSend() {
		return this.getVfFailedToSendSeg() + this.getOgFailedToSendSeg() + this.getEtFailedToSendSeg()
				+ this.getWeFailedToSendSeg() + this.getInterFailedToSendSeg();
	}

	public int getSmsExpired() {
		return this.getVfExpiredSms() + this.getOgExpiredSms() + this.getEtExpiredSms() + this.getWeExpiredSms()
				+ this.getInterExpiredSms();
	}

	public int getSegmentsExpired() {
		return this.getVfExpiredSeg() + this.getOgExpiredSeg() + this.getEtExpiredSeg() + this.getWeExpiredSeg()
				+ this.getInterExpiredSeg();
	}

	public int getSmsReceived() {
		return this.getVfReceivedSms() + this.getOgReceivedSms() + this.getEtReceivedSms() + this.getWeReceivedSms()
				+ this.getInterReceivedSms();
	}

	public int getSegmentsReceived() {
		return this.getVfReceivedSeg() + this.getOgReceivedSeg() + this.getEtReceivedSeg() + this.getWeReceivedSeg()
				+ this.getInterReceivedSeg();
	}

	public int getSmsUnknown() {
		return this.getVfUnknownSms() + this.getOgUnknownSms() + this.getEtUnknownSms() + this.getWeUnknownSms()
				+ this.getInterUnknownSms();
	}

	public int getSegmentsUnknown() {
		return this.getVfUnknownSeg() + this.getOgUnknownSeg() + this.getEtUnknownSeg() + this.getWeUnknownSeg()
				+ this.getInterUnknownSeg();
	}

	public int getSmsProcessing() {
		return this.getVfProcessingSms() + this.getOgProcessingSms() + this.getEtProcessingSms()
				+ this.getWeProcessingSms() + this.getInterProcessingSms();
	}

	public int getSegmentsProcessing() {
		return this.getVfProcessingSeg() + this.getOgProcessingSeg() + this.getEtProcessingSeg()
				+ this.getWeProcessingSeg() + this.getInterProcessingSeg();
	}

	public int getSmsPartialDelivered() {
		return this.getVfPartialDeliveredSms() + this.getOgPartialDeliveredSms() + this.getEtPartialDeliveredSms()
				+ this.getWePartialDeliveredSms() + this.getInterPartialDeliveredSms();
	}

	public int getSegmentsPartialDelivered() {
		return this.getVfPartialDeliveredSeg() + this.getOgPartialDeliveredSeg() + this.getEtPartialDeliveredSeg()
				+ this.getWePartialDeliveredSeg() + this.getInterPartialDeliveredSeg();
	}

	public int getSmsPartialReceived() {
		return this.getVfPartialReceivedSms() + this.getOgPartialReceivedSms() + this.getEtPartialReceivedSms()
				+ this.getWePartialReceivedSms() + this.getInterPartialReceivedSms();
	}

	public int getSegmentsPartialReceived() {
		return this.getVfPartialReceivedSeg() + this.getOgPartialReceivedSeg() + this.getEtPartialReceivedSeg()
				+ this.getWePartialReceivedSeg() + this.getInterPartialReceivedSeg();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((smsApiHourlySmsStatsPK == null) ? 0 : smsApiHourlySmsStatsPK.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SmsApiHourlySmsStats other = (SmsApiHourlySmsStats) obj;
		if (smsApiHourlySmsStatsPK == null) {
			if (other.smsApiHourlySmsStatsPK != null)
				return false;
		} else if (!smsApiHourlySmsStatsPK.equals(other.smsApiHourlySmsStatsPK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{smsApiHourlySmsStatsPK=").append(smsApiHourlySmsStatsPK).append(", vfNewSeg=").append(vfNewSeg)
				.append(", vfNewSms=").append(vfNewSms).append(", vfSubmittedSeg=").append(vfSubmittedSeg)
				.append(", vfSubmittedSms=").append(vfSubmittedSms).append(", vfSentSeg=").append(vfSentSeg)
				.append(", vfSentSms=").append(vfSentSms).append(", vfTimedOutSeg=").append(vfTimedOutSeg)
				.append(", vfTimedOutSms=").append(vfTimedOutSms).append(", vfFailedToSendSeg=")
				.append(vfFailedToSendSeg).append(", vfFailedToSendSms=").append(vfFailedToSendSms)
				.append(", vfDeliveredSeg=").append(vfDeliveredSeg).append(", vfDeliveredSms=").append(vfDeliveredSms)
				.append(", vfNotDeliveredSeg=").append(vfNotDeliveredSeg).append(", vfNotDeliveredSms=")
				.append(vfNotDeliveredSms).append(", vfFailedSeg=").append(vfFailedSeg).append(", vfFailedSms=")
				.append(vfFailedSms).append(", vfReceivedSeg=").append(vfReceivedSeg).append(", vfReceivedSms=")
				.append(vfReceivedSms).append(", vfExpiredSeg=").append(vfExpiredSeg).append(", vfExpiredSms=")
				.append(vfExpiredSms).append(", vfUnknownSeg=").append(vfUnknownSeg).append(", vfUnknownSms=")
				.append(vfUnknownSms).append(", vfProcessingSeg=").append(vfProcessingSeg).append(", vfProcessingSms=")
				.append(vfProcessingSms).append(", vfPartialDeliveredSeg=").append(vfPartialDeliveredSeg)
				.append(", vfPartialDeliveredSms=").append(vfPartialDeliveredSms).append(", vfPartialReceivedSeg=")
				.append(vfPartialReceivedSeg).append(", vfPartialReceivedSms=").append(vfPartialReceivedSms)
				.append(", etNewSeg=").append(etNewSeg).append(", etNewSms=").append(etNewSms)
				.append(", etSubmittedSeg=").append(etSubmittedSeg).append(", etSubmittedSms=").append(etSubmittedSms)
				.append(", etSentSeg=").append(etSentSeg).append(", etSentSms=").append(etSentSms)
				.append(", etTimedOutSeg=").append(etTimedOutSeg).append(", etTimedOutSms=").append(etTimedOutSms)
				.append(", etFailedToSendSeg=").append(etFailedToSendSeg).append(", etFailedToSendSms=")
				.append(etFailedToSendSms).append(", etDeliveredSeg=").append(etDeliveredSeg)
				.append(", etDeliveredSms=").append(etDeliveredSms).append(", etNotDeliveredSeg=")
				.append(etNotDeliveredSeg).append(", etNotDeliveredSms=").append(etNotDeliveredSms)
				.append(", etFailedSeg=").append(etFailedSeg).append(", etFailedSms=").append(etFailedSms)
				.append(", etReceivedSeg=").append(etReceivedSeg).append(", etReceivedSms=").append(etReceivedSms)
				.append(", etExpiredSeg=").append(etExpiredSeg).append(", etExpiredSms=").append(etExpiredSms)
				.append(", etUnknownSeg=").append(etUnknownSeg).append(", etUnknownSms=").append(etUnknownSms)
				.append(", etProcessingSeg=").append(etProcessingSeg).append(", etProcessingSms=")
				.append(etProcessingSms).append(", etPartialDeliveredSeg=").append(etPartialDeliveredSeg)
				.append(", etPartialDeliveredSms=").append(etPartialDeliveredSms).append(", etPartialReceivedSeg=")
				.append(etPartialReceivedSeg).append(", etPartialReceivedSms=").append(etPartialReceivedSms)
				.append(", ogNewSeg=").append(ogNewSeg).append(", ogNewSms=").append(ogNewSms)
				.append(", ogSubmittedSeg=").append(ogSubmittedSeg).append(", ogSubmittedSms=").append(ogSubmittedSms)
				.append(", ogSentSeg=").append(ogSentSeg).append(", ogSentSms=").append(ogSentSms)
				.append(", ogTimedOutSeg=").append(ogTimedOutSeg).append(", ogTimedOutSms=").append(ogTimedOutSms)
				.append(", ogFailedToSendSeg=").append(ogFailedToSendSeg).append(", ogFailedToSendSms=")
				.append(ogFailedToSendSms).append(", ogDeliveredSeg=").append(ogDeliveredSeg)
				.append(", ogDeliveredSms=").append(ogDeliveredSms).append(", ogNotDeliveredSeg=")
				.append(ogNotDeliveredSeg).append(", ogNotDeliveredSms=").append(ogNotDeliveredSms)
				.append(", ogFailedSeg=").append(ogFailedSeg).append(", ogFailedSms=").append(ogFailedSms)
				.append(", ogReceivedSeg=").append(ogReceivedSeg).append(", ogReceivedSms=").append(ogReceivedSms)
				.append(", ogExpiredSeg=").append(ogExpiredSeg).append(", ogExpiredSms=").append(ogExpiredSms)
				.append(", ogUnknownSeg=").append(ogUnknownSeg).append(", ogUnknownSms=").append(ogUnknownSms)
				.append(", ogProcessingSeg=").append(ogProcessingSeg).append(", ogProcessingSms=")
				.append(ogProcessingSms).append(", ogPartialDeliveredSeg=").append(ogPartialDeliveredSeg)
				.append(", ogPartialDeliveredSms=").append(ogPartialDeliveredSms).append(", ogPartialReceivedSeg=")
				.append(ogPartialReceivedSeg).append(", ogPartialReceivedSms=").append(ogPartialReceivedSms)
				.append(", weNewSeg=").append(weNewSeg).append(", weNewSms=").append(weNewSms)
				.append(", weSubmittedSeg=").append(weSubmittedSeg).append(", weSubmittedSms=").append(weSubmittedSms)
				.append(", weSentSeg=").append(weSentSeg).append(", weSentSms=").append(weSentSms)
				.append(", weTimedOutSeg=").append(weTimedOutSeg).append(", weTimedOutSms=").append(weTimedOutSms)
				.append(", weFailedToSendSeg=").append(weFailedToSendSeg).append(", weFailedToSendSms=")
				.append(weFailedToSendSms).append(", weDeliveredSeg=").append(weDeliveredSeg)
				.append(", weDeliveredSms=").append(weDeliveredSms).append(", weNotDeliveredSeg=")
				.append(weNotDeliveredSeg).append(", weNotDeliveredSms=").append(weNotDeliveredSms)
				.append(", weFailedSeg=").append(weFailedSeg).append(", weFailedSms=").append(weFailedSms)
				.append(", weReceivedSeg=").append(weReceivedSeg).append(", weReceivedSms=").append(weReceivedSms)
				.append(", weExpiredSeg=").append(weExpiredSeg).append(", weExpiredSms=").append(weExpiredSms)
				.append(", weUnknownSeg=").append(weUnknownSeg).append(", weUnknownSms=").append(weUnknownSms)
				.append(", weProcessingSeg=").append(weProcessingSeg).append(", weProcessingSms=")
				.append(weProcessingSms).append(", wePartialDeliveredSeg=").append(wePartialDeliveredSeg)
				.append(", wePartialDeliveredSms=").append(wePartialDeliveredSms).append(", wePartialReceivedSeg=")
				.append(wePartialReceivedSeg).append(", wePartialReceivedSms=").append(wePartialReceivedSms)
				.append(", interNewSeg=").append(interNewSeg).append(", interNewSms=").append(interNewSms)
				.append(", interSubmittedSeg=").append(interSubmittedSeg).append(", interSubmittedSms=")
				.append(interSubmittedSms).append(", interSentSeg=").append(interSentSeg).append(", interSentSms=")
				.append(interSentSms).append(", interTimedOutSeg=").append(interTimedOutSeg)
				.append(", interTimedOutSms=").append(interTimedOutSms).append(", interFailedToSendSeg=")
				.append(interFailedToSendSeg).append(", interFailedToSendSms=").append(interFailedToSendSms)
				.append(", interDeliveredSeg=").append(interDeliveredSeg).append(", interDeliveredSms=")
				.append(interDeliveredSms).append(", interNotDeliveredSeg=").append(interNotDeliveredSeg)
				.append(", interNotDeliveredSms=").append(interNotDeliveredSms).append(", interFailedSeg=")
				.append(interFailedSeg).append(", interFailedSms=").append(interFailedSms).append(", interReceivedSeg=")
				.append(interReceivedSeg).append(", interReceivedSms=").append(interReceivedSms)
				.append(", interExpiredSeg=").append(interExpiredSeg).append(", interExpiredSms=")
				.append(interExpiredSms).append(", interUnknownSeg=").append(interUnknownSeg)
				.append(", interUnknownSms=").append(interUnknownSms).append(", interProcessingSeg=")
				.append(interProcessingSeg).append(", interProcessingSms=").append(interProcessingSms)
				.append(", interPartialDeliveredSeg=").append(interPartialDeliveredSeg)
				.append(", interPartialDeliveredSms=").append(interPartialDeliveredSms)
				.append(", interPartialReceivedSeg=").append(interPartialReceivedSeg)
				.append(", interPartialReceivedSms=").append(interPartialReceivedSms).append("}");
		return builder.toString();
	}

}
