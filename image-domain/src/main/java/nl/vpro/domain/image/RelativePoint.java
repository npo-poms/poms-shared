package nl.vpro.domain.image;

import lombok.Data;

import java.io.Serial;

import jakarta.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a relative position in an image. Basically two floats ranging from {@code 0} to {@code 1}.
 *
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Data
public class RelativePoint implements java.io.Serializable {

    public static final RelativePoint MIDDLE = RelativePoint.of(
        0.5f, 0.5f);

    @Serial
    private static final long serialVersionUID = 0L;

    private final float x;
    private final float y;


    public static RelativePoint of(
        @Min(0) @Max(1) float x,
        @Min(0) @Max(1)  float y
    ) {
        return new RelativePoint(x, y);
    }

    @JsonCreator
    public RelativePoint(
         @Min(0) @Max(1) @JsonProperty("x") float x,
         @Min(0) @Max(1) @JsonProperty("y") float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + Math.round(100 * x) + "% " + Math.round(100 * y) + "%";
    }
}
