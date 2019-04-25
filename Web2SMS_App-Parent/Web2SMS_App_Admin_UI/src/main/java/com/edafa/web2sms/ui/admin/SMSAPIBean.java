package com.edafa.web2sms.ui.admin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.model.Account;
import com.edafa.web2sms.dalayer.model.AccountIP;
import com.edafa.web2sms.acc_manag.service.account.exception.AccountNotFoundException;
import com.edafa.web2sms.service.accountmanagement.facing.interfaces.AdminAccountManagementFacingLocal;
import com.edafa.web2sms.service.api.sms.exceptions.InvalidAccountSMSApiPasswordException;
import com.edafa.web2sms.service.api.sms.exceptions.InvalidIpsListException;
import com.edafa.web2sms.service.api.sms.interfaces.SMSAPIManagementBeanLocal;
import com.edafa.web2sms.service.api.sms.model.ActivateSMSAPIRequest;
import com.edafa.web2sms.service.model.AdminTrxInfo;
import com.edafa.web2sms.ui.util.CommonUtil;
import com.edafa.web2sms.utils.TrxId;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

@ManagedBean(name = "sMSAPIBean")
@ViewScoped
public class SMSAPIBean
{
	private final Logger adminLogger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	@EJB
	private AdminAccountManagementFacingLocal adminAccountManagement;
	
	@EJB
	private SMSAPIManagementBeanLocal smsManagementBeanLocal;

	private FacesContext facesContext = FacesContext.getCurrentInstance();
	private String messageBundleName = facesContext.getApplication().getMessageBundle();
	private Locale locale = facesContext.getViewRoot().getLocale();
	private ResourceBundle bundle = ResourceBundle.getBundle(messageBundleName, locale);

	private String accountID = "";
	private String companyName = "";
	private String billingMSISDN = "";

	private List<Account> accounts = new ArrayList<Account>();

	boolean filtered = false;
	boolean viewDetailsFlag = false;
	private boolean addSMSAPIFlag = false;
	private String password;
	private String ip;

	private List<String> ipList = new ArrayList<String>();
	
	private Account smsApiAccount = new Account();
	private String removedIP;
	
	private String secretKey;
	private boolean resetPasswordFlag = false; 
	
	/*------------------------- setters and getters -----------------*/

	public String getRemovedIP()
	{
		return removedIP;
	}

	public void setRemovedIP(String removedIP)
	{
		this.removedIP = removedIP;
	}

	public Account getSmsApiAccount()
	{
		return smsApiAccount;
	}
	
	public void setSmsApiAccount(Account smsApiAccount)
	{
		this.smsApiAccount = smsApiAccount;
	}
	
	public String getAccountID()
	{
		return accountID;
	}

	public List<Account> getAccounts()
	{
		return accounts;
	}

	public void setAccounts(List<Account> accounts)
	{
		this.accounts = accounts;
	}

	public boolean isAddSMSAPIFlag()
	{
		return addSMSAPIFlag;
	}

	public void setAddSMSAPIFlag(boolean addSMSAPIFlag)
	{
		this.addSMSAPIFlag = addSMSAPIFlag;
	}

	public void setAccountID(String accountID)
	{
		this.accountID = accountID;
	}

