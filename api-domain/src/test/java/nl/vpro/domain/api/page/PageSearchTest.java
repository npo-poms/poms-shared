package nl.vpro.domain.api.page;

import java.time.Instant;
import java.util.*;

import org.junit.Test;

import nl.vpro.domain.api.*;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.page.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 2.3
 */
public class PageSearchTest {
    @Test
    public void testGetText() {
        PageSearch in = new PageSearch();
        in.setText(new SimpleTextMatcher("Title"));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<local:pageSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:text>Title</api:text>\n" +
                "</local:pageSearch>");
        assertThat(new TextMatcher("Title")).isEqualTo(out.getText());
    }

    @Test
    public void testApplyText() {
        PageSearch in = new PageSearch();
        in.setText(new SimpleTextMatcher("title"));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();
        object.setTitle("main title");
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testGetBroadcasters() {
        PageSearch in = new PageSearch();
        in.setBroadcasters(new TextMatcherList(new TextMatcher("VPRO"), new TextMatcher("TROS")));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:broadcasters match=\"MUST\">\n" +
                "        <api:matcher>VPRO</api:matcher>\n" +
                "        <api:matcher>TROS</api:matcher>\n" +
                "    </api:broadcasters>\n" +
                "</local:pageSearch>");
        assertThat(out.getBroadcasters().asList()).containsExactly(new TextMatcher("VPRO"), new TextMatcher("TROS"));
    }

    @Test
    public void testApplyBroadcasters() {
        PageSearch in = new PageSearch();
        in.setBroadcasters(new TextMatcherList(TextMatcher.should("VPRO"), TextMatcher.should("TROS")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();
        object.setBroadcasters(Collections.singletonList(new Broadcaster("VPRO")));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testGetTypes() {
        PageSearch in = new PageSearch();
        in.setTypes(new TextMatcherList(new TextMatcher("ARTICLE"), new TextMatcher("PLAYER")));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:types match=\"MUST\">\n" +
                "        <api:matcher>ARTICLE</api:matcher>\n" +
                "        <api:matcher>PLAYER</api:matcher>\n" +
                "    </api:types>\n" +
                "</local:pageSearch>\n");
        assertThat(out.getTypes().asList()).containsExactly(new TextMatcher(PageType.ARTICLE.name()), new TextMatcher(PageType.PLAYER.name()));
    }

    @Test
    public void testApplyTypes() {
        PageSearch in = new PageSearch();
        in.setTypes(new TextMatcherList(new TextMatcher(PageType.PLAYER.name())));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        Page video = new Page(PageType.PLAYER);
        assertThat(in.test(video)).isTrue();
    }

    @Test
    public void testGetPortals() {
        PageSearch in = new PageSearch();
        in.setPortals(new TextMatcherList(new TextMatcher("WETENSCHAP24")));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:portals match=\"MUST\">\n" +
                "        <api:matcher>WETENSCHAP24</api:matcher>\n" +
                "    </api:portals>\n" +
                "</local:pageSearch>");
        assertThat(out.getPortals().asList()).containsExactly(new TextMatcher("WETENSCHAP24"));
    }

    @Test
    public void testApplyPortals() {
        PageSearch in = new PageSearch();
        in.setPortals(new TextMatcherList(new TextMatcher("http://www.wetenschap24.nl")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setPortal(new Portal("WETENSCHAP24", "http://www.wetenschap24.nl", "Noorderlicht"));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testApplySections() {
        PageSearch in = new PageSearch();
        in.setSections(new TextMatcherList(new TextMatcher("noorderlicht")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        Portal portal = new Portal("WETENSCHAP24", "http://www.wetenschap24.nl", "Wetenschap");
        Section section = new Section("noorderlicht", "Noorderlicht");
        portal.setSection(section);
        object.setPortal(portal);
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testGetGenres() {
        PageSearch in = new PageSearch();
        in.setGenres(new TextMatcherList(new TextMatcher("3.0.1.1")));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:genres match=\"MUST\">\n" +
                "        <api:matcher>3.0.1.1</api:matcher>\n" +
                "    </api:genres>\n" +
                "</local:pageSearch>");
        assertThat(out.getGenres().asList()).containsExactly(new TextMatcher("3.0.1.1"));
    }

    @Test
    public void testApplyGenres() {
        PageSearch in = new PageSearch();
        in.setGenres(new TextMatcherList(new TextMatcher("3.0.1.2")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setGenres(new TreeSet<>(Collections.singletonList(new Genre(new Term("3.0.1.2")))));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testGetTags() {
        PageSearch in = new PageSearch();
        in.setTags(new ExtendedTextMatcherList(new ExtendedTextMatcher("tag1")));

        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                        "<local:pageSearch xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                        "    <api:tags match=\"MUST\">\n" +
                        "        <api:matcher>tag1</api:matcher>\n" +
                        "    </api:tags>\n" +
                        "</local:pageSearch>");
        assertThat(out.getTags().asList()).containsExactly(new ExtendedTextMatcher("tag1"));
    }

    @Test
    public void testApplyTags() {
        PageSearch in = new PageSearch();
        in.setTags(new ExtendedTextMatcherList(new ExtendedTextMatcher("tag1")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setTags(Collections.singletonList("tag1"));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testApplyKeywords() {
        PageSearch in = new PageSearch();
        in.setKeywords(new ExtendedTextMatcherList(new ExtendedTextMatcher("apen")));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setKeywords(Collections.singletonList("apen"));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testGetSortDate() {
        PageSearch in = new PageSearch();
        in.setSortDates(new DateRangeMatcherList(new DateRangeMatcher(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200), true)));
        PageSearch out = JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:shared=\"urn:vpro:shared:2009\" xmlns:pages=\"urn:vpro:pages:2013\" xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:sortDates match=\"MUST\">\n" +
                "        <api:matcher inclusiveEnd=\"true\">\n" +
                "            <api:begin>1970-01-01T01:00:00.100+01:00</api:begin>\n" +
                "            <api:end>1970-01-01T01:00:00.200+01:00</api:end>\n" +
                "        </api:matcher>\n" +
                "    </api:sortDates>\n" +
                "</local:pageSearch>");
        assertThat(out.getSortDates().asList().get(0)).isEqualTo(new DateRangeMatcher(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200), true));
    }

    @Test
    public void testApplySortDate() {
        PageSearch in = new PageSearch();
        in.setSortDates(new DateRangeMatcherList(new DateRangeMatcher(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200), false)));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setPublishStartInstant(Instant.ofEpochMilli(50));
        assertThat(in.test(object)).isFalse();
        object.setPublishStartInstant(Instant.ofEpochMilli(100));
        assertThat(in.test(object)).isTrue();
        object.setPublishStartInstant(Instant.ofEpochMilli(150));
        assertThat(in.test(object)).isTrue();
        object.setPublishStartInstant(Instant.ofEpochMilli(200));
        assertThat(in.test(object)).isFalse();
        object.setPublishStartInstant(Instant.ofEpochMilli(250));
        assertThat(in.test(object)).isFalse();
    }

    @Test
    public void testApplySortDateNot() {
        PageSearch in = new PageSearch();
        in.setSortDates(new DateRangeMatcherList(new DateRangeMatcher(Instant.ofEpochMilli(100), Instant.ofEpochMilli(200), false, Match.NOT)));

        Page object = new Page(PageType.ARTICLE);
        assertThat(in.test(object)).isFalse();

        object.setPublishStartInstant(Instant.ofEpochMilli(50));
        assertThat(in.test(object)).isTrue();
        object.setPublishStartInstant(Instant.ofEpochMilli(100));
        assertThat(in.test(object)).isFalse();
        object.setPublishStartInstant(Instant.ofEpochMilli(150));
        assertThat(in.test(object)).isFalse();
        object.setPublishStartInstant(Instant.ofEpochMilli(200));
        assertThat(in.test(object)).isTrue();
        object.setPublishStartInstant(Instant.ofEpochMilli(250));
        assertThat(in.test(object)).isTrue();
    }

    @Test
    public void testRelations() {
        PageSearch in = new PageSearch();
        RelationSearch rs1 = new RelationSearch();
        RelationSearch rs2 = new RelationSearch();
        rs1.setTypes(TextMatcherList.must(TextMatcher.must("DIRECTOR")));
        rs2.setTypes(TextMatcherList.must(TextMatcher.must("ACTOR")));
        in.setRelations(new RelationSearchList(rs1, rs2));
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:relations>\n" +
                "        <api:relationSearch>\n" +
                "            <api:types match=\"MUST\">\n" +
                "                <api:matcher>DIRECTOR</api:matcher>\n" +
                "            </api:types>\n" +
                "        </api:relationSearch>\n" +
                "        <api:relationSearch>\n" +
                "            <api:types match=\"MUST\">\n" +
                "                <api:matcher>ACTOR</api:matcher>\n" +
                "            </api:types>\n" +
                "        </api:relationSearch>\n" +
                "    </api:relations>\n" +
                "</local:pageSearch>");

    }

    @Test
    public void testReferrals() {
        PageSearch in = new PageSearch();
        AssociationSearch as1 = new AssociationSearch();
        AssociationSearch as2 = new AssociationSearch();
        as1.setTypes(TextMatcherList.must(TextMatcher.must("TOP_STORY")));
        as2.setTypes(TextMatcherList.must(TextMatcher.must("SLECHT")));
        in.setReferrals(new AssociationSearchList(as1, as2));
        JAXBTestUtil.roundTripAndSimilar(in,
            "<local:pageSearch xmlns:api=\"urn:vpro:api:2013\" xmlns:media=\"urn:vpro:media:2009\" xmlns:local=\"uri:local\">\n" +
                "    <api:referrals>\n" +
                "        <api:search>\n" +
                "            <api:types match=\"MUST\">\n" +
                "                <api:matcher>TOP_STORY</api:matcher>\n" +
                "            </api:types>\n" +
                "        </api:search>\n" +
                "        <api:search>\n" +
                "            <api:types match=\"MUST\">\n" +
                "                <api:matcher>SLECHT</api:matcher>\n" +
                "            </api:types>\n" +
                "        </api:search>\n" +
                "    </api:referrals>\n" +
                "</local:pageSearch>");

    }
}
