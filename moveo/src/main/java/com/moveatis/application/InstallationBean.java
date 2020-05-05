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

package com.moveatis.application;

import java.io.Serializable;
import java.util.Calendar;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.enums.ApplicationStatusCode;
import com.moveatis.interfaces.AnonUser;
import com.moveatis.interfaces.Application;
import com.moveatis.interfaces.Role;
import com.moveatis.interfaces.Session;
import com.moveatis.user.AnonUserEntity;

/**
 * The class does the installation task of the Moveatis application.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Stateless
public class InstallationBean implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(InstallationBean.class);
	private static final String REPORT_EMAIL = "sami.m.j.kallio@student.jyu.fi";

	private ApplicationEntity applicationEntity;

	@Inject
	private Application applicationEJB;

	@Inject
	private Role roleEJB;

	@Inject
	private AnonUser anonUserEJB;

	@Inject
	private Session sessionEJB;

	public InstallationBean() {

	}

	/**
	 * The method creates the application, which includes setting the installation
	 * date, adding the user to superusers, and adding the reporting email address,
	 * where possible error reports are sent.
	 * 
	 * @return enum, which result of the installation.
	 */
	public ApplicationStatusCode createApplication() {

		if (!applicationEJB.checkInstalled()) {

			applicationEntity = new ApplicationEntity();
			applicationEntity.setApplicationInstalled(Calendar.getInstance().getTime());
			applicationEntity.setSuperUsers(roleEJB.listSuperusers());
			applicationEntity.setReportEmail(REPORT_EMAIL);
			applicationEJB.create(applicationEntity);

			AnonUserEntity anonEntity = new AnonUserEntity();
			anonEntity.setCreator(sessionEJB.getLoggedIdentifiedUser());
			anonUserEJB.create(anonEntity);

			return ApplicationStatusCode.INSTALLATION_OK;
		}

		return ApplicationStatusCode.ALREADY_INSTALLED;
	}
}
