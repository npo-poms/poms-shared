package nl.vpro.domain.api.page;

import java.util.concurrent.CompletableFuture;

import nl.vpro.domain.api.profile.ProfileDefinition;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.update.SectionRepository;
import nl.vpro.util.CloseableIterator;
import nl.vpro.util.FilteringIterator;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface PageSearchRepository extends SectionRepository {

    Page load(String id);

    CompletableFuture<Page> loadAsync(String id);

    Page[] loadByCrid(String... crid);

    CompletableFuture<Page[]> loadByCridsAsync(String... crids);

    CompletableFuture<Page[]> loadByUrlsAsync(String... urls);

    CompletableFuture<Page[]> loadByStatRefsAsync(String... statRefs);

    PageSearchResult find(ProfileDefinition<Page> profile, PageForm form, long offset, Integer max);

    PageSearchResult findRelated(Page media, ProfileDefinition<Page> profile, PageForm form, Integer max);

    CloseableIterator<Page> iterate(ProfileDefinition<Page> profile, PageForm form, long offset, Integer max, FilteringIterator.KeepAlive keepAlive);
}
