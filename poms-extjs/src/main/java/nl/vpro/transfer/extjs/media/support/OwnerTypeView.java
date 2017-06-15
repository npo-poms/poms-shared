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

import nl.vpro.domain.media.support.OwnerType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {
        "type",
        "name"
        })
public class OwnerTypeView {

    private String type;

    private String name;

    private OwnerTypeView() {
    }

    private OwnerTypeView(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static OwnerTypeView create(OwnerType fullType) {
        return fullType == null ? null : new OwnerTypeView(fullType.name(), fullType.getDisplayName());
    }

    public static OwnerTypeView[] create(OwnerType[] fullTypes) {
        List<OwnerTypeView> result = new ArrayList<OwnerTypeView>(fullTypes.length);

        for(OwnerType fullType : fullTypes) {
            result.add(OwnerTypeView.create(fullType));
        }

        return result.toArray(new OwnerTypeView[result.size()]);
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }
}
