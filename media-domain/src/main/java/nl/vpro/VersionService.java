package nl.vpro;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
@Slf4j
public class VersionService {

    private static final String FALLBACK = "5.8";

    private static String version;

    private static Float floatVersion;

    public static String version() {
        if (version == null) {
            try {
                InputStream inputStream = VersionService.class.getClassLoader().getResourceAsStream("/maven.properties");
                if (inputStream != null) {
                    Properties properties = new Properties();
                    properties.load(inputStream);
                    version = properties.getProperty("media.version");
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

    public static Float floatVersion() {
        if (floatVersion == null) {
            String version = version();
            Matcher matcher = Pattern.compile("(\\d+\\.?\\d*).*").matcher(version);
            if (matcher.matches()) {
                floatVersion = Float.parseFloat(matcher.group(1));
            }
        }
        return floatVersion;
    }

    /**
     * E.g. in test cases in may be usefull to fix version.
     */
    public static void setVersion(Float version) {
        if (! Objects.equals(VersionService.floatVersion(), version)) {
            log.info("Setting version {} -> {}", VersionService.version, version);
            VersionService.floatVersion = version;
            VersionService.version = String.valueOf(version);
        }


    }
}
