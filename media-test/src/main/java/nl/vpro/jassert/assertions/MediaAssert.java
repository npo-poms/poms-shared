/*
 * Copyright (C) 2012 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.jassert.assertions;

import java.util.*;

import org.checkerframework.checker.nullness.qual.NonNull;

import org.apache.commons.lang3.tuple.Pair;
import org.assertj.core.api.Fail;

import nl.vpro.domain.media.*;
import nl.vpro.domain.media.support.*;
import nl.vpro.domain.user.Broadcaster;

import static nl.vpro.jassert.assertions.MediaAssertions.assertThat;
import static nl.vpro.jassert.assertions.MediaAssertions.locationAssertThat;

/**
 * @author Roelof Jan Koekoek
 * @since 1.5
 */
public class MediaAssert extends PublishableObjectAssert<MediaAssert, MediaObject> {

    protected MediaAssert(MediaObject actual) {
        super(actual, MediaAssert.class);
    }

    public MediaAssert isProgram() {
        isNotNull();

        assertThat((Object)actual).isInstanceOf(Program.class);
        return myself;

    }

    public MediaAssert isProgram(ProgramType type) {
        isProgram();

        assertThat(((Program)actual).getType()).isEqualTo(type);
        return myself;

    }

    public MediaAssert hasMid(String mid) {
        isProgram();
        assertThat(actual.getMid()).isEqualTo(mid);
        return myself;
    }

    public MediaAssert isGroup() {
        isNotNull();
        assertThat((Object)actual).isInstanceOf(Group.class);
        return myself;
    }

    public MediaAssert isGroup(GroupType type) {
        isGroup();
        assertThat(((Group)actual).getType()).isEqualTo(type);
        return myself;
    }

    public MediaAssert hasPoSeriesID(String poSeriesID) {
        String type = actual.getClass().getSimpleName();
        String id = "not available";
        try {
            isGroup();
            Group group = (Group)actual;
            type = group.getClass().getSimpleName();
            id = group.getMid();
            assertThat(id).isEqualTo(poSeriesID);
            return myself;
        } catch(AssertionError ignore) {
        }
        Fail.fail("expecting Program of Group with poSeriesID " + poSeriesID + ", got " + type + " with id " + id);
        return myself;
    }

    public MediaAssert isSegment() {
        isNotNull();
        assertThat((Object)actual).isInstanceOf(Segment.class);
        return myself;
    }

    public MediaAssert isVideo() {
        isNotNull();
        assertThat((Object) actual.getAVType()).isEqualTo(AVType.VIDEO);
        return myself;
    }

    public MediaAssert isAudio() {
        isNotNull();
        assertThat((Object) actual.getAVType()).isEqualTo(AVType.AUDIO);
        return myself;
    }

    public MediaAssert isMixed() {
        isNotNull();
        assertThat((Object) actual.getAVType()).isEqualTo(AVType.MIXED);
        return myself;
    }

    public MediaAssert hasBroadcasters() {
        isNotNull();
        try {
            assertThat(actual.getBroadcasters()).isNotEmpty();
            return myself;
        } catch(AssertionError e) {
            Fail.fail("expecting non-empty broadcasters");
            return myself;
        }
    }

    public MediaAssert hasBroadcasters(String... ids) {
        hasBroadcasters();
        validateIsNotNull(ids);
        Set<String> set = new TreeSet<>();
        for(Broadcaster broadcaster : actual.getBroadcasters()) {
            set.add(broadcaster.getId());
        }
        assertThat(set).contains(ids);
        return myself;
    }

    public MediaAssert hasOnlyBroadcasters(String... ids) {
        hasBroadcasters();
        validateIsNotNull(ids);
        Set<String> set = new TreeSet<>();
        for(Broadcaster broadcaster : actual.getBroadcasters()) {
            set.add(broadcaster.getId());
        }

        assertThat(set).containsOnly(ids);
        return myself;
    }

    public MediaAssert hasBroadcasters(Broadcaster... broadcasters) {
        hasBroadcasters();
        validateIsNotNull(broadcasters);

        assertThat(actual.getBroadcasters()).contains(broadcasters);
        return myself;
    }

    public MediaAssert hasOnlyBroadcasters(Broadcaster... broadcasters) {
        hasBroadcasters();
        validateIsNotNull(broadcasters);

        assertThat(actual.getBroadcasters()).containsOnly(broadcasters);
        return myself;
    }

    public MediaAssert hasTitles() {
        isNotNull();
        try {
            assertThat(actual.getTitles()).isNotEmpty();
            return myself;
        } catch(AssertionError e) {
            Fail.fail("expecting nog-empty titles");
            return myself;
        }
    }

