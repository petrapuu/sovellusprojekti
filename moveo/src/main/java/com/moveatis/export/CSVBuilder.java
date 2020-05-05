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
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Builds CSV formatted data to OutputStream.
 * 
 * @author Ilari Paananen
 */
public class CSVBuilder {
	private OutputStreamWriter out;
	private String sep;
	private boolean atLineBegin;

	/**
	 * Constructs a builder with the given stream and separator.
	 * 
	 * @param output
	 *            The stream to write the CSV data to.
	 * @param separator
	 *            The separator to be used between the fields.
	 */
	public CSVBuilder(OutputStream output, String separator) {
		out = new OutputStreamWriter(output, StandardCharsets.UTF_8);
		sep = separator;
		atLineBegin = true;
	}

	/**
	 * Adds the long field to the stream.
	 * 
	 * @param value
	 *            The field value.
	 * @return The instance of the CSVBuilder for convenience.
	 * @throws IOException
	 */
	public CSVBuilder add(Long value) throws IOException {
		writeSep();
		write(value.toString());
		return this;
	}

	/**
	 * Adds the long field followed by a percent character (%) to the stream.
	 * 
	 * @param value
	 *            Field value.
	 * @return The instance of the CSVBuilder for convenience.
	 * @throws IOException
	 */
	public CSVBuilder addPercent(Long value) throws IOException {
		writeSep();
		write(value + "%");
		return this;
	}

	/**
	 * Escapes the string field and adds it to the stream.
	 * 
	 * @param value
	 *            The field value.
	 * @return The instance of the CSVBuilder for convenience.
	 * @throws IOException
	 */
	public CSVBuilder add(String value) throws IOException {
		writeSep();
		if (value != null)
			write("\"" + StringEscapeUtils.escapeCsv(value) + "\"");
		else
			write("\"\"");
		return this;
	}

	/**
	 * Adds a CSV new line to the stream.
	 * 
	 * @return The instance of the CSVBuilder for convenience.
	 * @throws IOException
	 */
	public CSVBuilder newLine() throws IOException {
		write("\r\n");
		atLineBegin = true;
		return this;
	}

	/**
	 * Closes the writer that uses the OutputStream given in the constructor.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		out.close();
	}

	/**
	 * Writes a string to the output stream. Makes it easier to replace member
	 * OutputStreamWriter with something else if needed.
	 * 
	 * @param s
	 *            String to write.
	 * @throws IOException
	 */
	private void write(String s) throws IOException {
		out.write(s);
	}

	/**
	 * Writes separator if we aren't at the begin of a line.
	 * 
	 * @throws IOException
	 */
	private void writeSep() throws IOException {
		if (atLineBegin)
			atLineBegin = false;
		else
			write(sep);
	}
}
