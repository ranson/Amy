/*
 * amy-di
 *
 * TODO: Project Beschreibung
 *
 * @author Tim Neumann
 * @version 1.0.0
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.di;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.ClassProvider;

/**
 * A test service for DependencyInjectionScopeTest
 * 
 * @author Tim Neumann, Leon Kiefer
 */
@Service
public class Service12 {

	@Context(ClassProvider.class)
	private Class<?> consumerClass;

	/**
	 * @return the consumerClass
	 */
	public Class<?> getConsumerClass() {
		return consumerClass;
	}

	public int id;
}
