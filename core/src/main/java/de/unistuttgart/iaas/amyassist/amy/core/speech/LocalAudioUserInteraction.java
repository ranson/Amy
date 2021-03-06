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

package de.unistuttgart.iaas.amyassist.amy.core.speech;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;

import org.slf4j.Logger;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;
import de.unistuttgart.iaas.amyassist.amy.core.speech.data.RuntimeExceptionRecognizerCantBeCreated;
import de.unistuttgart.iaas.amyassist.amy.core.speech.grammar.GrammarObjectsCreator;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.LocalSpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.recognizer.manager.SpeechRecognizerManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.tts.TextToSpeech;

/**
 * Manager of the Local Speech Recognition System
 * 
 * @author Kai Menzel
 */
@Service(LocalAudioUserInteraction.class)
public class LocalAudioUserInteraction implements AudioUserInteraction {

	@Reference
	private Logger logger;

	@Reference
	private SpeechInputHandler inputHandler;

	@Reference
	private TextToSpeech tts;

	@Reference
	private GrammarObjectsCreator grammarData;

	private SpeechRecognizerManager localRecognition;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.AudioUserInteraction#init()
	 */
	@Override
	public void init() {
		this.localRecognition = new LocalSpeechRecognizerManager(createNewAudioInputStream(), this.inputHandler,
				this.tts, this.grammarData);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.AudioUserInteraction#start()
	 */
	@Override
	public void start() {
		this.localRecognition.start();
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.speech.AudioUserInteraction#stop()
	 */
	@Override
	public void stop() {
		this.localRecognition.stop();
	}

	/**
	 * starts the default AudioInputStream
	 * 
	 * @return AudioInputStream from the local mic
	 */
	private AudioInputStream createNewAudioInputStream() {
		TargetDataLine mic = null;
		try {
			mic = AudioSystem.getTargetDataLine(this.getFormat());
			mic.open(this.getFormat());
			mic.start();
			return new AudioInputStream(mic);
		} catch (LineUnavailableException e) {
			throw new RuntimeExceptionRecognizerCantBeCreated("AudioInputStream can't be created", e);
		}
	}

	/**
	 * Returns the AudioFormat for the default AudioInputStream
	 * 
	 * @return fitting AudioFormat
	 */
	private AudioFormat getFormat() {
		final float sampleRate = 16000.0f;
		final int sampleSizeInBits = 16;
		final int channels = 1;
		final boolean signed = true;
		final boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}

}
