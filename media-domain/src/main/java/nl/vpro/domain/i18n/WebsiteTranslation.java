package nl.vpro.domain.i18n;

import lombok.ToString;

import javax.persistence.Entity;

import nl.vpro.domain.media.UpdatableIdentifiable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
@ToString(callSuper = true)
public class WebsiteTranslation extends TextTranslation<MediaObjectTranslation> implements UpdatableIdentifiable<Long, WebsiteTranslation> {


    public WebsiteTranslation(String value) {
        super(value);
    }

    public WebsiteTranslation() {
    }

    @Override
    public void update(WebsiteTranslation from) {
        set(from.get());
    }
}
