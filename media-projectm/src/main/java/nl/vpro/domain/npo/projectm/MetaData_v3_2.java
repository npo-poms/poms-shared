package nl.vpro.domain.npo.projectm;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

import javax.xml.datatype.XMLGregorianCalendar;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.classification.TermId;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.npo.projectm.metadata.v3_2.*;
import nl.vpro.domain.user.*;
import nl.vpro.util.TextUtil;
import nl.vpro.xml.util.XmlUtils;

/**
 * @author Michiel Meeuwissen
 * @since 3.0
 */
@SuppressWarnings("Duplicates")
@Slf4j
public class MetaData_v3_2 {

    protected static final String imageFormat = "s720";


    public static Aflevering createAflevering(Program program, MediaProvider provider) {
        Aflevering aflevering = new Aflevering();

        aflevering.setPrid(program.getMid());
        aflevering.setPridexport(program.getMid());
        aflevering.setTitel(program.getMainTitle());
        aflevering.setLexicoTitel(program.getLexicoTitle());
        aflevering.setIcon(getIcon(program));
        aflevering.setInhk(TextUtil.truncate(getDescription(program, TextualType.SHORT), 256));
        aflevering.setKykw(MediaObjects.getKijkwijzer(program));
        aflevering.setOrti(getTitle(program, TextualType.ORIGINAL));
        aflevering.setAfltitel(getTitle(program, TextualType.SUB));
        aflevering.setMail(firstOrNull(program.getEmail()));
        aflevering.setWebs(firstOrNull(program.getWebsites()));
        aflevering.setTwitteraccount(SocialRef.getValueOrNull(MediaObjects.getSocialAccount(program)));
        aflevering.setTwitterhashtag(SocialRef.getValueOrNull(MediaObjects.getSocialHash(program)));
        aflevering.setGenre(getGenre(program));
        aflevering.setSubgenre(getSubGenre(program));
        aflevering.setOmroepen(createOmroepen(program, ServiceLocator.getBroadcasterService()));
        aflevering.setPersonen(createPersonen(program));


        if (program.isEpisode()) {
            MemberRef seasonRef = getEpisodeRef(program, MediaType.SEASON);
            if (seasonRef != null) {
                Group season = provider.findByMid(seasonRef.getMidRef());
                if (season != null) {
                    Serie serie = createSerie(season);
                    aflevering.setAflnr(String.valueOf(seasonRef.getNumber()));
                    aflevering.setSerie(serie);
                    MemberRef serieRef = getMemberRef(season, MediaType.SERIES);
                    if (serieRef != null) {
                        Group series = provider.findByMid(serieRef.getMidRef());
                        if (series != null) {
                            Parentserie parent = createParentserie(series);
                            aflevering.setParentserie(parent);
                        } else {
                            log.warn("Series {} could not be found in {}", serieRef.getMidRef(), provider);
                        }
                    }
                } else {
                    log.warn("Season {} could not be found in {}", seasonRef.getMidRef(), provider);
                }
            }
        }

        aflevering.setTimestamp(getCalendar(program.getLastModifiedInstant()));

        return aflevering;

    }

    public static Serie createSerie(Group group) {
        Serie serie = new Serie();
        serie.setSrid(group.getMid());
        serie.setTitel(group.getMainTitle());
        serie.setLexicoTitel(group.getLexicoTitle());
        serie.setIcon(getIcon(group));
        serie.setOrti(getTitle(group, TextualType.ORIGINAL));
        serie.setMail(firstOrNull(group.getEmail()));
        serie.setWebs(firstOrNull(group.getWebsites()));
        serie.setTwitteraccount(SocialRef.getValueOrNull(MediaObjects.getSocialAccount(group)));
        serie.setTwitterhashtag(SocialRef.getValueOrNull(MediaObjects.getSocialHash(group)));
        serie.setGenre(getGenre(group));
        serie.setSubgenre(getSubGenre(group));
        serie.setOmroepen(createOmroepen(group, ServiceLocator.getBroadcasterService()));
        serie.setInhl(TextUtil.truncate(group.getMainDescription(), 4096));
        return serie;
    }
    public static Parentserie createParentserie(Group group) {
        Parentserie parentserie = new Parentserie();
        parentserie.setPsrid(group.getMid());
        parentserie.setTitel(group.getMainTitle());
        return parentserie;
    }

    protected static String getIcon(MediaObject media) {
        if (media.getImages().size() == 0) {
            return null;
        }

        Image image = media.findImage(ImageType.ICON);
        if (image == null) {
            image = media.getImages().get(0);
        }

        if (image == null) {
            return null;
        }

        return Images.getImageLocation(image, "jpg", imageFormat);
    }
    protected static String getDescription(MediaObject media, TextualType type) {
        Description description = media.findDescription(type);
        return description != null ? description.get() : null;
    }

    protected static String getTitle(MediaObject media, TextualType type) {
        Title description = media.findTitle(type);
        return description != null ? description.get() : null;
    }
    protected static String firstOrNull(Collection<? extends Supplier<String>> col) {
        if (col == null || col.isEmpty()) return null;
        return col.iterator().next().get();
    }


    protected static String getGenre(MediaObject mediaObject) {
        if (mediaObject.getGenres().isEmpty()) {
            return null;
        }
        return getGenreAtLevel(mediaObject.getGenres().first(), 4);

    }


    protected static String getSubGenre(MediaObject mediaObject) {
        if (mediaObject.getGenres().isEmpty()) {
            return null;
        }
        return getGenreAtLevel(mediaObject.getGenres().first(), 5);
    }

    protected static String getGenreAtLevel(Genre genre, int level) {
        TermId id = new TermId(genre.getTermId());
        int[] parts = id.getParts();
        if (parts.length < level) {
            return null;
        }
        parts = Arrays.copyOf(parts, level);
        id = new TermId(parts);
        return ClassificationServiceLocator.getInstance().getTerm(id.toString()).getName();
    }

    protected static Omroepen createOmroepen(MediaObject mediaObject, BroadcasterService broadcasterService) {

        List<Broadcaster> broadcasters = mediaObject.getBroadcasters();
        if (broadcasters.isEmpty()) {
            return null;
        }
        Omroepen omroepen = new Omroepen();
        for (Broadcaster broadcaster : broadcasters) {
            if (broadcasterService != null) {
                broadcaster = broadcasterService.find(broadcaster.getId());
            }
            omroepen.getOmroep().add(broadcaster.getMisId());
        }
        return omroepen;
    }

    protected static Personen createPersonen(MediaObject mediaObject) {

        List<Person> persons = mediaObject.getPersons();
        if (persons.isEmpty()) {
            return null;
        }
        Personen personen = new Personen();
        for (Person person : persons) {
            Persoon persoon = new Persoon();
            persoon.setNaam(person.getGivenName()+" "+person.getFamilyName());
            Rol rol = new Rol();
            persoon.getRol().add(person.getRole().toString());
            personen.getPersoon().add(persoon);
        }
        return personen;

    }

    protected static XMLGregorianCalendar getCalendar(Instant date) {
        return XmlUtils.toXml(Schedule.ZONE_ID, date);
    }

    private static MemberRef getEpisodeRef(Program media, MediaType type) {
        for (MemberRef memberRef : media.getEpisodeOf()) {
            if (type.equals(memberRef.getType())) {
                return memberRef;
            }
        }
        return null;
    }

    private static MemberRef getMemberRef(MediaObject media, MediaType type) {
        for (MemberRef memberRef : media.getMemberOf()) {
            if (type.equals(memberRef.getType())) {
                return memberRef;
            }
        }
        return null;
    }


}
