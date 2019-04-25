
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.enums.StatsStatsType;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Entity
@Table(name = "COLLECTION_STATS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "CollectionStats.findAll", query = "SELECT c FROM CollectionStats c"),
		@NamedQuery(name = "CollectionStats.findByProcessingDate", query = "SELECT c FROM CollectionStats c WHERE c.collectionStatsPK.processingDate = :processingDate"),
		@NamedQuery(name = "CollectionStats.findByProcessingHour", query = "SELECT c FROM CollectionStats c WHERE c.collectionStatsPK.processingHour = :processingHour"),
		@NamedQuery(name = "CollectionStats.findByGroupId", query = "SELECT c FROM CollectionStats c WHERE c.collectionStatsPK.groupId = :groupId"),
		@NamedQuery(name = "CollectionStats.findByOwnerId", query = "SELECT c FROM CollectionStats c WHERE c.collectionStatsPK.ownerId = :ownerId"),
		@NamedQuery(name = "CollectionStats.findByStatsType", query = "SELECT c FROM CollectionStats c WHERE c.collectionStatsPK.statsType = :statsType") })
public class CollectionStats implements Serializable {

	private static final long serialVersionUID = -1003290642466262786L;

	@EmbeddedId
	protected CollectionStatsPK collectionStatsPK;

	@Column(name = "VF_NEW_SEG")
	private BigInteger vfNewSeg;
	@Column(name = "VF_NEW_SMS")
	private BigInteger vfNewSms;
	@Column(name = "VF_SUBMITTED_SEG")
	private BigInteger vfSubmittedSeg;
	@Column(name = "VF_SUBMITTED_SMS")
	private BigInteger vfSubmittedSms;
	@Column(name = "VF_SENT_SEG")
	private BigInteger vfSentSeg;
	@Column(name = "VF_SENT_SMS")
	private BigInteger vfSentSms;
	@Column(name = "VF_TIMED_OUT_SEG")
	private BigInteger vfTimedOutSeg;
	@Column(name = "VF_TIMED_OUT_SMS")
	private BigInteger vfTimedOutSms;
	@Column(name = "VF_FAILED_TO_SEND_SEG")
	private BigInteger vfFailedToSendSeg;
	@Column(name = "VF_FAILED_TO_SEND_SMS")
	private BigInteger vfFailedToSendSms;
	@Column(name = "VF_DELIVERED_SEG")
	private BigInteger vfDeliveredSeg;
	@Column(name = "VF_DELIVERED_SMS")
	private BigInteger vfDeliveredSms;
	@Column(name = "VF_NOT_DELIVERED_SEG")
	private BigInteger vfNotDeliveredSeg;
	@Column(name = "VF_NOT_DELIVERED_SMS")
	private BigInteger vfNotDeliveredSms;
	@Column(name = "VF_FAILED_SEG")
	private BigInteger vfFailedSeg;
	@Column(name = "VF_FAILED_SMS")
	private BigInteger vfFailedSms;
	@Column(name = "VF_RECEIVED_SEG")
	private BigInteger vfReceivedSeg;
	@Column(name = "VF_RECEIVED_SMS")
	private BigInteger vfReceivedSms;
	@Column(name = "VF_EXPIRED_SEG")
	private BigInteger vfExpiredSeg;
	@Column(name = "VF_EXPIRED_SMS")
	private BigInteger vfExpiredSms;
	@Column(name = "VF_UNKNOWN_SEG")
	private BigInteger vfUnknownSeg;
	@Column(name = "VF_UNKNOWN_SMS")
	private BigInteger vfUnknownSms;
	@Column(name = "VF_PROCESSING_SEG")
	private BigInteger vfProcessingSeg;
	@Column(name = "VF_PROCESSING_SMS")
	private BigInteger vfProcessingSms;
	@Column(name = "VF_PARTIAL_DELIVERED_SEG")
	private BigInteger vfPartialDeliveredSeg;
	@Column(name = "VF_PARTIAL_DELIVERED_SMS")
	private BigInteger vfPartialDeliveredSms;
	@Column(name = "VF_PARTIAL_RECEIVED_SEG")
	private BigInteger vfPartialReceivedSeg;
	@Column(name = "VF_PARTIAL_RECEIVED_SMS")
	private BigInteger vfPartialReceivedSms;

	@Column(name = "ET_NEW_SEG")
	private BigInteger etNewSeg;
	@Column(name = "ET_NEW_SMS")
	private BigInteger etNewSms;
	@Column(name = "ET_SUBMITTED_SEG")
	private BigInteger etSubmittedSeg;
	@Column(name = "ET_SUBMITTED_SMS")
	private BigInteger etSubmittedSms;
	@Column(name = "ET_SENT_SEG")
	private BigInteger etSentSeg;
	@Column(name = "ET_SENT_SMS")
	private BigInteger etSentSms;
	@Column(name = "ET_TIMED_OUT_SEG")
	private BigInteger etTimedOutSeg;
	@Column(name = "ET_TIMED_OUT_SMS")
	private BigInteger etTimedOutSms;
	@Column(name = "ET_FAILED_TO_SEND_SEG")
	private BigInteger etFailedToSendSeg;
	@Column(name = "ET_FAILED_TO_SEND_SMS")
	private BigInteger etFailedToSendSms;
	@Column(name = "ET_DELIVERED_SEG")
	private BigInteger etDeliveredSeg;
	@Column(name = "ET_DELIVERED_SMS")
	private BigInteger etDeliveredSms;
	@Column(name = "ET_NOT_DELIVERED_SEG")
	private BigInteger etNotDeliveredSeg;
	@Column(name = "ET_NOT_DELIVERED_SMS")
	private BigInteger etNotDeliveredSms;
	@Column(name = "ET_FAILED_SEG")
	private BigInteger etFailedSeg;
	@Column(name = "ET_FAILED_SMS")
	private BigInteger etFailedSms;
	@Column(name = "ET_RECEIVED_SEG")
	private BigInteger etReceivedSeg;
	@Column(name = "ET_RECEIVED_SMS")
	private BigInteger etReceivedSms;
	@Column(name = "ET_EXPIRED_SEG")
	private BigInteger etExpiredSeg;
	@Column(name = "ET_EXPIRED_SMS")
	private BigInteger etExpiredSms;
	@Column(name = "ET_UNKNOWN_SEG")
	private BigInteger etUnknownSeg;
	@Column(name = "ET_UNKNOWN_SMS")
	private BigInteger etUnknownSms;
	@Column(name = "ET_PROCESSING_SEG")
	private BigInteger etProcessingSeg;
	@Column(name = "ET_PROCESSING_SMS")
	private BigInteger etProcessingSms;
	@Column(name = "ET_PARTIAL_DELIVERED_SEG")
	private BigInteger etPartialDeliveredSeg;
	@Column(name = "ET_PARTIAL_DELIVERED_SMS")
	private BigInteger etPartialDeliveredSms;
	@Column(name = "ET_PARTIAL_RECEIVED_SEG")
	private BigInteger etPartialReceivedSeg;
	@Column(name = "ET_PARTIAL_RECEIVED_SMS")
	private BigInteger etPartialReceivedSms;

