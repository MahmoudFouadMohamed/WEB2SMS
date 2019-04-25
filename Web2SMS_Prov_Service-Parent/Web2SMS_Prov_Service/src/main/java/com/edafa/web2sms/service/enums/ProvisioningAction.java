package com.edafa.web2sms.service.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum ProvisioningAction {
	ACTIVATE, DEACTIVATE, SUSPEND, REACTIVATE, DELETE, DOWNGRADE, UPGRADE;
}
