package nl.vpro.nep.domain;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author Michiel Meeuwissen
 * @since 5.25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.Builder
public class FailureResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = -8133232400817291872L;

    private boolean success;

    private String status;

    private String errors;

}