	@Column(name = "OG_NEW_SEG")
	private BigInteger ogNewSeg;
	@Column(name = "OG_NEW_SMS")
	private BigInteger ogNewSms;
	@Column(name = "OG_SUBMITTED_SEG")
	private BigInteger ogSubmittedSeg;
	@Column(name = "OG_SUBMITTED_SMS")
	private BigInteger ogSubmittedSms;
	@Column(name = "OG_SENT_SEG")
	private BigInteger ogSentSeg;
	@Column(name = "OG_SENT_SMS")
	private BigInteger ogSentSms;
	@Column(name = "OG_TIMED_OUT_SEG")
	private BigInteger ogTimedOutSeg;
	@Column(name = "OG_TIMED_OUT_SMS")
	private BigInteger ogTimedOutSms;
	@Column(name = "OG_FAILED_TO_SEND_SEG")
	private BigInteger ogFailedToSendSeg;
	@Column(name = "OG_FAILED_TO_SEND_SMS")
	private BigInteger ogFailedToSendSms;
	@Column(name = "OG_DELIVERED_SEG")
	private BigInteger ogDeliveredSeg;
	@Column(name = "OG_DELIVERED_SMS")
	private BigInteger ogDeliveredSms;
	@Column(name = "OG_NOT_DELIVERED_SEG")
	private BigInteger ogNotDeliveredSeg;
	@Column(name = "OG_NOT_DELIVERED_SMS")
	private BigInteger ogNotDeliveredSms;
	@Column(name = "OG_FAILED_SEG")
	private BigInteger ogFailedSeg;
	@Column(name = "OG_FAILED_SMS")
	private BigInteger ogFailedSms;
	@Column(name = "OG_RECEIVED_SEG")
	private BigInteger ogReceivedSeg;
	@Column(name = "OG_RECEIVED_SMS")
	private BigInteger ogReceivedSms;
	@Column(name = "OG_EXPIRED_SEG")
	private BigInteger ogExpiredSeg;
	@Column(name = "OG_EXPIRED_SMS")
	private BigInteger ogExpiredSms;
	@Column(name = "OG_UNKNOWN_SEG")
	private BigInteger ogUnknownSeg;
	@Column(name = "OG_UNKNOWN_SMS")
	private BigInteger ogUnknownSms;
	@Column(name = "OG_PROCESSING_SEG")
	private BigInteger ogProcessingSeg;
	@Column(name = "OG_PROCESSING_SMS")
	private BigInteger ogProcessingSms;
	@Column(name = "OG_PARTIAL_DELIVERED_SEG")
	private BigInteger ogPartialDeliveredSeg;
	@Column(name = "OG_PARTIAL_DELIVERED_SMS")
	private BigInteger ogPartialDeliveredSms;
	@Column(name = "OG_PARTIAL_RECEIVED_SEG")
	private BigInteger ogPartialReceivedSeg;
	@Column(name = "OG_PARTIAL_RECEIVED_SMS")
	private BigInteger ogPartialReceivedSms;

	@Column(name = "WE_NEW_SEG")
	private BigInteger weNewSeg;
	@Column(name = "WE_NEW_SMS")
	private BigInteger weNewSms;
	@Column(name = "WE_SUBMITTED_SEG")
	private BigInteger weSubmittedSeg;
	@Column(name = "WE_SUBMITTED_SMS")
	private BigInteger weSubmittedSms;
	@Column(name = "WE_SENT_SEG")
	private BigInteger weSentSeg;
	@Column(name = "WE_SENT_SMS")
	private BigInteger weSentSms;
	@Column(name = "WE_TIMED_OUT_SEG")
	private BigInteger weTimedOutSeg;
	@Column(name = "WE_TIMED_OUT_SMS")
	private BigInteger weTimedOutSms;
	@Column(name = "WE_FAILED_TO_SEND_SEG")
	private BigInteger weFailedToSendSeg;
	@Column(name = "WE_FAILED_TO_SEND_SMS")
	private BigInteger weFailedToSendSms;
	@Column(name = "WE_DELIVERED_SEG")
	private BigInteger weDeliveredSeg;
	@Column(name = "WE_DELIVERED_SMS")
	private BigInteger weDeliveredSms;
	@Column(name = "WE_NOT_DELIVERED_SEG")
	private BigInteger weNotDeliveredSeg;
	@Column(name = "WE_NOT_DELIVERED_SMS")
	private BigInteger weNotDeliveredSms;
	@Column(name = "WE_FAILED_SEG")
	private BigInteger weFailedSeg;
	@Column(name = "WE_FAILED_SMS")
	private BigInteger weFailedSms;
	@Column(name = "WE_RECEIVED_SEG")
	private BigInteger weReceivedSeg;
	@Column(name = "WE_RECEIVED_SMS")
	private BigInteger weReceivedSms;
	@Column(name = "WE_EXPIRED_SEG")
	private BigInteger weExpiredSeg;
	@Column(name = "WE_EXPIRED_SMS")
	private BigInteger weExpiredSms;
	@Column(name = "WE_UNKNOWN_SEG")
	private BigInteger weUnknownSeg;
	@Column(name = "WE_UNKNOWN_SMS")
	private BigInteger weUnknownSms;
	@Column(name = "WE_PROCESSING_SEG")
	private BigInteger weProcessingSeg;
	@Column(name = "WE_PROCESSING_SMS")
	private BigInteger weProcessingSms;
	@Column(name = "WE_PARTIAL_DELIVERED_SEG")
	private BigInteger wePartialDeliveredSeg;
	@Column(name = "WE_PARTIAL_DELIVERED_SMS")
	private BigInteger wePartialDeliveredSms;
	@Column(name = "WE_PARTIAL_RECEIVED_SEG")
	private BigInteger wePartialReceivedSeg;
	@Column(name = "WE_PARTIAL_RECEIVED_SMS")
	private BigInteger wePartialReceivedSms;

