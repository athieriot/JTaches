package com.github.athieriot.jtaches;

import com.beust.jcommander.JCommander;
import com.github.athieriot.jtaches.command.CommandArgs;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import static com.github.athieriot.jtaches.command.Configuration.yamlToMap;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public enum Command {;

    public static void main(String[] args) {
        try {
            executeMain(args);

        } catch (IOException ioe) {
            System.out.println("A problem occured with guardian: " + ioe.getMessage());
            System.exit(1);
        } catch (InvalidParameterException ipe) {
            System.out.println("There is a problem with a tache: " + ipe.getMessage());
            System.exit(1);
        } catch (InterruptedException ie) {
            System.out.println("The guardian was interrupted by something: " + ie.getMessage());
            System.exit(1);
        } catch (YAMLException ye) {
            System.out.println("Unable to build a tache for this configuration:");
            System.out.println("\t - " + ye.getMessage());
            System.out.println("\t - " + getRootCauseMessage(ye));
            System.exit(1);
        }
    }

    public static void executeMain(String [] args) throws IOException, InterruptedException {
        CommandArgs commandArgs = parseCommandLine(args);
        List<Tache> taches = parseConfigurationFile(commandArgs.getConfigurationFile());

        Guardian guardian = Guardian.create();

        for(Tache tache : taches) {
            /*- If a tache is invalid, the program quit
              - If the configuration file reference a non existing class, the guardian skip it
            */
            guardian.registerTache(tache);
        }

        if(!commandArgs.isRegisterOnly()) {
            guardian.watch();
        }
    }

    private static CommandArgs parseCommandLine(String[] args) {
        CommandArgs commandArgs = new CommandArgs();
        JCommander jCommander = new JCommander(commandArgs, args);

        if(commandArgs.hasHelp()) {jCommander.usage(); System.exit(0);}
        return commandArgs;
    }
    private static List<Tache> parseConfigurationFile(String configurationFile) {
        try {
            return yamlToMap(configurationFile);
        } catch(FileNotFoundException fnfe) {
            System.out.println("Configuration file did not exists.");
            System.exit(1);
        }

        return newArrayList();
    }
}
