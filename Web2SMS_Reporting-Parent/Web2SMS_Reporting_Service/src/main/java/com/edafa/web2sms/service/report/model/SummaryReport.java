package com.edafa.web2sms.service.report.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "SummaryReport", namespace = "http://www.edafa.com/web2sms/service/model/")
public class SummaryReport {

	@XmlElement(required = true, nillable = false)
	private int totalSMSCount;
	@XmlElement(required = true, nillable = false)
	private int totalSMSSegCount;
	@XmlElement(required = true, nillable = false)
	private int deliverdSMSCount;
	@XmlElement(required = true, nillable = false)
	private int deliverdSMSSegCount;
	@XmlElement(required = true, nillable = false)
	private int unDeliverdSMSCount;
	@XmlElement(required = true, nillable = false)
	private int unDeliverdSMSSegCount;
	@XmlElement(required = true, nillable = false)
	private int pendingSMSCount;
	@XmlElement(required = true, nillable = false)
	private int pendingSMSSegCount;
	@XmlElement(required = true, nillable = false)
	private int failedSMSCount;
	@XmlElement(required = true, nillable = false)
	private int failedSMSSegCount;

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

	public SummaryReport() {
		totalSMSCount = 0;
		totalSMSSegCount = 0;
		deliverdSMSCount = 0;
		deliverdSMSSegCount = 0;
		unDeliverdSMSCount = 0;
		unDeliverdSMSSegCount = 0;
		pendingSMSCount = 0;
		pendingSMSSegCount = 0;
		failedSMSCount = 0;
		failedSMSSegCount = 0;
	}

	public int getTotalSMSCount() {
		return totalSMSCount;
	}

	public void setTotalSMSCount(int totalSMSCount) {
		this.totalSMSCount = totalSMSCount;
	}

	public int getTotalSMSSegCount() {
		return totalSMSSegCount;
	}

	public void setTotalSMSSegCount(int totalSMSSegCount) {
		this.totalSMSSegCount = totalSMSSegCount;
	}

	public int getDeliverdSMSCount() {
		return deliverdSMSCount;
	}

	public void setDeliverdSMSCount(int deliverdSMSCount) {
		this.deliverdSMSCount = deliverdSMSCount;
	}

	public int getDeliverdSMSSegCount() {
		return deliverdSMSSegCount;
	}

	public void setDeliverdSMSSegCount(int deliverdSMSSegCount) {
		this.deliverdSMSSegCount = deliverdSMSSegCount;
	}

	public int getUnDeliverdSMSCount() {
		return unDeliverdSMSCount;
	}

	public void setUnDeliverdSMSCount(int unDeliverdSMSCount) {
		this.unDeliverdSMSCount = unDeliverdSMSCount;
	}

	public int getUnDeliverdSMSSegCount() {
		return unDeliverdSMSSegCount;
	}

	public void setUnDeliverdSMSSegCount(int unDeliverdSMSSegCount) {
		this.unDeliverdSMSSegCount = unDeliverdSMSSegCount;
	}

	public int getPendingSMSCount() {
		return pendingSMSCount;
	}

	public void setPendingSMSCount(int pendingSMSCount) {
		this.pendingSMSCount = pendingSMSCount;
	}

	public int getPendingSMSSegCount() {
		return pendingSMSSegCount;
	}

	public void setPendingSMSSegCount(int pendingSMSSegCount) {
		this.pendingSMSSegCount = pendingSMSSegCount;
	}

	public int getFailedSMSCount() {
		return failedSMSCount;
	}

	public void setFailedSMSCount(int failedSMSCount) {
		this.failedSMSCount = failedSMSCount;
	}

	public int getFailedSMSSegCount() {
		return failedSMSSegCount;
	}