	@Column(name = "INTER_NEW_SEG")
	private BigInteger interNewSeg;
	@Column(name = "INTER_NEW_SMS")
	private BigInteger interNewSms;
	@Column(name = "INTER_SUBMITTED_SEG")
	private BigInteger interSubmittedSeg;
	@Column(name = "INTER_SUBMITTED_SMS")
	private BigInteger interSubmittedSms;
	@Column(name = "INTER_SENT_SEG")
	private BigInteger interSentSeg;
	@Column(name = "INTER_SENT_SMS")
	private BigInteger interSentSms;
	@Column(name = "INTER_TIMED_OUT_SEG")
	private BigInteger interTimedOutSeg;
	@Column(name = "INTER_TIMED_OUT_SMS")
	private BigInteger interTimedOutSms;
	@Column(name = "INTER_FAILED_TO_SEND_SEG")
	private BigInteger interFailedToSendSeg;
	@Column(name = "INTER_FAILED_TO_SEND_SMS")
	private BigInteger interFailedToSendSms;
	@Column(name = "INTER_DELIVERED_SEG")
	private BigInteger interDeliveredSeg;
	@Column(name = "INTER_DELIVERED_SMS")
	private BigInteger interDeliveredSms;
	@Column(name = "INTER_NOT_DELIVERED_SEG")
	private BigInteger interNotDeliveredSeg;
	@Column(name = "INTER_NOT_DELIVERED_SMS")
	private BigInteger interNotDeliveredSms;
	@Column(name = "INTER_FAILED_SEG")
	private BigInteger interFailedSeg;
	@Column(name = "INTER_FAILED_SMS")
	private BigInteger interFailedSms;
	@Column(name = "INTER_RECEIVED_SEG")
	private BigInteger interReceivedSeg;
	@Column(name = "INTER_RECEIVED_SMS")
	private BigInteger interReceivedSms;
	@Column(name = "INTER_EXPIRED_SEG")
	private BigInteger interExpiredSeg;
	@Column(name = "INTER_EXPIRED_SMS")
	private BigInteger interExpiredSms;
	@Column(name = "INTER_UNKNOWN_SEG")
	private BigInteger interUnknownSeg;
	@Column(name = "INTER_UNKNOWN_SMS")
	private BigInteger interUnknownSms;
	@Column(name = "INTER_PROCESSING_SEG")
	private BigInteger interProcessingSeg;
	@Column(name = "INTER_PROCESSING_SMS")
	private BigInteger interProcessingSms;
	@Column(name = "INTER_PARTIAL_DELIVERED_SEG")
	private BigInteger interPartialDeliveredSeg;
	@Column(name = "INTER_PARTIAL_DELIVERED_SMS")
	private BigInteger interPartialDeliveredSms;
	@Column(name = "INTER_PARTIAL_RECEIVED_SEG")
	private BigInteger interPartialReceivedSeg;
	@Column(name = "INTER_PARTIAL_RECEIVED_SMS")
	private BigInteger interPartialReceivedSms;

	public CollectionStats() {}

	public CollectionStats(CollectionStatsPK collectionStatsPK) {
		this.collectionStatsPK = collectionStatsPK;
	}

	public CollectionStats(Date processingDate, BigInteger processingHour, String groupId, String ownerId,
			StatsStatsType statsType) {
		this.collectionStatsPK = new CollectionStatsPK(processingDate, processingHour, groupId, ownerId, statsType);
	}

	public CollectionStatsPK getCollectionStatsPK() {
		return collectionStatsPK;
	}

	public void setCollectionStatsPK(CollectionStatsPK collectionStatsPK) {
		this.collectionStatsPK = collectionStatsPK;
	}

	public BigInteger getVfNewSeg() {
		return vfNewSeg;
	}

	public void setVfNewSeg(BigInteger vfNewSeg) {
		this.vfNewSeg = vfNewSeg;
	}

	public BigInteger getVfNewSms() {
		return vfNewSms;
	}

	public void setVfNewSms(BigInteger vfNewSms) {
		this.vfNewSms = vfNewSms;
	}

	public BigInteger getVfSubmittedSeg() {
		return vfSubmittedSeg;
	}

	public void setVfSubmittedSeg(BigInteger vfSubmittedSeg) {
		this.vfSubmittedSeg = vfSubmittedSeg;
	}

	public BigInteger getVfSubmittedSms() {
		return vfSubmittedSms;
	}

	public void setVfSubmittedSms(BigInteger vfSubmittedSms) {
		this.vfSubmittedSms = vfSubmittedSms;
	}

	public BigInteger getVfSentSeg() {
		return vfSentSeg;
	}

	public void setVfSentSeg(BigInteger vfSentSeg) {
		this.vfSentSeg = vfSentSeg;
	}

	public BigInteger getVfSentSms() {
		return vfSentSms;
	}

	public void setVfSentSms(BigInteger vfSentSms) {
		this.vfSentSms = vfSentSms;
	}

	public BigInteger getVfTimedOutSeg() {
		return vfTimedOutSeg;
	}

	public void setVfTimedOutSeg(BigInteger vfTimedOutSeg) {
		this.vfTimedOutSeg = vfTimedOutSeg;
	}

	public BigInteger getVfTimedOutSms() {
		return vfTimedOutSms;
	}

	public void setVfTimedOutSms(BigInteger vfTimedOutSms) {
		this.vfTimedOutSms = vfTimedOutSms;
	}

	public BigInteger getVfFailedToSendSeg() {
		return vfFailedToSendSeg;
	}

