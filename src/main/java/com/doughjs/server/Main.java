package com.doughjs.server;

import java.io.*;
import java.util.Properties;

import com.doughjs.project.Project;

public class Main
{
	private final int port;
	private final Project project;

	private static Properties loadProperties(String projPath) {
		String envName = System.getenv("DOUGH_ENV");

		if (envName == null) {
			envName = "development";
		}

		File projBaseDir = new File(projPath);
		File configDir = new File(projBaseDir, "config/environments/" + envName);
		Properties props = new Properties();
		String configFilenames[] = configDir.list();

		if (configFilenames != null) {
			for (String filename : configFilenames) {
				if (filename.endsWith(".properties")) {
					try {
						FileReader fr = new FileReader(new File(configDir, filename));
						props.load(fr);
						fr.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}

		return props;
	}

	public static void main(String... args) throws Exception
	{
		String port = null;
		String baseDir = null;

		// Process the arguments
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if (arg.equals("-h") || arg.equals("--help")) {
				printUsage();
				System.exit(0);
			}

			if ("-p".equals(arg) || "--port".equals(arg)) {
				port = args[++i];
			} else if ("-b".equals(arg) || "--base".equals(arg)) {
				baseDir = args[++i];
			}
		}

		//always force this to either command line arg or this default
		if (baseDir == null) {
			baseDir = ".";
		}

		Properties props = loadProperties(baseDir);

		if (port != null) {
			props.setProperty("port", port);
		}
		
		props.setProperty("baseDir", baseDir);

		Main srv = new Main(props);
		srv.start();
	}

	public Main(Properties props) {
		this.port = Integer.parseInt(props.getProperty("port", "8080"));
		this.project = new Project(props);
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
