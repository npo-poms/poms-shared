package nl.vpro.domain.media;

import nl.vpro.domain.user.*;


/**
 * These are the filters that are used in the hibernate filters.
 *
 * @since 7.10
 */
public class MediaObjectFilters {

    /**
     * Normal users normally have this filter enabled, and won't see deleted objected.
     */
    public static final String DELETED_FILTER = "deletedFilter";
    public static final String INVERSE_DELETED_FILTER = "inverseDeletedFilter";
    /**
     * This filter is enabled during the publication process, things that are under embargo will not be queried.
     */
    public static final String PUBLICATION_FILTER = "publicationFilter";
    public static final String INVERSE_PUBLICATION_FILTER = "inversePublicationFilter";
    /**
     * Normally on the poms backend things under embargo are visible. The exception are CLIPs of different broadcasters.
     * They may not be visible by other people because they may contain the result of Wie is de Mol? or so.
     */
    public static final String EMBARGO_FILTER = "embargoFilter";
    public static final String INVERSE_EMBARGO_FILTER = "inverseEmbargoFilter";
    /**
     * This filter is enabled for 'third party users'. They may only see objects that are of one of their organizations
     * ({@link Broadcaster broadcasters}, {@link Portal portals} or {@link ThirdParty third parties})
     */
    public static final String ORGANIZATION_FILTER = "organizationFilter";


    /**
     * This is used to filter {@link RelationDefinition}, normal users only see the ones that are associated to their broadcaster.
     */
    public static final String BROADCASTER_FILTER = "broadcasterFilter";

    protected static final String ORGANIZATION_FILTER_CONDITION = """
           0 < (
    (select count(*) from mediaobject_portal o where o.mediaobject_id = id and o.portals_id in (:organizations))
     +
    (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:organizations))
     +
    (select count(*) from mediaobject_thirdparty o where o.mediaobject_id = id and o.thirdparties_id in (:organizations))
    )
        """;

    private MediaObjectFilters() {
        // no instances for this
    }

}
