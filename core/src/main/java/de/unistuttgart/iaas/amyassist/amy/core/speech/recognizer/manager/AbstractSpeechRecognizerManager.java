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

package de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.SpeechRecognizer;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.handler.RecognitionResultHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.Output;

/**
 * Class that manages the Recognizers belonging to a given AudioInputStream
 * 
 * @author Kai Menzel
 */
public abstract class AbstractSpeechRecognizerManager
		implements SpeechRecognitionResultManager, SpeechRecognizerManager {

	private final Logger logger = LoggerFactory.getLogger(AbstractSpeechRecognizerManager.class);

	private SpeechInputHandler inputHandler;
	private Output output;
	private String mainGrammarName;

	private SpeechRecognizer mainRecognizer;
	private Map<String, SpeechRecognizer> recognizerList = new HashMap<>();

	private boolean soundPlaying = false;
	private boolean srListening = false;

	private Thread currentRecognizer;

	private boolean recognitionThreadRunning;

	/**
	 * Object that handles All Recognizers with the given AudioInputStream
	 * 
	 * @param ais
	 *            AudioInputStream for the SpeechRecognition
	 * @param inputHandler
	 *            Handler that will handle the SpeechRecognitionResult
	 * @param output
	 *            Output Object where to Output the result of the Recognizer
	 * @param grammarData
	 *            DataSet of all GrammarObjects
	 * 
	 */
	public AbstractSpeechRecognizerManager(AudioInputStream ais, SpeechInputHandler inputHandler, Output output,
			GrammarObjectsCreator grammarData) {
		this.inputHandler = inputHandler;
		this.output = output;
		this.mainGrammarName = grammarData.getMainGrammar().getName();

		createRecognizers(grammarData, ais);

		this.currentRecognizer = new Thread(this.mainRecognizer);
	}

	private void createRecognizers(GrammarObjectsCreator grammarData, AudioInputStream ais) {
		this.mainRecognizer = new SpeechRecognizer(grammarData.getMainGrammar(),
				getMainResultHandler(grammarData.getMainGrammar()), ais);

		if (grammarData.getSwitchableGrammars() != null && !grammarData.getSwitchableGrammars().isEmpty()) {
			for (Grammar grammar : grammarData.getSwitchableGrammars()) {
				if (!this.recognizerList.containsKey(grammar.getName())) {
					this.recognizerList.put(grammar.getName(),
							new SpeechRecognizer(grammar, getResultHandler(grammar), ais));
				}
			}
		}
	}

	/**
	 * Get the correct ResultHandler for the environment
	 * 
	 * @param grammar
	 *            Grammar of the Recognizer the Handler is created for
	 * @return ResultHandler for the Recognizer
	 */
	protected abstract RecognitionResultHandler getMainResultHandler(Grammar grammar);

	/**
	 * Get the correct ResultHandler for the environment
	 * 
	 * @param grammar
	 *            Grammar of the Recognizer the Handler is created for
	 * @return ResultHandler for the Recognizer
	 */
	protected abstract RecognitionResultHandler getResultHandler(Grammar grammar);

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager#stop()
	 */
	@Override
	public void start() {
		this.recognitionThreadRunning = true;
		this.currentRecognizer.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager#stop()
	 */
	@Override
	public void stop() {
		this.recognitionThreadRunning = false;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleCommand(java.lang.String)
	 */
	@Override
	public void handleCommand(String result) {
		Future<String> handle = this.inputHandler.handle(result);
		try {
			voiceOutput(handle.get());
		} catch (ExecutionException e) {
			if (e.getCause() != null && e.getCause().getClass().equals(IllegalArgumentException.class)) {
				voiceOutput("unknown command");
			} else {
				this.logger.error("unknown error", e);
			}
		} catch (InterruptedException e) {
			this.logger.error("[Recognition Stopped] Error with SpeechInputhandler Return", e);
			stop();
			Thread.currentThread().interrupt();
		}

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleGrammarSwitch(de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.Grammar)
	 */
	@Override
	public void handleGrammarSwitch(Grammar grammar) {
		if (grammar == null || grammar.getName().equals(this.mainGrammarName)) {
			switchGrammar(this.mainRecognizer);
		} else {
			switchGrammar(this.recognizerList.get(grammar.getName()));
		}
	}

	private void switchGrammar(SpeechRecognizer recognizer) {
		this.currentRecognizer.interrupt();
		this.currentRecognizer = new Thread(recognizer);
		this.currentRecognizer.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#handleListeningState(boolean)
	 */
	@Override
	public void handleListeningState(boolean listening) {
		this.srListening = listening;
		/**
		 * notify(listening);
		 */
	}

	/**
	 * Notify the System that sound is (not longer) Playing
	 * T O D O private void notify(boolean listening) {}
	 */

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isListening()
	 */
	@Override
	public boolean isListening() {
		return this.srListening;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#voiceOutput(java.lang.String)
	 */
	@Override
	public void voiceOutput(String outputString) {
		setSoundPlaying(true);
		this.output.output(this.listener, outputString);

	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#stopOutput()
	 */
	@Override
	public void stopOutput() {
		this.output.stopOutput();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#setSoundPlaying(boolean)
	 */
	@Override
	public void setSoundPlaying(boolean outputActive) {
		this.soundPlaying = outputActive;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isSoundPlaying()
	 */
	@Override
	public boolean isSoundPlaying() {
		return this.soundPlaying;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#setRecognitionThreadRunning(boolean)
	 */
	@Override
	public void setRecognitionThreadRunning(boolean recognitionRunning) {
		this.recognitionThreadRunning = recognitionRunning;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognitionResultManager#isRecognitionThreadRunning()
	 */
	@Override
	public boolean isRecognitionThreadRunning() {
		return this.recognitionThreadRunning;
	}

	/**
	 * listens to the Voice Output
	 */
	private LineListener listener = event -> {
		if (event.getType() == LineEvent.Type.STOP) {
			((Clip) event.getSource()).close();
			setSoundPlaying(false);
		}
	};

}
