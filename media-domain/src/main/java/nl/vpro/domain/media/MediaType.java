/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.checkerframework.checker.nullness.qual.NonNull;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * This class provides a combined view on all program and group types and their
 * configuration options. Both program and groups have a type property, but there
 * is now way to add this property to their abstract super class while providing two
 * implementations of a generic super type.
 */
@XmlEnum
@XmlType(name = "mediaTypeEnum")
public enum MediaType {

    /**
     * The abstract type denoting every possible media type
     */
    MEDIA(MediaObject.class) {
        @Override
        public String toString() {
            return "Alle media";
        }


        @Override
        public SubMediaType getSubType() {
            return null;

        }

        @Override
        public List<SubMediaType> getSubTypes () {
            return
                Stream.concat(
                    Stream.concat(
                        Arrays.stream(ProgramType.values()),
                        Arrays.stream(GroupType.values())
                    ),
                    Arrays.stream(SegmentType.values())
                ).collect(Collectors.toList());

        }


    },

     /**
     * The abstract type denoting every type of a {@link Program}
     */
    PROGRAM(Program.class) {
        @Override
        public String toString() {
            return "Programma";
        }

        @Override
        public ProgramType getSubType() {
            return null;
        }


        @Override
        public List<SubMediaType> getSubTypes() {
            return Arrays.asList(ProgramType.values());
        }

        @Override
        public boolean hasSegments() {
            return true;
        }
    },


    BROADCAST(Program.class) {
        @Override
        public String toString() {
            return "Uitzending";
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.BROADCAST;
        }

        @Override
        public boolean hasSegments() {
            return true;
        }


    },
    CLIP(Program.class) {
        @Override
        public String toString() {
            return "Clip";
        }

        @Override
        public boolean hasSegments() {
            return true;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.CLIP;
        }


    },
    STRAND(Program.class) {
        @Override
        public String toString() {
            return "Koepelprogramma";
        }


        @Override
        public ProgramType getSubType() {
            return ProgramType.STRAND;

        }
    },
    TRAILER(Program.class) {
        @Override
        public String toString() {
            return "Trailer";
        }

        @Override
        public boolean hasSegments() {
            return true;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.TRAILER;

        }


    },
    MOVIE(Program.class) {
        @Override
        public String toString() {
            return "Film";
        }


        @Override
        public boolean hasSegments() {
            return true;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.MOVIE;

        }
    },


    /**
     * The abstract type denoting every type of a {@link Group}
     */

    GROUP(Group.class) {
        @Override
        public String toString() {
            return "Groep";
        }



        @Override
        public GroupType getSubType() {
            return null;
        }

        @Override
        public List<SubMediaType> getSubTypes() {
            return Arrays.asList(GroupType.values());
        }
    },

    SERIES(Group.class) {
        @Override
        public String toString() {
            return "Serie";
        }

        @Override
        public GroupType getSubType() {
            return GroupType.SERIES;
        }

        @Override
        public boolean hasOrdering() {
            return true;
        }
    },
    SEASON(Group.class) {
        @Override
        public String toString() {
            return "Seizoen";
        }

        @Override
        public boolean hasOrdering() {
            return true;
        }

        @Override
        public GroupType getSubType() {
            return GroupType.SEASON;
        }
    },
    UMBRELLA(Group.class) {
        @Override
        public String toString() {
            return "Paraplu";
        }


        @Override
        public boolean hasOrdering() {
            return true;
        }

        @Override
        public GroupType getSubType() {
            return GroupType.UMBRELLA;
        }
    },
    // MSE-1453
    @Deprecated
    ARCHIVE(Group.class) {
        @Override
        public String toString() {
            return "Archief";
        }

        @Override
        public GroupType getSubType() {
            return GroupType.ARCHIVE;
        }
    },
    COLLECTION(Group.class) {
        @Override
        public String toString() {
            return "Collectie";
        }


        @Override
        public GroupType getSubType() {
            return GroupType.COLLECTION;

        }
    },
    PLAYLIST(Group.class) {
        @Override
        public String toString() {
            return "Speellijst";
        }


        @Override
        public boolean hasOrdering() {
            return true;
        }

        @Override
        public GroupType getSubType() {
            return GroupType.PLAYLIST;

        }
    },
    ALBUM(Group.class) {
        @Override
        public String toString() {
            return "Album";
        }


        @Override
        public boolean hasOrdering() {
            return true;
        }

        @Override
        public GroupType getSubType() {
            return GroupType.ALBUM;

        }
    },
    SEGMENT(Segment.class) {
        @Override
        public String toString() {
            return "Segment";
        }


        @Override
        public SegmentType getSubType() {
            return SegmentType.SEGMENT;

        }
    },
/*
    COLLECTION {
        @Override
        public String toString() {
            return "Collectie";
        }

        public String getMediaClass() {
            return Group.class.getSimpleName();
        }
    },
    COMPILATION {
        @Override
        public String toString() {
            return "Compilatie";
        }
        @Override
        public String getMediaClass() {
            return Group.class.getSimpleName();
        }

    },
    CONCEPT {
        @Override
        public String toString() {
            return "Concept";
        }

        public String getMediaClass() {
            return Group.class.getSimpleName();
        }
    },
    SHOW {
        @Override
        public String toString() {
            return "Show";
        }

        public String getMediaClass() {
            return Group.class.getSimpleName();
        }

    },
    */

