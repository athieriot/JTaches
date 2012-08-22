JTaches
=======

[![Build Status](https://secure.travis-ci.org/athieriot/JTaches.png)](http://travis-ci.org/athieriot/JTaches)

About
-----

JTaches provides a Java way to execute tasks on file events.
Mostly inspired by the [Guard](https://github.com/guard/guard/) ruby gem.

Sources
-------

Until we find a better way to deploy it, you have to compile JTaches yourself.
Download the sources via git or directly: https://github.com/athieriot/JTaches/tarball/master

        mvn clean install

        ./jtaches -h

Usage
-----

You have to provide a configuration file.
By default, the program search for **.jtaches.yaml** in the current directory but you can override it from the command line.

        ./jtaches -file whatever.yaml

Configuration
-------------

The configuration need to be in Yaml format and the content look like this:

        - !!com.github.athieriot.jtaches.taches.ScriptTache [{
            path: ".",
            script: "notify-send <path>/<file>/<event>",
        }]

A list of Taches classes (in classpath) containing a list of properties.
The syntax is borrow to [SnakeYaml](http://code.google.com/p/snakeyaml/wiki/Documentation) as it is the parser used internally.

Taches
------

For now, you have access to three Taches:

+ **com.github.athieriot.jtaches.taches.SysoutTache** : Display envent details in the console.
    - "path", the directory to watch

+ **com.github.athieriot.jtaches.taches.CopyTache** : Copy watched files to another location.
    - "path", the directory to watch
    - "copyTo", where to copy the files

+ **com.github.athieriot.jtaches.taches.ScriptTache** : Display envent details in the console.
    - "path", the directory to watch
    - "script", the script to execute (Patterns '<path>', '<file>' and '<event>' while be replaced by real values)
    - "workingDirectory", the working dir where to execute the script (optional)

Hack
----

If you want to build a Tache, you need to write a new class implementing the Tache interface.
ConfiguredTache is just an abstract class made to ease the work.

Then, you have access to five things:

+ getPath() that you need to implement to provide the path to watch.
+ onCreate(), onDelete() and onModify() which are events callbacks and have to be implemented. See [WatchEvent](http://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchEvent.html).
+ Log is a static logging object from which you have access to info(), debug(), etc... See [Minlog](http://code.google.com/p/minlog/). The default log level in INFO and DEBUG in verbose mode.

Additionally if you use ConfiguredTache:

+ getConfiguration() provide a Map, loaded with the attributes in the Yaml file.
+ ConfiguredTache constructor can take a list of String as second argument to indicate mandatory configuration parameters

One thing you have to know with the configuration file is that the Taches are instantiated by SnakeYaml directly.
So, if you respect the interface and the SnakeYaml documentation, you can override the default constructors to do whatever you want.

Known issues
------------

+ Watch is not recursive: [https://github.com/athieriot/JTaches/issues/1](https://github.com/athieriot/JTaches/issues/1)
+ Modify is fired after each creation: [https://github.com/athieriot/JTaches/issues/4](https://github.com/athieriot/JTaches/issues/4)
