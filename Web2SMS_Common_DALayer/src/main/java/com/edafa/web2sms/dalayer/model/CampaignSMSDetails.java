package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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

import com.edafa.web2sms.dalayer.model.constants.CampaignSMSDetailsConst;

/**
 * The persistent class for the CAMPAIGNS_SMS_DETAILS database table.
 * 
 */
@Entity
@Table(name = "CAMPAIGNS_SMS_DETAILS")
@NamedQueries({
	@NamedQuery(name = "CampaignSMSDetails.findAll", query = "SELECT c FROM CampaignSMSDetails c"),
	@NamedQuery(name = "CampaignSMSDetails.findSMSByCampaignId", query = "SELECT c.smsText FROM CampaignSMSDetails c where c.campaign.campaignId = :campaignId")
})
public class CampaignSMSDetails implements Serializable,  CampaignSMSDetailsConst{
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CAMPAIGNS_SMS_DETAILS_ID_GENERATOR", sequenceName = "CAMP_SMS_DETAIL_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAMPAIGNS_SMS_DETAILS_ID_GENERATOR")
	@Column(unique = true, nullable = false, precision = 10)
	private long id;

	@Column(name = "REGISTERED_DELIVERY", nullable = false, precision = 1)
	private Boolean registeredDelivery;

	@Column(name = "SENDER_NAME", nullable = false, length = 20)
	private String senderName;

	// @Column(name = "Rec", nullable = false)
	// private Integer recipientsCount;

	@Column(name = "SMS_COUNT", nullable = false)
	private Integer smsCount;

	@Column(name = "SMS_SEG_COUNT", nullable = false)
	private Integer smsSegCount;

	@Column(name = "SMS_TEXT", nullable = false, length = 4000)
	private String smsText;

	@JoinColumn(name = "LANGUAGE_ID", referencedColumnName = "LANGUAGE_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH)
	private Language language;

	// uni-directional many-to-one association to Campaign
	@OneToOne
	@JoinColumn(name = "CAMPAIGN_ID", nullable = false)
	private Campaign campaign;

	public CampaignSMSDetails() {
	}

	public CampaignSMSDetails(Campaign campaign) {
		this.campaign = campaign;
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Boolean getRegisteredDelivery() {
		return this.registeredDelivery;
	}

	public void setRegisteredDelivery(Boolean registeredDelivery) {
		this.registeredDelivery = registeredDelivery;
	}

	public String getSenderName() {
		return this.senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public Integer getSMSCount() {
		return this.smsCount;
	}

	public void setSMSCount(Integer smsCount) {
		this.smsCount = smsCount;
	}

	public Integer getSMSSegCount() {
		return this.smsSegCount;
	}

	public void setSMSSegCount(Integer smsSegCount) {
		this.smsSegCount = smsSegCount;
	}

	public String getSMSText() {
		return this.smsText;
	}

	public void setSMSText(String smsText) {
		this.smsText = smsText;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Campaign getCampaign() {
		return this.campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	@Override
	public String toString() {
		return "(language=\"" + language + "\", smsText=\"" + smsText + "\", senderName=\"" + senderName
				+ "\", registeredDelivery=\"" + registeredDelivery + "\", smsCount=\"" + smsCount
				+ "\", smsSegCount=\"" + smsSegCount + ")";
	}

}