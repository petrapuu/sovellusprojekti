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

/**
 * The class has the URIs for redirection within the Jyväskylä University login
 * system.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
public class RedirectURLs {

	public static final String HOME_URI = "https://moveatis.sport.jyu.fi";
	public static final String SHIBBOLETH_REDIRECT_SECURE_URI = "https://moveatis.sport.jyu.fi/moveatis/secure";
	public static final String LOCALHOST_REDIRECT_SECURE_URI = "http://localhost:8080/jyutesting/";
	public static final String LOCALHOST_HOME_URI = "http://localhost:8080/moveatis";
	public static final String SHIBBOLET_LOGOUT_URL = "https://moveatis.sport.jyu.fi/Shibboleth.sso/Logout?return="
			+ "https://login.jyu.fi/sso/logout.php?return=" + "https%3A%2F%2Fmoveatis.sport.jyu.fi";

	public static final String CONTROL_PAGE_URI = "https://moveatis.sport.jyu.fi/app/control/";
	public static final String ERROR_PAGE_URI = "https://moveatis.sport.jyu.fi/error";
	public static final String SMTP_HOST = "localhost";

	public RedirectURLs() {

	}
}
