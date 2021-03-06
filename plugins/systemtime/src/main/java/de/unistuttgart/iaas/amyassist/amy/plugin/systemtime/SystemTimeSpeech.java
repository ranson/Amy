/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
 *
 * SPDX-License-Identifier: Apache-2.0
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at 
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * For more information see notice.md
 */

package de.unistuttgart.iaas.amyassist.amy.plugin.systemtime;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.SpeechCommand;

/**
 * A plugin which tells time and date
 * 
 * @author Florian Bauer, Patrick Gebhardt
 */
@Service
@SpeechCommand({ "what is", "tell me" })
public class SystemTimeSpeech {

	@Reference
	private SystemTimeLogic logic;

	/**
	 * A method to convert the integer day to an ordinal (from 1 to 31)
	 * 
	 * @param i
	 *            the day as integer
	 * @return the day as ordinal, e.g. 1st
	 */
	public static String ordinal(int i) {
		String[] ordinals = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		if (10 < i && i < 14) {
			return i + "th";
		} else {
			return i + ordinals[i % 10];
		}
	}

	/**
	 * A method which returns the current time
	 * 
	 * @return current time (hour minute) in a string, e.g. it is 10:30
	 */
	@Grammar("the time")
	public String time(String[] s) {
		return "it is " + this.logic.getHour() + ":" + this.logic.getMinute();
	}

	/**
	 * A method which returns the current date
	 * 
	 * @return current date (day month year) in a string, e.g. it is the 20th of june
	 */
	@Grammar("the date")
	public String date(String[] s) {
		return "it is the " + ordinal(Integer.parseInt(this.logic.getDay())) + " of " + this.logic.getMonth();
	}

	/**
	 * A method which returns the current year
	 * 
	 * @return current year in a string, e.g. it is 2018
	 */
	@Grammar("the year")
	public String year(String[] s) {
		return "it is " + this.logic.getYear();
	}

}
