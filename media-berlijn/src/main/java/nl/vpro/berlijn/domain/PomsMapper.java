package nl.vpro.berlijn.domain;

import lombok.extern.log4j.Log4j2;

import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.*;
import org.springframework.stereotype.Service;

import com.google.common.annotations.VisibleForTesting;

import nl.vpro.berlijn.domain.epg.EPGContents;
import nl.vpro.berlijn.domain.epg.EPGEntry;
import nl.vpro.berlijn.domain.productmetadata.Genre;
import nl.vpro.berlijn.domain.productmetadata.*;
import nl.vpro.berlijn.domain.productmetadata.Language;
import nl.vpro.domain.TextualObject;
import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.ContentRating;
import nl.vpro.domain.media.MediaType;
import nl.vpro.domain.media.Person;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.subtitles.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;
import nl.vpro.logging.Log4j2Helper;
import nl.vpro.logging.simple.Level;
import nl.vpro.util.TextUtil;

import static java.util.Optional.ofNullable;

@Service
@Log4j2
public class PomsMapper {
    public static final OwnerType OWNER = OwnerType.MIS;

    private final BroadcasterService broadcasterService;

    private final ClassificationService classificationService;

    private final SubtitlesProvider subtitlesService;

    @Inject
    public PomsMapper(BroadcasterService broadcasterService, ClassificationService classificationService, SubtitlesProvider subtitlesService) {
        this.broadcasterService = broadcasterService;
        this.classificationService = classificationService;
        this.subtitlesService = subtitlesService;
    }


    public MediaObject createIfEmpty(@Nullable MediaObject m, ProductMetadata productMetadata) {
        var mid = productMetadata.getMid();
        var type = productMetadata.getMediaType();
        if (m == null) {
            m = type.getMediaInstance();
            m.setMid(mid);
        } else {
            m.setMediaType(type);
        }
        return m;
    }

    /**
     * Maps content from the berlijn {@link ProductMetadataContents product metatadata topic} to a {@link MediaObject}
     * Doesn't touch e.g. scheduleevents, which are filled by {@link #map(Channel, LocalDate, EPGEntry)}
     */
    public void map(ProductMetadataContents contents, MediaObject mo) {

        ofNullable(contents.title())
            .filter(StringUtils::isNotBlank)
            .ifPresentOrElse(
                t -> mo.addTitle(t, OWNER, TextualType.SUB),
                () -> mo.removeTitle(OWNER, TextualType.SUB)
            );

        ofNullable(contents.displayTitle())
            .filter(StringUtils::isNotBlank)
            .ifPresentOrElse(
                t -> mo.addTitle(t, OWNER, TextualType.MAIN),
                () -> mo.removeTitle(OWNER, TextualType.MAIN)
            );

        if (mo.getMainTitle() == null  && mo.getMediaType() == MediaType.SEASON) {
            mo.addTitle("Seizoen", OwnerType.TEMPORARY, TextualType.MAIN);
        }

        if (mo.getMainTitle() == null  && mo.getMediaType() == MediaType.SERIES) {
            mo.addTitle("Serie", OwnerType.TEMPORARY, TextualType.MAIN);
        }

        mo.setAVType(ofNullable(contents.mediaType())
            .map(mt -> AVType.valueOf(mt.name().toUpperCase()))
            .orElseGet(() -> mo instanceof Group ? AVType.MIXED : AVType.UNKNOWN));

        mo.setBroadcasters(mapBroadcaster(streamNullable(contents.broadcasters())));

        mo.setGenres(mapGenre(streamNullable(contents.genres())));


        ofNullable(contents.contentRating()).ifPresentOrElse(
            cr -> {
                mo.setContentRatings(cr.nicamContent().stream()
                    .map(c -> ContentRating.valueOf(c.code()))
                    .collect(Collectors.toList()));
                mo.setAgeRating(Optional.ofNullable(cr.nicamAge()).map(NicamAge::toAgeRating).orElse(null));
            },
            () -> {
                mo.setContentRatings(Collections.emptyList());
                mo.setAgeRating(null);
            }
        );

        mo.setCredits(streamNullable(contents.castAndCrew()).map(c -> {
            var person = new Person();
            person.setRole(RoleType.valueOf(c.role().toUpperCase()));
            person.setFamilyName(c.person().familyName());
            person.setGivenName(c.person().givenName());

            if (c.person().id() != null) {
                person.setExternalId("berlijn:" + c.person().id());
            }
            return person;
        }).collect(Collectors.toList()));

        mo.setCountries(ofNullable(contents.productionCountry()).stream().toList());

        mo.setIsDubbed(false);
        mo.setLanguages(
            streamNullable(contents.languages())
                .filter(l -> l.language() != null)
                .peek(l -> {
                    if (l.usage() == Language.Usage.dubbed) {
                        mo.setIsDubbed(true);
                    }
                })
                .map(l -> new UsedLanguage(l.language().toLocale(), UsedLanguage.usageOf(l.usage())))
                .collect(Collectors.toList()));



        streamNullable(contents.signLanguages()).map(SignLanguage::type).forEach(lc -> {
            mo.getLanguages().add(new UsedLanguage(lc.toLocale(), UsedLanguage.Usage.SIGNING));
        });

        mapAvailableSubtitles(contents, mo);
        optionalSynopsisToDescription(ofNullable(contents.synopsis()), mo);
    }

