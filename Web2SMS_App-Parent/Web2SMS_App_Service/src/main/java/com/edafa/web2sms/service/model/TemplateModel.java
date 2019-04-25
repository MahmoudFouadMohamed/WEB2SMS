package com.edafa.web2sms.service.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;

@XmlType(name = "Template", namespace = "http://www.edafa.com/web2sms/service/model/")
public class TemplateModel {

	@XmlElement(required = true, nillable = false)
	Integer templateId;
	@XmlElement(required = true, nillable = false)
	String templateName;
	@XmlElement(required = true, nillable = false)
	Boolean templateType;
	@XmlElement(required = true, nillable = false)
	String text;
//	@XmlElement(required = true, nillable = false)
//	int textLength;
	@XmlElement(required = true, nillable = false)
	private LanguageNameEnum language;

	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	public Integer getTemplateId() {
		return templateId;
	}
	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Boolean getTemplateType() {
		return templateType;
	}
	public void setTemplateType(Boolean templateType) {
		this.templateType = templateType;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public LanguageNameEnum getLanguage() {
		return language;
	}
	public void setLanguage(LanguageNameEnum language) {
		this.language = language;
	}

}
