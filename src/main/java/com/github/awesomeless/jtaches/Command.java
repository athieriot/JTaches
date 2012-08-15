package com.github.awesomeless.jtaches;

import com.beust.jcommander.JCommander;
import com.github.awesomeless.jtaches.Guardian;
import com.github.awesomeless.jtaches.command.CommandArgs;
import com.github.awesomeless.jtaches.taches.SysoutTache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.yamlToMap;

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
        Map<String, String> configurationMap = parseConfigurationFile(commandArgs.configurationFile);

        Guardian guardian = Guardian.create();
        guardian.registerTache(new SysoutTache(configurationMap));

        if(!commandArgs.registerOnly) guardian.watch();
    }

    private static CommandArgs parseCommandLine(String[] args) {
        CommandArgs commandArgs = new CommandArgs();
        JCommander jCommander = new JCommander(commandArgs, args);

        if(commandArgs.help) {jCommander.usage(); System.exit(0);}
        return commandArgs;
    }
    private static Map<String, String> parseConfigurationFile(String configurationFile) throws FileNotFoundException {
        try {
            return yamlToMap(configurationFile);
        } catch(FileNotFoundException fnfe) {
            System.out.println("Configuration file did not exists.");
            System.exit(1);
        }

        return null;
    }
}
