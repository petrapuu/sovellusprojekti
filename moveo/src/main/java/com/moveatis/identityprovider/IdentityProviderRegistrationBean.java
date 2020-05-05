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

package com.moveatis.identityprovider;

import java.io.IOException;
import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.application.RedirectURLs;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * An example managed bean for customizing an identity service.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Named(value = "identityProviderRegistrationBean")
@RequestScoped
public class IdentityProviderRegistrationBean implements IdentityProvider, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(IdentityProviderRegistrationBean.class);
	private IdentifiedUserEntity userEntity;
	private IdentityProviderInformationEntity identityProviderInformationEntity;

	public IdentityProviderRegistrationBean() {

	}

	public void registerSuperUser(ActionEvent actionEvent) {

		try {
			FacesContext.getCurrentInstance().getExternalContext()
					.redirect(RedirectURLs.SHIBBOLETH_REDIRECT_SECURE_URI);
		} catch (IOException ex) {
			LOGGER.debug("Error in registration", ex);
		}
	}

	@Override
	public IdentifiedUserEntity getIdentifiedUserEntity() {
		userEntity = new IdentifiedUserEntity();
		identityProviderInformationEntity = new IdentityProviderInformationEntity();

		userEntity.setIdentityProviderInformationEntity(identityProviderInformationEntity);
		identityProviderInformationEntity.setUserEntity(userEntity);

		return userEntity;
	}

}
