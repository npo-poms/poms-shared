package nl.vpro.domain.api.page;

import nl.vpro.domain.page.Association;
import nl.vpro.domain.page.LinkType;
import nl.vpro.domain.page.Referral;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class AssociationSearchListTest {


    @Test
    public void testPredicate() {
        AssociationSearchList list = new AssociationSearchList(Collections.singletonList(AssociationSearch.of(LinkType.TOP_STORY)));

        Association assocation1 = new Referral("http://www.vpro.nl/home.html", "Zie dit mooie artikel", LinkType.TOP_STORY);

        assertThat(list.test(assocation1)).isTrue();
    }
}
