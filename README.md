JTaches
=======

[![Build Status](https://secure.travis-ci.org/athieriot/JTaches.png)](http://travis-ci.org/athieriot/JTaches)

![](http://dl.dropbox.com/u/4955384/jtaches.png)

About
-----

JTaches provides a Java way to execute tasks on file events.
Mostly inspired by the [Guard](https://github.com/guard/guard/) ruby gem.

Binaries
--------

Thanks to this Protip: [http://coderwall.com/p/ssuaxa](http://coderwall.com/p/ssuaxa), you can now download a Linux executable and just use it (you still need Java):

        wget https://github.com/downloads/athieriot/JTaches/jtaches-1.0.1
        chmod +x jtaches-1.0.1
        ./jtaches-1.0.1 -h

For all other cases, you can find a JAR pre-compiled with all needed dependencies on the [Sonatype](http://search.maven.org/#search%7Cga%7C1%7Cjtaches) repository:
[jtaches-1.0.1-jar-with-dependencies.jar](http://search.maven.org/remotecontent?filepath=com/github/athieriot/jtaches/1.0.1/jtaches-1.0.1-jar-with-dependencies.jar)

Then you just need to execute:

        java -jar jtaches-1.0.1-jar-with-dependencies.jar -h

Example of shell launcher: [https://github.com/athieriot/JTaches/blob/master/jtaches](https://github.com/athieriot/JTaches/blob/master/jtaches)

Sources
-------

If you absolutely want the latest version, you will have to compile JTaches yourself.
Download the sources via git or directly: https://github.com/athieriot/JTaches/tarball/master

        mvn clean install

        ./jtaches -h
        
Usage
-----

        ./jtaches

And that's it.

But you have to provide a configuration file first.
By default, the program search for **.jtaches.yaml** in the current directory but you can override it from the command line.

        ./jtaches -file whatever.yaml

Configuration
-------------

The configuration need to be in Yaml format and the content look like this:

        - !!com.github.athieriot.jtaches.taches.ScriptTache [{
            path: ".",
            script: "notify-send <path>/<filename>/<event>",
        }]

A list of Taches classes (in classpath) containing a list of properties.
The syntax is borrow to [SnakeYaml](http://code.google.com/p/snakeyaml/wiki/Documentation) as it is the parser used internally.

Full example here: [https://github.com/athieriot/JTaches/blob/master/.jtaches.yaml](https://github.com/athieriot/JTaches/blob/master/.jtaches.yaml)

Taches
------

For now, you have access to three Taches:

+ **com.github.athieriot.jtaches.taches.SysoutTache** : Display envent details in the console.
    - "path", the directory to watch
    - "excludes", list of exclusion patterns separated by a ';' (optional)

+ **com.github.athieriot.jtaches.taches.CopyTache** : Copy watched files to another location.
    - "path", the directory to watch
    - "copyTo", where to copy the files
    - "makePath", indicate to create or not the copy path if not exists (optional, default true)
    - "excludes", list of exclusion patterns separated by a ';' (optional)

+ **com.github.athieriot.jtaches.taches.ScriptTache** : Display envent details in the console.
    - "path", the directory to watch
    - "script", the script to execute (Patterns ```<path>```, ```<filename>```, ```<shortname>```, ```<ext>``` and ```<event>``` while be replaced by real values)
    - "workingDirectory", the working dir where to execute the script (optional)
    - "excludes", list of exclusion patterns separated by a ';' (optional)

Patterns for exclusion needs to match the Java regular expression representation: [http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html](http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html)

Hack
----

If you want to build a Tache, you need to write a new class implementing the Tache interface.
ConfiguredTache is just an abstract class made to ease the work.

Installation via Maven:


        <dependency>
                <groupId>com.github.athieriot</groupId>
                <artifactId>jtaches</artifactId>
                <version>1.0.1</version>
                <scope>test</scope>
        </dependency>

Then, you have access to five things:

+ getPath() that you need to implement to provide the path to watch.
+ onCreate(), onDelete() and onModify() which are events callbacks and have to be implemented. See [WatchEvent](http://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchEvent.html).
+ Log is a static logging object from which you have access to info(), debug(), etc... See [Minlog](http://code.google.com/p/minlog/). The default log level in INFO and DEBUG in verbose mode.

Additionally if you use ConfiguredTache:

+ getConfiguration() provide a Map, loaded with the attributes in the Yaml file.
+ ConfiguredTache constructor can take a list of String as second argument to indicate mandatory configuration parameters.
+ You can override additionalValidation(Map<String, String> configuration) to provide custom validation. Method executed after mandatory parameters validation.

One thing you have to know with the configuration file is that the Taches are instantiated by SnakeYaml directly.
So, if you respect the interface and the SnakeYaml documentation, you can override the default constructors to do whatever you want.

Known issues
------------

[https://github.com/athieriot/JTaches/issues?milestone=2&state=open](https://github.com/athieriot/JTaches/issues?milestone=2&state=open)
