package com.edafa.web2sms.acc_manag.utils.security;

/**
 * Session Bean implementation class EncryptionUtil
 */
/**
 * Default constructor. 
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.crypto.Cipher;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.acc_manag.utils.configs.enums.Configs;
import com.edafa.web2sms.acc_manag.utils.configs.enums.LoggersEnum;


/**
 * @author JavaDigest
 * 
 */
@Singleton
@LocalBean
@Startup
// @DependsOn("SMSGW_Utils/AccManagLoggingManagerBean")
public class EncryptionUtil implements EncryptionUtilLocal {
	/**
	 * String to hold name of the encryption algorithm.
	 */
	public final String ALGORITHM = "RSA";
	/**
	 * String to hold the name of the private key file.
	 */
	// public final String PRIVATE_KEY_FILE = "private.key";
	//
	// /**
	// * String to hold name of the public key file.
	// */
	// public final String PUBLIC_KEY_FILE = "public.key";

	@Resource(name = "java:app/env/basedir")
	String basePath;

	PublicKey publicKey;
	PrivateKey privateKey;

	Logger appLogger = LogManager.getLogger(LoggersEnum.ACCOUNT_MNGMT_UTILS.name());

	@PostConstruct
	void init() {
		// String basePath = AppSettings.BaseDir.getEnvEntryValue();
		String securityPath = basePath + Configs.SECURITY_FILES_PATH.getValue();
		String fileName = "keys";
		ObjectInputStream inputStream = null;
		try {
			File keyFile = new File(securityPath + fileName);
			inputStream = new ObjectInputStream(new FileInputStream(keyFile));
			publicKey = (PublicKey) inputStream.readObject();
			inputStream.close();
		} catch (FileNotFoundException e) {
			appLogger.error("Cannot start the application, no encryption key found");
			throw new Error("Cannot find encryption key at " + securityPath, e);
		} catch (IOException e) {
			appLogger.error("Cannot start the application, no encryption key found");
			throw new Error("Cannot open encryption key", e);
		} catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}

	/**
	 * Encrypt the plain text using public key.
	 * 
	 * @param text
	 *            : original plain text
	 * @param key
	 *            :The public key
	 * @return Encrypted text
	 * @throws java.lang.Exception
	 */
	public byte[] encrypt(String text, Key key) {
		if (key == null)
			return null;
		byte[] cipherText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);
			// encrypt the plain text using the public key
			cipher.init(Cipher.ENCRYPT_MODE, key);
			cipherText = cipher.doFinal(text.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return cipherText;
	}

	/**
	 * Decrypt text using private key.
	 * 
	 * @param text
	 *            :encrypted text
	 * @param key
	 *            :The private key
	 * @return plain text
	 * @throws java.lang.Exception
	 */
	public String decrypt(byte[] text, Key key) {
		if (key == null)
			return null;
		byte[] dectyptedText = null;
		try {
			// get an RSA cipher object and print the provider
			final Cipher cipher = Cipher.getInstance(ALGORITHM);

			// decrypt the text using the private key
			cipher.init(Cipher.DECRYPT_MODE, key);
			dectyptedText = cipher.doFinal(text);

		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return new String(dectyptedText);
	}

	@Override
	public String decrypt(byte[] text) {
		return decrypt(text, publicKey);
	}

	@Override
	public String encryptString(String str) {
		return new String(encrypt(str, privateKey));
	}

	@Override
	public String decryptString(String str) {
		return new String(encrypt(str, publicKey));
	}

}
