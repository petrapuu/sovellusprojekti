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

package com.moveatis.observation;

import java.io.Serializable;

import com.moveatis.category.CategoryType;
import com.moveatis.helpers.Validation;

/**
 * The observation has its own categories, so renaming the original category
 * does not affect on old observations.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Ilari Paananen <ilari.k.paananen at student.jyu.fi>
 */
public class ObservationCategory implements Serializable {

	private static final long serialVersionUID = 1L;

	private CategoryType type;
	private Long tag;
	private String name;

	public ObservationCategory() {
		this.type = CategoryType.COUNTED;
		this.name = "";
	}

	public ObservationCategory(ObservationCategory other) {
		this.type = other.type;
		this.name = other.name;
		this.tag = other.tag;
	}

	public CategoryType getType() {
		return type;
	}

	public void setType(CategoryType type) {
		this.type = type;
	}

	public int getTypeAsInt() {
		return type.ordinal();
	}

	/**
	 * Returns true if the type of the category is COUNTED. The method is used with
	 * the PrimeFaces boolean button.
	 */
	public boolean getTypeAsBoolean() {
		return (type == CategoryType.COUNTED);
	}

	/**
	 * Sets the type of the category based on the given boolean value. If the value
	 * is true, the type will be COUNTED. Otherwise the type will be TIMED. The
	 * method is used with the PrimeFaces boolean button.
	 */
	public void setTypeAsBoolean(boolean value) {
		if (value) {
			type = CategoryType.COUNTED;
		} else {
			type = CategoryType.TIMED;
		}
	}

	public Long getTag() {
		return tag;
	}

	public void setTag(Long tag) {
		this.tag = tag;
	}

	public String getName() {
		return name;
	}

	public final void setName(String name) {
		String validName = Validation.validateForJsAndHtml(name).trim();
		if (!this.name.equals(validName)) {
			this.name = validName;
		}
	}
}
