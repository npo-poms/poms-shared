package nl.vpro.api.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Named;

import nl.vpro.domain.Mappings;
import nl.vpro.domain.Xmlns;
import nl.vpro.domain.api.MediaChange;
import nl.vpro.domain.api.SuggestResult;
import nl.vpro.domain.api.media.*;
import nl.vpro.domain.api.page.PageForm;
import nl.vpro.domain.api.page.PageSearchResult;
import nl.vpro.domain.api.page.PageSearchResults;
import nl.vpro.domain.api.profile.Profile;
import nl.vpro.domain.api.subtitles.SubtitlesForm;
import nl.vpro.domain.constraint.LocalizedString;
import nl.vpro.domain.gtaa.Scheme;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.page.Page;
import nl.vpro.domain.page.update.*;
import nl.vpro.domain.subtitles.Subtitles;
import nl.vpro.domain.subtitles.SubtitlesType;

import static nl.vpro.domain.Xmlns.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.3
 */
@Slf4j
public class ApiMappings extends Mappings {

    @Getter
    final URI pomsLocation;

    @Inject
    public ApiMappings(@Named("${npo-media.baseUrl}") String pomsLocation) {
        this.pomsLocation = pomsLocation == null ? URI.create("https://poms.omroep.nl") : URI.create(pomsLocation);
        generateDocumentation = true;
        log.info("Using poms location {}", this.pomsLocation);
    }


    @Override
    protected void fillMappings() {
        MAPPING.put(PROFILE_NAMESPACE, new Class[]{Profile.class});
        MAPPING.put(API_NAMESPACE, new Class[]{
            PageForm.class,
            ScheduleForm.class,
            SubtitlesForm.class,
            RedirectList.class,
            MediaSearchResults.class,
            PageSearchResults.class,
            MediaSearchResult.class,
            PageSearchResult.class,
            MediaChange.class,
            SuggestResult.class,
            nl.vpro.domain.api.Error.class,
            MediaResult.class
        });
        MAPPING.put(PAGE_NAMESPACE, new Class[]{Page.class});
        MAPPING.put(PAGEUPDATE_NAMESPACE, new Class[]{PageUpdate.class, ImageType.class, SaveResultList.class, DeleteResult.class});
        MAPPING.put(Xmlns.MEDIA_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.media.Filter.class});
        MAPPING.put(Xmlns.PAGE_CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.page.Filter.class});
        MAPPING.put(Xmlns.CONSTRAINT_NAMESPACE, new Class[]{nl.vpro.domain.constraint.Operator.class,  LocalizedString.class,
});
        MAPPING.put(Xmlns.MEDIA_SUBTITLES_NAMESPACE, new Class[]{Subtitles.class, SubtitlesType.class});
        MAPPING.put(GTAA_NAMESPACE, Scheme.classes());

        Xmlns.fillLocationsAtPoms(KNOWN_LOCATIONS, pomsLocation.toString());
    }


}