	public void setVfFailedToSendSeg(BigInteger vfFailedToSendSeg) {
		this.vfFailedToSendSeg = vfFailedToSendSeg;
	}

	public BigInteger getVfFailedToSendSms() {
		return vfFailedToSendSms;
	}

	public void setVfFailedToSendSms(BigInteger vfFailedToSendSms) {
		this.vfFailedToSendSms = vfFailedToSendSms;
	}

	public BigInteger getVfDeliveredSeg() {
		return vfDeliveredSeg;
	}

	public void setVfDeliveredSeg(BigInteger vfDeliveredSeg) {
		this.vfDeliveredSeg = vfDeliveredSeg;
	}

	public BigInteger getVfDeliveredSms() {
		return vfDeliveredSms;
	}

	public void setVfDeliveredSms(BigInteger vfDeliveredSms) {
		this.vfDeliveredSms = vfDeliveredSms;
	}

	public BigInteger getVfNotDeliveredSeg() {
		return vfNotDeliveredSeg;
	}

	public void setVfNotDeliveredSeg(BigInteger vfNotDeliveredSeg) {
		this.vfNotDeliveredSeg = vfNotDeliveredSeg;
	}

	public BigInteger getVfNotDeliveredSms() {
		return vfNotDeliveredSms;
	}

	public void setVfNotDeliveredSms(BigInteger vfNotDeliveredSms) {
		this.vfNotDeliveredSms = vfNotDeliveredSms;
	}

	public BigInteger getVfFailedSeg() {
		return vfFailedSeg;
	}

	public void setVfFailedSeg(BigInteger vfFailedSeg) {
		this.vfFailedSeg = vfFailedSeg;
	}

	public BigInteger getVfFailedSms() {
		return vfFailedSms;
	}

	public void setVfFailedSms(BigInteger vfFailedSms) {
		this.vfFailedSms = vfFailedSms;
	}

	public BigInteger getVfReceivedSeg() {
		return vfReceivedSeg;
	}

	public void setVfReceivedSeg(BigInteger vfReceivedSeg) {
		this.vfReceivedSeg = vfReceivedSeg;
	}

	public BigInteger getVfReceivedSms() {
		return vfReceivedSms;
	}

	public void setVfReceivedSms(BigInteger vfReceivedSms) {
		this.vfReceivedSms = vfReceivedSms;
	}

	public BigInteger getVfExpiredSeg() {
		return vfExpiredSeg;
	}

	public void setVfExpiredSeg(BigInteger vfExpiredSeg) {
		this.vfExpiredSeg = vfExpiredSeg;
	}

	public BigInteger getVfExpiredSms() {
		return vfExpiredSms;
	}

	public void setVfExpiredSms(BigInteger vfExpiredSms) {
		this.vfExpiredSms = vfExpiredSms;
	}

	public BigInteger getVfUnknownSeg() {
		return vfUnknownSeg;
	}

	public void setVfUnknownSeg(BigInteger vfUnknownSeg) {
		this.vfUnknownSeg = vfUnknownSeg;
	}

	public BigInteger getVfUnknownSms() {
		return vfUnknownSms;
	}

	public void setVfUnknownSms(BigInteger vfUnknownSms) {
		this.vfUnknownSms = vfUnknownSms;
	}

	public BigInteger getVfProcessingSeg() {
		return vfProcessingSeg;
	}

	public void setVfProcessingSeg(BigInteger vfProcessingSeg) {
		this.vfProcessingSeg = vfProcessingSeg;
	}

	public BigInteger getVfProcessingSms() {
		return vfProcessingSms;
	}

	public void setVfProcessingSms(BigInteger vfProcessingSms) {
		this.vfProcessingSms = vfProcessingSms;
	}

	public BigInteger getVfPartialDeliveredSeg() {
		return vfPartialDeliveredSeg;
	}

	public void setVfPartialDeliveredSeg(BigInteger vfPartialDeliveredSeg) {
		this.vfPartialDeliveredSeg = vfPartialDeliveredSeg;
	}

	public BigInteger getVfPartialDeliveredSms() {
		return vfPartialDeliveredSms;
	}

	public void setVfPartialDeliveredSms(BigInteger vfPartialDeliveredSms) {
		this.vfPartialDeliveredSms = vfPartialDeliveredSms;
	}

	public BigInteger getVfPartialReceivedSeg() {
		return vfPartialReceivedSeg;
	}

	public void setVfPartialReceivedSeg(BigInteger vfPartialReceivedSeg) {
		this.vfPartialReceivedSeg = vfPartialReceivedSeg;
	}

	public BigInteger getVfPartialReceivedSms() {
		return vfPartialReceivedSms;
	}

	public void setVfPartialReceivedSms(BigInteger vfPartialReceivedSms) {
		this.vfPartialReceivedSms = vfPartialReceivedSms;
	}

	public BigInteger getEtNewSeg() {
		return etNewSeg;
	}

	public void setEtNewSeg(BigInteger etNewSeg) {
		this.etNewSeg = etNewSeg;
	}

	public BigInteger getEtNewSms() {
		return etNewSms;
	}

	public void setEtNewSms(BigInteger etNewSms) {
		this.etNewSms = etNewSms;
	}

	public BigInteger getEtSubmittedSeg() {
		return etSubmittedSeg;
	}

	public void setEtSubmittedSeg(BigInteger etSubmittedSeg) {
		this.etSubmittedSeg = etSubmittedSeg;
	}

	public BigInteger getEtSubmittedSms() {
		return etSubmittedSms;
	}

	public void setEtSubmittedSms(BigInteger etSubmittedSms) {
		this.etSubmittedSms = etSubmittedSms;
	}

	public BigInteger getEtSentSeg() {
		return etSentSeg;
	}

	public void setEtSentSeg(BigInteger etSentSeg) {
		this.etSentSeg = etSentSeg;
	}

	public BigInteger getEtSentSms() {
		return etSentSms;
	}

	public void setEtSentSms(BigInteger etSentSms) {
		this.etSentSms = etSentSms;
	}

	public BigInteger getEtTimedOutSeg() {
		return etTimedOutSeg;
	}

	public void setEtTimedOutSeg(BigInteger etTimedOutSeg) {
		this.etTimedOutSeg = etTimedOutSeg;
	}

