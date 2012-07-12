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
import org.mozilla.javascript.ImporterTopLevel;
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

		project.handle(target, baseRequest, request, response);
	}
}
