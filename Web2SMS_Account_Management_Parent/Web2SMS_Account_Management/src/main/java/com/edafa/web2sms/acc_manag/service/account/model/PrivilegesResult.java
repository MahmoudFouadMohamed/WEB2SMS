/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.model;

import com.edafa.web2sms.acc_manag.service.enums.ResponseStatus;
import com.edafa.web2sms.acc_manag.service.model.PrivilegeModel;
import com.edafa.web2sms.acc_manag.service.model.ResultStatus;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author mahmoud
 */
@XmlType(name = "PrivilegesResult", namespace = "http://www.edafa.com/web2sms/service/acc_manag/account/model")
public class PrivilegesResult extends ResultStatus {
    
    	@XmlElement(name = "Privilege", required = true, nillable = false)
        List<PrivilegeModel> privilegesList;

    public PrivilegesResult() {
    }
        
    public PrivilegesResult(ResponseStatus status) {
        super(status);
    }

    public PrivilegesResult(ResponseStatus status, String errorMessage) {
        super(status, errorMessage);
    }

    public void setPrivilegesList(List<PrivilegeModel> privilegesList) {
        this.privilegesList = privilegesList;
    }

    public List<PrivilegeModel> getPrivilegesList() {
        return privilegesList;
    }
}
