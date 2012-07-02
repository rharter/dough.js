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
