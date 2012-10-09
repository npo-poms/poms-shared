package nl.vpro.util;

import java.io.OutputStream;

/**
 * Executor for external commands.
 * @author Michiel Meeuwissen
 */
public interface CommandExecutor {


    /**
     * Executes with the given arguments. The command itself is supposed to be a member of the implementation, so you
     * would have a CommandExecutor instance for every external program you'd like to wrap.
     * The version with no outputstream argument logs the output.
     */
    void execute(String... args);

    /**
     * Executes the command
     * @param out Stdout of the command will be written to this. (stderr is logged to error)
     * @param args
     */
    void execute(OutputStream out, String... args);

}
