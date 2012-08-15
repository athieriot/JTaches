package com.github.awesomeless.jtaches.command;

import com.beust.jcommander.Parameter;

public class CommandArgs {

    @Parameter(names = {"--registerOnly"}, description = "Test mode - Taches registration only")
    public boolean registerOnly = false;

    @Parameter(names = {"-f", "--file"}, description = "Configuration file. Default to .jtaches.yaml")
    public String configurationFile = ".jtaches.yaml";

    @Parameter(names = {"-h", "--help"}, description = "Display this help")
    public boolean help = false;
}
