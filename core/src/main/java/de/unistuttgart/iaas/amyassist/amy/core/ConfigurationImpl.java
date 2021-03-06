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

package de.unistuttgart.iaas.amyassist.amy.core;

import java.util.Set;

import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Reference;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.IPlugin;
import de.unistuttgart.iaas.amyassist.amy.core.pluginloader.PluginLoader;

/**
 * Implementation of {@link Configuration}
 * 
 * @author Leon Kiefer
 */
@Service
public class ConfigurationImpl implements Configuration {

	@Reference
	private PluginLoader loader;

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.Configuration#getInstalledPlugins()
	 */
	@Override
	public String[] getInstalledPlugins() {
		Set<String> pluginNames = this.loader.getPluginNames();
		return pluginNames.toArray(new String[pluginNames.size()]);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.Configuration#getPluginVersion(java.lang.String)
	 */
	@Override
	public String getPluginVersion(String pluginName) {
		IPlugin plugin = this.loader.getPlugin(pluginName);
		if (plugin != null)
			return plugin.getVersion();
		return null;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.Configuration#getPluginDescription(java.lang.String)
	 */
	@Override
	public String getPluginDescription(String pluginName) {
		IPlugin plugin = this.loader.getPlugin(pluginName);
		if (plugin != null)
			return plugin.getDescription();
		return null;
	}

}
