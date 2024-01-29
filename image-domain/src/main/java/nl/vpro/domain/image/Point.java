package nl.vpro.domain.image;

import lombok.Data;

import java.io.Serial;

import jakarta.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents the location of one pixel in an image.
 *
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Data
public class Point implements java.io.Serializable {

    @Serial
    private static final long serialVersionUID = 0L;

    private final int x;
    private final int y;


    public static Point of(@Positive  int x, @Positive int y) {
        return new Point(x, y);
    }

    @JsonCreator
    public Point(@JsonProperty("x") int x, @JsonProperty("y") int y) {
        this.x = x;
        this.y = y;
    }

    public Point times(float multiplier) {
        return new Point(Math.round(multiplier * x), Math.round(multiplier * y));
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
