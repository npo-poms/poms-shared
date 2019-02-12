package nl.vpro.domain;

import java.time.Instant;

/**
 *
 * @author Michiel Meeuwissen
 * @since 5.10
 */
public class Updatables {

    public static void fillFor(Updatable accountable, Instant now) {
        accountable.setLastModifiedInstant(now);
        if (accountable.getCreationInstant() == null) {
            accountable.setCreationInstant(now);
        }
    }

     public static void copyFrom(Updatable source, Updatable target) {
        target.setLastModifiedInstant(source.getLastModifiedInstant());
        target.setCreationInstant(source.getCreationInstant());
    }

    public static  void copyFromIfTargetUnset(Updatable source, Updatable target) {

        if (target.getLastModifiedInstant() == null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (target.getCreationInstant() == null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }


    public static void copyFromIfSourceSet(Updatable source, Updatable target) {
        if (source.getLastModifiedInstant() != null) {
            target.setLastModifiedInstant(source.getLastModifiedInstant());
        }
        if (source.getCreationInstant() != null) {
            target.setCreationInstant(source.getCreationInstant());
        }
    }

}
