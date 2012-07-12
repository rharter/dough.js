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
        var container = new JavaAdapter(Container, bighub.server);
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

    java_response.close();
};