    protected void optionalSynopsisToDescription(Optional<Synopsis> synopsis, TextualObject<?, ?, ?> mo) {
        synopsis.ifPresentOrElse(s ->
            synopsisToDescription(s, mo),
            () -> deleteSynopsisFromDescription(mo)
        );
    }

    private void synopsisToDescription(Synopsis synopsis, TextualObject<?, ?, ?> mo) {
        mo.setDescription(unhtml(synopsis.longText()), OWNER, TextualType.MAIN);
        mo.setDescription(unhtml(synopsis.shortText()), OWNER, TextualType.SHORT);
        mo.setDescription(unhtml(synopsis.brief()), OWNER, TextualType.KICKER);
    }
    private void deleteSynopsisFromDescription(TextualObject<?, ?, ?> mo) {
        mo.removeDescription(OWNER, TextualType.MAIN);
        mo.removeDescription(OWNER, TextualType.SHORT);
        //mo.removeDescription(OWNER, TextualType.KICKER);// not incoming, so not destroying? Was that the reason to comment this out?
    }


    private ScheduleEvent map(Channel channel, LocalDate guideDate, EPGEntry entry) {
        if (entry.guideStartTime() == null) {
            log.warn("No start time in {}", entry); //https://publiekeomroep.atlassian.net/browse/VPPM-2235
            return null;
        }
        if (! Objects.equals(entry.guideStartTime(), entry.startTime())) {
            log.debug("A difference! {}", entry);
        }
        Duration duration = Duration.ofSeconds(entry.duration());

        var event = ScheduleEvent.builder()
            .channel(channel)
            .guideDay(guideDate)
            .start(entry.guideStartTime())// startTime?
            .duration(Duration.ofSeconds(entry.duration()))
            .midRef(entry.prid())
            .repeat(entry.isRerun() ? Repeat.rerun() : null)
            .guci(entry.guci())
            .effectiveStart(entry.startTime())
            .build();
        Optional.ofNullable(entry.productOverride()).ifPresent(p -> {
            // TODO, it seems that all these fields are _always_ overridden?
            // https://publiekeomroep.atlassian.net/browse/VPPM-2246
            optionalSynopsisToDescription(ofNullable(p.synopsis()), event);
            }
        );
        return event;
    }

    public MediaTable map(EPGContents epg) {
        var channel = Channel.findByPDId(epg.channelId());
        var guideDate = epg.date();

        Schedule schedule = new Schedule(channel, guideDate);
        schedule.setStart(epg.periodStart());
        schedule.setStop(epg.periodEnd());

        MediaTable table = new MediaTable();
        table.setSchedule(schedule);

        for (EPGEntry entry : epg.entries()) {


            ScheduleEvent event = map(channel, guideDate, entry);
            if (event == null) {
                continue;
            }
            try {
                entry.assertValid();
                schedule.addScheduleEvent(event);


                var programByMid = table.find(entry.prid()).orElse(null);
                var crid = "crid://npo/programmagegevens/" + entry.crid();
                if (programByMid == null) {
                    Program program = new Program(entry.prid());
                    program.addCrid(crid);
                    table.add(program);
                } else {
                    programByMid.addCrid(crid);
                }
            } catch (AssertionError ae) {
                log.error(ae);
            }
        }
        return table;

    }


