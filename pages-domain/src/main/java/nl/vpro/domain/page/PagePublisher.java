package nl.vpro.domain.page;

import java.util.concurrent.Future;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public interface PagePublisher {


    Future<?> publish(Page page);

    Future<?> republishPage(String url);

    Future<?> revoke(String url);

}
