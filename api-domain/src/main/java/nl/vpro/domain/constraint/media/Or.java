package nl.vpro.domain.constraint.media;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElements;

import nl.vpro.domain.constraint.AbstractOr;
import nl.vpro.domain.constraint.Constraint;
import nl.vpro.domain.media.MediaObject;

/**
 * @author Michiel Meeuwissen
 * @since 2.0
 */
public class Or extends AbstractOr<MediaObject> {

    protected Or() {
    }

    @SafeVarargs
    public Or(Constraint<MediaObject>... constraints) {
        super(constraints);
    }

    public Or(List<Constraint<MediaObject>> constraints) {
        super(constraints);
    }

    @Override
    @XmlElements({
        @XmlElement(name = "and", type = And.class),
        @XmlElement(name = "or", type = Or.class),
        @XmlElement(name = "not", type = Not.class),
        @XmlElement(name = "avType", type = AvTypeConstraint.class),
        @XmlElement(name = "avFileFormat", type = AvFileFormatConstraint.class),
        @XmlElement(name = "avFileExtension", type = AVFileExtensionConstraint.class),
        @XmlElement(name = "programUrl", type = ProgramUrlConstraint.class),
        @XmlElement(name = "descendantOf", type = DescendantOfConstraint.class),
        @XmlElement(name = "broadcaster", type = BroadcasterConstraint.class),
        @XmlElement(name = "hasImage", type = HasImageConstraint.class),
        @XmlElement(name = "hasLocation", type = HasLocationConstraint.class),
        @XmlElement(name = "hasPrediction", type = HasPredictionConstraint.class),
        @XmlElement(name = "type", type = MediaTypeConstraint.class),
        @XmlElement(name = "channel", type = ChannelConstraint.class),
        @XmlElement(name = "scheduleEvent", type = ScheduleEventDateConstraint.class),
        //@XmlElement(name = "sortDate", type = SortDateConstraint.class),
        @XmlElement(name = "hasPortal", type = HasPortalConstraint.class),
        @XmlElement(name = "portal", type = PortalConstraint.class),
        @XmlElement(name = "isExclusive", type = HasPortalRestrictionConstraint.class),
        @XmlElement(name = "exclusive", type = PortalRestrictionConstraint.class),
        @XmlElement(name = "hasGeoRestriction", type = HasGeoRestrictionConstraint.class),
        @XmlElement(name = "geoRestriction", type = GeoRestrictionConstraint.class),
        @XmlElement(name = "ageRating", type = AgeRatingConstraint.class),
        @XmlElement(name = "hasAgeRating", type = HasAgeRatingConstraint.class),
        @XmlElement(name = "contentRating", type = ContentRatingConstraint.class),
        @XmlElement(name = "hasContentRating", type = HasContentRatingConstraint.class),
        @XmlElement(name = "genre", type = GenreConstraint.class),
    })
    public List<Constraint<MediaObject>> getConstraints() {
        return constraints;
    }
}
