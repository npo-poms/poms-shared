package nl.vpro.domain.image;

import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.domain.image.backend.BackendImage;
import nl.vpro.jackson2.Jackson2Mapper;

import static nl.vpro.test.util.jackson2.Jackson2TestUtil.assertThatJson;


@Slf4j
class ImageSourceServiceTest {

    static Map<ImageSource.Type, Dimension> DIMENSIONS = new HashMap<>();
    static {
        DIMENSIONS.put(ImageSource.Type.THUMBNAIL, Dimension.of(100, 100));
        DIMENSIONS.put(ImageSource.Type.MOBILE, Dimension.of(200, 200));
        DIMENSIONS.put(ImageSource.Type.LARGE, Dimension.of(300, 300));


        ImageUrlServiceHolder.setInstance(new AbsoluteImageUrlServiceImpl("https://images.poms.omroep.nl/"));
    }


    public static class SomeImageThatCantImplementMetadata implements MetadataSupplier {

        long getId() {
            return 1;
        }

        @Override
        public @NonNull Metadata getMetadata() {
            return ImageMetadataImpl.builder()
                .areaOfInterest(new Area(0, 0, 10, 10))
                .dimension(Dimension.of(100, 200))
                .title("Test")
                .build();
        }


    }

    public static class SomeCreator implements  ImageSourceCreator<SomeImageThatCantImplementMetadata> {

        @Override
        public Optional<ImageSource> createFor(SomeImageThatCantImplementMetadata imageThatCantImplementMetadata, Metadata imageMetadata, ImageSource.Key type) {
            return
                Optional.ofNullable(DIMENSIONS.get(type.getType())).map(dim ->

                    ImageSource.of(type)
                        .url("https://bla/" + type.getShortName() + "/" + imageThatCantImplementMetadata.getId() + "." + type.getFormat().getFileExtension())
                        .dimension(dim)
                        .build());
        }
    }


    /**
     * How it could work with some object that is an image, but only implements {@link MetadataSupplier}
     */
    @Test
    void forSomeSupplier() {
        MetadataSupplier supplier = new SomeImageThatCantImplementMetadata();

        ImageSourceSet imageMetadata = ImageSourceService.INSTANCE.getSourceSet(supplier);
        assertThatJson(imageMetadata)
            .withoutUnmarshalling()
            .isSimilarTo("{\n" +
            "  \"THUMBNAIL.WEBP\" : {\n" +
            "    \"url\" : \"https://bla/TN.W/1.webp\",\n" +
            "    \"type\" : \"THUMBNAIL\",\n" +
            "    \"format\" : \"WEBP\",\n" +
            "    \"dimension\" : {\n" +
            "      \"width\" : 100,\n" +
            "      \"height\" : 100\n" +
            "    }\n" +
            "  },\n" +
            "  \"MOBILE.WEBP\" : {\n" +
            "    \"url\" : \"https://bla/M1.W/1.webp\",\n" +
            "    \"type\" : \"MOBILE\",\n" +
            "    \"format\" : \"WEBP\",\n" +
            "    \"dimension\" : {\n" +
            "      \"width\" : 200,\n" +
            "      \"height\" : 200\n" +
            "    }\n" +
            "  },\n" +
            "  \"LARGE.WEBP\" : {\n" +
            "    \"url\" : \"https://bla/L1.W/1.webp\",\n" +
            "    \"type\" : \"LARGE\",\n" +
            "    \"format\" : \"WEBP\",\n" +
            "    \"dimension\" : {\n" +
            "      \"width\" : 300,\n" +
            "      \"height\" : 300\n" +
            "    }\n" +
            "  },\n" +
            "  \"MOBILE.JPG\" : {\n" +
            "    \"url\" : \"https://bla/M1.J/1.jpg\",\n" +
            "    \"type\" : \"MOBILE\",\n" +
            "    \"format\" : \"JPG\",\n" +
            "    \"dimension\" : {\n" +
            "      \"width\" : 200,\n" +
            "      \"height\" : 200\n" +
            "    }\n" +
            "  }\n" +
            "}");
    }


    /**
     *  A {@link BackendImage} properly implements {@link Metadata} itself.
     */

    @Test
    void forBackendImage() throws JsonProcessingException {
        BackendImage  image = BackendImage.builder()
            .imageFormat(ImageFormat.JPG)
            .title("bla")
            .height(100)
            .width(200)
            .build();
        image.setCreationInstant(Instant.ofEpochMilli(1675851618171L));
        image.setId(123L);

        log.info("{}", Jackson2Mapper.getInstance().writeValueAsString(image));
        assertThatJson(Jackson2Mapper.getModelInstance(), ImageMetadata.of(image))
            .withoutUnmarshalling()
            .isSimilarTo("{\n" +
                "  \"title\" : \"bla\",\n" +
                "  \"height\" : 100,\n" +
                "  \"width\" : 200,\n" +
                "  \"sourceSet\" : {\n" +
                "    \"THUMBNAIL.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/s100/123.webp\",\n" +
                "      \"type\" : \"THUMBNAIL\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 100,\n" +
                "        \"height\" : 50\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE_HALF.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/s160/123.webp\",\n" +
                "      \"type\" : \"MOBILE_HALF\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 160,\n" +
                "        \"height\" : 80\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/s320/123.webp\",\n" +
                "      \"type\" : \"MOBILE\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 320,\n" +
                "        \"height\" : 160\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE_2.WEBP\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/s640%3E/123.webp\",\n" +
                "      \"type\" : \"MOBILE_2\",\n" +
                "      \"format\" : \"WEBP\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 200,\n" +
                "        \"height\" : 100\n" +
                "      }\n" +
                "    },\n" +
                "    \"MOBILE.JPG\" : {\n" +
                "      \"url\" : \"https://images.poms.omroep.nl/s640%3E/123.jpg\",\n" +
                "      \"type\" : \"MOBILE\",\n" +
                "      \"format\" : \"JPG\",\n" +
                "      \"dimension\" : {\n" +
                "        \"width\" : 200,\n" +
                "        \"height\" : 100\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"creationDate\" : 1675851618171,\n" +
                "  \"pointOfInterest\" : {\n" +
                "    \"x\" : 50.0,\n" +
                "    \"y\" : 50.0\n" +
                "  },\n" +
                "  \"picture\" : {\n" +
                "    \"sources\" : {\n" +
                "      \"image/webp\" : \"https://images.poms.omroep.nl/s100/123.webp 100w, https://images.poms.omroep.nl/s160/123.webp 160w, https://images.poms.omroep.nl/s320/123.webp 320w, https://images.poms.omroep.nl/s640%3E/123.webp 200w\",\n" +
                "      \"image/jpeg\" : \"https://images.poms.omroep.nl/s640%3E/123.jpg 200w\"\n" +
                "    },\n" +
                "    \"imageSrc\" : \"https://images.poms.omroep.nl/s640%3E/123.jpg\",\n" +
                "    \"alternative\" : \"bla\",\n" +
                "    \"imageTitle\" : \"bla\",\n" +
                "    \"width\" : 200,\n" +
                "    \"height\" : 100,\n" +
                "    \"pointOfInterest\" : \"50% 50%\"\n" +
                "  },\n" +
                "  \"alternativeOrTitle\" : \"bla\"\n" +
                "}");
    }

}
