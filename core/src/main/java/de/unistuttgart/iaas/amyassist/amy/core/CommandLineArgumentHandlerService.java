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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import asg.cliche.Command;
import de.unistuttgart.iaas.amyassist.amy.core.di.annotation.Service;

/**
 * This class handles the command line arguments passed to the core.
 *
 * @author Tim Neumann
 */
@Service
public class CommandLineArgumentHandlerService implements CommandLineArgumentHandler {

	private Map<Flag, List<String>> flags;

	private boolean flagsValid = true;

	/**
	 * Initializes the command line handler
	 *
	 * @param args
	 *            The command line arguments.
	 */
	public void init(String[] args) {
		this.flags = new EnumMap<>(Flag.class);

		FlagParameterInformation flagParaInfo = new FlagParameterInformation(null);
		for (String arg : args) {
			if (flagParaInfo.getRemainingParaCount() > 0) {
				processFlagParameter(flagParaInfo, arg);
				continue;
			}
			flagParaInfo = processNewFlag(arg);
			if (flagParaInfo == null)
				return;
		}

		if (flagParaInfo.getRemainingParaCount() > 0) {
			output("Missing parameter for last flag.");
			this.flagsValid = false;
			return;
		}

		output("This is Amy. Copyright (c) 2018 the Amy project authors. For help run with flag -h.");

	}

	/**
	 * Processes the argument strin as a new flag
	 * 
	 * @param arg
	 *            The string argument
	 * @return The new {@link FlagParameterInformation} object
	 */
	private FlagParameterInformation processNewFlag(String arg) {
		Flag f = Flag.getFlagFromString(arg);

		if (f == null) {
			output("Unknown command line flag: " + arg + ". Run with -h for help.");
			this.flagsValid = false;
			return null;
		}

		if (this.flags.containsKey(f) && !f.canRepeat()) {
			output("Duplicate command line flag: " + arg + ". Run with -h for help.");
			this.flagsValid = false;
			return null;
		}

		FlagParameterInformation info = new FlagParameterInformation(f);

		if (info.getRemainingParaCount() <= 0) {
			this.flags.put(f, null);
		}

		switch (f) {
		case HELP:
			printHelp();
			break;
		case VERSION:
			output(version());
			break;
		case NOTICE:
			output(notice());
			break;
		default:
			// All other flags don't do anything immediately.
			break;
		}

		return info;
	}

