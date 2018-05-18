/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.TreeSet;

import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBException;

import org.junit.Test;

import nl.vpro.domain.media.Group;
import nl.vpro.domain.media.GroupType;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupUpdateTest extends MediaUpdateTest {


    @Test
    public void testGetSetOnConsistency() throws Exception {
        GroupUpdate groupUpdate = GroupUpdate.create(new Group(GroupType.ALBUM));

        groupUpdate.setType(GroupType.PLAYLIST);

        assertThat(groupUpdate.getType()).isEqualTo(GroupType.PLAYLIST);
    }

    @Test
    public void testFetchOnDefaultOwner() throws Exception {
        GroupUpdate group = GroupUpdate.create();
        group.setTitles(new TreeSet<>(Arrays.asList(new TitleUpdate("title", TextualType.MAIN))));

        Group result = group.fetch();

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.BROADCASTER);
    }

    @Test
    public void testUnMarshal() throws Exception {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group type=\"SEASON\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><scheduleEvents/><images/></group>";

        GroupUpdate result = toUpdate(input);

        assertThat(result.getType()).isEqualTo(GroupType.SEASON);
    }

    @Test
    public void testGetType() throws Exception {
        GroupUpdate update = GroupUpdate.create();
        update.setVersion(null);
        update.setType(GroupType.SEASON);

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<group xmlns=\"urn:vpro:media:update:2009\" type=\"SEASON\" ordered=\"true\" embeddable=\"true\">\n" +
            "  <credits/>\n" +
            "  <locations/>\n" +
            "  <scheduleEvents/>\n" +
            "  <images/>\n" +
            "</group>\n";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPoSeriesID() throws Exception {
        GroupUpdate update = GroupUpdate.create();
        update.setVersion(null);
        update.setPoSeriesID("VPWON_333");

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
            "<group xmlns=\"urn:vpro:media:update:2009\" ordered=\"true\" embeddable=\"true\" mid=\"VPWON_333\">\n" +
            "  <credits/>\n" +
            "  <locations/>\n" +
            "  <scheduleEvents/>\n" +
            "  <images/>\n" +
            "  <poSeriesID>VPWON_333</poSeriesID>\n" +
            "</group>\n";

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    protected GroupUpdate toUpdate(String xml) throws JAXBException {
        Reader reader = new StringReader(xml);
        return JAXB.unmarshal(reader, GroupUpdate.class);
    }

}
