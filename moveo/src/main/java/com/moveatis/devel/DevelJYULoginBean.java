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

package com.moveatis.devel;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.application.InstallationBean;
import com.moveatis.enums.ApplicationStatusCode;
import com.moveatis.identityprovider.IdentityProviderBean;
import com.moveatis.identityprovider.IdentityProviderInformationEntity;
import com.moveatis.interfaces.Application;
import com.moveatis.interfaces.Role;
import com.moveatis.interfaces.Session;
import com.moveatis.interfaces.User;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The bean is a dummy login bean for development purposes, as it mocks the
 * Sibboleth identity provider system of Jyväskylä University.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Named(value = "develJYULoginBean")
@RequestScoped
public class DevelJYULoginBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(DevelJYULoginBean.class);

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
	private InstallationBean installationBean;
	@Inject
	private Application applicationEJB;

	private String username;
	private String givenName;
	private String affiliation;
	private final Boolean isLocalhost;

	/** Creates a new instance of DevelJYULoginBean. */
	public DevelJYULoginBean() {

		isLocalhost = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getRequestURL().toString().contains("localhost");
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getAffiliation() {
		return affiliation;
	}

	public void setAffiliation(String affiliation) {
		this.affiliation = affiliation;
	}

	public Boolean getIsLocalhost() {
		return isLocalhost;
	}

	public String doLogin() {
		IdentityProviderInformationEntity ipInformationEntity = ipBean.findIpEntityByUsername(username);

		if (ipInformationEntity != null) {
			userEntity = ipInformationEntity.getIdentifiedUserEntity();
			sessionBean.setIdentityProviderUser(userEntity);
		} else {
			return "fail?faces-redirect=true";
		}
		if (sessionBean.getReturnUri() != null) {
			try {
				FacesContext.getCurrentInstance().getExternalContext().redirect(sessionBean.getReturnUri());
			} catch (IOException ex) {
				LOGGER.debug("Error in doLogin", ex);
			}
		} else {
			return "/app/control/index?faces-redirect=true";
		}

		return "fail?faces-redirect=true";

	}

	public String doRegistration() {
		userEntity = new IdentifiedUserEntity();

		IdentityProviderInformationEntity identityProviderInformationEntity = new IdentityProviderInformationEntity();
		identityProviderInformationEntity.setUsername(username);
		identityProviderInformationEntity.setAffiliation(affiliation);

		userEntity.setIdentityProviderInformationEntity(identityProviderInformationEntity);
		userEntity.setGivenName(givenName);

		identityProviderInformationEntity.setUserEntity(userEntity);

		userEJB.create(userEntity);

		sessionBean.setIdentityProviderUser(userEntity);

		if (applicationEJB.checkInstalled()) {
			return "/app/control/index?faces-redirect=true";
		} else {
			return "fail?faces-redirect=true";
		}
	}

	public String doSuperUserRegistration() {
		userEntity = new IdentifiedUserEntity();

		IdentityProviderInformationEntity identityProviderInformationEntity = new IdentityProviderInformationEntity();
		identityProviderInformationEntity.setUsername(username);
		identityProviderInformationEntity.setAffiliation(affiliation);

		userEntity.setIdentityProviderInformationEntity(identityProviderInformationEntity);
		userEntity.setGivenName(givenName);

		identityProviderInformationEntity.setUserEntity(userEntity);

		userEJB.create(userEntity);
		roleEJB.addSuperuserRoleToUser(userEntity);

		sessionBean.setIdentityProviderUser(userEntity);

		if (installationBean.createApplication() == ApplicationStatusCode.INSTALLATION_OK) {
			return "/app/control/index?faces-redirect=true";
		} else {
			return "fail?faces-redirect=true";
		}
	}
}
