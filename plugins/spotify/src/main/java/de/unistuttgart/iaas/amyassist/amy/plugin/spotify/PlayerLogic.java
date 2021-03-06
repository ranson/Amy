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

package de.unistuttgart.iaas.amyassist.amy.plugin.spotify;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;

import com.wrapper.spotify.model_objects.miscellaneous.CurrentlyPlayingContext;
import com.wrapper.spotify.model_objects.miscellaneous.Device;
import com.wrapper.spotify.model_objects.specification.Paging;
import com.wrapper.spotify.model_objects.specification.Track;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.DeviceEntity;
import de.unistuttgart.iaas.amyassist.amy.plugin.spotify.rest.PlaylistEntity;

/**
 * This class have methods to control a spotify client from a user. For examlpe play, pause playback or search for music
 * tracks etc.
 * 
 * @author Lars Buttgereit
 */
@Service(PlayerLogic.class)
public class PlayerLogic {
	@Reference
	private SpotifyAPICalls spotifyAPICalls;
	@Reference
	private Search search;
	@Reference
	private Logger logger;

	private static final int VOLUME_MUTE_VALUE = 0;
	private static final int VOLUME_MAX_VALUE = 100;
	private static final int VOLUME_UPDOWN_VALUE = 10;

	/**
	 * needed for the first init. need the clientID and the clientSecret form a spotify devloper account
	 * 
	 * @param clientID
	 *            from spotify developer account
	 * @param clientSecret
	 *            spotify developer account
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit(String clientID, String clientSecret) {
		this.spotifyAPICalls.setClientID(clientID);
		this.spotifyAPICalls.setClientSecret(clientSecret);
		return this.spotifyAPICalls.authorizationCodeUri();
	}

	/**
	 * needed for the first init. this method need the properties file in apikeys
	 * 
	 * @return login link to a personal spotify account
	 */
	public URI firstTimeInit() {
		return this.spotifyAPICalls.authorizationCodeUri();
	}

	/**
	 * create the refresh token in he authorization object with the authCode
	 * 
	 * @param authCode
	 *            Callback from the login link
	 */
	public void inputAuthCode(String authCode) {
		this.spotifyAPICalls.createRefreshToken(authCode);
	}

	/**
	 * get all devices that logged in at the moment
	 * 
	 * @return empty ArrayList if no device available else Maps with the name, id and type of the device
	 */
	public List<DeviceEntity> getDevices() {
		List<DeviceEntity> devicesList = new ArrayList<>();
		Device[] devices = this.spotifyAPICalls.getDevices();
		if (devices != null) {
			for (int i = 0; i < devices.length; i++) {
				DeviceEntity deviceData;
				deviceData = new DeviceEntity(devices[i].getType(),
						devices[i].getName(), devices[i].getId());
				devicesList.add(deviceData);
			}
		}
		return devicesList;
	}

	/**
	 * set the given device as acutal active device for playing music
	 * 
	 * @param deviceNumber
	 *            index of the device array. Order is the same as in the output in getDevices
	 * @return selected device
	 */
	public String setDevice(int deviceNumber) {
		List<DeviceEntity> devices = getDevices();
		if (devices.size() > deviceNumber) {
			this.spotifyAPICalls.setCurrentDevice(devices.get(deviceNumber).getID());
			return devices.get(deviceNumber).getName();
		}
		this.logger.warn("No device with this number was found");
		return "No device found";
	}

	/**
	 * set a device direct with the device id
	 * 
	 * @param deviceID
	 *            from a spotify device
	 * @return true if the device is available, else false
	 */
	public boolean setDevice(String deviceID) {
		return this.spotifyAPICalls.setCurrentDevice(deviceID);
	}

	/**
	 * this call the searchAnaything method in the Search class
	 * 
	 * @param searchText
	 *            the text you want to search
	 * @param type
	 *            artist, track, playlist, album
	 * @param limit
	 *            how many results maximal searched for
	 * @return one output String with all results
	 */
	public List<Map<String, String>> search(String searchText, String type, int limit) {
		return this.search.searchList(searchText, type, limit);
	}

