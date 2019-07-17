package nl.vpro.domain.page;

import lombok.extern.slf4j.Slf4j;

import java.net.URISyntaxException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;;

import nl.vpro.domain.classification.ClassificationService;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.classification.TermNotFoundException;
import nl.vpro.domain.media.MediaObject;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.page.update.*;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.domain.user.BroadcasterService;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */

@Slf4j
public class PageBuilder<PB extends PageBuilder<PB, P>, P extends Page> {

    protected final P page;

    public static PageBuilder page(PageType type) {
        return new DefaultPageBuilder(new Page(type));
    }
    public static PageBuilder article(){
        return page(PageType.ARTICLE);
    }

    public static PageBuilder article(String url) {
        return article().url(url);
    }

    public static PageBuilder movie() {
        return page(PageType.MOVIE);
    }

    public static PageBuilder movie(String url) {
        return movie().url(url);
    }

    public static PageBuilder home() {
        return page(PageType.HOME);
    }


    public static PageBuilder home(String url) {
        return home().url(url);
    }


    @SuppressWarnings("unchecked")
    private final PB self = (PB) this;

    PageBuilder(P page) {
        this.page = page;
    }

    public PB url(String deepLink) {
        page.setUrl(deepLink);
        return self;
    }

    public PB type(PageType pt) {
        page.setType(pt);
        return self;
    }

    public PB crids(String... crids) {
        List<Crid> list = new ArrayList<>(crids.length);
        for(String crid : crids) {
            list.add(new Crid(crid));
        }
        page.setCrids(list);
        return self;
    }

    public PB alternativeUrls(String... urls) {
        return alternativeUrls(Arrays.asList(urls));
    }

    public PB alternativeUrls(List<String> urls) {
            page.setAlternativeUrls(urls);
            return self;
        }

    public PB title(String title) {
        page.setTitle(title);
        return self;
    }

    public PB subtitle(String subtitle) {
        page.setSubtitle(subtitle);
        return self;
    }

    public PB paragraphs(Paragraph... paragraphs) {
        return paragraphs(Arrays.asList(paragraphs));
    }

    public PB paragraphs(List<Paragraph> paragraphs) {
        page.setParagraphs(paragraphs);
        return self;
    }

    public PB portal(Portal portal) {
        page.setPortal(portal);
        return self;
    }

    public PB portal(String portal) {
        return portal(Portal.builder().id(portal).build());
    }

    public PB mainImage(Image mainImage) {
        page.setImages(Collections.singletonList(mainImage));
        return self;
    }

    public PB image(String mainImage) {
        Image image = new Image();
        image.setUrl(mainImage);
        return mainImage(image);
    }


    public PB relation(Relation relation) {
        page.getRelations().add(relation);
        return self;
    }

    public PB relation(RelationDefinition def, String uri, String text) {
        return relation(new Relation(def, uri, text));
    }

    public PB relationText(RelationDefinition def, String text) {
        return relation(def, null, text);
    }

    public PB relationUri(RelationDefinition def, String uri) {
        return relation(def, uri, null);
    }

    public PB summary(String summary) {
        page.setSummary(summary);
        return self;
    }

    public PB tags(String... tags) {
        return tags(Arrays.asList(tags));
    }

    public PB tags(List<String> tags) {
        page.setTags(tags);
        return self;
    }

    public PB statRefs(String... statRefs) {
        return statRefs(Arrays.asList(statRefs));
    }

    public PB statRefs(List<String> pageRefs) {
        page.setStatRefs(pageRefs);
        return self;
    }

    public PB keywords(String... keywords) {
        return keywords(Arrays.asList(keywords));
    }

    public PB keywords(List<String> keywords) {
        page.setKeywords(keywords);
        return self;
    }

    public PB genres(SortedSet<Genre> genres) {
        page.setGenres(genres);
        return self;
    }

    public PB genres(Genre... g) {
        SortedSet<Genre> genres = page.getGenres();
        if (genres == null) {
            genres = new TreeSet<>();
        }
        genres.addAll(Arrays.asList(g));
        page.setGenres(genres);
        return self;
    }


