package nl.vpro.parkpost;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import javax.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.update.ProgramUpdate;
import nl.vpro.domain.user.ServiceLocator;
import nl.vpro.parkpost.promo.bind.PromoEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
public class PromoEventConverterTest {

    private PromoEvent event;

    private final String locationBaseUrl = "http://adaptive.npostreaming.nl/u/npo/promo/";

    @BeforeEach
    public void setUp() {
        event = new PromoEvent();
        event.setProductCode("1P1302AK_BOEKEN4");
        event.setPromotedProgramProductCode("VPROWON_12345");
        event.setPromoType(ProductCode.Type.P);
        event.setProgramTitle("Program Title");
        ServiceLocator.setBroadcasterService("VPRO");
    }

    @Test
    public void testConvertWithoutMid() throws Exception {
        assertThatThrownBy(() -> {
            event.setPromotedProgramProductCode(null);
            PromoEventConverter.convert(event, locationBaseUrl);
        }).isInstanceOf(PromoEventConverter.NoMidException.class);
    }

    @Test
    public void testConvertOnNonPromoType() throws Exception {
        assertThatThrownBy(() -> {
            event.setPromoType(ProductCode.Type.A);
            PromoEventConverter.convert(event, locationBaseUrl);
        }).isInstanceOf(PromoEventConverter.NoPromoException.class);
    }

    @Test
    public void testConvertWithEmptyTitle() throws Exception {
        assertThatThrownBy(() -> {

            event.setProgramTitle("");
            PromoEventConverter.convert(event, locationBaseUrl);
        }).isInstanceOf(PromoEventConverter.NoTitleException.class);
    }

    @Test
    public void testConvertForProductCodeRelation() throws Exception {
        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getRelations()).contains(new Relation(new RelationDefinition("PROMO_PRODUCTCODE", "NPO"), null, event.getProductCode()));
    }

    @Test
    public void testConvertForPromoVersionRelation() throws Exception {
        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getRelations()).contains(new Relation(new RelationDefinition("PROMO_VERSION", "NPO"), null, "AK"));
    }

    @Test
    public void testConvertForPromoChannelRelation() throws Exception {
        event.setNet("NED1");
        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getRelations()).contains(new Relation(new RelationDefinition("PROMO_CHANNEL", "NPO"), null, "NED1"));
    }

    @Test
    public void testConvertForPromoRefererRelation() throws Exception {
        event.setReferrer("Morgen");
        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getRelations()).contains(new Relation(new RelationDefinition("PROMO_REFERRER", "NPO"), null, "Morgen"));
    }

    @Test
    public void testConvertForPlacingWindowStart() throws Exception {
        Instant start = Instant.ofEpochMilli(12345);
        event.setPlacingWindowStart(start);

        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getPublishStartInstant()).isEqualTo(start);
    }

    @Test
    public void testPlacingWindowEnd() throws Exception {
        Instant end = Instant.ofEpochMilli(12345);
        event.setPlacingWindowEnd(end);

        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();

        assertThat(update.getPublishStopInstant()).isEqualTo(end);
    }

    @Test
    public void testConvertOnLocations() throws Exception {
        Program update = PromoEventConverter.convert(event, locationBaseUrl).fetch();
        event.setFrameCount(100L);

        assertThat(update.getLocations()).isEmpty();
    }

    @Test
    public void testConvert() throws Exception {
        event.setProductCode("1P1302AK_BOEKEN4");
        event.setOrderCode("1P110213_VPR_BOEKEN______");
        event.setPromotedProgramProductCode("VPRO_1234");
        event.setFrameCount(100L);
        event.setBroadcaster("VPRO");
        event.setTrailerTitle("Trailer-titel");
        event.setNet("1");
        event.setReferrer("Aankondiging");

        ProgramUpdate update = PromoEventConverter.convert(event, locationBaseUrl);

        assertEquals("VPRO_1234", update.getMemberOf().iterator().next().getMediaRef());
        assertEquals(1000 * 4, update.getDuration().toMillis());

        assertEquals("VPRO", update.getBroadcasters().get(0));
        assertEquals("Trailer-titel", update.getTitles().first().get());
        assertEquals(TextualType.MAIN, update.getTitles().first().getType());

        assertEquals("crid://parkpost/1P1302AK_BOEKEN4", update.getCrids().get(0));
        assertThat(update.getDuration()).isEqualTo(Duration.ofSeconds(4));
    }

    @Test
    public void testConvertFiles() throws Exception {
        PromoEvent event = JAXB.unmarshal(getClass().getResourceAsStream("/parkpost/BP0702VD_2_HOLLANDS.xml"), PromoEvent.class);
        ProgramUpdate update = PromoEventConverter.convert(event, locationBaseUrl);
        assertThat(update.getLocations()).hasSize(2);
        assertThat(update.getLocations().first().getProgramUrl()).isEqualTo("http://adaptive.npostreaming.nl/u/npo/promo/1P1302AK_BOEKEN4/1P1302AK_BOEKEN4.ism");
        assertThat(new ArrayList<>(update.getLocations()).get(1).getAvAttributes().getBitrate()).isEqualTo(1000000);

    }

    @Test
    public void testConvertFiles2() throws Exception {
        PromoEvent event = JAXB.unmarshal(getClass().getResourceAsStream("/parkpost/parkpost.xml"), PromoEvent.class);
        ProgramUpdate update = PromoEventConverter.convert(event, locationBaseUrl);
        assertThat(update.getLocations()).hasSize(1);
        assertThat(update.getLocations().first().getProgramUrl()).isEqualTo("http://adaptive.npostreaming.nl/u/npo/promo/1P0203MO_JOCHEMMY/1P0203MO_JOCHEMMY.ism");
        assertThat(new ArrayList<>(update.getLocations()).get(0).getAvAttributes().getBitrate()).isNull();

    }
}
