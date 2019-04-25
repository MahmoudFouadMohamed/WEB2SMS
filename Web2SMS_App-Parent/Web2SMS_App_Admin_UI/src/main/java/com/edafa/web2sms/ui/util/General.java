package com.edafa.web2sms.ui.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;

@ManagedBean
@ApplicationScoped
public class General {

	String logValue;

	Map<String, Object> moduleLogValues;
	Map<String, Object> ApplicationLogValues;
	Map<String, Object> dateFormatsValues;
	Map<String, Object> startTimeIntervl;
	Map<String, Object> supportedLang;
//	Map<String, Object> paymentModesBillingProp;
//	Map<String, Object> ChannelStatusMap;
//	Map<String, String> lookupForServicesNames;
//	Map<String, String> lookupForBillPaymentTypes;
//	Map<String, String> SchedulerStatusNames;
//	Map<String, String> SysServersTypes;
	Map<String, String> SMSMSISDNTypes;
	Map<String, String> WeeksNo;

	ArrayList<String> Days;

	@PostConstruct
	public void init() {

		// //////////////////////////////////////////////////
		Days = new ArrayList<String>();
		for (int i = 1; i < 29; i++) {
			Days.add(String.valueOf(i));

		}

		// ///////////////////////////
		WeeksNo = new LinkedHashMap<String, String>();
		WeeksNo.put("Saturday", String.valueOf(Calendar.SATURDAY)); // label,
																	// value
		WeeksNo.put("Sunday", String.valueOf(Calendar.SUNDAY));
		WeeksNo.put("Monday", String.valueOf(Calendar.MONDAY));
		WeeksNo.put("Tuesday", String.valueOf(Calendar.TUESDAY));
		WeeksNo.put("Wednesday", String.valueOf(Calendar.WEDNESDAY));
		WeeksNo.put("Thursday", String.valueOf(Calendar.THURSDAY));
		WeeksNo.put("Friday", String.valueOf(Calendar.FRIDAY));
		// //////////////////////////////

		// //////////////////////////////////////////////////////////////
		startTimeIntervl = new LinkedHashMap<String, Object>();
		startTimeIntervl.put("Second", "Second"); // label, value
		startTimeIntervl.put("Minute", "Minute");
		startTimeIntervl.put("Hour", "Hour");
		startTimeIntervl.put("Day", "Day");
		// //////////////////////////////
//		paymentModesBillingProp = new LinkedHashMap<String, Object>();
//		paymentModesBillingProp.put("Credit Card", "R"); // label, value
//		paymentModesBillingProp.put("Transfer", "U");
//		paymentModesBillingProp.put("Direct Debit", "D");
//		paymentModesBillingProp.put("Cash", "C");
		// //////////////////////////////
		SMSMSISDNTypes = new LinkedHashMap<String, String>();
		SMSMSISDNTypes.put("International", "INTL"); // label, value
		SMSMSISDNTypes.put("Subscriber", "SUB");
		SMSMSISDNTypes.put("Alphanumeric", "ALPHAN");
		SMSMSISDNTypes.put("National", "NAT");
		SMSMSISDNTypes.put("Short Code", "SC");

		// //////////////////////////////
		moduleLogValues = new LinkedHashMap<String, Object>();
		moduleLogValues.put("Debug", "debug");
		moduleLogValues.put("Info", "info"); // label, value

		moduleLogValues.put("Warnning", "warnning");
		moduleLogValues.put("Error", "error");
		// //////////////////////////////
		ApplicationLogValues = new LinkedHashMap<String, Object>();
		ApplicationLogValues.put("Info", "info"); // label, value
		ApplicationLogValues.put("Debug", "debug");
		// ///////////////////////////
		dateFormatsValues = new LinkedHashMap<String, Object>();
		dateFormatsValues.put("yyyyMMdd", "yyyyMMdd"); // label, value
		dateFormatsValues.put("mmyyyydd", "mmyyyydd");
		dateFormatsValues.put("ddmmyyyy", "ddmmyyyy");
		dateFormatsValues.put("ddyyyymm", "ddyyyymm");
		dateFormatsValues.put("yyyyddmm", "yyyyddmm");
		dateFormatsValues.put("mmddyyyy", "mmddyyyy");
		// //////////////////////////////
		supportedLang = new LinkedHashMap<String, Object>();
		supportedLang.put("English", "EN"); // label, value
		supportedLang.put("Arabic", "AR");

	}

	public String getCeil(String num) {
		String result = null;

		int ceil = (int) Math.ceil(Double.valueOf(num));

		if (ceil == 0) {
			result = String.valueOf("1");
		}// end if
		else {
			result = String.valueOf(ceil);
		}// end else

		return result;
	}

	public String piastersToLE(String num) {
		if (num != null && !num.isEmpty()) {
			try {
				long value = Long.valueOf(num);
				value = value / 100L;
				return String.valueOf(value);
			} catch (Exception e) {
				return "";
			}

		}

		return "";
	}

