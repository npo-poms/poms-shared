package nl.vpro;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.vpro.util.IntegerVersion;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class VersionService {

    private static final String FALLBACK = "7.9.0";

    private static String version;

    private static IntegerVersion integerVersion;

    private VersionService() {

    }

    public static String version() {
        if (version == null) {
            try {
                InputStream inputStream = VersionService.class.getClassLoader().getResourceAsStream("/maven.properties");
                if (inputStream != null) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    version = properties.getProperty("git.build.version");
                } else {
                    log.warn("Could not find /maven.properties");
                    version = FALLBACK;
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                return FALLBACK;
            }
        }
        return version;
    }

    public static IntegerVersion integerVersion() {
        if (integerVersion == null) {
            String version = version();
            Matcher matcher = Pattern.compile("([0-9]+(?:\\.[0-9]+(?:\\.[0-9]+)?)?).*").matcher(version);
            if (matcher.matches()) {
                integerVersion = new IntegerVersion(matcher.group(1));
            }
        }
        return integerVersion;
    }

    /**
     * E.g. in test cases in may be useful to fix version.
     */
    public static void setVersion(IntegerVersion version) {
        if (! Objects.equals(VersionService.integerVersion(), version)) {
            log.info("Setting version {} -> {}", VersionService.version, version);
            VersionService.integerVersion = version;
            VersionService.version = String.valueOf(version);
        }
    }

     /**
     * E.g. in test cases in may be useful to fix version.
     */
    public static void setVersion(int ... version) {
        setVersion(IntegerVersion.of(version));
    }
}
