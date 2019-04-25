package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "MonthName", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum MonthName {
	JAN, FEB, MAR, APR, MAY, JUNE, JULY, AUG, SEPT, OCT, NOV, DEC;

}
