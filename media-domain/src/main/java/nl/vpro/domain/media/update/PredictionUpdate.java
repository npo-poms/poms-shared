package nl.vpro.domain.media.update;

import lombok.Data;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.media.*;
import nl.vpro.jackson2.StringInstantToJsonTimestamp;
import nl.vpro.xml.bind.InstantXmlAdapter;

/**
 * The update representation of a planned prediction.
 *
 * @author Michiel Meeuwissen
 * @since 5.6
 * @see nl.vpro.domain.media.update
 * @see nl.vpro.domain.media.Prediction
 */

@Data
@XmlAccessorType(XmlAccessType.NONE)
@XmlType(name = "predictionUpdateType")
@XmlRootElement(name = "prediction")
@lombok.Builder
@lombok.AllArgsConstructor
public class PredictionUpdate implements Comparable<PredictionUpdate>, MutableEmbargo<PredictionUpdate> {

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

    @XmlAttribute
    protected Encryption encryption;

    public PredictionUpdate() {
    }

    /**
     * A very common case is to provide internetvod.
     * @since 7.10
     */
    public static PredictionUpdate internetvod() {
        return PredictionUpdate.builder()
            .platform(Platform.INTERNETVOD)
            .build();

    }

    public static PredictionUpdate of(Prediction prediction) {
        return PredictionUpdate.builderOf(prediction).build();
    }

    public static PredictionUpdate.Builder builderOf(Prediction prediction) {
        assert prediction.isPlannedAvailability() : "The representation of an unplanned availability is simply the _absence_ of it in the list of prediction of a mediaobject. Following is not planned: "+ prediction;
        return PredictionUpdate.builder()
               .platform(prediction.getPlatform())
               .publishStart(prediction.getOwnPublishStartInstant())
               .publishStop(prediction.getOwnPublishStopInstant())
               .encryption(prediction.getEncryption());
    }

    /**
     * Copies the values of this prediction update to the given prediction (and returns it)
     * <p>
     * This implies that {@link Prediction#setPlannedAvailability(boolean)} is set to true. because {@link PredictionUpdate} only represents planned availabilities.
     * @return the given prediction (after changes were applied)
     */
    public Prediction toPrediction(Prediction prediction) {
        prediction.setPlatform(platform);
        prediction.setPublishStartInstant(publishStart);
        prediction.setPublishStopInstant(publishStop);
        prediction.setEncryption(encryption);
        prediction.setPlannedAvailability(true);
        return prediction;
    }

    public Prediction toPrediction() {
        return toPrediction(new Prediction());
    }

    @Override
    public int compareTo(PredictionUpdate o) {
        return platform.compareTo(o.getPlatform());
    }

    @Override
    public Instant getPublishStartInstant() {
        return publishStart;
    }

    @Override
    public Instant getPublishStopInstant() {
        return publishStop;
    }

    @NonNull
    @Override
    public PredictionUpdate setPublishStartInstant(@Nullable Instant publishStart) {
        setPublishStart(publishStart);
        return this;
    }

    @NonNull
    @Override
    public PredictionUpdate setPublishStopInstant(@Nullable Instant publishStop) {
        setPublishStop(publishStop);
        return this;
    }
}
