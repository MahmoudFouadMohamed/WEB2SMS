package com.edafa.web2sms.service.model;

import com.edafa.csv.exceptions.InvalidCSVRecord;
import com.edafa.csv.record.CSVRecord;
import com.edafa.csv.record.DefaultCSVRecord;
import com.edafa.csv.record.Field;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.sms.SMSUtils;

public class ContactCSVRecord extends DefaultCSVRecord {
	private Field firstName = new Field("First Name", 0);
	private Field lastName = new Field("Last Name", 1);
	private Field MSISDN = new Field("MSISDN", 2);

	public ContactCSVRecord(Field field) {
		// this.receiverMSISDN = receiverMSISDN;
		registerField(field);
	}

	public ContactCSVRecord() {
		registerField(MSISDN);
		registerField(firstName);
		registerField(lastName);
	}

	public void reRegisterFields() {
		unregisterAllFields();
		registerField(MSISDN);
		registerField(firstName);
		registerField(lastName);
	}

	@Override
	public boolean validateRecord(String[] record) {
		boolean isValid = false;
		try {
			fillFields(record);
		} catch (InvalidCSVRecord e) {
			return false;
		}

		if (SMSUtils.validateLocalAddress(fieldsMap.get(MSISDN.getSeq()).getValue())) {
			isValid = true;
		} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
				&& SMSUtils.validateInternationalAddress(MSISDN.getValue())) {
			isValid = true;
		}

		this.isValid = isValid;
		return isValid;
	}

	public Field getMSISDN() {
		return fieldsMap.get(MSISDN.getSeq());
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

	@Override
	public CSVRecord clone() throws CloneNotSupportedException {
		ContactCSVRecord newRecord = (ContactCSVRecord) super.clone();
		Field newRecFirstName = newRecord.getField(firstName.getSeq());
		Field newRecLastName = newRecord.getField(lastName.getSeq());
		Field newRecMSISDN = newRecord.getField(MSISDN.getSeq());
		newRecord.setFirstName(newRecFirstName);
		newRecord.setLastName(newRecLastName);
		newRecord.setMSISDN(newRecMSISDN);

		return newRecord;
	}
}
