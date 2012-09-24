package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.security.InvalidParameterException;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static java.lang.Boolean.parseBoolean;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;

public class CopyTache extends ConfiguredTache {

    public static final String CONFIGURATION_COPY_TO = "copyTo";
    public static final String CONFIGURATION_MAKE_PATH = "makePath";

    public CopyTache(Map<String, String> configuration) {
        super(configuration, CONFIGURATION_COPY_TO);
    }

    @Override
    protected void additionalValidation(Map<String, String> configuration) throws InvalidParameterException {
        super.additionalValidation(configuration);

        if(configuration.get(CONFIGURATION_PATH).startsWith(configuration.get(CONFIGURATION_COPY_TO))
                || configuration.get(CONFIGURATION_COPY_TO).startsWith(configuration.get(CONFIGURATION_PATH))) {
            throw new InvalidParameterException("An error occured while creating the task. Incompatibility between Path parameter and CopyTo parameter.");
        }
    }

    @Override
    public void onCreate(WatchEvent<?> event) {
        doCopy(event);
    }

    @Override
    public void onDelete(WatchEvent<?> event) {
        Path to = get(getConfiguration().get(CONFIGURATION_COPY_TO), resolveFileName(event));

        try {
            info("Deleting file: " + to.toString());
            delete(to);
        } catch (IOException e) {
            info("Unable to delete file: " + e.getMessage(), e);
        }
    }

    @Override
    public void onModify(WatchEvent<?> event) {
        doCopy(event);
    }

    private String resolveFileName(WatchEvent<?> event) {
        return event.context().toString();
    }

    private void doCopy(WatchEvent<?> event) {
        Path from = get(getConfiguration().get(CONFIGURATION_PATH), resolveFileName(event));
        Path to = get(getConfiguration().get(CONFIGURATION_COPY_TO), resolveFileName(event));

        try {
            makePathIfWanted(to);

            info("Copying file: " + to.toString());
            copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            info("Unable to copy file: " + e.getMessage(), e);
        }
    }

    //CLEANUP: Could be good to handle Boolean as the object and not only via String
    private void makePathIfWanted(Path file) throws IOException {
        if(!getConfiguration().containsKey(CONFIGURATION_MAKE_PATH)
                || parseBoolean(getConfiguration().get(CONFIGURATION_MAKE_PATH))) {
            createDirectories(file.getParent());
        }
    }
}
