package nl.vpro.media.tva.bindinc;

import java.util.*;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Michiel Meeuwissen
 * @since ...
 */
class FileNameComparatorTest {


    @Test
    public void sort() {

        List<String> list = new ArrayList<>(Arrays.asList(
            "20210104171109000dayBBC120210108.xml",
            "20210108091723000dayfrance220210111.xml",
            "20210108080527000dayN_3_20210126.xml",
            "20201230121315000dayBBC120210108.xml",
            null,
            "1.xml",
            "20201230121315000dayXYZ20210108.xml"

        ));
        list.sort(new FileNameComparator());

        assertThat(list).containsExactly(
            "20201230121315000dayBBC120210108.xml",
            "20210104171109000dayBBC120210108.xml",
            "20201230121315000dayXYZ20210108.xml",
            "20210108091723000dayfrance220210111.xml",
            "20210108080527000dayN_3_20210126.xml",
            "1.xml",
            null
        );


    }

}
