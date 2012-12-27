# Dough.js

Dough.js is a web application framework that allows you to create full featured web applications using the `model-view-controller` paradigm.  Using Javascript on the JVM, Dough.js allows you to easily port existing Java libraries and drivers to the javascript environment in the form of plugins.

Using a plugin architecture allows you to choose which features of Dough.js you will use, including SQL and NoSQL backing stores, multiple rendering engines, and more.  If there isn't a plugin readily available, feel free to make your own.

# Getting Started

To get started, download the [project template](https://github.com/rharter/template-dough.js) to see a working app.

You can build the server application using the following command from the root of the project.

    mvn clean package

This will output a jar file in the `target` directory named `server-[version].jar`.

To execute the server and run a web application, use the following command.

    java -jar /path/to/server-[version].jar --base /path/to/project

To learn more about options available to the server, use the `--help` parameter.

    java -jar /path/to/server-[version].jar --help

# The Project

Until generation scripts are created, you are required to make your project structure by hand.

    - Root
      + app
        + controllers
        + views
      + config
      + lib
        + resources
        + vendor

## app/controllers

Your web application controllers live in the `app/controllers` directory.  Controllers are Javascript files that generally contain all of the request handler functions pertaining to a given section of your app.

## app/views

The standard templating language for Dough.js is [Mustache](http://mustache.github.com/).  Mustache template files live in subdirectories inside the `app/views` directory.  The names of the subdirectories are arbitrary, but should correlate to the controller names for organizational purposes.  For instance, all templates related to a `user.js` controller should live in `app/views/users/`.

## config

The `config` directory is not currently heavily used, but will be in the near future.  Currently, your `routes.js` file lives here.

## lib/resources

The `lib/resources` directory is where you'll put all of your custom Javascript helper files, along with any jar files that you want included in your project. Custom helper files should live directly in this folder, and any custom plugins (usually Javascript and jar files) will go in subdirectories named for the plugin.

## lib/vendor

This is where third party plugins reside.  Third party distributable plugins have a specific structure like so:

    - plugin_name
      + jars
      + resources

The root directory is named after the plugin is contains for ease of identification.  The `jars` directory contains any required jar files, and the `resources` directory is for the Javascript files that the plugin will access.

# License

Dough.js is released under the [MIT License](http://www.opensource.org/licenses/MIT).