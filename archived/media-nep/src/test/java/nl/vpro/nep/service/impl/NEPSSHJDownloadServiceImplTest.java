package nl.vpro.nep.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.*;
import net.schmizz.sshj.xfer.FileSystemFile;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.*;

import nl.vpro.nep.service.NEPDownloadService;
import nl.vpro.util.FileSizeFormatter;

import static nl.vpro.util.ExceptionUtils.wrapException;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
@Disabled("This actually does something")
public class NEPSSHJDownloadServiceImplTest {

    private NEPSSHJDownloadServiceImpl impl;

    //static String fileName = "KN_1689705__000001927-002511602.mp4";
    //static String fileName = "npo-1dvr__2019-04-01T071443751-2019-04-01T071446102.mp4";
    //String fileName = "VPWON_1265965__000414370-000917470.mp4";
    static String fileName = "POW_04596569__003141869-005019605.mp4";

    static String testDest = "/tmp/test.mp4";

    @BeforeEach
    public void setup() {
        impl = new NEPSSHJDownloadServiceImpl(NEPTest.PROPERTIES);
    }

    @Test
    public void client() throws IOException {
        log.info("{}", impl);
        try (SSHClient client = impl.createClient()) {
            log.info("Client {}", client);
            try (final SFTPClient sftp = client.newSFTPClient()) {

                for (RemoteResourceInfo s : sftp.ls("/")) {
                    log.info("{}", s);

                }
            }
        }
    }

    @Test
    public void createFile() throws Exception {
        Instant start = Instant.now();

        File dest = new File(testDest);
        impl.download("", fileName, wrapException(() -> Files.newOutputStream(dest.toPath())), (fc) -> NEPDownloadService.Proceed.TRUE);

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
        try (SSHClient client = impl.createClient();
             SFTPClient sftp = client.newSFTPClient()) {

            if (!simple) {

                try (final RemoteFile handle = sftp.open(fileName, EnumSet.of(OpenMode.READ));
                     final InputStream in = handle.new ReadAheadRemoteFileInputStream(32);
                     final FileOutputStream outputStream = new FileOutputStream(testDest)) {
                    long size = IOUtils.copy(in, outputStream, 1024 * 10);
                    log.info("Ready with {} bytes ({})", size, FileSizeFormatter.DEFAULT.formatSpeed(size, start));
                }
            } else {
                sftp.get(fileName, new FileSystemFile(new File(testDest)));
            }
        }

        log.info("Duration {}", Duration.between(start, Instant.now()));




    }

    @Test
    public void testSshjAvailability() throws IOException, InterruptedException {

        impl.checkAvailabilityAndConsume("", fileName, Duration.ofSeconds(10),
            (fd) -> {
                log.info("found {}", fd);
                return NEPDownloadService.Proceed.TRUE;

            }, (handle) -> {});

    }


    @Test
    @Disabled("This actually does something")
    @SneakyThrows
    public void async() {
        List<ForkJoinTask<?>> tasks = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(checkAvailability("bestaatniet." + i)));

        }
        for (ForkJoinTask<?> task : tasks) {
            task.get();
        }
        tasks.clear();
        for (int i = 0; i < 10; i++) {
            tasks.add(ForkJoinPool.commonPool().submit(checkAvailability("bestaatniet." + i)));
        }
        for (ForkJoinTask<?> t : tasks) {
            t.get();
        }
    }


    public Runnable checkAvailability(String file) {
        return () -> {
            Instant start = Instant.now();
            try {
                impl.checkAvailabilityAndConsume("", file, Duration.ofMinutes(2), (dc) -> NEPDownloadService.Proceed.TRUE, (rf) -> {});
            } catch (IOException | InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            log.info("Took {}", Duration.between(start, Instant.now()));
        };
    }


}
