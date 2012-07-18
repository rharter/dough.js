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

    // Read the configuration
    //bighub.config.init();

}

bighub.global.start_server = function(port) {
    if (bighub.server !== undefined) {
        bighub.server.port = port;
        bighub.server.start();
    }
}