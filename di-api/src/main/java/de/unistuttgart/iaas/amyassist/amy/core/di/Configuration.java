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

package de.unistuttgart.iaas.amyassist.amy.core.di;

import javax.annotation.Nonnull;

import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;

/**
 * Configuration of the Service Locator
 * 
 * @author Leon Kiefer
 */
public interface Configuration {
	/**
	 * Adds an external Service instance to the DI. The DI does not manage the dependencies of the external Service, but
	 * the DI can inject the external Service as dependency into other managed services.
	 * 
	 * @param serviceType
	 *            The type of this service
	 * @param externalService
	 *            The instance of this service
	 */
	<T> void addExternalService(@Nonnull Class<T> serviceType, @Nonnull T externalService);

	void registerContextProvider(String key, StaticProvider<?> staticProvider);

	/**
	 * Registers a service
	 * 
	 * @param cls
	 *            The service to register.
	 */
	void register(@Nonnull Class<?> cls);

	/**
	 * Registers a service provider
	 * 
	 * @param serviceDescription
	 *            The description of service
	 * @param serviceFunction
	 *            The instance of the service provider
	 */
	<T> void register(ServiceDescription<T> serviceDescription, ServiceFunction<T> serviceFunction);

	/**
	 * Registers a service provider
	 * 
	 * @param serviceType
	 *            The type of this service
	 * @param serviceFunction
	 *            The instance of the service provider
	 */
	<T> void register(Class<T> serviceType, ServiceFunction<T> serviceFunction);

}
