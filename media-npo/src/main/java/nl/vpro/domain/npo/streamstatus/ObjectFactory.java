package nl.vpro.domain.npo.streamstatus;

import jakarta.xml.bind.annotation.XmlRegistry;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StreamStatus }
     *
     */
    public StreamStatus createStreamStatus() {
        return new StreamStatus();
    }


}
