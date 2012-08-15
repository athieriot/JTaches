package com.github.awesomeless.jtaches;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class CommandTest {

    @Test(timeOut = 2000)
    public void main_command_must_at_least_register_taches() throws Exception {
        String[] argv = { "--registerOnly" };
        Command.executeMain(argv);
    }
}
