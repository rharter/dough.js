package com.ryanharter.ssj.handlers;

import java.io.*;

import javax.script.Invocable;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

public class JavascriptHandler extends AbstractHandler
{
    public File directory;

    public JavascriptHandler(File directory) {
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
		String scriptPath = directory.getAbsolutePath() +

		    target.replace("/", File.separator);
		System.out.println("looking for script: " + scriptPath);

		File javascript = new File(scriptPath);

		if (!javascript.exists()) {
			System.out.println("Javascript file doesn't exist at path: " + scriptPath);
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			return;
		}

		Context cx = Context.enter();

		try {
		    Scriptable scope = cx.initStandardObjects();

		    FileInputStream fis = new FileInputStream(javascript);
		    InputStreamReader reader = new InputStreamReader(fis);

		    Object result = cx.evaluateReader(scope, reader, target, 1, null);
 
		    response.getWriter().println(result);
		} catch (Exception e) {
		    e.printStackTrace();
		} finally {
		    Context.exit();
		}
    }
}