package com.edafa.web2sms.service.model;

public class FileValidation {

	private int validContacts = 0, invalidContacts = 0, totalContacts = 0, createdlistId;

	public int getCreatedlistId() {
		return createdlistId;
	}

	public void setCreatedlistId(int createdlistId) {
		this.createdlistId = createdlistId;
	}

	public int getValidContacts() {
		return validContacts;
	}

	public void setValidContacts(int validContacts) {
		this.validContacts = validContacts;
	}

	public int getInvalidContacts() {
		return invalidContacts;
	}

	public void setInvalidContacts(int invalidContacts) {
		this.invalidContacts = invalidContacts;
	}

	public int getTotalContacts() {
		return totalContacts;
	}

	public void setTotalContacts(int totalContacts) {
		this.totalContacts = totalContacts;
	}

	public void incValidContacts() {
		this.validContacts++;
		this.totalContacts++;

	}

	public void incInvalidContacts() {
		this.invalidContacts++;
		this.totalContacts++;
	}

	// public FileDetails getFileDetails() {
	// return fileDetails;
	// }
	// public void setFileDetails(FileDetails fileDetails) {
	// this.fileDetails = fileDetails;
	// }
	//
	//

}
