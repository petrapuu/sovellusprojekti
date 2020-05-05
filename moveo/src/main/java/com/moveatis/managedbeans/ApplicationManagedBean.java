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

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;

import com.moveatis.interfaces.Application;
import com.moveatis.interfaces.Session;

/**
 * The bean that serves the application installation view.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@ManagedBean(name = "applicationManagedBean")
@RequestScoped
public class ApplicationManagedBean {

	@Inject
	private Application applicationEJB;

	@Inject
	private Session sessionBean;

	public ApplicationManagedBean() {

	}

	/**
	 * Returns true if the application has been installed, otherwise false.
	 */
	public Boolean getInstalled() {
		return applicationEJB.checkInstalled();
	}

	/**
	 * Redirects the user to the installation URI if the application has not been
	 * installed yet. If the application is running on localhost server it redirects
	 * to the localhost version of the installation URI.
	 */
	public String doInstall() {
		if (applicationEJB.checkInstalled()) {
			return "index?faces-redirect=true";
		} else {
			if (sessionBean.getIsLocalhost()) {
				return "jyutesting/index.xhtml?faces-redirect=true";
			} else {
				return "install?faces-redirect=true";
			}
		}
	}
}
