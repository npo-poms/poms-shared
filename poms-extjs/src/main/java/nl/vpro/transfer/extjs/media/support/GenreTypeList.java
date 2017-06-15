/**
 * Copyright (C) 2010 All rights reserved
 * VPRO The Netherlands
 */
package nl.vpro.transfer.extjs.media.support;

import javax.xml.bind.annotation.XmlRootElement;

import nl.vpro.domain.classification.ClassificationServiceLocator;
import nl.vpro.domain.classification.Term;
import nl.vpro.domain.media.Genre;
import nl.vpro.transfer.extjs.TransferList;

/**
 * @since 1.9
 */
@XmlRootElement(name = "genres")
public class GenreTypeList extends TransferList<GenreTypeView> {

    private GenreTypeList() {
    }

    public static GenreTypeList create() {
        GenreTypeList typeList = new GenreTypeList();

        for(Term type : ClassificationServiceLocator.getInstance().valuesOf("3.0.1")) {
            typeList.add(GenreTypeView.create(new Genre(type.getTermId())));
        }

        typeList.success = true;

        return typeList;
    }
}
