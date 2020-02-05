package nl.vpro.domain.npo;

import java.time.Instant;
import java.time.ZoneId;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.npo.notify.v3_0.Notify;
import nl.vpro.domain.npo.restriction.v2_2.Tijdsbeperking;
import nl.vpro.domain.npo.revoke.v3_0.Revoke;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.domain.user.Broadcasters;
import nl.vpro.xml.util.XmlUtils;

/**
 * @author Michiel Meeuwissen
 * @since 4.3
 */
public class Utils {

    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Amsterdam"); // Restrictions seem to be shipped _without_ explicit timezones.

    public static Instant getStart(Tijdsbeperking tijdsbeperking) {
        return tijdsbeperking == null ? null : XmlUtils.toInstant(ZONE_ID, tijdsbeperking.getStarttijd());
    }

    public static Instant getEind(Tijdsbeperking tijdsbeperking) {
        return tijdsbeperking == null ? null : XmlUtils.toInstant(ZONE_ID, tijdsbeperking.getEindtijd());
    }

    public static boolean equals(Notify n1 , Notify n2) {
        if (!Objects.equals(n1.getPlatform(), n2.getPlatform())) {
            return false;
        }
        if (!Objects.equals(n1.getPrid(), n2.getPrid())) {
            return false;
        }
        if (! Objects.equals(n1.getTimestamp(), n2.getTimestamp())) {
            return false;
        }

        return true;

    }

    public static boolean equals(nl.vpro.domain.npo.restriction.v2_2.Restriction r1, nl.vpro.domain.npo.restriction.v2_2.Restriction r2) {
        return
            Objects.equals(r1.getPrid(), r2.getPrid()) &&
                Objects.equals(r1.getTimestamp(), r2.getTimestamp()) &&
                equals(r1.getGeoiprestrictie(), r2.getGeoiprestrictie()) &&
                equals(r1.getLeeftijdsbeperking(), r2.getLeeftijdsbeperking()) &&
                Objects.equals(r1.getTitel(), r2.getTitel()) &&
                equals(r1.getOmroepen(), r2.getOmroepen());
        }

    public static boolean equals(Revoke r1, Revoke r2) {
        return
            Objects.equals(r1.getPrid(), r2.getPrid()) &&
                Objects.equals(r1.getTimestamp(), r2.getTimestamp()) &&
                Objects.equals(r1.getTitel(), r2.getTitel()) &&
                equals(r1.getOmroepen(), r2.getOmroepen());
    }


    public static boolean equals(nl.vpro.domain.npo.restriction.v2_2.Omroepen   o1, nl.vpro.domain.npo.restriction.v2_2.Omroepen o2) {
        if (o1 == null || o2 == null) {
            return o1 == null && o2 == null;
        }
        return Objects.equals(o1.getOmroep(), o2.getOmroep());
    }

    public static boolean equals(nl.vpro.domain.npo.revoke.v3_0.Omroepen  o1, nl.vpro.domain.npo.revoke.v3_0.Omroepen o2) {
        if (o1 == null || o2 == null) {
            return o1 == null && o2 == null;
        }
        return Objects.equals(o1.getOmroep(), o2.getOmroep());
    }

    public static boolean equals(nl.vpro.domain.npo.restriction.v2_2.Leeftijdsbeperking l1, nl.vpro.domain.npo.restriction.v2_2.Leeftijdsbeperking l2) {
        if (l1 == null || l2 == null) {
            return l1 == null && l2 == null;
        }
        return Objects.equals(l1.getLeeftijd(), l2.getLeeftijd());
    }

    public static boolean equals(nl.vpro.domain.npo.restriction.v2_2.Geoiprestrictie g1, nl.vpro.domain.npo.restriction.v2_2.Geoiprestrictie g2) {
        if (g1 == null || g2 == null) {
            return g1 == null && g2 == null;
        }
        return Objects.equals(g1.getGeoiplabel(), g2.getGeoiplabel());
    }

    public static Program toProgram(BroadcasterService broadcasterService, nl.vpro.domain.npo.notify.v3_0.Notify notify, OwnerType owner) {
        MediaBuilder.ProgramBuilder builder = notifyBuilder();
        builder.mid(notify.getPrid());
        builder.mainTitle(notify.getTitel(), owner);

        builder.broadcasters(
            notify.getOmroepen().getOmroep().stream()
                .map(StringUtils::trim)
                .map(o -> Broadcasters.getByAnyId(broadcasterService, o).orElse(null))
                .filter(Objects::nonNull)
                .toArray(Broadcaster[]::new)
        );
        return builder.build();
    }

    public static Program toProgram(BroadcasterService broadcasterService,nl.vpro.domain.npo.notify.v3_2.Notify notify, OwnerType owner) {
        MediaBuilder.ProgramBuilder builder = notifyBuilder();
        builder.mid(notify.getPrid());
        builder.mainTitle(notify.getTitel(), owner);
        builder.broadcasters(
            notify.getOmroepen().getOmroep().stream()
                .map(StringUtils::trim)
                .map(o -> Broadcasters.getByAnyId(broadcasterService, o).orElse(null))
                .filter(Objects::nonNull)
                .toArray(Broadcaster[]::new)
        );
        return builder.build();
    }

    protected static MediaBuilder.ProgramBuilder notifyBuilder() {
        MediaBuilder.ProgramBuilder builder = MediaBuilder.program();
        builder.type(ProgramType.BROADCAST);
        builder.avType(AVType.VIDEO);
        return builder;
    }

    public static Program toProgram(nl.vpro.domain.npo.forecast.v2_0.Aflevering aflevering, OwnerType owner) {
        MediaBuilder.ProgramBuilder builder = notifyBuilder();
        builder.mid(aflevering.getPrid());
        builder.mainTitle(aflevering.getTitel(), owner);
        AuthorityPlatform platform  = AuthorityPlatform.valueOf(aflevering
                .getPlatform()
                .toLowerCase()
        );
        builder.predictions(new Prediction(platform.getDomainPlatform(), Prediction.State.ANNOUNCED));
        return builder.build();

    }


    public static String toString(nl.vpro.domain.npo.notify.v3_0.Notify notify) {
        return notify.getPrid() + " " + notify.getTitel();
    }


    public static String toString(nl.vpro.domain.npo.notify.v3_2.Notify notify) {
        return notify.getPrid() + " " + notify.getTitel();
    }


    public static String toString(nl.vpro.domain.npo.restriction.v2_1.Restriction restriction) {
        return restriction.getPrid() + " " + restriction.getTitel();
    }

    public static String toString(Revoke pluto) {
        return pluto.getPrid() + " " + pluto.getTitel();
    }
}
