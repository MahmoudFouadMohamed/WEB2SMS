/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model;

import com.edafa.web2sms.dalayer.enums.CampaignStatusName;
import com.edafa.web2sms.dalayer.enums.UserLoginStatusName;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

/**
 *
 * @author mahmoud
 */
@Entity
@Table(name = "USER_LOGIN_STATUS")
@XmlRootElement
@ObjectTypeConverter(name = "UserLoginStatusConverter", dataType = String.class, objectType = UserLoginStatusName.class, defaultObjectValue = "UNKNOWN", conversionValues = {
    @ConversionValue(dataValue = "INITIAL", objectValue = "INITIAL"),
    @ConversionValue(dataValue = "TEMP", objectValue = "TEMP"),
    @ConversionValue(dataValue = "ACTIVE", objectValue = "ACTIVE"),
    @ConversionValue(dataValue = "BLOCKED", objectValue = "BLOCKED")})

@NamedQueries({
    @NamedQuery(name = "UserLoginStatus.findAll", query = "SELECT u FROM UserLoginStatus u"),
    @NamedQuery(name = "UserLoginStatus.findByStatusId", query = "SELECT u FROM UserLoginStatus u WHERE u.statusId = :statusId"),
    @NamedQuery(name = "UserLoginStatus.findByName", query = "SELECT u FROM UserLoginStatus u WHERE u.name = :name")})
public class UserLoginStatus implements Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "STATUS_ID")
    private BigDecimal statusId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "NAME")
    @Enumerated(EnumType.STRING)
    @Convert("UserLoginStatusConverter")
    private UserLoginStatusName name;
//    @OneToMany(mappedBy = "userLoginStatus")
//    private Collection<AccountUserLogin> accountUserLoginCollection;

    public UserLoginStatus() {
    }

    public UserLoginStatus(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public UserLoginStatus(BigDecimal statusId, UserLoginStatusName name) {
        this.statusId = statusId;
        this.name = name;
    }

    public BigDecimal getStatusId() {
        return statusId;
    }

    public void setStatusId(BigDecimal statusId) {
        this.statusId = statusId;
    }

    public UserLoginStatusName getName() {
        return name;
    }

    public void setName(UserLoginStatusName name) {
        this.name = name;
    }

//    @XmlTransient
//    public Collection<AccountUserLogin> getAccountUserLoginCollection() {
//        return accountUserLoginCollection;
//    }
//
//    public void setAccountUserLoginCollection(Collection<AccountUserLogin> accountUserLoginCollection) {
//        this.accountUserLoginCollection = accountUserLoginCollection;
//    }
    @Override
    public int hashCode() {
        int hash = 0;
        hash += (statusId != null ? statusId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserLoginStatus)) {
            return false;
        }
        UserLoginStatus other = (UserLoginStatus) object;
        if ((this.statusId == null && other.statusId != null) || (this.statusId != null && !this.statusId.equals(other.statusId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.edafa.web2sms.dalayer.model.UserLoginStatus[ statusId=" + statusId + " ]";
    }

}
