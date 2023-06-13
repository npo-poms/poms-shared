package nl.vpro.domain.image;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Representation of a square area in an image.
 * Defined by a {@link #getLowerLeft()} and {@link #getUpperRight()} {@link Point}
 *
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Data
@EqualsAndHashCode
@ToString
public class Area implements Serializable {

    @Serial
    private static final long serialVersionUID = 936296968752859966L;

    final Point lowerLeft;
    final Point upperRight;

    @JsonCreator
    public Area(@JsonProperty("lowerLeft") Point lowerLeft, @JsonProperty("upperRight") Point upperRight) {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }

    public Area(int x1, int y1, int x2, int y2) {
        this(Point.of(x1, y1), Point.of(x2, y2));
    }

    public Area times(float multiplier) {
        return new Area(lowerLeft.times(multiplier), upperRight.times(multiplier));
    }

    /**
     *
     */
    public static RelativePoint relativeCenter(Area area, Dimension dimension) {
        if (area != null && dimension != null && dimension.getWidth() != null && dimension.getHeight() != null) {
            return new RelativePoint(
                0.5f * (area.getLowerLeft().getX() + area.getUpperRight().getX()) / dimension.getWidth(),
                0.5f * (area.getLowerLeft().getY() + area.getUpperRight().getY()) / dimension.getHeight()
            );
        } else {
            return RelativePoint.MIDDLE;
        }
    }
}
