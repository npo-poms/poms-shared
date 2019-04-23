package nl.vpro.domain.media.support;

/**
 * @author Michiel Meeuwissen
 * @since 5.11
 */
public class ImageBackendServiceHolder {


    private static ImageBackendService instance;

    public static ImageBackendService getInstance() {
        return instance;
    }
    public static void setInstance(ImageBackendService instance) {
        ImageBackendServiceHolder.instance = instance;
    }

}
