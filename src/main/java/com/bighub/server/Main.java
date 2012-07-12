package com.bighub.server;

import java.io.*;
import java.util.Properties;

import com.bighub.project.Project;

public class Main
{
	private final int port;
	private final String secret;
	private final Project project;

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
		this.secret = props.getProperty("secret", "eb27fb2e61ed603363461b3b4e37e0a0");

		String baseDir = props.getProperty("baseDir", ".");
		this.project = new Project(baseDir);
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
			this.project.startServer(this.port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void loadPlugins() {
	
	}
}
