/*
 * Copyright (C) 2013 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;
import nl.vpro.i18n.Displayable;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "dateRangePresetTypeEnum")
public enum DateRangePreset implements RangeFacetItem<Instant>, Displayable {
    BEFORE_LAST_YEAR {
        @Override
        public String getDisplayName() {
            return "Voor vorig jaar";
        }

        @Override
        public Instant getBegin() {
            return Instant.ofEpochMilli(Long.MIN_VALUE);
        }

        @Override
        public Instant getEnd() {
            return LAST_YEAR.getBegin();
        }
    },

    LAST_YEAR {
        @Override
        public String getDisplayName() {
            return "Vorig jaar";
        }

        @Override
        public Instant getBegin() {
            return today().minusYears(1).toInstant();
        }
    },

    LAST_MONTH {
        @Override
        public String getDisplayName() {
            return "Vorige maand";
        }

        @Override
        public Instant getBegin() {
            return today().minusMonths(1).toInstant();
        }
    },

    LAST_WEEK {
        @Override
        public String getDisplayName() {
            return "Vorige week";
        }

        @Override
        public Instant getBegin() {
            return today().minusWeeks(1).toInstant();
        }
    },

    YESTERDAY {
        @Override
        public String getDisplayName() {
            return "Gisteren";
        }

        @Override
        public Instant getBegin() {
            return today().minusDays(1).toInstant();
        }
    },

    TODAY {
        @Override
        public String getDisplayName() {
            return "Vandaag";
        }

        @Override
        public Instant getEnd() {
            return today().plusDays(1).toInstant();
        }
    },

    THIS_WEEK {
        @Override
        public String getDisplayName() {
            return "Deze week";
        }

        @Override
        public Instant getEnd() {
            return today().plusWeeks(1).toInstant();
        }
    },

    TOMORROW {
        @Override
        public String getDisplayName() {
            return "Morgen";
        }

        @Override
        public Instant getBegin() {
            return today().plusDays(1).toInstant();
        }

        @Override
        public Instant getEnd() {
            return today().plusDays(2).toInstant();
        }

    };

    @Override
    public Instant getBegin() {
        return today().toInstant();
    }

    @Override
    public Instant getEnd() {
        return today().toInstant();
    }

    ZonedDateTime today() {
        return ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS);
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean matches(Instant begin, Instant end) {
        return getBegin().equals(begin)
            &&
            getEnd().equals(end);
    }

    public DateRangeFacetItem asDateRangeFacetItem() {
        return new DateRangeFacetItem(getName(), getBegin(), getEnd());
    }


}
