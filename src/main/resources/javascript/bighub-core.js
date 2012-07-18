// Header comments need to be filled in here

// Define the root bighub object
var bighub = bighub || {};

/*
 * Keep a reference to the global context
 */
bighub.global = this;

/**
 * Stores the global system config, to be loaded from a file.
 */
var config = config || {};

/**
 * Creates an object structure for the provided namespace path. 
 *
 * For example, <code>namespace("bighub.router")</code> will result in
 * an object namespace of <code>bighub.router</code> so that you can
 * add objects and methods like <code>bighub.router.register</code>.
 */
bighub.global.namespace = function (path) {
    var parts = path.split('.');
    var cur = bighub.global;

    for (var part; parts.length && (part = parts.shift());) {
        if (cur[part]) {
            cur = cur[part];
        } else {
            cur = cur[part] = {};
        }
    }
};

/**
 * Initializes a bighub project environment.
 * 
 * @param {string} path The root path of the bighub project.
 */
bighub.global.init = function(root) {
    bighub.global.root = root;

}

bighub.global.start_server = function(port) {
    if (bighub.server !== undefined) {
		try {
        	bighub.server.port = port;
        	bighub.server.start();
		} catch (e) {
			out.println(e);
		}
    }
}

bighub.global.handle = function(java_target, java_base_request, java_request, java_response) {
    var request = {};
    request.method = java_request.getMethod() + "";
    request.path_info = java_request.getPathInfo() + "";
    request.uri = java_request.getRequestURI() + "";
    request.url = java_request.getRequestURL() + "";
    request.query_encoding = java_request.getQueryEncoding() + "";
    request.query_string = java_request.getQueryString() + "";
    request.scheme = java_request.getScheme() + "";
    request.server_name = java_request.getServerName() + "";
    request.server_port = java_request.getServerPort();
    request.remote_addr = java_request.getRemoteAddr() + "";
    request.remote_host = java_request.getRemoteHost() + "";
    request.remote_port = java_request.getRemotePort();

    bighub.global.log.debug('Started ' + request.method + ' \"' + request.uri + '\" for ' + request.remote_host + ' at ' + new Date());

    out.println("Started " + request.method + " \"" + request.uri + "\" for " + request.remote_host + " at " + new Date());

    var handler = bighub.router.resolve(request.uri);
    
    if (handler === undefined) {
        out.println("\nNo route matches [" + request.method + "] \"" + request.uri + "\"");
        return;
    }

    out.println("Processing with " + handler.constructor);
    for (var f in handler) {
        out.println("Processing with " + f);
    }

    var ret = handler();
    if (ret !== undefined) {
        var outputStream = null;
        try {
            outputStream = java_response.getOutputStream();
            outputStream.println(ret.toString());
        } finally {
            if (outputStream !== null) {
                outputStream.close();
            }
        }
    }
}

/**
 * Environment changes
 */
Array.prototype.each = function (f) {
    for (var i = 0; i < this.length; i++) {
        f(this[i], i);
    }
};
