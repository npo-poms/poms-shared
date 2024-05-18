package nl.vpro.domain.media;

import nl.vpro.domain.user.*;


/**
 * These are the filters that are used in POMS.
 * <p>
 * There are three types of filters:
 * <ol>
 *     <li>Filtering out <em>deleted</em> stuff (this is used by the publisher, and also by 'normal' GUI users)</li>
 *     <li>Filtering out <em>unpublished</em> stuff (this is used by the publisher)</li>
 *     <li>Filtering out <em>unpublished</em> CLIPs that are of another broadcaster. Used in the GUI only.</li>
 * </ol>
 *
 * @since 7.10
 */
public class MediaObjectFilters {

    public static final String PARAMETER_BROADCASTERS = "broadcasters";
    public static final String PARAMETER_ORGANIZATIONS = "organizations";

    /**
     * Normal users normally have this filter enabled, and won't see deleted objects.
     */
    public static final String DELETED_FILTER = "deletedFilter";
    public static final String SE_DELETED_FILTER = "seDeletedFilter";
    public static final String DELETED_FILTER_CONDITION = """
            (workflow NOT IN ('MERGED', 'FOR_DELETION', 'DELETED', 'TEMPORARY') and mergedTo_id is null)
        """;

    public static final String MR_DELETED_FILTER = "mrDeletedFilter";
    public static final String MR_DELETED_FILTER_CONDITION =
        """
     (select m.workflow from mediaobject m where m.id = owner_id and m.mergedTo_id is null) NOT IN ('MERGED', 'FOR_DELETION', 'DELETED', 'TEMPORARY')
     AND
     (select m.workflow from mediaobject m where m.id = member_id and m.mergedTo_id is null) NOT IN ('MERGED', 'FOR_DELETION', 'DELETED', 'TEMPORARY')
     """;
    /**
     * This filter is enabled during the publication process, things that are under embargo will not be queried.
     */

    public static final String PUBLICATION_FILTER = "publicationFilter";

    public static final String PUBLICATION_FILTER_CONDITION_RESTRICTIONS =
        """
        (start is null or start <= now())
         and
        (stop is null or stop > now())
        """;


     public static final String PUBLICATION_FILTER_CONDITION_PUBLISHABLES =
        """
        (publishStart is null or publishStart <= now())
        and
        (publishStop is null or publishStop > now())
        """;

    /**
     * This filter is enabled during the publication process, things that are under embargo will not be queried (MemberRef version)
     */
    public static final String MR_PUBLICATION_FILTER = "mrPublicationFilter";

    public static final String MR_PUBLICATION_FILTER_CONDITION =
        """
        (
              (
               (select m.publishStart from mediaobject m where m.id = member_id) is null
                or now() > (select m.publishStart from mediaobject m where m.id = member_id)               )
               and (
                (select m.publishStop from mediaobject m where m.id = member_id) is null
                or now() < (select m.publishStop from mediaobject m where m.id = member_id)
              )
              and
              (
                (select m.publishStart from mediaobject m where m.id = owner_id) is null
                or now() > (select m.publishStart from mediaobject m where m.id = owner_id)
               )
               and (
                (select m.publishStop from mediaobject m where m.id = owner_id) is null
                or now() < (select m.publishStop from mediaobject m where m.id = owner_id)
              )
       )
        """;

    /**
     * Normally on the poms backend things under embargo are visible. The exception are CLIPs of different broadcasters.
     * They may not be visible by other people because they may contain the result of Wie is de Mol? or so.
     */
    public static final String EMBARGO_FILTER = "embargoFilter";
    public static final String EMBARGO_FILTER_CONDITION = """
            (
               publishStart is null
               or
               publishStart < now()
               or
               (select p.type from program p where p.id = id) != 'CLIP'
               or
               (0 < (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:broadcasters)))
            )
        """;
    public static final String MR_EMBARGO_FILTER = "mrEmbargoFilter";

    public static final String MR_EMBARGO_FILTER_CONDITION =
            """
        ((
           (select m.publishStart from mediaobject m where m.id = owner_id) is null
           or now() > (select m.publishStart from mediaobject m where m.id = owner_id)
           or 'CLIP' != (select p.type from program p where p.id = owner_id)
           or 0 < (select count(*) from mediaobject_broadcaster b where b.mediaobject_id = owner_id and b.broadcasters_id in (:broadcasters))
        ) AND
        (
           (select m.publishStart from mediaobject m where m.id = member_id) is null
           or now() > (select m.publishStart from mediaobject m where m.id = member_id)
           or 'CLIP' != (select p.type from program p where p.id = member_id)
           or 0 < (select count(*) from mediaobject_broadcaster b where b.mediaobject_id = member_id and b.broadcasters_id in (:broadcasters))
        ))
        """;
    /**
     * This filter is enabled for 'third party users'. They may only see objects that are of one of their organizations
     * ({@link Broadcaster broadcasters}, {@link Portal portals} or {@link ThirdParty third parties})
     */
    public static final String ORGANIZATION_FILTER = "organizationFilter";


    protected static final String ORGANIZATION_FILTER_CONDITION = """
           0 < (
    (select count(*) from mediaobject_portal o where o.mediaobject_id = id and o.portals_id in (:organizations))
     +
    (select count(*) from mediaobject_broadcaster o where o.mediaobject_id = id and o.broadcasters_id in (:organizations))
     +
    (select count(*) from mediaobject_thirdparty o where o.mediaobject_id = id and o.thirdparties_id in (:organizations))
    )
        """;
    /**
     * This is used to filter {@link RelationDefinition}, normal users only see the ones that are associated to their broadcaster.
     */
    public static final String BROADCASTER_FILTER = "broadcasterFilter";



    private MediaObjectFilters() {
        // no instances for this
    }

}
