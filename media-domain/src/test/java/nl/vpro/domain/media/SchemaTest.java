package nl.vpro.domain.media;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.Xmlns;
import nl.vpro.domain.media.search.*;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Workflow;
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
@Slf4j
public class SchemaTest extends AbstractSchemaTest {

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
    public void testMedia() throws IOException {
        testNamespace(Xmlns.MEDIA_NAMESPACE);
    }

    @Test
    public void testMediaSearch() throws IOException {
        testNamespace(Xmlns.SEARCH_NAMESPACE);
    }

    @Test
    public void testShared() throws IOException {
        testNamespace(Xmlns.SHARED_NAMESPACE);
    }

    @Test
    public void testUpdate() throws IOException {
        testNamespace(Xmlns.UPDATE_NAMESPACE);
    }

    @Test
    public void testAbsent() throws IOException {
        testNamespace("");
    }

    /**
     * Checks whether manual XSD contains the correct channels.
     */
    @Test
    public void testChannels() {
        testMediaEnum( "channelEnum", Channel.class);
    }

    @Test
    public void testTextualType() {
        testMediaEnum( "textualTypeEnum", TextualType.class);
    }

    @Test
    public void testProgramType() {
        testMediaEnum( "programTypeEnum", ProgramType.class);
    }

    @Test
    public void testGroupType() {
        testMediaEnum( "groupTypeEnum", GroupType.class);
    }

    @Test
    public void testSegmentType() {
        testMediaEnum( "segmentTypeEnum", SegmentType.class);
    }

    @Test
    public void testMediaType() {
        testMediaEnum("mediaTypeEnum", MediaType.class);
    }

    @Test
    public void testAgeRatingType() {
        testMediaEnum("ageRatingType", AgeRating.class);
    }

    @Test
    public void testContentRating() {
        testMediaEnum("contentRatingType", ContentRating.class);
    }
    @Test
    public void testRoleType() {
        testMediaEnum("roleType", RoleType.class);
    }

    @Test
    public void testIntentionType() {
        testMediaEnum("intentionEnum", IntentionType.class);
    }

    @Test
    public void testTargetGroup() {
        testMediaEnum("targetGroupEnum", TargetGroupType.class);
    }

    @Test
    public void testRegion() {
        testMediaEnum("geoRestrictionEnum", Region.class);
    }

    @Test
    public void testLicense() {
        //testMediaEnum("targetGroupEnum", TargetGroupType.class);
    }


    @Test
    public void testWorkflow() {
        testSharedEnum("workflowEnumType", Workflow.class);
    }

    @Test
    public void testSubtitlesType() {
        testSharedEnum("subtitlesTypeEnum", SubtitlesType.class);
    }

    @Test
    public void testSubtitlesWorkflow() {
        testSharedEnum("subtitlesWorkflowEnum", SubtitlesWorkflow.class);
    }

    @Test
    public void testAVType() {
        testMediaEnum("avTypeEnum", AVType.class);
    }

    @Test
    public void testLicenseEnum() {
        testEnum("/nl/vpro/domain/media/vproShared.xsd", "licenseEnum",
            () -> Arrays.stream(License.values())
                .filter(License::display)
                .map(License::getId).toList());
    }


    @Test
    public void testPlatform() {
        testMediaEnum("platformTypeEnum", Platform.class);
    }

    protected <T extends Enum<T>> void testMediaEnum(String enumTypeName, Class<T> enumClass)  {
        testEnum("/nl/vpro/domain/media/vproMedia.xsd", enumTypeName, enumClass);
    }

    protected <T extends Enum<T>> void testSharedEnum(String enumTypeName, Class<T> enumClass)  {
        testEnum("/nl/vpro/domain/media/vproShared.xsd", enumTypeName, enumClass);
    }

}
