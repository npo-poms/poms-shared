package nl.vpro.domain;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * See <a href="https://wiki.vpro.nl/display/poms/Gebruikersbeheer#Gebruikersbeheer-Rollen">Gebruikersbeheer</a>
 *
 * @author Michiel Meeuwissen
 * @since 5.4
 */
@Slf4j
public class Roles {

    private Roles() {
    }

    public static final String ROLE = "ROLE_";

    public static final String MEDIA = "MEDIA_";

    public static final String API = "API_";

    /**
     * This is the default role of normal users. They have access, and can only write content of their own broadcaster(s)
     */
    public static final String USER = MEDIA + "USER";
    public static final String USER_ROLE = ROLE + USER;

    /**
     * A super-user has user access to nodes of every broadcaster.
     */
    public static final String SUPERUSER = MEDIA + "SUPERUSER";
    public static final String SUPERUSER_ROLE = ROLE + SUPERUSER;

    /**
     * A super-process is like a super-user
     */
    public static final String SUPERPROCESS =  MEDIA + "SUPERPROCESS";
    public static final String SUPERPROCESS_ROLE = ROLE + SUPERPROCESS;

    /**
     * A super-admin may do (nearly) everything
     */
    public static final String SUPERADMIN = MEDIA + "SUPERADMIN";
    public static final String SUPERADMIN_ROLE = ROLE + SUPERADMIN;



    /**
     * An authority may do very specific things, like creating broadcasts for radio channels. See {@link nl.vpro.domain.media.update.UpdateSupplier}.
     * <p>
     * Basically it is a superuser too, but it may be restricted to certain aspects.
     */
    public static final String AUTHORITY = MEDIA + "AUTHORITY";
    public static final String AUTHORITY_ROLE = ROLE + AUTHORITY;

    /**
     * This is a system role which is assigned to one user which may do stuff 'on behalf' of other users.  See the RunAs-services.
     */
    public static final String RUNAS  = "RUNAS";
    public static final String RUNAS_ROLE = ROLE + RUNAS;


    /**
     * The publisher role is kind of special. It can only see things that are allowed to be published.
     * <p>
     * This is a system role as nobody or nothing else would normally need to have this role.
     */
    public static final String PUBLISHER  = MEDIA + "PUBLISHER";
    public static final String PUBLISHER_ROLE = ROLE + PUBLISHER;

    /**
     * Support are people at NPO-helpdesk who can see everything, including deleted record, but edit nothing. (MSE-2015)
     * <p>
     * They may also not be limited in publishing bulk.
     */
    public static final String SUPPORT     = MEDIA + "SUPPORT";
    public static final String SUPPORT_ROLE = ROLE + SUPPORT;

    /**
     * Process roles may use the backend api.
     */
    public static final String PROCESS      = MEDIA + "PROCESS";
    public static final String PROCESS_ROLE = ROLE + PROCESS;

    /**
     * A limited role. May only edit objects originally created by theirselves.
     */
    public static final String EXTERNALUSER  = MEDIA + "EXTERNALUSER";
    public static final String EXTERNALUSER_ROLE = ROLE + EXTERNALUSER;

    /**
     * Has more rights on schedule event data
     */
    public static final String ARCHIVIST       = MEDIA + "ARCHIVIST";
    public static final String ARCHIVIST_ROLE = ROLE + ARCHIVIST;

    /**
     * Certain users in POMS backend are allowed to change/add schedule related fields.
     * <p>
     * <a href="https://jira.vpro.nl/browse/MSE-3999">MSE-3999</a>
     */
    public static final String SCHEDULE = MEDIA + "SCHEDULE";
    public static final String SCHEDULE_ROLE = ROLE + SCHEDULE;

    /**
     * Have the right to upload files as locations.
     */
    public static final String UPLOAD = MEDIA + "UPLOAD";
    public static final String UPLOAD_ROLE = ROLE + UPLOAD;


    /**
     * Have the right merge series
     */
    public static final String MERGE_SERIES = MEDIA + "MERGESERIES";
    public static final String MERGE_SERIES_ROLE = ROLE + MERGE_SERIES;

