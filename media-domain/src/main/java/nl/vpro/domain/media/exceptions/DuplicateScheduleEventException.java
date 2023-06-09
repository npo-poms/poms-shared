package nl.vpro.domain.media.exceptions;

import java.io.Serial;

public class DuplicateScheduleEventException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = -6223584920154956501L;

    public DuplicateScheduleEventException() {
        super();
    }

    public DuplicateScheduleEventException(final String message) {
        super(message);
    }
}