	public BigInteger getEtTimedOutSms() {
		return etTimedOutSms;
	}

	public void setEtTimedOutSms(BigInteger etTimedOutSms) {
		this.etTimedOutSms = etTimedOutSms;
	}

	public BigInteger getEtFailedToSendSeg() {
		return etFailedToSendSeg;
	}

	public void setEtFailedToSendSeg(BigInteger etFailedToSendSeg) {
		this.etFailedToSendSeg = etFailedToSendSeg;
	}

	public BigInteger getEtFailedToSendSms() {
		return etFailedToSendSms;
	}

	public void setEtFailedToSendSms(BigInteger etFailedToSendSms) {
		this.etFailedToSendSms = etFailedToSendSms;
	}

	public BigInteger getEtDeliveredSeg() {
		return etDeliveredSeg;
	}

	public void setEtDeliveredSeg(BigInteger etDeliveredSeg) {
		this.etDeliveredSeg = etDeliveredSeg;
	}

	public BigInteger getEtDeliveredSms() {
		return etDeliveredSms;
	}

	public void setEtDeliveredSms(BigInteger etDeliveredSms) {
		this.etDeliveredSms = etDeliveredSms;
	}

	public BigInteger getEtNotDeliveredSeg() {
		return etNotDeliveredSeg;
	}

	public void setEtNotDeliveredSeg(BigInteger etNotDeliveredSeg) {
		this.etNotDeliveredSeg = etNotDeliveredSeg;
	}

	public BigInteger getEtNotDeliveredSms() {
		return etNotDeliveredSms;
	}

	public void setEtNotDeliveredSms(BigInteger etNotDeliveredSms) {
		this.etNotDeliveredSms = etNotDeliveredSms;
	}

	public BigInteger getEtFailedSeg() {
		return etFailedSeg;
	}

	public void setEtFailedSeg(BigInteger etFailedSeg) {
		this.etFailedSeg = etFailedSeg;
	}

	public BigInteger getEtFailedSms() {
		return etFailedSms;
	}

	public void setEtFailedSms(BigInteger etFailedSms) {
		this.etFailedSms = etFailedSms;
	}

	public BigInteger getEtReceivedSeg() {
		return etReceivedSeg;
	}

	public void setEtReceivedSeg(BigInteger etReceivedSeg) {
		this.etReceivedSeg = etReceivedSeg;
	}

	public BigInteger getEtReceivedSms() {
		return etReceivedSms;
	}

	public void setEtReceivedSms(BigInteger etReceivedSms) {
		this.etReceivedSms = etReceivedSms;
	}

	public BigInteger getEtExpiredSeg() {
		return etExpiredSeg;
	}

	public void setEtExpiredSeg(BigInteger etExpiredSeg) {
		this.etExpiredSeg = etExpiredSeg;
	}

	public BigInteger getEtExpiredSms() {
		return etExpiredSms;
	}

	public void setEtExpiredSms(BigInteger etExpiredSms) {
		this.etExpiredSms = etExpiredSms;
	}

	public BigInteger getEtUnknownSeg() {
		return etUnknownSeg;
	}

	public void setEtUnknownSeg(BigInteger etUnknownSeg) {
		this.etUnknownSeg = etUnknownSeg;
	}

	public BigInteger getEtUnknownSms() {
		return etUnknownSms;
	}

	public void setEtUnknownSms(BigInteger etUnknownSms) {
		this.etUnknownSms = etUnknownSms;
	}

	public BigInteger getEtProcessingSeg() {
		return etProcessingSeg;
	}

	public void setEtProcessingSeg(BigInteger etProcessingSeg) {
		this.etProcessingSeg = etProcessingSeg;
	}

	public BigInteger getEtProcessingSms() {
		return etProcessingSms;
	}

	public void setEtProcessingSms(BigInteger etProcessingSms) {
		this.etProcessingSms = etProcessingSms;
	}

	public BigInteger getEtPartialDeliveredSeg() {
		return etPartialDeliveredSeg;
	}

	public void setEtPartialDeliveredSeg(BigInteger etPartialDeliveredSeg) {
		this.etPartialDeliveredSeg = etPartialDeliveredSeg;
	}

	public BigInteger getEtPartialDeliveredSms() {
		return etPartialDeliveredSms;
	}

	public void setEtPartialDeliveredSms(BigInteger etPartialDeliveredSms) {
		this.etPartialDeliveredSms = etPartialDeliveredSms;
	}

	public BigInteger getEtPartialReceivedSeg() {
		return etPartialReceivedSeg;
	}

	public void setEtPartialReceivedSeg(BigInteger etPartialReceivedSeg) {
		this.etPartialReceivedSeg = etPartialReceivedSeg;
	}

	public BigInteger getEtPartialReceivedSms() {
		return etPartialReceivedSms;
	}

	public void setEtPartialReceivedSms(BigInteger etPartialReceivedSms) {
		this.etPartialReceivedSms = etPartialReceivedSms;
	}

	public BigInteger getOgNewSeg() {
		return ogNewSeg;
	}

	public void setOgNewSeg(BigInteger ogNewSeg) {
		this.ogNewSeg = ogNewSeg;
	}

	public BigInteger getOgNewSms() {
		return ogNewSms;
	}

	public void setOgNewSms(BigInteger ogNewSms) {
		this.ogNewSms = ogNewSms;
	}

	public BigInteger getOgSubmittedSeg() {
		return ogSubmittedSeg;
	}

	public void setOgSubmittedSeg(BigInteger ogSubmittedSeg) {
		this.ogSubmittedSeg = ogSubmittedSeg;
	}

	public BigInteger getOgSubmittedSms() {
		return ogSubmittedSms;
	}

	public void setOgSubmittedSms(BigInteger ogSubmittedSms) {
		this.ogSubmittedSms = ogSubmittedSms;
	}

	public BigInteger getOgSentSeg() {
		return ogSentSeg;
	}

	public void setOgSentSeg(BigInteger ogSentSeg) {
		this.ogSentSeg = ogSentSeg;
	}

	public BigInteger getOgSentSms() {
		return ogSentSms;
	}

	public void setOgSentSms(BigInteger ogSentSms) {
		this.ogSentSms = ogSentSms;
	}

