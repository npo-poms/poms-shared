package nl.vpro.domain.media.nebo.enrichment.v2_4;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.*;

import jakarta.xml.bind.JAXB;
import jakarta.xml.datatype.*;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.MediaTestDataBuilder;
import nl.vpro.domain.media.Program;
import nl.vpro.domain.media.nebo.base.FragmentenType;
import nl.vpro.domain.media.nebo.shared.*;
import nl.vpro.domain.media.support.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Michiel Meeuwissen
 */
@SuppressWarnings("deprecation")
public class NeboXmlImportTest {


    public static NeboXmlImport getTestNeboXmlImport(String prid) throws DatatypeConfigurationException {
        NeboXmlImport enrichment = new NeboXmlImport();
        AfleveringType aflevering = new AfleveringType();
        enrichment.setAflevering(aflevering);
        aflevering.setTite("Must have title");
        aflevering.setPrid(prid);
        FragmentenType fragmenten = new FragmentenType();
        aflevering.setFragmenten(fragmenten);
        FragmentenType.Fragment fragment = new FragmentenType.Fragment();
        fragment.setStarttijd(new Date(0L));
        fragment.setEindtijd(new Date(Long.MAX_VALUE));
        fragment.setTrefwoorden("tref woord en");
        fragment.setTitel("must have title too");
        FragmentenType.Fragment.Afbeelding afb = new FragmentenType.Fragment.Afbeelding();
        afb.setTitel("afbeeldingtitel");
        afb.setPad("bla.jpg");
        fragment.setAfbeelding(afb);
        fragmenten.setFragment(Arrays.asList(fragment));
        StreamsType streams = new StreamsType();
        streams.setPublicatieStartdatumtijd(dateToXML(new Date(0)));
        aflevering.setStreams(streams);
        StreamType stream = new StreamType();
        stream.setFormaat(FormatResType.WMV);
        stream.setKwaliteit(QualityResType.BB);
        stream.setValue("http://stream.server/stream.bb.wmv");
        streams.getStream().add(stream);
        return enrichment;
    }

    protected static XMLGregorianCalendar dateToXML(Date date) throws DatatypeConfigurationException {
        DatatypeFactory f = DatatypeFactory.newInstance();
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        return f.newXMLGregorianCalendar(c);
    }


    protected Program getTestProgram() {
        Image icon = new Image();
        icon.setImageUri("http://plaatjes/123.jpg");
        icon.setTitle("Icon titel");
        icon.setDescription("Icon descr");
        icon.setType(ImageType.ICON);
        return MediaTestDataBuilder
            .program()
            .constrained()
            .mid("mijn_mooie_prid")
            .websites("http://www.vpro.nl")
            .emails("bla@foo.bar")
            .images(icon)
            .titles(new Title("Broadcaster subtitle", OwnerType.BROADCASTER, TextualType.SUB))
            .segments(
                MediaTestDataBuilder
                    .segment()
                    .constrained()
                    .descriptions(new Description("Beschrijving", OwnerType.NEBO, TextualType.MAIN))
                    .images(new Image(OwnerType.BROADCASTER, ImageType.STILL, "urn.media.image.123"))
                    .build()
            )
            .build();
    }

    @Test
    public void getProgram() throws DatatypeConfigurationException {
        NeboXmlImport neboXmlImport = getTestNeboXmlImport("nebo_xml_prid");
        ByteArrayOutputStream marshalled = new ByteArrayOutputStream();
        JAXB.marshal(neboXmlImport, marshalled);
        neboXmlImport = JAXB.unmarshal(new ByteArrayInputStream(marshalled.toByteArray()), NeboXmlImport.class);
        assertEquals(0L, neboXmlImport.getAflevering().getFragmenten().getFragment().get(0).getStarttijd().getTime());
    }

    @Test
    public void marshal() {
        Program program = getTestProgram();
        JAXB.marshal(new NeboXmlImport(program), System.out);
    }

    @Test
    public void unmarshal() {
        Program program = getTestProgram();
        assertEquals(1, program.getSegments().first().getImages().size());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        JAXB.marshal(new NeboXmlImport(program), out);
        NeboXmlImport xml = JAXB.unmarshal(new ByteArrayInputStream(out.toByteArray()), NeboXmlImport.class);
        assertEquals(1, program.getSegments().first().getImages().size());


        Program newProgram = xml.getAflevering().getProgram();
        assertEquals(program.getMainTitle(), newProgram.getMainTitle());
        assertEquals(program.getSubTitle(), newProgram.getSubTitle());
        //assertEquals(program.getEpisodeTitle(), newProgram.getEpisodeTitle());

        assertEquals(program.getSegments().size(), newProgram.getSegments().size());
        assertEquals(
            program.getSegments().first().getStart(),
            newProgram.getSegments().first().getStart()
        );
        assertEquals(
            program.getSegments().first().getDuration().get(),
            newProgram.getSegments().first().getDuration().get()
        );
        assertEquals(
            program.getSegments().first().getMainTitle(),
            newProgram.getSegments().first().getMainTitle()
        );
        assertEquals(
            program.getSegments().first().getMainDescription(),
            newProgram.getSegments().first().getMainDescription()
        );

        assertEquals(
            program.getSegments().first().getMainDescription(),
            newProgram.getSegments().first().getMainDescription()
        );


        assertEquals(1, newProgram.getSegments().first().getImages().size());

        assertEquals(
            program.getSegments().first().getImages().get(0),
            newProgram.getSegments().first().getImages().get(0)
        );

        assertEquals(
            program.getImages().get(0).getImageUri(),
            newProgram.getImages().get(0).getImageUri()
        );


        assertEquals(
            program.getMid(),
            newProgram.getMid()
        );
    }

    @Test
    public void unmarshal2() {
        String test = """
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <nebo_xml_import type="verrijking" versie="2.3">
                <aflevering prid='myprid'>
                    <atit>Episode title MIS</atit>
                    <fragmenten>
                        <fragment>
                            <eindtijd>03:02:00</eindtijd>
                            <afbeelding>
                                <pad>urn.media.image.123</pad>
                            </afbeelding>
                            <omschrijving>Beschrijving</omschrijving>
                            <starttijd>01:02:00</starttijd>
                            <titel>Main title</titel>
                        </fragment>
                    </fragmenten>
                    <gids_tekst>Main title</gids_tekst>
                    <tite>Main title</tite>
                </aflevering>
            </nebo_xml_import>
            """;

        NeboXmlImport xml = JAXB.unmarshal(new ByteArrayInputStream(test.getBytes()), NeboXmlImport.class);
        assertEquals(1, xml.getAflevering().getFragmenten().getFragment().size());
        assertNotNull(xml.getAflevering().getFragmenten().getFragment().get(0).getAfbeelding());
        assertEquals("urn.media.image.123", xml.getAflevering().getFragmenten().getFragment().get(0).getAfbeelding().getPad());

    }
}
