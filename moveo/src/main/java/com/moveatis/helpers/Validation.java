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
package com.moveatis.helpers;

/**
 * The helper class contains the static validation function(s).
 * 
 * @author Ilari Paananen
 */
public class Validation {

	/**
	 * Validates the given string so that it can be placed as a JavaScript string in
	 * an HTML script tag.
	 * 
	 * @param s
	 *            The string to be validated.
	 * @return The validated string that contains only accepted characters.
	 */
	public static String validateForJsAndHtml(String s) {
		// TODO: Allow some other chars? Don't allow some of these?
		// Maybe only blacklist characters shown here:
		// http://benv.ca/2012/10/02/you-are-probably-misusing-DOM-text-methods/
		String validChars = " ,.-;:_!?*/+()[]{}|=#";

		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length();) {
			int codePoint = s.codePointAt(i);
			if (Character.isLetterOrDigit(codePoint) || (validChars.indexOf(codePoint) >= 0)) {
				sb.appendCodePoint(codePoint);
			}
			i += Character.charCount(codePoint);
		}

		return sb.toString();
	}
}
