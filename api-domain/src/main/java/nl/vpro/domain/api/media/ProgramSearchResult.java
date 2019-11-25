package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.api.*;
import nl.vpro.domain.media.Program;

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
        return new ProgramSearchResult(Collections.emptyList(), offset, max, 0L, TotalQualifier.EQUAL_TO);
    }

    public ProgramSearchResult(List<SearchResultItem<? extends Program>> searchResultItems, Long offset, Integer max, Long listSizes, TotalQualifier totalQualifier) {
        super(searchResultItems, offset, max, listSizes, totalQualifier);
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
