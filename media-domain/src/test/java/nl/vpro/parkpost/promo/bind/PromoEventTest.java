/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.parkpost.promo.bind;

import java.io.*;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * See https://jira.vpro.nl/browse/MSE-1324
 *
 * @author Roelof Jan Koekoek
 * @since 1.8
 */
public class PromoEventTest {

    @Test
    public void testBinding() {
        String input = """
            <?xml version="1.0" encoding="utf-8"?>
            <NPO_gfxwrp>
            \t<ProductCode>KP2702MO_KOEKEI</ProductCode>
            \t<OrderCode>3P130227_EO___KOEK___E____</OrderCode>
            \t<PromotedProgramProductCode >EO_101196643</PromotedProgramProductCode >
            \t<Referrer>Morgen</Referrer>
            \t<MXF_Name>MXF51236100</MXF_Name>
            \t<ProgramTitle/>
            \t<EpisodeTitle>Koek </EpisodeTitle>
            \t<Net>3</Net>
            \t<PromoType>P</PromoType>
            \t<TrailerTitle/>
            \t<SerieTitle>Koek </SerieTitle>
            \t<FrameCount>0</FrameCount>
            \t<VideoFormat/>
            \t<FirstTransmissionDate>2013-02-10T20:28:45+01:00</FirstTransmissionDate>
            \t<PlannedTransmissionDate>0000-00-00 00:00:00</PlannedTransmissionDate>
            \t<PlacingWindowStart>2013-02-26T00:00:00+01:00</PlacingWindowStart>
            \t<PlacingWindowEnd>2013-02-26T23:59:59+01:00</PlacingWindowEnd>
            </NPO_gfxwrp>""";

        Reader reader = new StringReader(input);
        PromoEvent event = JAXB.unmarshal(reader, PromoEvent.class);

        assertThat(event.getFiles()).isNull();

        Writer writer = new StringWriter();
        JAXB.marshal(event, writer);
        String output = writer.toString();

        Diff diff = DiffBuilder.compare(input).withTest(output).ignoreWhitespace().build();
        assertFalse(diff.hasDifferences(), diff + " " + output);
    }

    @Test
    public void testFiles() {
        PromoEvent event = JAXB.unmarshal(getClass().getResourceAsStream("/parkpost/BP0702VD_2_HOLLANDS.xml"), PromoEvent.class);

        assertThat(event.getFiles()).hasSize(2);
        System.out.println(event.getFiles().get(0).getUrl());
        assertThat(event.getFiles().get(0).getUrl()).isEqualTo("http://adaptive.npostreaming.nl/u/npo/promo/1P1302AK_BOEKEN4/1P1302AK_BOEKEN4.ism");
    }

    @Test
    public void testFiles2() {
        PromoEvent event = JAXB.unmarshal(getClass().getResourceAsStream("/parkpost/parkpost.xml"), PromoEvent.class);
        assertThat(event.getFiles()).hasSize(6);
        assertThat(event.getFiles().get(0).getFileName()).isEqualTo("1P0203MO_JOCHEMMY.ismc");
    }
}
