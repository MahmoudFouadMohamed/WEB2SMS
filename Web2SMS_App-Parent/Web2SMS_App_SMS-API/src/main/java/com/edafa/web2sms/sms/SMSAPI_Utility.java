package com.edafa.web2sms.sms;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.dao.interfaces.AccountSMSAPIDaoLocal;
import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountSender;
import com.edafa.web2sms.dalayer.model.AccountSenderPK;
import com.edafa.web2sms.dalayer.pojo.AccountSMSAPI;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.acc_manag.service.account.exception.SenderNameNotAttached;
import com.edafa.web2sms.service.list.exception.InvalidRequestException;
import com.edafa.web2sms.sms.exceptions.AccountNotRegisteredOnAPIException;
import com.edafa.web2sms.sms.exceptions.AuthorizationFailedException;
import com.edafa.web2sms.sms.exceptions.NotTrustedIPException;
import com.edafa.web2sms.sms.exceptions.SecretKeyDecryptionFailedException;
import com.edafa.web2sms.sms.model.CampaignRecieverDetails;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.web2sms.utils.security.AESLocal;
import com.edafa.web2sms.utils.security.interfaces.HashUtilsLocal;
import com.edafa.web2sms.utils.sms.SMSUtils;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;

@Stateless
public class SMSAPI_Utility {

        Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
	Logger smsLogger = LogManager.getLogger(LoggersEnum.SMS_API_MNGT.name());
        
        private static Map accountLastReportTime = new HashMap<String, Long>();
        
	@EJB
	AccountSMSAPIDaoLocal accountSMSAPIDao;

	@EJB
	AESLocal aes;

	@EJB
	HashUtilsLocal hu;
	
	static public class DateValidationResult{
		
		Date startDate;
		Date endDate;
		
		
		public Date getStartDate() {
			return startDate;
		}


		public void setStartDate(Date startDate) {
			this.startDate = startDate;
		}


		public Date getEndDate() {
			return endDate;
		}


