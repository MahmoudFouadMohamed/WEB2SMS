/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.LanguageNameEnum;
import com.edafa.web2sms.dalayer.model.constants.LanguageConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "LANGUAGES")
@XmlRootElement
@ObjectTypeConverter(name = "LanguageConverter", dataType = String.class, objectType = LanguageNameEnum.class, defaultObjectValue = "UNKNOWN", conversionValues = {
		@ConversionValue(dataValue = "ENGLISH", objectValue = "ENGLISH"), @ConversionValue(dataValue = "ARABIC", objectValue = "ARABIC")})
@NamedQueries({@NamedQuery(name = "Language.findAll", query = "SELECT l FROM Language l"),
		@NamedQuery(name = "Language.findByLanguageId", query = "SELECT l FROM Language l WHERE l.languageId = :languageId"),
		@NamedQuery(name = "Language.findByLanguageName", query = "SELECT l FROM Language l WHERE l.languageName = :languageName")})
public class Language implements Serializable, LanguageConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "LANGUAGE_ID")
	private Integer languageId;
	@Basic(optional = false)
	@Column(name = "LANGUAGE_NAME")
	@Enumerated(EnumType.STRING)
	@Convert(value = "LanguageConverter")
	private LanguageNameEnum languageName;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "language", fetch = FetchType.LAZY)
	private List<SMSLog> sMSLogList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "language")
	private List<CampaignSMSDetails> campaignList;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "language")
	private List<Template> templateList;

	public Language() {
	}

	public Language(Integer languageId) {
		this.languageId = languageId;
	}

	public List<Template> getTemplateList() {
		return templateList;
	}

	public void setTemplateList(List<Template> templateList) {
		this.templateList = templateList;
	}

	public Language(Integer languageId, LanguageNameEnum languageName) {
		this.languageId = languageId;
		this.languageName = languageName;
	}

	public Language(LanguageNameEnum languageName) {
		this.languageName = languageName;
	}

	public Integer getLanguageId() {
		return languageId;
	}

	public void setLanguageId(Integer languageId) {
		this.languageId = languageId;
	}

	public LanguageNameEnum getLanguageName() {
		return languageName;
	}

	public void setLanguageName(LanguageNameEnum languageName) {
		this.languageName = languageName;
	}

	@XmlTransient
	public List<CampaignSMSDetails> getCampaignList() {
		return campaignList;
	}

	public void setCampaignList(List<CampaignSMSDetails> campaignList) {
		this.campaignList = campaignList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (languageId != null ? languageId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Language)) {
			return false;
		}
		Language other = (Language) object;
		if ((this.languageId == null && other.languageId != null) || (this.languageId != null && !this.languageId.equals(other.languageId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "(" + languageId + "," + languageName + ")";
	}
}
