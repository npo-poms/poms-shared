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
class MediaAssertTest {

    @Test
    void isProgramOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat(null).isProgram();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void isProgram() {
        mediaAssertThat((program().build())).isProgram();
    }

    @Test
    void isProgramOnType() {
        mediaAssertThat((program().withType().build())).isProgram(ProgramType.BROADCAST);
    }

    @Test
    void isGroupOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isGroup();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void isGroup() {
        mediaAssertThat((group().build())).isGroup();
    }

    @Test
    void isGroupOnType() {
        mediaAssertThat((group().withType().build())).isGroup(GroupType.PLAYLIST);
    }

    @Test
    void hasPoSeriesIDOnOtherClass() {
        assertThatThrownBy(() -> {
            mediaAssertThat((segment().build())).hasPoSeriesID("VPROWON_12345");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasPoSeriesIDOnOtherId() {
        assertThatThrownBy(() -> {
            mediaAssertThat((segment().build())).hasPoSeriesID("no match");
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasPoSeriesIDOnGroup() {
        mediaAssertThat((group().withPoSeriesID().build())).hasPoSeriesID("VPRO_12345");
    }

    @Test
    void isSegmentOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isSegment();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void isSegment() {
        mediaAssertThat((segment().build())).isSegment();
    }

    @Test
    void isVideoOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject) null).isVideo();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void isVideo() {
        mediaAssertThat(program().avType(AVType.VIDEO).build()).isVideo();
    }

    @Test
    void isAudioOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isAudio();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void isAudio() {
        mediaAssertThat(program().avType(AVType.AUDIO).build()).isAudio();
    }

    @Test
    void isMixedOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject) null).isMixed();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void isMixed() {
        mediaAssertThat(program().avType(AVType.MIXED).build()).isMixed();
    }

    @Test
    void hasWorkflowOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasWorkflow(Workflow.PUBLISHED);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasWorkflowOnNullArgument() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withWorkflow().build()).hasWorkflow(null);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasWorkflow() {
        mediaAssertThat((program().withWorkflow().build())).hasWorkflow(Workflow.PUBLISHED);
    }

    @Test
    void hasTitleOnNull() {
        assertThatThrownBy(() -> {
        mediaAssertThat((MediaObject)null).hasTitles();
    }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasBroadcasterWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasBroadcasters();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasBroadcaster() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters();
    }

    @Test
    void hasBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters("AVRO");
    }

    @Test
    void hasOnlyBroadcasterWithIdsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasOnlyBroadcasterWithIds() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters("AVRO", "BNN");
    }

