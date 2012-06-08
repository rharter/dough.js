package com.ryanharter.ssj.handlers;

import java.io.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
	    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	    return;
	} else {
	    baseRequest.setHandled(true);
	}
	
	Context cx = Context.enter();
	
	try {
	    Scriptable scope = cx.initStandardObjects();
	    
	    // Set up the environment
	    String sys = "var sys = {" +
		"'env': 'development'" +
		"}";
	    cx.evaluateString(scope, sys, "sys", 1, null);

	    String req = "var request = {" +
		"'method': '" + request.getMethod() + "'" +
		"}";
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
}