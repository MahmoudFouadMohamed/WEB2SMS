package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.edafa.web2sms.dalayer.model.constants.SendingRateLimiterConstant;

/**
 *
 * @author mahmoud
 */
@Entity
@Table(name = "SENDING_RATE_LIMITER")
@XmlRootElement
@Access(AccessType.FIELD)
@NamedQueries({
	@NamedQuery(name = "SendingRateLimiter.findAll", query = "SELECT a FROM SendingRateLimiter a"),
	@NamedQuery(name="SendingRateLimiter.updateLimiter", query="UPDATE SendingRateLimiter l set l.maxPermits = :maxPermits, l.smsapiEnabled = :smsapiEnabled, l.campEnabled = :campEnabled where l.limiterId = :limiterId")})
public class SendingRateLimiter implements SendingRateLimiterConstant, Serializable {

    private static final long serialVersionUID = 1L;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Id
    @Basic(optional = false)
    @NotNull
    @SequenceGenerator(name = "sendingRateLimiterIdSeq", sequenceName = "SENDINGRATE_LIMITER_ID_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sendingRateLimiterIdSeq")
    @Column(name = "LIMITER_ID")
    private String limiterId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "MAX_PERMITS")
    private Integer maxPermits;
    @Size(max = 200)
    @Column(name = "DESCRIPTION")
    private String description;
    @Basic(optional = false)
    @NotNull
    @Column(name = "SMSAPI_ENABLED")
    private Boolean smsapiEnabled;
    @Basic(optional = false)
    @NotNull
    @Column(name = "CAMP_ENABLED")
    private Boolean campEnabled;
    
    @XmlTransient
    @ManyToMany(fetch=FetchType.EAGER)
   	@JoinTable(name = "ACCOUNT_SENDING_RATE_LIMITERS", joinColumns = 
   	@JoinColumn(name = "LIMITER_ID"), inverseJoinColumns = @JoinColumn(name = "ACCOUNT_ID"))
   	private List<Account> accountsList;

    public SendingRateLimiter() {
    }

    public SendingRateLimiter(String limiterId) {
        this.limiterId = limiterId;
    }

    public SendingRateLimiter(String limiterId,Integer maxPermits){
    	 this.limiterId = limiterId;
         this.maxPermits = maxPermits;	
    }
    public SendingRateLimiter(String limiterId, Integer maxPermits, Boolean smsappiEnabled, Boolean campEnabled) {
        this.limiterId = limiterId;
        this.maxPermits = maxPermits;
        this.smsapiEnabled = smsappiEnabled;
        this.campEnabled = campEnabled;
    }

    public String getLimiterId() {
        return limiterId;
    }

    public void setLimiterId(String limiterId) {
        this.limiterId = limiterId;
    }

    public Integer getMaxPermits() {
        return maxPermits;
    }

    public void setMaxPermits(Integer maxPermits) {
        this.maxPermits = maxPermits;
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
        hash += (limiterId != null ? limiterId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof SendingRateLimiter)) {
            return false;
        }
        SendingRateLimiter other = (SendingRateLimiter) object;
        if ((this.limiterId == null && other.limiterId != null) || (this.limiterId != null && !this.limiterId.equals(other.limiterId))) {
            return false;
        }
        return true;
    }

    public void setSmsapiEnabled(Boolean smsapiEnabled) {
        this.smsapiEnabled = smsapiEnabled;
    }

    public void setCampEnabled(Boolean campEnabled) {
        this.campEnabled = campEnabled;
    }

    public Boolean isSmsapiEnabled() {
        return smsapiEnabled;
    }

    public Boolean isCampEnabled() {
        return campEnabled;
    }
    
    
    public Boolean getSmsapiEnabled() {
        return smsapiEnabled;
    }

    public Boolean getCampEnabled() {
        return campEnabled;
    }
    @XmlTransient
    public List<Account> getAccountsList() {
		return accountsList;
	}
    @XmlTransient
	public void setAccountsList(List<Account> accountsList) {
		this.accountsList = accountsList;
	}

	@Override
    public String toString() {
        StringBuilder str = new StringBuilder("SendingRateLimiter{limiterId=");
        str = str.append(limiterId);
        str = str.append(", maxPermits=");
        str = str.append(maxPermits);
        str = str.append(", description=");
        str = str.append(description);
        str = str.append(", smsapiEnabled=");
        str = str.append(smsapiEnabled);
        str = str.append(", campEnabled=");
        str = str.append(campEnabled);
        str = str.append(", accounts=");
        str = str.append(accountsList);
        str = str.append("}");

        return str.toString();
    }

}
