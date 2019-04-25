package com.edafa.web2sms.utils.security;

import java.security.Key;

import javax.annotation.PostConstruct;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.LocalBean;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateless;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

//@Singleton
//@LocalBean
//@Startup
//@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
//@Lock(LockType.READ)
@Stateless
public class AES implements AESLocal
{
	private static final String ALGO = "AES";
	private static final byte[] keyValue = new byte[] { 'E', 'D', 'A', 'F', 'A', 
		'T', 'E', 'L', 'E', 'C', 'O', 'M', 
		'S', 'O', 'L', 'U' };
	
	private Key key;
	
	@PostConstruct
	void init(){
		key = new SecretKeySpec(keyValue, ALGO);
	}
	
	@Override
	public String encrypt(String data) throws Exception
	{
		//Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, key);
		byte[] encVal = c.doFinal(data.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}// end of method encrypt

	@Override
	public String decrypt(String encryptedData) throws Exception
	{
		//Key key = generateKey();
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, key);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}// end of method decrypt

	private Key generateKey() throws IllegalArgumentException
	{
		Key key = new SecretKeySpec(keyValue, ALGO);
		return key;
	}// end of method generateKey
	
	private Key generateKey(String keyValue) throws IllegalArgumentException
	{
		Key key = new SecretKeySpec(keyValue.getBytes(), ALGO);
		return key;
	}// end of method generateKey

	@Override
	public String encrypt(String data, String key) throws Exception
	{
		Key encryptKey = generateKey(key);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.ENCRYPT_MODE, encryptKey);
		byte[] encVal = c.doFinal(data.getBytes());
		String encryptedValue = new BASE64Encoder().encode(encVal);
		return encryptedValue;
	}// end of method encrypt

	@Override
	public String decrypt(String encryptedData, String key) throws Exception
	{
		Key decryptKey = generateKey(key);
		Cipher c = Cipher.getInstance(ALGO);
		c.init(Cipher.DECRYPT_MODE, decryptKey);
		byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
		byte[] decValue = c.doFinal(decordedValue);
		String decryptedValue = new String(decValue);
		return decryptedValue;
	}// end of method decrypt
	
}// end of class AES