	public String getCeil(String num, String rowCount) {
		double page = Double.parseDouble(num);
		double count = Double.parseDouble(rowCount);
		String result = null;
		if (page < count) {
			page++;
		}
		int ceil = (int) Math.ceil(page);

		if (ceil == 0) {
			result = String.valueOf("1");
		}// end if
		else {
			result = String.valueOf(ceil);
		}// end else

		return result;
	}
	
	public String getFacesSeverity() {
		String severityStr="";
		if(FacesContext.getCurrentInstance().getMaximumSeverity()!=null){
			severityStr= FacesContext.getCurrentInstance().getMaximumSeverity().toString();
			if(severityStr.contains("INFO")){
				severityStr="disclaimer success";
			}else if(severityStr.contains("ERROR")){
				severityStr="disclaimer error";
			}else{
				severityStr="";
			}
		}		
		return severityStr;
	}

	public double roundTwoDecimals(double d) 
	{
		if(Double.isNaN(d))
		{
			return 0;
		}// end if
		else
		{
			DecimalFormat twoDForm = new DecimalFormat("#.##");
			return Double.valueOf(twoDForm.format(d));
		}// end else
	}

	public String getLogValue() {
		return logValue;
	}

	public void setLogValue(String logValue) {
		this.logValue = logValue;
	}

	public Map<String, Object> getModuleLogValues() {
		return moduleLogValues;
	}

	public void setModuleLogValues(Map<String, Object> moduleLogValues) {
		this.moduleLogValues = moduleLogValues;
	}

	public Map<String, Object> getDateFormatsValues() {
		return dateFormatsValues;
	}

	public void setDateFormatsValues(Map<String, Object> dateFormatsValues) {
		this.dateFormatsValues = dateFormatsValues;
	}

	public Map<String, Object> getApplicationLogValues() {
		return ApplicationLogValues;
	}

	public void setApplicationLogValues(Map<String, Object> applicationLogValues) {
		ApplicationLogValues = applicationLogValues;
	}

	public Map<String, Object> getStartTimeIntervl() {
		return startTimeIntervl;
	}

	public void setStartTimeIntervl(Map<String, Object> startTimeIntervl) {
		this.startTimeIntervl = startTimeIntervl;
	}

	public Map<String, Object> getSupportedLang() {
		return supportedLang;
	}

	public void setSupportedLang(Map<String, Object> supportedLang) {
		this.supportedLang = supportedLang;
	}

//	public Map<String, Object> getPaymentModesBillingProp() {
//		return paymentModesBillingProp;
//	}
//
//	public void setPaymentModesBillingProp(Map<String, Object> paymentModesBillingProp) {
//		this.paymentModesBillingProp = paymentModesBillingProp;
//	}
//
//	public Map<String, Object> getChannelStatusMap() {
//		return ChannelStatusMap;
//	}
//
//	public void setChannelStatusMap(Map<String, Object> channelStatusMap) {
//		ChannelStatusMap = channelStatusMap;
//	}
//
	// public ServiceName get(String abbreviation) {
	// return lookupForServicesNames.get(abbreviation);
	// }

	// public Map<String, String> getLookupForServicesNames() {
	// return lookupForServicesNames;
	// }
	//
	// public void setLookupForServicesNames(Map<String, String>
	// lookupForServicesNames) {
	// this.lookupForServicesNames = lookupForServicesNames;
	// }
	//
	// public Map<String, String> getLookupForBillPaymentTypes() {
	// return lookupForBillPaymentTypes;
	// }
	//
	// public void setLookupForBillPaymentTypes(Map<String, String>
	// lookupForBillPaymentTypes) {
	// this.lookupForBillPaymentTypes = lookupForBillPaymentTypes;
	// }
	//
	// public Map<String, String> getSchedulerStatusNames() {
	// return SchedulerStatusNames;
	// }
	//
	// public void setSchedulerStatusNames(Map<String, String>
	// schedulerStatusNames) {
	// SchedulerStatusNames = schedulerStatusNames;
	// }
	//
	// public Map<String, String> getSysServersTypes() {
	// return SysServersTypes;
	// }
	//
	// public void setSysServersTypes(Map<String, String> sysServersTypes) {
	// SysServersTypes = sysServersTypes;
	// }

	public Map<String, String> getSMSMSISDNTypes() {
		return SMSMSISDNTypes;
	}

	public void setSMSMSISDNTypes(Map<String, String> sMSMSISDNTypes) {
		SMSMSISDNTypes = sMSMSISDNTypes;
	}

	public ArrayList<String> getDays() {
		return Days;
	}

	public void setDays(ArrayList<String> days) {
		Days = days;
	}

	public Map<String, String> getWeeksNo() {
		return WeeksNo;
	}

	public void setWeeksNo(Map<String, String> weeksNo) {
		WeeksNo = weeksNo;
	}

}
