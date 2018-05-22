package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.EnumSet;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import nl.vpro.util.CommandExecutor;
import nl.vpro.util.CommandExecutorImpl;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Ignore("This actually does something")
public class NEPFTPDownloadServiceImplTest {

    private NEPFTPDownloadServiceImpl impl;

    @Before
    public void setup() {
        impl = new NEPFTPDownloadServiceImpl(
            "sftp-itemizer.nepworldwide.nl",
            "npo",
            "***REMOVED***",
            "94:06:26:d5:e4:f5:18:b5:52:a9:19:b1:97:db:94:9e");
    }

    @Test
    public void createFile() throws Exception {
        Instant start = Instant.now();
        String fileName = "VPWON_1265965__000414370-000917470.mp4";
        File dest = File.createTempFile("test", ".mp4");
        impl.download(fileName, new FileOutputStream(dest), (fc) -> true);

        Duration duration = Duration.between(start, Instant.now());
        assertThat(dest.length()).isEqualTo(221400200L);
    }

 /*   @Test
    public void testJsch() throws IOException, JSchException, SftpException {
        JSch sshClient = new JSch();
        HostKey hostKey = new HostKey ( "sftp-itemizer.nepworldwide.nl",  Base64.getDecoder().decode ("AAAAB3NzaC1yc2EAAAADAQABAAABAQCV4gmmgKyPVyOyZv1jdVpu/KzS9w2v4/vxDeKbuXvl0tldvDAmMi/QY1XvLueuZJy8PmilpGj6po1JuU0V2RGX/Js18b9lyCAQptdaeUk45lYvM8bpGfkzB509i3+CaM6U1onEIftFs4vzDLMwHrZQ6kdlRGGs6bLYy1vpqs7h6mO/XGDeLLVpjLPZbz/TrWt98kinn+Rg/TwYV0VNyqac5DkpWtFEUucIrq6zZs1q3Pw8YHMo02BWlWXFR/yi41ODb+RH1dTlZEs3vrMgwFvVD5c+4EKy1hZ65SJ6xVXwaMyN4w1LaHLwwe3K8rNDS+m5gyaswhdeZthqDiXysFwj"));

        sshClient.getHostKeyRepository().add(hostKey, null);
        Session session = sshClient.getSession("npo", "sftp-itemizer.nepworldwide.nl");
        session.setConfig("StrictHostKeyChecking", "no");


        session.setPassword("***REMOVED***");

        session.connect();

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();
        InputStream inputStream = sftp.get("AT_2100854__000000000-005329000.mp4");
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        IOUtils.copy(inputStream, outputStream, 1024 * 10);


    }*/
    @Test
    public void testSshj() throws IOException {
        SSHClient client = impl.createClient();
        final SFTPClient sftp = client.newSFTPClient();
        final RemoteFile handle = sftp.open("AT_2100854__000000000-005329000.mp4", EnumSet.of(OpenMode.READ));


        //        sftp.get("AT_2081412__000000000-010000000.mp4", new FileSystemFile(new File("/tmp/test.mp4")));

        InputStream in = handle.new ReadAheadRemoteFileInputStream(16);

        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");
        IOUtils.copy(in, outputStream, 1024 * 10);
        log.info("Ready");
        in.close();
        outputStream.close();
        handle.close();

    }

    @Test
    public void testCommandExecutor() throws IOException {
        CommandExecutor wget = new CommandExecutorImpl("/opt/local/bin/wget");
        FileOutputStream outputStream = new FileOutputStream("/tmp/test.mp4");

        wget.execute(outputStream, "-q", "-O-", "ftp://npo:***REMOVED***@sftp-itemizer.nepworldwide.nl/AT_2100854__000000000-005329000.mp4");

    }
}