	public void setFailedSMSSegCount(int failedSMSSegCount) {
		this.failedSMSSegCount = failedSMSSegCount;
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

	public void incSmsSent(int vfSentSms, int ogSentSms, int etSentSms, int weSentSms, int interSentSms) {
		this.vfSentSms += vfSentSms;
		this.ogSentSms += ogSentSms;
		this.etSentSms += etSentSms;
		this.weSentSms += weSentSms;
		this.interSentSms += interSentSms;
	}

	public void incSmsDelivered(int vfDeliveredSms, int ogDeliveredSms, int etDeliveredSms, int weDeliveredSms,
			int interDeliveredSms) {
		this.vfDeliveredSms += vfDeliveredSms;
		this.ogDeliveredSms += ogDeliveredSms;
		this.etDeliveredSms += etDeliveredSms;
		this.weDeliveredSms += weDeliveredSms;
		this.interDeliveredSms += interDeliveredSms;
	}

	public void incSmsNotDelivered(int vfNotDeliveredSms, int ogNotDeliveredSms, int etNotDeliveredSms,
			int weNotDeliveredSms, int interNotDeliveredSms) {
		this.vfNotDeliveredSms += vfNotDeliveredSms;
		this.ogNotDeliveredSms += ogNotDeliveredSms;
		this.etNotDeliveredSms += etNotDeliveredSms;
		this.weNotDeliveredSms += weNotDeliveredSms;
		this.interNotDeliveredSms += interNotDeliveredSms;
	}

	public void incSegmentsSent(int vfSentSegments, int ogSentSegments, int etSentSegments, int weSentSegments,
			int interSentSegments) {
		this.vfSentSegments += vfSentSegments;
		this.ogSentSegments += ogSentSegments;
		this.etSentSegments += etSentSegments;
		this.weSentSegments += weSentSegments;
		this.interSentSegments += interSentSegments;
	}

	public void incSegmentsDelivered(int vfDeliveredSegments, int ogDeliveredSegments, int etDeliveredSegments,
			int weDeliveredSegments, int interDeliveredSegments) {
		this.vfDeliveredSegments += vfDeliveredSegments;
		this.ogDeliveredSegments += ogDeliveredSegments;
		this.etDeliveredSegments += etDeliveredSegments;
		this.weDeliveredSegments += weDeliveredSegments;
		this.interDeliveredSegments += interDeliveredSegments;
	}

	public void incSegmentsNotDelivered(int vfNotDeliveredSegments, int ogNotDeliveredSegments,
			int etNotDeliveredSegments, int weNotDeliveredSegments, int interNotDeliveredSegments) {
		this.vfNotDeliveredSegments += vfNotDeliveredSegments;
		this.ogNotDeliveredSegments += ogNotDeliveredSegments;
		this.etNotDeliveredSegments += etNotDeliveredSegments;
		this.weNotDeliveredSegments += weNotDeliveredSegments;
		this.interNotDeliveredSegments += interNotDeliveredSegments;
	}

	public void incTotalSMSCount(int totalSMSCount) {
		this.totalSMSCount += totalSMSCount;
	}

	public void incTotalSMSSegCount(int totalSMSSegCount) {
		this.totalSMSSegCount += totalSMSSegCount;
	}

	public void incDeliverdSMSCount(int deliverdSMSCount) {
		this.deliverdSMSCount += deliverdSMSCount;
	}

	public void incDeliverdSMSSegCount(int deliverdSMSSegCount) {
		this.deliverdSMSSegCount += deliverdSMSSegCount;
	}

	public void incUnDeliverdSMSCount(int unDeliverdSMSCount) {
		this.unDeliverdSMSCount += unDeliverdSMSCount;
	}

	public void incUnDeliverdSMSSegCount(int unDeliverdSMSSegCount) {
		this.unDeliverdSMSSegCount += unDeliverdSMSSegCount;
	}

	public void incPendingSMSCount(int pendingSMSCount) {
		this.pendingSMSCount += pendingSMSCount;
	}

	public void incPendingSMSSegCount(int pendingSMSSegCount) {
		this.pendingSMSSegCount += pendingSMSSegCount;
	}

	public void incFailedSMSCount(int failedSMSCount) {
		this.failedSMSCount += failedSMSCount;
	}

	public void incFailedSMSSegCount(int failedSMSSegCount) {
		this.failedSMSSegCount += failedSMSSegCount;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{totalSMSCount=").append(totalSMSCount).append(", totalSMSSegCount=").append(totalSMSSegCount)
				.append(", deliverdSMSCount=").append(deliverdSMSCount).append(", deliverdSMSSegCount=")
				.append(deliverdSMSSegCount).append(", unDeliverdSMSCount=").append(unDeliverdSMSCount)
				.append(", unDeliverdSMSSegCount=").append(unDeliverdSMSSegCount).append(", pendingSMSCount=")
				.append(pendingSMSCount).append(", pendingSMSSegCount=").append(pendingSMSSegCount)
				.append(", failedSMSCount=").append(failedSMSCount).append(", failedSMSSegCount=")
				.append(failedSMSSegCount).append(", vfSentSms=").append(vfSentSms).append(", ogSentSms=")
				.append(ogSentSms).append(", etSentSms=").append(etSentSms).append(", weSentSms=").append(weSentSms)
				.append(", interSentSms=").append(interSentSms).append(", vfDeliveredSms=").append(vfDeliveredSms)
				.append(", ogDeliveredSms=").append(ogDeliveredSms).append(", etDeliveredSms=").append(etDeliveredSms)
				.append(", weDeliveredSms=").append(weDeliveredSms).append(", interDeliveredSms=")
				.append(interDeliveredSms).append(", vfNotDeliveredSms=").append(vfNotDeliveredSms)
				.append(", ogNotDeliveredSms=").append(ogNotDeliveredSms).append(", etNotDeliveredSms=")
				.append(etNotDeliveredSms).append(", weNotDeliveredSms=").append(weNotDeliveredSms)
				.append(", interNotDeliveredSms=").append(interNotDeliveredSms).append(", vfSentSegments=")
				.append(vfSentSegments).append(", ogSentSegments=").append(ogSentSegments).append(", etSentSegments=")
				.append(etSentSegments).append(", weSentSegments=").append(weSentSegments)
				.append(", interSentSegments=").append(interSentSegments).append(", vfDeliveredSegments=")
				.append(vfDeliveredSegments).append(", ogDeliveredSegments=").append(ogDeliveredSegments)
				.append(", etDeliveredSegments=").append(etDeliveredSegments).append(", weDeliveredSegments=")
				.append(weDeliveredSegments).append(", interDeliveredSegments=").append(interDeliveredSegments)
				.append(", vfNotDeliveredSegments=").append(vfNotDeliveredSegments).append(", ogNotDeliveredSegments=")
				.append(ogNotDeliveredSegments).append(", etNotDeliveredSegments=").append(etNotDeliveredSegments)
				.append(", weNotDeliveredSegments=").append(weNotDeliveredSegments)
				.append(", interNotDeliveredSegments=").append(interNotDeliveredSegments).append("}");
		return builder.toString();
	}

}
