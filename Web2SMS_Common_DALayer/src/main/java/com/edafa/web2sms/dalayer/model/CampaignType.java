package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import com.edafa.web2sms.dalayer.enums.CampaignTypeName;

/**
 *
 * @author khalid
 */
@Entity
@Table(name = "CAMPAIGN_TYPE")
@XmlRootElement
@ObjectTypeConverter(name = "CampaignTypeNameConverter", dataType = String.class, objectType = CampaignTypeName.class, defaultObjectValue = "NORMAL_CAMPAIGN", conversionValues = {
	@ConversionValue(dataValue = "NORMAL_CAMPAIGN", objectValue = "NORMAL_CAMPAIGN"),
	@ConversionValue(dataValue = "INTRA_CAMPAIGN", objectValue = "INTRA_CAMPAIGN"),
	@ConversionValue(dataValue = "CUSTOMIZED_CAMPAIGN", objectValue = "CUSTOMIZED_CAMPAIGN"),
	@ConversionValue(dataValue = "API_CAMPAIGN", objectValue = "API_CAMPAIGN")})
@NamedQueries({
    @NamedQuery(name = "CampaignType.findAll", query = "SELECT c FROM CampaignType c"),
    @NamedQuery(name = "CampaignType.findByCampaignTypeId", query = "SELECT c FROM CampaignType c WHERE c.campaignTypeId = :campaignTypeId"),
    @NamedQuery(name = "CampaignType.findByCampaignTypeName", query = "SELECT c FROM CampaignType c WHERE c.campaignTypeName = :campaignTypeName")})
public class CampaignType implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "CAMPAIGN_TYPE_ID")
    private Integer campaignTypeId;
    @Enumerated(EnumType.STRING)
   	@Convert(value = "CampaignTypeNameConverter")
     @Column(name = "CAMPAIGN_TYPE_NAME")
    private CampaignTypeName campaignTypeName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "type", fetch = FetchType.LAZY)
    private List<Campaign> campaignsList;

    public CampaignType() {
    }

    public CampaignType(Integer campaignTypeId) {
        this.campaignTypeId = campaignTypeId;
    }

    public Integer getCampaignTypeId() {
        return campaignTypeId;
    }

    public void setCampaignTypeId(Integer campaignTypeId) {
        this.campaignTypeId = campaignTypeId;
    }

    public CampaignTypeName getCampaignTypeName() {
        return campaignTypeName;
    }

    public void setCampaignTypeName(CampaignTypeName campaignTypeName) {
        this.campaignTypeName = campaignTypeName;
    }

    @XmlTransient
    public List<Campaign> getCampaignsList() {
        return campaignsList;
    }

    public void setCampaignsList(List<Campaign> campaignsList) {
        this.campaignsList = campaignsList;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (campaignTypeId != null ? campaignTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CampaignType)) {
            return false;
        }
        CampaignType other = (CampaignType) object;
        if ((this.campaignTypeId == null && other.campaignTypeId != null) || (this.campaignTypeId != null && !this.campaignTypeId.equals(other.campaignTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "CampaignType[ campaignTypeId=" + campaignTypeId + " ]";
    }
    
}