	public String getCompanyName()
	{
		return companyName;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

	public boolean isViewDetailsFlag()
	{
		return viewDetailsFlag;
	}

	public void setViewDetailsFlag(boolean viewDetailsFlag)
	{
		this.viewDetailsFlag = viewDetailsFlag;
	}

	public List<String> getIpList()
	{
		return ipList;
	}

	public void setIpList(List<String> ipList)
	{
		this.ipList = ipList;
	}

	public void setCompanyName(String companyName)
	{
		this.companyName = companyName;
	}

	public String getBillingMSISDN()
	{
		return billingMSISDN;
	}

	public void setBillingMSISDN(String billingMSISDN)
	{
		this.billingMSISDN = billingMSISDN;
	}

	/* ------------------------- Paging --------------------------- */

	long rowCount = 0L;

	long currentPage = 0;
	int pageSize = 10;

	public String FirstPage()
	{
		currentPage = 0;
		fillTable(0);

		return null;
	}

	public String nextPage()
	{
		if (currentPage + pageSize < rowCount)
		{
			currentPage += pageSize;
			if (currentPage < rowCount)
			{
				fillTable(currentPage);
			} else
			{
				LastPage();
			}
		}

		return null;
	}

	public String previousPage()
	{
		if (currentPage != 0)
		{
			currentPage -= pageSize;
			if (currentPage < pageSize)
			{
				currentPage = 0;
				FirstPage();
			} else
			{
				fillTable(currentPage);
			}
		}

		return null;
	}

	public String LastPage()
	{
		if (rowCount != 0)
		{
			long pagesMod = rowCount % pageSize;
			currentPage = rowCount - pagesMod;
			if (pagesMod > 0)
			{
				fillTable(rowCount - pagesMod);
			} else
			{
				fillTable(rowCount - pageSize);
			}
		}// end if

		return null;
	}

	public long getRowCount()
	{
		return rowCount;
	}

	public void setRowCount(long rowCount)
	{
		this.rowCount = rowCount;
	}

	public long getCurrentPage()
	{
		return currentPage;
	}

	public void setCurrentPage(long currentPage)
	{
		this.currentPage = currentPage;
	}

	public int getPageSize()
	{
		return pageSize;
	}

	public void setPageSize(int pageSize)
	{
		this.pageSize = pageSize;
	}

	/*------------------------- Action methods --------------------------------------*/

	public String filter()
	{
		currentPage = 0;
		filtered = true;
		fillTable(0);
		return null;
	}

	public String logTrxId(String trxId)
	{
		String sb = new String();
		sb = "Trx(" + trxId + "): ";
		return sb;
	}

	public String userLogInfo(String id, String userName)
	{
		String sb = new String();
		sb = "Admin(" + id + "," + userName + "): ";
		return sb;
	}

	@SuppressWarnings(
	{ "unchecked", "rawtypes" })
	private void fillTable(long currentPage)
	{
		accounts = new ArrayList<Account>();
		List list = null;
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Customer care representative searching for account with accountId="
					+ accountID + " or campanyName=" + companyName + " billingMsisdn=" + billingMSISDN
					+ " paginated from=" + currentPage + " with max=" + pageSize);
			list = adminAccountManagement.searchAccount(trxInfo.getAccountAdminTrxInfo(), accountID, companyName,
					billingMSISDN, Long.valueOf(currentPage).intValue(), pageSize);

			if (list != null && !list.isEmpty())
			{
				accounts = new ArrayList<Account>(list);
				adminLogger.info(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
								.getAdminName()) + "Accounts list fetched successfully size=" + list.size());
			} else
			{
				adminLogger.info(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
								.getAdminName()) + "Account List is empty");
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("noResultFound")),
								null));
			}
			setRowCount(adminAccountManagement.countSearchAccount(CommonUtil.manageTrxInfo(trxID).getAccountAdminTrxInfo(),
					accountID, companyName, billingMSISDN));
		} catch (Exception e)
		{
			adminLogger.error(
					logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while fetching list of users", e);
		}
	}

	public String enableSMS()
	{
		addSMSAPIFlag = true;
		viewDetailsFlag = false;
		ip = "";
		ipList.clear();
		password = "";
		
		return null;
	}

	public String disableSMS()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);

			smsManagementBeanLocal.deactivateSMSAPI(trxInfo, smsApiAccount.getAccountId());

			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Deactivating sms api service for account_id=" + smsApiAccount.getAccountId());

			viewDetailsFlag = false;
			ip = "";
			ipList.clear();
			fillTable(currentPage);

			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("sms_api_deactivation_succeed")),
							null));
		}// end try
		catch (Exception e)
		{
			addSMSAPIFlag = true;
			adminLogger.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Error while deactivating sms api service for account_id="
					+ smsApiAccount.getAccountId());
		}// end catch
		
		return null;
	}// end of method disableSMS

	public String viewDetails()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			adminLogger.info(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
					.getAdminName())
					+ "Display SMS Api details for account_id="
					+ smsApiAccount.getAccountId());
			
			if (!ipList.isEmpty())
			{
				ipList.clear();
			}// end if
			
			password = "";
			viewDetailsFlag = true;
			addSMSAPIFlag = false;
			
			for (Account account_ : accounts)
			{
				if (account_.getAccountId().equals(smsApiAccount.getAccountId()))
				{
					for(AccountIP ipAddress : account_.getAccountSmsApi().getAccountIPs())
					{
						ipList.add(ipAddress.getClientIp());
					}// end for
					
					break;
				}// end if
			}// end for
			
			secretKey = smsManagementBeanLocal.getCurrentSecureKey(trxInfo, smsApiAccount.getAccountId());
			
		}// end try 
		catch (Exception e)
		{
			adminLogger.error(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
					.getAdminName())
					+ "Error while view details for sms api service");
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle
							.getString("error_view_sms_api_details")), null));
		}// end catch
		
		return null;
	}// end of method viewDetails

	public String regenerateSecretKey()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);

			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Regenerate Secret Key for account_id=" + smsApiAccount.getAccountId());
			
			String newSecretKey = smsManagementBeanLocal.regenerateSecureKey(trxInfo, smsApiAccount.getAccountId());

			secretKey = newSecretKey;
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("sms_api_regenerate_secret_key_succeed")),
							null));
		}// end try
		catch (Exception e)
		{
			addSMSAPIFlag = true;
			adminLogger.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Error while Regenerating Secret Key for account_id="
					+ smsApiAccount.getAccountId());
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("sms_api_regenerate_secret_key_failed")),
							null));
		}// end catch
		
		return null;
	}// end of method regenerateSecretKey
	
	
	public String addNewIP()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);

			if (ip == null || ip.trim().isEmpty() || ip.trim().equals(""))
			{
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("required_ip")),
								null));
			}// end if 
			else
			{
				if(isUniqueIp(ip))
				{
					adminLogger.info(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName())
							+ "Add new ip=" + ip + " for account_id=" + smsApiAccount.getAccountId());
					ipList.add(ip);
					// reset ip field
					ip = "";
				}// end if
				else
				{
					adminLogger.error(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName())
							+ "ip=" + ip + " for account_id=" + smsApiAccount.getAccountId() + " is already exist");
					
					FacesContext.getCurrentInstance().addMessage(
							null,
							new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("duplicate_ip_address")),
									null));
				}// end else
			}// end else
		}// end try 
		catch (Exception e)
		{
			adminLogger.error(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
					.getAdminName())
					+ "Error while adding new ip=" + ip + " for account_id=" + smsApiAccount.getAccountId());
		}// end catch
		
		return null;
	}// end of method addNewIP

	private boolean isUniqueIp(String ipAddress)
	{
		boolean isUnique = true;
		
		for(String ip_ : ipList)
		{
			if(ip_.equals(ipAddress))
			{
				isUnique = false;
				break;
			}// end if
		}// end for
		
		return isUnique;
	}// end of method isUniqueIp
	
	public String removeIP()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);

			adminLogger.info(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), 
					trxInfo.getAdmin().getAdminName())
					+ "Removing ip=" + removedIP + " from ips list size=" + ipList.size());

			String toRemoveIP = null;
			
			for (String ip : ipList)
			{
				if (ip.equals(removedIP))
				{
					toRemoveIP = ip;
				}// end if
			}// end for
			
			if(toRemoveIP != null)
			{
				ipList.remove(toRemoveIP);
			}// end if
		}// end try 
		catch (Exception e)
		{
			adminLogger.error(userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
					.getAdminName())
					+ "Error while removing ip=" + removedIP + " from ips list size=" + ipList.size());
		}// end catch
		
		return null;
	}// end of method removeIP

	public String activateSmsAPIService()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			boolean isValidIPsList = isValidIPsList(ipList);
			if(isValidIPsList)
			{
				ActivateSMSAPIRequest request = new ActivateSMSAPIRequest();
				request.setAccountId(smsApiAccount.getAccountId());
				request.setPassword(password);
				
				request.setIPs(ipList);
				smsManagementBeanLocal.activateSMSAPI(trxInfo, request);

				addSMSAPIFlag = false;

				fillTable(currentPage);

				adminLogger.info(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
						+ "Activating sms api service for account_id=" + smsApiAccount.getAccountId());
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("sms_api_activation_succeed")),
								null));
			}// end if
			else
			{
				adminLogger
				.error(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
								.getAdminName()) + "Error while activating sms api service for account_id="
						+ smsApiAccount.getAccountId() + ", IPs list is empty");
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("empty_ip_list")),
								null));
			}// end else
		}// end try
		catch(AccountNotFoundException e)
		{
			addSMSAPIFlag = false;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while activating sms api service for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("account_not_found")),
							null));
		}// end catch
		catch(InvalidAccountSMSApiPasswordException e)
		{
			addSMSAPIFlag = true;
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while activating sms api service for account_id="
					+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							new MessageFormat(bundle.getString(e.getMessage())).format(new Object[]{e.getAllowedLength()}),
							null));
			
		}// end catch
		catch(InvalidIpsListException e)
		{
			addSMSAPIFlag = true;
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while activating sms api service for account_id="
					+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("ip_list_not_valid")),
							null));
		}// end catch
		catch (Exception e)
		{
			addSMSAPIFlag = true;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while activating sms api service for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			if(e.getCause() instanceof ConstraintViolationException)
			{
                ConstraintViolationException constraintException = (ConstraintViolationException) e.getCause();
			                
                for (Iterator<ConstraintViolation<?>> it = constraintException.getConstraintViolations().iterator(); it.hasNext();)
                {
                    ConstraintViolation<? extends Object> v = it.next();
                    
                    adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while activating sms api service for account_id="
							+ smsApiAccount.getAccountId() + "Constraint Violation Message" + ": " + v + "," + v.getMessage());
                }// end for
	         }// end if
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("sms_api_activation_error")),
							null));
		}// end catch
		
		return null;
	}// end of method activateSmsAPIService

	private boolean isValidIPsList(List<String> ipList)
	{
		boolean isValidList = true;
		
		if(ipList == null || ipList.isEmpty())
		{
			isValidList = false;
		}// end if
		
		return isValidList;
	}// end of method isValidIPsList
	
	public String cancelActivateSmsAPIService()
	{
		AdminTrxInfo trxInfo = null;
		
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Cancel sms api service activation for account_id=" + smsApiAccount.getAccountId());
			
			addSMSAPIFlag = false;
			ip = "";
			ipList.clear();
			
			fillTable(currentPage);
		}// end try
		catch(Exception e)
		{
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while cancelling sms api service activation  for account_id="
					+ smsApiAccount.getAccountId());
		}// end catch
		
		return null;
	}// end of method cancelActivateSmsAPIService
	
	public String cancelEditSmsAPIService()
	{
		AdminTrxInfo trxInfo = null;
		
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Cancel sms api service editing for account_id=" + smsApiAccount.getAccountId());
			
			viewDetailsFlag = false;
			ip = "";
			ipList.clear();
			
			fillTable(currentPage);
		}// end try
		catch(Exception e)
		{
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while cancelling sms api service editing for account_id="
					+ smsApiAccount.getAccountId());
		}// end catch
		
		return null;
	}// end of method cancelEditSmsAPIService
	
	public String resetPassword()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			String accountId = smsApiAccount.getAccountId();
			
			smsManagementBeanLocal.resetPassword(trxInfo, accountId, password);

			resetPasswordFlag = false;

			fillTable(currentPage);

			adminLogger.info(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
					+ "Reset password for account_id=" + smsApiAccount.getAccountId());
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("reset_account_password_succeed")),
							null));
		}// end try
		catch(AccountNotFoundException e)
		{
			resetPasswordFlag = false;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while resetting password for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("account_not_found")),
							null));
		}// end catch
		catch(InvalidAccountSMSApiPasswordException e)
		{
			resetPasswordFlag = true;
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while resetting password for account_id="
					+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR,
							new MessageFormat(bundle.getString(e.getMessage())).format(new Object[]{e.getAllowedLength()}),
							null));
			
		}// end catch
		catch (Exception e)
		{
			resetPasswordFlag = true;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while resetting password for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			if(e.getCause() instanceof ConstraintViolationException)
			{
                ConstraintViolationException constraintException = (ConstraintViolationException) e.getCause();
			                
                for (Iterator<ConstraintViolation<?>> it = constraintException.getConstraintViolations().iterator(); it.hasNext();)
                {
                    ConstraintViolation<? extends Object> v = it.next();
                    
                    adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while resetting password for account_id="
							+ smsApiAccount.getAccountId() + "Constraint Violation Message" + ": " + v + "," + v.getMessage());
                }// end for
	         }// end if
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("reset_account_password_error")),
							null));
		}// end catch
		
		return null;
	}// end of method resetPassword
	
	public String updateAccountIpList()
	{
		AdminTrxInfo trxInfo = null;
		try
		{
			String trxID = TrxId.getTrxId(CommonUtil.TRX_PREFIX);
			trxInfo = CommonUtil.manageTrxInfo(trxID);
			
			boolean isValidIPsList = isValidIPsList(ipList);
			if(isValidIPsList)
			{
				String accountId = smsApiAccount.getAccountId();
				
				smsManagementBeanLocal.EditIpsList(ipList, accountId, trxInfo);

				viewDetailsFlag = false;

				fillTable(currentPage);

				adminLogger.info(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin().getAdminName())
						+ "Updating IPs list for account_id=" + smsApiAccount.getAccountId());
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_INFO, String.format(bundle.getString("update_ip_list_succeed")),
								null));
			}// end if
			else
			{
				adminLogger
				.error(logTrxId(trxInfo.getTrxId())
						+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
								.getAdminName()) + "Error while updating IPs list for account_id="
						+ smsApiAccount.getAccountId() + ", IPs list is empty");
				
				FacesContext.getCurrentInstance().addMessage(
						null,
						new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("empty_ip_list")),
								null));
			}// end else
		}// end try
		catch(AccountNotFoundException e)
		{
			viewDetailsFlag = false;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while updating IPs list for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("account_not_found")),
							null));
		}// end catch
		catch(InvalidIpsListException e)
		{
			viewDetailsFlag = true;
			adminLogger
			.error(logTrxId(trxInfo.getTrxId())
					+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
							.getAdminName()) + "Error while updating IPs list for account_id="
					+ smsApiAccount.getAccountId(), e);
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("ip_list_not_valid")),
							null));
		}// end catch
		catch (Exception e)
		{
			viewDetailsFlag = true;
			adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while updating IPs list for account_id="
							+ smsApiAccount.getAccountId(), e);
			
			if(e.getCause() instanceof ConstraintViolationException)
			{
                ConstraintViolationException constraintException = (ConstraintViolationException) e.getCause();
			                
                for (Iterator<ConstraintViolation<?>> it = constraintException.getConstraintViolations().iterator(); it.hasNext();)
                {
                    ConstraintViolation<? extends Object> v = it.next();
                    
                    adminLogger
					.error(logTrxId(trxInfo.getTrxId())
							+ userLogInfo(String.valueOf(trxInfo.getAdmin().getAdminId()), trxInfo.getAdmin()
									.getAdminName()) + "Error while updating IPs list for account_id="
							+ smsApiAccount.getAccountId() + "Constraint Violation Message" + ": " + v + "," + v.getMessage());
                }// end for
	         }// end if
			
			FacesContext.getCurrentInstance().addMessage(
					null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, String.format(bundle.getString("update_ip_list_error")),
							null));
		}// end catch
		
		return null;
	}// end of method updateAccountIpList
	
	// Getters && Setters
	public String getIp()
	{
		return ip;
	}
	
	public void setIp(String ip)
	{
		this.ip = ip;
	}
	
	public String getSecretKey()
	{
		return secretKey;
	}
	
	public void setSecretKey(String secretKey)
	{
		this.secretKey = secretKey;
	}
	
	public boolean isResetPasswordFlag()
	{
		return resetPasswordFlag;
	}
	
	public void setResetPasswordFlag(boolean resetPasswordFlag)
	{
		this.resetPasswordFlag = resetPasswordFlag;
	}
}// end of class SMSAPIBean
