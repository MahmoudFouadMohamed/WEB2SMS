package com.edafa.web2sms.utils.configs.enums;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "Module")
@XmlEnum
public enum ModulesEnum {
	CoreUtils, CampaignEngine, SMSAPIEngine, AggCDRConfig, DetailedCDRConfig

	;

	private String name;
	private boolean configsApplied = true;

	ModulesEnum() {
		this.name = name();
	}

	ModulesEnum(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	/**
	 * @return the configsApplied
	 */
	public boolean isConfigsApplied() {
		return configsApplied;
	}

	/**
	 * @param configsApplied
	 *            the configsApplied to set
	 */
	public void setConfigsApplied(boolean configsApplied) {
		this.configsApplied = configsApplied;
	}

	@Override
	public String toString() {
		return name;
	}
}
