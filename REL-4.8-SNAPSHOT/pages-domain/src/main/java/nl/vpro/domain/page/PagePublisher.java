package nl.vpro.domain.page;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface PagePublisher {


    void publish(Page page);

    void republishPage(String url);

    void revoke(String url);

}