	/**
	 * this play method play a featured playlist from spotify
	 * 
	 * @return a Playlist object. can be null
	 */
	public PlaylistEntity play() {
		List<PlaylistEntity> playLists;
		playLists = this.search.getFeaturedPlaylists(5);
		if (!playLists.isEmpty() && 1 < playLists.size()
				&& this.spotifyAPICalls.playListFromUri(playLists.get(1).getUri())) {
			return playLists.get(1);
		}
		this.logger.warn("no featured playlist found");
		return null;
	}

	/**
	 * this method play the item that searched before. Use only after a search
	 * 
	 * @param songNumber
	 *            number of the item form the search before
	 * @param type
	 *            to find the right search Results
	 * @return a map with the song data
	 */
	public Map<String, String> play(int songNumber, SearchTypes type) {
		if (songNumber < this.search.restoreUris(type).size()) {
			this.spotifyAPICalls.playListFromUri(this.search.restoreUris(type).get(songNumber));
		} else {
			this.logger.warn("Item not found");
		}
		return new HashMap<>();
	}

	/**
	 * resume the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean resume() {
		return this.spotifyAPICalls.resume();
	}

	/**
	 * pause the actual playback
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean pause() {
		return this.spotifyAPICalls.pause();
	}

	/**
	 * goes one song forward in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean skip() {
		return this.spotifyAPICalls.skip();
	}

	/**
	 * goes one song back in the playlist or album
	 * 
	 * @return a boolean. true if the command was executed, else if the command failed
	 */
	public boolean back() {
		return this.spotifyAPICalls.back();
	}

	/**
	 * gives the actual played song in the spotify client back
	 * 
	 * @return a hashMap with the keys name and artist
	 */
	public Map<String, String> getCurrentSong() {
		CurrentlyPlayingContext currentlyPlayingContext = this.spotifyAPICalls.getCurrentSong();
		if (currentlyPlayingContext != null) {
			Track[] track = { currentlyPlayingContext.getItem() };
			return this.search.createTrackOutput(new Paging.Builder<Track>().setItems(track).build()).get(0);
		}
		return new HashMap<>();

	}

	/**
	 * get a list from user created or followed playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getOwnPlaylists(int limit) {
		return this.search.getOwnPlaylists(limit);
	}

	/**
	 * get a list from featured playlists
	 * 
	 * @param limit
	 *            limit of returned playlists
	 * @return a list from Playlists
	 */
	public List<PlaylistEntity> getFeaturedPlaylists(int limit) {
		return this.search.getFeaturedPlaylists(limit);
	}

	/**
	 * this method controls the volume of the player
	 * 
	 * @param volumeString
	 *            allowed strings: mute, max, up, down
	 * @return a int from 0-100. This represent the Volume in percent. if the volume is unknown the return value is -1
	 */
	public int setVolume(String volumeString) {
		int volume = this.spotifyAPICalls.getVolume();
		if (volume != -1) {
			switch (volumeString) {
			case "mute":
				this.spotifyAPICalls.setVolume(VOLUME_MUTE_VALUE);
				return VOLUME_MUTE_VALUE;
			case "max":
				this.spotifyAPICalls.setVolume(VOLUME_MAX_VALUE);
				return VOLUME_MAX_VALUE;
			case "up":
				volume = Math.min(VOLUME_MAX_VALUE, volume + VOLUME_UPDOWN_VALUE);
				this.spotifyAPICalls.setVolume(volume);
				return volume;
			case "down":
				volume = Math.max(VOLUME_MUTE_VALUE, volume - VOLUME_UPDOWN_VALUE);
				this.spotifyAPICalls.setVolume(volume);
				return volume;
			default:
				this.logger.warn("Incorrect volume command");
				return -1;
			}
		}
		return volume;
	}

	/**
	 * set the volume direct with an integer
	 * 
	 * @param volume
	 *            a interger between 0 and 100
	 * @return the new volume. -1 the volume was not between 0 and 100
	 */
	public int setVolume(int volume) {
		if (volume >= VOLUME_MUTE_VALUE && volume <= VOLUME_MAX_VALUE) {
			this.spotifyAPICalls.setVolume(volume);
			return volume;
		}
		return -1;
	}

}
