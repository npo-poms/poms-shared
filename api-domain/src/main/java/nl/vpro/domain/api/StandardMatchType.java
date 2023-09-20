/*
 * Copyright (C) 2016 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

/**
 * @author rico
 * @since 4.6
 */
public enum StandardMatchType implements MatchType {
    TEXT {
        @Override
        public boolean eval(String value, String input, boolean ignoreCase) {
            return Matchers.TEXT.eval(value, input, ignoreCase);
        }

        @Override
        public Validation valid(String value) {
            return Matchers.TEXT.valid(value);
        }
    },
    REGEX {
        @Override
        public boolean eval(String value, String input, boolean ignoreCase) {
            return Matchers.REGEX.eval(value, input, ignoreCase);
        }

        @Override
        public Validation valid(String value) {
            return Matchers.REGEX.valid(value);
        }
    },
    WILDCARD {
        @Override
        public boolean eval(String value, String input, boolean ignoreCase) {
            return Matchers.WILDCARD.eval(value, input, ignoreCase);
        }

        @Override
        public Validation valid(String value) {
            return Matchers.WILDCARD.valid(value);
        }
    };

    @Override
    public String getName() {
        return name();
    }


}
