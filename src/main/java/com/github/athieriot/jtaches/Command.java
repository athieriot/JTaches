package com.github.athieriot.jtaches;

import com.beust.jcommander.JCommander;
import com.esotericsoftware.minlog.Log;
import com.github.athieriot.jtaches.command.CommandArgs;
import com.github.athieriot.jtaches.command.ConsoleLogger;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.List;

import static com.esotericsoftware.minlog.Log.*;
import static com.github.athieriot.jtaches.command.Configuration.yamlToMap;
import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public enum Command {;

    public static void main(String[] args) {
        try {
            executeMain(args);

        } catch (IOException ioe) {
            //TODO: Localize the messages
            info("A problem occured with guardian: " + ioe.getMessage(), ioe);
            System.exit(1);
        } catch (InvalidParameterException ipe) {
            info("There is a problem with a tache: " + ipe.getMessage(), ipe);
            System.exit(1);
        } catch (InterruptedException ie) {
            info("The guardian was interrupted by something: " + ie.getMessage(), ie);
            System.exit(1);
        } catch (YAMLException ye) {
            info("Unable to build a tache for this configuration:");
            info("\t - " + ye.getMessage());
            info("\t - " + getRootCauseMessage(ye));
            System.exit(1);
        }
    }

    public static void executeMain(String [] args) throws IOException, InterruptedException {
        CommandArgs commandArgs = parseCommandLine(args);
        initializeLogger(commandArgs, new ConsoleLogger());
        List<Tache> taches = parseConfigurationFile(commandArgs.getConfigurationFile());

        startWatching(commandArgs, taches);
    }

    private static void startWatching(CommandArgs commandArgs, List<Tache> taches) throws IOException, InterruptedException {
        Guardian guardian = Guardian.create();
        guardian.setCommandArgs(commandArgs);

        for(Tache tache : taches) {
            /*- If a tache is invalid, the program quit
              - If the configuration file reference a non existing class, the guardian skip it
            */
            //FIXME: Don't register the same path twice
            guardian.registerTache(tache);
        }

        keepWatching(commandArgs, guardian);
    }
    private static void keepWatching(CommandArgs commandArgs, Guardian guardian) throws IOException, InterruptedException {
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
    static void initializeLogger(CommandArgs commandArgs, Log.Logger logger) {
        Log.setLogger(logger);
        if(commandArgs.isVerbose()) {
            Log.set(LEVEL_DEBUG);
            debug("==Verbose mode activated==");
        }
    }
    private static List<Tache> parseConfigurationFile(String configurationFile) {
        try {
            return yamlToMap(configurationFile);
        } catch(FileNotFoundException fnfe) {
            info("Configuration file did not exists.", fnfe);
            System.exit(1);
        }

        return newArrayList();
    }
}
