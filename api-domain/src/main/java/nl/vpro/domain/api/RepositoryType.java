package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 4.7
 */
public enum RepositoryType {
    COUCHDB,
    ELASTICSEARCH,
    CLIENT
    ;


    public static <T> T switchRepository(RepositoryType repository, T couchdb, T elasticSearch) {
        switch (repository) {
            case COUCHDB:
                return couchdb;
            case ELASTICSEARCH:
                return elasticSearch;
            case CLIENT:
                if (couchdb != null) {
                    return couchdb;
                } else {
                    return elasticSearch;
                }
            default:
                throw new IllegalStateException();
        }
    }

}
