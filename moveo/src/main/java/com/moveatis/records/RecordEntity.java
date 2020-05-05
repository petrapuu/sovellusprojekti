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
package com.moveatis.records;

import java.io.File;
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.moveatis.abstracts.AbstractRecordEntity;
import com.moveatis.observation.ObservationCategory;
import com.moveatis.observation.ObservationEntity;

/**
 * The entity represent the data of a record, which will be persisted to the
 * database.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Entity
@Table(name = "RECORD")
public class RecordEntity extends AbstractRecordEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	private ObservationCategory category;
	private Long endTime;

	@ManyToOne
	private ObservationEntity observation;

	// Not used in version 1.0
	private File voiceComment;

	public ObservationEntity getObservation() {
		return observation;
	}

	public void setObservation(ObservationEntity observation) {
		this.observation = observation;
	}

	public ObservationCategory getCategory() {
		return category;
	}

	public void setObservationCategory(ObservationCategory category) {
		this.category = category;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	/**
	 * Not used in version 1.0.
	 * 
	 * @return The file for the voice comment.
	 */
	public File getVoiceComment() {
		return voiceComment;
	}

	/**
	 * Not used in version 1.0.
	 * 
	 * @param voiceComment
	 *            The file that holds the voice comment.
	 */
	public void setVoiceComment(File voiceComment) {
		this.voiceComment = voiceComment;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RecordEntity)) {
			return false;
		}
		RecordEntity other = (RecordEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.records.RecordEntity[ id=" + id + " ]";
	}

}
