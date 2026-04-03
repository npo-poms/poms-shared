package nl.vpro.domain.npo.mediastatus;

import jakarta.xml.bind.annotation.XmlRegistry;

import nl.vpro.domain.npo.streamstatus.StreamStatus;


@XmlRegistry
public class ObjectFactory {


    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link StreamStatus }
     *
     */
    public MediaStatus createMediaStatus() {
        return new MediaStatus();
    }


}