		public void setEndDate(Date endDate) {
			this.endDate = endDate;
		}


		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder();
			builder.append("DateValidationResult [startDate=");
			builder.append(startDate);
			builder.append(", endDate=");
			builder.append(endDate);
			builder.append("]");
			return builder.toString();
		}
		
		
	}
	
	public void validateRequest(String trxId, Account account, String password, String ip) throws DBException,
			AccountNotFoundException, AccountNotRegisteredOnAPIException, NotTrustedIPException,
			InvalidRequestException {
		if (password != null && !password.isEmpty() && ip != null && !ip.isEmpty()) {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(trxId + "Validating the new request");
                    }                    

                    if (account == null) {
                        throw new AccountNotFoundException(account.getAccountId());
                    }

			AccountSMSAPI accountAPIDetails = accountSMSAPIDao.findPojoByAccountId(account);
			if (accountAPIDetails == null) {
				throw new AccountNotRegisteredOnAPIException(account.getAccountId());
                        }
                    boolean isIPTrusted = false;
                        List<String> trustedIPsList = accountAPIDetails.getAccountIPs();
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(trxId + "Searching for ip(" + ip + ") in trusted ip list: " + trustedIPsList);
                    }
                        for (String accountIP : trustedIPsList) {
				if (accountIP.equals(ip)) {
					isIPTrusted = true;
					break;
				}
			}
			if (!isIPTrusted)
				throw new NotTrustedIPException("The IP(" + ip + ") is not listed on trusted ip list for account("
						+ account.getAccountId() + ").");
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(trxId + "Validating account password.");
                    }
			if (!accountAPIDetails.getPassword().equals(hu.hashWord(password))) {
				throw new InvalidRequestException("please enter the correct password");
			}
		} else {
			throw new InvalidRequestException("This is not valid request.");
		}

            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(trxId + "Request is valid");
            }
	}

	/**
	 * This is a generic function for check hashing of request, in case of no
	 * repeated parameters. NOTE: parameters in request string are ordered
	 * according to their order in parameters map
	 */
	public void checkHashing(String trxId, StringBuilder requestString, String secureHash, Account account, String password)
			throws AuthorizationFailedException, SecretKeyDecryptionFailedException, AccountNotRegisteredOnAPIException {
            if (smsLogger.isDebugEnabled()) {
                    StringBuilder logString = new StringBuilder(requestString);
                if (password != null) {
                    String pass = "&Password=";
                    int index = logString.indexOf(pass);
                    if (index != -1) {
                        logString.delete(index, index + pass.length() + password.length());
                    }
                }
                smsLogger.debug(trxId + "Validating Secure Hash for (" + logString + ")");
            }

		String secureHashSecretKey = "";
		String accountSecretKeyEncrypted;
                AccountSMSAPI accountAPIDetails = accountSMSAPIDao.findPojoByAccountId(account);
            if (accountAPIDetails == null) {
                throw new AccountNotRegisteredOnAPIException(account.getAccountId());
            }
		accountSecretKeyEncrypted = accountAPIDetails.getSecureKey();
		try {
			secureHashSecretKey = aes.decrypt(accountSecretKeyEncrypted);
		} catch (Exception e) {
			throw new SecretKeyDecryptionFailedException();
		}

		String calcHash = hu.SHA256(requestString.toString(), secureHashSecretKey);
		if (!secureHash.equals(calcHash)) {
                    if (password != null) {
                        String pass = "&Password=";
                        int index = requestString.indexOf(pass);
                        if (index != -1) {
                            requestString.delete(index, index + pass.length() + password.length());
                        }
                    }
                    smsLogger.error(trxId + "Authorization failed, Secure Hash String: " + requestString.toString());
			throw new AuthorizationFailedException("Authorization failed, incorrect Secure Hash");
		}
            if (smsLogger.isTraceEnabled()) {
                smsLogger.trace(trxId + "Secure Hash is valid");
            }
	}
        
    public List<SMSDetails> validateSMSList(String trxId, List<SMSDetails> smsList, Account account, String smsIdPrefix, boolean cachedRequest) {
        List<SMSDetails> validSMSList = new ArrayList<>();
        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(trxId + "Validating " + smsList.size() + " SMS(s).");
        }
        List<AccountSender> accountSendersList;
        if (account != null) {
            accountSendersList = account.getAccountSendersList();
            if (smsLogger.isTraceEnabled()) {
                smsLogger.trace(trxId + "Found Senders " + accountSendersList + "for Account(" + account.getAccountId() + ")");
            }
        } else {
            accountSendersList = new ArrayList<>(0);
        }
        AccountSender validSender = null;
        AccountSenderPK accountSendersPK;
        boolean validSMS = false;
        String accountId = null;
        String msisdn = null;
        String senderName = null;
        for (SMSDetails smsDetails : smsList) {
            if(!cachedRequest || cachedRequest & smsDetails.isCachedSMS()){
            validSMS = false;
            accountId = smsDetails.getAccountId();
            msisdn = smsDetails.getReceiverMSISDN();
            senderName = smsDetails.getSenderName();

            if (msisdn != null) {
                if (SMSUtils.validateLocalAddress(msisdn)) {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(trxId + "Valid local MSISDN: " + msisdn);
                    }
                    validSMS = true;
                } else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
                        && SMSUtils.validateInternationalAddress(msisdn)) {
                    if (smsLogger.isTraceEnabled()) {
                        smsLogger.trace(trxId + "Valid international MSISDN: " + msisdn);
                    }
                    validSMS = true;
                } else {
                    if (smsLogger.isDebugEnabled()) {
                        smsLogger.debug(trxId + "Invalid MSISDN: " + msisdn);
                    }
                }
            } else {
                smsLogger.warn(trxId + "MSISDN is null.");
            }

            validSender = null;
            for (AccountSender accountSender : accountSendersList) {
                accountSendersPK = accountSender.getAccountSendersPK();
                if (accountSendersPK != null && accountSendersPK.getSenderName().equals(senderName)) {
                    validSender = accountSender;
                    break;
                }
            }

            if (validSender == null) {
                smsLogger.error(trxId + "Account(" + accountId + ") not eligible to use SenderName(" + senderName + ").");
            }

            if (validSMS && validSender != null && smsDetails.getSMSText() != null && !smsDetails.getSMSText().isEmpty()) {
                smsDetails.setLanguage();
                smsDetails.setDeliveryReport(true);
                
//                smsDetails.setSmsId(SMSId.getSMSId(smsIdPrefix).getId());
                validSMSList.add(smsDetails);
            }
            }
        }
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(trxId + "Found " + validSMSList.size() + " valid SMS(s).");
        }
        return validSMSList;
    }
    
	public CampaignRecieverDetails validateMSISDNs(String trxId, CampaignRecieverDetails smsList) {
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(trxId + "Validating " + smsList.getReceiverMSISDN().size() + " SMS(s).");
            }
		CampaignRecieverDetails validSMSList = new CampaignRecieverDetails();
            Set<String> msisdns = new HashSet<String>();

		for (String msisdn : smsList.getReceiverMSISDN()) {
			boolean validSMS = false;
			try {
				if (msisdn != null) {
					if (SMSUtils.validateLocalAddress(msisdn)) {
                                            if (smsLogger.isTraceEnabled()) {
                                                smsLogger.trace(trxId + "valid local MSISDN: " + msisdn);
                                            }
						validSMS = true;
					} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
							&& SMSUtils.validateInternationalAddress(msisdn)) {
                                            if (smsLogger.isTraceEnabled()) {
                                                smsLogger.trace(trxId + "valid international MSISDN: " + msisdn);
                                            }
						validSMS = true;
					} else {
                                            if (smsLogger.isDebugEnabled()) {
                                                smsLogger.debug(trxId + "Invalid MSISDN: " + msisdn);
                                            }
					}
				} else {
                                    smsLogger.warn(trxId + "MSISDN is null.");
				}

				if (validSMS) {
					msisdns.add(msisdn);
				}
			} catch (Exception ex) {
				appLogger.error(trxId + "exception while validating msisdns.");
				smsLogger.error(trxId + "exception while validating msisdns.");
			}
		}
            validSMSList.setReceiverMSISDN(new ArrayList<String>(msisdns));
            if (smsLogger.isDebugEnabled()) {
                smsLogger.debug(trxId + "Found " + validSMSList.getReceiverMSISDN().size() + " valid MSISDN(s).");
            }

		return validSMSList;
	}

    public void validateSmsId(String smsId) throws InvalidRequestException {
        if (!(smsId != null && !smsId.isEmpty())) {
            throw new InvalidRequestException("SMS ID is empty");
        }
    }

    public boolean validateMSISDN (String trxId, String msisdn){
		boolean validSMS = false;
		try {
			if (msisdn != null) {
				if (SMSUtils.validateLocalAddress(msisdn)) {
                                        if (smsLogger.isTraceEnabled()) {
                                            smsLogger.trace(trxId + " valid local MSISDN: " + msisdn);
                                        }
					validSMS = true;
				} else if ((boolean) Configs.ALLOW_INTERNATOINAL_SMS.getValue()
						&& SMSUtils.validateInternationalAddress(msisdn)) {
                                        if (smsLogger.isTraceEnabled()) {
                                            smsLogger.trace(trxId + " valid international MSISDN: " + msisdn);
                                        }
					validSMS = true;
				} else {
                                        if (smsLogger.isDebugEnabled()) {
                                            smsLogger.debug(trxId + " Invalid MSISDN: " + msisdn);
                                        }
				}
			} else {
                                smsLogger.warn(trxId + " MSISDN is null.");
			}

		} catch (Exception e) {
			
			smsLogger.warn(trxId + " Exception: " + e.getMessage());
			appLogger.error(trxId + " Exception: " + e.getMessage(), e);
                // TODO raise alarm ??!
		}
		return validSMS;
    }

    private static ThreadLocal<DateFormat> formatter = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd/MM/yyyy");
        }
    };
    
    public DateValidationResult validateDates(String trxId, String startDate, String enddate) throws InvalidRequestException {
       
    	DateValidationResult dateValidationResult= new DateValidationResult();
	   if (smsLogger.isTraceEnabled()) {
           smsLogger.trace(trxId + "start date: " + startDate + ", end date: " + enddate);
       }
	   if (!(startDate != null && !startDate.isEmpty() && enddate != null && !enddate.isEmpty())) {
            throw new InvalidRequestException("Empty Required fields");
        }else {
			try {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(new Date());
                                calendar.add(Calendar.MONTH, -(int) Configs.SMSAPI_INQUIRE_MONTHES.getValue());
                                Date startSearch = calendar.getTime();
				Date sDate = (Date) formatter.get().parse(startDate);
				Date eDate = (Date) formatter.get().parse(enddate);
				if(sDate.before(startSearch)){
                                    if (!eDate.after(startSearch)) {
                                        smsLogger.warn(trxId + "Start and end dates too early");
                                        throw new InvalidRequestException("Dates range are not supported");
                                    }
                                    if (smsLogger.isTraceEnabled()) {
                                        smsLogger.trace(trxId + "Start date too early, will set it with: " + startSearch);
                                    }
                                    sDate = startSearch;
                                }
				dateValidationResult.setStartDate(sDate);
				dateValidationResult.setEndDate(eDate);
				 if (smsLogger.isTraceEnabled()) {
			           smsLogger.trace(trxId + "formatted start date: " + sDate + ", formatted end date: " + eDate);
			        }
				 if(!sDate.before(eDate))
					 throw new InvalidRequestException("Dates are not ordered");
			} catch (ParseException e) {
				smsLogger.error("ParseException : "+ e.getMessage());
				appLogger.error("ParseException : " + e);
				 throw new InvalidRequestException("Dates are in invalid formates");
			}
        }
      return dateValidationResult;  
    }
        
	public void validateSenderName(String trxId, String senderName, Account account) throws SenderNameNotAttached {
            AccountSender sender = null;
            List<AccountSender> accountSendersList;
            if (account != null) {
                accountSendersList = account.getAccountSendersList();
                if (smsLogger.isTraceEnabled()) {
                    smsLogger.trace(trxId + "Found Senders " + accountSendersList + "for Account(" + account.getAccountId() + ")");
                }
            } else {
                accountSendersList = new ArrayList<>(0);
            }

            for (AccountSender accountSender : accountSendersList) {
                if (accountSender.getAccountSendersPK() != null && accountSender.getAccountSendersPK().getSenderName().equals(senderName)) {
                    sender = accountSender;
                    break;
                }
            }

		if (sender == null) {
			smsLogger.error(trxId + " Account(" + account.getAccountId() + ") not eligible to use SenderName(" + senderName + ").");
			throw new SenderNameNotAttached();
		}
	}     

    public boolean checkPassingReportTimeInterval(String trxId, String accountId) {
        Long lastReportTime = (Long) accountLastReportTime.get(accountId);
        int smsapiReportTimeInterval = (int) Configs.SMSAPI_REPORT_TIME_INTERVAL.getValue();

        if (smsLogger.isTraceEnabled()) {
            smsLogger.trace(trxId + "Check account last report time");
        }

        boolean returnResult = true;
        if (lastReportTime != null) {
            if (System.currentTimeMillis() - lastReportTime > smsapiReportTimeInterval) {
                accountLastReportTime.put(accountId, System.currentTimeMillis());
                returnResult = true;
            } else {
                returnResult = false;
            }
        } else {
            accountLastReportTime.put(accountId, System.currentTimeMillis());
            returnResult = true;
        }
        if (smsLogger.isDebugEnabled()) {
            smsLogger.debug(trxId + "Check account last report time result " + returnResult);
        }
        return returnResult;
    }

}
