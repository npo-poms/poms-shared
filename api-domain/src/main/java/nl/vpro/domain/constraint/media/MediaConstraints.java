/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.constraint.media;

import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.constraint.Constraints;
import nl.vpro.domain.media.*;
import nl.vpro.domain.user.Broadcaster;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
public class MediaConstraints {

    @SafeVarargs
    public static And and(Constraint<MediaObject>... constraints) {
        return new And(constraints);
    }

    @SafeVarargs
    public static Or or(Constraint<MediaObject>... constraints) {
        return new Or(constraints);
    }

    public static Not not(Constraint<MediaObject> constraint) {
        return new Not(constraint);
    }

    public static Constraint<MediaObject> alwaysTrue() {
        return Constraints.alwaysTrue();
    }

    public static Constraint<MediaObject> alwaysFalse() {
        return Constraints.alwaysFalse();
    }
    public static AvFileFormatConstraint avFileFormat(AVFileFormat format) {
        return new AvFileFormatConstraint(format.name());
    }

    public static AvTypeConstraint avType(AVType type) {
        return new AvTypeConstraint(type);
    }

    public static BroadcasterConstraint broadcaster(Broadcaster broadcaster) {
        return broadcaster(broadcaster.getDisplayName());
    }

    public static BroadcasterConstraint broadcaster(String broadcaster) {
        return new BroadcasterConstraint(broadcaster);
    }

    public static DescendantOfConstraint descendantOf(DescendantRef ref) {
        return descendantOf(ref.getMidRef());
    }

    public static DescendantOfConstraint descendantOf(String ref) {
        return new DescendantOfConstraint(ref);
    }

    public static HasImageConstraint hasImage() {
        return new HasImageConstraint();
    }

    public static MediaTypeConstraint mediaType(MediaType type) {
        return new MediaTypeConstraint(type);
    }

    public static AVFileExtensionConstraint extension(String extension) {
        return new AVFileExtensionConstraint(extension);
    }

    public static HasLocationConstraint hasLocation(String platform) {
        HasLocationConstraint hasLocationConstraint = new HasLocationConstraint();
        hasLocationConstraint.setPlatform(platform);
        return hasLocationConstraint;
    }

    public static AgeRatingConstraint ageRating(AgeRating ageRating) {
        return new AgeRatingConstraint(ageRating);
    }

    public static ContentRatingConstraint contentRating(ContentRating contentRating) {
        return new ContentRatingConstraint(contentRating);
    }


}
