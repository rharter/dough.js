namespace('bighub.server');

/*
 * Set the default values
 */
bighub.server.port = 8080;

bighub.server.start = function() {
    var jetty = JavaImporter(Packages.org.eclipse.jetty.embedded,
                             Packages.org.eclipse.jetty.server,
                             Packages.org.eclipse.jetty.server.handler,
                             Packages.org.eclipse.jetty.server.nio,
                             Packages.org.eclipse.jetty.util.thread);

    with (jetty) {
		var server = bighub.server.server = new Server();
		server.setStopAtShutdown(true);
		
		// Increase the thread pool
		var threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(100);
		server.setThreadPool(threadPool);
		
		// Ensure using the non-blocking connector
		var connector = new SelectChannelConnector();
		connector.setPort(bighub.server.port);
		connector.setMaxIdleTime(30000);
		server.setConnectors([connector]);
		
		// Add the handlers
		var handlers = new HandlerList();
		
		var publicHandler = new ResourceHandler();
		publicHandler.setResourceBase(bighub.global.root + '/public/');
		handlers.addHandler(publicHandler);
		
		handlers.addHandler(new JavaAdapter(AbstractHandler, bighub.server));
		
		server.setHandler(handlers);
		
		server.start();
		server.join();
    }
};

bighub.server.handle = function(java_target, java_base_request, java_request, java_response) {
    var request = {};
    request.method = java_request.getMethod() + "";
    request.target = java_target + "";
    request.query = java_request.getQueryString() + "";
    request.path = java_request.getPathInfo() + "";
    request.client_address = {
        port: java_request.getRemotePort(),
        host: java_request.getRemoteHost(),
        address: java_request.getRemoteAddr()
    };

	// Take care of the parameters
	request.params = {};
	var names = java_request.getParameterMap().keySet().toArray().slice();
	for each(var n in names) {
		var v = java_request.getParameterValues(n);
		if (v.length === 1) {
			request.params[n] = v[0] + "";
		} else {
			request.params[n] = v.slice().map(function (el) { return el + '' });
		}
	}

    out.println("Started " + request.method + " \"" + request.target + "\" for " + request.client_address.address + " at " + new Date());

    var route = bighub.router.resolve(request.target, request.method);;
    
    if (route === undefined) {
        out.println("\nNo route matches [" + request.method + "] \"" + request.target + "\"\n");
        return;
    }

    out.println("Processing with " + JSON.stringify(route));

	for (var p in route.params) {
		request.params[p] = route.params[p];
	}
	var ret = route.handler(request, java_response);
    if (ret !== undefined) {
        var outputStream = null;
        try {
            outputStream = java_response.getOutputStream();
            outputStream.print(ret.toString());
        } catch (e) {
            out.println("Error: " + e.toString());
        } finally {
            if (outputStream !== null) {
                outputStream.close();
            }
        }
    }
};
