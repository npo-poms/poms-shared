package nl.vpro.domain.media.update;

import lombok.Data;

import java.time.Instant;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.media.Platform;
import nl.vpro.domain.media.Prediction;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * @author Michiel Meeuwissen
 * @since 5.6
 */

@Data
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "predictionUpdateType")
@XmlRootElement(name = "prediction")
@lombok.Builder
@lombok.AllArgsConstructor
public class PredictionUpdate implements Comparable<PredictionUpdate> {

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStart;

    @XmlAttribute
    @XmlJavaTypeAdapter(InstantXmlAdapter.class)
    @XmlSchemaType(name = "dateTime")
    @JsonDeserialize(using = StringInstantToJsonTimestamp.Deserializer.class)
    @JsonSerialize(using = StringInstantToJsonTimestamp.Serializer.class)
    protected Instant publishStop;

    @NotNull
    @XmlValue
    @JsonProperty("platform")
    protected Platform platform;

    public PredictionUpdate() {

    }

    public static PredictionUpdate of(Prediction prediction) {
        return PredictionUpdate.builder()
            .platform(prediction.getPlatform())
            .publishStart(prediction.getPublishStartInstant())
            .publishStop(prediction.getPublishStopInstant())

            .build();
    }

    public Prediction toPrediction() {
        return new Prediction(platform, publishStart, publishStop);
    }

    @Override
    public int compareTo(PredictionUpdate o) {
        return platform.compareTo(o.getPlatform());

    }
}
