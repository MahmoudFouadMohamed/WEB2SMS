package com.edafa.web2sms.sms.model;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.sms.enums.ResultStatus;

@XmlType(name = "ExpiredResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "ExpiredResponse", namespace = "http://www.edafa.com/web2sms/sms/model/")
public class ExpiredResponse {

    @XmlElement(name = "ResultStatus")
    protected ResultStatus resultStatus;

    @XmlElement(name = "Description")
    protected String description;

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
        StringBuilder str = new StringBuilder("ExpiredResponse{resultStatus=");
        str = str.append(resultStatus);
        if (description != null) {
            str = str.append(", description=")
                    .append(description);
        }
        str = str.append('}');
        return str.toString();
    }

}
