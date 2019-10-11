package nl.vpro.domain.page.update;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import nl.vpro.domain.classification.Term;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.Schedule;
import nl.vpro.domain.page.Crid;
import nl.vpro.domain.page.PageType;
import nl.vpro.domain.page.Section;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class PageUpdateBuilder<PB extends PageUpdateBuilder<PB, P>, P extends PageUpdate> {

    protected final P page;

    @SuppressWarnings("unchecked")
    protected final PB self = (PB) this;

    public static DefaultPageBuilder page(PageType type, String url) {
        return new DefaultPageBuilder(new PageUpdate(type, url));
    }

    public static DefaultPageBuilder article(String url) {
        return page(PageType.ARTICLE, url);
    }

    private PageUpdateBuilder(P page) {
        this.page = page;
    }


    public PB broadcasters(String... broadcasters) {
        return broadcasters(Arrays.asList(broadcasters));
    }

    public PB portal(PortalUpdate portal) {
        page.setPortal(portal);
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

    public PB keywords(String... keywords) {
        return keywords(Arrays.asList(keywords));
    }

    public PB keywords(List<String> keywords) {
        page.setKeywords(keywords);
        return self;
    }

    public PB genres(String... genres) {
        return genres(Arrays.asList(genres));
    }
    public PB genres(List<String> genres) {
        page.setGenres(genres);
        return self;
    }

    public PB genres(Term... genres) {
        return genreTerms(Arrays.asList(genres));
    }

    public PB genreTerms(List<Term> genres) {
        page.setGenres(Lists.transform(genres, new Function<Term, String>() {
            @Nullable
            @Override
            public String apply(@Nullable Term input) {
                return input == null ? null : input.getTermId();

            }
        }));
        return self;
    }

    public PB paragraphs(ParagraphUpdate... paragraphs) {
        return paragraphs(Arrays.asList(paragraphs));
    }

    public PB paragraphs(List<ParagraphUpdate> paragraphs) {
        page.setParagraphs(paragraphs);
        return self;
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

    public PB broadcasters(List<String> broadcasters) {
        page.setBroadcasters(broadcasters);
        return self;
    }

    public PB links(LinkUpdate... links) {
        return links(Arrays.asList(links));
    }

    public PB links(List<LinkUpdate> links) {
        page.setLinks(links);
        return self;
    }

    public PB embeds(EmbedUpdate... embeds) {
        page.setEmbeds(Arrays.asList(embeds));
        return self;
    }

    public PB images(ImageUpdate... images) {
        page.setImages(Arrays.asList(images));
        return self;
    }

    public PB relations(RelationUpdate... relations) {
        page.setRelations(Arrays.asList(relations));
        return self;
    }

    public PB crids(Crid... crids) {
        page.setCrids(Arrays.asList(crids));
        return self;
    }
    public PB crids(String... crids) {
        page.setCrids(Lists.transform(Arrays.asList(crids), new Function<String, Crid>() {
            @Nullable
            @Override
            public Crid apply(@Nullable String input) {
                return input == null ? null : new Crid(input);

            }
        }));
        return self;
    }

    public PB alternativeUrls(String... urls) {
        page.setAlternativeUrls(Arrays.asList(urls));
        return self;
    }

    public PB statRefs(String... statRefs) {
        page.setStatRefs(Arrays.asList(statRefs));
        return self;
    }

    public PB publishStart(Date date) {
        page.setPublishStart(fromDate(date));
        return self;
    }

    public PB publishStart(Instant date) {
        page.setPublishStart(date);
        return self;
    }

    public PB publishStart(LocalDateTime date) {
        return publishStart(date.atZone(Schedule.ZONE_ID).toInstant());

    }

    public PB lastPublished(Instant date) {
        page.setLastPublished(date);
        return self;
    }

    public PB lastPublished(LocalDateTime date) {
        return lastPublished(date.atZone(Schedule.ZONE_ID).toInstant());
    }

    public PB creationDate(Instant date) {
        page.setCreationDate(date);
        return self;
    }

    public PB creationDate(LocalDateTime date) {
        return creationDate(date.atZone(Schedule.ZONE_ID).toInstant());
    }


    public PB lastModified(Instant date) {
        page.setLastModified(date);
        return self;
    }


    public PB lastModified(LocalDateTime date) {
        return lastModified(date.atZone(Schedule.ZONE_ID).toInstant());
    }

    public PB withNow() {
        Instant now = Instant.now();
        return creationDate(now)
            .lastModified(now);
    }

    Instant fromDate(Date date) {
        return date == null ? null : date.toInstant();
    }

    public PB example() {
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

    public P build() {
        return page;
    }

    public static class DefaultPageBuilder extends PageUpdateBuilder<DefaultPageBuilder, PageUpdate> {
        private DefaultPageBuilder(PageUpdate page) {
            super(page);
        }
    }
}
