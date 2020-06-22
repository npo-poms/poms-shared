package nl.vpro.poms.shared;

/**
 * @author Michiel Meeuwissen
 * @since 5.13
 */
public class Headers {

    private  Headers() {

    }

    /**
     * A request header signifying the preferred {@link OwnerType}
     */
    public static final String OWNER_TYPE_HEADER = "X-Poms-CurrentOwner";

    /**
     * A request header signifying the preferred date format in json
     * TODO: NPA-539
     */
    public static final String JSON_TIMESTAMPS = "X-NPO-TIMESTAMPS";

    /**
     * A prefix for response headers related to NPO
     */
    public static final String X_NPO                        = "X-NPO";

    /**
     * The request was authenticated, the response acknowledges that via this header
     */
    public static final String NPO_CURRENT_USER             = X_NPO + "-currentUser";

    /**
     * The request was authenticated, the response informs via this header which is the current employer of the authenticated user
     */
    public static final String NPO_CURRENT_CURRENT_EMPLOYER = X_NPO + "-currentEmployer";

    /**
     * The request was authenticated, the response informs via this header on which broadcasters the authenticated user has rights
     */
    public static final String NPO_BROADCASTERS             = X_NPO + "-broadcasters";

    /**
     * The request was authenticated, the response informs via this header on which portals the authenticated user has rights
     */
    public static final String NPO_PORTALS                 = X_NPO + "-portals";

    /**
     * The request was authenticated, the response informs via this header on which roles were assigned to the authenticated user
     */
    public static final String NPO_ROLES                    = X_NPO + "-roles";

    /**
     * Indication about implicitely perform 'redirects' of ids (related to merging of media objects)
     */
    public static final String NPO_REDIRECTS                 = X_NPO + "-redirects";


    public static final String NPO_VERSION                  = X_NPO + "-version";

    public static final String NPO_TOOK                    = X_NPO + "-took";


    public static final String NPO_VALIDATION_WARNING_HEADER = X_NPO + "-validation-warning";
    public static final String NPO_VALIDATION_ERROR_HEADER = X_NPO + "-validation-error";

    public static final String NPO_CLIENTIP                = X_NPO + "-clientip";
}



