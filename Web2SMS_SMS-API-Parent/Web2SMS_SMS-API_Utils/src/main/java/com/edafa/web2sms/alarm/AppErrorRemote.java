package com.edafa.web2sms.alarm;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * The persistent class for the ALARMS database table.
 *
 */
@XmlType(name = "AppErrorRemote")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "AppErrorRemote")
public class AppErrorRemote {

    @XmlElement(name = "appErrorId")
    private Integer appErrorId;

    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "description")
    private String description;

    public AppErrorRemote() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAppErrorId() {
        return this.appErrorId;
    }

    public void setAppErrorId(Integer alarmId) {
        this.appErrorId = alarmId;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "AppErrorRemote{" + "appErrorId=" + appErrorId + ", name=" + name + ", description=" + description + '}';
    }
}
