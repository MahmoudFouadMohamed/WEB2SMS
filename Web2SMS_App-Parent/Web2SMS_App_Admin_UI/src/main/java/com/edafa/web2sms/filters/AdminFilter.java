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

import com.edafa.web2sms.dalayer.model.Admin;

@WebFilter("Admin/*")
public class AdminFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
			ServletException {
		// TODO Auto-generated method stubuser.getRoleId().getRoleId().equals(1)
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		// LoginBean auth = (LoginBean) req.getSession().getAttribute("login");

		// skip the resources library
		if (!req.getRequestURI().startsWith(req.getContextPath() + ResourceHandler.RESOURCE_IDENTIFIER)) {
			res.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP
																					// 1.1.
			res.setHeader("Pragma", "no-cache"); // HTTP 1.0.
			res.setDateHeader("Expires", 0); // Proxies.
		}
		Admin user = (Admin) req.getSession().getAttribute("user");
		if (user != null) {
			if (user.getRoleId().getRoleId() == 1
					|| req.getRequestURI().contains(req.getContextPath() + "/Login/loginPage.xhtml")) {
				// User is logged in, so just continue request.
				chain.doFilter(request, response);
			} else {
				// User is not logged in, so redirect to index.
				res.sendRedirect(req.getContextPath() + "/home.html");
			}
		} else {
			// user in session = null , so redirect to index.
			res.sendRedirect(req.getContextPath() + "/home.html");
		}

	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

}
