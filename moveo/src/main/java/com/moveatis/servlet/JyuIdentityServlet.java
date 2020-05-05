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
package com.moveatis.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.application.InstallationBean;
import com.moveatis.application.RedirectURLs;
import com.moveatis.enums.ApplicationStatusCode;
import com.moveatis.identityprovider.IdentityProvider;
import com.moveatis.identityprovider.IdentityProviderBean;
import com.moveatis.identityprovider.IdentityProviderInformationEntity;
import com.moveatis.identityprovider.IdentityProviderRegistrationBean;
import com.moveatis.interfaces.Application;
import com.moveatis.interfaces.Role;
import com.moveatis.interfaces.Session;
import com.moveatis.interfaces.User;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The servlet handles the identification of a user using the Shibboleth service
 * of Jyväskylä University.
 * 
 * If you are modifying Moveatis to your own organization, you need to implement
 * your own identity provider service with the classes in the identity provider
 * package.
 * 
 * @see IdentityProviderInformationEntity
 * @see IdentityProvider
 * @see IdentityProviderBean
 * @see IdentityProviderRegistrationBean
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@WebServlet(name = "JyuIdentityServlet", urlPatterns = { "/moveatis/secure" })
public class JyuIdentityServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger(JyuIdentityServlet.class);

	private IdentifiedUserEntity userEntity;

	@Inject
	private Session sessionBean;
	@Inject
	private IdentityProviderBean ipBean;
	@Inject
	private User userEJB;
	@Inject
	private Role roleEJB;
	@Inject
	private Application applicationEJB;
	@Inject
	private InstallationBean installationEJB;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @throws ServletException
	 *             if a servlet-specific error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String userName = (String) request.getAttribute("eppn");
	    userName=new String(userName.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
	    
		String affiliation = (String) request.getAttribute("unscoped-affiliation");
	    affiliation=new String(affiliation.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);
	    
	    String displayName = (String) request.getAttribute("displayName");		
	    displayName=new String(displayName.getBytes(StandardCharsets.ISO_8859_1),StandardCharsets.UTF_8);

		if (userName != null && affiliation != null && displayName != null) {
			IdentityProviderInformationEntity ipInformationEntity = ipBean.findIpEntityByUsername(userName);

			if (ipInformationEntity != null) {
				userEntity = ipInformationEntity.getIdentifiedUserEntity();
				sessionBean.setIdentityProviderUser(userEntity);
				response.sendRedirect(RedirectURLs.CONTROL_PAGE_URI);

			} else {
				/*
				 * IdentityProviderInformationEntity was not found, but as our service is open
				 * to all students and affiliates of Jyväskylä University, we shall create a new
				 * entity for this user
				 */
				userEntity = new IdentifiedUserEntity();

				IdentityProviderInformationEntity identityProviderInformationEntity = new IdentityProviderInformationEntity();
				identityProviderInformationEntity.setUsername(userName);
				identityProviderInformationEntity.setAffiliation(affiliation);

				userEntity.setIdentityProviderInformationEntity(identityProviderInformationEntity);
				userEntity.setGivenName(displayName);

				identityProviderInformationEntity.setUserEntity(userEntity);

				userEJB.create(userEntity);
				sessionBean.setIdentityProviderUser(userEntity);

				if (!applicationEJB.checkInstalled()) {
					// Application itself has not been installed yet, so that
					// needs to be done
					// First user is the admin user
					roleEJB.addSuperuserRoleToUser(userEntity);

					if (installationEJB.createApplication() == ApplicationStatusCode.INSTALLATION_OK) {
						response.sendRedirect(RedirectURLs.CONTROL_PAGE_URI);
					} else {
						response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
					}
				} else {
					response.sendRedirect(RedirectURLs.CONTROL_PAGE_URI);
				}
			}
		} else {
			response.sendRedirect(RedirectURLs.HOME_URI);
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @throws ServletException
	 *             if a servlet-specific error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		processRequest(request, response);
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description.
	 */
	@Override
	public String getServletInfo() {
		return "This servlet is the endpoint to Shibboleth-identityprovider service";
	}

}
