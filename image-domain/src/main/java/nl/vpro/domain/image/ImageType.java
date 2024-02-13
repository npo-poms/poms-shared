/*
 * Copyright (C) 2010 Licensed under the Apache License, Version 2.0
 * VPRO The Netherlands
 */
package nl.vpro.domain.image;

import lombok.Getter;

import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlType;

import org.apache.commons.lang3.StringUtils;

import nl.vpro.domain.Xmlns;
import nl.vpro.i18n.Displayable;

/**
 * The POMS supported image types, as can be selected in the GUI, and are available as metadata for all images.
 * @author rico
 */
@XmlEnum
@XmlType(name = "imageTypeEnum", namespace = Xmlns.SHARED_NAMESPACE)
public enum ImageType implements Displayable {
    PICTURE("Picture") {
        @Override
        public String getDisplayName() {
            return "Afbeelding";
        }
    },
    PORTRAIT("Portrait") {
        @Override
        public String getDisplayName() {
            return "Portret";
        }
    },
    STILL("Video still"){
        @Override
        public String getDisplayName() {
            return "Still";
        }
    },
    LOGO("Product logo") {
        @Override
        public String getDisplayName() {
            return "Logo";
        }
    },
    ICON("Product icon") {
        @Override
        public String getDisplayName() {
            return "Icoon";
        }
    },
    PROMO_LANDSCAPE("Promotie landschap") {
        @Override
        public String getDisplayName() {
            return "Landschap";
        }
    },
    PROMO_PORTRAIT("Promotie portrait") {
        @Override
        public String getDisplayName() {
            return "Portrait";
        }
    },
    BACKGROUND("Background picture") {
        @Override
        public String getDisplayName() {
            return "Achtergrond";
        }
    };

    @Getter
    private final String description;

    ImageType(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return getDisplayName();
    }

    public static ImageType valueOfOrNull(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        } else {
            return valueOf(s);
        }
    }
}
