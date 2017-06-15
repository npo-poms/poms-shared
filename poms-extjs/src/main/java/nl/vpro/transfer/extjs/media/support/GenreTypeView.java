/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import nl.vpro.domain.media.Genre;

/**
 * @since 1.9
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
    "type",
    "text"
})
public class GenreTypeView {

    private String type;

    private String text;

    private GenreTypeView() {
    }

    private GenreTypeView(String type, String text) {
        this.type = type;
        this.text = text;
    }

    public static GenreTypeView create(Genre fullType) {
        return fullType == null ? null : new GenreTypeView(fullType.getTermId(), fullType.getDisplayName());
    }

    public static GenreTypeView[] create(Genre[] fullTypes) {
        List<GenreTypeView> result = new ArrayList<>(fullTypes.length);

        for(Genre fullType : fullTypes) {
            result.add(GenreTypeView.create(fullType));
        }

        return result.toArray(new GenreTypeView[result.size()]);
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