    public PB genres(Term... t) {
        SortedSet<Genre> genres = page.getGenres();
        if (genres == null) {
            genres = new TreeSet<>();
        }
        for (Term term : t) {
            genres.add(new Genre(term));
        }
        page.setGenres(genres);
        return self;
    }

     public PB genres(String ... t) {
        SortedSet<Genre> genres = page.getGenres();
        if (genres == null) {
            genres = new TreeSet<>();
        }
        for (String term : t) {
            genres.add(new Genre(new Term(term)));
        }
        page.setGenres(genres);
        return self;
    }

    public PB broadcasters(Broadcaster... broadcasters) {
        return broadcasters(Arrays.asList(broadcasters));
    }

    @SuppressWarnings("unchecked")
    public PB broadcasters(List<Broadcaster> broadcasters) {
        page.setBroadcasters(broadcasters);
        return self;
    }

    public PB broadcasters(String... broadcasters) {
        return broadcasters(Arrays.stream(broadcasters).map(b -> new Broadcaster(b, b)).collect(Collectors.toList()));
    }



    @SuppressWarnings("unchecked")
    public PB embeds(Embed... embeds) {
        return embeds(Arrays.asList(embeds));
    }

    public PB embeds(MediaObject... mediaObjects) {
        List<Embed> embed = new ArrayList<>();
        for(MediaObject m : mediaObjects) {
            embed.add(new Embed(m));
        }
        return embeds(embed);
    }

    public PB embeds(List<Embed> Embeds) {
        page.setEmbeds(Embeds);
        return self;
    }

    public PB referrals(List<Referral> referrals) {
        page.setReferrals(referrals);
        return self;
    }

    public PB referrals(Referral... referrals) {
        return referrals(Arrays.asList(referrals));
    }


    public PB links(List<Link> links) {
        page.setLinks(links);
        return self;
    }

    public PB links(Link... links) {
        return links(Arrays.asList(links));
    }

    public PB publishStart(Date date) {
        page.setPublishStartInstant(fromDate(date));
        return self;
    }

    public PB lastModified(Date date) {
        page.setLastModified(fromDate(date));
        return self;
    }

    public PB creationDate(Date date) {
        page.setCreationDate(fromDate(date));
        return self;
    }

    Instant fromDate(Date date) {
        return date == null ? null : date.toInstant();
    }


    public PB publishStart(Instant date) {
        page.setPublishStartInstant(date);
        return self;
    }

    public PB publishStart(LocalDateTime date) {
        page.setPublishStartInstant(date.atZone(Schedule.ZONE_ID).toInstant());
        return self;
    }

    public PB lastModified(Instant date) {
        page.setLastModified(date);
        return self;
    }

    public PB lastModified(LocalDateTime date) {
        page.setLastModified(date.atZone(Schedule.ZONE_ID).toInstant());
        return self;
    }

    public PB creationDate(Instant date) {
        page.setCreationDate(date);
        return self;
    }

    public PB creationDate(LocalDateTime date) {
        page.setCreationDate(date.atZone(Schedule.ZONE_ID).toInstant());
        return self;
    }

    public PB lastPublished(Instant date) {
        page.setLastPublished(date);
        return self;
    }

