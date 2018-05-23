package nl.vpro.nep.service.impl;

import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.OpenMode;
import net.schmizz.sshj.sftp.RemoteFile;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;

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

import nl.vpro.util.FileSizeFormatter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Ignore("This actually does something")
public class NEPSSHJDownloadServiceImplTest {

    private NEPSSJDownloadServiceImpl impl;

    //static String fileName = "KN_1689705__000001927-002511602.mp4";
    static String fileName = "AT_2100854__000000000-005329000.mp4";
    //String fileName = "VPWON_1265965__000414370-000917470.mp4";

    static String testDest = "/tmp/test.mp4";

    @Before
    public void setup() {
        impl = new NEPSSJDownloadServiceImpl(
            "sftp-itemizer.nepworldwide.nl",
            "npo",
            "***REMOVED***",
            "AAAAB3NzaC1yc2EAAAADAQABAAABAQCV4gmmgKyPVyOyZv1jdVpu/KzS9w2v4/vxDeKbuXvl0tldvDAmMi/QY1XvLueuZJy8PmilpGj6po1JuU0V2RGX/Js18b9lyCAQptdaeUk45lYvM8bpGfkzB509i3+CaM6U1onEIftFs4vzDLMwHrZQ6kdlRGGs6bLYy1vpqs7h6mO/XGDeLLVpjLPZbz/TrWt98kinn+Rg/TwYV0VNyqac5DkpWtFEUucIrq6zZs1q3Pw8YHMo02BWlWXFR/yi41ODb+RH1dTlZEs3vrMgwFvVD5c+4EKy1hZ65SJ6xVXwaMyN4w1LaHLwwe3K8rNDS+m5gyaswhdeZthqDiXysFwj");
    }

    @Test
    public void createFile() throws Exception {
        Instant start = Instant.now();

        File dest = new File(testDest);
        impl.download(fileName, new FileOutputStream(dest), (fc) -> true);

        Duration duration = Duration.between(start, Instant.now());
        assertThat(dest.length()).isEqualTo(221400200L);
    }
/*
    @Test
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

        boolean simple = true;
        Instant start = Instant.now();
        SSHClient client = impl.createClient();
        final SFTPClient sftp = client.newSFTPClient();

        if (! simple) {

            final RemoteFile handle = sftp.open(fileName, EnumSet.of(OpenMode.READ));
            InputStream in = handle.new ReadAheadRemoteFileInputStream(32);
            FileOutputStream outputStream = new FileOutputStream(testDest);
            long size = IOUtils.copy(in, outputStream, 1024 * 10);
            log.info("Ready with {} bytes ({})", size,FileSizeFormatter.DEFAULT.formatSpeed(size, start));
            in.close();
            outputStream.close();
            handle.close();
        } else {
            sftp.get(fileName, new FileSystemFile(new File(testDest )));
        }
        sftp.close();
        client.close();

        log.info("Duration {} {}", Duration.between(start, Instant.now()));




    }

    @Test
    public void testSshjAvailability() throws IOException {

        impl.checkAvailabilityAndConsume(fileName, Duration.ofSeconds(10),
            (fd) -> {
                log.info("found {}", fd);
                return true;

            }, (handle) -> {});



    }


}
