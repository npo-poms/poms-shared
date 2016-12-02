package nl.vpro.domain.media.exceptions;

public class DuplicateScheduleEventException extends RuntimeException {
    private static final long serialVersionUID = -6223584920154956501L;
    
    public DuplicateScheduleEventException() {
        super();
    }

    public DuplicateScheduleEventException(final String message) {
        super(message);
    }
}
