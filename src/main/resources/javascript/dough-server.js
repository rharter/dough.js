namespace('dough.server');

/*
 * Set the default values
 */
dough.server.port = 8080;
dough.server.sessionHandler = null;

dough.server.start = function() {
    var jetty = JavaImporter(Packages.org.eclipse.jetty.embedded,
                             Packages.org.eclipse.jetty.server,
                             Packages.org.eclipse.jetty.server.handler,
                             Packages.org.eclipse.jetty.server.nio,
							 Packages.org.eclipse.jetty.server.session,
                             Packages.org.eclipse.jetty.util.thread);

    with (jetty) {
		var server = dough.server.server = new Server();
		server.setStopAtShutdown(true);
		
		// Increase the thread pool
		var threadPool = new QueuedThreadPool();
		threadPool.setMaxThreads(100);
		server.setThreadPool(threadPool);
		
		// Ensure using the non-blocking connector
		var connector = new SelectChannelConnector();
		connector.setPort(dough.server.port);
		connector.setMaxIdleTime(30000);
		server.setConnectors([connector]);
		
		// Add the handlers
		var handlers = new HandlerList();
		
		handlers.addHandler(new SessionHandler());
		
		var publicHandler = new ResourceHandler();
		publicHandler.setResourceBase(dough.global.root + '/public/');
		handlers.addHandler(publicHandler);
		
		handlers.addHandler(new JavaAdapter(AbstractHandler, dough.server));
		
		server.setHandler(handlers);
		
		server.start();
		server.join();
    }
};

dough.server.handle = function(java_target, java_base_request, java_request, java_response) {
    var request = new dough.server._Request(java_request);
	request.target = java_target + '';

	if (dough.server.sessionHandler) {
		request.session = dough.server.sessionHandler
				.getSession(request._request.getSession(true).getId());
	}

    out.println("Started " + request.method + " \"" + request.target + "\" for " + request.client_address.address + " at " + new Date() + "\n");

    var route = dough.router.resolve(request.target, request.method);;
    
    if (route === undefined) {
        out.println("\nNo route matches [" + request.method + "] \"" + request.target + "\"\n");
        return;
    }
	
	// If this is a protected route and there isn't a user, redirect
	if (route.auth_redirect && request.session && !request.session.val('user_id')) {
		java_response.sendRedirect(route.auth_redirect + '?forward_url=' + request.target);
	}

    out.println("Processing with " + JSON.stringify(route.pattern));

	request = route.evaluateParams(request);
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
}

dough.server._Request = function (request) {
	this._request = request;

	this.method = request.getMethod() + "";
	this.query = request.getQueryString() + "";
	this.path = request.getPathInfo() + "";
	this.client_address = {
	    port: request.getRemotePort(),
	    host: request.getRemoteHost(),
	    address: request.getRemoteAddr()
	};
	
	this.params = {};
	var names = request.getParameterMap().keySet().toArray().slice();
	for each(var n in names) {
		var v = request.getParameterValues(n);
		if (v.length === 1) {
			this.params[n] = v[0] + "";
		} else {
			this.params[n] = v.slice().map(function (el) { return el + '' });
		}
	}
}