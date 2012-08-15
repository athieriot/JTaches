package com.github.awesomeless.jtaches;

import com.beust.jcommander.JCommander;
import com.github.awesomeless.jtaches.command.CommandArgs;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.security.InvalidParameterException;

import static java.nio.file.Paths.get;

public class Command {

    public static void main(String[] args) {
        try {
            executeMain(args);

        } catch (IOException ioe) {
            System.out.println("A problem occured when initialize guardian: " + ioe.getMessage());
        } catch (InvalidParameterException ipe) {
            System.out.println("Invalid tache detected: " + ipe.getMessage());
        }
    }

    public static void executeMain(String [] args) throws IOException {
        CommandArgs commandArgs = parseCommandLine(args);

        Guardian guardian = Guardian.create();
        guardian.registerTache(null);

        if(!commandArgs.registerOnly) guardian.watch();
    }

    private static CommandArgs parseCommandLine(String[] args) {
        CommandArgs commandArgs = new CommandArgs();
        JCommander jCommander = new JCommander(commandArgs, args);

        if(commandArgs.help) {jCommander.usage(); System.exit(0);}
        return commandArgs;
    }
}
