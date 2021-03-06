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
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceDescription;
import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;

/**
 * 
 * @author Leon Kiefer
 *
 * @param <T>
 *            service type
 */
public interface ServiceProvider<T> {
	/**
	 * @param resolvedDependencies
	 *            a map of ServiceFactories for each dependency
	 * @param context
	 *            a map of the static context information
	 * @return the service of this ServiceProvider for the given context
	 */
	@Nonnull
	T getService(Map<ServiceDescription<?>, ServiceFactory<?>> resolvedDependencies, @Nullable Map<String, ?> context);

	/**
	 * 
	 * @return the dependencies
	 */
	@Nonnull
	Collection<ServiceDescription<?>> getDependencies();

	/**
	 * @return the requiredContextProviderTypes
	 */
	@Nonnull
	Collection<String> getRequiredContextIdentifiers();

}
