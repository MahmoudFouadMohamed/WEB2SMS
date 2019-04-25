package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Tier", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class TierModel implements Serializable {
	@XmlElement(required = true, nillable = false)
	Integer tierId;
	@XmlElement(required = true, nillable = false)
	String tierName;
	@XmlElement(required = true, nillable = false)
	Integer quota;
	@XmlElement(required = true, nillable = true)
	String description;
	@XmlElement(required = true, nillable = true)
	String rateplan;

	public Integer getTierId() {
		return tierId;
	}

	public void setTierId(Integer tierId) {
		this.tierId = tierId;
	}

	public String getTierName() {
		return tierName;
	}

	public void setTierName(String tierName) {
		this.tierName = tierName;
	}

	public Integer getQuota() {
		return quota;
	}

	public void setQuota(Integer qouta) {
		this.quota = qouta;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRateplan() {
		return rateplan;
	}

	public void setRateplan(String rateplan) {
		this.rateplan = rateplan;
	}

	@XmlTransient
	public boolean isValid() {
		return !(tierId == null || tierId == 0);
	}

}
