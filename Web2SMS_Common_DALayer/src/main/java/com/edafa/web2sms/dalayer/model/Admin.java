/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.edafa.web2sms.dalayer.model.constants.AdminConst;

/**
 *
 * @author yyaseen
 */
@Entity
@Table(name = "ADMINS")
@XmlType(name = "Admin", namespace = "http://www.edafa.com/web2sms/service/model/")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Admin.findAll", query = "SELECT a FROM Admin a"),
		@NamedQuery(name = "Admin.findByAdminId", query = "SELECT a FROM Admin a WHERE a.adminId = :adminId"),
		@NamedQuery(name = "Admin.findByAdminName", query = "SELECT a FROM Admin a WHERE a.adminName = :adminName"),
		@NamedQuery(name = "Admin.findByUsername", query = "SELECT a FROM Admin a WHERE a.username = :username"),
		@NamedQuery(name = "Admin.findByPassword", query = "SELECT a FROM Admin a WHERE a.password = :password"),
		@NamedQuery(name = "Admin.findByResetFlag", query = "SELECT a FROM Admin a WHERE a.resetFlag = :resetFlag"),
		@NamedQuery(name = "Admin.findByResetDate", query = "SELECT a FROM Admin a WHERE a.resetDate = :resetDate"),
		@NamedQuery(name = "Admin.findPassword", query = "SELECT a.password FROM Admin a WHERE a.username = :username") })
public class Admin implements Serializable, AdminConst {

	private static final long serialVersionUID = 1L;
	// @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ADMIN_ID")
	@SequenceGenerator(name = "ADMIN_ID_SEQ")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ADMIN_ID_SEQ")
	private BigDecimal adminId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 150)
	@Column(name = "ADMIN_NAME")
	private String adminName;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 100)
	@Column(name = "USERNAME")
	private String username;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 250)
	@Column(name = "PASSWORD")
	private String password;
	@Basic(optional = false)
	@NotNull
	@Column(name = "RESET_FLAG")
	private boolean resetFlag;
	@Basic(optional = false)
	@NotNull
	@Column(name = "ACTIVE")
	private boolean activeFlag;
	@Basic(optional = false)
	@NotNull
	@Column(name = "RESET_DATE")
	@Temporal(TemporalType.DATE)
	private Date resetDate;
	@JoinColumn(name = "ROLE_ID", referencedColumnName = "ROLE_ID")
	@ManyToOne(optional = false)
	private SystemRole roleId;

	public Admin() {}

	public Admin(BigDecimal adminId) {
		this.adminId = adminId;
	}

	public Admin(BigDecimal adminId, String adminName, String username, String password, boolean resetFlag,
			boolean activeFlag, Date resetDate) {
		this.adminId = adminId;
		this.adminName = adminName;
		this.username = username;
		this.password = password;
		this.resetFlag = resetFlag;
		this.activeFlag = activeFlag;
		this.resetDate = resetDate;
	}

	public BigDecimal getAdminId() {
		return adminId;
	}

	public void setAdminId(BigDecimal adminId) {
		this.adminId = adminId;
	}

	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean getResetFlag() {
		return resetFlag;
	}

	public void setResetFlag(boolean resetFlag) {
		this.resetFlag = resetFlag;
	}

	public boolean getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(boolean activeFlag) {
		this.activeFlag = activeFlag;
	}

	public Date getResetDate() {
		return resetDate;
	}

	public void setResetDate(Date resetDate) {
		this.resetDate = resetDate;
	}

	public SystemRole getRoleId() {
		return roleId;
	}

	public void setRoleId(SystemRole roleId) {
		this.roleId = roleId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (adminId != null ? adminId.hashCode() : 0);
		return hash;
	}

	public boolean isvalid() {
		if (this.adminName != null && this.password != null && this.username != null)
			return true;
		return false;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are not set
		if (!(object instanceof Admin)) {
			return false;
		}
		Admin other = (Admin) object;
		if ((this.adminId == null && other.adminId != null)
				|| (this.adminId != null && !this.adminId.equals(other.adminId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{adminId=").append(adminId).append(", adminName=").append(adminName).append(", username=")
				.append(username).append(", password=").append(password).append(", resetFlag=").append(resetFlag)
				.append(", activeFlag=").append(activeFlag).append(", resetDate=").append(resetDate).append(", roleId=")
				.append(roleId).append("}");
		return builder.toString();
	}

}
