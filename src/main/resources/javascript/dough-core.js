// Header comments need to be filled in here

// Define the root bighub object
var dough = dough || {};

/*
 * Keep a reference to the global context
 */
dough.global = this;

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
dough.global.namespace = function (path) {
    var parts = path.split('.');
    var cur = dough.global;

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
dough.global.init = function(root) {
    dough.global.root = root;
}

dough.global.start_server = function(port) {
    if (dough.server !== undefined) {
		try {
        	dough.server.port = port;
        	dough.server.start();
		} catch (e) {
			out.println(e);
		}
    }
}
