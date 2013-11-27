package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.parseBoolean;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.delete;
import static java.nio.file.Paths.get;

public class LessCompilerTache extends ConfiguredTache {

    public static final String CONFIGURATION_COMPILE_TO = "compileTo";
    public static final String CONFIGURATION_MAKE_PATH = "makePath";

    public LessCompilerTache(Map<String, String> configuration) {
        super(configuration, CONFIGURATION_COMPILE_TO);
    }

    @Override
    protected void additionalValidation(Map<String, String> configuration) throws InvalidParameterException {
        super.additionalValidation(configuration);

        if(configuration.get(CONFIGURATION_PATH).startsWith(configuration.get(CONFIGURATION_COMPILE_TO))
                || configuration.get(CONFIGURATION_COMPILE_TO).startsWith(configuration.get(CONFIGURATION_PATH))) {
            throw new InvalidParameterException("An error occured while creating the task. Incompatibility between Path parameter and CompileTo parameter.");
        }
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        if (isLessFile(event)) {
            doCompile(event);
        }
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        if (isLessFile(event)) {
            Path to = get(getConfiguration().get(CONFIGURATION_COMPILE_TO), resolveFileName(event).replaceAll(".less", ".css"));

            try {
                info("Deleting file: " + to.toString());
                delete(to);
            } catch (IOException e) {
                info("Unable to delete file: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        if (isLessFile(event)) {
            doCompile(event);
        }
    }

    private String resolveFileName(WatchEvent<?> event) {
        return event.context().toString();
    }

    private void doCompile(WatchEvent<?> event) {
        LessCompiler lessCompiler = new LessCompiler();

        Path from = get(getConfiguration().get(CONFIGURATION_PATH), resolveFileName(event));
        Path to = get(getConfiguration().get(CONFIGURATION_COMPILE_TO), resolveFileName(event).replaceAll(".less", ".css"));

        try {
            makePathIfWanted(to);

            info("Compiling file: " + to.toString());
            lessCompiler.compile(from.toFile(), to.toFile());
        } catch (IOException e) {
            info("Unable to copy file: " + e.getMessage(), e);
        } catch (LessException e) {
            info("Unable to compile file: " + e.getMessage(), e);
        }
    }

    //CLEANUP: Could be good to handle Boolean as the object and not only via String
    private void makePathIfWanted(Path file) throws IOException {
        if(!getConfiguration().containsKey(CONFIGURATION_MAKE_PATH)
                || parseBoolean(getConfiguration().get(CONFIGURATION_MAKE_PATH))) {
            createDirectories(file.getParent());
        }
    }

    private boolean isLessFile(WatchEvent<?> event) {
        return resolveFileName(event).matches(".*less$");
    }
}
