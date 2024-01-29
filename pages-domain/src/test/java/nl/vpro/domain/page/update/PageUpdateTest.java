package nl.vpro.domain.page.update;

import java.io.IOException;
import java.io.StringReader;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.media.MediaClassificationService;
import nl.vpro.domain.page.*;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.serialize.SerializeTestUtil;

import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


public class PageUpdateTest {

    private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

    static {
        ClassificationServiceLocator.setInstance(MediaClassificationService.getInstance());
    }
    @Mock
    BroadcasterService broadcasterService;

    @Mock
    PortalService portalService;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(broadcasterService.find("VPRO")).thenReturn(new Broadcaster("VPRO"));
        when(portalService.find("WETENSCHAP24")).thenReturn(new Portal("WETENSCHAP24","NPOWETENSCHAP"));

        ServiceLocator.setPortalService(portalService);
        ServiceLocator.setBroadcasterService(broadcasterService);
    }



    @Test
    public void validate() {
        PageUpdate pageUpdate = new PageUpdate(PageType.ARTICLE, "http://www.test.vpro.nl/123");
        pageUpdate.setBroadcasters(singletonList("VPRO"));
        pageUpdate.setTitle("main title");

        assertThat(VALIDATOR.validate(pageUpdate)).isEmpty();
        assertThat(pageUpdate.getType()).isNotNull();
    }


    @Test
    public void validateMSE_2589() {
        PageUpdate pageUpdate = JAXB.unmarshal(requireNonNull(getClass().getResourceAsStream("/MSE-2589.xml")), PageUpdate.class);

        assertThat(VALIDATOR.validate(pageUpdate)).isEmpty();

    }

    @Test
    public void validateGenres() {
        PageUpdate pageUpdate = new PageUpdate(PageType.ARTICLE, "http://www.test.vpro.nl/123");
        pageUpdate.setTitle("foo");
        pageUpdate.setBroadcasters(singletonList("VPRO"));

        pageUpdate.setGenres(singletonList("3.0.1.2"));

        assertThat(VALIDATOR.validate(pageUpdate)).isEmpty();


        pageUpdate.setGenres(singletonList("3.0.1"));
        assertThat(VALIDATOR.validate(pageUpdate)).hasSize(1);
    }


    @Test
    public void unmarshal() {
        String xml = """
            <page type="ARTICLE" url="http://www.vpro.nl/article/123"  xmlns="urn:vpro:pages:update:2013">
              <crid>crid://bla/123</crid>
              <broadcaster>VPRO</broadcaster>
              <title>Hoi2</title>
            </page>""";
        PageUpdate update = JAXB.unmarshal(new StringReader(xml), PageUpdate.class);
        assertThat(update.getTitle()).isEqualTo("Hoi2");
    }

    private static final RelationDefinition DEF = new RelationDefinition("FOO", "VPRO");

    @Test
    public void json() {
        PageUpdate page = PageUpdateBuilder
            .article("http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html")
            .portal(PortalUpdate
                .builder()
                .id("3voor12")
                .url("http://3voor12-beta-test.vpro.nl/")
                .section(Section.builder().displayName("display").path("/bla").build()).build())
            .relations(RelationUpdate.text(DEF, "bla"))
            .images(ImageUpdate
                .builder()
                .source("source")
                .sourceName("vpro")
                .description("description")
                .license(License.CC_BY)
                .title("title")
                .type(ImageType.ICON)
                .imageUrl("https://www.vpro.nl/plaatje")
                .credits("page image credits")
                .build())
            .paragraphs(
                ParagraphUpdate.builder()
                    .title("title")
                    .body("body")
                    .image(ImageUpdate
                        .builder()
                        .credits("paragraph image credits")
                        .build()
                    )
                .build()
            )
            .build();
        assertThat(page.getWorkflow()).isEqualTo(PageWorkflow.PUBLISHED);
        PageUpdate rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(Jackson2Mapper.getPublisherInstance(), page, """
            {
              "type" : "ARTICLE",
              "url" : "http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html",
              "portal" : {
                "section" : {
                  "path" : "/bla",
                  "value" : "display"
                },
                "id" : "3voor12",
                "url" : "http://3voor12-beta-test.vpro.nl/"
              },
              "paragraphs" : [ {
                "title" : "title",
                "body" : "body",
                "image" : {
                  "credits" : "paragraph image credits"
                }
              } ],
              "images" : [ {
                "title" : "title",
                "description" : "description",
                "source" : "source",
                "sourceName" : "vpro",
                "license" : "CC_BY",
                "credits" : "page image credits",
                "image" : {
                  "imageLocation" : {
                    "url" : "https://www.vpro.nl/plaatje"
                  }
                },
                "type" : "ICON"
              } ],
              "relations" : [ {
                "type" : "FOO",
                "broadcaster" : "VPRO",
                "value" : "bla"
              } ],  "expandedWorkflow":"PUBLISHED"
            }""");

        assertThat(rounded.getPortal().getSection().getDisplayName()).isEqualTo("display");


    }

    @Test
    public void serialize() throws IOException {
         PageUpdate page = PageUpdateBuilder
            .article("http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html")
            .portal(PortalUpdate
                .builder()
                .id("3voor12")
                .url("http://3voor12-beta-test.vpro.nl/")
                .section(Section.builder().displayName("display").path("/bla").build()).build())
             .links(LinkUpdate.of("http://www.vpro.nl"))
             .images(ImageUpdate.builder().build())
            .build();
        SerializeTestUtil.roundTripAndEquals(page);
    }
}
