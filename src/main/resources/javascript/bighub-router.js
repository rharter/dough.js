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
    route.pattern = pattern;
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
    var path_tokens = get_tokens(path);

    var routes = bighub.router.routes;
    for (var i = 0; i < routes.length; i++) {
        var route = routes[i];

        // ensure the route handles this method
        if (route.methods === undefined || route.methods.indexOf(method) > -1) {
            var route_tokens = get_tokens(route.pattern);

            var match = true;
            var params = {};
            for (var j = path_mark = 0; j <= route_tokens.length; j++, path_mark++) {
                var item = route_tokens[j];

                // Fell off route but not path, no match
                if (item === undefined && path_tokens[path_mark] !== undefined) {
                    params = {};
                    match = false;
                    break;
                }

                // Got a variable in the route
                if (item === ':' && path_tokens[j] !== undefined) {
                    var name = route_tokens[++j];
                    params[name] = path_tokens[path_mark];
                    continue;
                }
                
                // Route token doesn't match path token, no match
                if (item !== path_tokens[path_mark]) {
                    params = {};
                    match = false;
                    break;
                }
            }

            if (match) {
				return {handler: route.handler, params: params};
            }
        }
    }
};

function get_tokens(str) {
    var tokens = [];
    var delims = ['/',':'];
    
    var cur = '';
    for (var i = 0; i < str.length; i++) {
        var ch = str[i];
        
        // check if we hit a delimiter
        if (delims.indexOf(ch) > -1) {
            if (cur.length > 0) tokens.push(cur);
            cur = '';
            tokens.push(ch);
            continue;
        } 

        // Add the character to cur
        cur += ch;

        // check if cur is a multi-char delimiter
        if (delims.indexOf(cur) > -1) {
            tokens.push(cur);
            cur = '';
            continue;
        }
    }

    if (cur.length > 0) tokens.push(cur);

    return tokens;
}

