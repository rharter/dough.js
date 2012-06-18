package com.bighub.server.handlers;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.mozilla.javascript.Script;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.commonjs.module.ModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.Require;
import org.mozilla.javascript.commonjs.module.RequireBuilder;
import org.mozilla.javascript.commonjs.module.provider.ModuleSourceProvider;
import org.mozilla.javascript.commonjs.module.provider.SoftCachingModuleScriptProvider;
import org.mozilla.javascript.commonjs.module.provider.UrlModuleSourceProvider;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.bighub.project.Project;

public class JavascriptHandler extends AbstractHandler
{
    private Project project;
    
    public JavascriptHandler(Project project)
    {
	super();
	this.project = project;
    }

    public void handle(String target, Request baseRequest,
		       HttpServletRequest request,
		       HttpServletResponse response)
	throws IOException, ServletException
    {
    	// Stupid code to ignore the favicon request
    	if (target.equals("/favicon.ico")) {
    		return;
    	}

	// Build the path to the target script
	String scriptPath = project.getAppPath() + target.replace("/", File.separator);
	System.out.println("looking for script: " + scriptPath);
	
	File javascript = new File(scriptPath);
	
	if (!javascript.exists()) {
	    System.out.println("Javascript file doesn't exist at path: " + scriptPath);
	    return;
	} else {
	    baseRequest.setHandled(true);
	}
	
	Context cx = Context.enter();
	
	try {
	    Scriptable scope = cx.initStandardObjects();
	    
	    // Set up the environment
	    String env = "development";
	    ScriptableObject.putProperty(scope, "env", env);

	    // Pass the request to the javascript environment
	    String req = "var request = " + convertRequestToJs(request) + ";";
	    cx.evaluateString(scope, req, "req", 1, null);

	    // Set up require
	    File resourceDir = project.getResourceDir();
	    Iterator<File> resources = FileUtils.iterateFiles(resourceDir, new String[]{ "js" }, true);
	    List<URI> resourceUris = new ArrayList<URI>();
	    while (resources.hasNext()) {
		resourceUris.add(resources.next().toURI());
	    }
	    ModuleSourceProvider sourceProvider = new UrlModuleSourceProvider(resourceUris, null);
	    ModuleScriptProvider scriptProvider = new SoftCachingModuleScriptProvider(sourceProvider);

	    RequireBuilder builder = new RequireBuilder();
	    builder.setSandboxed(true);
	    builder.setModuleScriptProvider(scriptProvider);

	    Require require = builder.createRequire(cx, scope);
	    require.install(scope);
	    
	    ScriptableObject.putProperty(scope, "project", project);
	    
	    FileInputStream fis = new FileInputStream(javascript);
	    InputStreamReader reader = new InputStreamReader(fis);

	    Script script = cx.compileReader(scope, reader, javascript.getName(), 1, null);
	    
	    Object result = script.exec(cx, scope);

	    response.getWriter().println(result);
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    Context.exit();
	}
    }

    /**
     * Builds a javascript object out of the servlet request.
     */
    private String convertRequestToJs(HttpServletRequest request) {
	JSONObject obj = new JSONObject();

	obj.put("params", convertParamsToJs(request.getParameterMap()));
	obj.put("encoding", request.getCharacterEncoding());
	obj.put("contentLength", request.getContentLength());
	obj.put("contentType", request.getContentType());
	obj.put("method", request.getMethod());

	return obj.toJSONString();
    }

    /**
     * Converts HTTP parameters to a javascript representation.
     * <br/>
     * <code>
     * {"param1":["value1","value2"],"param2":"value2","param3":"value3"}
     * </code>
     */
    private JSONObject convertParamsToJs(Map<String, String[]> params) {
	JSONObject obj = new JSONObject();
	
	for (String key : params.keySet()) {
	    String[] val = params.get(key);

	    if (val.length > 1) {
		JSONArray arr = new JSONArray();
		for (String v : val) {
		    arr.add(v);
		}
		obj.put(key, arr);
	    } else if (val.length == 1) {
		obj.put(key, val[0]);
	    } else {
		obj.put(key, null);
	    }
	}

	return obj;
    }
}
