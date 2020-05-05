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
package com.moveatis.timezone;

import java.util.Calendar;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * The class to keep the default time zone of Moveatis.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
public class TimeZoneInformation {

	/**
	 * We use stardard UTC timezone for saving information to the server - the
	 * client can then convert this time his/her timezone
	 */
	private static final TimeZone TIMEZONE = TimeZone.getTimeZone("UTC");

	public TimeZoneInformation() {

	}

	public static TimeZone getTimeZone() {
		return TIMEZONE;
	}

	/**
	 * Gets a TimeZone from the time zone offset and the daylight saving time.
	 *
	 * @param offset
	 *            the time zone offset in milliseconds.
	 * @param DSTSaving
	 *            the daylight saving time in milliseconds.
	 */
	public static TimeZone getTimeZoneFromOffset(int offset, int DSTSaving) {
		if (DSTSaving > 0) {
			return new SimpleTimeZone(offset - DSTSaving, "GMT/" + offset, Calendar.JANUARY, -1, Calendar.SUNDAY, 2,
					Calendar.DECEMBER, -1, Calendar.SUNDAY, 2, DSTSaving);
		} else {
			return new SimpleTimeZone(offset, "GMT/" + offset);
		}
	}
}
