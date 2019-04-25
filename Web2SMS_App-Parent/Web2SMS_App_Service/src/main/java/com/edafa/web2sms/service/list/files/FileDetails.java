package com.edafa.web2sms.service.list.files;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "FileDetails", namespace = "com.edafa.web2sms.service.list.files")
public class FileDetails {

	@XmlElement(required = true, nillable = false)
	String fileToken, fileType, listName, listDescription;

	/*
	 * Contains column number of each type set the value by -1 if it is not
	 * exist
	 */
	@XmlElement(required = true, nillable = false)
	int msisdnColNumber, fnameColNumber, lnameColNumber, value1ColNumber, value2ColNumber, value3ColNumber,
			value4ColNumber, value5ColNumber;

	@XmlElement(required = true, nillable = true)
	String delimiter;

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getFileToken() {
		return fileToken;
	}

	public void setFileToken(String fileToken) {
		this.fileToken = fileToken;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public int getMsisdnColNumber() {
		return msisdnColNumber;
	}

	public void setMsisdnColNumber(int msisdnColNumber) {
		this.msisdnColNumber = msisdnColNumber;
	}

	public int getFnameColNumber() {
		return fnameColNumber;
	}

	public void setFnameColNumber(int fnameColNumber) {
		this.fnameColNumber = fnameColNumber;
	}

	public int getLnameColNumber() {
		return lnameColNumber;
	}

	public void setLnameColNumber(int lnameColNumber) {
		this.lnameColNumber = lnameColNumber;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getListDescription() {
		return listDescription;
	}

	public void setListDescription(String listDescription) {
		this.listDescription = listDescription;
	}

	public int getValue1ColNumber() {
		return value1ColNumber;
	}

	public void setValue1ColNumber(int value1ColNumber) {
		this.value1ColNumber = value1ColNumber;
	}

	public int getValue2ColNumber() {
		return value2ColNumber;
	}

	public void setValue2ColNumber(int value2ColNumber) {
		this.value2ColNumber = value2ColNumber;
	}

	public int getValue3ColNumber() {
		return value3ColNumber;
	}

	public void setValue3ColNumber(int value3ColNumber) {
		this.value3ColNumber = value3ColNumber;
	}

	public int getValue4ColNumber() {
		return value4ColNumber;
	}

	public void setValue4ColNumber(int value4ColNumber) {
		this.value4ColNumber = value4ColNumber;
	}

	public int getValue5ColNumber() {
		return value5ColNumber;
	}

	public void setValue5ColNumber(int value5ColNumber) {
		this.value5ColNumber = value5ColNumber;
	}

	public void isValid() {
		// TODO: col no. can't be same for all fields
	}

	@Override
	public String toString() {
		String str = "File type: " + fileType;
		str += ", File token: " + fileToken;
		str += ", using list name: " + listName;
		str += ", List Description: " + listDescription;
		str += ". Also file has [";
		str += fnameColNumber != -1 ? ("Fname colNum. " + fnameColNumber) : "";
		str += lnameColNumber != -1 ? (", Lname colNum. " + lnameColNumber) : "";
		str += msisdnColNumber != -1 ? (", MSISDN colNum. " + msisdnColNumber) : "";
		str += value1ColNumber != -1 ? ("value1 colNum. " + value1ColNumber) : "";
		str += value2ColNumber != -1 ? ("value2 colNum. " + value2ColNumber) : "";
		str += value3ColNumber != -1 ? ("value3 colNum. " + value3ColNumber) : "";
		str += value4ColNumber != -1 ? ("value4 colNum. " + value4ColNumber) : "";
		str += value5ColNumber != -1 ? ("value5 colNum. " + value5ColNumber) : "";

		str += "].";

		return str;
	}
}
