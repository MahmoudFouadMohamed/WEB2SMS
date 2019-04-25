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
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.constants.SystemRoleConst;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "SYSTEM_ROLES")
@XmlType(name = "SystemRole", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "Role.findAll", query = "SELECT r FROM SystemRole r"),
		@NamedQuery(name = "Role.findByRoleName", query = "SELECT r FROM SystemRole r WHERE r.roleName = :roleName"),
		@NamedQuery(name = "Role.findByDesceription", query = "SELECT r FROM SystemRole r WHERE r.desceription = :desceription"),
		@NamedQuery(name = "Role.findByRoleId", query = "SELECT r FROM SystemRole r WHERE r.roleId = :roleId") })
public class SystemRole implements Serializable, SystemRoleConst {
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "ROLE_NAME")
	private String roleName;
	@Size(max = 200)
	@Column(name = "DESCERIPTION")
	private String desceription;
	@Column(name = "DIRECTORY")
	private String directory;
	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ROLE_ID")
	private Integer roleId;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "roleId")
	private List<Admin> adminList;

	public SystemRole() {
	}

	public SystemRole(Integer roleId) {
		this.roleId = roleId;
	}

	public SystemRole(Integer roleId, String roleName) {
		this.roleId = roleId;
		this.roleName = roleName;
	}

	public String getRoleName() {
		return roleName;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDesceription() {
		return desceription;
	}

	public void setDesceription(String desceription) {
		this.desceription = desceription;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	@XmlTransient
	public List<Admin> getAdminList() {
		return adminList;
	}

	public void setAdminList(List<Admin> adminList) {
		this.adminList = adminList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (roleId != null ? roleId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof SystemRole)) {
			return false;
		}
		SystemRole other = (SystemRole) object;
		if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "com.edafa.web2sms.module.Role[ roleId=" + roleId + " ]";
	}

}
