package nl.vpro.domain.media;

import lombok.extern.log4j.Log4j2;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.search.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.media.update.*;
import nl.vpro.domain.media.update.action.MoveAction;
import nl.vpro.domain.media.update.collections.XmlCollection;
import nl.vpro.domain.subtitles.SubtitlesType;
import nl.vpro.domain.subtitles.SubtitlesWorkflow;
import nl.vpro.domain.support.License;
import nl.vpro.domain.user.Broadcaster;
import nl.vpro.test.util.jaxb.AbstractSchemaTest;


/**
 * Tests whether the POMS schemas are changed. If tests-cases fail here, fix them, but <em>also don't forget to make the changes to the XSDs, also to the manually maintained ones</em>.
 * in nl/vpro/domain/media
 * <p>
 * E.g. in nl/vpro/domain/media/vproMedia.xsd
 * <p>
 * So normally you'd have to change <em>two</em> XSDs.
 *
 * @author Michiel Meeuwissen
 * @since 3.4
 */
@Log4j2
class SchemaTest extends AbstractSchemaTest {

    @Override
    protected  Class<?>[] getClasses() {
        return new Class<?>[] {
            // media
            Program.class,
            Segment.class,
            Schedule.class,
            Group.class,
            MediaTable.class,
            Broadcaster.class,
            // search
            MediaForm.class,
            MediaSearchResult.class,
            MediaListItem.class,
            // update
            MediaIdentifiableImpl.class,
            ProgramUpdate.class,
            GroupUpdate.class,
            SegmentUpdate.class,
            MoveAction.class,
            BulkUpdate.class,
            ImageUpdate.class,
            LocationUpdate.class,
            StreamingStatusImpl.class,
            UploadResponse.class,
            StandaloneMemberRef.class,
            //
            TranscodeRequest.class,
            TranscodeStatus.class,
            ItemizeRequest.class,
            LiveItemizeRequest.class,
            ItemizeResponse.class,
            // no namespace
            XmlCollection.class
            //
        };
    }

    @Test
    void media() throws IOException {
        testNamespace(Xmlns.MEDIA_NAMESPACE);
    }

    @Test
    void mediaSearch() throws IOException {
        testNamespace(Xmlns.SEARCH_NAMESPACE);
    }

    @Test
    void shared() throws IOException {
        testNamespace(Xmlns.SHARED_NAMESPACE);
    }

    @Test
    void update() throws IOException {
        testNamespace(Xmlns.UPDATE_NAMESPACE);
    }

    @Test
    void absent() throws IOException {
        testNamespace("");
    }

    /**
     * Checks whether manual XSD contains the correct channels.
     */
    @Test
    void channels() {
        testMediaEnum( "channelEnum", Channel.class);
    }

    @Test
    void textualType() {
        testMediaEnum( "textualTypeEnum", TextualType.class);
    }

    @Test
    void programType() {
        testMediaEnum( "programTypeEnum", ProgramType.class);
    }

    @Test
    void groupType() {
        testMediaEnum( "groupTypeEnum", GroupType.class);
    }

    @Test
    void segmentType() {
        testMediaEnum( "segmentTypeEnum", SegmentType.class);
    }

    @Test
    void mediaType() {
        testMediaEnum("mediaTypeEnum", MediaType.class);
    }

    @Test
    void ageRatingType() {
        testMediaEnum("ageRatingType", AgeRating.class);
    }

    @Test
    void contentRating() {
        testMediaEnum("contentRatingType", ContentRating.class);
    }
    @Test
    void roleType() {
        testMediaEnum("roleType", RoleType.class);
    }

    @Test
    void intentionType() {
        testMediaEnum("intentionEnum", IntentionType.class);
    }

    @Test
    void targetGroup() {
        testMediaEnum("targetGroupEnum", TargetGroupType.class);
    }


    @Test
    void ownerType() {
        testSharedEnum("ownerTypeEnum", OwnerType.class);
    }


    @Test
    void region() {
        testMediaEnum("geoRestrictionEnum", Region.class);
    }


    @Test
    void aspectRatio() {
        testMediaEnum("aspectRatioEnum", AspectRatio.class);
    }


    @Test
    void license() {
        //testMediaEnum("targetGroupEnum", TargetGroupType.class);
    }

    @Test
    void chapterType() {
        testMediaEnum("chapterType", ChapterType.class);

    }

    @Test
    void workflow() {
        testSharedEnum("workflowEnumType", Workflow.class);
    }

    @Test
    void subtitlesType() {
        testSharedEnum("subtitlesTypeEnum", SubtitlesType.class);
    }

    @Test
    void subtitlesWorkflow() {
        testSharedEnum("subtitlesWorkflowEnum", SubtitlesWorkflow.class);
    }

    @Test
    void aVType() {
        testMediaEnum("avTypeEnum", AVType.class);
    }

    @Test
    void licenseEnum() {
        testEnum("/nl/vpro/domain/media/vproShared.xsd", "licenseEnum",
            () -> Arrays.stream(License.values())
                .filter(License::display)
                .map(License::getId).toList());
    }


    @Test
    void platform() {
        testMediaEnum("platformTypeEnum", Platform.class);
    }

    protected <T extends Enum<T>> void testMediaEnum(String enumTypeName, Class<T> enumClass)  {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", enumTypeName, enumClass);
    }

    protected <T extends Enum<T>> void testSharedEnum(String enumTypeName, Class<T> enumClass)  {
        testEnum("/nl/vpro/domain/media/vproShared.xsd", enumTypeName, enumClass);
    }

}
