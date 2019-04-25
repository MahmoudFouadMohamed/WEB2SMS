package com.edafa.web2sms.filters;

import java.io.IOException;

import javax.faces.application.ResourceHandler;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager; import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.dalayer.model.Admin;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 * Servlet Filter implementation class LoginFilter
 */
@WebFilter("/*")
public class LoginFilter implements Filter {

	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI.name());

	/**
	 * Default constructor.
	 */
	public LoginFilter() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		// prevent going back to a secure page after log out
		if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) { // Skip
																											// JSF
																											// resources
																											// (CSS/JS/Images/etc)
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
																					// 1.1.
			res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			res.setDateHeader("Expires", 0); // Proxies.
		}

		Admin user = (Admin) req.getSession().getAttribute("user");
		String loginURL = req.getContextPath() + "/home.html";
		String firstURL = req.getContextPath() + "/Login/UserLogin.xhtml";
		if (user != null || req.getRequestURI().equals(loginURL) || req.getRequestURI().contains(firstURL)
				|| req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
			// User is logged in, so just continue request.
			chain.doFilter(request, response);
		} else {
			// User is not logged in, so redirect to index.

			res.sendRedirect(req.getContextPath() + "/home.html");
		}
		
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
