package nl.vpro.domain.media.search;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class MediaFormTest {


    @Test
    public void xml() {
        MediaForm form = MediaForm.builder()
            .asc(MediaSortField.lastModified)
            .max(1000)
            .build();
        JAXBTestUtil.roundTripAndSimilar(form, "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<s:mediaForm xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
                "    <s:pager>\n" +
                "        <s:offset>0</s:offset>\n" +
                "        <s:max>1000</s:max>\n" +
                "        <s:sort>lastModified</s:sort>\n" +
                "        <s:order>ASC</s:order>\n" +
                "    </s:pager>\n" +
                "</s:mediaForm>\n"
            );
    }

    @Test
    public void builder() {
        MediaForm form = MediaForm.builder()
            .broadcasters(null)
            .locationsCount(IntegerRange.builder().start(0L).stop(0L, true).build())
            .lastPublishedRange(InstantRange.builder().start(InstantRange.Value.of(LocalDateTime.of(2017, 9, 29, 16, 35).atZone(Schedule.ZONE_ID).toInstant())).build())
            .build();
        JAXBTestUtil.roundTripAndSimilar(form, "<s:mediaForm xmlns=\"urn:vpro:media:2009\" xmlns:shared=\"urn:vpro:shared:2009\" xmlns:s=\"urn:vpro:media:search:2012\" xmlns:update=\"urn:vpro:media:update:2009\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\">\n" +
            "    <s:pager>\n" +
            "        <s:offset>0</s:offset>\n" +
            "        <s:order>ASC</s:order>\n" +
            "    </s:pager>\n" +
            "    <s:locationsCount>\n" +
            "        <s:start>0</s:start>\n" +
            "        <s:stop inclusive=\"true\">0</s:stop>\n" +
            "    </s:locationsCount>\n" +
            "    <s:lastPublishedRange>\n" +
            "        <s:start>2017-09-29T16:35:00+02:00</s:start>\n" +
            "    </s:lastPublishedRange>\n" +
            "</s:mediaForm>");
    }


}
