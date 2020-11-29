package nl.vpro.domain.api.page;

import nl.vpro.domain.page.Association;
import nl.vpro.domain.page.Link;
import nl.vpro.domain.page.LinkType;
import nl.vpro.domain.page.Referral;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class AssociationSearchTest {

    @Test
    public void testPredicateReferral() {
        AssociationSearch search = AssociationSearch.of(LinkType.TOP_STORY);

        Association assocation1 = new Referral("http://www.vpro.nl/home.html", "Zie dit mooie artikel", LinkType.TOP_STORY);
        assertThat(search.test(assocation1)).isTrue();

        Association assocation2 = new Referral("http://www.vpro.nl/home.html", "Zie dit zozo artikel");
        assertThat(search.test(assocation2)).isFalse();
    }

    @Test
    public void testPredicateLinks() {
        AssociationSearch search = AssociationSearch.of(LinkType.TOP_STORY);

        Association assocation1 = new Link("http://www.vpro.nl/home.html", "Zie dit mooie artikel", LinkType.TOP_STORY);
        assertThat(search.test(assocation1)).isTrue();

        Association assocation2 = new Link("http://www.vpro.nl/home.html", "Zie dit zozo artikel");
        assertThat(search.test(assocation2)).isFalse();
    }
}
