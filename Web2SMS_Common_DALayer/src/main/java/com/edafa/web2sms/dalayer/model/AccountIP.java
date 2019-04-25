
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

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
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.AccountIPConst;

/**
 *
 * @author Khalid
 */
@Entity
@Table(name = "ACCOUNT_IP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountIp.findAll", query = "SELECT a FROM AccountIP a"),
    @NamedQuery(name = "AccountIp.findById", query = "SELECT a FROM AccountIP a WHERE a.id = :id"),
    @NamedQuery(name = "AccountIp.findByClientIp", query = "SELECT a FROM AccountIP a WHERE a.clientIp = :clientIp")})
public class AccountIP implements Serializable, AccountIPConst {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @SequenceGenerator(name = "AcctIPId", sequenceName = "ACCT_IP_ID_SEQ", allocationSize = 1)
   	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "AcctIPId")
    @Column(name = "ID")
    private String id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 20)
    @Column(name = "CLIENT_IP")
    private String clientIp;
    @JoinColumn(name = "ACCT_SMS_API_ID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private AccountSMSAPI acctSmsApiId;

    public AccountIP() {
    }

    public AccountIP(String id) {
        this.id = id;
    }

    public AccountIP(String id, String clientIp) {
        this.id = id;
        this.clientIp = clientIp;
    }
    
    public AccountIP(AccountSMSAPI acctSmsApiId, String clientIp) {
        this.acctSmsApiId = acctSmsApiId;
        this.clientIp = clientIp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public AccountSMSAPI getAcctSmsApiId() {
        return acctSmsApiId;
    }

    public void setAcctSmsApiId(AccountSMSAPI acctSmsApiId) {
        this.acctSmsApiId = acctSmsApiId;
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
        if (!(object instanceof AccountIP)) {
            return false;
        }
        AccountIP other = (AccountIP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

	@Override
	public String toString() {
		return "AccountIP [id=" + id + ", clientIp=" + clientIp + ", acctSmsApiId=" + acctSmsApiId + "]";
	}

   
    
}
