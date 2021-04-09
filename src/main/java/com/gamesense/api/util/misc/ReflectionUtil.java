package com.gamesense.api.util.misc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.gamesense.client.GameSense;

/**
 * Why not?
 * @author Hoosiers
 * @since 04/06/2021
 * @author lukflug
 */

public class ReflectionUtil {
  
	private static final boolean debug = false;

	public static ArrayList<Class<?>> findClassesInPath(String classPath) {
		if (debug) GameSense.LOGGER.info("Loading classes from " + classPath + " ...");

		final ArrayList<Class<?>> foundClasses = new ArrayList<>();
		String resource = ReflectionUtil.class.getClassLoader().getResource(classPath.replace(".", "/")).getPath();

		if (resource.contains("!")) {

			try {
				ZipInputStream file = new ZipInputStream(new URL(resource.substring(0, resource.lastIndexOf('!'))).openStream());

				ZipEntry entry;
				while ((entry = file.getNextEntry()) != null) {
					String name = entry.getName();

					if (name.startsWith(classPath.replace(".", "/") + "/") && name.endsWith(".class")) {

						try {
							Class<?> clazz = Class.forName(name.substring(0, name.length() - 6).replace("/", "."));
							foundClasses.add(clazz);
							if (debug) GameSense.LOGGER.info("Loaded " + clazz.getName() + "!");
						} catch (ClassNotFoundException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			try {

				URL classPathURL = ReflectionUtil.class.getClassLoader().getResource(classPath.replace(".", "/"));

				if (classPathURL != null) {

					File file = new File(classPathURL.getFile());

					if (file.exists()) {
						String[] classNamesFound = file.list();

						if (classNamesFound != null) {

							for (String className : classNamesFound) {

								if (className.endsWith(".class")) {
									foundClasses.add(Class.forName(classPath + "." + className.replace(".class", "")));
									if (debug) GameSense.LOGGER.info("Loaded " + className + "!");
								}
							}
						}
					}
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}

		return foundClasses;
	}
}