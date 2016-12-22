/*
 * Copyright (C) 2013 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.api;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Schedule;

/**
 * @author Roelof Jan Koekoek
 * @since 2.0
 */
@XmlEnum
@XmlType(name = "dateRangePresetTypeEnum")
public enum DateRangePreset implements RangeFacetItem<Date> {
    BEFORE_LAST_YEAR {
        @Override
        public String getName() {
            return "Voor vorig jaar";
        }

        @Override
        public Date getBegin() {
            return new Date(Long.MIN_VALUE);
        }

        @Override
        public Date getEnd() {
            return LAST_YEAR.getBegin();
        }
    },

    LAST_YEAR {
        @Override
        public String getName() {
            return "Vorig jaar";
        }

        @Override
        public Date getBegin() {
            return Date.from(today().minusYears(1).toInstant());
        }
    },

    LAST_MONTH {
        @Override
        public String getName() {
            return "Vorige maand";
        }

        @Override
        public Date getBegin() {
            return Date.from(today().minusMonths(1).toInstant());
        }
    },

    LAST_WEEK {
        @Override
        public String getName() {
            return "Vorige week";
        }

        @Override
        public Date getBegin() {
            return Date.from(today().minusWeeks(1).toInstant());
        }
    },

    YESTERDAY {
        @Override
        public String getName() {
            return "Gisteren";
        }

        @Override
        public Date getBegin() {
            return Date.from(today().minusDays(1).toInstant());
        }
    },

    TODAY {
        @Override
        public String getName() {
            return "Vandaag";
        }

        @Override
        public Date getEnd() {
            return Date.from(today().plusDays(1).toInstant());
        }
    },

    THIS_WEEK {
        @Override
        public String getName() {
            return "Deze week";
        }

        @Override
        public Date getEnd() {
            return Date.from(today().plusWeeks(1).toInstant());
        }
    },

    TOMORROW {
        @Override
        public String getName() {
            return "Morgen";
        }

        @Override
        public Date getBegin() {
            return Date.from(today().plusDays(1).toInstant());
        }

        @Override
        public Date getEnd() {
            return Date.from(today().plusDays(2).toInstant());
        }

    };

    @Override
    public Date getBegin() {
        return Date.from(today().toInstant());
    }

    @Override
    public Date getEnd() {
        return Date.from(today().toInstant());
    }

    ZonedDateTime today() {
        return ZonedDateTime.now(Schedule.ZONE_ID).truncatedTo(ChronoUnit.DAYS);
    }

    @Override
    public boolean matches(Date begin, Date end) {
        return getBegin().equals(begin)
            &&
            getEnd().equals(end);
    }

    public DateRangeFacetItem asDateRangeFacetItem() {
        return new DateRangeFacetItem(getName(), getBegin(), getEnd());
    }


}
