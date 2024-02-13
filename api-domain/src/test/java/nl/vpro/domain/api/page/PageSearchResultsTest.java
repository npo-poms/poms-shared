package nl.vpro.domain.api.page;

import lombok.extern.log4j.Log4j2;

import java.io.StringReader;
import java.util.Collections;

import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.api.TermFacetResultItem;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
@Log4j2
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

    @Test
    public void fromXml() {
        String in = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <api:pageSearchResult xmlns:api="urn:vpro:api:2013" xmlns="urn:vpro:media:2009" xmlns:media="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:pages="urn:vpro:pages:2013" total="432" totalQualifier="EQUAL_TO" offset="0" max="0">
                  <api:items>
                    <api:item xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:type="api:searchResultItem" score="0.3099519">
                      <api:result xsi:type="pages:pageType" url="https://www.vprobroadcast.com/play~WO_VPRO_2297327~antibiotics~.html" type="VIDEO" creationDate="2019-11-22T14:15:09.443+01:00" lastModified="2019-11-22T14:15:09.443+01:00" lastPublished="2019-11-22T14:15:24.341+01:00" publishStart="2015-10-16T14:23:14.729+02:00" refCount="0" sortDate="2015-10-16T14:23:14.729+02:00">
                        <pages:crid>crid://vpro/media/vprobroadcast/WO_VPRO_2297327</pages:crid>
                        <pages:broadcaster id="VPRO"></pages:broadcaster>
                        <pages:portal id="VPROBROADCAST" url="https://www.vprobroadcast.com">
                          <pages:name>www.vprobroadcast.com</pages:name>
                        </pages:portal>
                        <pages:title>Antibiotics</pages:title>
                        <pages:images>
                          <pages:image type="PICTURE" url="https://images.poms.omroep.nl/image/s360/665086.jpg">
                            <pages:title>Antibiotica</pages:title>
                            <pages:description>Labyrint</pages:description>
                          </pages:image>
                        </pages:images>
                      </api:result>
                    </api:item>
                  </api:items>
                  <api:facets/>
                  <api:selectedFacets/>
                </api:pageSearchResult>
            """;
        PageSearchResult result = JAXB.unmarshal(new StringReader(in), PageSearchResult.class);
        log.info("{}", result);
        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.get(0).getResult().getTitle()).isEqualTo("Antibiotics");

    }
}
