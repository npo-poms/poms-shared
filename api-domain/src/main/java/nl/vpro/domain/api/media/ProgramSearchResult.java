package nl.vpro.domain.api.media;

import nl.vpro.domain.api.GenericMediaSearchResult;
import nl.vpro.domain.api.SearchResult;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.media.Program;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Collections;
import java.util.List;

/**
 * Exists only because of https://jira.vpro.nl/browse/API-118
 *
 * @author Michiel Meeuwissen
 * @since 2.0
 */
@XmlRootElement(name = "programSearchResult")
@XmlType(name = "programSearchResultType")
public class ProgramSearchResult extends GenericMediaSearchResult<Program> {

    public ProgramSearchResult() {
    }

    public static ProgramSearchResult emptyResult(Long offset, Integer max) {
        return new ProgramSearchResult(Collections.emptyList(), offset, max, 0L);
    }

    public ProgramSearchResult(List<SearchResultItem<? extends Program>> searchResultItems, Long offset, Integer max, long listSizes) {
        super(searchResultItems, offset, max, listSizes);
    }

    public ProgramSearchResult(SearchResult<Program> episodes) {
        super(episodes);
    }

    public ProgramSearchResult(GenericMediaSearchResult<Program> sr) {
        super(sr);
        setFacets(sr.getFacets());
    }

    @Override
    public ProgramResult asResult() {
        return new ProgramResult(super.asResult());
    }
}
