/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.validation.constraints.NotNull;
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
    MEDIA {
        @Override
        public String toString() {
            return "Alle media";
        }

        @Override
        public MediaObject getMediaInstance() {
            throw new RuntimeException("Not possible to make instances of " + this);

        }

        @Override
        public Class<MediaObject> getMediaObjectClass() {
            return MediaObject.class;
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
                        Arrays.stream(GroupType.values())),
                    Arrays.stream(SegmentType.values())
                ).collect(Collectors.toList());

        }


    },

    PROGRAM {
        @Override
        public String toString() {
            return "Programma";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            return p;
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


    BROADCAST {
        @Override
        public String toString() {
            return "Uitzending";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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
    CLIP {
        @Override
        public String toString() {
            return "Clip";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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
    STRAND {
        @Override
        public String toString() {
            return "Koepelprogramma";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.STRAND;

        }
    },
    TRAILER {
        @Override
        public String toString() {
            return "Trailer";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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
    MOVIE {
        @Override
        public String toString() {
            return "Film";
        }

        @Override
        public Program getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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


    GROUP {
        @Override
        public String toString() {
            return "Groep";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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

    SERIES {
        @Override
        public String toString() {
            return "Serie";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            g.setType(getSubType());
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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
    SEASON {
        @Override
        public String toString() {
            return "Seizoen";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            g.setType(getSubType());
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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
    UMBRELLA {
        @Override
        public String toString() {
            return "Paraplu";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            g.setType(getSubType());
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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
    ARCHIVE {
        @Override
        public String toString() {
            return "Archief";
        }

        @Override
        public Group getMediaInstance() {
            return new Group(getSubType(), false);
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
        }


        @Override
        public GroupType getSubType() {
            return GroupType.ARCHIVE;
        }
    },
    COLLECTION {
        @Override
        public String toString() {
            return "Collectie";
        }

        @Override
        public Group getMediaInstance() {
            return new Group(getSubType(), false);
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
        }

        @Override
        public GroupType getSubType() {
            return GroupType.COLLECTION;

        }
    },
    PLAYLIST {
        @Override
        public String toString() {
            return "Speellijst";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            g.setType(GroupType.valueOf(this.name()));
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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
    ALBUM {
        @Override
        public String toString() {
            return "Album";
        }

        @Override
        public Group getMediaInstance() {
            Group g = new Group();
            g.setType(GroupType.valueOf(this.name()));
            return g;
        }

        @Override
        public Class<Group> getMediaObjectClass() {
            return Group.class;
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
    SEGMENT {
        @Override
        public String toString() {
            return "Segment";
        }

        @Override
        public Segment getMediaInstance() {
            Segment s = new Segment();
            return s;
        }

        @Override
        public Class<Segment> getMediaObjectClass() {
            return Segment.class;
        }

        @Override
        public SegmentType getSubType() {
            return SegmentType.SEGMENT;

        }

        @Override
        public List<SubMediaType> getSubTypes() {
            return Arrays.asList(SegmentType.values());
        }
    },
    VISUALRADIOSEGMENT {
        @Override
        public String toString() {
            return "Visual radio segment";
        }

        @Override
        public Segment getMediaInstance() {
            Segment s = new Segment();
            return s;
        }

        @Override
        public Class<Segment> getMediaObjectClass() {
            return Segment.class;
        }

        @Override
        public SegmentType getSubType() {
            return SegmentType.VISUALRADIOSEGMENT;

        }

        @Override
        public List<SubMediaType> getSubTypes() {
            return Arrays.asList(SegmentType.values());
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

    TRACK {
        @Override
        public String toString() {
            return "Track";
        }

        @Override
        public MediaObject getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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
    VISUALRADIO {
        @Override
        public String toString() {
            return "Visual radio";
        }

        @Override
        public MediaObject getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
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
    /**
     * @since 2.1
     */
    RECORDING {
        @Override
        public MediaObject getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
        }

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
    PROMO {
        @Override
        public String toString() {
            return "Promo";
        }

        @Override
        public boolean hasSegments() {
            return false;
        }

        @Override
        public MediaObject getMediaInstance() {
            Program p = new Program();
            p.setType(getSubType());
            return p;
        }

        @Override
        public ProgramType getSubType() {
            return ProgramType.PROMO;

        }

    };


    public abstract MediaObject getMediaInstance();



    public final String getMediaClass() {
        return getMediaObjectClass().getSimpleName();
    }

    public Class<? extends MediaObject> getMediaObjectClass() {
        return Program.class;
    }


    public boolean hasSegments() {
        return false;
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
            return new MediaType[]{};
        }

        return new MediaType[]{MediaType.SERIES, MediaType.SEASON};
    }

    public final boolean hasEpisodes() {
        return getSubType() != null && getSubType().canContainEpisodes();
    }

    public MediaType[] preferredEpisodeTypes() {
        if(!hasEpisodes()) {
            return new MediaType[]{};
        }

        return new MediaType[]{MediaType.BROADCAST};
    }

    public MediaType[] allowedEpisodeTypes() {
        if(!hasEpisodes()) {
            return new MediaType[]{};
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


    @NotNull
    public static MediaType getMediaType(MediaObject media) {
        SubMediaType type = media.getType();
        return type == null ? null : type.getMediaType();
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
            return c.toArray(new Class<?>[c.size()]);
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
