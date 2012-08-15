package com.github.awesomeless.jtaches;

import com.beust.jcommander.JCommander;
import com.github.awesomeless.jtaches.command.CommandArgs;
import com.github.awesomeless.jtaches.taches.SysoutTache;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Map;

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
            return yamlLoading(configurationFile);
        } catch(FileNotFoundException fnfe) {
            System.out.println("Configuration file did not exists.");
            System.exit(1);
        }

        return null;
    }

    static Map<String, String> yamlLoading(String configurationFile) throws FileNotFoundException {
        Yaml yaml = new Yaml();
        return (Map<String, String>) yaml.load(new FileInputStream(new File(configurationFile)));
    }
}
