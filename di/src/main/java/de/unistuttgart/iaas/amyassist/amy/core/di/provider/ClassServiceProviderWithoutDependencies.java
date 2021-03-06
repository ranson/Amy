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

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * 
 * A Service Provider without dependencies
 * 
 * @author Leon Kiefer
 * @param <T>
 *            the service type
 */
public class ClassServiceProviderWithoutDependencies<T> implements ServiceFunction<T> {

	protected Class<? extends T> cls;

	public ClassServiceProviderWithoutDependencies(Class<? extends T> cls) {
		if (!Util.isValidServiceClass(cls))
			throw new IllegalArgumentException(
					"There is a problem with the class " + cls.getName() + ". It can't be used as a Service");
		this.cls = cls;
	}

	@Override
	public T getService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies, Map<String, ?> context) {
		return this.createService();
	}

	protected T createService() {
		try {
			return this.cls.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("The constructor of " + this.cls.getName() + " should have been checked",
					e);
		}
	}

	@Override
	public Collection<ServiceDescription<?>> getDependencies() {
		return Collections.emptyList();
	}

	@Override
	public Collection<String> getRequiredContextIdentifiers() {
		return Collections.emptyList();
	}
}
