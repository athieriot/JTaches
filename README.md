JTaches
=======

[![Build Status](https://secure.travis-ci.org/athieriot/JTaches.png)](http://travis-ci.org/athieriot/JTaches) [![Coverage Status](https://coveralls.io/repos/athieriot/JTaches/badge.png?branch=master)](https://coveralls.io/r/athieriot/JTaches?branch=master)

About
-----

JTaches provides a Java way to execute tasks on file events.
Mostly inspired by the [Guard](https://github.com/guard/guard/) ruby gem.

Binaries
--------

Thanks to this Protip: [http://coderwall.com/p/ssuaxa](http://coderwall.com/p/ssuaxa), you can now download a Linux executable and just use it (you still need Java):

        wget https://github.com/athieriot/JTaches/releases/download/1.3/jtaches-1.3.0
        chmod +x jtaches-1.3.0
        ./jtaches-1.3.0 -h

For all other cases, you can find a JAR pre-compiled with all needed dependencies on the [Sonatype](http://search.maven.org/#search%7Cga%7C1%7Cjtaches) repository:
[jtaches-1.3-jar-with-dependencies.jar](http://search.maven.org/remotecontent?filepath=com/github/athieriot/jtaches/1.3/jtaches-1.3-jar-with-dependencies.jar)

Then you just need to execute:

        java -jar jtaches-1.3-jar-with-dependencies.jar -h

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
By default, the program looks up **.jtaches.yaml** in the current directory but you can override this default filename from the command line.

        ./jtaches -file whatever.yaml

Configuration
-------------

The configuration needs to be in Yaml format and the content look like this:

        - !!com.github.athieriot.jtaches.taches.ScriptTache [{
            path: ".",
            script: "notify-send <path>/<filename>/<event>",
        }]

That is: a list of Taches classes (in classpath) containing a list of properties.
The syntax is borrowed from [SnakeYaml](http://code.google.com/p/snakeyaml/wiki/Documentation) as it is the parser internally used.

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

+ **com.github.athieriot.jtaches.taches.LessCompilerTache** : Compile a Less file to Css in the specified location. Using: [lesscss-java](https://github.com/marceloverdijk/lesscss-java)
    - "path", the directory to watch
    - "compileTo", where to compile the files
    - "makePath", indicate to create or not the copy path if not exists (optional, default true)
    - "excludes", list of exclusion patterns separated by a ';' (optional)

+ **com.github.athieriot.jtaches.taches.ScriptTache** : Execute a command on file event
    - "path", the directory to watch
    - "script", the script to execute (Patterns ```<path>```, ```<filename>```, ```<shortname>```, ```<ext>``` and ```<event>``` while be replaced by real values)
    - "workingDirectory", the working dir where to execute the script (optional)
    - "excludes", list of exclusion patterns separated by a ';' (optional)

+ **com.github.athieriot.jtaches.taches.RabbitmqTache** : Sending filename as a message to a RabbitMQ exchange (using events as routing keys).
    - "path", the directory to watch
    - "exchangeName", name of the exchange to which send messages
    - "createEventRoutingKey", RoutingKey for onCreate event (optional, default ENTRY_CREATE)
    - "modifyEventRoutingKey", RoutingKey for onModify event (optional, default ENTRY_MODIFY)
    - "deleteEventRoutingKey", RoutingKey for onDelete event (optional, default ENTRY_DELETE)
    - "absolutePath", if true the absolute path of the modified file is send to RabbitMQ, if false it's the relative path (optional, default false)
    - "host", host address of your RabbitMQ instance (optional) 
    - "port", port (optional) 
    - "username", username of your RabbitMQ instance (optional)
    - "password", password of your RabbitMQ instance (optional)
    - "virtualHost", virtualhost (optional)
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
                <version>1.3</version>
                <scope>test</scope>
        </dependency>

Then, you have access to five things:

+ getPath() that you need to implement to provide the path to watch.
+ getExcludes() that you need to implement to provide a Collection of Strings to exclude from the watching
+ onCreate(), onDelete() and onModify() which are events callbacks and have to be implemented. See [WatchEvent](http://docs.oracle.com/javase/7/docs/api/java/nio/file/WatchEvent.html).
+ Log is a static logging object from which you have access to info(), debug(), etc... See [Minlog](http://code.google.com/p/minlog/). The default log level in INFO and DEBUG in verbose mode.

Additionally if you use ConfiguredTache:

+ getConfiguration() provides a Map, loaded with the attributes in the Yaml file.
+ ConfiguredTache constructor can take a list of String as second argument to indicate mandatory configuration parameters.
+ You can override additionalValidation(Map<String, String> configuration) to provide custom validation. Method executed after mandatory parameters validation.

One thing you have to know with the configuration file is that the Taches are instantiated by SnakeYaml directly.
So, if you respect the interface and the SnakeYaml documentation, you can override the default constructors to do whatever you want.

Troubleshooting
---------------

+ IntelliJ backup files:

   If you are using IntelliJ as idea, or if you use another tool that generate backup files in your current work directory, you might want to stop those files from notifying JTaches.
   This is doable by using the ```excludes``` property available in most Taches.

   For example, to ignore IntelliJ backup files, use this in one of you Tache:

      ```excludes: "^[.#]|_jb_bak__$"```

Known issues
------------

[https://github.com/athieriot/JTaches/issues?milestone=2&state=open](https://github.com/athieriot/JTaches/issues?milestone=2&state=open)


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/athieriot/jtaches/trend.png)](https://bitdeli.com/free "Bitdeli Badge")

