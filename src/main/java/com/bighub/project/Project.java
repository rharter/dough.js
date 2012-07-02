package com.bighub.project;

import java.io.File;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

/**
 * Encapsulates a bighub project so that consumers don't need to know where the resources
 * come from or how to get them.
 */
public class Project {
    
    private static final String PUBLIC_PATH = File.separator + "public";
    private static final String APP_PATH = File.separator + "app";
    private static final String LIB_PATH = File.separator + "lib" + File.separator;
    private static final String PLUGIN_PATH = LIB_PATH + "plugins";
    private static final String RESOURCE_PATH = LIB_PATH + "resources";
    
    private File root;
    
    public Project(File file) {
	root = file;
    }

    public Project(String path) {
	root = new File(path);
    }
    
    public File getPublicDir() {
	return new File(getPublicPath());
    }

    public String getPublicPath() {
	return root.getAbsolutePath() + PUBLIC_PATH;
    }

    public File getAppDir() {
	return new File(getAppPath());
    }

    public String getAppPath() {
	return root.getAbsolutePath() + APP_PATH;
    }
    
    public File getPluginDir() {
	return new File(getPluginPath());
    }

    public String getPluginPath() {
	return root.getAbsolutePath() + PLUGIN_PATH;
    }

    public File getResourceDir() {
	return new File(getResourcePath());
    }

    public String getResourcePath() {
	return root.getAbsolutePath() + RESOURCE_PATH;
    }

    /**
     * Returns a list of URLs of plugin jars.
     */
    public List<URL> getAllPluginUrls() {
        List<URL> urls = new ArrayList<URL>();
        Collection<File> files = FileUtils.listFiles(getPluginDir(), new String[] { ".jar" }, true);

        for (File file : files) {
            try {
                urls.add(file.toURI().toURL());
            } catch (MalformedURLException e) {
                // Just don't include the file in the url list
            }
        }

        return urls;
    }
}
