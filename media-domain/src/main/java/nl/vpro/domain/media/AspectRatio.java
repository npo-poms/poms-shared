package nl.vpro.domain.media;

import javax.xml.bind.annotation.XmlEnumValue;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.XmlValued;
import nl.vpro.domain.media.bind.AspectRatioToString;
import nl.vpro.i18n.Displayable;

import static org.meeuw.math.IntegerUtils.gcd;

@JsonSerialize(using = AspectRatioToString.Serializer.class)
@JsonDeserialize(using = AspectRatioToString.Deserializer.class)
public enum AspectRatio implements XmlValued, Displayable {

    @XmlEnumValue("4:3")
    _4x3(4, 3),

    @XmlEnumValue("16:9")
    _16x9(16, 9),

    @XmlEnumValue("xCIF")
    _xCIF(352, 288) { // == 11x9
        @Override
        public String toString() {
            return "xCIF";
        }
        @Override
        public boolean display() {
            return false;
        }
    },

    /**
     * @since 7.10
     */
    @XmlEnumValue("9:16")
    _9x16(9, 16);


    private final int w;
    private final int h;

    AspectRatio(int w, int h) {
        int gcd = gcd(w, h);
        this.w = w / gcd;
        this.h = h / gcd;
    }


    public static @Nullable AspectRatio fromDimension(@Nullable Integer w, @Nullable Integer h) {
        if (w == null || h == null) {
            return null;
        }
        int gcd = gcd(w, h);
        if (gcd == 0) return null;
        int aw = w / gcd;
        int ah = h / gcd;
        for (AspectRatio a : AspectRatio.values()) {
            if (aw == a.w && ah == a.h) {
                return a;
            }
        }
        return null;
    }

    /**
     * @since 1.8
     */
    public static AspectRatio fromString(String s) {
        if (s == null || s.isEmpty()) return null;
        String[] split = s.split("\\s*[^\\d]\\s*", 2);
        return fromDimension(
            Integer.parseInt(split[0].trim()),
            Integer.parseInt(split[1].trim()));
    }

    @Override
    public String toString() {
        return w + ":" + h;
    }

    @Override
    public String getDisplayName() {
        return toString();
    }

}
