package nl.vpro.domain.page.update;

import java.lang.reflect.Array;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.checkerframework.checker.nullness.qual.NonNull;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.page.*;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageUpdateBuilder {

    protected final PageUpdate page;

    public static PageUpdateBuilder page() {
        return new PageUpdateBuilder(new PageUpdate());
    }

    public static PageUpdateBuilder page(@NonNull PageType type, @NonNull String url) {
        return new PageUpdateBuilder(new PageUpdate(type, url));
    }

    public static PageUpdateBuilder article(String url) {
        return page(PageType.ARTICLE, url);
    }

    public static PageUpdateBuilder page(Page page) {
        return PageUpdate.builder(page.getType(), page.getUrl())
            .creationDate(page.getCreationDate())
            .lastModified(page.getLastModified())
            .lastPublished(page.getLastPublished())
            .publishStart(page.getPublishStartInstant())
            .alternativeUrls(toArray(page.getAlternativeUrls()))
            .broadcasters(toArray(page.getBroadcasters(), String.class, Broadcaster::getId))
            .crids(toArray(page.getCrids()))
            .embeds(toArray(page.getEmbeds(), EmbedUpdate.class, EmbedUpdate::of))
            .genres(toArray(page.getGenres(), String.class, Genre::getTermId))
            .images(toArray(page.getImages(), ImageUpdate.class, ImageUpdate::of))
            .keywords(toArray(page.getKeywords()))
            .links(toArray(page.getLinks(), LinkUpdate.class, LinkUpdate::of))
            .paragraphs(toArray(page.getParagraphs(), ParagraphUpdate.class, ParagraphUpdate::of))
            .portal(PortalUpdate.of(page.getPortal()))
            .relations(toArray(page.getRelations(), RelationUpdate.class, RelationUpdate::of))
            .statRefs(toArray(page.getStatRefs()))
            .subtitle(page.getSubtitle())
            .summary(page.getSummary())
            .title(page.getTitle())
            .tags(page.getTags())
            ;
    }

    @SuppressWarnings("unchecked")
    private static <T, UT> UT[] toArray(Collection<T> collection, Class<UT> clazz, Function<T, UT> mapper) {
        if (collection == null || collection.isEmpty()) {
            return (UT[]) Array.newInstance(clazz, 0);
        } else {
            return collection.stream().map(mapper).toArray(i -> (UT[]) Array.newInstance(clazz, i));
        }
    }

     private static String[] toArray(Collection<String> collection) {
        if (collection == null || collection.isEmpty()) {
            return new String[0];
        } else {
            return collection.toArray(new String[0]);
        }
    }

    private PageUpdateBuilder(PageUpdate page) {
        this.page = page;
    }


    public PageUpdateBuilder broadcasters(String... broadcasters) {
        return broadcasters(Arrays.asList(broadcasters));
    }

    public PageUpdateBuilder portal(PortalUpdate portal) {
        page.setPortal(portal);
        return this;
    }

    public PageUpdateBuilder title(String title) {
        page.setTitle(title);
        return this;
    }

    public PageUpdateBuilder subtitle(String subtitle) {
        page.setSubtitle(subtitle);
        return this;
    }

    public PageUpdateBuilder keywords(String... keywords) {
        return keywords(Arrays.asList(keywords));
    }

    public PageUpdateBuilder keywords(List<String> keywords) {
        page.setKeywords(keywords);
        return this;
    }

    public PageUpdateBuilder genres(String... genres) {
        return genres(Arrays.asList(genres));
    }
    public PageUpdateBuilder genres(List<String> genres) {
        page.setGenres(genres);
        return this;
    }

    public PageUpdateBuilder genres(Term... genres) {
        return genreTerms(Arrays.asList(genres));
    }

    public PageUpdateBuilder genreTerms(List<Term> genres) {
        page.setGenres(genres.stream().map(Term::getTermId).collect(Collectors.toList()));
        return this;
    }

    public PageUpdateBuilder paragraphs(ParagraphUpdate... paragraphs) {
        return paragraphs(Arrays.asList(paragraphs));
    }

    public PageUpdateBuilder paragraphs(List<ParagraphUpdate> paragraphs) {
        page.setParagraphs(paragraphs);
        return this;
    }

    public PageUpdateBuilder summary(String summary) {
        page.setSummary(summary);
        return this;
    }

    public PageUpdateBuilder tags(String... tags) {
        return tags(Arrays.asList(tags));
    }


    public PageUpdateBuilder tags(List<String> tags) {
        page.setTags(tags);
        return this;
    }

    public PageUpdateBuilder broadcasters(List<String> broadcasters) {
        page.setBroadcasters(broadcasters);
        return this;
    }

    public PageUpdateBuilder links(LinkUpdate... links) {
        return links(Arrays.asList(links));
    }

    public PageUpdateBuilder links(List<LinkUpdate> links) {
        page.setLinks(links);
        return this;
    }

    public PageUpdateBuilder embeds(EmbedUpdate... embeds) {
        page.setEmbeds(Arrays.asList(embeds));
        return this;
    }

    public PageUpdateBuilder images(ImageUpdate... images) {
        page.setImages(Arrays.asList(images));
        return this;
    }

    public PageUpdateBuilder relations(RelationUpdate... relations) {
        page.setRelations(Arrays.asList(relations));
        return this;
    }


    public PageUpdateBuilder crids(String... crids) {
        page.setCrids(Arrays.asList(crids));
        return this;
    }

    public PageUpdateBuilder alternativeUrls(String... urls) {
        page.setAlternativeUrls(Arrays.asList(urls));
        return this;
    }

    public PageUpdateBuilder statRefs(String... statRefs) {
        page.setStatRefs(Arrays.asList(statRefs));
        return this;
    }

    public PageUpdateBuilder publishStart(Date date) {
        page.setPublishStart(fromDate(date));
        return this;
    }

    public PageUpdateBuilder publishStart(Instant date) {
        page.setPublishStart(date);
        return this;
    }

    public PageUpdateBuilder publishStart(LocalDateTime date) {
        return publishStart(date.atZone(Schedule.ZONE_ID).toInstant());

    }

    public PageUpdateBuilder lastPublished(Instant date) {
        page.setLastPublished(date);
        return this;
    }

    public PageUpdateBuilder lastPublished(LocalDateTime date) {
        return lastPublished(date.atZone(Schedule.ZONE_ID).toInstant());
    }

    public PageUpdateBuilder creationDate(Instant date) {
        page.setCreationDate(date);
        return this;
    }

    public PageUpdateBuilder creationDate(LocalDateTime date) {
        return creationDate(date.atZone(Schedule.ZONE_ID).toInstant());
    }


    public PageUpdateBuilder lastModified(Instant date) {
        page.setLastModified(date);
        return this;
    }


    public PageUpdateBuilder lastModified(LocalDateTime date) {
        return lastModified(date.atZone(Schedule.ZONE_ID).toInstant());
    }

    public PageUpdateBuilder withNow() {
        Instant now = Instant.now();
        return creationDate(now)
            .lastModified(now);
    }

    public PageUpdateBuilder workflow(PageWorkflow workflow) {
        page.setWorkflow(workflow);
        return this;
    }

    public PageUpdateBuilder deleted() {
        return workflow(PageWorkflow.DELETED);
    }

    public PageUpdateBuilder deleted(boolean deleted) {
        return workflow(deleted ? PageWorkflow.DELETED : PageWorkflow.PUBLISHED);
    }

    Instant fromDate(Date date) {
        return date == null ? null : date.toInstant();
    }

    public PageUpdateBuilder example() {
        return
            title("Groot brein in klein dier")
                .subtitle("De naakte molrat heeft ‘m")
                .summary("Een klein, harig beestje met het gewicht van een paperclip was mogelijk de directe voorouder van alle hedendaagse zoogdieren, waaronder de mens. Levend in de schaduw van de dinosaurussen kroop het diertje 195 miljoen jaar geleden tussen de planten door, op zoek naar insecten die het met zijn vlijmscherpe tandjes vermaalde. Het is de oudste zoogdierachtige die tot nu toe is gevonden.")
                .paragraphs(new ParagraphUpdate("Voorouders", "Onderzoekers vonden de directe voorouder van ...", null))
                .portal(
                    PortalUpdate.builder()
                        .id("WETENSCHAP24")
                        .url("http://npowetenschap.nl")
                        .section(
                            Section.builder().displayName("quantummechanica").path("/quantum").build()
                        ).build())
                .images(new ImageUpdate(ImageType.ICON, null, null, new ImageLocation("http://www.wetenschap24.nl/.imaging/stk/wetenschap/vtk-imagegallery-normal/media/wetenschap/noorderlicht/artikelen/2001/May/3663525/original/3663525.jpeg")))
                .tags("molrat", "brein", "naakt", "jura")
                .keywords("wetenschap", "hoezo", "biologie")
                .broadcasters("VPRO", "KRO")
                .links(LinkUpdate.topStory("http://www.vpro.nl/heelgoed.html", "kijk hier!"))
                .embeds(new EmbedUpdate("POMS_VPRO_203778", "Noorderlicht"))
                .genres("3.0.1.1")
                .crids("crid://example/1")
                .creationDate(LocalDateTime.of(2017, 2, 1, 7, 52))
                .lastModified(LocalDateTime.of(2017, 2, 1, 8, 52))
                .lastPublished(LocalDateTime.of(2017, 2, 1, 9, 52))
                .publishStart(LocalDateTime.of(2017, 2, 1, 9, 52))
                .statRefs("http://comscore/1")
                .alternativeUrls(build().getUrl() + "?alternative")
            ;
    }

    public PageUpdate build() {
        if (page.getWorkflow() == null) {
            page.setWorkflow(PageWorkflow.PUBLISHED);
        }
        return page;
    }

}
