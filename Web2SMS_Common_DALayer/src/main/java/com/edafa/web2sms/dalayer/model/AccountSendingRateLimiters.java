/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author mahmoud
 */
@Entity
@Table(name = "ACCOUNT_SENDING_RATE_LIMITERS")
@XmlRootElement
@Access(AccessType.FIELD)
public class AccountSendingRateLimiters implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @SequenceGenerator(name = "accountSendingRateLimiterIdSeq", sequenceName = "ACCOUNT_SENDRATE_LIMITER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "accountSendingRateLimiterIdSeq")
    @Column(name = "ACCOUNT_LIMITER_ID")
    private BigDecimal accountLimiterId;
    @JoinColumn(name = "ACCOUNT_ID", referencedColumnName = "ACCOUNT_ID")
    @ManyToOne
    private Account accountId;
    @JoinColumn(name = "LIMITER_ID", referencedColumnName = "LIMITER_ID")
    @ManyToOne
    private SendingRateLimiter limiterId;

    public AccountSendingRateLimiters() {
    }

    public AccountSendingRateLimiters(BigDecimal accountLimiterId) {
        this.accountLimiterId = accountLimiterId;
    }

    public BigDecimal getAccountLimiterId() {
        return accountLimiterId;
    }

    public void setAccountLimiterId(BigDecimal accountLimiterId) {
        this.accountLimiterId = accountLimiterId;
    }

    public Account getAccountId() {
        return accountId;
    }

    public void setAccountId(Account accountId) {
        this.accountId = accountId;
    }

    public SendingRateLimiter getLimiterId() {
        return limiterId;
    }

    public void setLimiterId(SendingRateLimiter limiterId) {
        this.limiterId = limiterId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (accountLimiterId != null ? accountLimiterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AccountSendingRateLimiters)) {
            return false;
        }
        AccountSendingRateLimiters other = (AccountSendingRateLimiters) object;
        if ((this.accountLimiterId == null && other.accountLimiterId != null) || (this.accountLimiterId != null && !this.accountLimiterId.equals(other.accountLimiterId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder("AccountSendingRateLimiters[ accountLimiterId=");
        str = str.append(accountLimiterId);
        str = str.append(", accountId=");
        str = str.append(accountId);
        str = str.append(", limiterId=");
        str = str.append(limiterId);
        str = str.append("}");

        return str.toString();
    }

}
