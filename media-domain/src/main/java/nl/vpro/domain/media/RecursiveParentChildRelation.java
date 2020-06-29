package nl.vpro.domain.media;

import java.util.SortedSet;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.13.1
 */
public interface RecursiveParentChildRelation extends ParentChildRelation {

	SortedSet<RecursiveMemberRef> getMemberOf();

	SortedSet<RecursiveMemberRef> getEpisodeOf();

	RecursiveMemberRef getSegmentOf();


}