    /**
     * Have the right merge episodes
     */
    public static final String MERGE_EPISODE = MEDIA + "MERGEEPISODE";
    public static final String MERGE_EPISODE_ROLE = ROLE + MERGE_EPISODE;

    /**
     * Have the right merge anything
     */
    public static final String MERGE_ALL  = MEDIA + "MERGEALL";
    public static final String MERGE_ALL_ROLE = ROLE + MERGE_ALL;

    /**
     * Allowed to add translations
     */
    public static final String TRANSLATOR  = MEDIA + "TRANSLATOR";
    public static final String TRANSLATOR_ROLE = ROLE + TRANSLATOR;

    /**
     * Role for users that are allowed to edit MIS owned fields, do note that they still need the regular permissions to actually do so.
     */
    public static final String MIS  = MEDIA + "MIS";
    public static final String MIS_ROLE = ROLE + MIS;


    /**
     * Allowed to create promos {@link ProgramType#PROMO}
     */
    public static final String PROMO = MEDIA + "PROMO";
    public static final String PROMO_ROLE = ROLE + PROMO;


    /**
     * The system role is only assigned via {@code nl.vpro.domain.user.UserService#systemAuthenticate)}
     */
    public static final String SYSTEM = "SYSTEM";
    public static final String SYSTEM_ROLE = ROLE + SYSTEM;


    /**
     * Unused, I think
     */
    public static final String TVVOD = API + "TVVOD";
    public static final String TVVOD_ROLE = ROLE + TVVOD;

    /**
     * This is role is only assigned by the security for the frontend api, and is only used when checking access to frontend api calls.
     */
    public static final String API_CLIENT = API + "CLIENT";
    public static final String API_CHANGES_CLIENT = API + "CHANGES_CLIENT";

    public static final String API_CLIENT_ROLE = ROLE + API_CLIENT;
    public static final String API_CHANGES_CLIENT_ROLE = ROLE + API_CHANGES_CLIENT;


    public static final String HAS_API_ROLE = "hasAnyRole('" + USER_ROLE + "','" + API_CLIENT_ROLE + "')";
    public static final String HAS_API_CHANGES_ROLE = "hasAnyRole('" + USER_ROLE + "','" + API_CHANGES_CLIENT_ROLE + "')";

    public static final String PAGES_USER = ROLE + "PAGES_USER";

    public static final String PAGES_SUPERUSER= ROLE + "PAGES_SUPERUSER";

    public static final String PAGES_PROCESS = ROLE + "PAGES_PROCESS";

    public static final String PAGES_SUPERPROCESS = ROLE + "PAGES_SUPERPROCESS";


    public static final Set<String> RECOGNIZED;
    static {
        final Set<String> recognized = new HashSet<>();
        try {
            for (Field f : Roles.class.getDeclaredFields()) {
                if (Modifier.isStatic(f.getModifiers()) && Modifier.isPublic(f.getModifiers()) && f.getType().equals(String.class)) {
                    String value = (String) f.get(null);
                    if (value.startsWith(ROLE) && value.length() > ROLE.length()) {
                        recognized.add(value);
                    }

                }
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        RECOGNIZED = Collections.unmodifiableSet(recognized);
        log.info("Recognized roles: {}", RECOGNIZED);
    }



    public static final Set<String> PRIVILEGED = Set.of(SUPERADMIN_ROLE, SUPERPROCESS_ROLE, PUBLISHER_ROLE, SUPPORT_ROLE, SYSTEM_ROLE);

    public static final Set<String> CAN_CHOOSE_OWNER_TYPE = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
        MIS_ROLE,
        SUPERADMIN_ROLE
    )));

    public static Set<String> allRoles() {
        Set<String> set = new LinkedHashSet<>();
        for (Field f : Roles.class.getDeclaredFields()) {
            if (Modifier.isPublic(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) && f.getType().equals(String.class)) {
                try {
                    String value = (String) f.get(null);
                    if (value.startsWith(ROLE)) {
                        String role = value.substring(ROLE.length()).toLowerCase();
                        if (! role.isEmpty()) {
                            set.add(role);
                        }
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage(), e);
                }
            }
        }
        return set;
    }



}

