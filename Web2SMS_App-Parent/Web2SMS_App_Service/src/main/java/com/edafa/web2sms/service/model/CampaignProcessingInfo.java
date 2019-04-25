package com.edafa.web2sms.service.model;

import java.util.Date;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "CampaignProcessingInfo", namespace = "http://www.edafa.com/web2sms/service/model/")
public class CampaignProcessingInfo {

	@XmlElement(required = true, nillable = false)
	String status;

	@XmlElement(required = true, nillable = true)
	Date startDate;

	@XmlElement(required = true, nillable = true)
	Date endDate;

	
}
