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

package de.unistuttgart.iaas.amyassist.amy;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;

/**
 * A Jupiter Extension for the TestFramework
 * 
 * @author Leon Kiefer
 */
public class FrameworkExtention
		implements TestInstancePostProcessor, BeforeTestExecutionCallback, AfterTestExecutionCallback {

	private TestFramework testFramework;

	/**
	 * @see org.junit.jupiter.api.extension.TestInstancePostProcessor#postProcessTestInstance(java.lang.Object,
	 *      org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void postProcessTestInstance(Object instance, ExtensionContext arg1) throws Exception {
		this.testFramework = new TestFramework();
		this.testFramework.setup(instance);
	}

	/**
	 * @see org.junit.jupiter.api.extension.BeforeTestExecutionCallback#beforeTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void beforeTestExecution(ExtensionContext arg0) throws Exception {
		this.testFramework.before();
	}

	/**
	 * @see org.junit.jupiter.api.extension.AfterTestExecutionCallback#afterTestExecution(org.junit.jupiter.api.extension.ExtensionContext)
	 */
	@Override
	public void afterTestExecution(ExtensionContext arg0) throws Exception {
		this.testFramework.after();
	}
}