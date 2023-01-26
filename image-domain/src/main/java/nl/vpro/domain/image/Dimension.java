package nl.vpro.domain.image;

import lombok.Data;

import javax.validation.constraints.Positive;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Comparator.*;

/**
 * @author Michiel Meeuwissen
 * @since 3.64
 */
@Data
public class Dimension implements java.io.Serializable, Comparable<Dimension> {

    private static final long serialVersionUID = 0L;

    private final Long width;
    private final Long height;


    public static Dimension of(@Positive  Integer width, @Positive Integer height) {
        return new Dimension(width == null ? null : width.longValue(), height == null ? null : height.longValue());
    }


    public static Dimension of(@Positive  Long width, @Positive Long height) {
        return new Dimension(width, height);
    }

    @JsonCreator
    public Dimension(@JsonProperty("width") Long width, @JsonProperty("height") Long height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the smallest possible dimension with the same aspect ratio.
     */
    public Dimension reduce() {
        long w = width;
        long h = height;
        long gcd = gcd(w, h);

        while (gcd != 1) {
            w /= gcd;
            h /= gcd;
            gcd = gcd(w, h);
        }
        return new Dimension(w,h);
    }

    @Override
    public int compareTo(Dimension o) {
        return comparing(Dimension::getWidth, nullsLast(naturalOrder()))
            .thenComparing(Dimension::getHeight, nullsLast(naturalOrder()))
            .compare(this, o);
    }

    protected long gcd(long a, long b) {
        if (b == 0) {
            return a;
        }
        return gcd(b,a % b);
    }

    @Override
    public String toString() {
        return width + "x" + height;
    }
}
