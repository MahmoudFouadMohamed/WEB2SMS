package com.edafa.web2sms.dalayer.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ListType", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum ListTypeName {

	NORMAL_LIST, TEMP_LIST, VIRTUAL_LIST, INTRA_LIST, PRENORMAL_LIST,
	INTRA_SUB_LIST, PRECUSTOMIZED_LIST, CUSTOMIZED_LIST, PRE_INTRA_SUB_LIST,
	// Default
	
	UNKNOWN

	;

}
