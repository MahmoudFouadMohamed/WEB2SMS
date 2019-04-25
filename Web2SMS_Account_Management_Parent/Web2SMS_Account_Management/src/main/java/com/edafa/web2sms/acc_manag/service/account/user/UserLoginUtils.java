/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.acc_manag.service.account.user;

import com.edafa.web2sms.acc_manag.utils.security.AESLocal;
import com.edafa.web2sms.acc_manag.utils.security.interfaces.HashUtilsLocal;
import java.util.Random;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mahmoud
 */
@Stateless
public class UserLoginUtils {

    @EJB
    HashUtilsLocal hu;

    @EJB
    AESLocal aes;

    private Pattern pattern;
    private Matcher matcher;
    private static final String lower = "abcdefghijklmnopqrstuvwxyz";
    private static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String digit = "1234567890";
    private static final String punct = "#$%&*+-.:;<=>?@^_";
    private static final String PASSWORD_PATTERN = "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[_{}@#$%^!\\[\\]\\(\\) :;,.?|&'\"*+-\\/~`\\\\]).{8,40})";

    public String generateTempPassword(int targetStringLength, Set<String> charTypes) {
//        int leftLimit = 97; // letter 'a'
//        int rightLimit = 122; // letter 'z'
//        Random random = new Random();
//        StringBuilder buffer = new StringBuilder(targetStringLength);
//        for (int i = 0; i < targetStringLength; i++) {
//            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
//            buffer.append((char) randomLimitedInt);
//        }
//
//        return buffer.toString();

        String SALTCHARS = "";
        if (charTypes != null && !charTypes.isEmpty()) {
            for (String charType : charTypes) {
                switch (charType) {
                    case "lower":
                        SALTCHARS += lower;
                        break;
                    case "upper":
                        SALTCHARS += upper;
                        break;
                    case "digit":
                        SALTCHARS += digit;
                        break;
                    case "punct":
                        SALTCHARS += punct;
                        break;
                }
            }
            if (SALTCHARS.isEmpty()) {
                SALTCHARS += lower;
            }
        } else {
            SALTCHARS += lower;
        }
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < targetStringLength) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        return salt.toString();
    }

    public String hashPassword(String loginPassword, String userName, String accountId) {
        String password = "1@?5" + loginPassword + "44tH" + userName + "Dfv4" + accountId;
        return hu.hashWord(password);
    }

    public boolean validatePassword(String hashedPassword, String loginPassword, String userName, String accountId) {
        return hashPassword(loginPassword, userName, accountId).equals(hashedPassword);
    }

    public boolean validatePasswordRules(String password, Set<String> avoidedWords) {

        if (password == null || password.isEmpty()) {
            return false;
        }

        if (avoidedWords != null && !avoidedWords.isEmpty()) {
            for (String word : avoidedWords) {
                if (password.toLowerCase().contains(word.toLowerCase())) {
                    return false;
                }
            }
        }

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public String generateSecureToken(String secureKey, String userName, String accountId) throws Exception {
        String plainData = userName + "@" + accountId + "@" + System.currentTimeMillis();
        return aes.encrypt(plainData, secureKey);
    }

    public void validateSecureToken(String token, String secureKey, String userName, String accountId, int tokenValidatyPeriod) throws Exception {

        String decryptSecureKey = "edafa@web2sms@phase_4";
        String secureKeyPlain = aes.decrypt(secureKey, decryptSecureKey);

        String plainData = aes.decrypt(token, secureKeyPlain);
        String[] parts = plainData.split("@");
        String tokenUserName = parts[0];
        String tokenAccountId = parts[1];
        String tokenTime = parts[2];

        if (!tokenUserName.equals(userName) || !tokenAccountId.equals(accountId)) {
            //@gomaa through token failure excetion
        }

        int tokenCreationTime = Integer.parseInt(tokenTime);
        int lastValidLoginTime = tokenCreationTime + tokenValidatyPeriod * 1000;
        if (System.currentTimeMillis() > lastValidLoginTime) {
            //@gomaa through token expire exception
        }
    }
}
