/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.config.model;

import com.edafa.web2sms.service.enums.ResponseStatus;
import com.edafa.web2sms.service.model.ResultStatus;
import com.edafa.web2sms.utils.configs.model.Config;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 *
 * @author mahmoud
 */
@XmlType(name = "ConfigResultSet", namespace = "http://www.edafa.com/service/config/model/")
public class ConfigResultSet extends ResultStatus {

    @XmlElement(name = "Config", required = true, nillable = false)
    private List<Config> configs;

    public ConfigResultSet() {
    }

    public ConfigResultSet(ResponseStatus status) {
        super(status);
    }

    public List<Config> getConfigs() {
        return configs;
    }

    public void setConfigs(List<Config> configs) {
        this.configs = configs;
    }
}
