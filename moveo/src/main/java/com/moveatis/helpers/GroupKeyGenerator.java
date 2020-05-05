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

import java.util.Random;

/**
 * The helper class could be used to generate a random group key, if the user
 * cannot come up with one by herself.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
public class GroupKeyGenerator {

	private static final int EVENGROUP_KEY_LENGTH = 8;
	private static final int LETTERS_COUNT = 25;
	private static final int NUMBERS_COUNT = 10;

	private static final int CHOOSE_LETTER = 0;

	private static final char[] LETTERS = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O',
			'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y' };
	private static final int[] NUMBERS = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };

	public GroupKeyGenerator() {

	}

	public static String getGroupKey() {
		Random randomizer = new Random();
		StringBuilder stb = new StringBuilder();
		int letterOrNumber;

		for (int i = 0; i <= EVENGROUP_KEY_LENGTH; i++) {
			letterOrNumber = randomizer.nextInt(2);
			if (letterOrNumber % 2 == CHOOSE_LETTER) {
				stb.append(LETTERS[randomizer.nextInt(LETTERS_COUNT)]);
			} else {
				stb.append(NUMBERS[randomizer.nextInt(NUMBERS_COUNT)]);
			}
		}

		return stb.toString();
	}

}