    TRACK(Program.class) {
        @Override
        public String toString() {
            return "Track";
        }

        @Override
        public MediaType[] allowedEpisodeOfTypes() {
            return new MediaType[]{MediaType.ALBUM};
        }

        @Override
        public boolean hasSegments() {
            return false;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.TRACK;

        }

    },
    VISUALRADIO(Program.class) {
        @Override
        public String toString() {
            return "Visual radio";
        }


        @Override
        public boolean hasSegments() {
            return true;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.VISUALRADIO;

        }
    },

    VISUALRADIOSEGMENT(Segment.class) {
        @Override
        public String toString () {
            return "Visual radio segment";
        }


        @Override
        public SegmentType getSubType () {
            return SegmentType.VISUALRADIOSEGMENT;

        }
    },
    /**
     * The abstract type denoting every type of a {@link Segment}
     */
     SEGMENTTYPE(Segment.class) {
        @Override
        public String toString() {
            return "Segments";
        }


        @Override
        public SegmentType getSubType() {
            return null;

        }

        @Override
        public List<SubMediaType> getSubTypes() {
            return Arrays.asList(SegmentType.values());
        }
    },

    /**
     * @since 2.1
     */
    RECORDING(Program.class) {

        @Override
        public String toString() {
            return "Opname";
        }

        @Override
        public boolean hasSegments() {
            return true;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.RECORDING;

        }
    },
    PROMO(Program.class) {
        @Override
        public String toString() {
            return "Promo";
        }

        @Override
        public boolean hasSegments() {
            return false;
        }



        @Override
        public ProgramType getSubType() {
            return ProgramType.PROMO;

        }

    };


    final Class<? extends MediaObject> clazz;
    final Constructor<?> constructor;
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<Method> setType;

    MediaType(Class<? extends MediaObject> clazz)  {
        this.clazz = clazz;
        if (Modifier.isAbstract(this.clazz.getModifiers())) {
            this.constructor = null;
        } else {
            try {
                this.constructor = clazz.getConstructor();
            } catch (NoSuchMethodException nsme) {
                throw new RuntimeException(nsme);
            }
        }
    }

    protected void setType(MediaObject o) throws InvocationTargetException, IllegalAccessException {
        if (this.setType == null){
            Method st;
            if (this.getSubType() != null) {
                try {
                    st = clazz.getMethod("setType", this.getSubType().getClass());
                } catch (NoSuchMethodException nsme) {
                    st = null;
                }
            } else {
                st = null;
            }
            this.setType = Optional.ofNullable(st);
        }
        if (this.setType.isPresent()) {
            this.setType.get().invoke(o, getSubType());
        }
    }


    public final MediaObject getMediaInstance() {
        try {
            if (constructor == null) {
                throw new RuntimeException("Not possible to make instances of " + this);
            }
            MediaObject o = (MediaObject) constructor.newInstance();
            setType(o);
            return o;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            throw new RuntimeException("For " + this + " ", e);
        }
    }



    public final String getMediaClass() {
        return getMediaObjectClass().getSimpleName();
    }

    public final Class<? extends MediaObject> getMediaObjectClass() {
        return clazz;
    }


    public boolean hasSegments() {
        return false;
    }

    /**
     * @since 5.11
     */
    public boolean canHaveScheduleEvents() {
        return getSubType() != null && getSubType().canHaveScheduleEvents();
    }


    public final boolean hasEpisodeOf() {
        return getSubType() != null && getSubType().hasEpisodeOf();
    }

