package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.google.common.io.Files.getFileExtension;
import static org.apache.commons.io.FilenameUtils.removeExtension;

public class ScriptTache extends ConfiguredTache {

    public static final String CONFIGURATION_SCRIPT = "script";
    public static final String CONFIGURATION_WORKING_DIRECTORY = "workingDirectory";

    //CLEANUP: Enumeration of patterns
    private static final String FULL_NAME_REPLACEMENT = "<filename>";
    private static final String NAME_REPLACEMENT = "<shortname>";
    private static final String EXT_REPLACEMENT = "<ext>";
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
        if (getConfiguration().get(CONFIGURATION_WORKING_DIRECTORY) != null) {
            processBuilder.directory(new File(getConfiguration().get(CONFIGURATION_WORKING_DIRECTORY)));
        }

        info("Executing script: " + script);
        try {
            Process process = processBuilder.start();
            process.waitFor();

            displayOutPut(process);
        } catch (IOException e) {
            info("Error executing the command: " + script + " - " + e.getMessage(), e);
        } catch (InterruptedException e) {
            info("Command interrupted during execution: " + script + " - " + e.getMessage(), e);
        }
    }

    private void displayOutPut(Process process) throws IOException {
        BufferedReader commandLineBuffer = new BufferedReader(new InputStreamReader(process.getInputStream()));

        String line = "";
        while ((line = commandLineBuffer.readLine()) != null) {
            System.out.println(line);
        }
    }

    String manufacturingScript(File file, String event) {
        String script = "";
        script = getConfiguration().get(CONFIGURATION_SCRIPT).replaceAll(FULL_NAME_REPLACEMENT, file.getName());
        script = script.replaceAll(NAME_REPLACEMENT, removeExtension(file.getName()));
        script = script.replaceAll(EXT_REPLACEMENT, getFileExtension(file.getName()));
        script = script.replaceAll(PATH_REPLACEMENT, file.getParent());
        script = script.replaceAll(EVENT_REPLACEMENT, event);

        return script;
    }
}
