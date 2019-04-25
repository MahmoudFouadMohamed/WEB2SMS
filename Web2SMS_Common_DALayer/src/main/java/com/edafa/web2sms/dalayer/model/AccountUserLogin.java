/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.OneToMany;
import javax.persistence.CascadeType;

/**
 *
 * @author mahmoud
 */
@Entity
@Table(name = "ACCOUNT_USER_LOGIN")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountUserLogin.findAll", query = "SELECT a FROM AccountUserLogin a"),
    @NamedQuery(name = "AccountUserLogin.findByAccountUserLoginId", query = "SELECT a FROM AccountUserLogin a WHERE a.accountUserLoginId = :accountUserLoginId"),
    @NamedQuery(name = "AccountUserLogin.findByPassword", query = "SELECT a FROM AccountUserLogin a WHERE a.password = :password"),
    @NamedQuery(name = "AccountUserLogin.findByPasswordCreateDate", query = "SELECT a FROM AccountUserLogin a WHERE a.passwordCreateDate = :passwordCreateDate"),
    @NamedQuery(name = "AccountUserLogin.findByFailedLogins", query = "SELECT a FROM AccountUserLogin a WHERE a.failedLogins = :failedLogins"),
    @NamedQuery(name = "AccountUserLogin.findByFailedTempPassword", query = "SELECT a FROM AccountUserLogin a WHERE a.failedTempPassword = :failedTempPassword"),
    @NamedQuery(name = "AccountUserLogin.findByTempTrial", query = "SELECT a FROM AccountUserLogin a WHERE a.tempTrial = :tempTrial"),
    @NamedQuery(name = "AccountUserLogin.findByLastLoginDate", query = "SELECT a FROM AccountUserLogin a WHERE a.lastLoginDate = :lastLoginDate")})
public class AccountUserLogin implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @SequenceGenerator(name = "USER_LOGIN_ID_GENERATOR", sequenceName = "USER_LOGIN_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "USER_LOGIN_ID_GENERATOR")
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACCOUNT_USER_LOGIN_ID")
    private BigDecimal accountUserLoginId;
    @Size(max = 200)
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "PASSWORD_CREATE_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date passwordCreateDate;
    @Column(name = "FAILED_LOGINS")
    private int failedLogins;
    @Column(name = "FAILED_TEMP_PASSWORD")
    private int failedTempPassword;
    @Column(name = "TEMP_TRIAL")
    private boolean tempTrial;
    @Column(name = "LAST_LOGIN_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastLoginDate;
    @JoinColumn(name = "ACCOUNT_USER_ID")
    @OneToOne
    private AccountUser accountUserId;
    @JoinColumn(name = "USER_LOGIN_STATUS", referencedColumnName = "STATUS_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    private UserLoginStatus userLoginStatus;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "userLoginId", orphanRemoval=true)
    private List<LoginOldPassword> loginOldPasswordList;

    public AccountUserLogin() {
    }

    public AccountUserLogin(BigDecimal accountUserLoginId) {
        this.accountUserLoginId = accountUserLoginId;
    }

    public BigDecimal getAccountUserLoginId() {
        return accountUserLoginId;
    }

    public void setAccountUserLoginId(BigDecimal accountUserLoginId) {
        this.accountUserLoginId = accountUserLoginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getPasswordCreateDate() {
        return passwordCreateDate;
    }

    public void setPasswordCreateDate(Date passwordCreateDate) {
        this.passwordCreateDate = passwordCreateDate;
    }

    public int getFailedLogins() {
        return failedLogins;
    }

    public void setFailedLogins(int failedLogins) {
        this.failedLogins = failedLogins;
    }

    public int getFailedTempPassword() {
        return failedTempPassword;
    }

    public void setFailedTempPassword(int failedTempPassword) {
        this.failedTempPassword = failedTempPassword;
    }

    public boolean getTempTrial() {
        return tempTrial;
    }

    public void setTempTrial(boolean tempTrial) {
        this.tempTrial = tempTrial;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public AccountUser getAccountUserId() {
        return accountUserId;
    }

    public void setAccountUserId(AccountUser accountUserId) {
        this.accountUserId = accountUserId;
    }

    public UserLoginStatus getUserLoginStatus() {
        return userLoginStatus;
    }

    public void setUserLoginStatus(UserLoginStatus userLoginStatus) {
        this.userLoginStatus = userLoginStatus;
    }

    public boolean isTempTrial() {
        return tempTrial;
    }

    public List<LoginOldPassword> getLoginOldPasswordList() {
        return loginOldPasswordList;
    }

    public void setLoginOldPasswordList(List<LoginOldPassword> loginOldPasswordList) {
        this.loginOldPasswordList = loginOldPasswordList;
    }

    
    
    
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountUserLoginId != null ? accountUserLoginId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountUserLogin)) {
            return false;
        }
        AccountUserLogin other = (AccountUserLogin) object;
        if ((this.accountUserLoginId == null && other.accountUserLoginId != null) || (this.accountUserLoginId != null && !this.accountUserLoginId.equals(other.accountUserLoginId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.edafa.web2sms.dalayer.model.AccountUserLogin[ accountUserLoginId=" + accountUserLoginId + " ]";
    }

}
