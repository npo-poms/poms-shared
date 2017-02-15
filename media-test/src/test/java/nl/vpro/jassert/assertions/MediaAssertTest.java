/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import java.time.Instant;

import org.junit.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.OwnerType;
import nl.vpro.domain.media.support.TextualType;
import nl.vpro.domain.media.support.Workflow;
import nl.vpro.domain.user.Broadcaster;

import static nl.vpro.domain.media.MediaTestDataBuilder.*;
import static nl.vpro.jassert.assertions.MediaAssertions.mediaAssertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class MediaAssertTest {

    @Test(expected = AssertionError.class)
    public void testIsProgramOnNull() throws Exception {
        mediaAssertThat(null).isProgram();
    }

    @Test
    public void testIsProgram() throws Exception {
        mediaAssertThat((program().build())).isProgram();
    }

    @Test
    public void testIsProgramOnType() throws Exception {
        mediaAssertThat((program().withType().build())).isProgram(ProgramType.BROADCAST);
    }

    @Test(expected = AssertionError.class)
    public void testIsGroupOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isGroup();
    }

    @Test
    public void testIsGroup() throws Exception {
        mediaAssertThat((group().build())).isGroup();
    }

    @Test
    public void testIsGroupOnType() throws Exception {
        mediaAssertThat((group().withType().build())).isGroup(GroupType.PLAYLIST);
    }

    @Test(expected = AssertionError.class)
    public void testHasPoSeriesIDOnOtherClass() throws Exception {
        mediaAssertThat((segment().build())).hasPoSeriesID("VPROWON_12345");
    }

    @Test(expected = AssertionError.class)
    public void testHasPoSeriesIDOnOtherId() throws Exception {
        mediaAssertThat((segment().build())).hasPoSeriesID("no match");
    }

    @Test
    public void testHasPoSeriesIDOnGroup() throws Exception {
        mediaAssertThat((group().withPoSeriesID().build())).hasPoSeriesID("VPRO_12345");
    }

    @Test(expected = AssertionError.class)
    public void testIsSegmentOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isSegment();
    }

    @Test
    public void testIsSegment() throws Exception {
        mediaAssertThat((segment().build())).isSegment();
    }

    @Test(expected = AssertionError.class)
    public void testIsVideoOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isVideo();
    }

    @Test
    public void testIsVideo() throws Exception {
        mediaAssertThat(program().avType(AVType.VIDEO).build()).isVideo();
    }

    @Test(expected = AssertionError.class)
    public void testIsAudioOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isAudio();
    }

    @Test
    public void testIsAudio() throws Exception {
        mediaAssertThat(program().avType(AVType.AUDIO).build()).isAudio();
    }

    @Test(expected = AssertionError.class)
    public void testIsMixedOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isMixed();
    }

    @Test
    public void testIsMixed() throws Exception {
        mediaAssertThat(program().avType(AVType.MIXED).build()).isMixed();
    }

    @Test(expected = AssertionError.class)
    public void testHasWorkflowOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test(expected = AssertionError.class)
    public void testHasWorkflowOnNullArgument() throws Exception {
        mediaAssertThat(program().withWorkflow().build()).hasWorkflow(null);
    }

    @Test
    public void testHasWorkflow() throws Exception {
        mediaAssertThat((program().withWorkflow().build())).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasTitles();
    }

    @Test(expected = AssertionError.class)
    public void testHasBroadcasterWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasBroadcasters();
    }

    @Test
    public void testHasBroadcaster() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters();
    }

    @Test
    public void testHasBroadcasterWithIds() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters("AVRO");
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyBroadcasterWithIdsOnFailure() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO");
    }

    @Test
    public void testHasOnlyBroadcasterWithIds() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO", "BNN");
    }

    @Test
    public void testHasBroadcasters() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyBroadcastersOnFailure() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test
    public void testHasOnlyBroadcasters() throws Exception {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"), new Broadcaster("BNN", "BNN"));
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasTitles();
    }

    @Test
    public void testHasTitle() throws Exception {
        mediaAssertThat(program().withTitles().build()).hasTitles();
    }

    @Test
    public void testHasTitleWithOwnerAndType() throws Exception {
        mediaAssertThat(program().withTitles().build()).hasTitle(OwnerType.BROADCASTER, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleForTextAndTypeOnFailure() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.SUB);
    }

    public void testHasTitleForTextAndType() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleForTextAndOwnerOnFailure() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.MIS);
    }

    public void testHasTitleForTextAndOwner() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasTitleForAllOwners() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnMissingOwner() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.CERES);
    }

    @Test
    public void testHasTitleForAllTypes() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.MAIN, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasTitleOnMissingType() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.ORIGINAL);
    }

    @Test
    public void testHasOnlyTitle() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyTitleWithFailingOwner() throws Exception {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasDescriptions();
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasDescriptions();
    }

    @Test
    public void testHasDescription() throws Exception {
        mediaAssertThat(program().withDescriptions().build()).hasDescriptions();
    }

    @Test
    public void testHasDescriptionWithOwnerAndType() throws Exception {
        mediaAssertThat(program().withDescriptions().build()).hasDescription(OwnerType.MIS, TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionForTextAndTypeOnFailure() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.SUB);
    }

    public void testHasDescriptionForTextAndType() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.MAIN);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionForTextAndOwnerOnFailure() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.MIS);
    }

    public void testHasDescriptionForTextAndOwner() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasDescriptionForAllOwners() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnMissingOwner() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.CERES);
    }

    @Test
    public void testHasDescriptionForAllTypes() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.MAIN, TextualType.SHORT);
    }

    @Test(expected = AssertionError.class)
    public void testHasDescriptionOnMissingType() throws Exception {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.ORIGINAL);
    }

    @Test
    public void testHasOnlyDescriptionWithOwner() throws Exception {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS, OwnerType.BROADCASTER);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyDescriptionWithFailingOwner() throws Exception {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS);
    }

    @Test(expected = AssertionError.class)
    public void testHasPredictionsOnFailure() throws Exception {
        mediaAssertThat(program().build()).hasPredictions();
    }

    @Test
    public void testHasPredictionsOnPlatform() throws Exception {
        mediaAssertThat(program().withPredictions().build()).hasPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyPredictionsOnFailure() throws Exception {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD);
    }

    @Test
    public void testHasOnlyPredictions() throws Exception {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    public void testHasPredictionOnPlatformAndState() throws Exception {
        mediaAssertThat(program().withPredictions().build()).hasPrediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
    }

    @Test
    public void testHasLocation() throws Exception {
        mediaAssertThat(program().withLocations().build()).hasLocations();
    }

    @Test
    public void testHasLocationForAllOwners() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationOnMissingOwner() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.CERES);
    }

    @Test
    public void testHasOnlyLocationForAllOwners() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test(expected = AssertionError.class)
    public void testHasOnlyLocationForFailingOwners() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER);
    }

    @Test
    public void testHasLocationWithUrl() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasLocations("http://player.omroep.nl/?aflID=4393288", "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf");
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationOnMissingUrl() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasLocations("http:missing");
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasLocationWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionWhenMissing() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasLocationWithRestriction();
    }

    @Test
    public void testHasLocationWithRestriction() throws Exception {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasLocationWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyWhenMissing() throws Exception {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasLocationWithRestrictionOnlyWhenNotAllSet() throws Exception {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test
    public void testHasLocationWithRestrictionOnly() throws Exception {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasPublicationWindowOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasPublicationWindow();
    }

    @Test(expected = AssertionError.class)
    public void testHasPublicationWindow() throws Exception {
        mediaAssertThat(program().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPublicationWindowOnStart() throws Exception {
        mediaAssertThat(program().withPublishStart().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPublicationWindowOnStop() throws Exception {
        mediaAssertThat(program().withPublishStop().build()).hasPublicationWindow();
    }

    @Test(expected = AssertionError.class)
    public void testHasPortalRestrictionOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasPortalRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasPortalRestrictionWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasPortalRestriction();
    }

    @Test
    public void testHasPortalRestriction() throws Exception {
        mediaAssertThat(program().withPortalRestrictions().build()).hasPortalRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasGeoRestrictionOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasGeoRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testHasGeoRestrictionWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasGeoRestriction();
    }

    @Test
    public void testHasGeoRestriction() throws Exception {
        mediaAssertThat(program().withGeoRestrictions().build()).hasGeoRestriction();
    }

    @Test(expected = AssertionError.class)
    public void testIsRestrictedOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).isRestricted();
    }

    @Test(expected = AssertionError.class)
    public void testIsRestrictedWhenNotRestricted() throws Exception {
        mediaAssertThat(program().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithPublishStart() throws Exception {
        mediaAssertThat(program().withPublishStart().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithPortalRestriction() throws Exception {
        mediaAssertThat(program().withPortalRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithGeoRestriction() throws Exception {
        mediaAssertThat(program().withGeoRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithRestrictedLocations() throws Exception {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).isRestricted();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationsOnNull() throws Exception {
        mediaAssertThat((MediaObject)null).hasRelations();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationsWhenEmpty() throws Exception {
        mediaAssertThat(program().build()).hasRelations();
    }

    @Test
    public void testHasRelations() throws Exception {
        mediaAssertThat(program().withRelations().build()).hasRelations();
    }

    @Test(expected = AssertionError.class)
    public void testHasRelationWhenMissing() throws Exception {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "AVRO")));
    }

    @Test
    public void testHasRelation() throws Exception {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.bluenote.com/", "Blue Note"));
    }
/*
    @Test
    public void testHasCeresRecord() throws Exception {
        Location location = new Location("http://bla/", OwnerType.BROADCASTER);
        Program program = program().locations(location).id(1L).build();

        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);
        mediaAssertThat(program).hasCeresRecord(Platform.INTERNETVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasCeresRecordForOtherPlatform() throws Exception {
        Location location = new Location("http://bla/", OwnerType.BROADCASTER);
        Program program = program().locations(location).id(1L).build();
        LocationAuthorityRecord.authoritative(program, Platform.INTERNETVOD);
        mediaAssertThat(program).hasCeresRecord(Platform.PLUSVOD);
    }

    @Test(expected = AssertionError.class)
    public void testHasCeresRecordWhenFalse() throws Exception {
        mediaAssertThat(program().build()).hasCeresRecord(Platform.INTERNETVOD);
    }*/
}
