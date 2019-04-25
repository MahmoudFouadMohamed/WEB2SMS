package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignStatus", namespace = "http://www.edafa.com/web2sms/service/model/enums")
@XmlEnum
@XmlAccessorType(XmlAccessType.FIELD)
public enum CampaignStatusName {
	NEW("New"),
	WAITING_APPROVAL("Waiting Approval"),
	APPROVED("Approved"),
	RUNNING("Running"),
	PARTIAL_RUN("Partial Run"),
	PAUSED("Paused"),
	CANCELLED("Cancelled"),
	FINISHED("Finished"),
	FAILED("Failed"),
	OBSOLETE("Obsoleted"),
	APPROVAL_OBSOLETE("Approval Obsoleted"),
	SEND_OBSOLETE("Send Obsoleted"),
	REJECTED("Rejected"),
	ON_HOLD("On Hold"),
	DELETED("Deleted"),
	UNKNOWN("Unknown");

	String statusName;

	private CampaignStatusName(String name) {
		this.statusName = name;
	}

	public String getStatusName() {
		return statusName;
	}

	public void setStatusName(String name) {
		this.statusName = name;
	}

	public String getName() {
		return this.name();
	}

	@Override
	public String toString() {
		return getStatusName();
	}
}
