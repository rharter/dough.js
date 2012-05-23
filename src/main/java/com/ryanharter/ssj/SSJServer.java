package com.ryanharter.ssj;

import java.io.*;

import org.eclipse.jetty.server.Server;

import com.ryanharter.ssj.handlers.JavascriptHandler;
import com.ryanharter.ssj.ui.*;

public class SSJServer
{
    public static void main(String[] args) throws Exception
    {
	File home = new FileChooser().choose();

	Server server = new Server(8080);
	server.setHandler(new JavascriptHandler(home));

	server.start();
	server.join();
    }
}