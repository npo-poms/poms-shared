package nl.vpro.letterbox;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;

import nl.vpro.jackson2.Jackson2Mapper;

/**
 * @author Michiel Meeuwissen
 * @since 5.8
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@lombok.Builder
@EqualsAndHashCode
@Slf4j
public class LetterBoxRegistration {
    private String key;

    private String endPointId;

    private String principalId;

    private String description;

    private String implementingClass;

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
