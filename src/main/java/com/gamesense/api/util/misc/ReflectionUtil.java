package com.gamesense.api.util.misc;

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
	private static List<String> classes;
	
    public static ArrayList<Class<?>> findClassesInPath(String classPath) {
    	if (classes==null) {
    		classes=new ArrayList<String>();
    		String resource=ReflectionUtil.class.getResource(ReflectionUtil.class.getSimpleName()+".class").getPath();
    		try {
            	ZipInputStream file=new ZipInputStream(new URL(resource.substring(0,resource.lastIndexOf('!'))).openStream());
            	ZipEntry entry;
    			while ((entry=file.getNextEntry())!=null) {
    				String name=entry.getName();
    				if (name.endsWith(".class")) {
    					classes.add(name.substring(0,name.length()-6).replace("/","."));
    				}
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	GameSense.LOGGER.info("Loading classes from "+classPath+" ...");
        final ArrayList<Class<?>> foundClasses = new ArrayList<>();
        for (String className: classes) {
        	if (className.startsWith(classPath+".")) {
        		try {
        			foundClasses.add(Class.forName(className));
        			System.out.println("Loaded "+className+"!");
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
        	}
        }
        return foundClasses;
    }
}