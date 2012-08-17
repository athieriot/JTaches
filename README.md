JTaches
=======

[![Build Status](https://secure.travis-ci.org/athieriot/JTaches.png)](http://travis-ci.org/athieriot/JTaches)

About
-----

JTaches provides a Java way to execute tasks on file events.
Mostly inspired by the [Guard]: https://github.com/guard/guard/ ruby gem.

Sources
-------

Until we find a better way to deploy it, you have to compile JTaches yourself.
Download the sources via git or directly: https://github.com/athieriot/JTaches/tarball/master

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

Taches
------

For now, you have access to three Taches:

    + com.github.awesomeless.jtaches.taches.SysoutTache : Display envent details in the console.
        - "path", the directory to watch

    + com.github.awesomeless.jtaches.taches.CopyTache : Copy watched files to another location.
        - "path", the directory to watch
        - "copyTo", where to copy the files

    + com.github.awesomeless.jtaches.taches.ScriptTache : Display envent details in the console.
        - "path", the directory to watch
        - "script", the script to execute (Patterns '<path>', '<file>' and '<event>' while be replaced by real values)
        - "workingDirectory", the working dir where to execute the script (optional)

Hack
----

If you want to build a Tache, you need to write a new class extending ConfiguredTache.
Then, you have access to four methods:

    + getConfiguration(), providing a Map loaded with the attributes in the Yaml file.
    + onCreate(), onDelete() and onModify() which correspond to event callbacks and have to be implemented. See WatchEvent.
    + additionally, ConfiguredTache can take a list of String as second argument to indicate mandatory configuration parameters

Known issues
------------

    + Watch is not recursive: https://github.com/athieriot/JTaches/issues/1
    + One instance of each Tache only: https://github.com/athieriot/JTaches/issues/2
    + Modify is fired after each creation: https://github.com/athieriot/JTaches/issues/4
