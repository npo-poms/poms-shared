package nl.vpro.domain.api.page;

import nl.vpro.domain.api.TermFacetResultItem;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class PageSearchResultsTest {

    @Test
    public void testSetSelectedFacetsGenresFalse() {
        PageFacetsResult result = new PageFacetsResult();
        PageFacetsResult selected = new PageFacetsResult();
        GenreFacetResultItem tfr = new GenreFacetResultItem(Collections.emptyList(), "name", "foo", 100);
        result.setGenres(Collections.singletonList(tfr));
        PageForm form = PageFormBuilder.form().genres("bar").genreFacet().build();
        PageSearchResults.setSelectedFacets(result, selected, form);
        assertThat(tfr.isSelected()).isFalse();
    }

    @Test
    public void testSetSelectedFacetsGenresTrue() {
        PageFacetsResult result = new PageFacetsResult();
        PageFacetsResult selected = new PageFacetsResult();
        GenreFacetResultItem tfr = new GenreFacetResultItem(Collections.emptyList(), "Foo", "foo", 100);
        result.setGenres(Collections.singletonList(tfr));
        PageForm form = PageFormBuilder.form().genres("name", "foo").genreFacet().build();
        PageSearchResults.setSelectedFacets(result, selected, form);
        assertThat(tfr.isSelected()).isTrue();
    }

    @Test
    public void testSetSelectedFacetsBroadcastersFalse() {
        PageFacetsResult result = new PageFacetsResult();
        PageFacetsResult selected = new PageFacetsResult();
        TermFacetResultItem tfr = new TermFacetResultItem("name", "foo", 100);
        result.setBroadcasters(Collections.singletonList(tfr));
        PageForm form = PageFormBuilder.form().broadcasters("bar").broadcasterFacet().build();
        PageSearchResults.setSelectedFacets(result, selected, form);
        assertThat(tfr.isSelected()).isFalse();
    }

    @Test
    public void testSetSelectedFacetsBroadcastersTrue() {
        PageFacetsResult result = new PageFacetsResult();
        PageFacetsResult selected = new PageFacetsResult();
        TermFacetResultItem tfr = new TermFacetResultItem("name", "foo", 100);
        result.setBroadcasters(Collections.singletonList(tfr));
        PageForm form = PageFormBuilder.form().broadcasters("name", "foo").broadcasterFacet().build();
        PageSearchResults.setSelectedFacets(result, selected, form);
        assertThat(tfr.isSelected()).isTrue();
    }
}
