package nl.vpro.nep.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @since 5.5
 */
@Data
@NoArgsConstructor
public class PlayreadyResponse {
    private boolean success;
    private String token;

    private List<WideVineResponse.Error> error;


    @Data
    public static class Error {
        int code;
        int status;
        String title;
    }
}
