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
public class ListContactPK implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Basic(optional = false)
	@NotNull
	@Column(name = "LIST_ID")
	private Integer listId;
	@Basic(optional = false)
	@NotNull
	@Size(min = 1, max = 23)
	@Column(name = "MSISDN")
	private String msisdn;

	public ListContactPK() {
	}

	public ListContactPK(Integer listId, String msisdn) {
		this.listId = listId;
		this.msisdn = msisdn;
	}

	public Integer getListId() {
		return listId;
	}

	public void setListId(Integer listId) {
		this.listId = listId;
	}

	public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (listId != null ? listId.hashCode() : 0);
		hash += (msisdn != null ? msisdn.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof ListContactPK)) {
			return false;
		}
		ListContactPK other = (ListContactPK) object;
		// if ((this.listId == null && other.listId != null) || (this.listId !=
		// null && !this.listId.equals(other.listId))) {
		// return false;
		// }
		// if ((this.msisdn == null && other.msisdn != null) || (this.msisdn !=
		// null && !this.msisdn.equals(other.msisdn))) {
		// return false;
		// }

		// if ((this.listId != null && other.listId != null &&
		// this.listId.equals(other.listId))) {
		// if ((this.msisdn != null && other.msisdn != null &&
		// this.msisdn.equals(other.msisdn))) {
		// return true;
		// }
		//
		// }

		return (this.listId != null && other.listId != null && this.listId.equals(other.listId) && (this.msisdn != null
				&& other.msisdn != null && this.msisdn.equals(other.msisdn)));
	}

	@Override
	public String toString() {
		return "com.edafa.web2sms.dalayer.model.ListContactsPK[ listId=" + listId + ", msisdn=" + msisdn + " ]";
	}

}
