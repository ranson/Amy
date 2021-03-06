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

package de.unistuttgart.iaas.amyassist.amy.restresources.home;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginManager;
import de.unistuttgart.iaas.amyassist.amy.core.speech.SpeechInputHandler;

/**
 * The home resource of Amy
 * 
 * @author Christian Bräuner
 */
@Path(HomeResource.PATH)
public class HomeResource {

	/**
	 * the Path of this resource
	 */
	public static final String PATH = "home";
	@Reference
	private PluginManager manager;
	@Reference
	private SpeechInputHandler speechInputHandler;
	@Context
	private UriInfo uriInfo;

	/**
	 * handles consoleInput from a client
	 * 
	 * @param input
	 *            the input from the client
	 * @return the response from Amy
	 */
	@POST
	@Path("console")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String useAmy(String input) {
		try {
			return this.speechInputHandler.handle(input).get();
		} catch (Exception e) {
			throw new WebApplicationException("can't handle input: "+ input, e, Status.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * returns all installed plugins
	 * 
	 * @return array of installed plugins
	 */
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public SimplePluginEntity[] getPlugins() {
		List<IPlugin> pluginList = this.manager.getPlugins();
		SimplePluginEntity[] plugins = new SimplePluginEntity[pluginList.size()+1];
		for (int i = 0; i < pluginList.size(); i++) {
			plugins[i] = new SimplePluginEntity(pluginList.get(i));
			plugins[i].setLink(createPath(pluginList.get(i)));
		}
		plugins[pluginList.size()] = createConfig();
		return plugins;
	}

	private SimplePluginEntity createConfig() {
		SimplePluginEntity config = new SimplePluginEntity();
		config.setName("Configuration");
		config.setDescription("Configurations for this Amy instance and installed plugins");
		config.setLink(this.uriInfo.getBaseUriBuilder().path("config").toString());
		return config;
	}

	private String createPath(IPlugin iPlugin) {
		List<Class<?>> classes = iPlugin.getClasses();
		for(Class<?> cls : classes) {
			if (cls.isAnnotationPresent(Path.class)) { //TODO change to has parent class
				return this.uriInfo.getBaseUriBuilder().path(cls).build().toString();
			}
		}
		return null;
	}

}
