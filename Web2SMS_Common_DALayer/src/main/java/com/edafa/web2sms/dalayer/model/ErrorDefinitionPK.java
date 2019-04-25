package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the ALARM_DEFINITIONS database table.
 * 
 */
@Embeddable
public class ErrorDefinitionPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="APP_ERROR_ID", insertable=false, updatable=false, unique=true, nullable=false)
	private int appErrorId;

	@Column(unique=true, nullable=false)
	private int severity;

	public ErrorDefinitionPK() {
	}
	public int getAppErrorId() {
		return this.appErrorId;
	}
	public void setAppErrorId(int alarmId) {
		this.appErrorId = alarmId;
	}
	public int getSeverity() {
		return this.severity;
	}
	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ErrorDefinitionPK)) {
			return false;
		}
		ErrorDefinitionPK castOther = (ErrorDefinitionPK)other;
		return 
			(this.appErrorId == castOther.appErrorId)
			&& (this.severity == castOther.severity);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.appErrorId ^ (this.appErrorId >>> 32)));
		hash = hash * prime + ((int) (this.severity ^ (this.severity >>> 32)));
		
		return hash;
	}
}