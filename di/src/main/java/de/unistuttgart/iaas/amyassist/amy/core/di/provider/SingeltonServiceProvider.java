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

package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;

/**
 * A ServiceProvider which provides only a single existing instance
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the type of the singleton
 */
public class SingeltonServiceProvider<T> implements ServiceFunction<T> {

	private final T instance;

	/**
	 * 
	 * @param instance
	 *            the singleton
	 */
	public SingeltonServiceProvider(@Nonnull T instance) {
		this.instance = instance;
	}

	@Override
	public T getService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies,
			@Nullable Map<String, ?> context) {
		return this.instance;
	}

	@Override
	public Collection<ServiceDescription<?>> getDependencies() {
		return Collections.emptySet();
	}

	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.emptySet();
	}
}
