package com.edafa.web2sms.dalayer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class DateTimeAdapter extends XmlAdapter<String, Date> {

	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

	@Override
	public String marshal(Date v) {
		try {
			if (v == null) {
				return null;
			} else {
				return dateFormat.format(v);
			}
		} catch (Exception e) {
			return null;
		}

	}
	@Override
	public Date unmarshal(String v) {
		try {
			if (v == null) {
				return null;
			} else {
				return dateFormat.parse(v);
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

}
