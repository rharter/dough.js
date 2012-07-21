// Header comments need to be filled in here

// Define the root dough object
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
 * Stores plugin initialzers to be run after config.
 */

 dough.global.initializers = {};

/**
 * Creates an object structure for the provided namespace path. 
 *
 * For example, <code>namespace("dough.router")</code> will result in
 * an object namespace of <code>dough.router</code> so that you can
 * add objects and methods like <code>dough.router.register</code>.
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
 * Initializes a dough project environment.
 * 
 * @param {string} path The root path of the dough project.
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

/**
 * Adds a new initializer with optional dependencies.
 */
dough.global.registerInitializer = function(id, initializer, dependencies) {
    var initer = {};
    initer.initializer = initializer;
    initer.dependencies = dependencies;
    dough.global.initializers[id] = initer;
}

/**
 * Executes the initializers, ensuring that dependencies are executed first.
 */
dough.global.executeInitializers = function() {
    for (var id in dough.global.initializers) {
    	if (dough.global.initializers.hasOwnProperty(id)) {
    		var initer = dough.global.initializers[id];
    		delete dough.global.initializers[id];
        	dough.global.executeInitializer(initer);
    	}
    }
}

dough.global.executeInitializer = function(initer) {
	if (initer.dependencies) {
	    for (var i = 0; i < initer.dependencies.length; i++) {
	    	var did = initer.dependencies[i];
	    	var dIniter = dough.global.initializers[did];

	    	if (dIniter) {
	    		delete dough.global.initializers[did];
	    		dough.global.executeInitializer(dIniter);
	    	}
	    }
	}

    initer.initializer();
}
