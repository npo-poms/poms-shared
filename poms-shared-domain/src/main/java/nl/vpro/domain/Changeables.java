package nl.vpro.domain;

import java.time.Instant;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class Changeables {

    public static void fillFor(Changeable accountable, Instant now) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
    }

     public static void copyFrom(Changeable source, Changeable target) {
        target.setLastModifiedInstant(source.getLastModifiedInstant());
        target.setCreationInstant(source.getCreationInstant());
    }

    public static  void copyFromIfTargetUnset(Changeable source, Changeable target) {

        if (target.getLastModifiedInstant() == null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (target.getCreationInstant() == null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    public static void copyFromIfSourceSet(Changeable source, Changeable target) {
        if (source.getLastModifiedInstant() != null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (source.getCreationInstant() != null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }

}
