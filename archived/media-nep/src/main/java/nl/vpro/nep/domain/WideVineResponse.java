package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class WideVineResponse {
    private boolean success;
    private String token;
    private String message;
    private List<Error> error;


    @Data
    public static class Error {
        int code;
        int status;
        String title;
    }
}
