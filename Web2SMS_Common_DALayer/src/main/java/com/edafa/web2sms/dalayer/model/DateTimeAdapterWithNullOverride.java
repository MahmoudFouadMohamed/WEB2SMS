package com.edafa.web2sms.dalayer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapterWithNullOverride extends XmlAdapter<String, Date> {

	private static final String NA = "NA";
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Override
	public String marshal(Date v) {
		try {
			if (v == null) {
				return NA;
			} else {
				return dateFormat.format(v);
			}
		} catch (Exception e) {
			return NA;
		}

	}
	@Override
	public Date unmarshal(String v) {
		try {
			if (v == null || v.equals(NA)) {
				return null;
			} else {
				return dateFormat.parse(v);
			}
		} catch (ParseException e) {
			return null;
		}
	}

}
