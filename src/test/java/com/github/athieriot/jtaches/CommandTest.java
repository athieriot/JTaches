package com.github.athieriot.jtaches;

import com.esotericsoftware.minlog.Log;
import com.github.athieriot.jtaches.command.CommandArgs;
import com.github.athieriot.jtaches.command.ConsoleLogger;
import org.testng.annotations.Test;

import static com.github.athieriot.jtaches.Command.executeMain;
import static com.github.athieriot.jtaches.Command.initializeLogger;
import static org.mockito.Mockito.*;

public class CommandTest {

    //TODO: Care this test suite and the coverage will be better
    @Test(timeOut = 2000)
    public void main_command_must_at_least_register_taches() throws Exception {
      String testFile = getClass().getClassLoader().getResource(".jtaches.full.yaml").getFile();

      String[] argv = { "--registerOnly", "--file", testFile};
      executeMain(argv);
    }

    @Test
    public void main_logger_must_display_debug_when_verbose_mode() throws Exception {
        CommandArgs mockArgs = mock(CommandArgs.class);
        Log.Logger spyiedLogger = spy(new ConsoleLogger());

        when(mockArgs.isVerbose()).thenReturn(true);
        initializeLogger(mockArgs, spyiedLogger);

        verify(spyiedLogger).log(anyInt(), anyString(), anyString(), any(Throwable.class));
    }

    @Test
    public void main_logger_must_NOT_display_debug_when_verbose_mode() throws Exception {
        CommandArgs mockArgs = mock(CommandArgs.class);
        Log.Logger spyiedLogger = spy(new ConsoleLogger());

        when(mockArgs.isVerbose()).thenReturn(false);
        initializeLogger(mockArgs, spyiedLogger);

        verify(spyiedLogger, never()).log(anyInt(), anyString(), anyString(), any(Throwable.class));
    }
}
