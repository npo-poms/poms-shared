/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "dateRangePresetTypeEnum")
public enum DateRangePreset implements RangeFacetItem<Instant> {
    BEFORE_LAST_YEAR {
        @Override
        public String getName() {
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
        public String getName() {
            return "Vorig jaar";
        }

        @Override
        public Instant getBegin() {
            return today().minusYears(1).toInstant();
        }
    },

    LAST_MONTH {
        @Override
        public String getName() {
            return "Vorige maand";
        }

        @Override
        public Instant getBegin() {
            return today().minusMonths(1).toInstant();
        }
    },

    LAST_WEEK {
        @Override
        public String getName() {
            return "Vorige week";
        }

        @Override
        public Instant getBegin() {
            return today().minusWeeks(1).toInstant();
        }
    },

    YESTERDAY {
        @Override
        public String getName() {
            return "Gisteren";
        }

        @Override
        public Instant getBegin() {
            return today().minusDays(1).toInstant();
        }
    },

    TODAY {
        @Override
        public String getName() {
            return "Vandaag";
        }

        @Override
        public Instant getEnd() {
            return today().plusDays(1).toInstant();
        }
    },

    THIS_WEEK {
        @Override
        public String getName() {
            return "Deze week";
        }

        @Override
        public Instant getEnd() {
            return today().plusWeeks(1).toInstant();
        }
    },

    TOMORROW {
        @Override
        public String getName() {
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
    public boolean matches(Instant begin, Instant end) {
        return getBegin().equals(begin)
            &&
            getEnd().equals(end);
    }

    public DateRangeFacetItem asDateRangeFacetItem() {
        return new DateRangeFacetItem(getName(), getBegin(), getEnd());
    }


}
