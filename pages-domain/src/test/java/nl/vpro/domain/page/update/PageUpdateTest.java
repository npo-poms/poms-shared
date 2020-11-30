package nl.vpro.domain.page.update;

import java.io.IOException;
import java.io.StringReader;
import java.util.Collections;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.xml.bind.JAXB;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import nl.vpro.domain.image.ImageType;
import nl.vpro.domain.page.*;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.Portal;
import nl.vpro.domain.user.*;
import nl.vpro.jackson2.Jackson2Mapper;
import nl.vpro.test.util.jackson2.Jackson2TestUtil;
import nl.vpro.test.util.serialize.SerializeTestUtil;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;


public class PageUpdateTest {

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
        pageUpdate.setBroadcasters(Collections.singletonList("VPRO"));
        pageUpdate.setTitle("main title");

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertThat(validator.validate(pageUpdate)).isEmpty();
        assertThat(pageUpdate.getType()).isNotNull();
    }


    @Test
    public void validateMSE_2589() {
        PageUpdate pageUpdate = JAXB.unmarshal(getClass().getResourceAsStream("/MSE-2589.xml"), PageUpdate.class);
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        assertThat(validator.validate(pageUpdate)).isEmpty();

    }

    @Test
    public void unmarshal() {
        String xml = "<page type=\"ARTICLE\" url=\"http://www.vpro.nl/article/123\"  xmlns=\"urn:vpro:pages:update:2013\">\n" +
            "  <crid>crid://bla/123</crid>\n" +
            "  <broadcaster>VPRO</broadcaster>\n" +
            "  <title>Hoi2</title>\n" +
            "</page>";
        PageUpdate update = JAXB.unmarshal(new StringReader(xml), PageUpdate.class);
        assertThat(update.getTitle()).isEqualTo("Hoi2");
    }

    private static RelationDefinition DEF = new RelationDefinition("FOO", "VPRO");

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
        PageUpdate rounded = Jackson2TestUtil.roundTripAndSimilarAndEquals(Jackson2Mapper.getPublisherInstance(), page, "{\n" +
            "  \"type\" : \"ARTICLE\",\n" +
            "  \"url\" : \"http://3voor12-beta-test.vpro.nl/lokaal/amsterdam/archief/Nieuws-test-pagina.html\",\n" +
            "  \"portal\" : {\n" +
            "    \"section\" : {\n" +
            "      \"path\" : \"/bla\",\n" +
            "      \"value\" : \"display\"\n" +
            "    },\n" +
            "    \"id\" : \"3voor12\",\n" +
            "    \"url\" : \"http://3voor12-beta-test.vpro.nl/\"\n" +
            "  },\n" +
            "  \"paragraphs\" : [ {\n" +
            "    \"title\" : \"title\",\n" +
            "    \"body\" : \"body\",\n" +
            "    \"image\" : {\n" +
            "      \"credits\" : \"paragraph image credits\"\n" +
            "    }\n" +
            "  } ],\n" +
            "  \"images\" : [ {\n" +
            "    \"title\" : \"title\",\n" +
            "    \"description\" : \"description\",\n" +
            "    \"source\" : \"source\",\n" +
            "    \"sourceName\" : \"vpro\",\n" +
            "    \"license\" : \"CC_BY\",\n" +
            "    \"credits\" : \"page image credits\",\n" +
            "    \"image\" : {\n" +
            "      \"imageLocation\" : {\n" +
            "        \"url\" : \"https://www.vpro.nl/plaatje\"\n" +
            "      }\n" +
            "    },\n" +
            "    \"type\" : \"ICON\"\n" +
            "  } ],\n" +
            "  \"relations\" : [ {\n" +
            "    \"type\" : \"FOO\",\n" +
            "    \"broadcaster\" : \"VPRO\",\n" +
            "    \"value\" : \"bla\"\n" +
            "  } ]," +
            "  \"expandedWorkflow\":\"PUBLISHED\"\n" +
            "}");

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
