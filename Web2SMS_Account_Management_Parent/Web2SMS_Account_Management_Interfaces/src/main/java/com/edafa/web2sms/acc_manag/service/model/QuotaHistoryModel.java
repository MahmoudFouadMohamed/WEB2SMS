package com.edafa.web2sms.acc_manag.service.model;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "QuotaHistory", namespace = "http://www.edafa.com/web2sms/service/acc_manag/model/")
@XmlAccessorType(XmlAccessType.FIELD)
public class QuotaHistoryModel implements Serializable {

	@XmlElement(required = true, nillable = false)
	private String accountId;
	@XmlElement(required = true, nillable = false)
	private int jan;
	@XmlElement(required = true, nillable = false)
	private int feb;
	@XmlElement(required = true, nillable = false)
	private int mar;
	@XmlElement(required = true, nillable = false)
	private int apr;
	@XmlElement(required = true, nillable = false)
	private int may;
	@XmlElement(required = true, nillable = false)
	private int june;
	@XmlElement(required = true, nillable = false)
	private int july;
	@XmlElement(required = true, nillable = false)
	private int aug;
	@XmlElement(required = true, nillable = false)
	private int sept;
	@XmlElement(required = true, nillable = false)
	private int oct;
	@XmlElement(required = true, nillable = false)
	private int nov;
	@XmlElement(required = true, nillable = false)
	private int dec;
	@XmlElement(required = true, nillable = false)
	private Date updateTimestamp;
	
	public QuotaHistoryModel() {
	}
	
	public QuotaHistoryModel(String accountId) {
		this.accountId = accountId;
	}
	
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public int getJan() {
		return jan;
	}
	public void setJan(int jan) {
		this.jan = jan;
	}
	public int getFeb() {
		return feb;
	}
	public void setFeb(int feb) {
		this.feb = feb;
	}
	public int getMar() {
		return mar;
	}
	public void setMar(int mar) {
		this.mar = mar;
	}
	public int getApr() {
		return apr;
	}
	public void setApr(int apr) {
		this.apr = apr;
	}
	public int getMay() {
		return may;
	}
	public void setMay(int may) {
		this.may = may;
	}
	public int getJune() {
		return june;
	}
	public void setJune(int june) {
		this.june = june;
	}
	public int getJuly() {
		return july;
	}
	public void setJuly(int july) {
		this.july = july;
	}
	public int getAug() {
		return aug;
	}
	public void setAug(int aug) {
		this.aug = aug;
	}
	public int getSept() {
		return sept;
	}
	public void setSept(int sept) {
		this.sept = sept;
	}
	public int getOct() {
		return oct;
	}
	public void setOct(int oct) {
		this.oct = oct;
	}
	public int getNov() {
		return nov;
	}
	public void setNov(int nov) {
		this.nov = nov;
	}
	public int getDec() {
		return dec;
	}
	public void setDec(int dec) {
		this.dec = dec;
	}
	public Date getUpdateTimestamp() {
		return updateTimestamp;
	}
	public void setUpdateTimestamp(Date updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}
	
	
	

}