	public BigInteger getOgTimedOutSeg() {
		return ogTimedOutSeg;
	}

	public void setOgTimedOutSeg(BigInteger ogTimedOutSeg) {
		this.ogTimedOutSeg = ogTimedOutSeg;
	}

	public BigInteger getOgTimedOutSms() {
		return ogTimedOutSms;
	}

	public void setOgTimedOutSms(BigInteger ogTimedOutSms) {
		this.ogTimedOutSms = ogTimedOutSms;
	}

	public BigInteger getOgFailedToSendSeg() {
		return ogFailedToSendSeg;
	}

	public void setOgFailedToSendSeg(BigInteger ogFailedToSendSeg) {
		this.ogFailedToSendSeg = ogFailedToSendSeg;
	}

	public BigInteger getOgFailedToSendSms() {
		return ogFailedToSendSms;
	}

	public void setOgFailedToSendSms(BigInteger ogFailedToSendSms) {
		this.ogFailedToSendSms = ogFailedToSendSms;
	}

	public BigInteger getOgDeliveredSeg() {
		return ogDeliveredSeg;
	}

	public void setOgDeliveredSeg(BigInteger ogDeliveredSeg) {
		this.ogDeliveredSeg = ogDeliveredSeg;
	}

	public BigInteger getOgDeliveredSms() {
		return ogDeliveredSms;
	}

	public void setOgDeliveredSms(BigInteger ogDeliveredSms) {
		this.ogDeliveredSms = ogDeliveredSms;
	}

	public BigInteger getOgNotDeliveredSeg() {
		return ogNotDeliveredSeg;
	}

	public void setOgNotDeliveredSeg(BigInteger ogNotDeliveredSeg) {
		this.ogNotDeliveredSeg = ogNotDeliveredSeg;
	}

	public BigInteger getOgNotDeliveredSms() {
		return ogNotDeliveredSms;
	}

	public void setOgNotDeliveredSms(BigInteger ogNotDeliveredSms) {
		this.ogNotDeliveredSms = ogNotDeliveredSms;
	}

	public BigInteger getOgFailedSeg() {
		return ogFailedSeg;
	}

	public void setOgFailedSeg(BigInteger ogFailedSeg) {
		this.ogFailedSeg = ogFailedSeg;
	}

	public BigInteger getOgFailedSms() {
		return ogFailedSms;
	}

	public void setOgFailedSms(BigInteger ogFailedSms) {
		this.ogFailedSms = ogFailedSms;
	}

	public BigInteger getOgReceivedSeg() {
		return ogReceivedSeg;
	}

	public void setOgReceivedSeg(BigInteger ogReceivedSeg) {
		this.ogReceivedSeg = ogReceivedSeg;
	}

	public BigInteger getOgReceivedSms() {
		return ogReceivedSms;
	}

	public void setOgReceivedSms(BigInteger ogReceivedSms) {
		this.ogReceivedSms = ogReceivedSms;
	}

	public BigInteger getOgExpiredSeg() {
		return ogExpiredSeg;
	}

	public void setOgExpiredSeg(BigInteger ogExpiredSeg) {
		this.ogExpiredSeg = ogExpiredSeg;
	}

	public BigInteger getOgExpiredSms() {
		return ogExpiredSms;
	}

	public void setOgExpiredSms(BigInteger ogExpiredSms) {
		this.ogExpiredSms = ogExpiredSms;
	}

	public BigInteger getOgUnknownSeg() {
		return ogUnknownSeg;
	}

	public void setOgUnknownSeg(BigInteger ogUnknownSeg) {
		this.ogUnknownSeg = ogUnknownSeg;
	}

	public BigInteger getOgUnknownSms() {
		return ogUnknownSms;
	}

	public void setOgUnknownSms(BigInteger ogUnknownSms) {
		this.ogUnknownSms = ogUnknownSms;
	}

	public BigInteger getOgProcessingSeg() {
		return ogProcessingSeg;
	}

	public void setOgProcessingSeg(BigInteger ogProcessingSeg) {
		this.ogProcessingSeg = ogProcessingSeg;
	}

	public BigInteger getOgProcessingSms() {
		return ogProcessingSms;
	}

	public void setOgProcessingSms(BigInteger ogProcessingSms) {
		this.ogProcessingSms = ogProcessingSms;
	}

	public BigInteger getOgPartialDeliveredSeg() {
		return ogPartialDeliveredSeg;
	}

	public void setOgPartialDeliveredSeg(BigInteger ogPartialDeliveredSeg) {
		this.ogPartialDeliveredSeg = ogPartialDeliveredSeg;
	}

	public BigInteger getOgPartialDeliveredSms() {
		return ogPartialDeliveredSms;
	}

	public void setOgPartialDeliveredSms(BigInteger ogPartialDeliveredSms) {
		this.ogPartialDeliveredSms = ogPartialDeliveredSms;
	}

	public BigInteger getOgPartialReceivedSeg() {
		return ogPartialReceivedSeg;
	}

	public void setOgPartialReceivedSeg(BigInteger ogPartialReceivedSeg) {
		this.ogPartialReceivedSeg = ogPartialReceivedSeg;
	}

	public BigInteger getOgPartialReceivedSms() {
		return ogPartialReceivedSms;
	}

	public void setOgPartialReceivedSms(BigInteger ogPartialReceivedSms) {
		this.ogPartialReceivedSms = ogPartialReceivedSms;
	}

	public BigInteger getWeNewSeg() {
		return weNewSeg;
	}

	public void setWeNewSeg(BigInteger weNewSeg) {
		this.weNewSeg = weNewSeg;
	}

	public BigInteger getWeNewSms() {
		return weNewSms;
	}

	public void setWeNewSms(BigInteger weNewSms) {
		this.weNewSms = weNewSms;
	}

	public BigInteger getWeSubmittedSeg() {
		return weSubmittedSeg;
	}

	public void setWeSubmittedSeg(BigInteger weSubmittedSeg) {
		this.weSubmittedSeg = weSubmittedSeg;
	}

	public BigInteger getWeSubmittedSms() {
		return weSubmittedSms;
	}

	public void setWeSubmittedSms(BigInteger weSubmittedSms) {
		this.weSubmittedSms = weSubmittedSms;
	}

