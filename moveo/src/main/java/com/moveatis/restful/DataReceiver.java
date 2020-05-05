/* 
 * Copyright (c) 2016, Jarmo Juujärvi, Sami Kallio, Kai Korhonen, Juha Moisio, Ilari Paananen 
 * Copyright (c) 2019, Visa Nykänen, Tuomas Moisio, Petra Puumala, Karoliina Lappalainen 
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
package com.moveatis.restful;

import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.moveatis.managedbeans.FeedbackAnalysisManagedBean;
import com.moveatis.managedbeans.ObservationManagedBean;

/**
 * Receives the images and csv-data created on the client side.
 * 
 * @author Visa Nykänen
 */
@Path("/summary")
public class DataReceiver implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ObservationManagedBean observationManagedBean;

	@Inject
	private FeedbackAnalysisManagedBean feedbackAnalysisManagedBean;

	@Context
	private HttpServletRequest httpRequest;

	/**
	 * Receives base64-encoded image file converts it to a byte array and stores it
	 * in the session.
	 * 
	 * @param data
	 *            The base64-encoded png-file
	 * @throws IOException
	 */
	@POST
	@Path("image")
	@Consumes(MediaType.TEXT_PLAIN)
	public void receiveImage(String data) throws IOException {
		String[] info = data.split(",");
		if (info.length != 3)
			return;
		String base64Image = info[2];
		byte[] img = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64Image);
		if (info[0].equals("obsimg"))
			observationManagedBean.setImage(img);
		if (info[0].equals("analpie"))
			feedbackAnalysisManagedBean.setPieImage(img);
		if (info[0].equals("analbar"))
			feedbackAnalysisManagedBean.setBarImage(img);
		if (info[0].equals("analtable"))
			feedbackAnalysisManagedBean.setTableImage(img);
		if (info[0].equals("reporttable"))
			feedbackAnalysisManagedBean.setReportImage(img);
	}

	/**
	 * Receives csv-data as a string
	 * 
	 * @param data
	 *            the csv-data
	 */
	@POST
	@Path("csv")
	@Consumes(MediaType.TEXT_PLAIN)
	public void receiveCSV(String data) {
		feedbackAnalysisManagedBean.setReportCSV(data);
	}

}
