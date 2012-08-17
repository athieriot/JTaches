package com.github.awesomeless.jtaches;

import com.beust.jcommander.JCommander;
import com.github.awesomeless.jtaches.command.CommandArgs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.InvalidParameterException;
import java.util.Map;

import static com.github.awesomeless.jtaches.command.Configuration.yamlToMap;
import static com.github.awesomeless.jtaches.utils.TacheUtils.constructionByReflection;

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
        Map<String, Map<String, String>> configurationMap = parseConfigurationFile(commandArgs.configurationFile);

        Guardian guardian = Guardian.create();

        for(String key : configurationMap.keySet()) {
            Tache newTache = buildNewTache(key, configurationMap.get(key));

            /*- If a tache is invalid, the program quit
              - If the configuration file reference a non existing class, the guardian skip it
            */
            guardian.registerTache(newTache);
        }

        if(!commandArgs.registerOnly) guardian.watch();
    }

    private static Tache buildNewTache(String key, Map<String, String> configuration) {
        try {
            return constructionByReflection(key, configuration);

        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | InvocationTargetException e) {
            System.out.println("Unable to build a tache for this configuration: " + key + " - " + e.getMessage());
            if(e.getCause() != null)
               System.out.println(e.getCause().getMessage());
        }

        return null;
    }

    private static CommandArgs parseCommandLine(String[] args) {
        CommandArgs commandArgs = new CommandArgs();
        JCommander jCommander = new JCommander(commandArgs, args);

        if(commandArgs.help) {jCommander.usage(); System.exit(0);}
        return commandArgs;
    }
    private static Map<String, Map<String, String>> parseConfigurationFile(String configurationFile) throws FileNotFoundException {
        try {
            return yamlToMap(configurationFile);
        } catch(FileNotFoundException fnfe) {
            System.out.println("Configuration file did not exists.");
            System.exit(1);
        }

        return null;
    }
}
