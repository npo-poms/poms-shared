/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.update;

import java.io.Reader;
import java.io.StringReader;
import java.util.Collections;
import java.util.TreeSet;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.test.util.jaxb.JAXBTestUtil;

import static org.assertj.core.api.Assertions.assertThat;

public class GroupUpdateTest extends MediaUpdateTest {

    @Test
    public void create() {
        GroupUpdate groupUpdate = (GroupUpdate) MediaUpdate.create(MediaBuilder
            .group()
            .mainTitle("foo bar")
            .build());
        assertThat(groupUpdate.getMainTitle()).isEqualTo("foo bar");

    }


    @Test
    public void testGetSetOnConsistency() {
        GroupUpdate groupUpdate = GroupUpdate.create(new Group(GroupType.ALBUM));

        groupUpdate.setType(GroupType.PLAYLIST);

        assertThat(groupUpdate.getType()).isEqualTo(GroupType.PLAYLIST);
    }

    @Test
    public void testFetchOnDefaultOwner() {
        GroupUpdate group = GroupUpdate.create();
        group.setTitles(new TreeSet<>(Collections.singletonList(new TitleUpdate("title", TextualType.MAIN))));

        Group result = group.fetch();

        assertThat(result.getTitles().first().getOwner()).isEqualTo(OwnerType.BROADCASTER);
    }

    @Test
    public void testUnMarshal() {
        String input = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><group type=\"SEASON\" embeddable=\"true\" xmlns=\"urn:vpro:media:update:2009\"><locations/><images/></group>";

        GroupUpdate result = toUpdate(input);

        assertThat(result.getType()).isEqualTo(GroupType.SEASON);
    }

    @Test
    public void testGetType() {
        GroupUpdate update = GroupUpdate.create();
        update.setVersion(null);
        update.setType(GroupType.SEASON);

        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <group ordered="true" type="SEASON" embeddable="true" xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <credits/>
                <locations/>
                <images/>
            </group>
            """;

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    @Test
    public void testGetPoSeriesID() {
        GroupUpdate update = GroupUpdate.create();
        update.setVersion(null);
        update.setPoSeriesID("VPWON_333");

        String expected = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <group ordered="true" embeddable="true" mid="VPWON_333" xmlns="urn:vpro:media:update:2009" xmlns:shared="urn:vpro:shared:2009" xmlns:media="urn:vpro:media:2009">
                <credits/>
                <locations/>
                <images/>
                <poSeriesID>VPWON_333</poSeriesID>
            </group>
            """;

        JAXBTestUtil.roundTripAndSimilar(update, expected);
    }

    protected GroupUpdate toUpdate(String xml) {
        Reader reader = new StringReader(xml);
        return JAXB.unmarshal(reader, GroupUpdate.class);
    }

}
