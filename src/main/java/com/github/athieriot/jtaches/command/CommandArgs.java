package com.github.athieriot.jtaches.command;

import com.beust.jcommander.Parameter;

public class CommandArgs {

    @Parameter(names = {"--registerOnly"}, description = "Test mode - Taches registration only")
    private boolean registerOnly = false;

    @Parameter(names = {"-f", "--file"}, description = "Configuration file.")
    private String configurationFile = ".jtaches.full.yaml";

    @Parameter(names = {"-h", "--help"}, description = "Display this help")
    private boolean help = false;

    public boolean isRegisterOnly() {
        return registerOnly;
    }

    public String getConfigurationFile() {
        return configurationFile;
    }

    public boolean hasHelp() {
        return help;
    }
}
