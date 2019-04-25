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

/**
 *
 * @author mahmoud
 */
@Entity
@Table(name = "LOGIN_OLD_PASSWORD")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LoginOldPassword.findAll", query = "SELECT l FROM LoginOldPassword l"),
    @NamedQuery(name = "LoginOldPassword.findById", query = "SELECT l FROM LoginOldPassword l WHERE l.id = :id"),
    @NamedQuery(name = "LoginOldPassword.findByPassword", query = "SELECT l FROM LoginOldPassword l WHERE l.password = :password"),
    @NamedQuery(name = "LoginOldPassword.findByCreationDate", query = "SELECT l FROM LoginOldPassword l WHERE l.creationDate = :creationDate"),
    @NamedQuery(name = "LoginOldPassword.findByEndDate", query = "SELECT l FROM LoginOldPassword l WHERE l.endDate = :endDate")})
public class LoginOldPassword implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "OLD_PASSWORD_ID")
    @SequenceGenerator(name = "OLD_PASS_ID_GENERATOR", sequenceName = "LOGIN_OLD_PASS_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OLD_PASS_ID_GENERATOR")
    private BigDecimal id;
    @Size(max = 255)
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDate;
    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;
    @JoinColumn(name = "USER_LOGIN_ID", referencedColumnName = "ACCOUNT_USER_LOGIN_ID")
    @ManyToOne(optional = false)
    private AccountUserLogin userLoginId;

    public LoginOldPassword() {
    }

    public LoginOldPassword(BigDecimal id) {
        this.id = id;
    }

    public LoginOldPassword(String password, Date creationDate, Date endDate, AccountUserLogin userLoginId) {
        this.password = password;
        this.creationDate = creationDate;
        this.endDate = endDate;
        this.userLoginId = userLoginId;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public AccountUserLogin getUserLoginId() {
        return userLoginId;
    }

    public void setUserLoginId(AccountUserLogin userLoginId) {
        this.userLoginId = userLoginId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LoginOldPassword)) {
            return false;
        }
        LoginOldPassword other = (LoginOldPassword) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.edafa.web2sms.dalayer.model.LoginOldPassword[ id=" + id + " ]";
    }

}
