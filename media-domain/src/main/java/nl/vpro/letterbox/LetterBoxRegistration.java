package nl.vpro.letterbox;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * One entry in {@link LetterBoxRegistration}
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@lombok.Builder
@EqualsAndHashCode
@Slf4j
public class LetterBoxRegistration {
    private final String key;

    private final String endPointId;

    private final String principalId;

    private final String description;

    private final String implementingClass;

    @Override
    public String toString() {
        try {
            return Jackson2Mapper.getInstance().writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }
}
