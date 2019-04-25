package com.edafa.web2sms.service.model;

import com.edafa.csv.record.DefaultCSVRecord;
import com.edafa.csv.record.Field;

public class ExportedContactCSVRecord extends DefaultCSVRecord {
	
	private Field MSISDN = new Field("MSISDN", 0);
	private Field firstName = new Field("firstName", 1);
	private Field lastName = new Field("lastName", 2);

	public ExportedContactCSVRecord(){
		registerField(MSISDN);
		registerField(firstName);
		registerField(lastName);
	}
	
	
	@Override
	public boolean validateRecord(String[] record) {
		return true;
	}


	public Field getMSISDN() {
		return MSISDN;
	}


	public void setMSISDN(Field mSISDN) {
		MSISDN = mSISDN;
	}


	public Field getFirstName() {
		return firstName;
	}


	public void setFirstName(Field firstName) {
		this.firstName = firstName;
	}


	public Field getLastName() {
		return lastName;
	}


	public void setLastName(Field lastName) {
		this.lastName = lastName;
	}
	
	

}
