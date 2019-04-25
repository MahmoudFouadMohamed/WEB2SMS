package com.edafa.web2sms.dalayer.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.AccountQuotaConst;


/**
 *
 * @author mayahmed
 */
@Entity
@Table(name = "ACCOUNT_QUOTA")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "AccountQuota.findAll", query = "SELECT a FROM AccountQuota a"),
    @NamedQuery(name = "AccountQuota.findByAccountTiersId", query = "SELECT a FROM AccountQuota a WHERE a.accountTiersId = :accountTiersId"),
    @NamedQuery(name = "AccountQuota.findByConsumedSmss", query = "SELECT a FROM AccountQuota a WHERE a.consumedSmss = :consumedSmss"),
    @NamedQuery(name = "AccountQuota.findByReservedSmss", query = "SELECT a FROM AccountQuota a WHERE a.reservedSmss = :reservedSmss"),
    @NamedQuery(name = "AccountQuota.findByExpairyDate", query = "SELECT a FROM AccountQuota a WHERE a.expairyDate = :expairyDate"),    
    @NamedQuery(name = "AccountQuota.UpdateQuota", query = "UPDATE AccountQuota a set a.consumedSmss = a.consumedSmss + :consumedSmss, a.reservedSmss = a.reservedSmss + :reservedSmss WHERE a.accountTiers.account.accountId = :accountId"),
    @NamedQuery(name = "AccountQuota.incrementReservedSmss", query = "UPDATE AccountQuota a set a.reservedSmss = a.reservedSmss + :reservedSmss WHERE a.accountTiersId = :accountTiersId")    
})


	
public class AccountQuota implements Serializable, AccountQuotaConst {
    private static final long serialVersionUID = 1L;

    @Id
    @Basic(optional = false)
    @Column(name = "ACCOUNT_TIERS_ID")
    private int accountTiersId;
    
    @Basic(optional = false)
    @Column(name = "CONSUMED_SMSS")
    private int consumedSmss;
    
    @Basic(optional = false)
    @Column(name = "RESERVED_SMSS")
    private int reservedSmss;
    
    @Basic(optional = false)
    @Column(name = "EXPAIRY_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expairyDate;

    @JoinColumn(name = "ACCOUNT_TIERS_ID", referencedColumnName = "ACCOUNT_TIERS_ID", insertable = false, updatable = false)
    @OneToOne(optional = false)
    private AccountTier accountTiers;

    public AccountQuota() {
    }

    public AccountQuota(Integer accountTiersId) {
        this.accountTiersId = accountTiersId;
    }

    public AccountQuota(int accountTiersId, int consumedSmss, int reservedSmss, Date expairyDate) {
        this.accountTiersId = accountTiersId;
        this.consumedSmss = consumedSmss;
        this.reservedSmss = reservedSmss;
        this.expairyDate = expairyDate;
    }

    public Integer getAccountTiersId() {
        return accountTiersId;
    }

    public void setAccountTiersId(Integer accountTiersId) {
        this.accountTiersId = accountTiersId;
    }

    public int getConsumedSmss() {
        return consumedSmss;
    }

    public void setConsumedSmss(int consumedSmss) {
        this.consumedSmss = consumedSmss;
    }

    public int getReservedSmss() {
        return reservedSmss;
    }

    public void setReservedSmss(int reservedSmss) {
        this.reservedSmss = reservedSmss;
    }

    public Date getExpairyDate() {
        return expairyDate;
    }

    public void setExpairyDate(Date expairyDate) {
        this.expairyDate = expairyDate;
    }

    public AccountTier getAccountTiers() {
        return accountTiers;
    }

    public void setAccountTiers(AccountTier accountTiers) {
        this.accountTiers = accountTiers;
    }


    @Override
    public String toString() {
        return "AccountQuota[ accountTiersId=" + accountTiersId + " aconsumedSmss: "+ consumedSmss + " reservedSmss: "+reservedSmss + " ]";
    }
    
}

