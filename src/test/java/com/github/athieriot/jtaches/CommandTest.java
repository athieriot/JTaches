package com.github.athieriot.jtaches;

import org.testng.annotations.Test;

import static com.github.athieriot.jtaches.Command.executeMain;

public class CommandTest {

    //TODO: Care this test suite and the coverage will be better
    @Test(timeOut = 2000)
    public void main_command_must_at_least_register_taches() throws Exception {
      String testFile = getClass().getClassLoader().getResource(".jtaches.full.yaml").getFile();

      String[] argv = { "--registerOnly", "--file", testFile};
      executeMain(argv);
    }
}
