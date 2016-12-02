package nl.vpro.util;

import java.io.ByteArrayOutputStream;

import org.junit.Test;
import org.slf4j.Logger;

import static org.junit.Assert.assertEquals;

/**
 * @author Michiel Meeuwissen
 */
public class CommandExecutorImplTest {

    @Test
    public void execute() {
        CommandExecutorImpl instance = new CommandExecutorImpl("/usr/bin/env");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        instance.execute(out, "echo", "hoi");
        assertEquals("hoi\n", new String(out.toByteArray()));

        instance.execute("echo", "hoi");
    }

    @Test
    public void logger() {
        Logger logger = new CommandExecutorImpl("/usr/bin/env").getLogger();
        assertEquals(CommandExecutorImpl.class.getName() + ".env.bin.usr", logger.getName());
    }


}
