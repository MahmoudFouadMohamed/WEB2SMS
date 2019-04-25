package com.edafa.web2sms.ui.util;

import java.util.Date;
import java.util.Map;

import javax.ejb.EJB;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.service.admin.UIUserLogginMangementBean;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

public class CustomHttpSessionListener implements HttpSessionListener
{
	@EJB
	private UIUserLogginMangementBean userLogginMangementBean;

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	@Override
	public void sessionCreated(HttpSessionEvent sessionEvent)
	{

	}// end of method sessionCreated

	@Override
	public void sessionDestroyed(HttpSessionEvent sessionEvent)
	{
		Admin user = null;
		try
		{
			user = (Admin) sessionEvent.getSession().getAttribute("user");
			Map<String, Date> loggedInUsersMap = userLogginMangementBean.getMapUIUserLogging();
			loggedInUsersMap.remove(user.getUsername());

			logger.debug("Session expired for admin : " + user.getUsername() + " with role : "
					+ user.getRoleId().getRoleName());
		}// end try
		catch (Exception e)
		{
			if (user != null)
			{
				logger.debug("Exception while expiring session for admin: " + user.getUsername() + " with role : "
						+ user.getRoleId().getRoleName());
				logger.error(e.getMessage(), e);
			}// end if
		}// end catch
	}// end of method sessionDestroyed

}// end of class HttpSessionListener
