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

import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.moveatis.abstracts.AbstractObservationEntity;
import com.moveatis.records.RecordEntity;

/**
 * The entity represents the observation data, that is saved to the database.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Table(name = "OBSERVATION")
@NamedQueries({
		@NamedQuery(name = "findByObserver", query = "SELECT observation FROM ObservationEntity observation WHERE observation.observer=:observer AND observation.removed is null"),
		@NamedQuery(name = "findWithoutEvent", query = "SELECT observation FROM ObservationEntity observation WHERE observation.observer=:observer AND observation.event is null AND observation.removed is null"),
		@NamedQuery(name = "findByEventsNotOwned", query = "SELECT observation FROM ObservationEntity observation WHERE observation.observer=:observer AND observation.event.creator<>:observer AND observation.removed is null") })
@Entity
public class ObservationEntity extends AbstractObservationEntity implements Serializable {

	private String name;

	private String target;

	private static final long serialVersionUID = 1L;

	@ElementCollection(fetch = FetchType.LAZY)
	private Set<ObservationCategorySet> observationCategorySets;

	@OneToMany(mappedBy = "observation", fetch = FetchType.LAZY, cascade = ALL)
	private List<RecordEntity> records;

	/**
	 * Whether the observation should be saved to database
	 */
	private Boolean userWantsToSaveToDatabase;

	public Boolean getUserWantsToSaveToDatabase() {
		return userWantsToSaveToDatabase;
	}

	public void setUserWantsToSaveToDatabase(Boolean userWantsToSaveToDatabase) {
		this.userWantsToSaveToDatabase = userWantsToSaveToDatabase;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public List<RecordEntity> getRecords() {
		return records;
	}

	public void setRecords(List<RecordEntity> records) {
		this.records = records;
	}

	public void addRecord(RecordEntity record) {
		if (this.getRecords() == null) {
			this.records = new ArrayList<>();
		}
		getRecords().add(record);
		record.setObservation(this);
	}

	public Set<ObservationCategorySet> getObservationCategorySets() {
		return observationCategorySets;
	}

	public void setObservationCategorySets(Set<ObservationCategorySet> observationCategorySets) {
		this.observationCategorySets = observationCategorySets;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ObservationEntity)) {
			return false;
		}
		ObservationEntity other = (ObservationEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.observation.ObservationEntity[ id=" + id + " ]";
	}

}
