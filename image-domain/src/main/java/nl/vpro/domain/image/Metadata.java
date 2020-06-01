package nl.vpro.domain.image;

import java.time.Instant;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.MutableEmbargo;
import nl.vpro.domain.Embargos;
import nl.vpro.domain.ChangeReport;
import nl.vpro.domain.support.License;
import nl.vpro.util.TimeUtils;

/**
 * @author Michiel Meeuwissen
 * @since 5.5
 */
public interface Metadata<T extends Metadata<T>>  extends MutableEmbargo<T>, BasicMetaData<T> {

    String getImageUri();

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

    default ChangeReport copyFrom(Metadata<?> image){
        ChangeReport change = new ChangeReport();
        if (!Objects.equals(getType(), image.getType())) {
            setType(image.getType());
            change.change();
        }
        if (!Objects.equals(getTitle(), image.getTitle())) {
            setTitle(image.getTitle());
            change.change();
        }
        if (!Objects.equals(getDescription(), image.getDescription())) {
            setDescription(image.getDescription());
            change.change();
        }
        if (!Objects.equals(getHeight(), image.getHeight())) {
            setHeight(image.getHeight());
            change.change();
        }
        if (!Objects.equals(getWidth(), image.getWidth())) {
            setWidth(image.getWidth());
            change.change();
        }
        if (!Objects.equals(getLicense(), image.getLicense())) {
            setLicense(image.getLicense());
            change.change();
        }
        if (!Objects.equals(getSource(), image.getSource())) {
            setSource(image.getSource());
            change.change();
        }
        if (!Objects.equals(getSourceName(), image.getSourceName())) {
            setSourceName(image.getSourceName());
            change.change();
        }
        if (!Objects.equals(getCredits(), image.getCredits())) {
            setCredits(image.getCredits());
            change.change();
        }
        if (!Objects.equals(getDate(), image.getDate())) {
            setDate(image.getDate());
            change.change();
        }

        return change.or(Embargos.copy(image, this));
    }


    default ChangeReport copyFromIfTargetUnset(Metadata<?> source) {
        ChangeReport change = new ChangeReport();
        if (getType() == null &&  ! Objects.equals(getType(), source.getType())) {
            setType(source.getType());
            change.change();
        }
        if (StringUtils.isEmpty(getTitle()) &&  ! Objects.equals(getTitle(), source.getTitle())) {
            setTitle(source.getTitle());
            change.change();
        }
        if (StringUtils.isEmpty(getDescription()) &&  ! Objects.equals(getDescription(), source.getDescription())) {
            setDescription(source.getDescription());
            change.change();
        }

        if ((getHeight() == null || getHeight() < 0) &&  ! Objects.equals(getHeight(), source.getHeight())) {
            setHeight(source.getHeight());
            change.change();
        }
        if ((getWidth() == null || getWidth() < 0)  &&  ! Objects.equals(getWidth(), source.getWidth())) {
            setWidth(source.getWidth());
            change.change();
        }
        if (StringUtils.isEmpty(getSource()) && ! Objects.equals(getSource(), source.getSource())) {
            setSource(source.getSource());
            change.change();
        }
        if (StringUtils.isEmpty(getSourceName()) &&  ! Objects.equals(getSourceName(), source.getSourceName())) {
            setSourceName(source.getSourceName());
            change.change();
        }
        if (getLicense() == null && ! Objects.equals(getLicense(), source.getLicense())) {
            setLicense(source.getLicense());
            change.change();
        }
        if (StringUtils.isEmpty(getCredits()) && ! Objects.equals(getCredits(), source.getCredits())) {
            setCredits(source.getCredits());
            change.change();
        }

        if (StringUtils.isEmpty(getDate())&& ! Objects.equals(getDate(), source.getDate())) {
            setDate(source.getDate());
            change.change();
        }
        return change.or(Embargos.copyIfTargetUnset(source, this));
    }

    default ChangeReport copyFromIfSourceSet(Metadata<?> source) {
        ChangeReport change = new ChangeReport();

        if (source.getType() != null && ! Objects.equals(getType(), source.getType())) {
            setType(source.getType());
            change.change();
        }
        if (StringUtils.isNotEmpty(source.getTitle()) &&  ! Objects.equals(getTitle(), source.getTitle())) {
            setTitle(source.getTitle());
            change.change();

        }
        if (StringUtils.isNotEmpty(source.getDescription()) &&  ! Objects.equals(getDescription(), source.getDescription())) {
            setDescription(source.getDescription());
            change.change();
        }
        if (source.getHeight() != null && source.getHeight() >= 0 &&  ! Objects.equals(getHeight(), source.getHeight())) {
            setHeight(source.getHeight());
            change.change();
        }
        if (source.getWidth() != null && source.getWidth() >= 0 && ! Objects.equals(getWidth(), source.getWidth())) {
            setWidth(source.getWidth());
            change.change();
        }
        if (StringUtils.isNotEmpty(source.getSource()) && ! Objects.equals(getSource(), source.getSource())) {
            setSource(source.getSource());
            change.change();
        }
        if (StringUtils.isNotEmpty(source.getSourceName()) && ! Objects.equals(getSourceName(), source.getSourceName())) {
            setSourceName(source.getSourceName());
            change.change();
        }
        if (source.getLicense() != null && ! Objects.equals(getLicense(), source.getLicense())) {
            setLicense(source.getLicense());
            change.change();
        }
        if (StringUtils.isNotEmpty(source.getCredits()) && ! Objects.equals(getCredits(), source.getCredits())) {
            setCredits(source.getCredits());
            change.change();
        }
        if (StringUtils.isNotEmpty(source.getDate()) && ! Objects.equals(getDate(), source.getDate())) {
            setDate(source.getDate());
            change.change();
        }
        return change.or(Embargos.copyIfSourceSet(source, this));

    }

}
