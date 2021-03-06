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

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import de.unistuttgart.iaas.amyassist.amy.core.di.util.Util;

/**
 * Test the equals method of ServiceDescriptionImpl
 * 
 * @author Leon Kiefer
 */
class ServiceDescriptionImplTest {

	@ParameterizedTest
	@MethodSource("values")
	void testNotEquals(Object obj) {
		assertThat(Util.serviceDescriptionFor(Service7API.class), is(not(equalTo(obj))));
	}

	static Stream<Object> values() {
		return Stream.of(null, new Object(), Util.serviceDescriptionFor(Service1.class));
	}

}
