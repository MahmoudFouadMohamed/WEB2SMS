package com.edafa.web2sms.sms.model;



import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;




@XmlType(name = "EnquireSMSsByDatesResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "EnquireSMSsByDatesResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class EnquireSMSsByDatesResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -241509849331562884L;
	
	@XmlElement(name = "smsApiDelivered")
	protected Integer smsApiDelivered;
	@XmlElement(name = "smsApiUnDelivered")
	protected Integer smsApiUnDelivered;
	@XmlElement(name = "apiCampDelivered")
	protected Integer apiCampDelivered;
	@XmlElement(name = "apiCampUnDelivered")
	protected Integer apiCampUnDelivered;
	
	@XmlElement(name = "ResultStatus")
	protected ResultStatus resultStatus;
	
	@XmlElement(name = "Description")
	protected String description;

	public EnquireSMSsByDatesResponse() {
	}

	public EnquireSMSsByDatesResponse(Integer smsApiDelivered, Integer smsApiUnDelivered,
			Integer apiCampDelivered, Integer apiCampUnDelivered,
			ResultStatus resultStatus, String description) {
		super();
		this.smsApiDelivered = smsApiDelivered;
		this.smsApiUnDelivered = smsApiUnDelivered;
		this.apiCampDelivered = apiCampDelivered;
		this.apiCampUnDelivered = apiCampUnDelivered;
		this.resultStatus = resultStatus;
		this.description = description;
	}

	public Integer getSmsApiDelivered() {
		return smsApiDelivered;
	}

	public void setSmsApiDelivered(Integer smsApiDelivered) {
		this.smsApiDelivered = smsApiDelivered;
	}

	public Integer getSmsApiUnDelivered() {
		return smsApiUnDelivered;
	}

	public void setSmsApiUnDelivered(Integer smsApiUnDelivered) {
		this.smsApiUnDelivered = smsApiUnDelivered;
	}

	public Integer getApiCampDelivered() {
		return apiCampDelivered;
	}

	public void setApiCampDelivered(Integer apiCampDelivered) {
		this.apiCampDelivered = apiCampDelivered;
	}

	public Integer getApiCampUnDelivered() {
		return apiCampUnDelivered;
	}

	public void setApiCampUnDelivered(Integer apiCampUnDelivered) {
		this.apiCampUnDelivered = apiCampUnDelivered;
	}

	public ResultStatus getResultStatus() {
		return resultStatus;
	}

	public void setResultStatus(ResultStatus resultStatus) {
		this.resultStatus = resultStatus;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EnquireSMSsByDatesResponse [smsApiDelivered=");
		builder.append(smsApiDelivered);
		builder.append(", smsApiUnDelivered=");
		builder.append(smsApiUnDelivered);
		builder.append(", apiCampDelivered=");
		builder.append(apiCampDelivered);
		builder.append(", apiCampUnDelivered=");
		builder.append(apiCampUnDelivered);
		builder.append(", resultStatus=");
		builder.append(resultStatus);
            if (description != null) {
                builder.append(", description=");
                builder.append(description);
            }
		builder.append("]");
		return builder.toString();
	}



}
