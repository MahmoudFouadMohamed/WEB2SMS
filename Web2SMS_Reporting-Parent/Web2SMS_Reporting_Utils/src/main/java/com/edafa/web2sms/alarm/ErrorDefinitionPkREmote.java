package com.edafa.web2sms.alarm;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.ErrorDefinitionPK;

@XmlType(name = "ErrorDefinitionPK")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ErrorDefinitionPK")
public class ErrorDefinitionPkREmote {

	@XmlElement(name = "appErrorId")
	private int appErrorId;

	@XmlElement(name = "severity")
	private int severity;

	public ErrorDefinitionPkREmote() {}

	public ErrorDefinitionPkREmote(ErrorDefinitionPK errorDefinitionPK) {
		this.appErrorId = errorDefinitionPK.getAppErrorId();
		this.severity = errorDefinitionPK.getSeverity();
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

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ErrorDefinitionPkREmote)) {
			return false;
		}
		ErrorDefinitionPkREmote castOther = (ErrorDefinitionPkREmote) other;
		return (this.appErrorId == castOther.appErrorId) && (this.severity == castOther.severity);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + (this.appErrorId ^ (this.appErrorId >>> 32));
		hash = hash * prime + (this.severity ^ (this.severity >>> 32));

		return hash;
	}

	@Override
	public String toString() {
		return "ErrorDefinitionPkREmote{" + "appErrorId=" + appErrorId + ", severity=" + severity + '}';
	}
}
