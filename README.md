# File based BigHub Concept

This project is a proof of concept for a BigHub server entirely based on file system based projects (so no database necessary) and using an embedded server.

* Static Resources
  * Static resources live in the public directory of the project and will be served first.
  * File names are based on path relative to public directory (i.e. /public/index.html will be resolved for /index.html, etc)
* Javascript Resources
  * Javascript files will be executed and the results passed to the client allowing for dynamic content.
  * File names are based on path relative to app directory (i.e. /app/hello.js will be resolved for /hello.js, etc)
* Plugins
  * Plugins allow extensibility by including jar files in the javascript environment.
  * All jar files in the /lib/plugins directory will be loaded into the javascript scope and can be accessed in standard ways.
* Resources
  * Resources are javascript files that will not be exposed directly to clients.
  * They can be included in javascript resources.
  * They enable the use and sharing of libraries of generic, reusable code.

# Project Directory Structure

<pre>
- ROOT
  +- app
    +- Javascript files in the directory structure of your choosing (will be reflected in URLs)
  +- public
    +- Static files to be served directly to the client
  +- lib
    +- plugins
    +- resources
  +- config
    +- config.js
</pre>

# File Types

## Plugins

Plugins are jar files that will be loaded at boot time and can be utilized from the javascripts. You can include any standard jar files in here and access them from javascript as you would any other class.

### Connectors

Connectors are a special type of plugin that defines a connector to an external system. These can connect to things like databases, web service providers, or file shares. Often times connectors will use plugins to bridge the gap from the java world and will serve as a convenient, thin, javascript wrapper.

## Resources

Resources are javascript files that don't get served to web clients, but can be included in other scripts. They are used to implement libraries. They return an object that can be utilized in other scripts for various purposes.

Resources can be namespaced using sub-directories and will be accessed using the path to the file starting in the lib directory.  e.g. to load a mysql library: var mysql = load('mysql-lib/mysql', {'param':'value'});