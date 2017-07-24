package nl.vpro.domain.image;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.support.License;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Metadata<T extends Metadata<T>>  extends Embargo<T> {

    String getUrn();

    ImageType getType();

    String getTitle();

    String getDescription();

    License getLicense();

    String getSource();

    String getSourceName();

    String getCredits();

    String getDate();

    Integer getHeight();

    Integer getWidth();


    void setType(ImageType type);
    void setTitle(String title);
    void setDescription(String description);
    void setSource(String source);
    void setSourceName(String sourceName);
    void setLicense(License license);
    void setHeight(Integer height);

    void setWidth(Integer width);

    void setDate(String date);

    default void setDate(Instant instant) {
        if (instant == null) {
            setDate((String) null);
            return;
        }
        setDate(String.valueOf(instant.atZone(TimeUtils.ZONE_ID).toLocalDate().getYear()));

    }

    default void copyFrom(Metadata<?> image){
        setType(image.getType());
        setTitle(image.getTitle());
        setDescription(image.getDescription());
        setHeight(image.getHeight());
        setWidth(image.getWidth());
        setLicense(image.getLicense());
        setSource(image.getSource());
        setSourceName(image.getSourceName());

    }


    default void copyFromIfUnset(Metadata<?> metadata) {

        if (getType() == null) {
            setType(metadata.getType());
        }
        if (StringUtils.isEmpty(getTitle())) {
            setTitle(metadata.getTitle());
        }
        if (StringUtils.isEmpty(getDescription())) {
            setDescription(metadata.getDescription());
        }
        if (getHeight() == null || getHeight() < 0) {
            setHeight(metadata.getHeight());
        }
        if (getWidth() == null || getWidth() < 0) {
            setWidth(metadata.getWidth());
        }
        if (StringUtils.isEmpty(getSource())) {
            setSource(metadata.getSource());
        }
        if (StringUtils.isEmpty(getSourceName())) {
            setSourceName(metadata.getSourceName());
        }
        if (getLicense() == null) {
            setLicense(metadata.getLicense());
        }
    }

}
