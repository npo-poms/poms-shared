package nl.vpro.domain.media;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.13.1
 */
public interface RecursiveParentChildRelation extends ParentChildRelation {

	List<MemberRef> getMemberOf();

	List<MemberRef> getEpisodeOf();

	ParentRef getSegmentOf();


}
