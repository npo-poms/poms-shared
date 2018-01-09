package nl.vpro.domain.image;

import java.time.Instant;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Embargo;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.support.License;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Metadata<T extends Metadata<T>>  extends Embargo<T> {

    String getImageUri();

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

    void setCredits(String credits);
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
        setCredits(image.getCredits());
        setDate(image.getDate());
        Embargos.copy(image, this);
    }


    default void copyFromIfTargetUnset(Metadata<?> source) {

        if (getType() == null) {
            setType(source.getType());
        }
        if (StringUtils.isEmpty(getTitle())) {
            setTitle(source.getTitle());
        }
        if (StringUtils.isEmpty(getDescription())) {
            setDescription(source.getDescription());
        }
        if (getHeight() == null || getHeight() < 0) {
            setHeight(source.getHeight());
        }
        if (getWidth() == null || getWidth() < 0) {
            setWidth(source.getWidth());
        }
        if (StringUtils.isEmpty(getSource())) {
            setSource(source.getSource());
        }
        if (StringUtils.isEmpty(getSourceName())) {
            setSourceName(source.getSourceName());
        }
        if (getLicense() == null) {
            setLicense(source.getLicense());
        }
        if (StringUtils.isEmpty(getCredits())) {
            setCredits(source.getCredits());
        }

        if (StringUtils.isEmpty(getDate())) {
            setDate(source.getDate());
        }
        Embargos.copyIfTargetUnset(source, this);
    }

    default void copyFromIfSourceSet(Metadata<?> source) {

        if (source.getType() != null) {
            setType(source.getType());
        }
        if (StringUtils.isNotEmpty(source.getTitle())) {
            setTitle(source.getTitle());
        }
        if (StringUtils.isNotEmpty(source.getDescription())) {
            setDescription(source.getDescription());
        }
        if (source.getHeight() != null && source.getHeight() >= 0) {
            setHeight(source.getHeight());
        }
        if (source.getWidth() != null && source.getWidth() >= 0) {
            setWidth(source.getWidth());
        }
        if (StringUtils.isNotEmpty(source.getSource())) {
            setSource(source.getSource());
        }
        if (StringUtils.isNotEmpty(source.getSourceName())) {
            setSourceName(source.getSourceName());
        }
        if (source.getLicense() != null) {
            setLicense(source.getLicense());
        }
        if (StringUtils.isNotEmpty(source.getCredits())) {
            setCredits(source.getCredits());
        }
        if (StringUtils.isNotEmpty(source.getDate())) {
            setDate(source.getDate());
        }
        Embargos.copyIfSourceSet(source, this);

    }

}
