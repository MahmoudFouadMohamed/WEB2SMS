package com.edafa.web2sms.utils.security;

import javax.ejb.Local;

/**
 * @author tmohamed
 *
 */

@Local
public interface AESLocal
{
	public String encrypt(String data) throws Exception;
	public String decrypt(String encryptedData) throws Exception;
	public String encrypt(String data,String key) throws Exception;
	public String decrypt(String encryptedData,String key) throws Exception;
}// end of interface AESLocal
