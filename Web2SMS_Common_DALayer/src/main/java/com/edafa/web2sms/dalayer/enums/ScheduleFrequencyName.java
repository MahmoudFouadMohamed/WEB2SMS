package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ScheduleFrequency", namespace = "http://www.edafa.com/web2sms/service/model/enums/")
@XmlEnum
public enum ScheduleFrequencyName {
	ONCE, DAILY, WEEKLY, MONTHLY, YEARLY, UNKNOWN;
}