	public BigInteger getWeSentSeg() {
		return weSentSeg;
	}

	public void setWeSentSeg(BigInteger weSentSeg) {
		this.weSentSeg = weSentSeg;
	}

	public BigInteger getWeSentSms() {
		return weSentSms;
	}

	public void setWeSentSms(BigInteger weSentSms) {
		this.weSentSms = weSentSms;
	}

	public BigInteger getWeTimedOutSeg() {
		return weTimedOutSeg;
	}

	public void setWeTimedOutSeg(BigInteger weTimedOutSeg) {
		this.weTimedOutSeg = weTimedOutSeg;
	}

	public BigInteger getWeTimedOutSms() {
		return weTimedOutSms;
	}

	public void setWeTimedOutSms(BigInteger weTimedOutSms) {
		this.weTimedOutSms = weTimedOutSms;
	}

	public BigInteger getWeFailedToSendSeg() {
		return weFailedToSendSeg;
	}

	public void setWeFailedToSendSeg(BigInteger weFailedToSendSeg) {
		this.weFailedToSendSeg = weFailedToSendSeg;
	}

	public BigInteger getWeFailedToSendSms() {
		return weFailedToSendSms;
	}

	public void setWeFailedToSendSms(BigInteger weFailedToSendSms) {
		this.weFailedToSendSms = weFailedToSendSms;
	}

	public BigInteger getWeDeliveredSeg() {
		return weDeliveredSeg;
	}

	public void setWeDeliveredSeg(BigInteger weDeliveredSeg) {
		this.weDeliveredSeg = weDeliveredSeg;
	}

	public BigInteger getWeDeliveredSms() {
		return weDeliveredSms;
	}

	public void setWeDeliveredSms(BigInteger weDeliveredSms) {
		this.weDeliveredSms = weDeliveredSms;
	}

	public BigInteger getWeNotDeliveredSeg() {
		return weNotDeliveredSeg;
	}

	public void setWeNotDeliveredSeg(BigInteger weNotDeliveredSeg) {
		this.weNotDeliveredSeg = weNotDeliveredSeg;
	}

	public BigInteger getWeNotDeliveredSms() {
		return weNotDeliveredSms;
	}

	public void setWeNotDeliveredSms(BigInteger weNotDeliveredSms) {
		this.weNotDeliveredSms = weNotDeliveredSms;
	}

	public BigInteger getWeFailedSeg() {
		return weFailedSeg;
	}

	public void setWeFailedSeg(BigInteger weFailedSeg) {
		this.weFailedSeg = weFailedSeg;
	}

	public BigInteger getWeFailedSms() {
		return weFailedSms;
	}

	public void setWeFailedSms(BigInteger weFailedSms) {
		this.weFailedSms = weFailedSms;
	}

	public BigInteger getWeReceivedSeg() {
		return weReceivedSeg;
	}

	public void setWeReceivedSeg(BigInteger weReceivedSeg) {
		this.weReceivedSeg = weReceivedSeg;
	}

	public BigInteger getWeReceivedSms() {
		return weReceivedSms;
	}

	public void setWeReceivedSms(BigInteger weReceivedSms) {
		this.weReceivedSms = weReceivedSms;
	}

	public BigInteger getWeExpiredSeg() {
		return weExpiredSeg;
	}

	public void setWeExpiredSeg(BigInteger weExpiredSeg) {
		this.weExpiredSeg = weExpiredSeg;
	}

	public BigInteger getWeExpiredSms() {
		return weExpiredSms;
	}

	public void setWeExpiredSms(BigInteger weExpiredSms) {
		this.weExpiredSms = weExpiredSms;
	}

	public BigInteger getWeUnknownSeg() {
		return weUnknownSeg;
	}

	public void setWeUnknownSeg(BigInteger weUnknownSeg) {
		this.weUnknownSeg = weUnknownSeg;
	}

	public BigInteger getWeUnknownSms() {
		return weUnknownSms;
	}

	public void setWeUnknownSms(BigInteger weUnknownSms) {
		this.weUnknownSms = weUnknownSms;
	}

	public BigInteger getWeProcessingSeg() {
		return weProcessingSeg;
	}

	public void setWeProcessingSeg(BigInteger weProcessingSeg) {
		this.weProcessingSeg = weProcessingSeg;
	}

	public BigInteger getWeProcessingSms() {
		return weProcessingSms;
	}

	public void setWeProcessingSms(BigInteger weProcessingSms) {
		this.weProcessingSms = weProcessingSms;
	}

	public BigInteger getWePartialDeliveredSeg() {
		return wePartialDeliveredSeg;
	}

	public void setWePartialDeliveredSeg(BigInteger wePartialDeliveredSeg) {
		this.wePartialDeliveredSeg = wePartialDeliveredSeg;
	}

	public BigInteger getWePartialDeliveredSms() {
		return wePartialDeliveredSms;
	}

	public void setWePartialDeliveredSms(BigInteger wePartialDeliveredSms) {
		this.wePartialDeliveredSms = wePartialDeliveredSms;
	}

	public BigInteger getWePartialReceivedSeg() {
		return wePartialReceivedSeg;
	}

	public void setWePartialReceivedSeg(BigInteger wePartialReceivedSeg) {
		this.wePartialReceivedSeg = wePartialReceivedSeg;
	}

	public BigInteger getWePartialReceivedSms() {
		return wePartialReceivedSms;
	}

	public void setWePartialReceivedSms(BigInteger wePartialReceivedSms) {
		this.wePartialReceivedSms = wePartialReceivedSms;
	}

	public BigInteger getInterNewSeg() {
		return interNewSeg;
	}

	public void setInterNewSeg(BigInteger interNewSeg) {
		this.interNewSeg = interNewSeg;
	}

	public BigInteger getInterNewSms() {
		return interNewSms;
	}

	public void setInterNewSms(BigInteger interNewSms) {
		this.interNewSms = interNewSms;
	}

	public BigInteger getInterSubmittedSeg() {
		return interSubmittedSeg;
	}

	public void setInterSubmittedSeg(BigInteger interSubmittedSeg) {
		this.interSubmittedSeg = interSubmittedSeg;
	}