	/**
	 * Processes the argument string as a flag parameter to the Flag specified in flagParaInfo
	 * 
	 * @param flagParaInfo
	 *            The current {@link FlagParameterInformation} object
	 * @param arg
	 *            The string argument
	 */
	private void processFlagParameter(FlagParameterInformation flagParaInfo, String arg) {
		flagParaInfo.getParas().add(arg);
		flagParaInfo.decreaseRemainingParaCount();
		if (flagParaInfo.getRemainingParaCount() == 0) {
			if (this.flags.containsKey(flagParaInfo.getFlag())) {
				List<String> existingParas = this.flags.get(flagParaInfo.getFlag());
				existingParas.addAll(flagParaInfo.getParas());
			} else {
				this.flags.put(flagParaInfo.getFlag(), flagParaInfo.getParas());
			}
		}
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler#shouldProgramContinue()
	 */
	@Override
	public boolean shouldProgramContinue() {
		if (this.flags == null)
			throw new NotInitializedException();
		if (!this.flagsValid)
			return false;
		for (Flag f : this.flags.keySet()) {
			if (f.isStopExecution())
				return false;
		}
		return true;
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler#getConfigPaths()
	 */
	@Override
	public List<String> getConfigPaths() {
		if (this.flags == null)
			throw new NotInitializedException();
		return this.flags.get(Flag.CONFIG);
	}

	/**
	 * @see de.unistuttgart.iaas.amyassist.amy.core.CommandLineArgumentHandler#getPluginPaths()
	 */
	@Override
	public List<String> getPluginPaths() {
		if (this.flags == null)
			throw new NotInitializedException();
		return this.flags.get(Flag.PLUGIN);
	}

	private void printHelp() {
		output("This is Amy. A open source personal assitance system.");
		output("Developed by a group of students at University of Stuttgart.");
		output("This is a research project. No functionality is tested. There may be harmful errors.");
		output("");
		output("Copyright (c) 2018 the Amy project authors.");
		output("");
		output("For information about the license please start the program with --notice");
		output("");
		output("For further information about the project see: github.com/AmyAssist");
		output("");
		output("For help with the command line interface, after the program has started please enter '?'.");
		output("");
		output("Command line flags:");
		output("");
		for (Flag f : Flag.values()) {
			String s = "  ";
			if (!f.getShortVariant().isEmpty()) {
				s += f.getShortVariant();
			}
			if ((!f.getShortVariant().isEmpty()) && (!f.getLongVariant().isEmpty())) {
				s += " or ";
			}
			if (!f.getLongVariant().isEmpty()) {
				s += f.getLongVariant();
			}

			s += " : " + f.getDescription();

			output(s);
		}
	}

	/**
	 * The Version String
	 * 
	 * @return the version of amy core
	 */
	@Command(name = "version", description = "Prints out the version.")
	public String version() {
		return "Amy core version: " + getClass().getPackage().getImplementationVersion();
	}

	/**
	 * The Notice text
	 * 
	 * @return the notice of Amy project
	 */
	@Command(name = "notice", description = "Prints out the license notice.")
	public String notice() {
		return "Copyright (c) 2018 the Amy project authors.\n" + " \n" + "  SPDX-License-Identifier: Apache-2.0\n"
				+ " \n" + "  Licensed under the Apache License, Version 2.0 (the \"License\");\n"
				+ "  you may not use this file except in compliance with the License.\n"
				+ "  You may obtain a copy of the License at\n" + " \n"
				+ "    http://www.apache.org/licenses/LICENSE-2.0\n" + " \n"
				+ "  Unless required by applicable law or agreed to in writing, software\n"
				+ "  distributed under the License is distributed on an \"AS IS\" BASIS,\n"
				+ "  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.\n"
				+ "  See the License for the specific language governing permissions and\n"
				+ "  limitations under the License.\n" + " \n" + "  For more information see notice.md";
	}

	private void output(String s) {
		System.out.println(s);
	}

	/**
	 * A data structure to contain all information required while reading the parameters of a flag
	 * 
	 * @author Tim Neumann
	 */
	private class FlagParameterInformation {
		/**
		 * The flag that this information is for.
		 */
		private Flag flag;

		/**
		 * The count of parameters still expecting
		 */
		private int remainingParaCount;

		/**
		 * The parameters already found.
		 */
		private List<String> paras;

		/**
		 * Creates a new flag parameter information.
		 * 
		 * @param flag
		 *            The flag the parameters are for
		 */
		public FlagParameterInformation(Flag flag) {
			this.flag = flag;
			if (flag != null) {
				this.remainingParaCount = flag.getParameterCount();
			} else {
				this.remainingParaCount = 0;
			}
			this.paras = new ArrayList<>(this.remainingParaCount);
		}

		/**
		 * Get's {@link #flag flag}
		 * 
		 * @return flag
		 */
		public Flag getFlag() {
			return this.flag;
		}

		/**
		 * Decreases {@link #remainingParaCount paraCount}
		 * 
		 */
		public void decreaseRemainingParaCount() {
			this.remainingParaCount--;
		}

		/**
		 * Get's {@link #remainingParaCount paraCount}
		 * 
		 * @return paraCount
		 */
		public int getRemainingParaCount() {
			return this.remainingParaCount;
		}

		/**
		 * Get's {@link #paras paras}
		 * 
		 * @return paras
		 */
		public List<String> getParas() {
			return this.paras;
		}
	}

	private enum Flag {
		/**
		 * The help flag.
		 */
		HELP("-h", "--help", "Prints a help message", true, 0, false),

		/**
		 * The version flag
		 */
		VERSION("-v", "--version", "Prints out the version.", true, 0, false),

		/**
		 * The license notice flag
		 */
		NOTICE("", "--notice", "Prints out the license notice.", true, 0, false),

		/**
		 * The config dir flag
		 */
		CONFIG("-c", "--config <path>", "Add a alternate location for the config directory."
				+ "Can be used multiple times to add multiple config directorys.", false, 1, true),

		/**
		 * The plugin url flag
		 */
		PLUGIN("-p", "--plugin <path>",
				"Load the plugin found at the following path. "
						+ "Ignore the plugin conf. Can be used multiple times to load more then one plugin.",
				false, 1, true);

		private String shortVariant;
		private String longVariant;
		private String description;
		private int parameterCount;
		private boolean stopExecution;
		private boolean canRepeat;

		Flag(String pShortVariant, String pLongVariant, String pDescription, boolean pStopExecution, int parameterCount,
				boolean pCanRepeat) {
			this.shortVariant = pShortVariant;
			this.longVariant = pLongVariant;
			this.description = pDescription;
			this.parameterCount = parameterCount;
			this.stopExecution = pStopExecution;
			this.canRepeat = pCanRepeat;
		}

		/**
		 * Returns the flag, that corresponds to the given string
		 *
		 * @param s
		 *            The string to give the flag for
		 * @return The flag for that string or null if such a flag was not found.
		 */
		public static Flag getFlagFromString(String s) {
			for (Flag f : values()) {
				if (f.longVariant.equals(s) || f.shortVariant.equals(s))
					return f;
			}
			return null;
		}

		/**
		 * Get's {@link #shortVariant shortVariant}
		 *
		 * @return shortVariant
		 */
		public String getShortVariant() {
			return this.shortVariant;
		}

		/**
		 * Get's {@link #longVariant longVariant}
		 *
		 * @return longVariant
		 */
		public String getLongVariant() {
			return this.longVariant;
		}

		/**
		 * Get's {@link #description description}
		 *
		 * @return description
		 */
		public String getDescription() {
			return this.description;
		}

		/**
		 * Get's {@link #parameterCount hasParameter}
		 *
		 * @return hasParameter
		 */
		public int getParameterCount() {
			return this.parameterCount;
		}

		/**
		 * Get's {@link #stopExecution stopExecution}
		 *
		 * @return stopExecution
		 */
		public boolean isStopExecution() {
			return this.stopExecution;
		}

		/**
		 * Get's {@link #canRepeat canRepeat}
		 * 
		 * @return canRepeat
		 */
		public boolean canRepeat() {
			return this.canRepeat;
		}
	}
}
