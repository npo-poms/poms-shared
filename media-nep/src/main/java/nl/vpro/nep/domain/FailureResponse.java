package nl.vpro.nep.domain;

import lombok.*;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class FailureResponse {

    private boolean success;

    private String status;

    private String errors;

}
