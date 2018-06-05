/*
 * This source file is part of the Amy open source project.
 * For more information see github.com/AmyAssist
 * 
 * Copyright (c) 2018 the Amy project authors.
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
 */

package de.unistuttgart.iaas.amyassist.amy.core.taskscheduler.api;

import java.util.Date;

/**
 * Service to schedule and execute concurrent tasks.
 * 
 * @author Leon Kiefer
 */
public interface TaskSchedulerAPI {
	/**
	 * Executes the given command at some time in the future.
	 * 
	 * @param runnable
	 *            The runnable to run in the future
	 */
	void execute(Runnable runnable);

	/**
	 * Schedules the given task to execute at the given time
	 * 
	 * @param task
	 *            the task to execute
	 * @param date
	 *            The date at which to execute that task
	 */
	void schedule(Runnable task, Date date);
}