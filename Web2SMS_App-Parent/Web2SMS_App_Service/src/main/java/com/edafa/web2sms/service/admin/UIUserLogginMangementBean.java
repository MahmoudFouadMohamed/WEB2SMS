package com.edafa.web2sms.service.admin;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;




import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Session Bean implementation class UIUserLogginMangementBean
 */
@Singleton
@LocalBean
@Startup
public class UIUserLogginMangementBean {
    private static Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());
	
	private Map<String, Date> mapUIUserLogging = new HashMap<String, Date>();
//	@EJB
//	private PropertyDaoLocal propertyDaoLocal;
//	private Property propertyActive;
	
	int activeWindow= 30;// 30 min 
	
    /**
     * Default constructor. 
     */
    public UIUserLogginMangementBean() {
    }
    
	public boolean find(String userName) throws Exception {
		String preLog = "userName[" + userName + "]";

		try {
			
			
			logger.info(preLog + "Working on user name[" + userName +"]");
//			propertyPassword = propertyDaoLocal.getProperty(ModuleNameType.WEB, "passwordExpiryTime");

//			propertyActive = propertyDaoLocal.getProperty(ModuleNameType.WEB, "lastActiveTime");

			activeWindow = (Integer) Configs.LAST_LOGIN_ACTIVE_WINDOW.getValue();
//			propertyLockingInterval = propertyDaoLocal.getProperty(ModuleNameType.WEB, "lockingInterval");

//			propertymMxNumberOfLoginFailure = propertyDaoLocal.getProperty(ModuleNameType.WEB, "maxNumberOfLoginFailure");

			/*if (propertyPassword == null) {
				throw new EPGException(new Exception("Cannot find passwordExpiryTime Property"));
			}*/

			/*if (propertyLockingInterval == null) {
				throw new EPGException(new Exception("Cannot find lastActiveTime Property"));
			}*/

			/*if (propertymMxNumberOfLoginFailure == null) {
				throw new EPGException(new Exception("Cannot find maxNumberOfLoginFailure Property"));
			}*/

			Calendar lastActiveDateCalender = Calendar.getInstance();
			logger.info(preLog + "Getting last active date");
			Date loginDate = mapUIUserLogging.get(userName);
			logger.info(preLog + "last active date[" + loginDate + "]");
			
			Calendar rightNowCalendar = Calendar.getInstance();
			
			if (loginDate != null) {
				
				logger.info(preLog + "User does exist ... Checking the last active date.");
				lastActiveDateCalender.setTime(loginDate);
				lastActiveDateCalender.add(Calendar.MINUTE,activeWindow);
				logger.info(preLog + "The time that should consider user is not active is [" + lastActiveDateCalender.getTime() + "].");
				
			}else{

				logger.info(preLog + "User does [NOT] exist ... This user can access the system.");
				//remove the user from Map
				mapUIUserLogging.put(userName, new Date());
				return false;
				
			}

			if(rightNowCalendar.after(lastActiveDateCalender)){
				logger.info(preLog + "This user is [NOT] active any more .. Will allow to access the system.");
				//remove the user from Map
				mapUIUserLogging.put(userName, new Date());
				//return null
				return false;
			}else{
				logger.info(preLog + "This user is active .. Will [NOT] allow to access the system.");
				return true;
			}

		} catch (Exception exception) {
			logger.error(preLog + "exception ", exception);

//			exception.printStackTrace();
			throw new Exception(exception);
		}
	}

	public void updateLogIn(String userId) throws Exception {
		String preLog = "UserName[" + userId + "]";
		logger.info(preLog + "Remove user from loggin");
		Date lastActiveDate = mapUIUserLogging.remove(userId);
		if(lastActiveDate == null){
			
			logger.info(preLog + "Usre does not exist.");
			
		}else{
			
			logger.info(preLog + "Last Active Date[" + lastActiveDate + "]");
			
		}
	}

	public void updateLastActiveDate(String userId) throws Exception {
		String preLog = "UserName[" + userId + "]";
		logger.info(preLog + "Update Last Active Date.");
		Date lastActiveDate = mapUIUserLogging.get(userId);

		if(lastActiveDate == null){
			
			logger.info(preLog + "User does not exist.");
			
		}else{
			
			logger.info(preLog + "Old Last Active Date[" + lastActiveDate + "]");
			mapUIUserLogging.put(userId, new Date());
			lastActiveDate = mapUIUserLogging.get(userId);
			logger.info(preLog + "New Last Active Date[" + lastActiveDate + "]");
			
		}
	}

/*	@Override
	public void updateLockCount(String userName, Date lastLockDate, int maxLockCount) throws EPGException {
		try {
			getEntityManager().createNamedQuery(UiSystemUser.UPDATE_LOCK_COUNT_AND_LAST_LOCK_DATE_BY_USERNAME).setParameter(UiSystemUser.USERNAME, userName)
					.setParameter(UiSystemUser.LAST_LOCK_DATE, lastLockDate).setParameter(UiSystemUser.LOCKE_COUNT, maxLockCount).executeUpdate();
		} catch (Exception exception) {
			throw new EPGException(exception);
		}
	}
*/
	
	public Map<String, Date> getMapUIUserLogging()
	{
		return mapUIUserLogging;
	}
}
