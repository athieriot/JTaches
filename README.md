JTaches
=======

[![Build Status](https://secure.travis-ci.org/aurelien/JTaches.png)](http://travis-ci.org/aurelien/JTaches)

About
-----

JTaches provides a Java way to execute tasks on file events.
Mostly inspired by the [Guard]: https://github.com/guard/guard/ ruby gem.

Sources
-------

Until we find a better way to deploy it, you have to compile JTaches yourself.
Download the sources via git or directly: https://github.com/aurelien/JTaches/tarball/master

mvn clean install

./jtaches -h

Usage
-----

You have to provide a configuration file.
By default, the program look for .jtaches.yaml in the current directory but you can override it from the command line.

./jtaches -file whatever.yaml

Configuration
-------------

The configuration need to be in Yaml format and the content look like this:

        com.github.awesomeless.jtaches.taches.SysoutTache:
            - path: "."

A list of Taches classes (in classpath) containing a list of properties.
Only the "path" property is mandatory.

Taches
------

For now, there is only the *SysoutTache* which display event details in the console.

If you want to build one, you need to write a new class extending ConfiguredTache.
Then, you have access to four methods:

    + getConfiguration(), providing a Map loaded with the attribute in the Yaml file.
    + onCreate(), onDelete() and onModify(), providing callback for these named events.
