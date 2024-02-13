package nl.vpro.domain.i18n;

import lombok.ToString;

import jakarta.persistence.Entity;

import nl.vpro.domain.media.UpdatableIdentifiable;

/**
 * @author Michiel Meeuwissen
 * @since 5.1
 */
@Entity
@ToString(callSuper = true)
public class WebsiteTranslation extends TextTranslation
    implements UpdatableIdentifiable<Long, WebsiteTranslation> {


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
