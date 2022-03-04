package nl.vpro.domain.api.page;

import java.util.List;

import nl.vpro.domain.api.IdList;
import nl.vpro.domain.api.SuggestResult;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.page.Page;
import nl.vpro.util.CloseableIterator;
import nl.vpro.util.FilteringIterator;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface PageService {

    SuggestResult suggest(String input, String profile, Integer max) throws ProfileNotFoundException;

    PageSearchResult find(PageForm form, String profile, Long offset, Integer max)  throws ProfileNotFoundException;

    Page load(String url);

    Page[] loadByCrid(String... crid);

    List<Page> loadForIds(IdList ids);

    PageSearchResult findRelated(Page page, String profile, PageForm form, Integer max) throws ProfileNotFoundException;

    CloseableIterator<Page> iterate(String profile, PageForm  form, Long offset, Integer max, FilteringIterator.KeepAlive keepAlive) throws ProfileNotFoundException;


}
