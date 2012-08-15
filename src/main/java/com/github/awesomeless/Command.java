package com.github.awesomeless;

import java.io.IOException;

import static java.nio.file.Paths.get;

public class Command {

    public static void main(String[] args) {
        try {
            Guardian guardian = Guardian.create();
            guardian.registerTache(null);
            guardian.watch();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