    @Test
    void hasBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasBroadcasters(new Broadcaster("AVRO", "AVRO"));
    }

    @Test
    void hasOnlyBroadcastersOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"));
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasOnlyBroadcasters() {
        mediaAssertThat(program().withBroadcasters().build()).hasOnlyBroadcasters(new Broadcaster("AVRO", "AVRO"), new Broadcaster("BNN", "BNN"));
    }

    @Test
    void hasTitleWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasTitles();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasTitle() {
        mediaAssertThat(program().withTitles().build()).hasTitles();
    }

    @Test
    void hasTitleWithOwnerAndType() {
        mediaAssertThat(program().withTitles().build()).hasTitle(OwnerType.BROADCASTER, TextualType.SHORT);
    }

    @Test
    void hasTitleForTextAndTypeOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.SUB);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasTitleForTextAndType() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", TextualType.MAIN);
    }

    @Test
    void hasTitleForTextAndOwnerOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasTitleForTextAndOwner() {
        mediaAssertThat((program().withTitles().build())).hasTitle("Main title", OwnerType.BROADCASTER);
    }

    @Test
    void hasTitleForAllOwners() {
        mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    void hasTitleOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasTitleForAllTypes() {
        mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.MAIN, TextualType.SHORT);
    }

    @Test
    void hasTitleOnMissingType() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasTitle(TextualType.ORIGINAL);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasOnlyTitle() {
        mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    void hasOnlyTitleWithFailingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withTitles().build())).hasOnlyTitles(OwnerType.BROADCASTER);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasDescriptionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasDescriptions();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasDescriptionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasDescriptions();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasDescription() {
        mediaAssertThat(program().withDescriptions().build()).hasDescriptions();
    }

    @Test
    void hasDescriptionWithOwnerAndType() {
        mediaAssertThat(program().withDescriptions().build()).hasDescription(OwnerType.MIS, TextualType.MAIN);
    }

    @Test
    void hasDescriptionForTextAndTypeOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.SUB);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasDescriptionForTextAndType() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", TextualType.MAIN);
    }

    @Test
    void hasDescriptionForTextAndOwnerOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasDescriptionForTextAndOwner() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription("Main description", OwnerType.BROADCASTER);
    }

    @Test
    void hasDescriptionForAllOwners() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.BROADCASTER, OwnerType.MIS);
    }

    @Test
    void hasDescriptionOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasDescriptionForAllTypes() {
        mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.MAIN, TextualType.SHORT);
    }

    @Test
    void hasDescriptionOnMissingType() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withDescriptions().build())).hasDescription(TextualType.ORIGINAL);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasOnlyDescriptionWithOwner() {
        mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS, OwnerType.BROADCASTER);
    }

    @Test
    void hasOnlyDescriptionWithFailingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withDescriptions().build()).hasOnlyDescriptions(OwnerType.MIS);
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasPredictionsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPredictions();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasPredictionsOnPlatform() {
        mediaAssertThat(program().withPredictions().build()).hasPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    void hasOnlyPredictionsOnFailure() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasOnlyPredictions() {
        mediaAssertThat(program().withPredictions().build()).hasOnlyPredictions(Platform.TVVOD, Platform.INTERNETVOD);
    }

    @Test
    void hasPredictionOnPlatformAndState() {
        mediaAssertThat(program().withPredictions().build()).hasPrediction(Platform.INTERNETVOD, Prediction.State.REVOKED);
    }

    @Test
    void hasLocation() {
        mediaAssertThat(program().withLocations().build()).hasLocations();
    }

    @Test
    void hasLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test
    void hasLocationOnMissingOwner() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocation(OwnerType.CERES);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasOnlyLocationForAllOwners() {
        mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER, OwnerType.NEBO);
    }

    @Test
    void hasOnlyLocationForFailingOwners() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasOnlyLocation(OwnerType.BROADCASTER);
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithUrl() {
        mediaAssertThat((program().withLocations().build())).hasLocations("http://player.omroep.nl/?aflID=4393288", "http://cgi.omroep.nl/legacy/nebo?/id/KRO/serie/KRO_1237031/KRO_1242626/sb.20070211.asf");
    }

    @Test
    void hasLocationOnMissingUrl() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocations("http:missing");
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestrictionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat(null).hasLocationWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestrictionWhenMissing() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasLocationWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestriction() {
        Program program = program().withLocations().build();
        program.getLocations().first().setPublishStartInstant(Instant.now());
        mediaAssertThat(program).hasLocationWithRestriction();
    }

    @Test
    void hasLocationWithRestrictionOnlyOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestrictionOnlyWhenMissing() {
        assertThatThrownBy(() -> {
            mediaAssertThat((program().withLocations().build())).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestrictionOnlyWhenNotAllSet() {
        assertThatThrownBy(() -> {
            Program program = program().withLocations().build();
            program.getLocations().first().setPublishStartInstant(Instant.now());
            mediaAssertThat(program).hasOnlyLocationsWithRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasLocationWithRestrictionOnly() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).hasOnlyLocationsWithRestriction();
    }

    @Test
    void hasPublicationWindowOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasPublicationWindow();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasPublicationWindow() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPublicationWindow();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasPublicationWindowOnStart() {
        mediaAssertThat(program().withPublishStart().build()).hasPublicationWindow();
    }

    @Test
    void hasPublicationWindowOnStop() {
        mediaAssertThat(program().withPublishStop().build()).hasPublicationWindow();
    }

    @Test
    void hasPortalRestrictionOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasPortalRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasPortalRestrictionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasPortalRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).hasPortalRestriction();
    }

    @Test
    void hasGeoRestrictionOnNull() {
        assertThatThrownBy(() -> {

            mediaAssertThat((MediaObject)null).hasGeoRestriction();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasGeoRestrictionWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasGeoRestriction();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).hasGeoRestriction();
    }

    @Test
    void isRestrictedOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).isRestricted();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void isRestrictedWhenNotRestricted() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).isRestricted();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void isRestrictedWithPublishStart() {
        mediaAssertThat(program().withPublishStart().build()).isRestricted();
    }

    @Test
    void isRestrictedWithPortalRestriction() {
        mediaAssertThat(program().withPortalRestrictions().build()).isRestricted();
    }

    @Test
    void isRestrictedWithGeoRestriction() {
        mediaAssertThat(program().withGeoRestrictions().build()).isRestricted();
    }

    @Test
    void isRestrictedWithRestrictedLocations() {
        Program program = program().withLocations().build();
        for(Location location : program.getLocations()) {
            location.setPublishStartInstant(Instant.now());
        }
        mediaAssertThat(program).isRestricted();
    }

    @Test
    void hasRelationsOnNull() {
        assertThatThrownBy(() -> {
            mediaAssertThat((MediaObject)null).hasRelations();
        }).isInstanceOf(AssertionError.class);

    }

    @Test
    void hasRelationsWhenEmpty() {
        assertThatThrownBy(() -> {
            mediaAssertThat(program().build()).hasRelations();
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasRelations() {
        mediaAssertThat(program().withRelations().build()).hasRelations();
    }

    @Test
    void hasRelationWhenMissing() {
        assertThatThrownBy(() -> {

            mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "AVRO")));
        }).isInstanceOf(AssertionError.class);
    }

    @Test
    void hasRelation() {
        mediaAssertThat(program().withRelations().build()).hasRelation(new Relation(new RelationDefinition("LABEL", "VPRO"), "http://www.bluenote.com/", "Blue Note"));
    }

}
