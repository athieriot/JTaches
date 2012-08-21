package com.github.athieriot.jtaches.taches;

import com.github.athieriot.jtaches.taches.internal.ConfiguredTache;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.WatchEvent;
import java.util.Map;

import static com.esotericsoftware.minlog.Log.info;
import static com.github.athieriot.jtaches.command.Configuration.CONFIGURATION_PATH;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.delete;
import static java.nio.file.Paths.get;

public class CopyTache extends ConfiguredTache {

    public static final String CONFIGURATION_COPY_TO = "copyTo";

    public CopyTache(Map<String, String> configuration) {
        super(configuration, CONFIGURATION_COPY_TO);
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
            info("Copying file: " + to.toString());
            copy(from, to, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            info("Unable to copy file: " + e.getMessage(), e);
        }
    }
}
