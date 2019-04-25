/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import com.edafa.web2sms.dalayer.model.constants.CampignListsCon;

/**
 * 
 * @author yyaseen
 */
@Entity
@Table(name = "CAMPAIGN_LISTS")
@XmlRootElement
@NamedQueries({
		@NamedQuery(name = "CampaignLists.findAll", query = "SELECT c FROM CampaignLists c"),
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdOrdered", query = "SELECT l FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId WHERE c.campaignListsPK.campaignId = :campaignId ORDER BY l.listName"),
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdOrderedWithCounts", query = "SELECT NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(cc.listContactPK.listId)) FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId LEFT JOIN Contact cc ON l.listId = cc.listContactPK.listId WHERE c.campaignListsPK.campaignId = :campaignId GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listName"),
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdAndAcctIdOrdered", query = "SELECT l FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId WHERE c.campaignListsPK.campaignId = :campaignId AND c.list.account.accountId = :accountId ORDER BY l.listName"),
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdAndAcctIdOrderedWithCounts", query = "SELECT NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(cc.listContactPK.listId)) FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId LEFT JOIN Contact cc ON l.listId = cc.listContactPK.listId WHERE c.campaignListsPK.campaignId = :campaignId AND c.list.account.accountId = :accountId GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listName"),
		
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdAndAcctIdOrderedAndTypes", query = "SELECT l FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId WHERE c.campaignListsPK.campaignId = :campaignId AND l.account.accountId = :accountId AND l.listType IN :listTypes ORDER BY l.listName"),
		@NamedQuery(name = "CampaignLists.findListsByCampaignIdAndAcctIdOrderedAndTypesWithCounts", query = "SELECT NEW com.edafa.web2sms.dalayer.model.ContactList(l.listId, l.listName, l.description, l.listType, COUNT(cc.listContactPK.listId)) FROM CampaignLists c LEFT JOIN ContactList l ON c.campaignListsPK.listId = l.listId LEFT JOIN Contact cc ON l.listId = cc.listContactPK.listId WHERE c.campaignListsPK.campaignId = :campaignId AND c.list.account.accountId = :accountId AND l.listType IN :listTypes GROUP BY l.listId, l.listName, l.description, l.listType ORDER BY l.listName"),
		
		@NamedQuery(name = "CampaignLists.findListNameByCampaignId", query = "SELECT NEW com.edafa.web2sms.dalayer.pojo.CampaignAssociatedList(campl.campaignListsPK.campaignId, l.listName) FROM CampaignLists campl, campl.list l WHERE l.listType IN :listTypes AND campl.campaignListsPK.campaignId IN :campIdList"),
		@NamedQuery(name = "CampaignLists.findByCampaignIdOrdered", query = "SELECT c FROM CampaignLists c WHERE c.campaignListsPK.campaignId = :campaignId ORDER BY c.list.listName"),
		@NamedQuery(name = "CampaignLists.findByListId", query = "SELECT c FROM CampaignLists c WHERE c.campaignListsPK.listId = :listId"),
		@NamedQuery(name = "CampaignLists.findSubmittableByCampIdOrdered", query = "SELECT l FROM CampaignLists l WHERE l.campaignListsPK.campaignId = :campaignId AND l.submittedSMSCount < (SELECT COUNT(c.listContactPK.listId) FROM Contact c WHERE c.listContactPK.listId = l.campaignListsPK.listId) ORDER BY l.campaignListsPK.listId"),
		@NamedQuery(name = "CampaignLists.isActiveCampaign", query = "SELECT count(L.campaignListsPK.listId) FROM CampaignLists L , Campaign C WHERE L.campaignListsPK.campaignId = C.campaignId AND L.campaignListsPK.listId = :listId AND C.status IN :statusList"),
		@NamedQuery(name = "CampaignLists.updateExecutionInfo", query = "UPDATE CampaignLists l SET l.submittedSMSCount = :submittedSMSCount, l.totalSubmittedSMSCount = :totalSubmittedSMSCount WHERE l.campaignListsPK.listId = :listId AND l.campaignListsPK.campaignId = :campaignId"),
		@NamedQuery(name = "CampaignLists.countSubmittedSMSInLists", query = "SELECT SUM(l.submittedSMSCount) FROM CampaignLists l WHERE l.campaignListsPK.campaignId = :campaignId"),
		@NamedQuery(name = "CampaignLists.resetSubmittedSMSInLists", query = "UPDATE CampaignLists l SET l.submittedSMSCount = 0 WHERE l.campaignListsPK.listId = :listId AND l.campaignListsPK.campaignId = :campaignId")})
public class CampaignLists implements Serializable, CampignListsCon {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	protected CampaignListsPK campaignListsPK;

	@Basic(optional = false)
	@Column(name = "SUBMITTED_SMS_COUNT")
	protected Integer submittedSMSCount = 0;

	@Basic(optional = false)
	@Column(name = "TOTAL_SUBMITTED_SMS_COUNT")
	protected Integer totalSubmittedSMSCount = 0;

	@JoinColumn(name = "LIST_ID", referencedColumnName = "LIST_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	protected ContactList list;

	public CampaignLists() {
	}

	public CampaignLists(CampaignListsPK campaignListsPK) {
		this.campaignListsPK = campaignListsPK;
	}

	public CampaignLists(String campaignId, Integer listId) {
		this.campaignListsPK = new CampaignListsPK(campaignId, listId);
	}

	public CampaignListsPK getCampaignListsPK() {
		return campaignListsPK;
	}

	public void setCampaignListsPK(CampaignListsPK campaignListsPK) {
		this.campaignListsPK = campaignListsPK;
	}

	public ContactList getLists() {
		return list;
	}

	public void setLists(ContactList lists) {
		this.list = lists;
	}

	public Integer getSubmittedSMSCount() {
		return submittedSMSCount;
	}

	public void setSubmittedSMSCount(Integer submittedSMSCount) {
		this.submittedSMSCount = submittedSMSCount;
	}

	public Integer getTotalSubmittedSMSCount() {
		return totalSubmittedSMSCount;
	}

	public void setTotalSubmittedSMSCount(Integer totalSubmittedSMSCount) {
		this.totalSubmittedSMSCount = totalSubmittedSMSCount;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (campaignListsPK != null ? campaignListsPK.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof CampaignLists)) {
			return false;
		}
		CampaignLists other = (CampaignLists) object;
		if ((this.campaignListsPK == null && other.campaignListsPK != null)
				|| (this.campaignListsPK != null && !this.campaignListsPK.equals(other.campaignListsPK))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "CampaignLists[ campaignListsPK=" + campaignListsPK + " ]";
	}

	public String logId() {
		return "(" + campaignListsPK.getListId() + ") ";
	}

}
