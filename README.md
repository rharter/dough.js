# Server Side Javascript Book Server #

* Server
* JavascriptHandler
  * Find resource at path
  * Execute javascript
  * Return result

# Project Directory Structure #

<pre>
- ROOT
  +- app
    +- Javascript files in the directory structure of your choosing (will be reflected in URLs)
  +- public
    +- Static files to be served directly to the client
  +- lib
    +- plugins
    +- resources
</pre>

# File Types

## Plugins

Plugins are jar files that will be loaded at boot time and can be utilized from the javascripts. You can include any standard jar files in here and access them from javascript as you would any other class.

### Connectors

Connectors are a special type of plugin that defines a connector to an external system. These can connect to things like databases, web service providers, or file shares. Often times connectors will use plugins to bridge the gap from the java world and will serve as a convenient, thin, javascript wrapper.

## Resources

Resources are javascript files that don't get served to web clients, but can be included in other scripts. They are used to implement libraries. They return an object that can be utilized in other scripts for various purposes.

Resources can be namespaced using sub-directories and will be accessed using the path to the file starting in the lib directory.  e.g. to load a mysql library: var mysql = load('mysql-lib/mysql', {'param':'value'});