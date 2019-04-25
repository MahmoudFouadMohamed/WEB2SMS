package com.edafa.web2sms.sms.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ResultStatus", namespace = "http://www.edafa.com/web2sms/service/enums/")
@XmlEnum
public enum ResultStatus {

    SUCCESS, INVALID_REQUEST, INTERNAL_SERVER_ERROR, GENERIC_ERROR, RESEND_CACHE_REQUEST_FAILED;

}
