package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the ALARMS database table.
 * 
 */
@Entity
@Table(name = "APP_ERRORS")
@NamedQuery(name = "AppErrorEntity.findAll", query = "SELECT a FROM AppErrorEntity a")
public class AppErrorEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "APP_ERROR_ID", unique = true, nullable = false)
	private Integer appErrorId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	// bi-directional many-to-one association to ErrorDefinitionEntity
	@OneToMany(mappedBy = "appErrorEntity")
	private List<ErrorDefinitionEntity> errorDefinitionEntities;

	public AppErrorEntity() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getAppErrorId() {
		return this.appErrorId;
	}

	public void setAppErrorId(Integer alarmId) {
		this.appErrorId = alarmId;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<ErrorDefinitionEntity> getAlarmDefinitions() {
		return this.errorDefinitionEntities;
	}

	public void setAlarmDefinitions(List<ErrorDefinitionEntity> errorDefinitionEntities) {
		this.errorDefinitionEntities = errorDefinitionEntities;
	}

	public ErrorDefinitionEntity addAlarmDefinition(ErrorDefinitionEntity errorDefinitionEntity) {
		getAlarmDefinitions().add(errorDefinitionEntity);
		errorDefinitionEntity.setAlarm(this);

		return errorDefinitionEntity;
	}

	public ErrorDefinitionEntity removeAlarmDefinition(ErrorDefinitionEntity errorDefinitionEntity) {
		getAlarmDefinitions().remove(errorDefinitionEntity);
		errorDefinitionEntity.setAlarm(null);

		return errorDefinitionEntity;
	}

}