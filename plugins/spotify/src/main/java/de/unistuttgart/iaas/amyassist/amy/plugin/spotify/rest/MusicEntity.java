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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Music Entity for JSON
 * 
 * @author Muhammed Kaya, Lars Buttgereit
 */
@XmlRootElement
public class MusicEntity {

	/**
	 * constructor for a MusicEntity
	 */
	public MusicEntity() {
		
	}
	
	/**
	 * constructor for a MusicEntity with set values
	 */
	public MusicEntity(String title, String artist) {
		this.title = title;
		this.artist = artist;
	}
	
	/**
	 * the artist of the music
	 */
	public String artist;
	
	/**
	 * the title of the music
	 */
	public String title;
	
	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.title + " " + this.artist;
	}
}