	public BigInteger getInterSubmittedSms() {
		return interSubmittedSms;
	}

	public void setInterSubmittedSms(BigInteger interSubmittedSms) {
		this.interSubmittedSms = interSubmittedSms;
	}

	public BigInteger getInterSentSeg() {
		return interSentSeg;
	}

	public void setInterSentSeg(BigInteger interSentSeg) {
		this.interSentSeg = interSentSeg;
	}

	public BigInteger getInterSentSms() {
		return interSentSms;
	}

	public void setInterSentSms(BigInteger interSentSms) {
		this.interSentSms = interSentSms;
	}

	public BigInteger getInterTimedOutSeg() {
		return interTimedOutSeg;
	}

	public void setInterTimedOutSeg(BigInteger interTimedOutSeg) {
		this.interTimedOutSeg = interTimedOutSeg;
	}

	public BigInteger getInterTimedOutSms() {
		return interTimedOutSms;
	}

	public void setInterTimedOutSms(BigInteger interTimedOutSms) {
		this.interTimedOutSms = interTimedOutSms;
	}

	public BigInteger getInterFailedToSendSeg() {
		return interFailedToSendSeg;
	}

	public void setInterFailedToSendSeg(BigInteger interFailedToSendSeg) {
		this.interFailedToSendSeg = interFailedToSendSeg;
	}

	public BigInteger getInterFailedToSendSms() {
		return interFailedToSendSms;
	}

	public void setInterFailedToSendSms(BigInteger interFailedToSendSms) {
		this.interFailedToSendSms = interFailedToSendSms;
	}

	public BigInteger getInterDeliveredSeg() {
		return interDeliveredSeg;
	}

	public void setInterDeliveredSeg(BigInteger interDeliveredSeg) {
		this.interDeliveredSeg = interDeliveredSeg;
	}

	public BigInteger getInterDeliveredSms() {
		return interDeliveredSms;
	}

	public void setInterDeliveredSms(BigInteger interDeliveredSms) {
		this.interDeliveredSms = interDeliveredSms;
	}

	public BigInteger getInterNotDeliveredSeg() {
		return interNotDeliveredSeg;
	}

	public void setInterNotDeliveredSeg(BigInteger interNotDeliveredSeg) {
		this.interNotDeliveredSeg = interNotDeliveredSeg;
	}

	public BigInteger getInterNotDeliveredSms() {
		return interNotDeliveredSms;
	}

	public void setInterNotDeliveredSms(BigInteger interNotDeliveredSms) {
		this.interNotDeliveredSms = interNotDeliveredSms;
	}

	public BigInteger getInterFailedSeg() {
		return interFailedSeg;
	}

	public void setInterFailedSeg(BigInteger interFailedSeg) {
		this.interFailedSeg = interFailedSeg;
	}

	public BigInteger getInterFailedSms() {
		return interFailedSms;
	}

	public void setInterFailedSms(BigInteger interFailedSms) {
		this.interFailedSms = interFailedSms;
	}

	public BigInteger getInterReceivedSeg() {
		return interReceivedSeg;
	}

	public void setInterReceivedSeg(BigInteger interReceivedSeg) {
		this.interReceivedSeg = interReceivedSeg;
	}

	public BigInteger getInterReceivedSms() {
		return interReceivedSms;
	}

	public void setInterReceivedSms(BigInteger interReceivedSms) {
		this.interReceivedSms = interReceivedSms;
	}

	public BigInteger getInterExpiredSeg() {
		return interExpiredSeg;
	}

	public void setInterExpiredSeg(BigInteger interExpiredSeg) {
		this.interExpiredSeg = interExpiredSeg;
	}

	public BigInteger getInterExpiredSms() {
		return interExpiredSms;
	}

	public void setInterExpiredSms(BigInteger interExpiredSms) {
		this.interExpiredSms = interExpiredSms;
	}

	public BigInteger getInterUnknownSeg() {
		return interUnknownSeg;
	}

	public void setInterUnknownSeg(BigInteger interUnknownSeg) {
		this.interUnknownSeg = interUnknownSeg;
	}

	public BigInteger getInterUnknownSms() {
		return interUnknownSms;
	}

	public void setInterUnknownSms(BigInteger interUnknownSms) {
		this.interUnknownSms = interUnknownSms;
	}

	public BigInteger getInterProcessingSeg() {
		return interProcessingSeg;
	}

	public void setInterProcessingSeg(BigInteger interProcessingSeg) {
		this.interProcessingSeg = interProcessingSeg;
	}

	public BigInteger getInterProcessingSms() {
		return interProcessingSms;
	}

	public void setInterProcessingSms(BigInteger interProcessingSms) {
		this.interProcessingSms = interProcessingSms;
	}

	public BigInteger getInterPartialDeliveredSeg() {
		return interPartialDeliveredSeg;
	}

	public void setInterPartialDeliveredSeg(BigInteger interPartialDeliveredSeg) {
		this.interPartialDeliveredSeg = interPartialDeliveredSeg;
	}

	public BigInteger getInterPartialDeliveredSms() {
		return interPartialDeliveredSms;
	}

	public void setInterPartialDeliveredSms(BigInteger interPartialDeliveredSms) {
		this.interPartialDeliveredSms = interPartialDeliveredSms;
	}

	public BigInteger getInterPartialReceivedSeg() {
		return interPartialReceivedSeg;
	}

	public void setInterPartialReceivedSeg(BigInteger interPartialReceivedSeg) {
		this.interPartialReceivedSeg = interPartialReceivedSeg;
	}

	public BigInteger getInterPartialReceivedSms() {
		return interPartialReceivedSms;
	}

	public void setInterPartialReceivedSms(BigInteger interPartialReceivedSms) {
		this.interPartialReceivedSms = interPartialReceivedSms;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((collectionStatsPK == null) ? 0 : collectionStatsPK.hashCode());
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
		CollectionStats other = (CollectionStats) obj;
		if (collectionStatsPK == null) {
			if (other.collectionStatsPK != null)
				return false;
		} else if (!collectionStatsPK.equals(other.collectionStatsPK))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{collectionStatsPK=").append(collectionStatsPK).append(", vfNewSeg=").append(vfNewSeg)
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
