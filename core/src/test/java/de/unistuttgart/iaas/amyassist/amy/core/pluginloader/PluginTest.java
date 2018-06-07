/*
 * Amy Assist
 *
 * Personal Assistance System
 *
 * @author Tim Neumann, Leon Kiefer, Benno Krauss, Christian Braeuner, Felix Burk, Florian Bauer, Kai Menzel, Lars Buttgereit, Muhammed Kaya, Patrick Gebhardt, Patrick Singer, Tobias Siemonsen
 *
 */
package de.unistuttgart.iaas.amyassist.amy.core.pluginloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests the Plugin class
 * 
 * @author Tim Neumann
 */
class PluginTest {

	private String manifest = "Manifest-Version: 1.0\n" + "Archiver-Version: Plexus Archiver\n"
			+ "Created-By: 9-internal+0-2016-04-14-195246.buildd.src (Oracle Corporat\n" + " ion)\n"
			+ "Built-By: ${user.name}\n" + "Build-Jdk: ${java.version}\n" + "Specification-Title: ${project.name}\n"
			+ "Specification-Version: ${project.artifact.selectedVersion.majorVersion}.${project.artifact.selectedVersion.minorVersion}\n"
			+ "Specification-Vendor: ${project.organization.name}\n" + "Implementation-Title: ${project.name}\n"
			+ "Implementation-Version: ${project.version}\n" + "Implementation-Vendor-Id: ${project.groupId}\n"
			+ "Implementation-Vendor: ${project.organization.name}\n" + "Implementation-URL: ${project.url}";

	private Manifest generateMaifest(String javaVersion, String projectName, String majorVersion, String minorVersion,
			String orgName, String projectVersion, String groupId, String projectURL) {
		String mf = this.manifest.replaceAll(Pattern.quote("${java.version}"), javaVersion);
		mf = mf.replaceAll(Pattern.quote("${project.name}"), projectName);
		mf = mf.replaceAll(Pattern.quote("${project.artifact.selectedVersion.majorVersion}"), majorVersion);
		mf = mf.replaceAll(Pattern.quote("${project.artifact.selectedVersion.minorVersion}"), minorVersion);
		mf = mf.replaceAll(Pattern.quote("${project.organization.name}"), orgName);
		mf = mf.replaceAll(Pattern.quote("${project.version}"), projectVersion);
		mf = mf.replaceAll(Pattern.quote("${project.groupId}"), groupId);
		mf = mf.replaceAll(Pattern.quote("${project.url}"), projectURL);
		return this.getManifestFromString(mf);
	}

	private Manifest getManifestFromString(String s) {
		try (InputStream is = new StringInputStream(s)) {
			return new Manifest(is);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	private class StringInputStream extends InputStream {
		private String string;

		private int pos;

		/**
		 * Creates a new String input stream from the given string
		 * 
		 * @param s
		 *            the string
		 */
		public StringInputStream(String s) {
			this.string = s;
			this.pos = 0;
		}

		/**
		 * @see java.io.InputStream#read()
		 */
		@Override
		public int read() throws IOException {
			if (this.pos >= this.string.length())
				return -1;
			return this.string.charAt(this.pos++);
		}

	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setFile(File)}
	 * and
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getFile()}.
	 */
	@Test
	void testFile() {
		ArrayList<File> files = new ArrayList<>();
		files.add(new File("/"));
		files.add(new File("/test"));
		files.add(new File("C://test"));
		files.add(new File("test"));

		Plugin p = new Plugin();

		for (File f : files) {
			p.setFile(f);
			Assertions.assertEquals(f, p.getFile(), "Wrong file: " + f.getName());
		}

	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setClassLoader(ClassLoader)}
	 * and
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getClassLoader()}.
	 */
	@Test
	void testClassLoader() {
		Plugin p = new Plugin();

		p.setClassLoader(ClassLoader.getSystemClassLoader());
		Assertions.assertEquals(ClassLoader.getSystemClassLoader(), p.getClassLoader(), "Wrong class loader");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getUniqueName()}.
	 */
	@Test
	void testUniqueName() {
		Plugin p = new Plugin();
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
			String projectName = "Name" + r.nextInt();
			Manifest mf = this.generateMaifest("a", projectName, "b", "c", "d", "e", "f", "g");
			p.setManifest(mf);
			Assertions.assertEquals(projectName, p.getUniqueName(), "WrongName: " + projectName);
		}
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getVersion()}.
	 */
	@Test
	void testVersion() {
		Plugin p = new Plugin();
		Random r = new Random();
		for (int i = 0; i < 100; i++) {
			String projectVersion = "Version" + r.nextInt();
			Manifest mf = this.generateMaifest("a", "b", "c", "d", "e", projectVersion, "f", "g");
			p.setManifest(mf);
			Assertions.assertEquals(projectVersion, p.getVersion(), "WrongVersion: " + projectVersion);
		}
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#setClasses(java.util.ArrayList)}
	 * and
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getClasses()}.
	 */
	@Test
	void testClasses() {
		ArrayList<Class<?>> list = new ArrayList<>();
		Plugin p = new Plugin();
		list.add(this.getClass());
		list.add(Plugin.class);
		p.setClasses(list);
		Assertions.assertEquals(list, p.getClasses(), "Wrong list of classes");
	}

	/**
	 * Test method for
	 * {@link de.unistuttgart.iaas.amyassist.amy.core.pluginloader.Plugin#getManifest()}.
	 */
	@Test
	void testManifest() {
		Manifest mf = this.generateMaifest("a", "b", "c", "d", "e", "f", "g", "h");
		Plugin p = new Plugin();
		p.setManifest(mf);
		Assertions.assertEquals(mf, p.getManifest(), "Wrong manifest");
	}

}