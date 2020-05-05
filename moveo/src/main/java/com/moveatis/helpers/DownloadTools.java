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
package com.moveatis.helpers;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;

/**
 * Provides static file-download functionalities
 * 
 * @author Visa Nykänen
 */
public class DownloadTools {
	/**
	 * Creates a png-file with the given filename from a byte array
	 * 
	 * @param filename
	 *            the filename for the image
	 * @param img_bytes
	 *            the byte-array containing the image
	 * @return The image-file
	 */
	public static File getImageFromByteArr(String filename, byte[] img_bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(img_bytes);
		BufferedImage image;
		File outputfile = null;
		try {
			image = ImageIO.read(bis);
			bis.close();
			outputfile = File.createTempFile(filename, ".png");
			ImageIO.write(image, "png", outputfile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return outputfile;
	}

	/**
	 * Downloads the given file as the given MIME-type with the given name
	 * 
	 * @param file
	 *            the file to be downloaded
	 * @param responseType
	 *            the MIME-type of the file
	 * @param downloadName
	 *            The name with which the file is downloaded
	 */
	public static void downloadFile(File file, String responseType, String downloadName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset();

		ec.setResponseContentType(responseType);
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + downloadName + "\"");
		try {
			OutputStream outputStream = ec.getResponseOutputStream();
			FileInputStream input = new FileInputStream(file);
			byte[] buffer = new byte[1024];
			while ((input.read(buffer)) != -1) {
				outputStream.write(buffer);
			}
			outputStream.flush();
			input.close();
			fc.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Downloads the data given in a string as a UTF8-encoded csv-file
	 * 
	 * @param data
	 *            The data to be downloaded
	 * @param fileName
	 *            The name for the csv-file to be downloaded
	 */
	public static void downloadCSV(String data, String fileName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		ExternalContext ec = fc.getExternalContext();

		ec.responseReset();

		ec.setResponseContentType("text/csv");
		ec.setResponseHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".csv" + "\"");
		try {
			OutputStream outputStream = ec.getResponseOutputStream();
			byte[] bytes = data.getBytes("Windows-1252");
			for (byte b : bytes) {
				outputStream.write(b);
			}
			outputStream.flush();
			fc.responseComplete();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
