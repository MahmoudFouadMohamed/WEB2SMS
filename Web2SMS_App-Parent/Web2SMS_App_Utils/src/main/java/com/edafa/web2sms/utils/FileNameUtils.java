package com.edafa.web2sms.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileNameUtils {

	public static String getUniqueFileName() {
		String postfix = String.valueOf((int) (Math.random() * 100));
		return getUniqueFileName("", postfix);
	}

	public static String getUniqueFileName(String prefix, String postfix) {
		DateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmssSSS");
		return df.format(prefix + new Date()) + "_" + postfix;
	}

	public static String encodeFileToken(final String outFilePathName) {
		return outFilePathName.replaceAll("\\.", "?").replaceAll("/", ".");
	}

	public static String decodeFileToken(final String outFilePathName) {
		return outFilePathName.replaceAll("\\.", "/").replaceAll("\\?", ".");
	}
}
