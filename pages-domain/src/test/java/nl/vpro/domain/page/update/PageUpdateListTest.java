package nl.vpro.domain.page.update;

import org.junit.jupiter.api.Test;

import nl.vpro.test.util.jaxb.JAXBTestUtil;

class PageUpdateListTest {

    @Test
    public void xml() {

        PageUpdateList list = PageUpdateList.of(
            PageUpdate.builder()
                .title("page 1")
            ,
            PageUpdate.builder()
                .title("page 2")
        );
        JAXBTestUtil.roundTripAndSimilar(list, """
            <?xml version="1.0" encoding="UTF-8"?><pageUpdate:pages xmlns:pageUpdate="urn:vpro:pages:update:2013" xmlns:shared="urn:vpro:shared:2009" xmlns:update="urn:vpro:media:update:2009" xmlns:page="urn:vpro:pages:2013" xmlns:media="urn:vpro:media:2009">
                <pageUpdate:page>
                  <pageUpdate:title>page 1</pageUpdate:title>
                </pageUpdate:page>
                <pageUpdate:page>
                  <pageUpdate:title>page 2</pageUpdate:title>
                </pageUpdate:page>
              </pageUpdate:pages>
            """);
    }

}
