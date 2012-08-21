package com.github.athieriot.jtaches.command;

import com.esotericsoftware.minlog.Log;

public class ConsoleLogger extends Log.Logger {

    public void log(int level, String category, String message, Throwable ex) {
        System.out.println(message);
    }
}
