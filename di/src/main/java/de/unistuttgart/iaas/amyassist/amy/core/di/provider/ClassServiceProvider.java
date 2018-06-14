package de.unistuttgart.iaas.amyassist.amy.core.di.provider;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.di.ServiceFactory;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Context;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceConsumer;
import de.unistuttgart.iaas.amyassist.amy.core.di.consumer.ServiceFunction;
import de.unistuttgart.iaas.amyassist.amy.core.di.context.provider.StaticProvider;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.NTuple;
import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;
/**
 * A ClassServiceProvider which provides service instances for a class
 * 
 * @author Leon Kiefer
 */
public class ClassServiceProvider<T> implements ServiceFunction<T> {
	private final Logger logger = LoggerFactory.getLogger(ServiceProvider.class);

	private final Class<? extends T> cls;

	/**
	 * A register which contains all dependencies
	 */
	private Collection<Class<?>> dependencies = new HashSet<>();
	private Collection<InjetionPoint> injetionPoints = new HashSet<>();
	private Collection<Class<?>> requiredContextProviderTypes = new HashSet<>();
	private final NTuple<Class<?>> contextType;
	private final NTuple<ContextInjectionPoint> contextInjectionPoints;
	private Map<NTuple<?>, T> serviceInstances = new HashMap<>();

	@Override
	public Collection<Class<?>> getRequiredContextProviderTypes() {
		return Collections.unmodifiableCollection(this.requiredContextProviderTypes);
	}

	@Override
	public Collection<Class<?>> getDependencies() {
		return Collections.unmodifiableCollection(this.dependencies);
	}

	public ClassServiceProvider(@Nonnull Class<? extends T> cls) {
		if (!Util.classCheck(cls))
			throw new IllegalArgumentException(
					"There is a problem with the class " + cls.getName() + ". It can't be used as a Service");

		this.cls = cls;
		Field[] dependencyFields = FieldUtils.getFieldsWithAnnotation(cls, Reference.class);
		for (Field field : dependencyFields) {
			if (field.isAnnotationPresent(Context.class)) {
				throw new IllegalArgumentException();
			}

			InjetionPoint injetionPoint = new InjetionPoint(field);
			this.injetionPoints.add(injetionPoint);
			Class<?> dependency = injetionPoint.getType();
			if (dependencies.contains(dependency)) {
				this.logger.warn("The Service {} have a duplicate dependeny on {}", cls.getName(),
						dependency.getName());
			} else {
				this.dependencies.add(dependency);
			}
		}

		Field[] contextFields = FieldUtils.getFieldsWithAnnotation(cls, Context.class);
		this.contextType = new NTuple<>(contextFields.length);
		this.contextInjectionPoints = new NTuple<>(contextFields.length);
		int i = 0;
		for (Field field : contextFields) {
			ContextInjectionPoint injetionPoint = new ContextInjectionPoint(field);
			this.requiredContextProviderTypes.add(injetionPoint.getContextProviderType());
			this.contextType.set(i, injetionPoint.getContextProviderType());
			this.contextInjectionPoints.set(i, injetionPoint);
			i++;
		}
	}

	private NTuple<?> getContextTuple(Map<Class<?>, StaticProvider<?>> contextProviders,
			@Nullable ServiceConsumer consumer) {
		if (consumer == null) {
			return new NTuple<>(this.contextType.n);
		}

		Class<?> consumerClass = consumer.getConsumerClass();
		return this.contextType.map(type -> contextProviders.get(type).getContext(consumerClass));
	}

	@Override
	public T getService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies,
			Map<Class<?>, StaticProvider<?>> contextProviders, @Nullable ServiceConsumer consumer) {
		NTuple<?> contextTuple = this.getContextTuple(contextProviders, consumer);
		if (this.serviceInstances.containsKey(contextTuple)) {
			return this.serviceInstances.get(contextTuple);
		}

		T createdService = this.createService(resolvedDependencies, contextTuple);
		this.serviceInstances.put(contextTuple, createdService);
		return createdService;
	}

	/**
	 * Create a new Service instance with the given resolved dependencies and
	 * context
	 * 
	 * @param resolvedDependencies
	 * @param contextTuple
	 *            the context
	 * @return
	 */
	private T createService(Map<Class<?>, ServiceFactory<?>> resolvedDependencies, NTuple<?> contextTuple) {
		try {
			T serviceInstance = this.cls.newInstance();
			for (InjetionPoint injetionPoint : injetionPoints) {
				ServiceFactory<?> serviceFactory = resolvedDependencies.get(injetionPoint.getType());
				injetionPoint.inject(serviceInstance, serviceFactory.build());
			}
			for (int i = 0; i < this.contextInjectionPoints.n; i++) {
				ContextInjectionPoint contextInjectionPoint = this.contextInjectionPoints.get(i);
				contextInjectionPoint.inject(serviceInstance, contextTuple.get(i));
			}

			Util.postConstruct(serviceInstance);
			return serviceInstance;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException("The constructor of " + this.cls.getName() + " should have been checked",
					e);
		}
	}

	@Override
	public Class<?> getConsumerClass() {
		return this.cls;
	}
}
