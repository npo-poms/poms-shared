/*
 * Copyright (C) 2012 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;

import static nl.vpro.domain.media.MediaTestDataBuilder.*;
import static nl.vpro.jassert.assertions.MediaAssertions.mediaAssertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
@SuppressWarnings({"deprecation", "CodeBlock2Expr", "RedundantCast"})
public class MediaAssertTest {

    @Test
    public void testIsProgramOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat(null).isProgram();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testIsProgram() {
        mediaAssertThat((program().build())).isProgram();
    }

    @Test
    public void testIsProgramOnType() {
        mediaAssertThat((program().withType().build())).isProgram(ProgramType.BROADCAST);
    }

    @Test
    public void testIsGroupOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isGroup();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testIsGroup() {
        mediaAssertThat((group().build())).isGroup();
    }

    @Test
    public void testIsGroupOnType() {
        mediaAssertThat((group().withType().build())).isGroup(GroupType.PLAYLIST);
    }

    @Test
    public void testHasPoSeriesIDOnOtherClass() {
        assertThatThrownBy(() -> {
            mediaAssertThat((segment().build())).hasPoSeriesID("VPROWON_12345");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasPoSeriesIDOnOtherId() {
        assertThatThrownBy(() -> {
            mediaAssertThat((segment().build())).hasPoSeriesID("no match");
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasPoSeriesIDOnGroup() {
        mediaAssertThat((group().withPoSeriesID().build())).hasPoSeriesID("VPRO_12345");
    }

    @Test
    public void testIsSegmentOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isSegment();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testIsSegment() {
        mediaAssertThat((segment().build())).isSegment();
    }

    @Test
    public void testIsVideoOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject) null).isVideo();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testIsVideo() {
        mediaAssertThat(program().avType(AVType.VIDEO).build()).isVideo();
    }

    @Test
    public void testIsAudioOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isAudio();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testIsAudio() {
        mediaAssertThat(program().avType(AVType.AUDIO).build()).isAudio();
    }

    @Test
    public void testIsMixedOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject) null).isMixed();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testIsMixed() {
        mediaAssertThat(program().avType(AVType.MIXED).build()).isMixed();
    }

    @Test
    public void testHasWorkflowOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasWorkflow(Workflow.PUBLISHED);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasWorkflowOnNullArgument() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withWorkflow().build()).hasWorkflow(null);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasWorkflow() {
        mediaAssertThat((program().withWorkflow().build())).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test
    public void testHasTitleOnNull() {
        assertThatThrownBy(() -> {
        mediaAssertThat((MediaObject)null).hasTitles();
    }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasBroadcasterWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasBroadcasters();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasBroadcaster() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters();
    }

    @Test
    public void testHasBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters("AVRO");
    }

    @Test
    public void testHasOnlyBroadcasterWithIdsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasOnlyBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO", "BNN");
    }

    @Test
    public void testHasBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test
    public void testHasOnlyBroadcastersOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"));
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasOnlyBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"), new Broadcaster("BNN", "BNN"));
    }

    @Test
    public void testHasTitleWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasTitles();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasTitle() {
        mediaAssertThat(program().withTitles().build()).hasTitles();
    }

    @Test
    public void testHasTitleWithOwnerAndType() {
        mediaAssertThat(program().withTitles().build()).hasTitle(OwnerType.BROADCASTER, TextualType.SHORT);
    }

    @Test
    public void testHasTitleForTextAndTypeOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.SUB);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasTitleForTextAndType() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.MAIN);
    }

    @Test
    public void testHasTitleForTextAndOwnerOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasTitleForTextAndOwner() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasTitleForAllOwners() {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    public void testHasTitleOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasTitleForAllTypes() {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.MAIN, TextualType.SHORT);
    }

    @Test
    public void testHasTitleOnMissingType() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.ORIGINAL);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasOnlyTitle() {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    public void testHasOnlyTitleWithFailingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasDescriptionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasDescriptions();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasDescriptionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasDescriptions();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasDescription() {
        mediaAssertThat(program().withDescriptions().build()).hasDescriptions();
    }

    @Test
    public void testHasDescriptionWithOwnerAndType() {
        mediaAssertThat(program().withDescriptions().build()).hasDescription(OwnerType.MIS, TextualType.MAIN);
    }

    @Test
    public void testHasDescriptionForTextAndTypeOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.SUB);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasDescriptionForTextAndType() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.MAIN);
    }

    @Test
    public void testHasDescriptionForTextAndOwnerOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasDescriptionForTextAndOwner() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.BROADCASTER);
    }

    @Test
    public void testHasDescriptionForAllOwners() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    public void testHasDescriptionOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasDescriptionForAllTypes() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.MAIN, TextualType.SHORT);
    }

    @Test
    public void testHasDescriptionOnMissingType() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.ORIGINAL);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasOnlyDescriptionWithOwner() {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS, OwnerType.BROADCASTER);
    }

    @Test
    public void testHasOnlyDescriptionWithFailingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasPredictionsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPredictions();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasPredictionsOnPlatform() {
        mediaAssertThat(program().withPredictions().build()).hasPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    public void testHasOnlyPredictionsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasOnlyPredictions() {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    public void testHasPredictionOnPlatformAndState() {
        mediaAssertThat(program().withPredictions().build()).hasPrediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
    }

    @Test
    public void testHasLocation() {
        mediaAssertThat(program().withLocations().build()).hasLocations();
    }

    @Test
    public void testHasLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test
    public void testHasLocationOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasOnlyLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test
    public void testHasOnlyLocationForFailingOwners() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithUrl() {
        mediaAssertThat((program().withLocations().build())).hasLocations("http://player.omroep.nl/?aflID=4393288", "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf");
    }

    @Test
    public void testHasLocationOnMissingUrl() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocations("http:missing");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestrictionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat(null).hasLocationWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestrictionWhenMissing() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocationWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestriction() {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasLocationWithRestriction();
    }

    @Test
    public void testHasLocationWithRestrictionOnlyOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestrictionOnlyWhenMissing() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestrictionOnlyWhenNotAllSet() {
        assertThatThrownBy(() -> {
            Program program = program().withLocations().build();
            program.getLocations().first().setPublishStartInstant(Instant.now());
            mediaAssertThat(program).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasLocationWithRestrictionOnly() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test
    public void testHasPublicationWindowOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasPublicationWindow();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasPublicationWindow() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPublicationWindow();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasPublicationWindowOnStart() {
        mediaAssertThat(program().withPublishStart().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPublicationWindowOnStop() {
        mediaAssertThat(program().withPublishStop().build()).hasPublicationWindow();
    }

    @Test
    public void testHasPortalRestrictionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasPortalRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasPortalRestrictionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPortalRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).hasPortalRestriction();
    }

    @Test
    public void testHasGeoRestrictionOnNull() {
        assertThatThrownBy(() -> {

            mediaAssertThat((MediaObject)null).hasGeoRestriction();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasGeoRestrictionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasGeoRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).hasGeoRestriction();
    }

    @Test
    public void testIsRestrictedOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isRestricted();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testIsRestrictedWhenNotRestricted() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).isRestricted();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testIsRestrictedWithPublishStart() {
        mediaAssertThat(program().withPublishStart().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).isRestricted();
    }

    @Test
    public void testIsRestrictedWithRestrictedLocations() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).isRestricted();
    }

    @Test
    public void testHasRelationsOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasRelations();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    public void testHasRelationsWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasRelations();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasRelations() {
        mediaAssertThat(program().withRelations().build()).hasRelations();
    }

    @Test
    public void testHasRelationWhenMissing() {
        assertThatThrownBy(() -> {

            mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "AVRO")));
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    public void testHasRelation() {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.bluenote.com/", "Blue Note"));
    }

}
