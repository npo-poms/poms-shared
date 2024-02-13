package nl.vpro.domain.npo.streamstatus;

import lombok.Data;

import java.time.Instant;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import nl.vpro.xml.bind.InstantXmlAdapter;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Tijdsbeperking {
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    Instant starttijd;
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    Instant eindtijd;
}
