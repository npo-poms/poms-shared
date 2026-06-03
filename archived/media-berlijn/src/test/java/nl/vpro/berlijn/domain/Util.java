package nl.vpro.berlijn.domain;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.nio.file.*;

@Log4j2
public class Util {

    @SneakyThrows
    public static InputStream getTable(String resource) {
        Path path = Paths.get(System.getProperty("user.home")).toAbsolutePath().normalize().resolve("kafka/" + resource);
        if (Files.exists(path)) {
            log.info("Using {}", path);

            return Files.newInputStream(path);
        } else {
            log.info("{} does not exist", path);
            return Util.class.getResourceAsStream(resource);
        }
    }
}