    public MediaType[] preferredEpisodeOfTypes() {
        if(!hasEpisodeOf()) {
            return new MediaType[]{};
        }

        return new MediaType[]{MediaType.SERIES, MediaType.SEASON};
    }

    public MediaType[] allowedEpisodeOfTypes() {
        if(!hasEpisodeOf()) {
            return new MediaType[0];
        }

        return new MediaType[]{MediaType.SERIES, MediaType.SEASON};
    }

    public final boolean canContainEpisodes() {
        return getSubType() != null && getSubType().canContainEpisodes();
    }

    public MediaType[] preferredEpisodeTypes() {
        if(!canContainEpisodes()) {
            return new MediaType[0];
        }

        return new MediaType[]{MediaType.BROADCAST};
    }

    public MediaType[] allowedEpisodeTypes() {
        if(!canContainEpisodes()) {
            return new MediaType[0];
        }

        return new MediaType[]{MediaType.BROADCAST};
    }

    public boolean hasMemberOf() {
        return true;
    }

    public MediaType[] preferredMemberOfTypes() {
        if(!hasMemberOf()) {
            return new MediaType[]{};
        }

        return new MediaType[]{MediaType.MEDIA};
    }

    public MediaType[] allowedMemberOfTypes() {
        return preferredMemberOfTypes();
    }

    public boolean hasMembers() {
        return true;
    }

    public MediaType[] preferredMemberTypes() {
        if(!hasMembers()) {
            return new MediaType[]{};
        }

        return new MediaType[]{MediaType.MEDIA};
    }

    public MediaType[] allowedMemberTypes() {
        return preferredMemberTypes();
    }

    public boolean hasOrdering() {
        return false;
    }

    public abstract SubMediaType getSubType();

    public List<SubMediaType> getSubTypes() {
        return getSubType() != null ? Collections.singletonList(getSubType()) : null;
    }


    public static MediaType of(String type) {
        return type == null ? null : valueOf(type.toUpperCase());
    }


    @NonNull
    public static MediaType getMediaType(MediaObject media) {
        SubMediaType type = media.getType();
        if (type != null) {
            return type.getMediaType();
        }
        if (media instanceof Program) {
            return MediaType.PROGRAM;
        }
        if (media instanceof Group) {
            return MediaType.GROUP;
        }
        if (media instanceof  Segment) {
            return MediaType.SEGMENT;
        }
        return MediaType.MEDIA;
    }

    @SneakyThrows
    public MediaObject createInstance() {

        Class<? extends MediaObject> clazz = getMediaObjectClass();

        if (Program.class.isAssignableFrom(clazz)) {
            Program o = (Program) clazz.newInstance();
            o.setType((ProgramType) getSubType());
            return o;
        }
        if (Group.class.isAssignableFrom(clazz)) {
            Group o = (Group) clazz.newInstance();
            o.setType((GroupType) getSubType());
            return o;
        }
        if (Segment.class.isAssignableFrom(clazz)) {
            Segment o = (Segment) clazz.newInstance();
            o.setType((SegmentType) getSubType());
            return o;
        }
        throw new IllegalStateException(clazz + " of " + this + " is not a program, group or segment");
    }

    /**
     * Returns all 'leaf' mediaTypes. That are all non abstract instances, that actually have a certain {@link #getSubType()}.
     * @since 5.8
     */
    public static MediaType[] leafValues() {
        return Arrays.stream(values()).filter(f -> f.getSubType() != null).toArray(MediaType[]::new);
    }
    /**
     * @since 5.8
     */
    public static MediaType[] leafValues(Class<? extends MediaObject> clazz) {
        return Arrays.stream(values()).filter(f -> f.getSubType() != null).filter(t -> clazz.isAssignableFrom(t.getMediaObjectClass())).toArray(MediaType[]::new);
    }

    /**
     * @since 2.1
     */
    public static Class<?>[] getClasses(Collection<MediaType> types) {
        if(types == null) {
            return new Class<?>[]{Program.class, Group.class, Segment.class};
        } else {
            Set<Class<?>> c = new HashSet<>();
            for(MediaType type : types) {
                c.add(type.getMediaObjectClass());
            }
            return c.toArray(new Class<?>[0]);
        }
    }

    /**
     * @since 2.1
     */
    public static Collection<MediaType> valuesOf(String types) {
        List<MediaType> result = new ArrayList<>();
        for(String s : types.split(",")) {
            result.add(valueOf(s.trim()));
        }
        return result;
    }


}
