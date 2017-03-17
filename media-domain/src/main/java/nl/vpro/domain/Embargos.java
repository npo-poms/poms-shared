package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
public class Embargos {

    public static void copy(Embargo from, Embargo to) {
        to.setPublishStartInstant(from.getPublishStartInstant());
        to.setPublishStopInstant(from.getPublishStopInstant());
    }
}
