package nl.vpro.domain.api.page;

import nl.vpro.domain.api.IdList;

import nl.vpro.domain.api.SuggestResult;
import nl.vpro.domain.api.profile.exception.ProfileNotFoundException;
import nl.vpro.domain.page.Page;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public interface PageService {

    SuggestResult suggest(String input, String profile, Integer max);

    PageSearchResult find(PageForm form, String profile, Long offset, Integer max)  throws ProfileNotFoundException;

    Page load(String url);

    Page[] loadByCrid(String... crid);

    List<Page> loadForIds(IdList ids);

    PageSearchResult findRelated(Page page, String profile, PageForm form, Integer max) throws ProfileNotFoundException;

}