    @VisibleForTesting
    Set<nl.vpro.domain.media.Genre> mapGenre(Stream<Genre> genre) {
        record GenreMapResult(String uri, List<Term> result) {}

        return genre
            .filter( g -> g.type() == GenreType.secondary)
            .map(Genre::code)
            .map(code -> "urn:tva:metadata:cs:2004:" + code)
            .map(uri -> new GenreMapResult(uri, classificationService.getTermsByReference(uri)))
            .peek(l -> {
                if (l.result().isEmpty()) {
                    throw new IllegalArgumentException("No genre matched for " + l.uri());
                }
            })
            .map(GenreMapResult::result)
            .flatMap(Collection::stream)
            .map(nl.vpro.domain.media.Genre::new)
            .collect(Collectors.toSet());
    }

    private void mapAvailableSubtitles(ProductMetadataContents contents, MediaObject mo) {

         SortedSet<AvailableSubtitles> incomingSubtitles = streamNullable(contents.captionLanguages())
            .map(l ->
                AvailableSubtitles.builder()
                    .language(new Locale(l.language().code()))
                    .type(l.closed() ? SubtitlesType.CAPTION : SubtitlesType.TRANSLATION)
                    .workflow(SubtitlesWorkflow.MISSING)
                    .build()
            ).collect(Collectors.toCollection(TreeSet::new));



         // if we actually _have_ the subtitles they overrule this, so write those back.
        subtitlesService.list(contents.prid())
            .stream()
            .filter(s -> s.getWorkflow() != SubtitlesWorkflow.DELETED)
            .map(AvailableSubtitlesUtil::toAvailable)
            .peek(s -> {
                    if (incomingSubtitles.contains(s)) {
                        log.info("Overriding {}", s);
                    } else {
                        log.info("Adding {}", s);
                    }
                }
            )
            .forEach(incomingSubtitles::add);

        mo.setAvailableSubtitles(incomingSubtitles);

        mo.setReleaseYear(ofNullable(contents.productionYear())
            .map(Integer::shortValue)
            .orElse(null)
        );


    }

    private final Set<String> warned = Collections.synchronizedSet(new HashSet<>());

    private List<Broadcaster> mapBroadcaster(Stream<String> broadcaster) {
        record IdAndResult(String id, Broadcaster result) {}

        return broadcaster
            .filter(i -> ! i.isBlank()) // VPPM-2240, yet another work around messy metadata.
            .map(i -> new IdAndResult(i, broadcasterService.findForIds(i).orElse(null)))
            .peek(b -> {
                if (b.result() == null) {
                    log.warn("No broadcaster found for {}", b.id());
                } else {
                    if (!b.result().getWhatsOnId().equalsIgnoreCase(b.id())) {
                        var newWarning = warned.add(b.id());
                        Log4j2Helper.log(log, newWarning ? Level.WARN : Level.DEBUG, "Broadcaster {} did not match on whatson id {}", b.result(), b.id());
                    }
                }
            })
            .map(IdAndResult::result)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }




    @NonNull
    public static <T> Stream<T> streamNullable(@Nullable Collection<T> list) {
        return (list != null ? list.stream() : Stream.empty());
    }


    @PolyNull
    public static String unhtml(@PolyNull String in)  {
        if (in == null) {
            return null;
        }
        if (! TextUtil.isValid(in, false)) {
            log.warn("Invalid text incoming {}", in);
            return TextUtil.unhtml(in);
        } else {
            return in;
        }
    }

    public static <T> Comparator<T> randomOrder(Random r) {

        int x = r.nextInt(), y = r.nextInt();
        return Comparator.comparingInt( s -> Integer.reverse((s.hashCode()&x)^y));
    }


}
