package com.edafa.web2sms.service.list.excel;

import java.util.Set;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

import com.edafa.web2sms.dalayer.model.Contact;
import com.edafa.web2sms.dalayer.model.ListContactPK;
import com.edafa.web2sms.service.list.files.FileDetails;
import com.edafa.web2sms.service.model.FileValidation;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.sms.SMSUtils;

public class XLSXSheethandler implements XSSFSheetXMLHandler.SheetContentsHandler {

	private FileDetails fileDetails;
	private FileValidation validation;
	Contact tmpContact;
	int cellNumber;
	int maxChar = (int) Configs.MAX_CONTACT_NAME_CHAR.getValue();
	boolean validContactFlag;
	Set<Contact> validContactSet;
	Logger listLogger = LogManager.getLogger(LoggersEnum.LIST_MNGMT.name());

	public XLSXSheethandler(FileDetails fileDetails, FileValidation validation, Set<Contact> validContactSet) {
		this.fileDetails = fileDetails;
		this.validation = validation;
		this.validContactSet = validContactSet;
	}

	@Override
	public void startRow(int rowNum) {
		tmpContact = new Contact();
		cellNumber = 0;
		validContactFlag = false;

	}

	@Override
	public void endRow(int paramInt) {
		if (validContactFlag) {

			validContactSet.add(tmpContact);
		}
	}

	@Override
	public void cell(String cellReference, String formattedValue, XSSFComment paramXSSFComment){//String cellReference, String formattedValue) {

		if (cellNumber == fileDetails.getMsisdnColNumber()) {

			if (formattedValue != null) {
				if (SMSUtils.validateLocalAddress(formattedValue)) {

					tmpContact.setListContactsPK(new ListContactPK(validation.getCreatedlistId(), formattedValue));
					validation.incValidContacts();
					validContactFlag = true;

				} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
						&& SMSUtils.validateInternationalAddress(formattedValue)) {
					tmpContact.setListContactsPK(new ListContactPK(validation.getCreatedlistId(), formattedValue));
					validation.incValidContacts();
					validContactFlag = true;
				} else {
					if (!formattedValue.equalsIgnoreCase("MSISDN") || !formattedValue.equalsIgnoreCase("number")
							|| !formattedValue.equalsIgnoreCase("mobile"))
						validation.incInvalidContacts();
				}
			}
		} else if (cellNumber == fileDetails.getFnameColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setFirstName(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(
						0, maxChar));
		} else if (cellNumber == fileDetails.getLnameColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setLastName(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(
						0, maxChar));
		} else if (cellNumber == fileDetails.getValue1ColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setValue1(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(0,
						maxChar));
		} else if (cellNumber == fileDetails.getValue2ColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setValue2(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(0,
						maxChar));
		} else if (cellNumber == fileDetails.getValue3ColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setValue3(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(0,
						maxChar));
		} else if (cellNumber == fileDetails.getValue4ColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setValue4(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(0,
						maxChar));
		} else if (cellNumber == fileDetails.getValue5ColNumber() && formattedValue != null) {
			if (!formattedValue.isEmpty())
				tmpContact.setValue5(formattedValue.length() <= maxChar ? formattedValue : formattedValue.substring(0,
						maxChar));
		}

		cellNumber++;
	}

	@Override
	public void headerFooter(String paramString1, boolean paramBoolean, String paramString2) {

	}

	public Set<Contact> getValidContactSet() {
		return validContactSet;
	}

}
