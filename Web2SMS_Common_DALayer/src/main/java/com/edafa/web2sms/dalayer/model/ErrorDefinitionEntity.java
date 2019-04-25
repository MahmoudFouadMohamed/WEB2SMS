package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the ALARM_DEFINITIONS database table.
 * 
 */
@Entity
@Table(name = "APP_ERRORS_DEFINITIONS")
@NamedQueries({ @NamedQuery(name = "ErrorDefinitionEntity.findAll", query = "SELECT a FROM ErrorDefinitionEntity a"),
// @NamedQuery(name = "ErrorDefinitionEntity.findByModule", query =
// "SELECT a FROM ErrorDefinitionEntity a WHERE a.module.name = :moduleName")
})
public class ErrorDefinitionEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ErrorDefinitionPK id;

	// bi-directional many-to-one association to AppErrorEntity
	@ManyToOne
	@JoinColumn(name = "APP_ERROR_ID")
	private AppErrorEntity appErrorEntity;

	@Column(name = "MONITOR_PERIOD")
	private long monitorPeriod;

	@Column(name = "RAISING_PERIOD")
	private long raisingPeriod;

	@Column(nullable = false)
	private long threshold;

	public ErrorDefinitionEntity() {
	}

	public ErrorDefinitionPK getId() {
		return this.id;
	}

	public void setId(ErrorDefinitionPK id) {
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

	public AppErrorEntity getAlarm() {
		return this.appErrorEntity;
	}

	public void setAlarm(AppErrorEntity appErrorEntity) {
		this.appErrorEntity = appErrorEntity;
	}

}