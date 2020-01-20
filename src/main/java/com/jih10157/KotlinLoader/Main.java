package com.jih10157.KotlinLoader;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Main extends JavaPlugin {
    private final static String KOTLIN_VERSION = "1.3.61";
    public Main() {
        loadKotlin();
    }
    public void loadKotlin() {
        getLogger().info("Loading kotlin library...");
        getLogger().info("kotlin library version: "+KOTLIN_VERSION);
        long l = System.currentTimeMillis();
        File libs = new File(getDataFolder(), "libs");
        if(!libs.exists()) libs.mkdirs();
        String[] strings = new String[] {
                "stdlib","stdlib-common","stdlib-jdk7","stdlib-jdk8","reflect"
        };
        for (String str : strings) {
            String fileName = "kotlin-"+str+"-"+KOTLIN_VERSION+".jar";
            File libFile = new File(libs, fileName);
            if(!libFile.exists()) {
                try(InputStream in = new URL("https://repo1.maven.org/maven2/org/jetbrains/kotlin/kotlin-"+str+"/"+KOTLIN_VERSION+"/"+fileName).openStream()) {
                    Files.copy(in, libFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            addClassPath(getJarUrl(libFile));
        }
        getLogger().info("Kotlin library loaded!");
        getLogger().info("Time taken: "+(System.currentTimeMillis()-l)+" milliseconds");
    }
    private static void addClassPath(URL url) {
        URLClassLoader sysloader = (URLClassLoader)ClassLoader.getSystemClassLoader();
        Class<URLClassLoader> sysclass = URLClassLoader.class;
        try {
            Method method = sysclass.getDeclaredMethod("addURL", URL.class);
            method.setAccessible(true);
            method.invoke(sysloader, url);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static URL getJarUrl(File file) {
        try {
            return new URL("jar:" + file.toURI().toURL().toExternalForm() + "!/");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

