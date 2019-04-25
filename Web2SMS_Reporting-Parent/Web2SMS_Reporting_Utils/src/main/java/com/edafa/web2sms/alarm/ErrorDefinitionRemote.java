package com.edafa.web2sms.alarm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.ErrorDefinitionEntity;

/**
 * The persistent class for the ALARM_DEFINITIONS database table.
 *
 */
@XmlType(name = "ErrorDefinitionRemote")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ErrorDefinitionRemote")
public class ErrorDefinitionRemote {

	@XmlElement(name = "ErrorDefinitionPK")
	private ErrorDefinitionPkREmote id;

	@XmlElement(name = "AppErrorRemote")
	private AppErrorRemote appErrorRemote;

	@XmlElement(name = "monitorPeriod")
	private long monitorPeriod;

	@XmlElement(name = "raisingPeriod")
	private long raisingPeriod;

	@XmlElement(name = "threshold")
	private long threshold;

	public ErrorDefinitionRemote() {}

	public ErrorDefinitionRemote(ErrorDefinitionEntity errorDefinitionEntity) {
		this.id = new ErrorDefinitionPkREmote(errorDefinitionEntity.getId());
		this.appErrorRemote = new AppErrorRemote(errorDefinitionEntity.getAlarm());
		this.monitorPeriod = errorDefinitionEntity.getMonitorPeriod();
		this.raisingPeriod = errorDefinitionEntity.getRaisingPeriod();
		this.threshold = errorDefinitionEntity.getThreshold();
	}

	public ErrorDefinitionPkREmote getId() {
		return this.id;
	}

	public void setId(ErrorDefinitionPkREmote id) {
		this.id = id;
	}

	public long getMonitorPeriod() {
		return this.monitorPeriod;
	}

	public void setMonitorPeriod(long monitorPeriod) {
		this.monitorPeriod = monitorPeriod;
	}

	public long getRaisingPeriod() {
		return raisingPeriod;
	}

	public void setRaisingPeriod(long raisingPeriod) {
		this.raisingPeriod = raisingPeriod;
	}

	public long getThreshold() {
		return this.threshold;
	}

	public void setThreshold(long threshold) {
		this.threshold = threshold;
	}

	public AppErrorRemote getAlarm() {
		return this.appErrorRemote;
	}

	public void setAlarm(AppErrorRemote appErrorEntity) {
		this.appErrorRemote = appErrorEntity;
	}

	@Override
	public String toString() {
		return "ErrorDefinitionRemote{" + "id=" + id + ", appErrorRemote=" + appErrorRemote + ", monitorPeriod="
				+ monitorPeriod + ", raisingPeriod=" + raisingPeriod + ", threshold=" + threshold + '}';
	}

}
