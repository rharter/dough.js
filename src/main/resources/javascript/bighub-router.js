// Header comments to go here

namespace('bighub.router');

/** 
 * Hash containing the registered routes
 */
bighub.router.routes = bighub.router.routes || [];

/**
 * Registers a route to handle web requests.
 *
 * @param {string} pattern A regex pattern to identify the URL patterns that
 *     should be handled by handler.
 * @param {function} handler A JavaScript function to handle matched routes.
 * @param {[string]} methods An array of method strings to matches, or 
 *     null to match all methods.
 */
bighub.router.register = function (pattern, handler, methods) {
    var route = {};
    route.pattern = new RegExp(pattern);
    route.methods = methods;
    route.handler = handler;
    bighub.router.routes.push(route);
}

/** 
 * Resolves a route based on a request URL.
 *
 * If route.methods is null, then any matching path will be returned.
 */
bighub.router.resolve = function (path, method) {

    var routes = bighub.router.routes;
    for (var i = 0; i < routes.length; i++) {
        var route = routes[i];
        if ((new RegExp(route.pattern)).test(path)) {
            if (route.methods === undefined || route.methods.indexOf(method) > -1) {
                return route.handler;
            }
        }
    }
};
