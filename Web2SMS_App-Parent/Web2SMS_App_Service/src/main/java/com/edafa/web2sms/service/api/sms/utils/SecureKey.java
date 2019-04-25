package com.edafa.web2sms.service.api.sms.utils;

import java.security.Key;
import java.util.UUID;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import javax.ejb.Stateless;
import javax.xml.bind.DatatypeConverter;

import com.edafa.web2sms.service.api.sms.utils.interfaces.SecureKeyLocal;

/*
#: Title 		: SecureKey
#: Date 		: 2013-10-29 1:04 PM
#: Author 		: Hesham Habib <hesham.habib@edafa.com>
#: Modified		: Khalid Abu El-Soud <khalid.abuelsoud@edafa.com>
#: Version 		: 1.0
#: Description 	: This class for generating a random key used for hashing
#: Options 		: This   class  generates  128-bit  random   key 
				  using  universally  unique identifier  (UUID).
				  The methods of this class are for manipulating 
				  the  Leach-Salz  variant. 
*/
@Stateless
public class SecureKey implements SecureKeyLocal{

	public String generateSecureKey() {
		return UUID.randomUUID().toString().replaceAll("-","").toUpperCase();
	}
	
	/*public static void main(String[] args) {
		
		System.out.println(SecureKey.generate());
	}*/
}
