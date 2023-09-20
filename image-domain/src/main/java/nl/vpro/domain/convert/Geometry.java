/*
 * Copyright (C) 2011 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.convert;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.image.Dimension;

public class Geometry {

    private static final Pattern GEOMETRY = Pattern.compile("^(\\d*)?(?:x(\\d*))?(?<=\\d{1})([\\^<>!]*)$"); // (\+\d{1,3}\+\d{1,3})?)

    private boolean matches;

    private Dimension dimension;

    private Set<Modifier> modifier;

    private Geometry() {
    }

    public static Geometry compile(String input) {
        return compile(input, -1);
    }

    public static Geometry compile(String input, int maxSize) {
        Geometry geometry = new Geometry();

        Matcher matcher = GEOMETRY.matcher(input);

        if(!matcher.find()) {
            return geometry;
        }

        geometry.matches = true;

        try {
            geometry.dimension =
                Dimension.of(
                    StringUtils.isNotEmpty(matcher.group(1)) ? Integer.valueOf(matcher.group(1)) : null,            (StringUtils.isNotEmpty(matcher.group(2))) ? Integer.valueOf(matcher.group(2)) : null);
        } catch(NumberFormatException e) {
            noMatch(geometry);
            return geometry;
        }

        String postFix = matcher.group(3);
        if (postFix != null) {
            geometry.modifier = Modifier.getByString(postFix);
        }

        Long width = geometry.dimension.getWidth();
        Long height = geometry.dimension.getHeight();
        if (maxSize > 0 && (width != null && width > maxSize || height != null && height > maxSize)) {
            if (geometry.modifier.isEmpty()) {
                noMatch(geometry);
                return geometry;
            } else {
                geometry.rescale(maxSize, maxSize);
            }

        }

        return geometry;
    }

    public void rescale(int maxx, int maxy) {
        Float factorx = null;
        Float factory = null;
        Long width = dimension.getWidth();
        Long height = dimension.getHeight();
        if (width != null && width > maxx) {
            factorx  = ((float) maxx) / width;
        }
        if (height != null && height > maxy) {
            factory = ((float) maxy) / height;
        }
        Float factor = null;
        if (factorx != null) {
            factor = factorx;
        }
        if (factory != null) {
            if (factor == null || factory < factor) {
                factor = factory;
            }
        }
        if (factor != null) {
            if (width != null) {
                width = (long) Math.floor(factor * width);
            }
            if (height != null) {
                height = (long) Math.floor(factor * height);
            }
            dimension = new Dimension(width, height);
        }
    }

    public boolean matches() {
        return matches;
    }

    public int width() {
        return dimension.getWidth().intValue();
    }

    public int height() {
        return dimension.getHeight().intValue();
    }

    public String size() {
        StringBuilder sb = new StringBuilder();
        if (dimension.getWidth() != null) {
            sb.append(dimension.getWidth());
        }
        if (dimension.getHeight() != null) {
            sb.append("x");
            sb.append(dimension.getHeight());
        }

        return sb.toString();
    }

    public Set<Modifier> getModifier() {
        return modifier;
    }

    public String offset() {
        throw new UnsupportedOperationException("Not implemented yet: geometry offset");
    }

    private static void noMatch(Geometry geometry) {
        geometry.matches = false;
        geometry.dimension = null;
    }

    public enum Modifier {
        FIT_MINIMAL('^'),
        ONLY_WHEN_BIGGER('>'),
        ONLY_WHEN_SMALLER('<'),
        NO_PRESERVE_ASPECTRATIO('!')
        ;

        private final char postFix;

        Modifier(char postFix) {
            this.postFix = postFix;
        }

        public char getPostFix() {
            return postFix;
        }

        public static Set<Modifier> getByString(String s) {
            Set<Modifier> result = new HashSet<Modifier>();
            for (char c : s.toCharArray()) {
                for (Modifier modifier : values()) {
                    if (modifier.postFix == c) {
                        result.add(modifier);
                    }
                }
            }
            return result;
        }
    }

}
