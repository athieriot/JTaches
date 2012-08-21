package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;

public class ScriptTache extends ConfiguredTache {

    public static final String CONFIGURATION_SCRIPT = "script";
    public static final String CONFIGURATION_WORKING_DIRECTORY = "workingDirectory";

    //CLEANUP: Enumeration or List
    private static final String FILE_REPLACEMENT = "<file>";
    private static final String PATH_REPLACEMENT = "<path>";
    private static final String EVENT_REPLACEMENT = "<event>";

    public ScriptTache(Map<String, String> configuration) {
        super(configuration, CONFIGURATION_SCRIPT);
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        executeScript(event);
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        executeScript(event);
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        executeScript(event);
    }

    void executeScript(WatchEvent<?> event) {
        executeScript(new File(getConfiguration().get(CONFIGURATION_PATH) + "/" + event.context().toString()), event.kind().name());
    }
    private void executeScript(File file, String event) {
        String script = manufacturingScript(file, event);

        ProcessBuilder processBuilder = new ProcessBuilder().command(script.split(" "));
        if (getConfiguration().get(CONFIGURATION_WORKING_DIRECTORY) != null)
            processBuilder.directory(new File(getConfiguration().get(CONFIGURATION_WORKING_DIRECTORY)));

        System.out.println("Executing script: " + script);
        try {
            processBuilder.start();
        } catch (IOException e) {
            System.out.println("Error executing the command: " + script + " - " + e.getMessage());
        }
    }

    String manufacturingScript(File file, String event) {
        String script = "";
        script = getConfiguration().get(CONFIGURATION_SCRIPT).replaceAll(FILE_REPLACEMENT, file.getName());
        script = script.replaceAll(PATH_REPLACEMENT, file.getParent());
        script = script.replaceAll(EVENT_REPLACEMENT, event);

        return script;
    }
}
