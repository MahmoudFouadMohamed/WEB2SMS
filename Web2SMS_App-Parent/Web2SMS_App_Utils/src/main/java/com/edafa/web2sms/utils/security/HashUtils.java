package com.edafa.web2sms.utils.security;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.utils.security.interfaces.HashUtilsLocal;

/**
 * Session Bean implementation class HashingUtils
 */

@Stateless
@LocalBean
public class HashUtils implements HashUtilsLocal {

	/**
	 * Default constructor.
	 */
	public HashUtils() {
		// TODO Auto-generated constructor stub
	}

	public String hashWithMD5(String word) {
		String returnResult = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(word.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			returnResult = sb.toString();

		} catch (NoSuchAlgorithmException ex) {
			returnResult = null;
		}
		return returnResult;
	}

	private String hashWithSHA512(String word) {
		String returnResult = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(word.getBytes());

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
			}

			returnResult = sb.toString();

		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			returnResult = null;
		}

		return returnResult;
	}

	public String hashWord(String word) {
		String returnResult = null;
		String MD5output = null;

		if ((MD5output = hashWithMD5(word)) != null) {
			returnResult = hashWithSHA512(MD5output);

		} else {
			returnResult = null;
		}
		return returnResult;
	}

	static final char[] HEX_TABLE = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };

	
	public String SHA256(String hashedFields, String secureSecret) {

		if (hashedFields.isEmpty() || hashedFields == null || secureSecret == null || secureSecret.isEmpty()) {
			return null;
		}

		
//		System.out.println(hashedFields);

		byte[] mac = null;

		try {

//			byte[] byteArray = new BigInteger(secureSecret, 16).toByteArray();
//			SecretKey key = new SecretKeySpec(byteArray, "HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(secureSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

			Mac hmac = Mac.getInstance("HmacSHA256");
			hmac.init(secretKey);
			// Coding with UTF-8
			byte[] btemp = hashedFields.toString().getBytes(StandardCharsets.UTF_8);
			hmac.update(btemp);
			mac = hmac.doFinal();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String hashValue = hex(mac);
		return hashValue;

	}
	
	
	public String SHA256(Map<String, String> fields, String secureSecret) {

		if (fields.isEmpty() || fields == null || secureSecret == null || secureSecret.isEmpty()) {
			return null;
		}

		String hashKeys = new String();
		String hashValues = new String();
		String hashedFields = new String();

		// create a list and sort it
		List fieldNames = new ArrayList(fields.keySet());
		Collections.sort(fieldNames);

		// create a buffer for the SHA256 input
		StringBuffer buf = new StringBuffer();

		// iterate through the list and add the remaining field values
		Iterator itr = fieldNames.iterator();

		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) fields.get(fieldName);
			hashKeys += fieldName + ", ";
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				buf.append(fieldName + "=" + fieldValue);
				if (itr.hasNext()) {
					buf.append('&');
				}
			}
		}

		hashedFields = buf.toString();

		// removing the last &
		int length = buf.length();
		char[] car = new char[1];
		car[0] = buf.charAt(length - 1);
		String lastcharachter = new String(car);
//		System.out.println(lastcharachter);

		if (lastcharachter.equals("&")) {
			buf.deleteCharAt(length - 1);
		}

		hashedFields = buf.toString();
//		System.out.println(hashedFields);

		byte[] mac = null;

		try {

//			byte[] b = new BigInteger(secureSecret, 16).toByteArray();
//			SecretKey key = new SecretKeySpec(b, "HmacSHA256");
			SecretKeySpec secretKey = new SecretKeySpec(secureSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

			Mac m = Mac.getInstance("HmacSHA256");
			m.init(secretKey);
			// Coding with UTF-8
			byte[] btemp = buf.toString().getBytes(StandardCharsets.UTF_8);
			m.update(btemp);
			mac = m.doFinal();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		String hashValue = hex(mac);
		return hashValue;

	} // end hashAllFields()

	public static String MD5(Map<String, String> fields, String secureSecret) {

		// create a list and sort it
		List fieldNames = new ArrayList(fields.keySet());
		Collections.sort(fieldNames);

		// create a buffer for the md5 input and add the secure secret first
		StringBuffer buf = new StringBuffer();
		buf.append(secureSecret);

		// iterate through the list and add the remaining field values
		Iterator itr = fieldNames.iterator();

		while (itr.hasNext()) {
			String fieldName = (String) itr.next();
			String fieldValue = (String) fields.get(fieldName);
			if ((fieldValue != null) && (fieldValue.length() > 0)) {
				buf.append(fieldValue);
			}
		}

		MessageDigest md5 = null;
		byte[] ba = null;

		// create the md5 hash and UTF-8 encode it
		try {
			md5 = MessageDigest.getInstance("MD5");
			ba = md5.digest(buf.toString().getBytes("UTF-8"));
		} catch (Exception e) {
		} // wont happen

		// return buf.toString();
		return hex(ba);
	}

	private static String hex(byte[] input) {

		// create a StringBuffer 2x the size of the hash array
		StringBuffer sb = new StringBuffer(input.length * 2);

		// retrieve the byte array data, convert it to hex
		// and add it to the StringBuffer
		for (int i = 0; i < input.length; i++) {
			sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
			sb.append(HEX_TABLE[input[i] & 0xf]);
		}
		return sb.toString();
	}

}
