package nl.vpro.pages.domain.es;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
public class ApiPageUpdatesIndex {

    public static String NAME = "pageupdates";

    public static String TYPE = "pageupdate";

    public static String DELETEDTYPE = "deletedpageupdate";


    public static String source() {
        return ApiPagesIndex.source("setting/apipages.json");
    }



    public static String mappingSource() {
        return ApiPagesIndex.source("mapping/pageupdate.json");
    }

    public static String deletedMappingSource() {
        return ApiPagesIndex.source("mapping/deletedpageupdate.json");
    }

}
