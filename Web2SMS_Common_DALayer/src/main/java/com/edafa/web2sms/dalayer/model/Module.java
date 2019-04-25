/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.ModuleConst;

/**
 * 
 * @author akhalifah
 */
@Entity
@Table(name = "MODULES")
@XmlRootElement
@NamedQueries({@NamedQuery(name = "Module.findAll", query = "SELECT m FROM Module m"),
		@NamedQuery(name = "Module.findById", query = "SELECT m FROM Module m WHERE m.id = :id"),
		@NamedQuery(name = "Module.findByName", query = "SELECT m FROM Module m WHERE m.name = :name"),
		@NamedQuery(name = "Module.findByDescription", query = "SELECT m FROM Module m WHERE m.description = :description")})
public class Module implements Serializable, ModuleConst {
	private static final long serialVersionUID = 1L;
	@Id
	@Basic(optional = false)
	@Column(name = "MODULE_ID")
	private Short id;
	@Basic(optional = false)
	@Column(name = "MODULE_NAME")
	private String name;
	@Column(name = "DESCRIPTION")
	private String description;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "module", fetch = FetchType.LAZY)
	private List<Configuration> configsList;

	public Module() {
	}

	public Module(Short id) {
		this.id = id;
	}

	public Module(Short id, String name) {
		this.id = id;
		this.name = name;
	}

	public Module(String name) {
		this.name = name;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
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
		if (!(object instanceof Module)) {
			return false;
		}
		Module other = (Module) object;
		if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Module [id=" + id + ", name=" + name + ", description=" + description + "]";
	}
}
