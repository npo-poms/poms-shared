package nl.vpro.parkpost;

import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.update.*;
import nl.vpro.parkpost.promo.bind.File;
import nl.vpro.parkpost.promo.bind.PromoEvent;

/**
 * @author Michiel Meeuwissen
 * @since 1.8
 */
@Slf4j
public class PromoEventConverter {

    public static String RELATION_OWNER = "NPO";

    public static String RELATION_VERSION = "PROMO_VERSION";

    public static String RELATION_PRODUCTCODE = "PROMO_PRODUCTCODE";

    public static String RELATION_CHANNEL = "PROMO_CHANNEL";

    public static String RELATION_REFERRER = "PROMO_REFERRER";

    private static final Set<String> IGNORE_EXTENSIONS = new HashSet<>(Arrays.asList("ismv", "ismc"));

    public static ProgramUpdate convert(PromoEvent event, String locationBaseUrl) throws NoMidException, NoPromoException, NoTitleException {
        validate(event);

        ProgramUpdate result = ProgramUpdate.create();
        result.setType(getProgramType(event));

        result.setTitles(new TreeSet<>(Arrays.asList(new TitleUpdate(resolveTitle(event), TextualType.MAIN))));
        result.setDuration(getDuration(event));
        result.setAVType(AVType.VIDEO);
        result.setCrids(Arrays.asList("crid://parkpost/" + event.getProductCode()));

        //result.setCrids(Arrays.asList("crid://parkpost/" + event.getOrderCode()));
        // volgens NEP is event.getOrderCode() unieker
        result.setPublishStartInstant(event.getPlacingWindowStart());
        result.setPublishStopInstant(event.getPlacingWindowEnd());
        if (StringUtils.isNotBlank(event.getBroadcaster())) {
            result.setBroadcasters(Arrays.asList(event.getBroadcaster()));
        }

        String programPrid = event.getPromotedProgramProductCode();
        if (StringUtils.isNotBlank(programPrid)) {
            MemberRefUpdate update = new MemberRefUpdate(1, programPrid);
            result.setMemberOf(new TreeSet<>(Arrays.asList(update)));
        }
        result.setPredictions(new TreeSet<>(Arrays.asList(PredictionUpdate.builder()
                .platform(Platform.INTERNETVOD)
                .publishStart(event.getPlacingWindowStart())
                .publishStop(event.getPlacingWindowEnd())
                .build()
            ))
        );

        addRelations(event, result);
        addLocations(event, result, locationBaseUrl);
        return result;
    }

    private static String resolveTitle(PromoEvent event) {
        String title = event.getTrailerTitle();

        if(StringUtils.isEmpty(title)) {
            title = event.getEpisodeTitle();
        }

        if(StringUtils.isEmpty(title)) {
            title = event.getProgramTitle();
        }
        return title;
    }



    private static void addLocations(PromoEvent event, ProgramUpdate result, String locationBaseUrl) {
        SortedSet<LocationUpdate> locations = new TreeSet<>();
        if (event.getFiles() != null && !event.getFiles().isEmpty()) {
            // https://jira.vpro.nl/browse/MSE-2402
            for (File file : event.getFiles()) {
                String extension = file.getExtension();
                if (IGNORE_EXTENSIONS.contains(extension)) {
                    continue;
                }
                String url = file.getUrl();
                if (StringUtils.isEmpty(url)) {
                    url = locationBaseUrl + event.getProductCode() + '/' + file.getFileName();
                }
                LocationUpdate location = LocationUpdate.builder()
                    .programUrl(url)
                    .duration(getDuration(event))
                    .width(file.getWidth())
                    .height(file.getHeight())
                    .format(file.getFormat())
                    .bitrate(file.getBitrate())
                    .build();
                locations.add(location);

            }
        }
        result.setLocations(locations);
    }

    private static void addRelations(PromoEvent event, ProgramUpdate result) {
        SortedSet<RelationUpdate> relations = new TreeSet<>();

        relations.add(new RelationUpdate(
            RELATION_PRODUCTCODE,
            RELATION_OWNER,
            null,
            event.getProductCode()));

        relations.add(new RelationUpdate(
            RELATION_VERSION,
            RELATION_OWNER,
            null,
            ProductCode.parse(event.getProductCode()).getVersionCode()));

        relations.add(new RelationUpdate(
            RELATION_CHANNEL,
            RELATION_OWNER,
            null,
            event.getNet()));

        relations.add(new RelationUpdate(
            RELATION_REFERRER,
            RELATION_OWNER,
            null,
            event.getReferrer()));

        result.setRelations(relations);
    }

    private static void validate(PromoEvent event) throws NoMidException, NoPromoException, NoTitleException {
        if(event.getPromotedProgramProductCode() == null) {
            throw new NoMidException(event);
        }
        if(event.getPromoType() != ProductCode.Type.P) {
            throw new NoPromoException(event);
        }

        if(StringUtils.isEmpty(resolveTitle(event))) {
            throw new NoTitleException(event);
        }
    }

    protected static java.time.Duration getDuration(PromoEvent promoEvent) {
        Long frames = promoEvent.getFrameCount();

        if(frames == null) {
            return null;
        }

        long duration = 1000 * frames / 25; // 25 fps
        return Duration.ofMillis(duration);
    }

    protected static String getProgramUrl(String productCode, String locationBaseUrl) {
        return locationBaseUrl + productCode + '/' + productCode + ".ism";
    }

    protected static ProgramType getProgramType(PromoEvent promoEvent) {
        switch(promoEvent.getPromoType()) {
            case P:
                return ProgramType.PROMO;
            case T:
                return ProgramType.TRAILER;
        }

        throw new UnsupportedOperationException("Unknown promo type: '" + promoEvent.getPromoType()+ "'");
    }

    public static class NoMidException extends Exception {
        public NoMidException(PromoEvent event) {
            super("PromoEvent{ProductCode=" + event.getProductCode() + ",Type=" + event.getPromoType() + "} lacks a MID, which is necessary for further handling");
        }
    }

    public static class NoPromoException extends Exception {
        public NoPromoException(PromoEvent event) {
            super("PromoEvent{ProductCode=" + event.getProductCode() + ",Type=" + event.getPromoType() + "} is not of Promo type");
        }
    }

    public static class NoTitleException extends Exception {
        public NoTitleException(PromoEvent event) {
            super("PromoEvent{ProductCode=" + event.getProductCode() + ",Type=" + event.getPromoType() + "} has an empty title");
        }
    }
}
