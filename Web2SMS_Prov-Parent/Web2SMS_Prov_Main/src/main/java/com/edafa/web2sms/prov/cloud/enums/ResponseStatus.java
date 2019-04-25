package com.edafa.web2sms.prov.cloud.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResponseStatus")
@XmlEnum
public enum ResponseStatus {

	SUCCESS, FAIL, INVALID_REQUEST;
}
