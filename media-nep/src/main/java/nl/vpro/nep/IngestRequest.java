package nl.vpro.nep;

import lombok.Data;

import java.util.List;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */
@Data
public class IngestRequest {

    Encryption encryption;
    String filename;
    String mid;
    List<String> platforms;
    Priority priority;
    Type type;
    Integer version;



    public enum Encryption {
        NONE, DRM;
    }
    public enum Priority {
        LOW, NORMAL, HIGH, URGENT
    }
    public enum Type {
        VIDEO /* Audio */
    }
}
