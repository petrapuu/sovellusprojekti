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

import java.io.Serializable;
import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The bean manages user-related information in a session.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Named(value = "userManagedBean")
@SessionScoped
public class UserManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LoggerFactory.getLogger(UserManagedBean.class);

	private Locale locale;
	private TimeZone timeZone;
	private String languageString;
	private String optionLanguageString;

	/**
	 * Creates a new instance of UserManagedBean.
	 */
	public UserManagedBean() {

	}

	/**
	 * Gets the locale to use in the user interface of the application.
	 */
	public Locale getLocale() {

		if (this.locale == null) {
			FacesContext context = FacesContext.getCurrentInstance();
			this.locale = context.getViewRoot().getLocale();
			this.languageString = this.locale.getLanguage();
		}
		return this.locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
		FacesContext context = FacesContext.getCurrentInstance();
		context.getViewRoot().setLocale(this.locale);

		this.languageString = this.locale.getLanguage();
	}

	public void setLocale(String language) {

		Locale newLocale = null;

		if (language.equalsIgnoreCase("fi")) {
			newLocale = new Locale("fi", "FI");
		} else if (language.equalsIgnoreCase("en")) {
			newLocale = new Locale("en");
		}

		if (newLocale != null) {
			this.setLocale(newLocale);
		}
	}

	public void changeLocale(ActionEvent event) {
		Locale finnishLocale = new Locale("fi", "FI");
		Locale defaultLocale = new Locale("en");

		if (this.locale.getLanguage().equals(finnishLocale.getLanguage())) {
			this.setLocale(defaultLocale);
			this.setLanguageString(defaultLocale.getLanguage());
			this.setOptionLanguageString(finnishLocale.getLanguage());
		} else {
			this.setLocale(finnishLocale);
			this.setLanguageString(finnishLocale.getLanguage());
			this.setOptionLanguageString(defaultLocale.getLanguage());
		}

	}

	public String getLanguageString() {
		return languageString;
	}

	public void setLanguageString(String languageString) {
		this.languageString = languageString;
	}

	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public String getOptionLanguageString() {
		Locale finnishLocale = new Locale("fi", "FI");

		if (this.languageString == null) {
			this.getLocale();
		}

		if (this.languageString.equals(finnishLocale.getLanguage())) {
			this.optionLanguageString = "English";
		} else {
			this.optionLanguageString = "Suomi";
		}

		return optionLanguageString;
	}

	public void setOptionLanguageString(String optionLanguageString) {
		this.optionLanguageString = optionLanguageString;
	}
}
