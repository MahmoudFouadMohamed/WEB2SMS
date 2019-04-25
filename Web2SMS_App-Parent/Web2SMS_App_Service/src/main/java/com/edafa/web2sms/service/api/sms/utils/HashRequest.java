package com.edafa.web2sms.service.api.sms.utils;

import java.util.Map;
import java.util.TreeMap;


public class HashRequest {

	/*
	public static void main(String[] args) {

		String externalTrxId = SecureKey.generate();
		
		Map<String,String> request = new TreeMap<String, String>();
		request.put("channelName", "CCP");
		request.put("terminalId", "");
		request.put("externalTrxId", externalTrxId);
		request.put("amount", "100");
		request.put("channelCode", "1234");
		request.put("isPurchase", "true");
		request.put("language", "en");
		request.put( "customerID", "");
		
		String secureHash = Hash.SHA256(request, "FD92A200F4A9EBCB4896177CA2760DD3");
		System.out.println(secureHash);

	}*/
	
	public static String hashCode(Map<String,String> request) {
		String secureHash = Hash.SHA256(request, "EE5A062D60594EABA2F575A6D8D8020B");
		return secureHash; 
	}
	
	public static String hashCodeMD5(Map<String,String> request) {
		String secureHash = Hash.MD5(request, "EE5A062D60594EABA2F575A6D8D8020B");
		return secureHash; 
	}
}
