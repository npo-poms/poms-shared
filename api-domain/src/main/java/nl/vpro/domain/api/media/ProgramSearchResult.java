package nl.vpro.domain.api.media;

import java.util.Collections;
import java.util.List;

import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
        return new ProgramSearchResult(Collections.emptyList(), offset, max, Total.EMPTY);
    }

    public ProgramSearchResult(List<SearchResultItem<? extends Program>> searchResultItems, Long offset, Integer max, Total total) {
        super(searchResultItems, offset, max, total);
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