    public MediaAssert hasTitle(String title, TextualType type) {
        hasTitles();

        Set<Pair<String, TextualType>> titles = new TreeSet<>();
        for(Title t : actual.getTitles()) {
            titles.add(Pair.of(t.get(), t.getType()));
        }

        Pair<String, TextualType> expected = Pair.of(title, type);
        try {
            assertThat(titles).contains(expected);

        } catch(AssertionError error) {

            Fail.fail(String.format("""
                missing title
                    TITLES:    %s
                    EXPECTED:  %s""", titles, expected));
        }
        return myself;
    }

    public MediaAssert hasTitle(String title, OwnerType type) {
        hasTitles();

        Set<Pair<String, OwnerType>> titles = new TreeSet<>();
        for(Title t : actual.getTitles()) {
            titles.add(Pair.of(t.get(), t.getOwner()));
        }

        Pair<String, OwnerType> expected = Pair.of(title, type);
        try {
            assertThat(titles).contains(expected);

        } catch(AssertionError error) {
            Fail.fail(String.format("""
                missing title
                    TITLES:    %s
                    EXPECTED:  %s""", titles, expected));
        }
        return myself;
    }

    public MediaAssert hasTitle(OwnerType owner, TextualType type) {
        hasTitles();

        Set<Pair<OwnerType, TextualType>> titles = new TreeSet<>();
        for(Title title : actual.getTitles()) {
            titles.add(Pair.of(title.getOwner(), title.getType()));
        }

        Pair<OwnerType, TextualType> expected = Pair.of(owner, type);
        try {
            assertThat(titles).contains(expected);
            return myself;
        } catch(AssertionError error) {
            Fail.fail(String.format("""
                missing title
                    TITLES:    %s
                    EXPECTED:  %s""", titles, expected));
            return myself;
        }
    }

    public MediaAssert hasTitle(OwnerType... owners) {
        hasTitles();
        hasOwners(actual.getTitles(), owners);
        return myself;
    }

    public MediaAssert hasOnlyTitles(OwnerType... owners) {
        hasTitles();
        hasOnlyOwners(actual.getTitles(), owners);
        return myself;
    }

    public MediaAssert hasTitle(TextualType... types) {
        hasTitles();
        hasTypes(actual.getTitles(), types);
        return myself;
    }

    public MediaAssert hasOnlyTitle(TextualType... types) {
        hasTitles();
        hasOnlyTypes(actual.getTitles(), types);
        return myself;
    }

    public MediaAssert hasDescriptions() {
        isNotNull();
        try {
            assertThat(actual.getDescriptions()).isNotEmpty();
            return myself;
        } catch(AssertionError e) {
            Fail.fail("expecting nog-empty description");
            return myself;
        }
    }

    public MediaAssert hasDescription(String Description, TextualType type) {
        hasDescriptions();

        Set<Pair<String, TextualType>> Descriptions = new TreeSet<>();
        for(Description t : actual.getDescriptions()) {
            Descriptions.add(Pair.of(t.get(), t.getType()));
        }

        Pair<String, TextualType> expected = Pair.of(Description, type);
        try {
            assertThat(Descriptions).contains(expected);

        } catch(AssertionError error) {
            Fail.fail(String.format("""
                missing description
                    DESCRIPTIONS:    %s
                    EXPECTED:  %s""", Descriptions, expected));
        }
        return myself;
    }

    public MediaAssert hasDescription(String Description, OwnerType type) {
        hasDescriptions();

        Set<Pair<String, OwnerType>> Descriptions = new TreeSet<>();
        for(Description t : actual.getDescriptions()) {
            Descriptions.add(Pair.of(t.get(), t.getOwner()));
        }

        Pair<String, OwnerType> expected = Pair.of(Description, type);
        try {
            assertThat(Descriptions).contains(expected);

        } catch(AssertionError error) {
            Fail.fail(String.format("""
                missing description
                    DESCRIPTIONS:    %s
                    EXPECTED:  %s""", Descriptions, expected));
        }
        return myself;
    }

    public MediaAssert hasDescription(OwnerType owner, TextualType type) {
        hasDescriptions();

        Set<Pair<OwnerType, TextualType>> descriptions = new TreeSet<>();
        for(Description description : actual.getDescriptions()) {
            descriptions.add(Pair.of(description.getOwner(), description.getType()));
        }

        Pair<OwnerType, TextualType> expected = Pair.of(owner, type);
        try {
            assertThat(descriptions).contains(expected);

        } catch(AssertionError error) {
            Fail.fail(String.format("""
                missing description
                    DESCRIPTIONS: %s
                    EXPECTED:     %s""", descriptions, expected));
        }
        return myself;
    }

    public MediaAssert hasDescription(OwnerType... owners) {
        hasDescriptions();
        hasOwners(actual.getDescriptions(), owners);
        return myself;
    }

