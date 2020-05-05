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
package com.moveatis.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.moveatis.observation.ObservationEntity;
import com.moveatis.records.RecordEntity;
import com.moveatis.user.AbstractUser;

/**
 * The interface to manage the observation entity. The observation contains the
 * records a user made when he or she was observing some event.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Local(Observation.class)
public interface Observation {

	void create(ObservationEntity observationEntity);

	void edit(ObservationEntity observationEntity);

	void remove(ObservationEntity observationEntity);

	void removeUnsavedObservation(ObservationEntity observationEntity);

	ObservationEntity find(Object id);

	List<ObservationEntity> findAll();

	List<ObservationEntity> findAllByObserver(AbstractUser observer);

	List<ObservationEntity> findWithoutEvent(AbstractUser observer);

	List<ObservationEntity> findByEventsNotOwned(AbstractUser observer);

	List<ObservationEntity> findRange(int[] range);

	List<RecordEntity> findRecords(Object id);

	int count();
}
