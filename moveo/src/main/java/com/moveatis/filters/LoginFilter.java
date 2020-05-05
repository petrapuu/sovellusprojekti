/* 
 * Copyright (c) 2016, Jarmo Juujärvi, Sami Kallio, Kai Korhonen, Juha Moisio, Ilari Paananen 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its 
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.moveatis.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.application.RedirectURLs;
import com.moveatis.interfaces.Session;

/**
 *
 * The filter checks that the pages in /app folder are accessed only through the
 * frontpage.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * 
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = { "/app/*" })
public class LoginFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginFilter.class);

	private static final boolean DEBUG = false;
	private FilterConfig filterConfig = null;
	private ServletContext context = null;

	@Inject
	private Session sessionBean;

	public LoginFilter() {
	}

	private void doBeforeProcessing(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {

	}

	private void doAfterProcessing(ServletRequest request, ServletResponse response)
			throws IOException, ServletException {

	}

	/**
	 *
	 * @param request
	 *            The servlet request to be processed.
	 * @param response
	 *            The servlet response to be created.
	 * @param chain
	 *            The filter chain to be processed.
	 *
	 * @exception IOException
	 *                if an input or output error occurs.
	 * @exception ServletException
	 *                if a servlet error occurs.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		doBeforeProcessing(request, response);

		if (!sessionBean.isLoggedIn()) {
			((HttpServletResponse) response).sendRedirect("/" + context.getContextPath() + "/index.xhtml");
			return;
		}

		Throwable problem = null;
		try {
			chain.doFilter(request, response);
		} catch (IOException | ServletException t) {
			problem = t;
		}

		doAfterProcessing(request, response);

		// If there was a problem, we want to rethrow it if it is
		// a known type, otherwise log it.
		if (problem != null) {
			if (problem instanceof ServletException) {
				throw (ServletException) problem;
			}
			if (problem instanceof IOException) {
				throw (IOException) problem;
			}
			sendProcessingError(problem, response);
		}
	}

	/**
	 * Returns the filter configuration object for the filter.
	 */
	public FilterConfig getFilterConfig() {
		return (this.filterConfig);
	}

	/**
	 * Sets the filter configuration object for the filter.
	 *
	 * @param filterConfig
	 *            The filter configuration object.
	 */
	public void setFilterConfig(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
	}

	/**
	 * Destroys the filter.
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Initializes the filter.
	 */
	@Override
	public void init(FilterConfig filterConfig) {
		this.filterConfig = filterConfig;
		if (filterConfig != null) {
			if (DEBUG) {
				log("LoginFilter:Initializing filter");
				this.context = filterConfig.getServletContext();
			}
		}
	}

	/**
	 * Returns a string representation of the object.
	 */
	@Override
	public String toString() {
		if (filterConfig == null) {
			return ("LoginFilter()");
		}
		StringBuilder sb = new StringBuilder("LoginFilter(");
		sb.append(filterConfig);
		sb.append(")");
		return (sb.toString());
	}

	private void sendProcessingError(Throwable t, ServletResponse response) {
		LOGGER.error("Error in login filtering", t);

		try {
			((HttpServletResponse) response).sendRedirect(RedirectURLs.ERROR_PAGE_URI);
		} catch (IOException ex) {
			LOGGER.error("Error in redirecting", ex);
		}
	}

	public void log(String msg) {
		filterConfig.getServletContext().log(msg);
	}
}