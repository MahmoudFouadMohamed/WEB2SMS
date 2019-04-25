/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 
 * @author yyaseen
 */
@Embeddable
public class CampaignListsPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 50)
	@Column(name = "CAMPAIGN_ID")
	private String campaignId;
	@Basic(optional = false)
	@NotNull
	@Column(name = "LIST_ID")
	private Integer listId;

	public CampaignListsPK() {
	}

	public CampaignListsPK(String campaignId, Integer listId) {
		this.campaignId = campaignId;
		this.listId = listId;
	}

	public String getCampaignId() {
		return campaignId;
	}

	public void setCampaignId(String campaignId) {
		this.campaignId = campaignId;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (campaignId != null ? campaignId.hashCode() : 0);
		hash += (listId != null ? listId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof CampaignListsPK)) {
			return false;
		}
		CampaignListsPK other = (CampaignListsPK) object;
		if ((this.campaignId == null && other.campaignId != null)
				|| (this.campaignId != null && !this.campaignId.equals(other.campaignId))) {
			return false;
		}
		if ((this.listId == null && other.listId != null) || (this.listId != null && !this.listId.equals(other.listId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CampaignListsPK[ campaignId=" + campaignId + ", listId=" + listId + " ]";
	}

}
