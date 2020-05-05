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
package com.moveatis.managedbeans;

import java.io.IOException;
import java.util.Map;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.component.menuitem.UIMenuItem;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.application.RedirectURLs;
import com.moveatis.groupkey.GroupKeyEntity;
import com.moveatis.interfaces.GroupKey;
import com.moveatis.interfaces.Session;
import com.moveatis.interfaces.TagUser;
import com.moveatis.user.TagUserEntity;

/**
 * The bean that manages the login for three types of users: the public user,
 * the tag user and the identified user.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@ManagedBean(name = "loginBean")
@RequestScoped
public class LoginManagedBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginManagedBean.class);

	@ManagedProperty("#{msg}")
	private ResourceBundle messages;

	@Inject
	private Session sessionBean;

	@Inject
	private GroupKey groupKeyEJB;

	@Inject
	private TagUser tagUserEJB;

	private String loginOutcome;
	private String tag;

	/**
	 * Creates a new instance of LoginManagedBean.
	 */
	public LoginManagedBean() {

	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Sets the session for a particular group key.
	 * 
	 * @return The navigation rule string that redirects to the category selection
	 *         view.
	 */
	public String doTagLogin() {
		GroupKeyEntity groupKeyEntity = groupKeyEJB.findByKey(tag);

		if (groupKeyEntity == null) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("dialogErrorTitle"), messages.getString("tagNotFound")));
		}

		TagUserEntity tagUserEntity = tagUserEJB.findByKey(groupKeyEntity);

		if (tagUserEntity == null) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("dialogErrorTitle"), messages.getString("tagNotFound")));
		} else {
			sessionBean.setTagUser(tagUserEntity);
			this.loginOutcome = "tagUser";
			return "taguser";
		}

		return "";
	}

	/**
	 * Sets the session for a public user.
	 * 
	 * @return The navigation rule string that redirects to the category selection
	 *         view.
	 */
	public String doAnonymityLogin() {
		sessionBean.setAnonymityUser();
		return "anonymityuser";
	}

	/**
	 * Allows users to login from different views. On May 2016, it's not working as
	 * supposed, since Shibboleth nulls the session on redirect.
	 * 
	 * @param actionEvent
	 *            The action event that activated the login button.
	 */
	public void doIdentityLogin(ActionEvent actionEvent) {

		String secureRedirectUri, defaultUri;

		if (((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest()).getRequestURL()
				.toString().contains("localhost")) {
			secureRedirectUri = RedirectURLs.LOCALHOST_REDIRECT_SECURE_URI;
			defaultUri = RedirectURLs.LOCALHOST_HOME_URI;
		} else {
			secureRedirectUri = RedirectURLs.SHIBBOLETH_REDIRECT_SECURE_URI;
			defaultUri = RedirectURLs.HOME_URI;
		}
		try {
			Object source = actionEvent.getSource();
			if (source instanceof UIMenuItem) {
				// Clicked in menu so we need to redirect to page where user clicked login
				Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext()
						.getRequestParameterMap();
				sessionBean.setReturnUri(defaultUri + params.get("view"));
			}

			FacesContext.getCurrentInstance().getExternalContext().redirect(secureRedirectUri);
		} catch (IOException ex) {
			LOGGER.error("An error happened in identitylogin" + ex.toString());
		}
	}

	public ResourceBundle getMessages() {
		return messages;
	}

	public void setMessages(ResourceBundle messages) {
		this.messages = messages;
	}

	public String getLoginOutcome() {
		return loginOutcome;
	}

	public void setLoginOutcome(String loginOutcome) {
		this.loginOutcome = loginOutcome;
	}
}