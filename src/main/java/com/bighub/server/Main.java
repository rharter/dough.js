package com.bighub.server;

import java.io.*;
import java.util.Properties;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

import com.bighub.server.handlers.JavascriptHandler;


public class Main
{
    private final int port;
    private final String secret;
    private final String baseDir;

    public static void main(String... args) throws Exception
    {
	Properties props = new Properties();

	// Process the arguments
	for (int i = 0; i < args.length; i++) {
	    String arg = args[i];
	    if (arg.equals("-h") || arg.equals("--help")) {
		printUsage();
		System.exit(0);
	    }

	    if ("-p".equals(arg) || "--port".equals(arg)) {
		props.put("port", args[++i]);
	    } else if ("-b".equals(arg) || "--base".equals(arg)) {
		props.put("baseDir", args[++i]);
	    }
	}

	Main srv = new Main(props);
	srv.start();
    }

    public Main(Properties props) {
	this.port = Integer.parseInt(props.getProperty("port", "8080"));
	this.baseDir = props.getProperty("baseDir", ".");
	this.secret = props.getProperty("secret", "eb27fb2e61ed603363461b3b4e37e0a0");
    }

    private static void printUsage() {
	String usage = "\nUsage: java -jar <path/to/server.jar> [options]\n" +
	    "\n" +
	    "Options:\n" +
	    " -h, --help                      Prints this help message\n" +
	    " -p <arg>, --port <arg>          Sets the port to which the server\n" +
	    "                                 should bind [default: 8080]\n" + 
	    " -b <path>, --base <path>        Sets the application base directory\n" +
	    "                                 [default: .]\n";

	System.out.print(usage);
    }

    private void start() {
	try {
	    Server srv = new Server();
	    srv.setStopAtShutdown(true);

	    // Increase the thread pool
	    QueuedThreadPool threadPool = new QueuedThreadPool();
	    threadPool.setMaxThreads(100);
	    srv.setThreadPool(threadPool);

	    // Ensure using the non-blocking connector (NIO)
	    Connector connector = new SelectChannelConnector();
	    connector.setPort(port);
	    connector.setMaxIdleTime(30000);
	    srv.setConnectors(new Connector[]{connector});

	    // Add the handlers
	    HandlerList handlers = new HandlerList();
	    handlers.addHandler(new JavascriptHandler(new File(baseDir)));
	    srv.setHandler(handlers);

	    srv.start();
	    srv.join();
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
}