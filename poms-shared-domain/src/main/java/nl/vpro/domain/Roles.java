package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class Roles {


    /**
     * This is the default role of normal users. They have access, and can only write content of their own broadcaster(s)
     */
    public static final String USER_ROLE = "ROLE_MEDIA_USER";

    /**
     * A super user has user access to nodes of every broadcaster.
     */
    public static final String SUPERUSER_ROLE = "ROLE_MEDIA_SUPERUSER";

    /**
     * A super process is like a super user
     */
    public static final String SUPERPROCESS_ROLE = "ROLE_MEDIA_SUPERPROCESS";


    /**
     * A super-admin may do (nearly) everything
     */
    public static final String SUPERADMIN_ROLE = "ROLE_MEDIA_SUPERADMIN";


    /**
     * This is a system role which is assigned to one user which may do stuff 'on behalf' of other users.  See the RunAs-services.
     */
    public static final String RUNAS_ROLE = "ROLE_RUNAS";


    public static final String SCREEN_SUPERADMIN_ROLE = "ROLE_SCREEN_SUPERADMIN";

    /**
     * Used by junction only. Has the right to add locations to any object.
     */
    public static final String ENCODER_SUPERPROCESS_ROLE = "ROLE_ENCODER_SUPERPROCESS";


    public static final String PUBLISHER_ROLE = "ROLE_MEDIA_PUBLISHER";

    public static final String SCREEN_PUBLISHER_ROLE = "ROLE_SCREEN_PUBLISHER";
    /**
     * Support are people at NPO-helpdesk who can see everything, including deleted record, but edit nothing. (MSE-2015)
     */
    public static final String SUPPORT_ROLE = "ROLE_MEDIA_SUPPORT";

    /**
     * Process roles may use the backend api.
     */
    public static final String PROCESS_ROLE = "ROLE_MEDIA_PROCESS";

    /**
     * A limited role. May only edit objects originally created by theirselves.
     */
    public static final String EXTERNALUSER_ROLE = "ROLE_MEDIA_EXTERNALUSER";

    /**
     * Has more rights on schedule event data
     */
    public static final String ARCHIVIST_ROLE = "ROLE_MEDIA_ARCHIVIST";

    /**
     * Have the right to upload files as locations.
     */
    public static final String UPLOAD_ROLE = "ROLE_MEDIA_UPLOAD";


    public static final String MERGE_SERIES_ROLE = "ROLE_MEDIA_MERGESERIES";
    public static final String MERGE_EPISODE_ROLE = "ROLE_MEDIA_MERGEEPISODE";
    public static final String MERGE_ALL_ROLE = "ROLE_MEDIA_MERGEALL";

    public static final String TRANSLATOR_ROLE = "ROLE_MEDIA_TRANSLATOR";

    /**
     * Role for users that are allowed to edit MIS owned fields, do note that they still need the regular permssions to actually do so.
     */
    public static final String MIS_ROLE = "ROLE_MEDIA_MIS";


    /**
     * The system role is only assigned via {@link nl.vpro.domain.user.UserService#systemAuthenticate)}
     */
    public static final String SYSTEM_ROLE = "ROLE_SYSTEM";

    public static final String API_USER = "hasAnyRole('" + USER_ROLE + "','ROLE_API_CLIENT','ROLE_API_USER','ROLE_API_SUPERUSER','ROLE_API_SUPERCLIENT')";
    public static final String API_CHANGES_USER = "hasAnyRole('" + USER_ROLE + "','ROLE_API_CHANGES_CLIENT', 'ROLE_API_CHANGES_SUPERCLIENT', 'ROLE_API_USER', 'ROLE_API_SUPERUSER')";

    public static final String PAGES_USER = "ROLE_PAGES_USER";

    public static final String PAGES_SUPERUSER= "ROLE_PAGES_SUPERUSER";

    public static final String PAGES_PROCESS = "ROLE_PAGES_PROCESS";

    public static final String PAGES_SUPERPROCESS = "ROLE_PAGES_SUPERPROCESS";


}
