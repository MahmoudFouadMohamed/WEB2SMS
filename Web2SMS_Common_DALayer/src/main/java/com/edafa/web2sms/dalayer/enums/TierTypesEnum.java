package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "TierTypes", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum TierTypesEnum {

	POSTPAID, //normal
	PREPAID; //one_off

}
