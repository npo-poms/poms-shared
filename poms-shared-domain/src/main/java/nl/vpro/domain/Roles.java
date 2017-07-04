package nl.vpro.domain;

/**
 * @author Michiel Meeuwissen
 * @since 5.4
 */
public class Roles {
    /**
     * A super process is like a super user
     */
    public static final String SUPERPROCESS_ROLE = "ROLE_MEDIA_SUPERPROCESS";
    /**
     * A super user has user access to nodes of every broadcaster.
     */

    public static final String SUPERUSER_ROLE = "ROLE_MEDIA_SUPERUSER";
    public static final String USER_ROLE = "ROLE_MEDIA_USER";
    /**
     * A super-admin may do (nearly) everything
     */
    public static final String SUPERADMIN_ROLE = "ROLE_MEDIA_SUPERADMIN";
    public static final String RUNAS_ROLE = "ROLE_RUNAS";
    public static final String SCREEN_SUPERADMIN_ROLE = "ROLE_SCREEN_SUPERADMIN";
    public static final String ENCODER_SUPERPROCESS_ROLE = "ROLE_ENCODER_SUPERPROCESS";
    public static final String PUBLISHER_ROLE = "ROLE_MEDIA_PUBLISHER";
    /**
     * Support are people at NPO-helpdesk who can see everything, including deleted record, but edit nothing. (MSE-2015)
     */
    public static final String SUPPORT_ROLE = "ROLE_MEDIA_SUPPORT";
    public static final String PROCESS_ROLE = "ROLE_MEDIA_PROCESS";
    public static final String EXTERNALUSER_ROLE = "ROLE_MEDIA_EXTERNALUSER";
    public static final String ARCHIVIST_ROLE = "ROLE_MEDIA_ARCHIVIST";
    public static final String UPLOAD_ROLE = "ROLE_MEDIA_UPLOAD";
    public static final String ENCODER_ROLE = "ROLE_MEDIA_ENCODER";
    public static final String MERGE_SERIES_ROLE = "ROLE_MEDIA_MERGESERIES";
    public static final String MERGE_EPISODE_ROLE = "ROLE_MEDIA_MERGEEPISODE";
    public static final String MERGE_ALL_ROLE = "ROLE_MEDIA_MERGEALL";


    // Role for users that are allowed to edit MIS owned fields, do note that they still need the regular permssions to actually do so.
    public static final String MIS_ROLE = "ROLE_MEDIA_MIS";


    /**
     * The system role is only assigned via {@link nl.vpro.domain.user.UserService#systemAuthenticate)}
     */
    public static final String SYSTEM_ROLE = "ROLE_SYSTEM";


}
