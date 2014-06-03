package nl.vpro.util;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.vpro.logging.LoggerOutputStream;

/**
 * Wrapper around the command line tools 'ssh' and 'scp'.
 * @author Michiel Meeuwissen
 * @since 3.1
 */
public class Ssh {

    private final static Logger LOG = LoggerFactory.getLogger(Ssh.class);

    private final OutputStream STDOUT = LoggerOutputStream.debug(LOG);
    private final OutputStream STDERR = LoggerOutputStream.error(LOG);

    private final String remote_host_name;// = "upload.omroep.nl";
    private final String remote_host_user; // = "vprosmc";
    private final File   remote_host_private_key_file;// = " key_for_upload_omroep_nl";

    private final CommandExecutor ssh = new CommandExecutorImpl("/usr/bin/ssh");
    private final CommandExecutor scp = new CommandExecutorImpl("/usr/bin/scp");

    public Ssh(String remoteHostName, String remoteHostUser, String privateKeyFile) {
        if (remoteHostName == null) throw new IllegalArgumentException();
        if (remoteHostUser == null) throw new IllegalArgumentException();
        remote_host_name = remoteHostName;
        remote_host_private_key_file = privateKeyFile == null
            ? new File(System.getProperty("user.home") + "/.ssh/id_dsa") :
            new File(privateKeyFile);
        this.remote_host_user = remoteHostUser;

        if (!remote_host_private_key_file.exists()) {
            throw new RuntimeException(remote_host_private_key_file + " does not exist");
        }
    }

    public Ssh(String remoteHostName, String remoteHostUser) {
        this(remoteHostName, remoteHostUser, null);
    }


    public void exec(String... command) throws Exception {
        final List<String> args = new ArrayList<>(
            Arrays.asList(
                "-i",
                remote_host_private_key_file.getAbsolutePath(),
                remote_host_user + "@" + remote_host_name));
        args.addAll(Arrays.asList(command));
        ssh.execute(args.toArray(new String[args.size()]));

    }


    public void upload(File localFrom, String remotePath) throws Exception {
        int result = scp.execute(STDOUT, STDERR,
            "-i",
            remote_host_private_key_file.getAbsolutePath(), localFrom.getAbsolutePath(), remote_host_user + "@" + remote_host_name + ":" + remotePath);

        if (result != 0)
            throw new RuntimeException("Not succeeded upload from " + localFrom + " to  " + remote_host_name + ":" + remotePath);
    }

    public void download(String remotePath, File localTo) throws Exception {
        int result =
            scp.execute(
                STDOUT,
                STDERR,
                "-i",
                remote_host_private_key_file.getAbsolutePath(),
                remote_host_user + "@" + remote_host_name + ":" + remotePath,
                localTo.getAbsolutePath());
        if (result != 0)
            throw new RuntimeException("Not succeeded download from " + remote_host_name + ":" + remotePath + " to  " + localTo);
    }

}
