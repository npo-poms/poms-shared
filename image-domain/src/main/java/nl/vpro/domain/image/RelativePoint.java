package nl.vpro.domain.image;

import lombok.Data;

import javax.validation.constraints.*;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a relative position in an image. Basically two floats ranging from {@code 0} to {@code 100}.
 *
 * @author Michiel Meeuwissen
 * @since 5.34
 */
@Data
public class RelativePoint implements java.io.Serializable {

    public static final RelativePoint MIDDLE = RelativePoint.of(50, 50);

    private static final long serialVersionUID = 0L;

    private final float x;
    private final float y;


    public static RelativePoint of(@Min(0) @Max(100) float x, @Min(0) @Max(100)  int y) {
        return new RelativePoint(x, y);
    }

    @JsonCreator
    public RelativePoint(@JsonProperty("x") float x, @JsonProperty("y") float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "" + Math.round(x) + "% " + Math.round(y) + "%";
    }
}
