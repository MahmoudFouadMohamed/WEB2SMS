package com.edafa.web2sms.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.edafa.web2sms.utils.configs.enums.Configs;

public abstract class DateTimeUtils {

	private static ThreadLocal<DateFormat> dateFormatters = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DateTimeUtils.getLogDateFormat());
		}
	};

	private static ThreadLocal<DateFormat> timeFormatters = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DateTimeUtils.getLogTimeFormat());
		}
	};

	private static ThreadLocal<DateFormat> timestampFormatters = new ThreadLocal<DateFormat>() {

		@Override
		protected DateFormat initialValue() {
			return new SimpleDateFormat(DateTimeUtils.getLogTimestampFormat());
		}
	};

	public static String getLogDateFormat() {
		return (String) Configs.LOG_DATE_FORMAT.getValue();
	}

	public static String getLogTimeFormat() {
		return (String) Configs.LOG_TIME_FORMAT.getValue();
	}

	public static String getLogTimestampFormat() {
		return (String) Configs.LOG_TIMESTAMP_FORMAT.getValue();
	}

	public static DateFormat getLogDateFormater() {
		return dateFormatters.get();
	}

	public static DateFormat getLogTimeFormater() {
		return timeFormatters.get();
	}

	public static DateFormat getLogTimestampFormater() {
		return timestampFormatters.get();
	}

}
