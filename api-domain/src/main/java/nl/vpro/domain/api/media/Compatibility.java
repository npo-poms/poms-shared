package nl.vpro.domain.api.media;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public class Compatibility {


    private static final ThreadLocal<Float> compatibility = ThreadLocal.withInitial(() -> null);

    public static void setCompatibility(float value) {
        compatibility.set(value);
    }

    public static void clearCompatibility() {
        compatibility.remove();
    }


    public static boolean compatibleBefore(float version) {
        if (compatibility.get() == null) {
            return true;
        }
        return version < compatibility.get();
    }

    public static boolean versionBefore(float version) {
        if (compatibility.get() == null) {
            return false;
        }
        return compatibility.get() < version;
    }
}
