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
            System.out.println("A problem occured with guardian: " + ioe.getMessage());
        } catch (InvalidParameterException ipe) {
            System.out.println("There is a problem with a tache: " + ipe.getMessage());
        } catch (InterruptedException ie) {
            System.out.println("The guardian was interrupted by something: " + ie.getMessage());
        }
    }

    public static void executeMain(String [] args) throws IOException, InterruptedException {
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
