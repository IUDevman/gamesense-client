package com.gamesense.api.util.misc;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
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
    public static ArrayList<Class<?>> findClassesInPath(String classPath) {
    	GameSense.LOGGER.info("Loading classes from "+classPath+" ...");
        final ArrayList<Class<?>> foundClasses = new ArrayList<>();
        String resource=ReflectionUtil.class.getClassLoader().getResource(classPath.replace(".","/")).getPath();
        try {
        	ZipInputStream file=new ZipInputStream(new URL(resource.substring(0,resource.lastIndexOf('!'))).openStream());
        	ZipEntry entry;
			while ((entry=file.getNextEntry())!=null) {
				String name=entry.getName();
				if (name.startsWith(classPath.replace(".","/")+"/") && name.endsWith(".class")) {
					try {
						Class<?> clazz=Class.forName(name.substring(0,name.length()-6).replace("/","."));
						foundClasses.add(clazz);
						System.out.println("Loaded "+clazz.getName()+"!");
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
        return foundClasses;
    }
}