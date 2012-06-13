package com.ryanharter.ssj.handlers;

import java.io.*;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

import org.mozilla.javascript.Script;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class JavascriptHandler extends AbstractHandler
{
    public File directory;

    public JavascriptHandler(File directory)
    {
	super();
	this.directory = directory;
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
	String scriptPath = directory.getAbsolutePath() + target.replace("/", File.separator);
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
	    JSONObject sys = new JSONObject();
	    sys.put("env", "development");
	    cx.evaluateString(scope, "var sys = " + sys.toJSONString() + ";", "sys", 1, null);

	    // Pass the request to the javascript environment
	    String req = "var request = " + convertRequestToJs(request) + ";";
	    cx.evaluateString(scope, req, "req", 1, null);

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
