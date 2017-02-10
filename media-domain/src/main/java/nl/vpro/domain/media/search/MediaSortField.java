package nl.vpro.domain.media.search;

import nl.vpro.domain.media.support.OwnerType;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public interface MediaSortField extends SortField {
    Enum sortTitle = Enum.sortTitle;
    Enum mid = Enum.mid;
    Enum type = Enum.type;
    Enum mediaType = Enum.mediaType;
    Enum sortDate = Enum.sortDate;
    Enum lastModified = Enum.lastModified;
    Enum creationDate = Enum.creationDate;
    Enum publishStop = Enum.publishStop;
    Enum publishStart = Enum.publishStart;
    Enum lastPublished = Enum.lastPublished;
    Enum lastModifiedBy = Enum.lastModifiedBy;
    Enum createdBy = Enum.createdBy;
    Enum locations = Enum.locations;
    Enum memberofCount = Enum.memberofCount;
    Enum episodeofCount = Enum.episodeofCount;


    enum Enum implements MediaSortField {
        sortTitle(Type.STRING),

        mid(Type.STRING),
        type(Type.STRING),
        mediaType(Type.STRING),

        sortDate(Type.LONG),
        lastModified(Type.LONG),
        creationDate(Type.LONG),
        publishStop(Type.LONG),
        publishStart(Type.LONG),
        lastPublished(Type.LONG),


        lastModifiedBy(Type.STRING),
        createdBy(Type.STRING),

        locations(Type.LONG) {
            @Override
            public String field() {
                return "locationCount";
            }
        },
        memberofCount(Type.LONG),
        episodeofCount(Type.LONG);
        private final Type t;

        Enum(Type type) {
            this.t = type;
        }

        @Override
        public Type type() {
            return t;
        }
    }

    static MediaSortField valueOfNullable(String string) {
        if (string == null) {
            return null;
        }
        return Enum.valueOf(string);
    }

    static MediaSortField valueOf(String string) {
        return Enum.valueOf(string);
    }

    static MediaSortField valueOf(MediaSortField.Enum e, OwnerType ot) {
        return new MediaSortField() {
            @Override
            public String name() {
                return e.name() + "_" + ot.name();

            }

            @Override
            public Type type() {
                return e.type();

            }
        };
    }
}
