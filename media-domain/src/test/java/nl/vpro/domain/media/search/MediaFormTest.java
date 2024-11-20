package nl.vpro.domain.media.search;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.Schedule;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

/**
 * @author Michiel Meeuwissen
 * @since 5.0
 */
public class MediaFormTest {

    MediaForm form = MediaForm.builder()
            .asc(MediaSortField.lastModified)
            .broadcaster("VPRO")
            .quotedText("foobar")
            .max(1000)
            .build();

    @Test
    public void xml() {

        JAXBTestUtil.roundTripAndSimilar(form, """
                <s:mediaForm xmlns:s="urn:vpro:media:search:2012" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:update="urn:vpro:media:update:2009" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                  <s:pager>
                    <s:offset>0</s:offset>
                    <s:max>1000</s:max>
                    <s:sort>lastModified</s:sort>
                    <s:order>ASC</s:order>
                  </s:pager>
                  <s:broadcaster>VPRO</s:broadcaster>
                  <s:text>"foobar"</s:text>
                </s:mediaForm>"""
            );
    }


    @Test
    public void json() {

        Jackson2TestUtil.roundTripAndSimilar(form, """
                {
                    "pager" : {
                      "offset" : 0,
                      "max" : 1000,
                      "sort" : "lastModified",
                      "order" : "ASC"
                    },
                    "broadcasters" : [ "VPRO" ],
                    "text" : "\\"foobar\\""
                }
                """
            );
    }

    @Test
    public void builder() {
        MediaForm form = MediaForm.builder()
            .broadcasters(null)
            .locationsCount(IntegerRange.builder()
                .start(0L)
                .stop(0L, false).build())
            .lastPublishedRange(InstantRange.builder().start(InstantRange.Value.of(LocalDateTime.of(2017, 9, 29, 16, 35).atZone(Schedule.ZONE_ID).toInstant())).build())
            .build();
        JAXBTestUtil.roundTripAndSimilar(form, """
            <?xml version="1.0" encoding="UTF-8"?><s:mediaForm xmlns:s="urn:vpro:media:search:2012" xmlns="urn:vpro:media:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:update="urn:vpro:media:update:2009" xmlns:xs="http://www.w3.org/2001/XMLSchema">
                            <s:pager>
                              <s:offset>0</s:offset>
                              <s:max>50</s:max>
                              <s:order>ASC</s:order>
                            </s:pager>
                            <s:locationsCount>
                              <s:start>0</s:start>
                              <s:stop inclusive="false">0</s:stop>
                            </s:locationsCount>
                            <s:lastPublishedRange>
                              <s:start>2017-09-29T16:35:00+02:00</s:start>
                            </s:lastPublishedRange>
                          </s:mediaForm>""");
    }


}