    public static PageBuilder from(
        PageUpdate update,
        @NonNull Instant creationDate,
        @NonNull Instant lastModified,
        @Nullable List<Referral> referrals,
        @Nullable Integer referrerCount,
        @Nullable List<Embed> embeds,
        ClassificationService classificationService,
        BroadcasterService broadcasterService,
        RelationDefinitionService relationDefinitionService
    ) {
        final Page page = new Page(update.getType());
        page.setType(update.getType());
        page.setUrl(update.getUrl());
        page.setPublishStartInstant(update.getPublishStart());
        page.setLastPublished(update.getLastPublished());
        page.setCreationDate(update.getCreationDate());
        if (page.getCreationDate() == null) {
            page.setCreationDate(creationDate);
        }
        if (update.getLastModified() != null) {
            page.setLastModified(update.getLastModified());
        } else {
            page.setLastModified(lastModified);
        }

        page.setCrids(update.getCrids());
        page.setAlternativeUrls(update.getAlternativeUrls());
        page.setStatRefs(update.getStatRefs());

        {
            List<Broadcaster> broadcasters = new ArrayList<>(update.getBroadcasters().size());
            for(String broadcaster : update.getBroadcasters()) {
                broadcasters.add(broadcasterService.find(broadcaster));
            }
            page.setBroadcasters(broadcasters);
        }

        if (update.getPortal() != null) {
            Portal portal = update.getPortal().toPortal();
            page.setPortal(portal);
        }
        page.setTitle(update.getTitle());
        page.setSubtitle(update.getSubtitle());
        page.setKeywords(update.getKeywords());
        page.setSummary(update.getSummary());

        if (isNotEmpty(update.getParagraphs())) {
            List<Paragraph> paragraphs = new ArrayList<>();
            for (ParagraphUpdate paragraphUpdate : update.getParagraphs()) {
                paragraphs.add(paragraphUpdate.toParagraph());
            }
            page.setParagraphs(paragraphs);
        }

        page.setTags(update.getTags());
        page.setRefCount(referrerCount);
        page.setReferrals(referrals);
        if (isNotEmpty(update.getImages())) {
            List<Image> images = new ArrayList<>();
            for(ImageUpdate imageUpdate : update.getImages()) {
                images.add(imageUpdate.toImage());
            }
            page.setImages(images);
        }
        if (isNotEmpty(update.getLinks())) {
            List<Link> links = new ArrayList<>();
            for (LinkUpdate linkUpdate : update.getLinks()) {
                links.add(linkUpdate.toLink());
            }
            page.setLinks(links);
        }
        page.setEmbeds(embeds);


        if (update.getGenres() != null) {
            SortedSet<Genre> genres = new TreeSet<>();
            for (String termId : update.getGenres()) {
                try {
                    Term term = classificationService.getTerm(termId);
                    genres.add(new Genre(term));
                } catch(TermNotFoundException e) {
                    log.warn("Can not add genre to {}, root cause: {}", update.getUrl(), e.getMessage());
                }

                if(!genres.isEmpty()) {
                    page.setGenres(genres);
                }
            }
        }
        if (update.getRelations() != null){
            SortedSet<Relation> relations = new TreeSet<>();
            for (RelationUpdate relationUpdate : update.getRelations()) {

                relations.add(relationUpdate.toRelation(relationDefinitionService));
            }
            if (! relations.isEmpty()) {
                page.setRelations(relations);
            }
        }


        return new DefaultPageBuilder(page);
    }

    @SuppressWarnings("unchecked")
    public PB example() {
        return
            title("Groot brein in klein dier")
                .subtitle("De naakte molrat heeft ‘m")
                .paragraphs(new Paragraph("Molrat", "Een klein, harig beestje met het gewicht van een paperclip was mogelijk de directe voorouder van alle hedendaagse zoogdieren, waaronder de mens. Levend in de schaduw van de dinosaurussen kroop het diertje 195 miljoen jaar geleden tussen de planten door, op zoek naar insecten die het met zijn vlijmscherpe tandjes vermaalde. Het is de oudste zoogdierachtige die tot nu toe is gevonden.", null))
                .url("http://www.wetenschap24.nl/nieuws/artikelen/2001/mei/Groot-brein-in-klein-dier.html")
                .portal(new Portal("WETENSCHAP24", "http://www.wetenschap24.nl", "Wetenschap 24"))
                .image("http://www.wetenschap24.nl/.imaging/stk/wetenschap/vtk-imagegallery-normal/media/wetenschap/noorderlicht/artikelen/2001/May/3663525/original/3663525.jpeg")
                .tags("molrat", "brein", "naakt", "jura")
                .keywords("wetenschap", "hoezo", "biologie")
                .broadcasters(new Broadcaster("VPRO"), new Broadcaster("KRO"))
                .genres(new Genre(new Term("3.0.1")))
                .publishStart(new Date(1370424584330L))
            ;
    }

    public P build() {
        return page;
    }

    public static class DefaultPageBuilder extends PageBuilder<DefaultPageBuilder, Page> {
        private DefaultPageBuilder(Page page) {
            super(page);
        }
    }

    private static boolean isNotEmpty(Collection<?> collection) {
        return collection != null && ! collection.isEmpty();
    }
}
