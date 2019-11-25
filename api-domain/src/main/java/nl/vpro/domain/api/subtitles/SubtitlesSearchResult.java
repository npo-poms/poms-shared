package nl.vpro.domain.api.subtitles;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.annotation.*;

import nl.vpro.domain.api.SearchResult;
import nl.vpro.domain.api.SearchResultItem;
import nl.vpro.domain.subtitles.StandaloneCue;


/**
 *
 * @author Michiel Meeuwissen
 * @since 4.8
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "subtitlesSearchResult")
@XmlType(name = "subtitlesSearchResultType")
@XmlSeeAlso({StandaloneCue.class})
public class SubtitlesSearchResult extends SearchResult<StandaloneCue> {

    public SubtitlesSearchResult() {
    }

    public static SubtitlesSearchResult emptyResult(Long offset, Integer max) {
        return new SubtitlesSearchResult(Collections.<SearchResultItem<? extends StandaloneCue>>emptyList(), offset, max, 0L, TotalQualifier.EQUAL_TO);
    }

    public SubtitlesSearchResult(List<SearchResultItem<? extends StandaloneCue>> list, Long offset, Integer max, Long total, TotalQualifier totalQualifier) {
        super(list, offset, max, total, totalQualifier);
    }


    public SubtitlesSearchResult(SearchResult<? extends StandaloneCue> sr) {
        super(sr);
    }

}
