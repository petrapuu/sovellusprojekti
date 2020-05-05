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
package com.moveatis.export;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.observation.ObservationCategory;
import com.moveatis.observation.ObservationEntity;
import com.moveatis.records.RecordEntity;

/**
 * Builds a CSV file from an observation.
 * 
 * @author Ilari Paananen
 */
public class CSVFileBuilder {

	private static final Logger LOGGER = LoggerFactory.getLogger(CSVFileBuilder.class);

	private long totalCount;
	private long totalDuration;

	/**
	 * Builds a CSV file from the given observation and writes it to the output
	 * stream.
	 * 
	 * @param out
	 *            The stream to write the CSV data to.
	 * @param obs
	 *            The observation to be built.
	 * @param separator
	 *            The separator used between the CSV fields.
	 * @throws IOException
	 */
	public void buildCSV(OutputStream out, ObservationEntity obs, String separator) throws IOException {

		Long obsDuration = obs.getDuration();
		Map<ObservationCategory, CategorySummaryItem> summaryItems = computeCategorySummaryItems(obs);
		List<RecordEntity> records = obs.getRecords();

		CSVBuilder csv = new CSVBuilder(out, separator);

		csv.add("Observation info").newLine();
		csv.newLine();

		csv.add("Attribute").add("Value").newLine();

		csv.add("name").add(obs.getName()).newLine();
		csv.add("target").add(obs.getTarget()).newLine();
		csv.add("description").add(obs.getDescription()).newLine();
		csv.add("duration").add(msToTimeStamp(obsDuration)).newLine();
		csv.add("records").add(totalCount).newLine();

		csv.newLine();
		csv.newLine();

		csv.add("Summary").newLine();
		csv.newLine();

		csv.add("Category").add("Count").add("Count %").add("Duration").add("Duration %").newLine();

		for (Map.Entry<ObservationCategory, CategorySummaryItem> entry : summaryItems.entrySet()) {
			String category = entry.getKey().getName();
			CategorySummaryItem item = entry.getValue();
			long countPercent = (long) (item.count * 100.0 / totalCount + 0.5);
			long durationPercent = (long) (item.duration * 100.0 / obsDuration + 0.5);
			csv.add(category).add(item.count).addPercent(countPercent).add(msToTimeStamp(item.duration))
					.addPercent(durationPercent).newLine();
		}

		csv.newLine();
		csv.newLine();

		csv.add("Recordings").newLine();
		csv.newLine();

		csv.add("Category").add("Start time").add("End time").add("Duration").newLine();

		for (RecordEntity record : records) {
			String category = record.getCategory().getName();
			Long startTime = record.getStartTime();
			Long endTime = record.getEndTime();
			csv.add(category).add(msToTimeStamp(startTime)).add(msToTimeStamp(endTime))
					.add(msToTimeStamp(endTime - startTime)).newLine();
		}

		csv.close();
	}

	public String msToTimeStamp(long ms) {
		long s = ms / 1000;
		return s / 60 + " min " + s % 60 + " s";
	}

	/**
	 * Computes category summary items from observation.
	 * 
	 * @param obs
	 *            Observation
	 * @return Summary items mapped by category.
	 */
	private Map<ObservationCategory, CategorySummaryItem> computeCategorySummaryItems(ObservationEntity obs) {

		// TODO: Categories should be in the same order as when the
		// observation was conducted.
		// Observation should contain this info, but does not yet.
		Map<ObservationCategory, CategorySummaryItem> summaryItems = new TreeMap<>(
				new Comparator<ObservationCategory>() {
					@Override
					public int compare(ObservationCategory c1, ObservationCategory c2) {
						return c1.getTag().compareTo(c2.getTag());
					}
				});

		List<RecordEntity> records = obs.getRecords();

		totalCount = 0;
		totalDuration = 0; // NOTE: Not used anywhere.

		for (RecordEntity record : records) {
			ObservationCategory category = record.getCategory();
			Long deltaTime = record.getEndTime() - record.getStartTime();

			CategorySummaryItem item = summaryItems.get(category);
			if (item == null) {
				item = new CategorySummaryItem();
				summaryItems.put(category, item);
			}

			item.count++;
			item.duration += deltaTime;

			totalCount++;
			totalDuration += deltaTime;
		}

		return summaryItems;
	}

	/**
	 * Private class for category summary info.
	 */
	private static class CategorySummaryItem {
		public long count = 0;
		public long duration = 0;
	}

	public void buildCSV(OutputStream outputStream, FeedbackAnalysisEntity feedbackAnalysis, String separator) {
		// TODO Auto-generated method stub

	}

}
