
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.AccountSMSAPIConst;

/**
 *
 * @author khalid
 */
@Entity
@Table(name = "ACCOUNT_SMS_API")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountSMSAPI.findAll", query = "SELECT a FROM AccountSMSAPI a"),
    @NamedQuery(name = "AccountSMSAPI.findById", query = "SELECT a FROM AccountSMSAPI a WHERE a.id = :id"),
    @NamedQuery(name = "AccountSMSAPI.findByAccountId", query = "SELECT a FROM AccountSMSAPI a WHERE a.account.accountId = :accountId"),
    @NamedQuery(name = "AccountSMSAPI.countByAccountId", query = "SELECT COUNT(a) FROM AccountSMSAPI a WHERE a.account.accountId = :accountId"),
    @NamedQuery(name = "AccountSMSAPI.findByPassword", query = "SELECT a FROM AccountSMSAPI a WHERE a.password = :password"),
    @NamedQuery(name = "AccountSMSAPI.findBySecureKey", query = "SELECT a FROM AccountSMSAPI a WHERE a.secureKey = :secureKey"),
    @NamedQuery(name = "AccountSMSAPI.findByActiveFlag", query = "SELECT a FROM AccountSMSAPI a WHERE a.activeFlag = :activeFlag"),
    @NamedQuery(name = "AccountSMSAPI.countAll", query="SELECT COUNT(a) FROM AccountSMSAPI a")})
public class AccountSMSAPI implements Serializable, AccountSMSAPIConst {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @SequenceGenerator(name = "AcctSMSAPIId", sequenceName = "ACCT_SMS_API_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AcctSMSAPIId")
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "PASSWORD")
    private String password;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "SECURE_KEY")
    private String secureKey;
    @Basic(optional = false)
    @NotNull
    @Column(name = "ACTIVE_FLAG")
    private boolean activeFlag;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "acctSmsApiId", fetch = FetchType.EAGER)
    private List<AccountIP> accountIPs;
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @OneToOne(optional = false)
    private Account account;

    public AccountSMSAPI() {
    }

    public AccountSMSAPI(String id) {
        this.id = id;
    }

    public AccountSMSAPI(String id, String password, String secureKey, boolean activeFlag) {
        this.id = id;
        this.password = password;
        this.secureKey = secureKey;
        this.activeFlag = activeFlag;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecureKey() {
        return secureKey;
    }

    public void setSecureKey(String secureKey) {
        this.secureKey = secureKey;
    }

    public boolean getActiveFlag() {
        return activeFlag;
    }

    public void setActiveFlag(boolean activeFlag) {
        this.activeFlag = activeFlag;
    }

    @XmlTransient
    public List<AccountIP> getAccountIPs() {
        return accountIPs;
    }

    @XmlTransient
    public void setAccountIPs(List<AccountIP> accountIPs) {
        this.accountIPs = accountIPs;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
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
        if (!(object instanceof AccountSMSAPI)) {
            return false;
        }
        AccountSMSAPI other = (AccountSMSAPI) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "AccountSMSAPI[ id=" + id + " ]";
    }
    
}

