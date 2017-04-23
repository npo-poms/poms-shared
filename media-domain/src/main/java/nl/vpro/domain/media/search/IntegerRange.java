/*
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.domain.media.search;

import lombok.*;

import javax.xml.bind.annotation.*;
import java.time.Instant;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "integerRangeType", propOrder = {
        "start",
        "stop"
        })
@ToString
@Data
@AllArgsConstructor
@Builder
public class IntegerRange implements Range<Long, IntegerRange.Value> {

    @XmlElement
    private Value start;

    @XmlElement
    private Value stop;

    public IntegerRange() {
    }
    
    

    @Data
    @EqualsAndHashCode(callSuper = true)
    public static class Value extends Range.RangeValue<Long> {
        
        @XmlValue
        Long value;
        
        public Value() {
            
        }
        @Builder
        public Value(Boolean inclusive, Long value) {
            super(inclusive);
            this.value = value;
        }

        public static Value of(Long instant) {
            return builder().value(instant).build();
        }
    }

}
