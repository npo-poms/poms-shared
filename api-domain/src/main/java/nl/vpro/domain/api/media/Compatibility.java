package nl.vpro.domain.api.media;

import nl.vpro.util.IntegerVersion;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class Compatibility {

    private static final ThreadLocal<IntegerVersion> compatibility = ThreadLocal.withInitial(() -> null);

    private Compatibility() {
    }


    public static void setCompatibility(IntegerVersion value) {
        compatibility.set(value);
    }

    public static void clearCompatibility() {
        compatibility.remove();
    }


    public static boolean compatibleBefore(IntegerVersion version) {
        if (compatibility.get() == null) {
            return true;
        }
        return version.isBefore(compatibility.get());
    }

    public static boolean versionBefore(IntegerVersion version) {
        if (compatibility.get() == null) {
            return false;
        }
        return compatibility.get().isBefore(version);
    }
}
