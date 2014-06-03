package nl.vpro.util;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Executor for external commands.
 *
 * @author Michiel Meeuwissen
 * @since 1.6
 */
public interface CommandExecutor {


    /**
     * Executes with the given arguments. The command itself is supposed to be a member of the implementation, so you
     * would have a CommandExecutor instance for every external program you'd like to wrap.
     * The version with no outputstream argument logs the output.
     * @return the exit code
     */
    int execute(String... args);

    /**
     * Executes the command
     *
     * @param out Stdout of the command will be written to this.
     * @param error Stderr of the comman will be written to this. To log errors use {@link nl.vpro.logging.LoggerOutputStream#error(org.slf4j.Logger)}
     * @param args The command and its arguments to be executed on the remote server
     * @return The exit code
     */
    int execute(OutputStream out, OutputStream error, String... args);


    /**
     * Executes the command
     *
     * @param in  Stdin of the command will be taken from this.
     * @param out Stdout of the command will be written to this.
     * @param error Stder of the command will be written to this.
     * @return The exit code
     */
    int execute(InputStream in, OutputStream out, OutputStream error, String... args);

}
