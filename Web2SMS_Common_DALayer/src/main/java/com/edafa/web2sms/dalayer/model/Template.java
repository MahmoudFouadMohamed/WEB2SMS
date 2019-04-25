/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.TemplateConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "TEMPLATES")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Template.findAll", query = "SELECT t FROM Template t"),
		@NamedQuery(name = "Template.findByTemplateId", query = "SELECT t FROM Template t WHERE t.TemplateId = :TemplateId"),
		@NamedQuery(name = "Template.findByTemplateName", query = "SELECT t FROM Template t WHERE t.TemplateName = :TemplateName"),
		@NamedQuery(name = "Template.findByText", query = "SELECT t FROM Template t WHERE t.text = :text"),
		@NamedQuery(name = "Template.findByDescription", query = "SELECT t FROM Template t WHERE t.description = :description"),
		@NamedQuery(name = "Template.findBySystemTemplateFlag", query = "SELECT t FROM Template t WHERE t.systemTemplateFlag = :systemTemplateFlag ORDER BY t.TemplateId"),
		// @NamedQuery(name = "Template.findByAccountAndAdmin", query =
		// "SELECT t FROM Template t WHERE t.account.accountId = :accountId OR t.systemTemplateFlag=true  ORDER BY t.TemplateId DESC"),
		@NamedQuery(name = "Template.findAdminTemplates", query = "SELECT t FROM Template t WHERE t.systemTemplateFlag=true \n "),
		@NamedQuery(name = "Template.countBySystemTemplateFlag", query = "SELECT COUNT(t) FROM Template t WHERE t.systemTemplateFlag = :systemTemplateFlag") })
@Cacheable(value = false)
public class Template implements Serializable, TemplateConst {
	private static final long serialVersionUID = 1L;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@SequenceGenerator(name = "SMSTemplateIdSeq", sequenceName = "SMS_TEMPLATE_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SMSTemplateIdSeq")
	@Column(name = "TEMPLATE_ID")
	private Integer TemplateId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 200)
	@Column(name = "TEMPLATE_NAME")
	private String TemplateName;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 300)
	@Column(name = "TEXT")
	private String text;
	@Size(max = 200)
	@Column(name = "DESCRIPTION")
	private String description;
	@Basic(optional = false)
	@NotNull
	@Column(name = "SYSTEM_TEMPLATE_FLAG")
	private Boolean systemTemplateFlag;
	@ManyToMany
	@JoinTable(name = "TEMPLATES_ACCOUNTS", joinColumns = @JoinColumn(name = "TEMPLATE_ID"), inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID"))
	private List<Account> accountsList;

	@JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH)
	private Language language;

	public Template() {
	}

	public Template(Integer TemplateId) {
		this.TemplateId = TemplateId;
	}

	public Template(Integer TemplateId, String TemplateName, String text, Boolean systemTemplateFlag, Language language) {
		this.TemplateId = TemplateId;
		this.TemplateName = TemplateName;
		this.text = text;
		this.systemTemplateFlag = systemTemplateFlag;
		this.language = language;
	}

	public Integer getTemplateId() {
		return TemplateId;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public void setTemplateId(Integer templateId) {
		TemplateId = templateId;
	}

	public String getTemplateName() {
		return TemplateName;
	}

	public void setTemplateName(String TemplateName) {
		this.TemplateName = TemplateName;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getSystemTemplateFlag() {
		return systemTemplateFlag;
	}

	public void setSystemTemplateFlag(Boolean systemTemplateFlag) {
		this.systemTemplateFlag = systemTemplateFlag;
	}

	@XmlTransient
	public List<Account> getAccountsList() {
		return accountsList;
	}

	public void setAccountsList(List<Account> accountsList) {
		this.accountsList = accountsList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (TemplateId != null ? TemplateId.hashCode() : 0);
		return hash;
	}

	public boolean isvalid() {
		if (this.TemplateName == null || this.text == null)
			return false;
		return true;
	}

	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Template)) {
			return false;
		}
		Template other = (Template) object;
		if ((this.TemplateId == null && other.TemplateId != null)
				|| (this.TemplateId != null && !this.TemplateId.equals(other.TemplateId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Template[ TemplateId=" + TemplateId + ", TemplateName=" + TemplateName + ", text=" + text
				+ ", systemTemplateFlag=" + systemTemplateFlag + ", " + ", language=" + language + " ]";
	}

}
