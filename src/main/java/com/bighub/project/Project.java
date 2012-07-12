package com.bighub.project;

import java.io.*;
import java.net.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

/**
 * Encapsulates a bighub project so that consumers don't need to know where the resources
 * come from or how to get them.
 */
public class Project {
    
	private static final String PUBLIC_PATH = File.separator + "public";
	private static final String APP_PATH = File.separator + "app";
	private static final String CONFIG_PATH = File.separator + "config";
	private static final String LIB_PATH = File.separator + "lib" + File.separator;
	private static final String PLUGIN_PATH = LIB_PATH + "plugins";
	private static final String RESOURCE_PATH = LIB_PATH + "resources";
    
	private File root;

	private Context context;
	private Scriptable scope;
    
	public Project(File file) {
		root = file;
		init();
	}

	/**
	 *
	 */
	public void startServer(int port) {
		if (context == null) {
			init();
		}

		try {
			Object bighub = scope.get("bighub", scope);
			Object global = scope.get("global", (Scriptable)bighub);
			Object startServer = scope.get("start_server", (Scriptable)global);
			if (!(startServer instanceof Function)) {
				System.err.println("Failed to start server: Couldn't find method: start_server");
				System.exit(1);
			} else {
				Object serverArgs[] = {
					port
				};
				Function f = (Function) startServer;
				f.call(context, scope, scope, serverArgs);
			}
		} catch (Exception e) {
			System.err.println("Exception occurred: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Closes a project by exiting the scripting context.
	 */
	public void close() {
		if (context != null) {
			context.exit();
		}
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

	public File getConfigDir() {
		return new File(getConfigPath());
	}

	public String getConfigPath() {
		return root.getAbsolutePath() + CONFIG_PATH;
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

	public ClassLoader getPluginClassLoader() {
		List<URL> plugins = getAllPluginUrls();
		return new URLClassLoader(plugins.toArray(new URL[] {}));
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

	private void init() {
		ContextFactory cf = ContextFactory.getGlobal();
		
		if (cf.getApplicationClassLoader() == null) {
			cf.initApplicationClassLoader(getPluginClassLoader());
		}

		context = cf.enterContext();
	
		try {
			ImporterTopLevel itl = new ImporterTopLevel(context);
			scope = itl;

			ScriptableObject.putProperty(scope, "out", System.out);
	    
			/*
			 * Evaluate the core
			 */
			InputStream coreStream = this.getClass().getResourceAsStream("/javascript/bighub-core.js");
			InputStreamReader coreReader = new InputStreamReader(coreStream);
			context.evaluateReader(scope, coreReader, "bighub-core.js", 1, null);

			/*
			 * Evaluate the server
			 */
			InputStream serverStream = this.getClass().getResourceAsStream("/javascript/bighub-server.js");
			InputStreamReader serverReader = new InputStreamReader(serverStream);
			context.evaluateReader(scope, serverReader, "bighub-server.js", 1, null);

			/*
			 * Evaluate the config
			 */
			InputStream configStream = this.getClass().getResourceAsStream("/javascript/bighub-config.js");
			InputStreamReader configReader = new InputStreamReader(configStream);
			context.evaluateReader(scope, configReader, "bighub-config.js", 1, null);

			/*
			 * Evaluate the router
			 */
			InputStream routerStream = this.getClass().getResourceAsStream("/javascript/bighub-router.js");
			InputStreamReader routerReader = new InputStreamReader(routerStream);
			context.evaluateReader(scope, routerReader, "bighub-router.js", 1, null);

			/*
			 * Evaluate the resources
			 */
			evaluate(context, scope, getResourceDir(), true);

			/*
			 * Evaluate the controllers
			 */
			evaluate(context, scope, getAppDir(), true);

			/*
			 * Evaluate the config files
			 */
			evaluate(context, scope, getConfigDir(), true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Evaluates javascript files in a directory
	 */
	private void evaluate(Context cx, Scriptable scope, File file, boolean recursive) {
		try {
			if (!file.isDirectory()) {
				FileReader reader = new FileReader(file);
				cx.evaluateReader(scope, reader, file.getName(), 1, null);
			} else {
				Iterator<File> files = FileUtils.iterateFiles(file, new String[]{"js"}, recursive);
				while (files.hasNext()) {
					File f = files.next();
					FileReader reader = new FileReader(f);
					cx.evaluateReader(scope, reader, f.getName(), 1, null);
				}
			}
		} catch (FileNotFoundException fnf) {
			fnf.printStackTrace();
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
