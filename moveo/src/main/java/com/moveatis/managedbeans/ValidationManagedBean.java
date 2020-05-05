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

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import com.moveatis.helpers.Validation;
import com.moveatis.interfaces.GroupKey;
import com.moveatis.interfaces.MessageBundle;

/**
 * The bean implements commonly used methods to validate user input.
 * 
 * @author Ilari Paananen
 */
@Named(value = "validationBean")
@RequestScoped
public class ValidationManagedBean {

	@Inject
	@MessageBundle
	private transient ResourceBundle messages;

	@Inject
	private GroupKey groupKeyEJB;

	private void throwError(String message) {
		throw new ValidatorException(
				new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("dialogErrorTitle"), message));
	}

	public void validateStringForJsAndHtml(FacesContext context, UIComponent component, Object value) {
		String s = (String) value;
		String valid = Validation.validateForJsAndHtml(s);
		if (!s.equals(valid)) {
			// String error =
			// MessageFormat.format(messages.getString("validate_invalidChars"),
			// invalidChars);
			String error = messages.getString("validate_invalidChars");
			throwError(error);
		}
	}

	public void validateGroupKey(FacesContext context, UIComponent component, Object value) {
		validateStringForJsAndHtml(context, component, value);
		validateStringMinLength((String) value, 4);
		validateStringMaxLength((String) value, 64);
		if (groupKeyEJB.findByKey((String) value) != null) {
			String error = messages.getString("validate_groupKeyReserved");
			throwError(error);
		}
	}

	public void validateShortString(FacesContext context, UIComponent component, Object value) {
		validateStringForJsAndHtml(context, component, value);
		validateStringMaxLength((String) value, 64);
	}

	public void validateLongString(FacesContext context, UIComponent component, Object value) {
		validateStringForJsAndHtml(context, component, value);
		validateStringMaxLength((String) value, 256);
	}

	private void validateStringMaxLength(String str, int maxLength) {
		if (str.length() > maxLength) {
			String error = MessageFormat.format(messages.getString("validate_maxLength"), maxLength);
			throwError(error);
		}
	}

	private void validateStringMinLength(String str, int minLength) {
		if (str.length() < minLength) {
			String error = MessageFormat.format(messages.getString("validate_minLength"), minLength);
			throwError(error);
		}
	}

}
