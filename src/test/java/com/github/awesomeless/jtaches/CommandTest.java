package com.github.awesomeless.jtaches;

import org.testng.annotations.Test;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;
import java.util.Map;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

public class CommandTest {

    @Test(timeOut = 2000)
    public void main_command_must_at_least_register_taches() throws Exception {
        String[] argv = { "--registerOnly" };
        Command.executeMain(argv);
    }

    @Test
    public void a_configuration_file_must_be_parsable() throws URISyntaxException, FileNotFoundException {
        String testFile = getClass().getClassLoader().getResource(".jtaches.test.yaml").getFile();

        Map<String, String> map = Command.yamlLoading(testFile);

        assertNotNull(map);
        assertEquals(map.get("path"), ".");
        assertEquals(map.get("other"), "ohyeah");
    }
}
