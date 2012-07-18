namespace('bighub.server');

/*
 * Set the default values
 */
bighub.server.port = 8080;

bighub.server.start = function() {
    out.println("=> Starting Simple server on http://0.0.0.0:" +
                bighub.server.port);
    out.println("=> Ctrl-C to shutdown server");
                
    var simpleServer = JavaImporter(Packages.org.simpleframework.http.core.Container,
                                    Packages.org.simpleframework.transport.connect.Connection,
                                    Packages.org.simpleframework.transport.connect.SocketConnection,
                                    Packages.org.simpleframework.http.Response,
                                    Packages.org.simpleframework.http.Request,
                                    Packages.java.net.InetSocketAddress,
                                    Packages.java.net.SocketAddress);

    with (simpleServer) {
		var handler_container = bighub.server.handler.handler_container;
		handler_container.containers.push(bighub.server.handler.resource_handler);
		handler_container.containers.push(bighub.server);
		
        var container = new JavaAdapter(Container, handler_container);
        var connection = new SocketConnection(container);
        var address = new InetSocketAddress(bighub.server.port);

        connection.connect(address);
    }
};

bighub.server.handle = function(java_request, java_response) {
    var request = {};
    request.method = java_request.getMethod() + "";
    request.target = java_request.getTarget() + "";
    request.query = java_request.getQuery() + "";
    request.path = java_request.getPath() + "";
    request.client_address = {
        port: java_request.getClientAddress().getPort(),
        host: java_request.getClientAddress().getHostName(),
        address: java_request.getClientAddress().getAddress().getHostAddress()
    };

    out.println("Started " + request.method + " \"" + request.target + "\" for " + request.client_address.address + " at " + new Date());

    var handler = bighub.router.resolve(request.target, request.method);;
    
    if (handler === undefined) {
        out.println("\nNo route matches [" + request.method + "] \"" + request.target + "\"\n");
        java_response.close();
        return;
    }

    out.println("Processing with " + handler);

	bighub.global.response = java_response;
    var ret = handler();
    if (ret !== undefined) {
        var outputStream = null;
        try {
            var now = new Date();
            java_response.set("Content-Type", "text/html");
            java_response.set("Server", "BigHub-fs/1.0 (Simple 4.1)");
            java_response.setDate("Date", now.getTime());
            java_response.setDate("Last-Modified", now.getTime());

            outputStream = java_response.getPrintStream();
            outputStream.println(ret.toString());
        } catch (e) {
            out.println("Error: " + e.toString());
        } finally {
            if (outputStream !== null) {
                outputStream.close();
            }
        }
    }
};

/*
 * Handlers are function that return true if they handled the request,
 * otherwise they return false to pass the request downstream.
 */
namespace('bighub.server.handler.handler_container');
bighub.server.handler.handler_container.containers = [];
bighub.server.handler.handler_container.handle = function (java_request, java_response) {
	var cons = bighub.server.handler.handler_container.containers;
	var handled = false;
	for (var i = 0; i < cons.length && !handled; i++) {
		var con = cons[i];
		handled = con.handle(java_request, java_response);
	}
}

/**
 * Resource Handler
 */
namespace('bighub.server.handler.resource_handler');

bighub.server.handler.resource_handler.list_directories = false;


bighub.server.handler.resource_handler.handle = function (java_request, java_response) {
	var imports = JavaImporter(Packages.org.simpleframework.http.resource.FileContext);
	
	out.println("1");
	with (imports) {
		if (!bighub.server.handler.resource_handler.file_context) {
			out.println("2");
			var base = bighub.global.root + '/public';
			bighub.server.handler.resource_handler.file_context = new FileContext(new File(base));
		}
		
		out.println("3");
		var file_context = bighub.server.handler.resource_handler.file_context;
		var file = file_context.getFile(java_request.getTarget());
		
		out.println("4");
		if (file === null) {
			return false;
		}
		
		out.println("5");
		if (file.isDirectory()) {
			if (bighub.server.handler.resource_handler.list_directories) {
				
			} else {
				return false;
			}
		}
		
		java_response.setContentType(file_context.getContentType(java_request.getTarget()));
		
		var is = new FileInputStream(file);
		var output = java_response.getOutputStream();
		var next = is.read();
		while (next !== -1) {
			output.write(next);
			next = is.read();
		}
		output.close();
		
		return true;
	}
}
