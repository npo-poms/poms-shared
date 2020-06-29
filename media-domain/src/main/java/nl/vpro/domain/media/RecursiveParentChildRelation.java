package nl.vpro.domain.media;

import java.util.Set;

/**
 *
 *
 * @author Michiel Meeuwissen
 * @since 5.13.1
 */
public interface RecursiveParentChildRelation extends ParentChildRelation {

	Set<RecursiveMemberRef> getMemberOf();

	Set<RecursiveMemberRef> getEpisodeOf();

	RecursiveMemberRef getSegmentOf();


}
