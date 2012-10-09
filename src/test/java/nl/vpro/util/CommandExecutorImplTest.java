package nl.vpro.util;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

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


}
