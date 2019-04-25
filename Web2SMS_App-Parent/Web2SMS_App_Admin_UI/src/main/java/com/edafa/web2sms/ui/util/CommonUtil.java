package com.edafa.web2sms.ui.util;

import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.utils.TrxId;

public class CommonUtil {

	public static String TRX_PREFIX = "1";
	private static Pattern pattern;
	private static Matcher matcher;

	private static final String IPADDRESS_PATTERN = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
			+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	private static final String EMAIL_ADDRESS_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	public static AdminTrxInfo manageTrxInfo(String ID) {
		Admin admin = new Admin();
		if (getObjectFromSession("user") != null) {
			admin = (Admin) getObjectFromSession("user");
		}
		AdminTrxInfo trxInfo = new AdminTrxInfo();
		trxInfo.setAdmin(admin);
		trxInfo.setTrxId(ID);
		return trxInfo;
	}

	public static AdminTrxInfo manageTrxInfo() {
		String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
		return manageTrxInfo(trxID);
	}
	/**
	 * Validate ip address with regular expression
	 * 
	 * @param ipStr
	 *            ip address for validation
	 * @return true valid ip address, false invalid ip address
	 */
	public static boolean checkIP(String ipStr) {
		pattern = Pattern.compile(IPADDRESS_PATTERN);
		matcher = pattern.matcher(ipStr);
		return matcher.matches();

	}

	public static boolean validateMobileNumber(String mobileNumber) {
		boolean isValidMobileNumberFormat = true;

		// Validating mobile number
		// contains only numbers, and length is 10 or 12
		// ex. 1028080569,01028080569,201028080569
		if (mobileNumber.matches("[0-9]{10}") || mobileNumber.matches("[0-9]{11}") || mobileNumber.matches("[0-9]{12}")) {
			isValidMobileNumberFormat = true;
		}// end if
		else {
			isValidMobileNumberFormat = false;
		}// end else

		return isValidMobileNumberFormat;
	}// end of method validateMobileNumber

	/**
	 * @author tmohamed Validate Email Address using Regular Expression
	 * @param emailAddress
	 *            email address to validate
	 * @return true if emailAddress is valid, false elsewhere
	 */
	public static boolean checkEmailAddress(String emailAddress) {
		pattern = Pattern.compile(EMAIL_ADDRESS_PATTERN);
		matcher = pattern.matcher(emailAddress);
		return matcher.matches();
	}// end of method checkEmailAddress

	/**
	 * Validate Path
	 * 
	 * @param urlPath
	 *            url Path for validation
	 * @return true valid urlPath, false invalid urlPath
	 */
	public static boolean checkPath(String urlPath) {
		return urlPath.startsWith("/");
	}

	private static String getLocalizedLabel(String key, String bundlePath) {
		ResourceBundle bundle = null;

		String message = "";
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		try {
			bundle = ResourceBundle.getBundle(bundlePath, locale);
		} catch (MissingResourceException e) {
		}// end catch

		if (bundle == null) {
			return null;
		}
		try {
			message = bundle.getString(key);
		} catch (Exception e) {
		}
		return message;
	}// end of method getLocalizedLabel

	public static String getLocalizedLabel(String key) {
		return getLocalizedLabel(key, "com.edafa.epg.ui.otp.resources.messages");
	}// end of method getLocalizedLabel

	// public static String getApplicationUri()
	// {
	// try
	// {
	// FacesContext ctxt = FacesContext.getCurrentInstance();
	// ExternalContext ext = ctxt.getExternalContext();
	// URI uri = new URI(ext.getRequestScheme(), null,
	// SMSGWConfigs.STARTUP_HOST_NAME.getValue(),
	// Integer.valueOf(SMSGWConfigs.STARTUP_PORT.getValue()),
	// ext.getRequestContextPath(),
	// null, null);
	// return uri.toASCIIString();
	// }// end try
	// catch (URISyntaxException e)
	// {
	// throw new FacesException(e);
	// }// end catch
	// }// end of method getApplicationUri

	// public static String getLoginPageURI()
	// {
	// return getApplicationUri() + "/Login/loginPage.jsf";
	// }// end of method getLoginPageURI

	public static void PostRedirect(FacesContext facesContext, String URL) {

		HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
		HttpServletResponse response = (HttpServletResponse) facesContext.getExternalContext().getResponse();

		URLConnection connection;
		String url = request.getRequestURL().substring(0, request.getRequestURL().lastIndexOf("/"));
		// FacesContext facesContext = FacesContext.getCurrentInstance();

		response.setStatus(307);
		response.setHeader("Location", url + URL);
		facesContext.responseComplete();

	}

	public static void putObjectIntoSession(String name, Object value) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(name, value);
	}// end of method putObjectIntoSession

	public static Object getObjectFromSession(String name) {
		Object object = FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(name);

		return object;
	}// end of method getObjectFromSession

	public static void removeObjectFromSession(String name) {
		FacesContext.getCurrentInstance().getExternalContext().getSessionMap().remove(name);
	}// end of method removeObjectFromSession

	public static TimeZone getTimeZone() {
		return TimeZone.getDefault();
	}

	// /////////////////////

	public static Date parseDateString(String str) {

		Date dt = new Date();
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("EEEE, MMM dd, yyyy HH:mm:ss a");
			dt = formatter.parse(str);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return dt;
	}

	// ///////////////////////////

	/**
	 * main method is used for testing purposes
	 * 
	 * @author tmohamed
	 * @param args
	 */
	public static void main(String[] args) {
		List<String> emailAddresses = new ArrayList<String>();
		emailAddresses.add("sdfdd");
		emailAddresses.add("11244");
		emailAddresses.add("dfdfd@ddfdf.ddhh@");
		emailAddresses.add("sdff@ddd.nbh");
		emailAddresses.add("taimour_mc115@yahoo.com");
		emailAddresses.add("eng.tamer_mohamed@yahoo.com");
		emailAddresses.add("eng.mohamed.tamer@gmail.com");
		emailAddresses.add("Tamer.Mohamed@Edafa.com");

		for (String emailAddress : emailAddresses) {
			System.out.println(emailAddress + " : " + CommonUtil.checkEmailAddress(emailAddress));
		}// end for
	}// end of main method

}// end of class CommonUtil
