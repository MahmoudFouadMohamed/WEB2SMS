package com.edafa.web2sms.utils.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import com.edafa.web2sms.utils.security.interfaces.HashingUtilsLocal;

/**
 * Session Bean implementation class HashingUtils
 */


@Stateless
@LocalBean
public class HashedUtils implements HashingUtilsLocal {

    /**
     * Default constructor. 
     */
    public HashedUtils() {
        // TODO Auto-generated constructor stub
    }
    
    public String hashWithMD5(String word) {
        String returnResult = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(word.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
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

            //convert the byte to hex format method 1
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
    
    

}
