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

package de.unistuttgart.iaas.amyassist.amy.plugin.alarmclock;

import java.util.NoSuchElementException;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.PostConstruct;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.plugin.api.IStorage;

/**
 * Implements the IAlarmStorage
 * 
 * @author Patrick Singer, Patrick Gebhardt, Florian Bauer
 *
 */
@Service
public class AlarmClockStorage implements IAlarmClockStorage {

	/**
	 * Core storage object that is instantiated via Dependency Injection
	 */
	@Reference
	IStorage storage;

	/**
	 * Storage key for the alarm counter value
	 */
	protected static final String ALARMCOUNTER = "alarmCounter";

	/**
	 * Storage key for the timer counter value
	 */
	protected static final String TIMERCOUNTER = "timerCounter";

	@Override
	public void storeAlarm(Alarm alarm) {
		this.storage.put("alarm" + alarm.getId(), alarm.toString());
	}

	@Override
	public void storeTimer(Timer timer) {
		this.storage.put("timer" + timer.getId(), timer.toString());
	}

	@Override
	public int getAlarmCounter() {
		return Integer.parseInt(this.storage.get(ALARMCOUNTER));

	}

	@Override
	public int getTimerCounter() {
		return Integer.parseInt(this.storage.get(TIMERCOUNTER));

	}

	@Override
	public void putAlarmCounter(int number) {
		this.storage.put(ALARMCOUNTER, number + "");
	}

	@Override
	public void putTimerCounter(int number) {
		this.storage.put(TIMERCOUNTER, number + "");
	}

	@Override
	public int incrementAlarmCounter() {
		int counter = Integer.parseInt(this.storage.get(ALARMCOUNTER));
		counter++;
		this.storage.put(ALARMCOUNTER, Integer.toString(counter));
		return counter;
	}

	@Override
	public int incrementTimerCounter() {
		int counter = Integer.parseInt(this.storage.get(TIMERCOUNTER));
		counter++;
		this.storage.put(TIMERCOUNTER, Integer.toString(counter));
		return counter;
	}

	@Override
	public boolean hasAlarm(int id) {
		return this.storage.has("alarm" + id);
	}

	@Override
	public boolean hasTimer(int id) {
		return this.storage.has("timer" + id);
	}

	@Override
	public void deleteAlarm(int id) {
		if (this.storage.has("alarm" + id))
			this.storage.delete("alarm" + id);
		else
			throw new NoSuchElementException();
	}

	@Override
	public void deleteTimer(int id) {
		if (this.storage.has("timer" + id))
			this.storage.delete("timer" + id);
		else
			throw new NoSuchElementException();
	}

	@Override
	public Alarm getAlarm(int id) {
		if (this.storage.has("alarm" + id)) {
			String alarmString = this.storage.get("alarm" + id);
			return Alarm.reconstructObject(alarmString);
		}
		throw new NoSuchElementException();
	}

	@Override
	public Timer getTimer(int id) {
		if (this.storage.has("timer" + id)) {
			String timerString = this.storage.get("timer" + id);
			return Timer.reconstructObject(timerString);
		}
		throw new NoSuchElementException();
	}

	/**
	 *
	 * Initialization method for logic class.
	 */
	@PostConstruct
	public void init() {
		if (!this.storage.has("ALARMCOUNTER"))
			this.storage.put(ALARMCOUNTER, "0");
		if (!this.storage.has(TIMERCOUNTER))
			this.storage.put(TIMERCOUNTER, "0");
	}

}
