package nl.vpro.domain.media;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.13.1
 */
public interface RecursiveParentChildRelation extends ParentChildRelation {

    SortedSet<RecursiveMemberRef> getMemberOf();

    void setMemberOf(SortedSet<RecursiveMemberRef> memberOf);

    default SortedSet<RecursiveMemberRef> getOrCreateMemberOf() {
        if (getMemberOf() == null) {
            setMemberOf(new TreeSet<>());
        }
        return getMemberOf();
    }

    SortedSet<RecursiveMemberRef> getEpisodeOf();

    void setEpisodeOf(SortedSet<RecursiveMemberRef> episodeOf);

    default SortedSet<RecursiveMemberRef> getOrCreateEpisodeOf() {
        if (getEpisodeOf() == null) {
            setEpisodeOf(new TreeSet<>());
        }
        return getEpisodeOf();
    }

    default SortedSet<RecursiveMemberRef> getOrCreate(StandaloneMemberRef.ObjectType type) {
        return switch (type) {
            case memberRef -> getOrCreateMemberOf();
            case episodeRef -> getOrCreateEpisodeOf();
        };
    }


    RecursiveMemberRef getSegmentOf();

    void setSegmentOf(RecursiveMemberRef recursiveMemberRef);


}
