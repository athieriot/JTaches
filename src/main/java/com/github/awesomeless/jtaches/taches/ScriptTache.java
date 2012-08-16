package com.github.awesomeless.jtaches.taches;

import com.github.awesomeless.jtaches.taches.internal.ConfiguredTache;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.CONFIGURATION_PATH;

public class ScriptTache extends ConfiguredTache {

    public static final String CONFIGURATION_SCRIPT = "script";
    public static final String CONFIGURATION_WORKING_DIRECTORY = "workingDirectory";

    private final String FILE_REPLACEMENT = "<file>";
    private final String PATH_REPLACEMENT = "<path>";
    private final String EVENT_REPLACEMENT = "<event>";

    public ScriptTache(Map<String, String> configuration) {
        super(configuration);
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
        File workingDirectory = new File(getConfiguration().get(CONFIGURATION_WORKING_DIRECTORY));
        String script = manufacturingScript(file, event);

        System.out.println("Executing script: " + script + " on directory: " + workingDirectory);

        ProcessBuilder processBuilder = new ProcessBuilder().command(script.split(" ")).directory(workingDirectory);
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
