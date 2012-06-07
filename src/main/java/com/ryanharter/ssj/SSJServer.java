package com.ryanharter.ssj;

import java.io.*;

import org.eclipse.jetty.server.Server;

import com.ryanharter.ssj.handlers.JavascriptHandler;
import com.ryanharter.ssj.ui.*;

public class SSJServer
{
    public final static String HOME_KEY = "BH_HOME";

    public static void main(String[] args) throws Exception
    {
	String homePath = System.getenv(HOME_KEY);
	if (homePath == null) {
            System.out.println("No home path found, set " + HOME_KEY + " environment variable to script directory.");
            System.exit(1);
        }
	
	File home = new File(homePath);
	Server server = new Server(8080);
	server.setHandler(new JavascriptHandler(home));

	server.start();
	server.join();
    }
}