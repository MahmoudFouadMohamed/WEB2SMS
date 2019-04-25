/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.model;

import com.edafa.web2sms.service.enums.ResponseStatus;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author mahmoud
 */
@XmlType(name = "CountResult", namespace = "http://www.edafa.com/web2sms/service/model/")
public class CountResult extends ResultStatus {
    @XmlElement
    Integer count;

    public CountResult() {
    }

    public CountResult(ResponseStatus status) {
        super(status);
    }

    public CountResult(ResponseStatus status, String errorMessage) {
        super(status, errorMessage);
    }    
    
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "CountResult{" + "count=" + count + '}';
    }
    
    
}
