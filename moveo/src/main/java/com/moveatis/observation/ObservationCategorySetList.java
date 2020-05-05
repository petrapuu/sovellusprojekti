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

import java.util.ArrayList;
import java.util.List;

/**
 * The observation has its own category sets, and this class has a list, which
 * holds those category sets.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Ilari Paananen <ilari.k.paananen at student.jyu.fi>
 */
public class ObservationCategorySetList {

	private List<ObservationCategorySet> categorySets = new ArrayList<>();

	public ObservationCategorySetList() {

	}

	public List<ObservationCategorySet> getCategorySets() {
		return categorySets;
	}

	public void setCategorySets(List<ObservationCategorySet> categorySets) {
		this.categorySets = categorySets;
	}

	/**
	 * Adds the given category set to the category set list.
	 */
	public void add(ObservationCategorySet categorySet) {
		categorySets.add(categorySet);
	}

	/**
	 * Creates a clone of the given category set and adds it to the category set
	 * list.
	 */
	public void addClone(ObservationCategorySet categorySet) {
		ObservationCategorySet cloned = new ObservationCategorySet(categorySet.getId(), categorySet.getName());
		for (ObservationCategory category : categorySet.getCategories()) {
			cloned.add(new ObservationCategory(category));
		}
		categorySets.add(cloned);
	}

	/**
	 * Searches for the category set with the given id from the category set list.
	 * Returns the category if it was found and null otherwise.
	 */
	public ObservationCategorySet find(Long id) {
		for (ObservationCategorySet categorySet : categorySets) {
			if (categorySet.getId().equals(id)) {
				return categorySet;
			}
		}
		return null;
	}

	/**
	 * Removes the given category set from the category set list.
	 */
	public void remove(ObservationCategorySet categorySet) {
		categorySets.remove(categorySet);
	}

}
