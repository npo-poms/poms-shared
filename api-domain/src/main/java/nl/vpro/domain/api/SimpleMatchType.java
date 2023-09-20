/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author Michiel Meeuwissen
 * @since 4.6
 */
public enum SimpleMatchType implements MatchType {
    TEXT {
        @Override
        public boolean eval(String value, String input, boolean ignoreCase) {
            return Matchers.TEXT.eval(value, input, ignoreCase);
        }

        @Override
        public Validation valid(String value) {
            return Matchers.TEXT.valid(value);
        }
    };
    @Override
    public String getName() {
        return name();
    }

}
