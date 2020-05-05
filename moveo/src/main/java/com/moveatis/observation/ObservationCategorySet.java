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
import java.util.ArrayList;
import java.util.List;

import com.moveatis.category.CategoryType;

/**
 * The observation has its own category sets, so renaming or removing original
 * category sets does not alter old observations.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Ilari Paananen <ilari.k.paananen at student.jyu.fi>
 */
public class ObservationCategorySet implements Serializable {

	private static final long serialVersionUID = 1L;

	private final Long id;
	private final String name;
	private final List<ObservationCategory> categories;

	/**
	 * Creates a new instance of ObservationCategorySet with the given id and name.
	 */
	public ObservationCategorySet(Long id, String name) {
		this.id = id;
		this.name = name;
		this.categories = new ArrayList<>();
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns the list of the categories in the category set.
	 */
	public List<ObservationCategory> getCategories() {
		return categories;
	}

	/**
	 * Adds a new category to the list of the categories.
	 * 
	 * @param type
	 *            The type of the new category.
	 * @param tag
	 *            The tag of the new category.
	 * @param name
	 *            The name of the new category.
	 */
	public void add(CategoryType type, Long tag, String name) {
		ObservationCategory category = new ObservationCategory();
		category.setType(type);
		category.setName(name);
		category.setTag(tag);
		categories.add(category);
	}

	public void add(Long tag, String name) {
		ObservationCategory category = new ObservationCategory();
		category.setName(name);
		category.setTag(tag);
		category.setType(CategoryType.COUNTED);
		categories.add(category);
	}

	/**
	 * Adds the given category to the list of the categories.
	 * 
	 * @param category
	 *            The category to be added.
	 */
	public void add(ObservationCategory category) {
		categories.add(category);
	}

	/**
	 * Adds an empty category to the list of the categories.
	 */
	public void addEmpty() {
		ObservationCategory category = new ObservationCategory();
		category.setTag(-1L);
		categories.add(category);
	}

	/**
	 * Removes the given category from the list of the categories.
	 * 
	 * @param category
	 *            The category to be removed.
	 */
	public void remove(ObservationCategory category) {
		categories.remove(category);
	}

}