    public MediaAssert hasOnlyDescriptions(OwnerType... owners) {
        hasDescriptions();
        hasOnlyOwners(actual.getDescriptions(), owners);
        return myself;
    }

    public MediaAssert hasDescription(TextualType... types) {
        hasDescriptions();
        hasTypes(actual.getDescriptions(), types);
        return myself;
    }

    public MediaAssert hasOnlyDescription(TextualType... types) {
        hasDescriptions();
        hasOnlyTypes(actual.getDescriptions(), types);
        return myself;
    }

    public MediaAssert hasPredictions() {
        isNotNull();
        try {
            assertThat(actual.getPredictions()).isNotEmpty();

        } catch(AssertionError e) {
            Fail.fail("expecting nog-empty predictions");
        }
        return myself;
    }

    public MediaAssert hasPredictions(Platform... platforms) {
        hasPredictions();
        Set<Platform> set = new TreeSet<>();
        for(Prediction prediction : actual.getPredictions()) {
            set.add(prediction.getPlatform());
        }

        assertThat(set).contains(platforms);
        return myself;

    }

    public MediaAssert hasOnlyPredictions(Platform... platforms) {
        hasPredictions();
        Set<Platform> set = new TreeSet<>();
        for(Prediction prediction : actual.getPredictions()) {
            set.add(prediction.getPlatform());
        }

        assertThat(set).containsOnly(platforms);
        return myself;

    }

    public MediaAssert hasPrediction(Platform platform, Prediction.State state) {
        hasPredictions();

        Set<Pair<Platform, Prediction.State>> predictions = new TreeSet<>();
        for(Prediction prediction : actual.getPredictions()) {
            predictions.add(Pair.of(prediction.getPlatform(), prediction.getState()));
        }

        Pair<Platform, Prediction.State> expected = Pair.of(platform, state);
        try {
            assertThat(predictions).contains(expected);
        } catch(AssertionError error) {
            Fail.fail(String.format(
                "    PREDICTIONS:    %s\n" +
                "    EXPECTED:  %s", predictions, expected));
        }
        return myself;

    }

    public MediaAssert hasLocations() {
        isNotNull();
        try {
            assertThat(actual.getLocations()).isNotEmpty();

        } catch(AssertionError e) {
            Fail.fail("expecting non-empty locations");
        }
        return myself;
    }

    public MediaAssert hasLocation(OwnerType... owners) {
        hasLocations();
        hasOwners(actual.getLocations(), owners);
        return myself;
    }

    public MediaAssert hasOnlyLocation(OwnerType... owners) {
        hasLocations();
        hasOnlyOwners(actual.getLocations(), owners);
        return myself;
    }

    public MediaAssert hasLocations(String... urls) {
        hasLocations();
        Set<String> set = new TreeSet<>();
        for(Location location : actual.getLocations()) {
            set.add(location.getProgramUrl());
        }

        assertThat(set).contains(urls);
        return myself;

    }

    public MediaAssert hasLocationWithRestriction() {
        isNotNull();
        hasLocations();
        for(Location location : actual.getLocations()) {
            try {
                locationAssertThat(location).hasPublicationWindow();
                return myself;
            } catch(AssertionError ignore) {

            }
        }
        Fail.fail("expecting al least one location with a publication window");
        return myself;
    }

    public MediaAssert hasOnlyLocationsWithRestriction() {
        isNotNull();
        hasLocations();
        for(Location location : actual.getLocations()) {
            try {
                locationAssertThat(location).hasPublicationWindow();
            } catch(AssertionError e) {
                Fail.fail("expecting location restrictions only");
            }
        }
        return myself;
    }

    public MediaAssert hasPortalRestriction() {
        isNotNull();
        try {
            assertThat(actual.getPortalRestrictions()).isNotEmpty();
        } catch(AssertionError e) {
            Fail.fail("expecting non-empty portal restrictions");
        }
        return myself;
    }

    public MediaAssert hasGeoRestriction() {
        isNotNull();
        try {
            assertThat(actual.getGeoRestrictions()).isNotEmpty();
        } catch(AssertionError e) {
            Fail.fail("expecting non-empty geo restrictions");
        }
        return myself;

    }

    public MediaAssert isRestricted() {
        try {
            MediaAssertions.mediaAssertThat(actual).hasPublicationWindow();
            return myself;
        } catch(AssertionError ignored) {
        }
        try {
            MediaAssertions.mediaAssertThat(actual).hasPortalRestriction();
            return myself;
        } catch(AssertionError ignored) {
        }
        try {
            MediaAssertions.mediaAssertThat(actual).hasGeoRestriction();
            return myself;
        } catch(AssertionError ignore) {
        }
        try {
            MediaAssertions.mediaAssertThat(actual).hasOnlyLocationsWithRestriction();
            return myself;
        } catch(AssertionError ignore) {
        }

        Fail.fail("expecting some restriction: publication window on media/locations, portal restriction or geo restriction");
        return myself;
    }


