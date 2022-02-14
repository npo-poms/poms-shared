package nl.vpro.letterbox;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * One entry in {@link LetterBoxRegistration}
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Getter
@Setter
@EqualsAndHashCode
@Slf4j
public class LetterBoxRegistration {
    private final String key;

    private final String endPointId;

    private final String principalId;

    private final String description;

    private final String implementingClass;

    private final Duration asyncAfter;

    @With
    private final boolean acceptAll;

    private String errors;


    @lombok.Builder
    @JsonCreator()
    private LetterBoxRegistration(
        @JsonProperty("key") String key,
        @JsonProperty("endPointId") String endPointId,
        @JsonProperty("principalId") String principalId,
        @JsonProperty("description") String description,
        @JsonProperty("implementingClass") String implementingClass,
        @JsonProperty("asyncAfter") Duration asyncAfter,
        @JsonProperty("acceptAll") boolean acceptAll,
        @JsonProperty("errors") String errors
    ) {
        this.key = key;
        this.endPointId = endPointId;
        this.principalId = principalId;
        this.description = description;
        this.implementingClass = implementingClass;
        this.asyncAfter = asyncAfter == null ? Duration.ofSeconds(20) : asyncAfter;
        this.acceptAll = acceptAll;
        this.errors = errors;
    }

    @Override
    public String toString() {
        try {
            return Jackson2Mapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }


    public static class Builder {
        public Builder async() {
            return asyncAfter(Duration.ZERO);
        }
    }
}
