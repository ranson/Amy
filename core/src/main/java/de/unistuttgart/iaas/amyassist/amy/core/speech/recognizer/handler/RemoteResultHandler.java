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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler;

import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager;

/**
 * Handler that handles the remote SpeechRecognition System intern commands
 * 
 * @author Kai Menzel
 */
public class RemoteResultHandler extends AbstractRecognitionResultHandler {

	/**
	 * @param srManager
	 *            Manager Object which handles this ResultHandler
	 * @param grammar
	 *            Grammar this ResultHandler handles
	 */
	public RemoteResultHandler(SpeechRecognitionResultManager srManager, Grammar grammar) {
		super(srManager, grammar);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.AbstractRecognitionResultHandler#environmentSpecificInputHandling(java.lang.String,
	 *      de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager)
	 */
	@Override
	protected boolean environmentSpecificInputHandling(String result, SpeechRecognitionResultManager srManager) {
		return false;
	}

}