    public MediaAssert hasRelations() {
        isNotNull();
        try {
            assertThat(actual.getRelations()).isNotEmpty();

        } catch(AssertionError e) {
            Fail.fail("expecting nog-empty relations");
        }
        return myself;
    }

    public MediaAssert hasRelation(Relation relation) {
        hasRelations();

        Set<Quartet<String, String, String, String>> relations = new TreeSet<>();
        for(Relation existing : actual.getRelations()) {
            relations.add(new Quartet<>(
                existing.getDefinition().getType(),
                existing.getDefinition().getBroadcaster(),
                existing.getUriRef(),
                existing.getText()
            ));
        }

        Quartet<String, String, String, String> expected = new Quartet<>(
            relation.getDefinition().getType(),
            relation.getDefinition().getBroadcaster(),
            relation.getUriRef(),
            relation.getText()
        );
        try {
            assertThat(relations).contains(expected);

        } catch(AssertionError error) {

            Fail.fail(String.format("""
                missing relation
                    RELATIONS: %s
                    EXPECTED:  %s""", relations, expected));
        }
        return myself;
    }

    private <T extends MutableOwnable> void hasOwners(Collection<T> ownables, OwnerType... owners) {
        validateIsNotNull(owners);
        Set<OwnerType> set = new TreeSet<>();
        for(MutableOwnable ownable : ownables) {
            set.add(ownable.getOwner());
        }

        assertThat(set).contains(owners);

    }

    private <T extends MutableOwnable> void hasOnlyOwners(Collection<T> ownables, OwnerType... owners) {
        validateIsNotNull(owners);
        Set<OwnerType> set = new TreeSet<>();
        for(MutableOwnable ownable : ownables) {
            set.add(ownable.getOwner());
        }

        assertThat(set).containsOnly(owners);

    }

    @SafeVarargs
    private final <S extends Comparable<S>> void hasTypes(Collection<? extends Typable<S>> typables, S... types) {
        validateIsNotNull(types);
        Set<S> set = new TreeSet<>();
        for(Typable<S> typable : typables) {
            set.add(typable.getType());
        }

        assertThat(set).contains(types);

    }

    @SafeVarargs
    private final <S extends Comparable<S>> void hasOnlyTypes(Collection<? extends Typable<S>> typables, S... types) {
        validateIsNotNull(types);
        Set<S> set = new TreeSet<>();
        for(Typable<S> typable : typables) {
            set.add(typable.getType());
        }

        assertThat(set).containsOnly(types);
    }

    private void validateIsNotNull(Object[] objects) {
        if(objects == null) {
            throw new NullPointerException("The given arguments should not be null");
        }
    }

    private static class Quartet<P extends Comparable<P>, Q extends Comparable<Q>, R extends Comparable<R>, S extends Comparable<S>> implements Comparable<Quartet<P, Q, R, S>> {
        final P p;

        final Q q;

        final R r;

        final S s;

        private Quartet(P p, Q q, R r, S s) {
            this.p = p;
            this.q = q;
            this.r = r;
            this.s = s;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append('{').append(p);
            sb.append(',').append(q);
            sb.append(',').append(r);
            sb.append(',').append(s);
            sb.append('}');
            return sb.toString();
        }

        @Override
        public boolean equals(Object o) {
            if(this == o) {
                return true;
            }
            if(o == null || getClass() != o.getClass()) {
                return false;
            }

            Quartet quartet = (Quartet)o;

            if(p != null ? !p.equals(quartet.p) : quartet.p != null) {
                return false;
            }
            if(q != null ? !q.equals(quartet.q) : quartet.q != null) {
                return false;
            }
            if(r != null ? !r.equals(quartet.r) : quartet.r != null) {
                return false;
            }
            if(s != null ? !s.equals(quartet.s) : quartet.s != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = p != null ? p.hashCode() : 0;
            result = 31 * result + (q != null ? q.hashCode() : 0);
            result = 31 * result + (r != null ? r.hashCode() : 0);
            result = 31 * result + (s != null ? s.hashCode() : 0);
            return result;
        }

        @Override
        public int compareTo(@NonNull Quartet<P, Q, R, S> quartet) {
            int result = Objects.compare(p, quartet.p, Comparator.naturalOrder());
            if(result != 0) {
                return result;
            }
            result = Objects.compare(q, quartet.q, Comparator.naturalOrder());
            if(result != 0) {
                return result;
            }
            result = Objects.compare(r, quartet.r, Comparator.naturalOrder());
            if(result != 0) {
                return result;
            }
            return Objects.compare(s, quartet.s, Comparator.naturalOrder());
        }
    }
}
