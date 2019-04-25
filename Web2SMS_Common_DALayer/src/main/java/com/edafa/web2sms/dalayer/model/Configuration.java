/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.ConfigConst;

/**
 * 
 * @author akhalifah
 */
@Entity
@Table(name = "SETTINGS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Configuration.findAll", query = "SELECT c FROM Configuration c"),
		@NamedQuery(name = "Configuration.findById", query = "SELECT c FROM Configuration c WHERE c.id = :id"),
		@NamedQuery(name = "Configuration.findByKey", query = "SELECT c FROM Configuration c WHERE c.key = :key"),
		@NamedQuery(name = "Configuration.findByValue", query = "SELECT c FROM Configuration c WHERE c.value = :value"),
		@NamedQuery(name = "Configuration.findByModuleName", query = "SELECT c FROM Configuration c WHERE c.module.name = :moduleName"),
		@NamedQuery(name = "Configuration.findByKeyAndModuleName", query = "SELECT c FROM Configuration c WHERE c.key = :key AND c.module.name = :moduleName"),
		@NamedQuery(name = "Configuration.findAllEditable", query = "SELECT c FROM Configuration c WHERE c.editFlag = true "),
		@NamedQuery(name = "Configuration.findAllEditableCount", query = "SELECT COUNT(c) FROM  Configuration c WHERE c.editFlag = true"),
		@NamedQuery(name = "Configuration.findEditableByModuleName", query = "SELECT c FROM Configuration c JOIN c.module AS m WHERE c.module.name = :moduleName and c.editFlag = true") })
public class Configuration implements ConfigConst, Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "PROPERTY_ID")
	private Integer id;
	@Basic(optional = false)
	@Column(name = "PROPERTY")
	private String key;
	@Basic(optional = false)
	@Column(name = "VALUE")
	private String value;
	@Column(name = "DESCRIPTION")
	private String description;
	@JoinColumn(name = "MODULE", referencedColumnName = "MODULE_ID")
	@ManyToOne(optional = false, fetch = FetchType.EAGER)
	private Module module;
	@Column(name = "EDIT_FLAG")
	private Boolean editFlag;

	// @Transient
	// private String transientValue;
	//
	// @Transient
	// private String runningConfig;
	//
	// @Transient
	// private String configType;

	public Configuration() {
	}

	public Configuration(Integer id) {
		this.id = id;
	}

	public Configuration(Integer id, String key, String value, Module module, Boolean editFlag) {
		this.id = id;
		this.key = key;
		this.value = value;
		this.module = module;
		this.editFlag = editFlag;
	}

	public Configuration(String key, String value, Module module, Boolean editFlag) {
		this.key = key;
		this.value = value;
		this.module = module;
		this.editFlag = editFlag;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Module getModuleId() {
		return module;
	}

	public void setModuleId(Module module) {
		this.module = module;
	}

	public Boolean getEditFlag() {
		return editFlag;
	}

	// public String getRunningConfig() {
	// return runningConfig;
	// }
	//
	// public void setRunningConfig(String runningConfig) {
	// this.runningConfig = runningConfig;
	// }
	//
	// public String getConfigType() {
	// return configType;
	// }
	//
	// public void setConfigType(String configType) {
	// this.configType = configType;
	// }

	public void setEditFlag(Boolean editFlag) {
		this.editFlag = editFlag;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Configuration)) {
			return false;
		}
		Configuration other = (Configuration) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Configuration [id=" + id + ", key=" + key + ", value=" + value + ", description=" + description
				+ ", module=" + module + "]";
	}

}
