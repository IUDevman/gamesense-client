package com.gamesense.api.util.misc;

import com.gamesense.client.GameSense;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * @author Hoosiers
 * @since 04/06/2021
 */

public class ReflectionUtil {

    public static ArrayList<Class<?>> findClassesInPath(String classPath) {
        final ArrayList<Class<?>> foundClasses = new ArrayList<>();

        try {
            //get the class loader from our main class
            ClassLoader classLoader = GameSense.class.getClassLoader();

            if (classLoader != null) {

                URL classPathURL = classLoader.getResource(classPath.replace(".", "/"));

                if (classPathURL != null) {

                    //location of the files inside the jar
                    File file = new File(classPathURL.getFile());

                    if (file.exists()) {
                        String[] classNamesFound = file.list();

                        if (classNamesFound != null) {

                            //we want to filter out other file types so we don't end up trying to load a .txt file
                            for (String classNames : classNamesFound) {

                                if (classNames.endsWith(".class")) {
                                    foundClasses.add(Class.forName(classPath + "." + classNames.replace(".class", "")));
                                    System.out.println(classNames);
                                }
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return foundClasses;
    }
}